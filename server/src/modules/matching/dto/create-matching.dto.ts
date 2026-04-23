import { Type } from 'class-transformer';
import {
  ArrayMinSize,
  IsArray,
  IsInt,
  IsNotEmpty,
  IsOptional,
  IsString,
  Min,
  ValidateNested,
} from 'class-validator';

export class CreateMatchingPairDto {
  @IsString()
  @IsNotEmpty()
  leftText!: string;

  @IsString()
  @IsNotEmpty()
  rightText!: string;

  @IsInt()
  @Min(0)
  @IsOptional()
  displayOrder?: number;
}

export class CreateMatchingQuestionDto {
  @IsString()
  @IsNotEmpty()
  topicId!: string;

  @IsString()
  @IsNotEmpty()
  title!: string;

  @IsArray()
  @ArrayMinSize(2)
  @ValidateNested({ each: true })
  @Type(() => CreateMatchingPairDto)
  pairs!: CreateMatchingPairDto[];
}
