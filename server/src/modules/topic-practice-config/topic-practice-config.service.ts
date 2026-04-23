import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateTopicPracticeConfigDto } from './dto/create-topic-practice-config.dto';

@Injectable()
export class TopicPracticeConfigService {
  constructor(private readonly prisma: PrismaService) {}

  async getAllTopicPracticeConfig(query?: string) {
    return this.prisma.topicPracticeConfig.findMany({
      where: query
        ? {
            Topic: {
              title: {
                contains: query,
                mode: 'insensitive',
              },
            },
          }
        : {},
      include: {
        Topic: true,
      },
    });
  }

  async getTopicPracticeConfigById(id: string) {
    const config = await this.prisma.topicPracticeConfig.findUnique({
      where: { id },
      include: {
        Topic: true,
      },
    });

    if (!config) {
      throw new NotFoundException('Topic practice config not found');
    }

    return config;
  }

  async getTopicPracticeConfigByTopicId(topicId: string) {
    return this.prisma.topicPracticeConfig.findMany({
      where: { topicId },
      include: {
        Topic: true,
      },
    });
  }

  async createTopicPracticeConfig(dto: CreateTopicPracticeConfigDto) {
    return this.prisma.topicPracticeConfig.create({
      data: {
        topicId: dto.topicId,
        questionType: dto.questionType,
      },
    });
  }

  async delete(id: string) {
    const config = await this.prisma.topicPracticeConfig.findUnique({
      where: { id },
    });

    if (!config) {
      throw new NotFoundException('Topic practice config not found');
    }

    return await this.prisma.topicPracticeConfig.delete({
      where: { id },
    });
  }
}
