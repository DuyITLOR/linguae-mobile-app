import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { Public } from '../../common';
import { HttpResponseService } from '../../common';
import type { HttpResponseBody } from '../../common';
import { SignInDto } from './dto/sign-in.dto';
import { SignUpDto } from './dto/sign-up.dto';
import { GoogleAuthDto } from './dto/google-auth.dto';
import { AuthService } from './auth.service';
import { ForgotPasswordDto } from './dto/forgot-password.dto';
import { ResetPasswordDto } from './dto/reset-password.dto';

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

  @Public()
  @Post('google')
  @HttpCode(HttpStatus.OK)
  async signinWithGoogle(
    @Body() body: GoogleAuthDto,
  ): Promise<HttpResponseBody<AuthPayload>> {
    const result = await this.authService.signInWithGoogle(body);
    return this.httpResponse.ok(result, 'Đăng nhập bằng Google thành công');
  }

  @Public()
  @Post('forgot-password')
  @HttpCode(HttpStatus.OK)
  async forgotPassword(
    @Body() body: ForgotPasswordDto,
  ): Promise<HttpResponseBody<{ email: string }>> {
    const result = await this.authService.forgotPassword(body);
    return this.httpResponse.ok(result, 'Đã gửi mã OTP đặt lại mật khẩu');
  }

  @Public()
  @Post('reset-password')
  @HttpCode(HttpStatus.OK)
  async resetPassword(
    @Body() body: ResetPasswordDto,
  ): Promise<HttpResponseBody<null>> {
    await this.authService.resetPassword(body);
    return this.httpResponse.ok(null, 'Đặt lại mật khẩu thành công');
  }
}
