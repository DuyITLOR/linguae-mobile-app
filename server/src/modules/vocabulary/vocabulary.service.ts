import { Injectable, NotFoundException } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateVocabularyDto } from './dto/create-vocabulary.dto';
import { UpdateVocabularyDto } from './dto/update-vocabulary.dto';

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

    async create(dto: CreateVocabularyDto) {
      return await this.prisma.vocabulary.create({
        data: {
          topicId: dto.topicId,
          word: dto.word,
          meaning: dto.meaning,
          pronunciationText: dto.pronunciationText,
          partOfSpeech: dto.partOfSpeech,
          difficulty: dto.difficulty ?? 1,
          updatedAt: new Date(),
          VocabularyExample: dto.examples?.length
            ? {
                create: dto.examples.map((e) => ({
                  sentence: e.sentence,
                  translation: e.translation,
                })),
              }
            : undefined,
        },
        include: {
          VocabularyExample: true,
        },
      });
    }

  async delete(id: string) {
    const vocabulary = await this.prisma.vocabulary.findUnique({
      where: { id },
    });

    if (!vocabulary) {
      throw new NotFoundException('Vocabulary is not found');
    }

    await this.prisma.vocabularyExample.deleteMany({
      where: { vocabularyId: id },
    });

    await this.prisma.userVocabularyProgress.deleteMany({
      where: { vocabularyId: id },
    });

    return await this.prisma.vocabulary.delete({
      where: { id },
    });
  }

  async update(id: string, dto: UpdateVocabularyDto) {
    const vocabulary = await this.prisma.vocabulary.findUnique({
      where: { id },
    });

    if (!vocabulary) {
      throw new NotFoundException('Vocabulary is not found');
    }

    const validExamples = dto.examples?.filter(
      (e): e is { sentence: string; translation?: string } => !!e.sentence,
    );

    return await this.prisma.vocabulary.update({
      where: { id },
      data: {
        topicId: dto.topicId,
        word: dto.word,
        meaning: dto.meaning,
        pronunciationText: dto.pronunciationText,
        partOfSpeech: dto.partOfSpeech,
        difficulty: dto.difficulty ?? 1,
        updatedAt: new Date(),
        VocabularyExample: validExamples
          ? {
              deleteMany: {},
              createMany: {
                data: validExamples.map((e) => ({
                  sentence: e.sentence,
                  translation: e.translation,
                })),
              },
            }
          : undefined,
      },
      include: {
        VocabularyExample: true,
      },
    });
  }
}
