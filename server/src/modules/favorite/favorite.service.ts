import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class FavoriteService {
  constructor(private readonly prisma: PrismaService) {}

  async getFavoriteByUserId(userId) {
    return await this.prisma.favoriteVocabulary.findMany({
      where: {
        userId,
      },
      include: {
        Vocabulary: true,
      },
    });
  }
}
