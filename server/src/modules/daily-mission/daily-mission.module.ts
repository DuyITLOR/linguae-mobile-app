import { Module } from '@nestjs/common';
import { DailyMissionService } from './daily-mission.service';
import { DailyMissionController } from './daily-mission.controller';
import { PrismaService } from '../prisma/prisma.service';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  controllers: [DailyMissionController],
  providers: [DailyMissionService, PrismaService],
})
export class DailyMissionModule {}
