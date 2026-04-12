import {
  BadRequestException,
  Body,
  Controller,
  Get,
  Post,
} from '@nestjs/common';
import type { ReviewCardDto, ReviewCardRequestDto } from './dto/reviewCard.dto';
import { FlashcardService } from './flashcard.service';
import { UserId } from '../../common';

@Controller('flashcard')
export class FlashcardController {
  constructor(private readonly flashcardService: FlashcardService) {}

  @Get('topics')
  getAllFlashcardTopics(@UserId() userId: string) {
    return this.flashcardService.getAllFlashcardTopics(userId);
  }

  @Post()
  review(@Body() body: ReviewCardRequestDto, @UserId() userId: string) {
    if (!body.vocabularyId || !body.status) {
      throw new BadRequestException(
        'Missing required fields: vocabularyId and status are required.',
      );
    }

    const dto = {
      ...body,
      userId,
    } as ReviewCardDto;
    return this.flashcardService.reviewFlashcard(dto);
  }
}
