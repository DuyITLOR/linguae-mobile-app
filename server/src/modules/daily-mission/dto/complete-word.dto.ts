import { IsString, IsNotEmpty } from 'class-validator';

export class CompleteWordDto {
  @IsString()
  @IsNotEmpty()
  vocabularyId: string;
}