import { Body, Controller, Get, Patch, UploadedFile, UseGuards, UseInterceptors, } from '@nestjs/common';
import { AdminGuard, HttpResponseBody, HttpResponseService, UserId } from '../../common';
import { UpdateMyProfileDto } from './dto/update-my-profile.dto';
import { UserService } from './user.service';
import { FileInterceptor } from '@nestjs/platform-express';
import { memoryStorage } from 'multer';

interface UserProfilePayload {
  id: string;
  email: string;
  fullName: string;
  avatarUrl: string | null;
  role: string;
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

  @Get()
  @UseGuards(AdminGuard)
  async getAllUsers(): Promise<HttpResponseBody<unknown>> {
    const result = await this.userService.getAllUsers();
    return this.httpResponse.ok(result, 'Lấy danh sách người dùng thành công');
  }

  @Get('number-of-users')
  async getNumberOfUsers() {
    return this.userService.getNumberOfUsers();
  }
}
