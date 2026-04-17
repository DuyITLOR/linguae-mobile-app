import {
  Body,
  Controller,
  Get,
  Param,
  Post,
  Query,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { TopicService } from './topic.service';
import { AdminGuard } from '../../common';
import { CreateTopicDto } from './dto/create-topic.dto';

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

  @Post()
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async create(@Body() dto: CreateTopicDto) {
    return await this.TopicService.createTopic(dto);
  }
}
