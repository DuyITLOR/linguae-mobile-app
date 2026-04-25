import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Put,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { ToeicService } from './toeic.service';
import type {
  CreateReadingPart5QuestionDto,
  CreateReadingPart6QuestionDto,
  CreateToeicDto,
} from './dto/createToeic.dto';
import type {
  UpdateReadingPart5QuestionDto,
  UpdateReadingPart5QuestionRequestDto,
  UpdateToeicDto,
  UpdateToeicRequestDto,
} from './dto/updateToeic.dto';
import { UserId } from '../../common/decorators/user-id.decorator';
import { SubmitToeicAnswerDto } from './dto/submitToeicAnswer.dto';

@Controller('toeic')
export class ToeicController {
  constructor(private readonly toeicService: ToeicService) {}

  // ==================== Get section ====================
  @Get()
  async getAllToeics() {
    return await this.toeicService.getAllToeics();
  }

  @Get(':id')
  async getToeicById(@Param('id') id: string) {
    return await this.toeicService.getToeicById(id);
  }

  @Get('reading-part-5-questions/:id')
  async getAllReadingPart5Questions(@Param('id') id: string) {
    return await this.toeicService.getAllReadingPart5Questions(id);
  }

  @Get('reading-part-6-questions/:id')
  async getAllReadingPart6Questions(@Param('id') id: string) {
    return await this.toeicService.getAllReadingPart6Questions(id);
  }

  // ==================== Create section ======================

  @Post()
  async createToeic(
    @Body() body: CreateToeicDto,
    @UserId() userId: string,
  ) {
    return await this.toeicService.createToeic(body, userId);
  }

  @Post('reading-part-5-questions')
  async createReadingPart5Questions(
    @Body() body: CreateReadingPart5QuestionDto[],
    @UserId() userId: string,
  ) {
    if (body.length === 0) {
      throw new BadRequestException('No questions provided');
    }

    if (!Array.isArray(body)) {
      throw new BadRequestException('Questions must be an array');
    }
    return await this.toeicService.createReadingPart5Questions(body, userId);
  }

  @Post('test/submit-answer')
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async submitToeicAnswer(
    @Body() body: SubmitToeicAnswerDto,
    @UserId() userId: string,
  ) {
    return await this.toeicService.submitToeicAnswer(body, userId);
  }

  // Part 6
  @Post('reading-part-6-questions')
  async createReadingPart6Questions(
    @Body() body: CreateReadingPart6QuestionDto[],
    @UserId() userId: string,
  ) {
    return await this.toeicService.createReadingPart6Question(body, userId);
  }

  // ==================== Update section ======================
  @Put(':id')
  async updateToeic(
    @Param('id') id: string,
    @Body() body: UpdateToeicRequestDto,
    @UserId() userId: string,
  ) {
    const dto = {
      id,
      userId,
      ...body,
    } as UpdateToeicDto;
    return await this.toeicService.updateToeic(dto);
  }

  @Put('reading-part-5-questions/:id')
  async updateReadingPart5Question(
    @Param('id') id: string,
    @Body() body: UpdateReadingPart5QuestionRequestDto,
    @UserId() userId: string,
  ) {
    const dto = {
      id,
      userId,
      ...body,
    } as UpdateReadingPart5QuestionDto;
    return await this.toeicService.updateReadingPart5Question(dto);
  }

  // ==================== Delete section ======================
  @Delete(':id')
  async deleteToeic(@Param('id') id: string, @UserId() userId: string) {
    return await this.toeicService.deleteToeic(id, userId);
  }
}
