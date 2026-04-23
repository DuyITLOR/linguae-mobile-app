/* eslint-disable @typescript-eslint/no-misused-promises */
/* eslint-disable @typescript-eslint/no-unsafe-return */
import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { DailyTaskType, MissionStatus, TaskStatus } from '@prisma/client';

@Injectable()
export class DailyMissionService {
  constructor(private prismaService: PrismaService) {}

  private todayOnly(date: Date): Date {
    return new Date(
      Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate()),
    );
  }

  private formatMission(mission: any) {
    return {
      id: mission.id,
      date: mission.date,
      status: mission.status,
      tasks: mission.DailyTask.map((task: any) => ({
        id: task.id,
        taskType: task.taskType,
        targetCount: task.targetCount,
        completedCount: task.completedCount,
        status: task.status,
        completedVocabularyIds: task.DailyTaskCompletion.map(
          (c: any) => c.vocabularyId,
        ),
      })),
    };
  }

  private async randomVocabularyIds(): Promise<string[]> {
    const result = await this.prismaService.$queryRaw<{ id: string }[]>`
		select id from "Vocabulary"
		order by RANDOM()
		limit 5
	`;

    return result.map((r) => r.id);
  }

  private async updateStreak(userId: string) {
    const today = this.todayOnly(new Date());
    const yesterday = new Date(today);
    yesterday.setUTCDate(yesterday.getUTCDate() - 1);

    const goal = await this.prismaService.userDailyGoal.upsert({
      where: { userId: userId },
      create: {
        userId,
        currentStreak: 1,
        bestStreak: 1,
        lastLearnedDate: today,
        updatedAt: new Date(),
      },
      update: {},
    });

    const lastDate = goal.lastLearnedDate
      ? this.todayOnly(goal.lastLearnedDate)
      : null;

    if (lastDate?.getTime() === today.getTime()) return goal;

    let newStreak: number;

    if (lastDate?.getTime() === yesterday.getTime()) {
      newStreak = goal.currentStreak + 1;
    } else {
      newStreak = 1;
    }

    const newBest = goal.bestStreak < newStreak ? newStreak : goal.bestStreak;

    return this.prismaService.userDailyGoal.update({
      where: { userId },
      data: {
        currentStreak: newStreak,
        bestStreak: newBest,
        lastLearnedDate: today,
        updatedAt: new Date(),
      },
    });
  }

  async getTodayMission(userId: string) {
    const today = this.todayOnly(new Date());

    const existing = await this.prismaService.dailyMission.findUnique({
      where: {
        userId_date: {
          userId,
          date: today,
        },
      },
      include: {
        DailyTask: {
          include: { DailyTaskCompletion: true },
        },
      },
    });

    if (existing) return this.formatMission(existing);

    const [vocabularyIds, fashcardIds] = await Promise.all([
      this.randomVocabularyIds(),
      this.randomVocabularyIds(),
    ]);

    const mission = await this.prismaService.dailyMission.create({
      data: {
        userId,
        date: today,
        updatedAt: new Date(),
        DailyTask: {
          create: [
            {
              taskType: DailyTaskType.VOCABULARY_LEARN,
              targetCount: 5,
              vocabularyIds: vocabularyIds,
              updatedAt: new Date(),
            },
            {
              taskType: DailyTaskType.FLASHCARD_LEARN,
              targetCount: 5,
              vocabularyIds: fashcardIds,
              updatedAt: new Date(),
            },
          ],
        },
      },
      include: {
        DailyTask: {
          include: {
            DailyTaskCompletion: true,
          },
        },
      },
    });

    return this.formatMission(mission);
  }

  private async checkAndCompleteMission(tx: any, dailyMissionId: string) {
    const allTasks = await tx.dailyTask.findMany({ where: { dailyMissionId } });
    const allDone = allTasks.every(
      (t: any) => t.status === TaskStatus.COMPLETED,
    );

    if (allDone) {
      const mission = await tx.dailyMission.update({
        where: { id: dailyMissionId },
        data: { status: MissionStatus.COMPLETED, updatedAt: new Date() },
      });

      setImmediate(() => this.updateStreak(mission.userId));
    }
  }

  async getTodaySummary(userId: string) {
    const today = this.todayOnly(new Date());

    const [mission, goal] = await Promise.all([
      this.prismaService.dailyMission.findUnique({
        where: { userId_date: { userId, date: today } },
        include: { DailyTask: true },
      }),
      this.prismaService.userDailyGoal.findFirst({ where: { userId } }),
    ]);

    const streak = {
      currentStreak: goal?.currentStreak ?? 0,
      bestStreak: goal?.bestStreak ?? 0,
      lastLearnedDate: goal?.lastLearnedDate ?? null,
    };

    if (!mission) {
      return {
        hasMission: false,
        totalTasks: 0,
        completedTasks: 0,
        overallProgress: 0,
        streak,
      };
    }

    const completedTasks = mission.DailyTask.filter(
      (t) => t.status === TaskStatus.COMPLETED,
    ).length;

    const totalProgress = mission.DailyTask.reduce(
      (sum, t) => sum + t.completedCount / t.targetCount,
      0,
    );

    return {
      hasMission: true,
      missionStatus: mission.status,
      totalTasks: mission.DailyTask.length,
      completedTasks,
      overallProgress: Math.round(
        (totalProgress / mission.DailyTask.length) * 100,
      ),
      tasks: mission.DailyTask.map((t) => ({
        id: t.id,
        taskType: t.taskType,
        completedCount: t.completedCount,
        targetCount: t.targetCount,
        status: t.status,
      })),
      streak,
    };
  }

  async completeWord(userId: string, taskId: string, vocabularyId: string) {
    console.log(1);
    const task = await this.prismaService.dailyTask.findFirst({
      where: {
        id: taskId,
        DailyMission: { userId },
      },
    });

    if (!task) throw new NotFoundException('Task not found');

    if (task.status === TaskStatus.COMPLETED) {
      throw new BadRequestException('Task already completed');
    }

    const ids = task.vocabularyIds as string[];
    if (!ids.includes(vocabularyId)) {
      throw new BadRequestException('Vocabulary not in the task');
    }

    console.log(2); 

    const alreadyCounted =
      await this.prismaService.dailyTaskCompletion.findUnique({
        where: {
          dailyTaskId_vocabularyId: {
            dailyTaskId: taskId,
            vocabularyId,
          },
        },
      });

    if (alreadyCounted) {
      return { alreadyCounted: true, task };
    }

    console.log(3); 
    const updatedTask = await this.prismaService.$transaction(async (tx) => {
      await tx.dailyTaskCompletion.create({
        data: {
          dailyTaskId: taskId,
          vocabularyId,
        },
      });

      const newCount = task.completedCount + 1;
      const isDone = newCount >= task.targetCount;

      const updated = await tx.dailyTask.update({
        where: { id: taskId },
        data: {
          completedCount: newCount,
          status: isDone ? TaskStatus.COMPLETED : TaskStatus.IN_PROGRESS,
          updatedAt: new Date(),
        },
      });

      if (isDone) {
        await this.checkAndCompleteMission(tx, task.dailyMissionId);
      }

      return updated;
    });

    return {
      alreadyCounted: false,
      task: updatedTask,
    };
  }

  async getStatistics(userId: string) {
    const today = this.todayOnly(new Date());
    const sixDaysAgo = new Date(today);
    sixDaysAgo.setUTCDate(sixDaysAgo.getUTCDate() - 6);

    const [goal, totalVocabularyLearned, todayMission, weeklyMissions, historyMissions] =
      await Promise.all([
        this.prismaService.userDailyGoal.findFirst({ where: { userId } }),
        this.prismaService.userVocabularyProgress.count({ where: { userId } }),
        this.prismaService.dailyMission.findUnique({
          where: { userId_date: { userId, date: today } },
          include: { DailyTask: true },
        }),
        this.prismaService.dailyMission.findMany({
          where: { userId, date: { gte: sixDaysAgo, lte: today } },
          include: { DailyTask: true },
          orderBy: { date: 'asc' },
        }),
        this.prismaService.dailyMission.findMany({
          where: { userId },
          include: { DailyTask: true },
          orderBy: { date: 'desc' },
          take: 10,
        }),
      ]);

    const streak = {
      currentStreak: goal?.currentStreak ?? 0,
      bestStreak: goal?.bestStreak ?? 0,
    };

    let todayProgress: {
      overallProgress: number;
      completedTasks: number;
      totalTasks: number;
      tasks: { id: string; taskType: string; completedCount: number; targetCount: number; status: string }[];
    } = { overallProgress: 0, completedTasks: 0, totalTasks: 0, tasks: [] };

    if (todayMission) {
      const completedTasks = todayMission.DailyTask.filter(
        (t) => t.status === TaskStatus.COMPLETED,
      ).length;
      const totalProgress = todayMission.DailyTask.reduce(
        (sum, t) => sum + t.completedCount / t.targetCount,
        0,
      );
      todayProgress = {
        overallProgress:
          todayMission.DailyTask.length > 0
            ? Math.round((totalProgress / todayMission.DailyTask.length) * 100)
            : 0,
        completedTasks,
        totalTasks: todayMission.DailyTask.length,
        tasks: todayMission.DailyTask.map((t) => ({
          id: t.id,
          taskType: t.taskType,
          completedCount: t.completedCount,
          targetCount: t.targetCount,
          status: t.status,
        })),
      };
    }

    const missionMap = new Map(
      weeklyMissions.map((m) => [m.date.toISOString().split('T')[0], m]),
    );

    const weeklyActivity = Array.from({ length: 7 }, (_, i) => {
      const d = new Date(sixDaysAgo);
      d.setUTCDate(d.getUTCDate() + i);
      const key = d.toISOString().split('T')[0];
      const m = missionMap.get(key);
      return {
        date: key,
        completedTasks: m
          ? m.DailyTask.filter((t) => t.status === TaskStatus.COMPLETED).length
          : 0,
        totalTasks: m ? m.DailyTask.length : 0,
      };
    });

    const missionHistory = historyMissions.map((m) => ({
      date: m.date.toISOString().split('T')[0],
      status: m.status,
      completedTasks: m.DailyTask.filter((t) => t.status === TaskStatus.COMPLETED).length,
      totalTasks: m.DailyTask.length,
    }));

    return { streak, totalVocabularyLearned, todayProgress, weeklyActivity, missionHistory };
  }

  async getWeeklyActivity(userId: string, weekOffset: number) {
    const today = this.todayOnly(new Date());
    const endDate = new Date(today);
    endDate.setUTCDate(today.getUTCDate() + weekOffset * 7);
    const startDate = new Date(endDate);
    startDate.setUTCDate(endDate.getUTCDate() - 6);

    const weeklyMissions = await this.prismaService.dailyMission.findMany({
      where: { userId, date: { gte: startDate, lte: endDate } },
      include: { DailyTask: true },
      orderBy: { date: 'asc' },
    });

    const missionMap = new Map(
      weeklyMissions.map((m) => [m.date.toISOString().split('T')[0], m]),
    );

    return Array.from({ length: 7 }, (_, i) => {
      const d = new Date(startDate);
      d.setUTCDate(d.getUTCDate() + i);
      const key = d.toISOString().split('T')[0];
      const m = missionMap.get(key);
      return {
        date: key,
        completedTasks: m
          ? m.DailyTask.filter((t) => t.status === TaskStatus.COMPLETED).length
          : 0,
        totalTasks: m ? m.DailyTask.length : 0,
      };
    });
  }

  async getTaskWords(userId: string, taskId: string) {
    const task = await this.prismaService.dailyTask.findFirst({
      where: {
        id: taskId,
        DailyMission: { userId },
      },
      include: { DailyTaskCompletion: true },
    });

    if (!task) throw new NotFoundException('Task not found');

    const ids = task.vocabularyIds as string[];
    const completedIds = task.DailyTaskCompletion.map((c) => c.vocabularyId);

    const vocabularies = await this.prismaService.vocabulary.findMany({
      where: { id: { in: ids } },
      include: { VocabularyExample: true },
    });

    const ordered = ids.map((id) => {
      const vocab = vocabularies.find((v) => v.id === id);
      return {
        ...vocab,
        isCompleted: completedIds.includes(id),
      };
    });

    return {
      taskId: task.id,
      taskType: task.taskType,
      targetCount: task.targetCount,
      completedCount: task.completedCount,
      status: task.status,
      words: ordered,
    };
  }
}
