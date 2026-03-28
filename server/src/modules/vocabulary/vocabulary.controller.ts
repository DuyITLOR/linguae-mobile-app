import { Controller, Get, Param, ParseIntPipe, Query } from '@nestjs/common';
import { VocabularyService } from './vocabulary.service';

@Controller('vocabulary')
export class VocabularyController {
  constructor(private readonly VocabularyService: VocabularyService) {}

  @Get()
  async getAllVocabularies(@Query('q') query: string) {
    return await this.VocabularyService.getVocabularies(query);
  }

  @Get(':id')
  async getVocabularyById(@Param('id') id: string) {
    return await this.VocabularyService.getVocabularyById(id);
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
}
