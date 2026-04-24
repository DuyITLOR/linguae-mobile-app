import { Level } from '@prisma/client';
import { Type } from 'class-transformer';
import {
  ArrayMaxSize,
  ArrayMinSize,
  IsArray,
  IsEnum,
  IsInt,
  IsNotEmpty,
  IsOptional,
  IsString,
  Max,
  Min,
  ValidateNested,
} from 'class-validator';

export class CreatePart5QuestionPayloadDto {
  @IsOptional()
  @IsString()
  id?: string;

  @IsString()
  @IsNotEmpty()
  question!: string;

  @IsArray()
  @ArrayMinSize(4)
  @ArrayMaxSize(4)
  @IsString({ each: true })
  options!: string[];

  @IsInt()
  @Min(0)
  @Max(3)
  answerIndex!: number;

  @IsOptional()
  @IsInt()
  @Min(0)
  displayOrder?: number;
}

export class CreatePart6QuestionPayloadDto {
  @IsOptional()
  @IsString()
  id?: string;

  @IsInt()
  @Min(1)
  title!: number;

  @IsString()
  @IsNotEmpty()
  question!: string;

  @IsArray()
  @ArrayMinSize(4)
  @ArrayMaxSize(4)
  @IsString({ each: true })
  options!: string[];

  @IsInt()
  @Min(0)
  @Max(3)
  answerIndex!: number;

  @IsOptional()
  @IsInt()
  @Min(0)
  displayOrder?: number;
}

export class CreatePart6PassagePayloadDto {
  @IsOptional()
  @IsString()
  id?: string;

  @IsString()
  @IsNotEmpty()
  passage!: string;

  @IsArray()
  @ArrayMinSize(1)
  @ValidateNested({ each: true })
  @Type(() => CreatePart6QuestionPayloadDto)
  questions!: CreatePart6QuestionPayloadDto[];
}

export class CreateToeicRequestDto {
  @IsString()
  @IsNotEmpty()
  title!: string;

  @IsEnum(Level)
  level!: Level;

  @IsOptional()
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CreatePart5QuestionPayloadDto)
  part5Questions?: CreatePart5QuestionPayloadDto[];

  @IsOptional()
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CreatePart6PassagePayloadDto)
  part6Passages?: CreatePart6PassagePayloadDto[];
}

export interface CreateToeicDto extends CreateToeicRequestDto {
  userId: string;
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
  options: Array<{
    title: number;
    options: string[];
    answer: number;
  }>;
}
