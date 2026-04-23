import { Module } from '@nestjs/common';
import { TopicController } from './topic.controller';
import { TopicService } from './topic.service';
import { PrismaModule } from '../prisma/prisma.module';
import { AdminGuard } from '../../common';
import { TopicPracticeConfigModule } from '../topic-practice-config/topic-practice-config.module';

@Module({
  imports: [PrismaModule, TopicPracticeConfigModule],
  controllers: [TopicController],
  providers: [TopicService, AdminGuard],
})
export class TopicModule {}
