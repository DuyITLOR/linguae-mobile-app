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

export class UpdatePart5QuestionPayloadDto {
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

export class UpdatePart6QuestionPayloadDto {
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

export class UpdatePart6PassagePayloadDto {
  @IsOptional()
  @IsString()
  id?: string;

  @IsString()
  @IsNotEmpty()
  passage!: string;

  @IsArray()
  @ArrayMinSize(1)
  @ValidateNested({ each: true })
  @Type(() => UpdatePart6QuestionPayloadDto)
  questions!: UpdatePart6QuestionPayloadDto[];
}

export class UpdateToeicRequestDto {
  @IsString()
  @IsNotEmpty()
  title!: string;

  @IsEnum(Level)
  level!: Level;

  @IsOptional()
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => UpdatePart5QuestionPayloadDto)
  part5Questions?: UpdatePart5QuestionPayloadDto[];

  @IsOptional()
  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => UpdatePart6PassagePayloadDto)
  part6Passages?: UpdatePart6PassagePayloadDto[];
}

export interface UpdateToeicDto extends UpdateToeicRequestDto {
  userId: string;
  id: string;
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
