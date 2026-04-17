import { Body, Controller, Delete, Get, Param, Post, Put } from '@nestjs/common';
import { ToeicService } from './toeic.service';
import type {
  CreateToeicDto,
  CreateToeicRequestDto,
} from './dto/createToeic.dto';
import type {
  UpdateToeicDto,
  UpdateToeicRequestDto,
} from './dto/updateToeic.dto';
import { UserId } from '../../common/decorators/user-id.decorator';

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

  // ==================== Create section ======================

  @Post()
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

  // ==================== Delete section ======================
  @Delete(':id')
  async deleteToeic(@Param('id') id: string, @UserId() userId: string) {
    return await this.toeicService.deleteToeic(id, userId);
  }
}
