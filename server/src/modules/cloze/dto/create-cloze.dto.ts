export class CreateClozeOptionDto {
  optionText: string;
  isCorrect: boolean;
  blankIndex: number;
}

export class CreateClozeQuestionDto {
  topicId: string;
  sentence: string;
  options: CreateClozeOptionDto[];
}
