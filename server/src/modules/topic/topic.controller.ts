import { Controller, Get } from '@nestjs/common';
import { TopicService } from './topic.service';

@Controller('topic')
export class TopicController {
  constructor(private readonly TopicService: TopicService) {}

  @Get()
  async getAllTopic() {
    return await this.TopicService.getAllTopic();
  }
}
