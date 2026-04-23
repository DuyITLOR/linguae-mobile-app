import { Module } from '@nestjs/common';
import { TopicPracticeConfigService } from './topic-practice-config.service';
import { TopicPracticeConfigController } from './topic-practice-config.controller';
import { PrismaModule } from '../prisma/prisma.module';
import { AdminGuard } from '../../common';

@Module({
  imports: [PrismaModule],
  providers: [TopicPracticeConfigService, AdminGuard],
  controllers: [TopicPracticeConfigController],
  exports: [TopicPracticeConfigService],
})
export class TopicPracticeConfigModule {}
