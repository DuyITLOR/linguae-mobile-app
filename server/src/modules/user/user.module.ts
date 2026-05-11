import { Module } from '@nestjs/common';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { AdminGuard } from '../../common';

@Module({
  controllers: [UserController],
  providers: [UserService, AdminGuard],
})
export class UserModule {}
