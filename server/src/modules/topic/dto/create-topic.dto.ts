import { Level } from '@prisma/client';
import { IsEnum, IsInt, IsNotEmpty, IsOptional, IsString, Min } from 'class-validator';

export class CreateTopicDto {
  @IsString()
  @IsNotEmpty()
  title!: string;

  @IsString()
  @IsOptional()
  icon?: string;

  @IsString()
  @IsOptional()
  description?: string;

  @IsEnum(Level)
  @IsOptional()
  level?: Level;

  @IsInt()
  @Min(0)
  @IsOptional()
  displayOrder?: number;
}
