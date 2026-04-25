import { ToeicLevel } from '@prisma/client';

export interface CreateToeicDto {
  title: string;
  level: ToeicLevel;
  part5: Part5[];
  part6: Part6[];
}

export interface Part5 {
  question: string;
  options: string[];
  answer: number;
}

export interface Part6 {
  question: string;
  options: Part6Option[];
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
