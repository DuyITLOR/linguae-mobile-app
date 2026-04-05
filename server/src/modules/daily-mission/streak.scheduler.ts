import { Injectable, Logger } from '@nestjs/common';
import { Cron } from '@nestjs/schedule';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class StreakScheduler {
  private readonly logger = new Logger(StreakScheduler.name);

  constructor(private readonly prisma: PrismaService) {}

  @Cron('5 0 * * *', { timeZone: 'Asia/Ho_Chi_Minh' })
  async resetMissedStreaks() {
    this.logger.log('Running streak reset job...');

    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    yesterday.setHours(0, 0, 0, 0);

    const result = await this.prisma.userDailyGoal.updateMany({
      where: {
        currentStreak: { gt: 0 },
        OR: [
          { lastLearnedDate: { lt: yesterday } },
          { lastLearnedDate: null },
        ],
      },
      data: {
        currentStreak: 0,
        updatedAt: new Date(),
      },
    });

    this.logger.log(`Reset streak for ${result.count} users`);
  }
}