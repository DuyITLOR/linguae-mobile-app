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
      await tx.dailyMission.update({
        where: { id: dailyMissionId },
        data: { status: MissionStatus.COMPLETED, updatedAt: new Date() },
      });
    }
  }

  async getTodaySummary(userId: string) {
    const today = this.todayOnly(new Date());

    const mission = await this.prismaService.dailyMission.findUnique({
      where: { userId_date: { userId, date: today } },
      include: { DailyTask: true },
    });

    if (!mission) {
      return {
        hasMission: false,
        totalTasks: 0,
        completedTasks: 0,
        overallProgress: 0,
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
    };
  }

  async completeWord(userId: string, taskId: string, vocabularyId: string) {
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
