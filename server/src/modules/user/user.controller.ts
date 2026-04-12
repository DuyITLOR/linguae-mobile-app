import { Body, Controller, Get, Patch, UploadedFile, UseInterceptors, } from '@nestjs/common';
import { HttpResponseBody, HttpResponseService, UserId } from '../../common';
import { UpdateMyProfileDto } from './dto/update-my-profile.dto';
import { UserService } from './user.service';
import { FileInterceptor } from '@nestjs/platform-express';
import { memoryStorage } from 'multer';

interface UserProfilePayload {
  id: string;
  email: string;
  fullName: string;
  avatarUrl: string | null;
}

@Controller('users')
export class UserController {
  constructor(
    private readonly userService: UserService,
    private readonly httpResponse: HttpResponseService,
  ) {}

  @Get('info')
  async getMyProfile(
    @UserId() userId: string,
  ): Promise<HttpResponseBody<UserProfilePayload>> {
    const result = await this.userService.getMyProfile(userId);
    return this.httpResponse.ok(result, 'Lấy thông tin người dùng thành công');
  }

  @Patch('info')
  @UseInterceptors(
    FileInterceptor('avatar', {
      storage: memoryStorage(),
    }),
  )
  async updateMyProfile(
    @UserId() userId: string,
    @Body() body: UpdateMyProfileDto,
    @UploadedFile() avatar?: Express.Multer.File,
  ): Promise<HttpResponseBody<UserProfilePayload>> {
    const result = await this.userService.updateMyProfile(userId, body, avatar);
    return this.httpResponse.ok(
      result,
      'Cập nhật thông tin người dùng thành công',
    );
  }
}
