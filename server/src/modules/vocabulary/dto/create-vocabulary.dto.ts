import { PartOfSpeech } from '@prisma/client';
import {
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
import { Type } from 'class-transformer';

export class CreateVocabularyExampleDto {
  @IsString()
  @IsNotEmpty()
  sentence!: string;

  @IsString()
  @IsOptional()
  translation?: string;
}

export class CreateVocabularyDto {
  @IsString()
  @IsNotEmpty()
  topicId!: string;

  @IsString()
  @IsNotEmpty()
  word!: string;

  @IsString()
  @IsNotEmpty()
  meaning!: string;

  @IsString()
  @IsOptional()
  pronunciationText?: string;

  @IsEnum(PartOfSpeech)
  @IsOptional()
  partOfSpeech?: PartOfSpeech;

  @IsInt()
  @Min(1)
  @Max(3)
  @IsOptional()
  difficulty?: number;

  @IsArray()
  @ValidateNested({ each: true })
  @Type(() => CreateVocabularyExampleDto)
  @IsOptional()
  examples?: CreateVocabularyExampleDto[];
}
