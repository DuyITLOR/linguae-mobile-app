import { Module } from '@nestjs/common';
import { AdminGuard } from '../../common';
import { PrismaModule } from '../prisma/prisma.module';
import { MatchingController } from './matching.controller';
import { MatchingService } from './matching.service';

@Module({
  imports: [PrismaModule],
  controllers: [MatchingController],
  providers: [MatchingService, AdminGuard],
})
export class MatchingModule {}
