import { Module } from '@nestjs/common';
import { TopicController } from './topic.controller';
import { TopicService } from './topic.service';
import { PrismaModule } from '../prisma/prisma.module';
import { AdminGuard } from '../../common';

@Module({
  imports: [PrismaModule],
  controllers: [TopicController],
  providers: [TopicService, AdminGuard],
})
export class TopicModule {}
