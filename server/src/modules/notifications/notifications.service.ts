import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Prisma } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { CreateNotificationLogDto } from './dto/create-notification-log.dto';
import { RegisterDeviceTokenDto } from './dto/register-device-token.dto';
import { UpdateNotificationSettingsDto } from './dto/update-notification-settings.dto';

const DEFAULT_REMINDER_TIME = '20:00';
const DEFAULT_TIMEZONE = 'Asia/Ho_Chi_Minh';
const DEFAULT_REMINDER_TITLE = 'Đến giờ luyện tập rồi!';
const DEFAULT_REMINDER_MESSAGE = 'Vào Linguae làm nhiệm vụ hằng ngày nhé.';
const DEFAULT_DAYS_OF_WEEK = [1, 2, 3, 4, 5, 6, 7];

interface NotificationSettingsPayload {
  notificationsEnabled: boolean;
  pushNotifications: boolean;
  emailNotifications: boolean;
  reminderTime: string;
  timezone: string;
  dailyReminder: {
    id: string | null;
    enabled: boolean;
    title: string;
    message: string | null;
    daysOfWeek: number[];
  };
}

@Injectable()
export class NotificationsService {
  constructor(private readonly prisma: PrismaService) {}

  async getSettings(userId: string): Promise<NotificationSettingsPayload> {
    const [setting, reminder] = await Promise.all([
      this.prisma.userSetting.findFirst({
        where: { userId },
        orderBy: { createdAt: 'desc' },
      }),
      this.prisma.reminder.findFirst({
        where: { userId },
        orderBy: { createdAt: 'desc' },
      }),
    ]);

    return this.toSettingsPayload(setting, reminder);
  }

  async updateSettings(
    userId: string,
    body: UpdateNotificationSettingsDto,
  ): Promise<NotificationSettingsPayload> {
    const existingSetting = await this.prisma.userSetting.findFirst({
      where: { userId },
      orderBy: { createdAt: 'desc' },
    });

    const existingReminder = await this.prisma.reminder.findFirst({
      where: { userId },
      orderBy: { createdAt: 'desc' },
    });

    const current = this.toSettingsPayload(existingSetting, existingReminder);
    const reminderTime =
      body.reminderTime === null || body.reminderTime === undefined
        ? current.reminderTime
        : this.validateTimeOfDay(body.reminderTime);
    const timezone =
      body.timezone === null || body.timezone === undefined
        ? current.timezone
        : this.validateTimezone(body.timezone);
    const notificationsEnabled =
      body.notificationsEnabled ?? current.notificationsEnabled;
    const pushNotifications =
      body.pushNotifications ?? current.pushNotifications;
    const emailNotifications =
      body.emailNotifications ?? current.emailNotifications;
    const dailyReminderEnabled =
      body.dailyReminderEnabled ??
      (notificationsEnabled &&
        pushNotifications &&
        current.dailyReminder.enabled);
    const title =
      body.reminderTitle === undefined
        ? current.dailyReminder.title
        : this.validateTitle(body.reminderTitle);
    const message =
      body.reminderMessage === undefined
        ? current.dailyReminder.message
        : this.normalizeNullableText(body.reminderMessage, 240);
    const daysOfWeek =
      body.daysOfWeek === undefined
        ? current.dailyReminder.daysOfWeek
        : this.validateDaysOfWeek(body.daysOfWeek);

    if (existingSetting) {
      await this.prisma.userSetting.update({
        where: { id: existingSetting.id },
        data: {
          notificationsEnabled,
          pushNotifications,
          emailNotifications,
          reminderTime,
          timezone,
          updatedAt: new Date(),
        },
      });
    } else {
      await this.prisma.userSetting.create({
        data: {
          userId,
          notificationsEnabled,
          pushNotifications,
          emailNotifications,
          reminderTime,
          timezone,
          updatedAt: new Date(),
        },
      });
    }

    if (existingReminder) {
      await this.prisma.reminder.update({
        where: { id: existingReminder.id },
        data: {
          title,
          message,
          timeOfDay: reminderTime,
          daysOfWeek,
          timezone,
          channel: 'PUSH',
          isEnabled: dailyReminderEnabled,
          updatedAt: new Date(),
        },
      });
    } else {
      await this.prisma.reminder.create({
        data: {
          userId,
          title,
          message,
          timeOfDay: reminderTime,
          daysOfWeek,
          timezone,
          channel: 'PUSH',
          isEnabled: dailyReminderEnabled,
          updatedAt: new Date(),
        },
      });
    }

    return this.getSettings(userId);
  }

  async registerDeviceToken(userId: string, body: RegisterDeviceTokenDto) {
    const token = body.token?.trim();
    if (!token) {
      throw new BadRequestException('Device token không được để trống');
    }

    const platform = this.validatePlatform(body.platform);
    const deviceName = this.normalizeNullableText(body.deviceName, 120);
    const now = new Date();

    return this.prisma.userDeviceToken.upsert({
      where: { token },
      update: {
        userId,
        platform,
        deviceName,
        isActive: true,
        lastSeenAt: now,
        updatedAt: now,
      },
      create: {
        userId,
        token,
        platform,
        deviceName,
        isActive: true,
        lastSeenAt: now,
        updatedAt: now,
      },
      select: {
        id: true,
        platform: true,
        deviceName: true,
        isActive: true,
        lastSeenAt: true,
      },
    });
  }

  async deactivateDeviceToken(userId: string, token: string) {
    const normalizedToken = token.trim();
    if (!normalizedToken) {
      throw new BadRequestException('Device token không được để trống');
    }

    const result = await this.prisma.userDeviceToken.updateMany({
      where: { userId, token: normalizedToken },
      data: { isActive: false, updatedAt: new Date() },
    });

    if (result.count === 0) {
      throw new NotFoundException('Không tìm thấy device token');
    }

    return { deactivated: true };
  }

  async createLog(userId: string, body: CreateNotificationLogDto) {
    const title = this.validateTitle(body.title ?? DEFAULT_REMINDER_TITLE);
    const sentAt = body.sentAt ? new Date(body.sentAt) : new Date();

    if (Number.isNaN(sentAt.getTime())) {
      throw new BadRequestException('sentAt không hợp lệ');
    }

    if (body.reminderId) {
      const reminder = await this.prisma.reminder.findFirst({
        where: { id: body.reminderId, userId },
        select: { id: true },
      });

      if (!reminder) {
        throw new NotFoundException('Không tìm thấy reminder');
      }
    }

    return this.prisma.notificationLog.create({
      data: {
        userId,
        reminderId: body.reminderId ?? null,
        title,
        body: this.normalizeNullableText(body.body, 240),
        sentAt,
      },
    });
  }

  async getLogs(userId: string, limit: number) {
    const take = Math.min(Math.max(limit, 1), 50);

    return this.prisma.notificationLog.findMany({
      where: { userId },
      orderBy: { createdAt: 'desc' },
      take,
    });
  }

  async markLogAsRead(userId: string, id: string) {
    const result = await this.prisma.notificationLog.updateMany({
      where: { id, userId },
      data: { readAt: new Date() },
    });

    if (result.count === 0) {
      throw new NotFoundException('Không tìm thấy notification log');
    }

    return { read: true };
  }

  private toSettingsPayload(
    setting: {
      notificationsEnabled: boolean;
      pushNotifications: boolean;
      emailNotifications: boolean;
      reminderTime: string | null;
      timezone: string | null;
    } | null,
    reminder: {
      id: string;
      title: string;
      message: string | null;
      timeOfDay: string;
      daysOfWeek: Prisma.JsonValue;
      timezone: string;
      isEnabled: boolean;
    } | null,
  ): NotificationSettingsPayload {
    const notificationsEnabled = setting?.notificationsEnabled ?? true;
    const pushNotifications = setting?.pushNotifications ?? true;

    return {
      notificationsEnabled,
      pushNotifications,
      emailNotifications: setting?.emailNotifications ?? false,
      reminderTime:
        setting?.reminderTime ?? reminder?.timeOfDay ?? DEFAULT_REMINDER_TIME,
      timezone: setting?.timezone ?? reminder?.timezone ?? DEFAULT_TIMEZONE,
      dailyReminder: {
        id: reminder?.id ?? null,
        enabled:
          reminder?.isEnabled ?? (notificationsEnabled && pushNotifications),
        title: reminder?.title ?? DEFAULT_REMINDER_TITLE,
        message: reminder?.message ?? DEFAULT_REMINDER_MESSAGE,
        daysOfWeek: this.normalizeDaysOfWeek(reminder?.daysOfWeek),
      },
    };
  }

  private validateTimeOfDay(value: string): string {
    const normalized = value.trim();
    const match = /^(\d{2}):(\d{2})$/.exec(normalized);
    if (!match) {
      throw new BadRequestException('reminderTime phải có dạng HH:mm');
    }

    const hour = Number(match[1]);
    const minute = Number(match[2]);
    if (hour > 23 || minute > 59) {
      throw new BadRequestException('reminderTime không hợp lệ');
    }

    return normalized;
  }

  private validateTimezone(value: string): string {
    const normalized = value.trim();
    if (!normalized || normalized.length > 64) {
      throw new BadRequestException('timezone không hợp lệ');
    }

    try {
      new Intl.DateTimeFormat('en-US', { timeZone: normalized });
    } catch {
      throw new BadRequestException('timezone không hợp lệ');
    }

    return normalized;
  }

  private validateTitle(value: string): string {
    const normalized = value.trim();
    if (!normalized) {
      throw new BadRequestException('Tiêu đề thông báo không được để trống');
    }
    if (normalized.length > 120) {
      throw new BadRequestException('Tiêu đề thông báo tối đa 120 ký tự');
    }

    return normalized;
  }

  private validateDaysOfWeek(value: number[]): number[] {
    if (!Array.isArray(value) || value.length === 0) {
      throw new BadRequestException('daysOfWeek không hợp lệ');
    }

    const days = Array.from(new Set(value.map((day) => Number(day)))).sort(
      (a, b) => a - b,
    );

    if (days.some((day) => !Number.isInteger(day) || day < 1 || day > 7)) {
      throw new BadRequestException('daysOfWeek chỉ nhận giá trị từ 1 đến 7');
    }

    return days;
  }

  private normalizeDaysOfWeek(value: Prisma.JsonValue | undefined): number[] {
    if (!Array.isArray(value)) {
      return DEFAULT_DAYS_OF_WEEK;
    }

    const days = value.filter(
      (day): day is number =>
        typeof day === 'number' &&
        Number.isInteger(day) &&
        day >= 1 &&
        day <= 7,
    );

    return days.length > 0 ? days : DEFAULT_DAYS_OF_WEEK;
  }

  private validatePlatform(value: string | undefined): string {
    const platform = value?.trim().toLowerCase() || 'android';
    if (!['android', 'ios'].includes(platform)) {
      throw new BadRequestException('platform chỉ hỗ trợ android hoặc ios');
    }

    return platform;
  }

  private normalizeNullableText(
    value: string | null | undefined,
    maxLength: number,
  ): string | null {
    if (value === null || value === undefined) {
      return null;
    }

    const normalized = value.trim();
    if (!normalized) {
      return null;
    }
    if (normalized.length > maxLength) {
      throw new BadRequestException(`Nội dung tối đa ${maxLength} ký tự`);
    }

    return normalized;
  }
}
