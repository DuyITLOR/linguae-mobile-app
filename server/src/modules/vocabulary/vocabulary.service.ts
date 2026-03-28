import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class VocabularyService {
  constructor(private readonly prisma: PrismaService) {}

  async getVocabularies(keyword?: string) {
    const cleanWord = keyword?.trim();

    if (!cleanWord) {
      return this.prisma.vocabulary.findMany();
    }

    return this.prisma.$queryRaw`
      select * from "Vocabulary"
      where word ILIKE ${'%' + cleanWord + '%'} or 
            meaning ILIKE ${'%' + cleanWord + '%'}
    `;
  }

  async getVocabularyById(id: string) {
    const vocabulary = await this.prisma.vocabulary.findUnique({
      where: { id: id },
      include: {
        VocabularyExample: true,
      },
    });

    if (!vocabulary) {
      throw new NotFoundException('Vocabulary not found');
    }

    return vocabulary;
  }

  async getVocabularyByTopic(topicId: string) {
    return await this.prisma.vocabulary.findMany({
      where: { topicId },
      orderBy: { createdAt: 'desc' },
    });
  }

  async getVocabularyByDifficulty(level: number) {
    return await this.prisma.vocabulary.findMany({
      where: { difficulty: level },
      orderBy: { createdAt: 'desc' },
    });
  }
}
