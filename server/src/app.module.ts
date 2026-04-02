import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { CommonModule } from './common';
import { AuthModule } from './modules/auth/auth.module';
import { PrismaModule } from './modules/prisma/prisma.module';
import { VocabularyModule } from './modules/vocabulary/vocabulary.module';
import { TopicModule } from './modules/topic/topic.module';
import { FavoriteModule } from './modules/favorite/favorite.module';

@Module({
  imports: [
    CommonModule,
    PrismaModule,
    AuthModule,
    VocabularyModule,
    TopicModule,
    FavoriteModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
