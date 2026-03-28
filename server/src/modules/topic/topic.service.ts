import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

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
}
