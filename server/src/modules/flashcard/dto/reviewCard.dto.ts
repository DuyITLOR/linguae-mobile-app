import { UserVocabularyStatus } from '@prisma/client';

export interface ReviewCardDto {
  vocabularyId: string;
  userId: string;
  status: UserVocabularyStatus;
}

export interface ReviewCardRequestDto {
  vocabularyId: string;
  status: UserVocabularyStatus;
}
