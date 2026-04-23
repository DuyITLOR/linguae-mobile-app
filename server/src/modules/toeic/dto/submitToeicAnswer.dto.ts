import { Type } from 'class-transformer';
import {
  ArrayNotEmpty,
  IsArray,
  IsIn,
  IsInt,
  IsString,
  Min,
  ValidateNested,
} from 'class-validator';

export class AnswerDto {
  @IsString()
  questionId!: string;

  @IsInt()
  @Min(0)
  selected!: number;

  @IsInt()
  @IsIn([5, 6])
  part!: number;
}

export class SubmitToeicAnswerDto {
  @IsString()
  toeicId!: string;

  @IsInt()
  @Min(0)
  correctAnswer!: number;

  @IsArray()
  @ArrayNotEmpty()
  @ValidateNested({ each: true })
  @Type(() => AnswerDto)
  answer!: AnswerDto[];
}
