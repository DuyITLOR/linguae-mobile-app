import { Topic } from '@prisma/client';

export interface TopicResponseDto {
  success: boolean;
  data?: Topic;
  message: string;
}
