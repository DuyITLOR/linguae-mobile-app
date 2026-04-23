import {
  Body,
  Patch,
  Controller,
  Param,
  Post,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common';
import { AdminGuard } from '../../common';
import { CreateMatchingQuestionDto } from './dto/create-matching.dto';
import { UpdateMatchingQuestionDto } from './dto/update-matching.dto';
import { MatchingService } from './matching.service';

@Controller('matching')
export class MatchingController {
  constructor(private readonly matchingService: MatchingService) {}

  @Post()
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async createMatchingQuestion(@Body() dto: CreateMatchingQuestionDto) {
    return this.matchingService.createMatchingQuestion(dto);
  }

  @Patch(':id')
  @UseGuards(AdminGuard)
  @UsePipes(new ValidationPipe({ whitelist: true, transform: true }))
  async updateMatchingQuestion(
    @Param('id') id: string,
    @Body() dto: UpdateMatchingQuestionDto,
  ) {
    return this.matchingService.updateMatchingQuestion(id, dto);
  }
}
