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
      where "word" ILIKE ${'%' + cleanWord + '%'} or 
            "meaning" ILIKE ${'%' + cleanWord + '%'}
    `;
  }

  async getVocabularyById(id: string, userId: string) {
    const vocabulary = await this.prisma.vocabulary.findUnique({
      where: { id: id },
      include: {
        VocabularyExample: true,
      },
    });

    if (!vocabulary) {
      throw new NotFoundException('Vocabulary not found');
    }

    await this.prisma.userVocabularyProgress.upsert({
      where: {
        userId_vocabularyId: { userId, vocabularyId: id },
      },
      create: {
        userId,
        vocabularyId: id,
        exposureCount: 1,
        lastReviewedAt: new Date(),
        updatedAt: new Date(),
        nextReviewAt: new Date(),
      },
      update: {
        exposureCount: { increment: 1 },
        lastReviewedAt: new Date(),
        updatedAt: new Date(),
      },
    });

    return vocabulary;
  }

  async getVocabularyByTopic(topicId: string, keyword?: string) {
    const cleanWord = keyword?.trim();

    if (!cleanWord)
      return await this.prisma.vocabulary.findMany({
        where: { topicId },
        orderBy: { createdAt: 'desc' },
      });

    return this.prisma.$queryRaw`
      select * from "Vocabulary"
      where ("topicId" = ${topicId}) And (
        "meaning" ilike ${'%' + cleanWord + '%'} or 
        "word" ilike ${'%' + cleanWord + '%'}
      ) 
    `;
  }

  async getVocabularyByDifficulty(level: number) {
    return await this.prisma.vocabulary.findMany({
      where: { difficulty: level },
      orderBy: { createdAt: 'desc' },
    });
  }
}
