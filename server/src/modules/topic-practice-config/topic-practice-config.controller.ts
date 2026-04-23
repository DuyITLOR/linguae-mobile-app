import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Query,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { TopicPracticeConfigService } from './topic-practice-config.service';
import { AdminGuard } from '../../common';
import { CreateTopicPracticeConfigDto } from './dto/create-topic-practice-config.dto';

@Controller('topic-practice-config')
export class TopicPracticeConfigController {
  constructor(private readonly topicPracticeConfigService: TopicPracticeConfigService) {}

  @Get()
  async getAllTopicPracticeConfig(@Query('q') query: string) {
    return await this.topicPracticeConfigService.getAllTopicPracticeConfig(query);
  }

  @Get(':id')
  async getTopicPracticeConfigById(@Param('id') id: string) {
    return await this.topicPracticeConfigService.getTopicPracticeConfigById(id);
  }

  @Post()
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async create(@Body() dto: CreateTopicPracticeConfigDto) {
    return await this.topicPracticeConfigService.createTopicPracticeConfig(dto);
  }

  @Delete(':id')
  @UseGuards(AdminGuard)
  async delete(@Param('id') id: string) {
    return await this.topicPracticeConfigService.delete(id);
  }
}
