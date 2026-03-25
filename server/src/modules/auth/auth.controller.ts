import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { Public } from '../../common';
import { HttpResponseService } from '../../common';
import type { HttpResponseBody } from '../../common';
import { SignInDto } from './dto/sign-in.dto';
import { SignUpDto } from './dto/sign-up.dto';
import { AuthService } from './auth.service';

interface AuthPayload {
  user: {
    id: string;
    email: string;
    fullName: string;
    avatarUrl: string | null;
  };
  accessToken: string;
}

@Controller('auth')
export class AuthController {
  constructor(
    private readonly authService: AuthService,
    private readonly httpResponse: HttpResponseService,
  ) {}

  @Public()
  @Post('sign-up')
  async signUp(
    @Body() body: SignUpDto,
  ): Promise<HttpResponseBody<AuthPayload>> {
    const result = await this.authService.signUp(body);
    return this.httpResponse.created(result, 'Đăng ký thành công');
  }

  @Public()
  @Post('sign-in')
  @HttpCode(HttpStatus.OK)
  async signIn(
    @Body() body: SignInDto,
  ): Promise<HttpResponseBody<AuthPayload>> {
    const result = await this.authService.signIn(body);
    return this.httpResponse.ok(result, 'Đăng nhập thành công');
  }
}
