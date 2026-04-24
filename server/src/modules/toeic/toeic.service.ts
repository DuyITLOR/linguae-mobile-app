import {
  BadRequestException,
  ForbiddenException,
  HttpException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import {
  CreateReadingPart5QuestionDto,
  CreateReadingPart6QuestionDto,
  CreateToeicDto,
} from './dto/createToeic.dto';
import {
  UpdateReadingPart5QuestionDto,
  UpdateToeicDto,
} from './dto/updateToeic.dto';
import { SubmitToeicAnswerDto } from './dto/submitToeicAnswer.dto';

@Injectable()
export class ToeicService {
  constructor(private readonly prisma: PrismaService) {}

  // ==================== Get Section ====================
  async getAllToeics() {
    try {
      const toeics = await this.prisma.toeic.findMany();
      return toeics;
    } catch (error) {
      const msg =
        error instanceof NotFoundException
          ? 'Toeics not found'
          : 'Failed to get toeics';
      throw new InternalServerErrorException(msg);
    }
  }

  async getToeicById(id: string) {
    try {
      const toeic = await this.prisma.toeic.findUnique({
        where: { id },
      });

      if (!toeic) {
        throw new NotFoundException('Toeic not found');
      }

      return toeic;
    } catch (error) {
      if (error instanceof NotFoundException) {
        throw error;
      }
      throw new InternalServerErrorException('Failed to get toeic');
    }
  }

  async getAllReadingPart5Questions(toeicId: string) {
    try {
      const readingPart5Questions =
        await this.prisma.readingPart5Question.findMany({
          where: { toeicId },
        });
      if (readingPart5Questions.length === 0) {
        throw new NotFoundException('Reading part 5 questions not found');
      }
      return readingPart5Questions;
    } catch (error) {
      const msg =
        error instanceof NotFoundException
          ? 'Reading part 5 questions not found'
          : 'Failed to get reading part 5 questions';
      throw new InternalServerErrorException(msg);
    }
  }

  async getAllReadingPart6Questions(toeicId: string) {
    try {
      const readingPart6Questions =
        await this.prisma.readingPart6Question.findMany({
          where: { toeicId },
          include: {
            readingPart6Options: true,
          },
        });
      if (readingPart6Questions.length === 0) {
        throw new NotFoundException('Reading part 6 questions not found');
      }
      return readingPart6Questions;
    } catch (error) {
      const msg =
        error instanceof NotFoundException
          ? 'Reading part 6 questions not found'
          : 'Failed to get reading part 6 questions';
      throw new InternalServerErrorException(msg);
    }
  }

  // ==================== Create section ======================

  async createToeic(dto: CreateToeicDto) {
    await this.checkPermission(dto.userId);

    const toeic = await this.prisma.toeic.create({
      data: {
        title: dto.title,
        level: dto.level,
      },
      select: {
        id: true,
        title: true,
        level: true,
      },
    });

    return toeic;
  }

  // Part 5
  async createReadingPart5Questions(
    dto: CreateReadingPart5QuestionDto[],
    userId: string,
  ) {
    await this.checkPermission(userId);
    try {
      const readingPart5Questions =
        await this.prisma.readingPart5Question.createMany({
          data: dto.map((item) => ({
            toeicId: item.toeicId,
            question: item.question,
            options: item.options,
            answer: item.answer,
          })),
        });
      return readingPart5Questions;
    } catch (error) {
      const msg =
        error instanceof NotFoundException ||
        error instanceof ForbiddenException;
      throw new InternalServerErrorException(msg);
    }
  }

  // Part 6
  async createReadingPart6Question(
    dto: CreateReadingPart6QuestionDto[],
    userId: string,
  ) {
    await this.checkPermission(userId);
    try {
      await this.prisma.$transaction(
        dto.map((dto) =>
          this.prisma.readingPart6Question.create({
            data: {
              toeicId: dto.toeicId,
              question: dto.question,
              readingPart6Options: {
                create: dto.options.map((opt) => ({
                  title: opt.title,
                  option: opt.options,
                  answer: opt.answer,
                })),
              },
            },
          }),
        ),
      );
    } catch (err) {
      const msg =
        err instanceof NotFoundException || err instanceof ForbiddenException
          ? 'Permission denied or related toeic not found'
          : 'Error at creating part 6 question service';
      throw new InternalServerErrorException(
        msg || 'Error at creating part 6 question service',
      );
    }
  }

  // Submit answer
  async submitToeicAnswer(dto: SubmitToeicAnswerDto, userId: string) {
    try {
      const [user, toeic] = await Promise.all([
        this.prisma.user.findUnique({
          where: { id: userId },
          select: { id: true },
        }),
        this.prisma.toeic.findUnique({
          where: { id: dto.toeicId },
          include: {
            readingPart5Questions: true,
            readingPart6Questions: {
              include: {
                readingPart6Options: true,
              },
            },
          },
        }),
      ]);

      if (!user) {
        throw new NotFoundException('User not found');
      }

      if (!toeic) {
        throw new NotFoundException('Toeic not found');
      }

      const part5AnswerKey = new Map<
        string,
        { correctAnswer: number; optionCount: number }
      >(
        toeic.readingPart5Questions.map((question) => [
          question.id,
          {
            correctAnswer: question.answer,
            optionCount: question.options.length,
          },
        ] as const),
      );
      const part6AnswerKey = new Map<
        string,
        { correctAnswer: number; optionCount: number }
      >();

      for (const question of toeic.readingPart6Questions) {
        for (const option of question.readingPart6Options) {
          part6AnswerKey.set(option.id, {
            correctAnswer: option.answer,
            optionCount: option.option.length,
          });
        }
      }

      const totalQuestions = part5AnswerKey.size + part6AnswerKey.size;
      if (totalQuestions === 0) {
        throw new BadRequestException('Toeic test has no questions');
      }

      if (dto.answer.length !== totalQuestions) {
        throw new BadRequestException(
          'Must answer all questions before submitting',
        );
      }

      const submittedQuestionIds = new Set<string>();
      let correctAnswer = 0;

      for (const submittedAnswer of dto.answer) {
        const duplicateKey = `${submittedAnswer.part}:${submittedAnswer.questionId}`;
        if (submittedQuestionIds.has(duplicateKey)) {
          throw new BadRequestException('Duplicate answers are not allowed');
        }
        submittedQuestionIds.add(duplicateKey);

        const answerKey =
          submittedAnswer.part === 5
            ? part5AnswerKey.get(submittedAnswer.questionId)
            : part6AnswerKey.get(submittedAnswer.questionId);

        if (!answerKey) {
          throw new BadRequestException(
            'Submitted answer does not belong to this toeic test',
          );
        }

        if (submittedAnswer.selected >= answerKey.optionCount) {
          throw new BadRequestException('Selected answer is out of range');
        }

        if (submittedAnswer.selected === answerKey.correctAnswer) {
          correctAnswer += 1;
        }
      }

      if (dto.correctAnswer !== correctAnswer) {
        throw new BadRequestException(
          'Correct answer count does not match the answer key',
        );
      }

      await this.prisma.$transaction(async (tx) => {
        // Create a new toeic session
        const toeicSession = await tx.toeicSession.create({
          data: {
            userId,
            toeicId: dto.toeicId,
            correctAnswers: correctAnswer,
          },
        });

        await tx.toeicAnswer.createMany({
          data: dto.answer.map((a) => ({
            toeicSessionId: toeicSession.id,
            questionId: a.questionId,
            part: a.part,
            selected: a.selected,
          })),
        });
      });

      return { message: 'Toeic answers submitted successfully' };
    } catch (err) {
      if (err instanceof HttpException) {
        throw err;
      }
      throw new InternalServerErrorException(
        'Error at submitting toeic answer service',
      );
    }
  }

  // ==================== Update section ======================
  async updateToeic(dto: UpdateToeicDto) {
    try {
      await this.checkPermission(dto.userId);
      const existingToeic = await this.prisma.toeic.findUnique({
        where: { id: dto.id },
      });

      if (!existingToeic) {
        throw new NotFoundException('Toeic not found');
      }

      const toeic = await this.prisma.toeic.update({
        where: { id: dto.id },
        data: {
          title: dto.title,
          level: dto.level,
        },
      });

      return toeic;
    } catch (error) {
      if (error instanceof NotFoundException) {
        throw error;
      }
      throw new InternalServerErrorException('Failed to update toeic');
    }
  }

  async updateReadingPart5Question(dto: UpdateReadingPart5QuestionDto) {
    try {
      await this.checkPermission(dto.userId);
      const existingReadingPart5Question =
        await this.prisma.readingPart5Question.findUnique({
          where: { id: dto.id },
        });
      if (!existingReadingPart5Question) {
        throw new NotFoundException('Reading part 5 question not found');
      }

      const readingPart5Question =
        await this.prisma.readingPart5Question.update({
          where: { id: dto.id },
          data: {
            question: dto.question,
            options: dto.options,
            answer: dto.answer,
          },
        });
      return readingPart5Question;
    } catch (error) {
      if (error instanceof NotFoundException) {
        throw error;
      }
      const msg =
        error instanceof NotFoundException ||
        error instanceof ForbiddenException;
      throw new InternalServerErrorException(msg);
    }
  }
  // ==================== Delete section ======================
  async deleteToeic(id: string, userId: string) {
    try {
      await this.checkPermission(userId);
      const existingToeic = await this.prisma.toeic.findUnique({
        where: { id },
      });

      if (!existingToeic) {
        throw new NotFoundException('Toeic not found');
      }

      await this.prisma.toeic.delete({
        where: { id },
      });

      return { message: 'Toeic deleted successfully' };
    } catch (error) {
      if (error instanceof NotFoundException) {
        throw error;
      }
      throw new InternalServerErrorException('Failed to delete toeic');
    }
  }

  // ==================== Helper functions ======================
  async checkPermission(userId: string) {
    try {
      const user = await this.prisma.user.findUnique({
        where: { id: userId },
      });
      if (!user) {
        throw new NotFoundException('User not found');
      }
      if (user.role !== 'ADMIN') {
        throw new ForbiddenException('User is not an admin');
      }
    } catch (error) {
      const msg =
        error instanceof NotFoundException ||
        error instanceof ForbiddenException;
      throw new InternalServerErrorException(msg);
    }
  }
}
