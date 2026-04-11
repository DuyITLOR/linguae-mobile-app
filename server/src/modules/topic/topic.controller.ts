import { Controller, Get, Param, Query } from '@nestjs/common';
import { TopicService } from './topic.service';

@Controller('topic')
export class TopicController {
  constructor(private readonly TopicService: TopicService) {}

  @Get()
  async getAllTopic(@Query('q') query: string) {
    return await this.TopicService.getAllTopic(query);
  }

  @Get(':topicId')
  async getTopicById(@Param('topicId') topicId: string) {
    return await this.TopicService.getTopicById(topicId);
  }
}
