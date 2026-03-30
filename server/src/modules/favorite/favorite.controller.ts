import { Controller, Get, Param } from '@nestjs/common';
import { FavoriteService } from './favorite.service';

@Controller('favorite')
export class FavoriteController {
  constructor(private readonly favoriteService: FavoriteService) {}

  @Get('user/:id')
  async getFavoriteByUserId(@Param('id') id: string) {
    return await this.favoriteService.getFavoriteByUserId(id);
  }
}
