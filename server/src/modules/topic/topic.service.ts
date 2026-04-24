import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateTopicDto } from './dto/create-topic.dto';
import { UpdateTopicDto } from './dto/update-topic.dto';

@Injectable()
export class TopicService {
  constructor(private readonly prisma: PrismaService) {}

  async getAllTopic(keyword?: string) {
    const cleanWord = keyword?.trim();
    if (!cleanWord) {
      const topics = await this.prisma.topic.findMany({
        include: {
          _count: {
            select: {
              Vocabulary: true,
            },
          },
        },
      });
      const levelOrder: Record<string, number> = {
        BEGINNER: 0,
        INTERMEDIATE: 1,
        ADVANCED: 2,
      };
      return topics.sort(
        (a, b) => (levelOrder[a.level] ?? 3) - (levelOrder[b.level] ?? 3),
      );
    }

    return this.prisma.$queryRaw`
      select t.*, json_build_object(
        'Vocabulary', COUNT(v.id)
      ) as "_count"
      from "Topic" t
      left join "Vocabulary" v on v."topicId" = t."id"
      where t."title" ilike ${'%' + keyword + '%'} or
            t."description" ilike ${'%' + keyword + '%'}
      group by t.id
      order by case t."level"
        when 'BEGINNER' then 0
        when 'INTERMEDIATE' then 1
        when 'ADVANCED' then 2
        else 3
      end
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
    try {
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
    } catch (error: any) {
      if (error.code === 'P2002') {
        throw new Error('Title already exists');
      }
      throw error;
    }
  }

  async delete(topicId: string) {
    const topic = await this.prisma.topic.findUnique({
      where: {
        id: topicId,
      },
    });

    if (!topic) {
      throw new NotFoundException('Topic not found');
    }

    return await this.prisma.topic.delete({
      where: {
        id: topicId,
      },
    });
  }

  async update(topicId: string, dto: UpdateTopicDto) {
    try {
      const topic = await this.prisma.topic.findUnique({
        where: {
          id: topicId,
        },
      });

      if (!topic) {
        throw new NotFoundException('Topic not found');
      }

      return await this.prisma.topic.update({
        where: {
          id: topicId,
        },
        data: {
          title: dto.title,
          description: dto.description,
          icon: dto.icon,
          level: dto.level,
          updatedAt: new Date(),
        },
      });
    } catch (error: any) {
      if (error.code === 'P2002') {
        throw new Error('Title already exists');
      }
      throw error;
    }
  }
}
