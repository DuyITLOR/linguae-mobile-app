import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class VocabularyService {
  constructor(private readonly prisma: PrismaService) {}

  async getAllVocabulary() {
    return await this.prisma.vocabulary.findMany();
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
