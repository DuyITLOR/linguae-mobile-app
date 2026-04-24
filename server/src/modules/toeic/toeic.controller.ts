import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Put,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { ToeicService } from './toeic.service';
import type {
  CreateReadingPart5QuestionDto,
  CreateReadingPart6QuestionDto,
  CreateToeicDto,
  CreateToeicRequestDto,
} from './dto/createToeic.dto';
import type {
  UpdateReadingPart5QuestionDto,
  UpdateReadingPart5QuestionRequestDto,
  UpdateToeicDto,
  UpdateToeicRequestDto,
} from './dto/updateToeic.dto';
import { UserId } from '../../common/decorators/user-id.decorator';
import { SubmitToeicAnswerDto } from './dto/submitToeicAnswer.dto';
import { AdminGuard } from '../../common/guards/admin.guard';

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
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async createToeic(
    @Body() body: CreateToeicRequestDto,
    @UserId() userId: string,
  ) {
    const dto = {
      userId,
      ...body,
    } as CreateToeicDto;
    return await this.toeicService.createToeic(dto);
  }

  @Post('reading-part-5-questions')
  @UseGuards(AdminGuard)
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
  @UseGuards(AdminGuard)
  async createReadingPart6Questions(
    @Body() body: CreateReadingPart6QuestionDto[],
    @UserId() userId: string,
  ) {
    return await this.toeicService.createReadingPart6Question(body, userId);
  }

  // ==================== Update section ======================
  @Put(':id')
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
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
  @UseGuards(AdminGuard)
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
  @UseGuards(AdminGuard)
  async deleteToeic(@Param('id') id: string, @UserId() userId: string) {
    return await this.toeicService.deleteToeic(id, userId);
  }

  @Delete(':toeicId/part6-passages/:passageId')
  @UseGuards(AdminGuard)
  async deletePart6Passage(
    @Param('toeicId') toeicId: string,
    @Param('passageId') passageId: string,
    @UserId() userId: string,
  ) {
    return await this.toeicService.deletePart6Passage(
      toeicId,
      passageId,
      userId,
    );
  }

  @Delete(':toeicId/part6-passages/:passageId/questions/:questionId')
  @UseGuards(AdminGuard)
  async deletePart6Question(
    @Param('toeicId') toeicId: string,
    @Param('passageId') passageId: string,
    @Param('questionId') questionId: string,
    @UserId() userId: string,
  ) {
    return await this.toeicService.deletePart6Question(
      toeicId,
      passageId,
      questionId,
      userId,
    );
  }
}
