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
