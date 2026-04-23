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

export class UpdateMatchingPairDto {
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

export class UpdateMatchingQuestionDto {
  @IsString()
  @IsNotEmpty()
  @IsOptional()
  topicId?: string;

  @IsString()
  @IsNotEmpty()
  @IsOptional()
  title?: string;

  @IsArray()
  @ArrayMinSize(2)
  @ValidateNested({ each: true })
  @Type(() => UpdateMatchingPairDto)
  @IsOptional()
  pairs?: UpdateMatchingPairDto[];
}
