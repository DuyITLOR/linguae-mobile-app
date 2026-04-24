import { ToeicLevel } from '@prisma/client';

export interface UpdateToeicRequestDto {
  title: string;
  description: string;
  level: ToeicLevel;
}

export interface UpdateToeicDto {
  userId: string;
  id: string;
  title: string;
  description: string;
  level: ToeicLevel;
}

export interface UpdateReadingPart5QuestionRequestDto {
  question: string;
  options: string[];
  answer: number;
}

export interface UpdateReadingPart5QuestionDto {
  id: string;
  userId: string;
  question: string;
  options: string[];
  answer: number;
}
