import {
  Body,
  Controller,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  Post,
} from '@nestjs/common';
import { DailyMissionService } from './daily-mission.service';
import { UserId } from '../../common';
import { CompleteWordDto } from './dto/complete-word.dto';

@Controller('daily-mission')
export class DailyMissionController {
  constructor(private readonly dailyMissionService: DailyMissionService) {}

  @Get('today')
  async getTodayMission(@UserId() userId: string) {
    return this.dailyMissionService.getTodayMission(userId);
  }

  @Get('today/summary')
  async getTodaySummary(@UserId() userId: string) {
    return this.dailyMissionService.getTodaySummary(userId);
  }

  @Post('task/:taskId/complete')
  @HttpCode(HttpStatus.OK)
  async completedWord(
    @UserId() userId: string,
    @Param('taskId') taskId: string,
    @Body() dto: CompleteWordDto,
  ) {
    return this.dailyMissionService.completeWord(
      userId,
      taskId,
      dto.vocabularyId,
    );
  }

  @Get('statistics')
  async getStatistics(@UserId() userId: string) {
    return this.dailyMissionService.getStatistics(userId);
  }

  @Get('task/:taskId/words')
  async getTaskWords(
    @UserId() userId: string,
    @Param('taskId') taskId: string,
  ) {
    return this.dailyMissionService.getTaskWords(userId, taskId);
  }
}
