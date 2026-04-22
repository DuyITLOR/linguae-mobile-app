import {
  BadRequestException,
  ForbiddenException,
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

const TOTAL_QUESTIONS = 28 + 16;

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
      if (dto.answers.length !== TOTAL_QUESTIONS) {
        throw new BadRequestException(
          'Must answer all questions before submitting',
        );
      }

      await this.prisma.$transaction(async (tx) => {
        // Create a new toeic session
        const toeicSession = await tx.toeicSession.create({
          data: {
            userId,
            toeicId: dto.toeicId,
            correctAnswers: dto.correctAnswers,
          },
        });

        await tx.toeicAnswer.createMany({
          data: dto.answers.map((a) => ({
            toeicSessionId: toeicSession.id,
            questionId: a.questionId,
            part: a.part,
            selected: a.selected,
          })),
        });
      });

      return { message: 'Toeic answers submitted successfully' };
    } catch (err) {
      const msg =
        err instanceof NotFoundException
          ? 'Toeic or questions not found'
          : 'Error at submitting toeic answer service';
      throw new InternalServerErrorException(msg);
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
