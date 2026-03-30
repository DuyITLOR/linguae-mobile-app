import { Controller, Get } from '@nestjs/common';
import { FavoriteService } from './favorite.service';
import { UserId } from '../../common';

@Controller('favorite')
export class FavoriteController {
  constructor(private readonly favoriteService: FavoriteService) {}

  @Get()
  async getFavoriteByUserId(@UserId() userId: string) {
    return await this.favoriteService.getFavoriteByUserId(userId);
  }
}
