import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class FavoriteService {
  constructor(private readonly prisma: PrismaService) {}

  async getFavoriteByUserId(userId: string) {
    return await this.prisma.favoriteVocabulary.findMany({
      where: {
        userId,
      },
      include: {
        Vocabulary: true,
      },
    });
  }

  async createFavorite(userId: string, vocabId: string) {
    console.log(userId);
    return await this.prisma.favoriteVocabulary.upsert({
      where: {
        userId_vocabularyId: {
          userId: userId,
          vocabularyId: vocabId,
        },
      },
      update: {},
      create: {
        userId: userId,
        vocabularyId: vocabId,
      },
    });
  }

  async removeFavorite(userId: string, vocabId: string) {
    return await this.prisma.favoriteVocabulary.delete({
      where: {
        userId_vocabularyId: {
          userId: userId,
          vocabularyId: vocabId,
        },
      },
    });
  }
}
