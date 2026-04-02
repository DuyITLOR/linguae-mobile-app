import { Controller, Delete, Get, Param, Post } from '@nestjs/common';
import { FavoriteService } from './favorite.service';
import { UserId } from '../../common';

@Controller('favorite')
export class FavoriteController {
  constructor(private readonly favoriteService: FavoriteService) {}

  @Get()
  async getFavoriteByUserId(@UserId() userId: string) {
    return await this.favoriteService.getFavoriteByUserId(userId);
  }

  @Post('vocabulary/:id')
  async createFavorite(@Param('id') id: string, @UserId() userId: string) {
    return await this.favoriteService.createFavorite(userId, id);
  }

  @Delete('vocabulary/:id')
  async removeFavorite(@Param('id') id: string, @UserId() userId: string) {
    return await this.favoriteService.removeFavorite(userId, id);
  }
}
