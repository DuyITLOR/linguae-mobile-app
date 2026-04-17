import { Module } from '@nestjs/common';
import { VocabularyController } from './vocabulary.controller';
import { PrismaModule } from '../prisma/prisma.module';
import { VocabularyService } from './vocabulary.service';
import { AdminGuard } from '../../common';

@Module({
  imports: [PrismaModule],
  controllers: [VocabularyController],
  providers: [VocabularyService, AdminGuard],
})
export class VocabularyModule {}
