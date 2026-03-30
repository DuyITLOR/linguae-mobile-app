import { Module } from '@nestjs/common';
import { FavoriteController } from './favorite.controller';
import { PrismaModule } from '../prisma/prisma.module';
import { FavoriteService } from './favorite.service';

@Module({
  imports: [PrismaModule],
  controllers: [FavoriteController],
  providers: [FavoriteService],
})
export class FavoriteModule {}
