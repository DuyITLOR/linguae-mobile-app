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
