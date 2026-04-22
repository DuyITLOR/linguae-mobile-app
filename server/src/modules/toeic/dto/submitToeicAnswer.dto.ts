import { IsArray, IsNumber, IsString } from 'class-validator';

export class AnswerDto {
  @IsString()
  questionId!: string;

  @IsNumber()
  selected!: number;

  @IsNumber()
  part!: number;
}

export class SubmitToeicAnswerDto {
  @IsString()
  toeicId!: string;

  @IsArray()
  answers!: AnswerDto[];

  @IsNumber()
  correctAnswers!: number;
}
