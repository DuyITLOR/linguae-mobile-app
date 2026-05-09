import {
  Body,
  Controller,
  Delete,
  DefaultValuePipe,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  ParseIntPipe,
  Patch,
  Post,
  Query,
} from '@nestjs/common';
import { HttpResponseBody, HttpResponseService, UserId } from '../../common';
import { CreateNotificationLogDto } from './dto/create-notification-log.dto';
import { RegisterDeviceTokenDto } from './dto/register-device-token.dto';
import { UpdateNotificationSettingsDto } from './dto/update-notification-settings.dto';
import { NotificationsService } from './notifications.service';

@Controller('notifications')
export class NotificationsController {
  constructor(
    private readonly notificationsService: NotificationsService,
    private readonly httpResponse: HttpResponseService,
  ) {}

  @Get('settings')
  async getSettings(@UserId() userId: string): Promise<HttpResponseBody> {
    const result = await this.notificationsService.getSettings(userId);
    return this.httpResponse.ok(result, 'Lấy cấu hình thông báo thành công');
  }

  @Patch('settings')
  async updateSettings(
    @UserId() userId: string,
    @Body() body: UpdateNotificationSettingsDto,
  ): Promise<HttpResponseBody> {
    const result = await this.notificationsService.updateSettings(userId, body);
    return this.httpResponse.ok(
      result,
      'Cập nhật cấu hình thông báo thành công',
    );
  }

  @Post('device-token')
  async registerDeviceToken(
    @UserId() userId: string,
    @Body() body: RegisterDeviceTokenDto,
  ): Promise<HttpResponseBody> {
    const result = await this.notificationsService.registerDeviceToken(
      userId,
      body,
    );
    return this.httpResponse.ok(result, 'Đồng bộ device token thành công');
  }

  @Delete('device-token')
  @HttpCode(HttpStatus.OK)
  async deactivateDeviceToken(
    @UserId() userId: string,
    @Body() body: RegisterDeviceTokenDto,
  ): Promise<HttpResponseBody> {
    const result = await this.notificationsService.deactivateDeviceToken(
      userId,
      body.token ?? '',
    );
    return this.httpResponse.ok(result, 'Tắt device token thành công');
  }

  @Get('logs')
  async getLogs(
    @UserId() userId: string,
    @Query('limit', new DefaultValuePipe(20), ParseIntPipe) limit: number,
  ): Promise<HttpResponseBody> {
    const result = await this.notificationsService.getLogs(userId, limit);
    return this.httpResponse.ok(result, 'Lấy lịch sử thông báo thành công');
  }

  @Post('logs')
  async createLog(
    @UserId() userId: string,
    @Body() body: CreateNotificationLogDto,
  ): Promise<HttpResponseBody> {
    const result = await this.notificationsService.createLog(userId, body);
    return this.httpResponse.created(result, 'Ghi nhận thông báo thành công');
  }

  @Patch('logs/:id/read')
  async markLogAsRead(
    @UserId() userId: string,
    @Param('id') id: string,
  ): Promise<HttpResponseBody> {
    const result = await this.notificationsService.markLogAsRead(userId, id);
    return this.httpResponse.ok(result, 'Đã đánh dấu thông báo đã đọc');
  }
}
