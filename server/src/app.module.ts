import { Module } from '@nestjs/common';
import { ScheduleModule } from '@nestjs/schedule';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { CommonModule } from './common';
import { AuthModule } from './modules/auth/auth.module';
import { PrismaModule } from './modules/prisma/prisma.module';
import { VocabularyModule } from './modules/vocabulary/vocabulary.module';
import { TopicModule } from './modules/topic/topic.module';
import { FavoriteModule } from './modules/favorite/favorite.module';
import { DailyMissionModule } from './modules/daily-mission/daily-mission.module';
import { ClozeModule } from './modules/cloze/cloze.module';
import { UserModule } from './modules/user/user.module';
import { FlashcardModule } from './modules/flashcard/flashcard.module';
import { ChatModule } from './modules/chat/chat.module';
import { ToeicModule } from './modules/toeic/toeic.module';

@Module({
  imports: [
    ScheduleModule.forRoot(),
    CommonModule,
    PrismaModule,
    AuthModule,
    VocabularyModule,
    TopicModule,
    FavoriteModule,
    UserModule,
    DailyMissionModule,
    ClozeModule,
    FlashcardModule,
    ChatModule,
    ToeicModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
