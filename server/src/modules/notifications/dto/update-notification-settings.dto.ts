export class UpdateNotificationSettingsDto {
  notificationsEnabled?: boolean;
  pushNotifications?: boolean;
  emailNotifications?: boolean;
  reminderTime?: string | null;
  timezone?: string | null;
  dailyReminderEnabled?: boolean;
  reminderTitle?: string;
  reminderMessage?: string | null;
  daysOfWeek?: number[];
}
