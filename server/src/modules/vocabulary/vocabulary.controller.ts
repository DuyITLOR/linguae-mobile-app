import { Body, Controller, Get, Param, ParseIntPipe, Post, Query, UsePipes, ValidationPipe } from '@nestjs/common';
import { VocabularyService } from './vocabulary.service';
import { UserId } from '../../common';
import { CreateVocabularyDto } from './dto/create-vocabulary.dto';

@Controller('vocabulary')
export class VocabularyController {
  constructor(private readonly VocabularyService: VocabularyService) {}

  @Get()
  async getAllVocabularies(@Query('q') query: string) {
    return await this.VocabularyService.getVocabularies(query);
  }

  @Get(':id')
  async getVocabularyById(@Param('id') id: string, @UserId() userId: string) {
    return await this.VocabularyService.getVocabularyById(id, userId);
  }

  @Get('topic/:id')
  async getVocabulariesByTopicId(
    @Param('id') id: string,
    @Query('q') query: string,
  ) {
    return await this.VocabularyService.getVocabularyByTopic(id, query);
  }

  @Get('difficulty/:level')
  async getVocabulariesByDifficulty(
    @Param('level', ParseIntPipe) level: number,
  ) {
    return await this.VocabularyService.getVocabularyByDifficulty(level);
  }

  @Post()
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async create(@Body() vocabulary: CreateVocabularyDto) {
    return await this.VocabularyService.create(vocabulary);
  }
}
