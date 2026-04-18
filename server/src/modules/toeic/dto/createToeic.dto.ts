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

export interface Part6Option {
  title: number;
  options: string[];
  answer: number;
}

export interface CreateReadingPart5QuestionDto {
  toeicId: string;
  question: string;
  options: string[];
  answer: number;
}

export interface CreateReadingPart6QuestionDto {
  toeicId: string;
  question: string;
  options: Part6Option[];
}
