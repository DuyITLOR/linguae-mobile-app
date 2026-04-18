import { PartOfSpeech } from '@prisma/client';
import {
  IsArray,
  IsEnum,
  IsInt,
  IsOptional,
  IsString,
  Max,
  Min,
  ValidateNested,
} from 'class-validator';
import { Type } from 'class-transformer';

export class UpdateVocabularyExampleDto {
  @IsString()
  @IsOptional()
  sentence?: string;

  @IsString()
  @IsOptional()
  translation?: string;
}

export class UpdateVocabularyDto {
  @IsString()
  @IsOptional()
  topicId?: string;

  @IsString()
  @IsOptional()
  word?: string;

  @IsString()
  @IsOptional()
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
  @Type(() => UpdateVocabularyExampleDto)
  @IsOptional()
  examples?: UpdateVocabularyExampleDto[];
}
