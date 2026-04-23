import { IsArray, IsNotEmpty, IsString } from 'class-validator';

export class CreateTopicPracticeConfigDto {
  @IsString()
  @IsNotEmpty()
  topicId: string;

  @IsArray()
  @IsString({ each: true })
  questionType: string[];
}
