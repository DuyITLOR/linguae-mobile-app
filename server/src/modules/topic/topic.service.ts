import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateTopicDto } from './dto/create-topic.dto';

@Injectable()
export class TopicService {
  constructor(private readonly prisma: PrismaService) {}

  async getAllTopic(keyword?: string) {
    const cleanWord = keyword?.trim();
    if (!cleanWord)
      return await this.prisma.topic.findMany({
        include: {
          _count: {
            select: {
              Vocabulary: true,
            },
          },
        },
        
      });

    return this.prisma.$queryRaw`
      select t.*, json_build_object(
        'Vocabulary', COUNT(v.id) 
      ) as "_count"
      from "Topic" t
      left join "Vocabulary" v on v."topicId" = t."id"
      where t."title" ilike ${'%' + keyword + '%'} or 
            t."description" ilike ${'%' + keyword + '%'}
      group by t.id
    `;
  }

  async getTopicById(topicId: string) {
    try {
      const topic = await this.prisma.topic.findUnique({
        where: {
          id: topicId,
        },
        include: {
          Vocabulary: true,
        },
      });
      return topic;
    } catch (error) {
      const msg = error instanceof Error ? error.message : 'Unknown error';
      throw new Error(`Failed to get topic: ${msg}`);
    }
  }

  async createTopic(dto: CreateTopicDto) {
    return await this.prisma.topic.create({
      data: {
        title: dto.title,
        description: dto.description,
        icon: dto.icon,
        level: dto.level,
        displayOrder: dto.displayOrder ?? 0,
        updatedAt: new Date(),
      },
    });
  }
}
