import { ToeicLevel } from '@prisma/client';

export interface CreateToeicRequestDto {
  title: string;
  level: ToeicLevel;
}

export interface CreateToeicDto {
  userId: string;
  title: string;
  level: ToeicLevel;
}

export interface CreateReadingPart5QuestionDto {
  toeicId: string;
  question: string;
  options: string[];
  answer: number;
}
