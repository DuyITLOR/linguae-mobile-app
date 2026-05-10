import {
  Body,
  Controller,
  DefaultValuePipe,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  ParseIntPipe,
  Post,
  Query,
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

  @Get('weekly-activity')
  async getWeeklyActivity(
    @UserId() userId: string,
    @Query('weekOffset', new DefaultValuePipe(0), ParseIntPipe) weekOffset: number,
  ) {
    return this.dailyMissionService.getWeeklyActivity(userId, weekOffset);
  }

  @Get('task/:taskId/words')
  async getTaskWords(
    @UserId() userId: string,
    @Param('taskId') taskId: string,
  ) {
    return this.dailyMissionService.getTaskWords(userId, taskId);
  }

  @Get('task/:taskId/cloze-questions')
  async getDailyClozeQuestions(
    @UserId() userId: string,
    @Param('taskId') taskId: string,
  ) {
    return this.dailyMissionService.getDailyClozeQuestions(userId, taskId);
  }

  @Get('task/:taskId/matching-questions')
  async getDailyMatchingQuestions(
    @UserId() userId: string,
    @Param('taskId') taskId: string,
  ) {
    return this.dailyMissionService.getDailyMatchingQuestions(userId, taskId);
  }

  @Post('task/:taskId/complete-exercise')
  @HttpCode(HttpStatus.OK)
  async completeDailyExercise(
    @UserId() userId: string,
    @Param('taskId') taskId: string,
  ) {
    return this.dailyMissionService.completeDailyExercise(userId, taskId);
  }
}
