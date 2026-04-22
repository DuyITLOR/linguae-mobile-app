import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  ParseIntPipe,
  Post,
  Put,
  Query,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { VocabularyService } from './vocabulary.service';
import { AdminGuard, UserId } from '../../common';
import { CreateVocabularyDto } from './dto/create-vocabulary.dto';
import { UpdateVocabularyDto } from './dto/update-vocabulary.dto';

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
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async create(@Body() vocabulary: CreateVocabularyDto) {
    return await this.VocabularyService.create(vocabulary);
  }

  @Delete(':id')
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async delete(@Param('id') id: string) {
    return await this.VocabularyService.delete(id);
  }

  @Put(':id')
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async update(@Param('id') id: string, @Body() dto: UpdateVocabularyDto) {
    return await this.VocabularyService.update(id, dto);
  }
}
