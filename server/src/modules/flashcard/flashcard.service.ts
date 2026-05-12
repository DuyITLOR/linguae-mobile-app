import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { ReviewCardDto } from './dto/reviewCard.dto';

export interface FlashcardTopicResponse {
  id: string;
  title: string;
  level: string;
  learnedWords: number;
  totalWords: number;
  masteredVocabIds: string[];
}

export interface TopicWithMasteredStatusDto {
  id: string;
  learnedWords: number;
  masteredVocabIds: string[];
}

@Injectable()
export class FlashcardService {
  constructor(private readonly prisma: PrismaService) {}

  async getAllFlashcardTopics(userId: string) {
    try {
      const topics = await this.prisma.topic.findMany({
        include: {
          Vocabulary: true,
        },
      });

      const topicsWithMasteedStatus = await this.prisma.topic.findMany({
        include: {
          Vocabulary: {
            select: {
              id: true,
              UserVocabularyProgress: {
                where: {
                  userId: userId,
                },
                select: {
                  vocabularyId: true,
                },
              },
            },
          },
        },
      });

      const newTopicsWithMasteedStatus = [] as TopicWithMasteredStatusDto[];
      for (const topicWithStatus of topicsWithMasteedStatus) {
        let learnedWords = 0;
        const masteredVocabIds: string[] = [];
        for (const vocab of topicWithStatus.Vocabulary) {
          if (vocab.UserVocabularyProgress.length > 0) {
            learnedWords++;
            masteredVocabIds.push(vocab.id);
          }
        }
        newTopicsWithMasteedStatus.push({
          id: topicWithStatus.id,
          learnedWords,
          masteredVocabIds,
        });
      }

      const res = [] as FlashcardTopicResponse[];
      for (const topic of topics) {
        for (const topicWithStatus of newTopicsWithMasteedStatus) {
          if (topic.id === topicWithStatus.id) {
            const totalWords = topic.Vocabulary.length;
            res.push({
              id: topic.id,
              title: topic.title,
              level: topic.level,
              learnedWords: topicWithStatus.learnedWords,
              totalWords,
              masteredVocabIds: topicWithStatus.masteredVocabIds,
            });
          }
        }
      }

      return res;
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Unknown error';
      throw new Error(`Failed to get flashcard topics: ${msg}`);
    }
  }

  async reviewFlashcard(dto: ReviewCardDto) {
    try {
      const check = await this.prisma.userVocabularyProgress.findUnique({
        where: {
          userId_vocabularyId: {
            userId: dto.userId,
            vocabularyId: dto.vocabularyId,
          },
        },
      });
      if (check) {
        await this.prisma.userVocabularyProgress.update({
          where: {
            userId_vocabularyId: {
              userId: dto.userId,
              vocabularyId: dto.vocabularyId,
            },
          },
          data: {
            status: dto.status,
            exposureCount: check.exposureCount + 1,
            lastReviewedAt: new Date(),
            nextReviewAt: new Date(Date.now() + 24 * 60 * 60 * 1000),
            updatedAt: new Date(),
          },
        });
      } else {
        await this.prisma.userVocabularyProgress.create({
          data: {
            userId: dto.userId,
            vocabularyId: dto.vocabularyId,
            status: dto.status,
            exposureCount: 1,
            lastReviewedAt: new Date(),
            nextReviewAt: new Date(Date.now() + 24 * 60 * 60 * 1000),
            updatedAt: new Date(),
          },
        });
      }
    } catch (err) {
      const msg = err instanceof Error ? err.message : 'Unknown error';
      throw new Error(`Failed to review flashcard: ${msg}`);
    }
  }
}
