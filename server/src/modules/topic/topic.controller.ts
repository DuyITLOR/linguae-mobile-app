import { Controller, Get, Query } from '@nestjs/common';
import { TopicService } from './topic.service';

@Controller('topic')
export class TopicController {
  constructor(private readonly TopicService: TopicService) {}

  @Get()
  async getAllTopic(@Query('q') query: string) {
    return await this.TopicService.getAllTopic(query);
  }
}
