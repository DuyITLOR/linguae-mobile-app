import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateMatchingQuestionDto } from './dto/create-matching.dto';
import { UpdateMatchingQuestionDto } from './dto/update-matching.dto';

@Injectable()
export class MatchingService {
  constructor(private readonly prisma: PrismaService) {}

  private async ensureTopicExists(topicId: string) {
    const topic = await this.prisma.topic.findUnique({
      where: { id: topicId },
      select: { id: true },
    });

    if (!topic) {
      throw new NotFoundException('Topic not found');
    }
  }

  private async ensureMatchingEnabledForTopic(topicId: string) {
    const config = await this.prisma.topicPracticeConfig.findFirst({
      where: { topicId },
    });

    if (config) {
      if (!config.questionType.includes('MATCHING')) {
        await this.prisma.topicPracticeConfig.update({
          where: { id: config.id },
          data: {
            questionType: {
              push: 'MATCHING',
            },
          },
        });
      }

      return;
    }

    await this.prisma.topicPracticeConfig.create({
      data: {
        topicId,
        questionType: ['MATCHING'],
      },
    });
  }

  private buildMatchingPairs(
    pairs: Array<{
      leftText: string;
      rightText: string;
      displayOrder?: number;
    }>,
  ) {
    return pairs.map((pair, index) => ({
      leftText: pair.leftText.trim(),
      rightText: pair.rightText.trim(),
      displayOrder: pair.displayOrder ?? index,
    }));
  }

  async getMatchingQuestions(topicId?: string) {
    return this.prisma.matchingQuestion.findMany({
      where: topicId ? { topicId } : undefined,
      orderBy: { createdAt: 'desc' },
      include: {
        MatchingPair: {
          orderBy: { displayOrder: 'asc' },
        },
      },
    });
  }

  async createMatchingQuestion(dto: CreateMatchingQuestionDto) {
    await this.ensureTopicExists(dto.topicId);
    await this.ensureMatchingEnabledForTopic(dto.topicId);

    return this.prisma.matchingQuestion.create({
      data: {
        topicId: dto.topicId,
        title: dto.title.trim(),
        MatchingPair: {
          create: this.buildMatchingPairs(dto.pairs),
        },
      },
      include: {
        MatchingPair: {
          orderBy: { displayOrder: 'asc' },
        },
      },
    });
  }

  async updateMatchingQuestion(id: string, dto: UpdateMatchingQuestionDto) {
    const matchingQuestion = await this.prisma.matchingQuestion.findUnique({
      where: { id },
      select: { id: true },
    });

    if (!matchingQuestion) {
      throw new NotFoundException('Matching question not found');
    }

    if (dto.topicId) {
      await this.ensureTopicExists(dto.topicId);
      await this.ensureMatchingEnabledForTopic(dto.topicId);
    }

    return this.prisma.matchingQuestion.update({
      where: { id },
      data: {
        topicId: dto.topicId,
        title: dto.title?.trim(),
        MatchingPair: dto.pairs
          ? {
              deleteMany: {},
              create: this.buildMatchingPairs(dto.pairs),
            }
          : undefined,
      },
      include: {
        MatchingPair: {
          orderBy: { displayOrder: 'asc' },
        },
      },
    });
  }

  async deleteMatchingQuestion(id: string) {
    const matchingQuestion = await this.prisma.matchingQuestion.findUnique({
      where: { id },
      select: { id: true },
    });

    if (!matchingQuestion) {
      throw new NotFoundException('Matching question not found');
    }

    return this.prisma.matchingQuestion.delete({
      where: { id },
      include: {
        MatchingPair: {
          orderBy: { displayOrder: 'asc' },
        },
      },
    });
  }
}
