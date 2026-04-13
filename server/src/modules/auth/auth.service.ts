import {
  BadRequestException,
  ConflictException,
  ForbiddenException,
  InternalServerErrorException,
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import {
  createHash,
  createHmac,
  randomBytes,
  randomInt,
  scryptSync,
  timingSafeEqual,
} from 'crypto';
import { OAuth2Client, TokenPayload } from 'google-auth-library';
import { PrismaService } from '../prisma/prisma.service';
import { ForgotPasswordDto } from './dto/forgot-password.dto';
import { GoogleAuthDto } from './dto/google-auth.dto';
import { ResetPasswordDto } from './dto/reset-password.dto';
import { SignInDto } from './dto/sign-in.dto';
import { SignUpDto } from './dto/sign-up.dto';
import { MailService } from '../mail/mail.service';

interface PublicUser {
  id: string;
  email: string;
  fullName: string;
  avatarUrl: string | null;
}

interface AuthResult {
  user: PublicUser;
  accessToken: string;
}

type SignUpUserRow = PublicUser;

interface SignInUserRow extends SignUpUserRow {
  passwordHash: string | null;
  provider: string;
}

interface ForgotPasswordUserRow {
  id: string;
  email: string;
  fullName: string;
  provider: string;
}

interface ResetPasswordUserRow extends ForgotPasswordUserRow {
  resetPasswordOtpHash: string | null;
  resetPasswordExpiresAt: Date | null;
}

@Injectable()
export class AuthService {
  private readonly googleClient: OAuth2Client;
  private readonly googleClientId: string;

  constructor(
    private readonly prisma: PrismaService,
    private readonly mailService: MailService,
  ) {
    this.googleClientId = process.env.GOOGLE_CLIENT_ID ?? '';
    this.googleClient = new OAuth2Client({
      clientId: this.googleClientId,
    });
  }

  async signUp(body: SignUpDto): Promise<AuthResult> {
    const email = body.email?.trim().toLowerCase();
    const fullName = body.fullName?.trim();
    const password = body.password;

    this.validateSignUpInput({ email, fullName, password });

    const existingUser = await this.prisma.user.findFirst({
      where: { email },
      select: { id: true },
    });

    if (existingUser) {
      throw new ConflictException('Email đã được đăng ký trước đó');
    }

    const passwordHash = this.hashPassword(password);

    const createdUser: unknown = await this.prisma.user.create({
      data: {
        email,
        fullName,
        passwordHash,
        provider: 'local',
      },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
      },
    });

    if (!this.isSignUpUserRow(createdUser)) {
      throw new InternalServerErrorException(
        'Đã xảy ra lỗi khi tạo người dùng',
      );
    }

    return {
      user: createdUser,
      accessToken: this.generateAccessToken(createdUser.id, createdUser.email),
    };
  }

  async signIn(body: SignInDto): Promise<AuthResult> {
    const email = body.email?.trim().toLowerCase();
    const password = body.password;

    if (!email || !password) {
      throw new BadRequestException('Vui lòng cung cấp email và mật khẩu');
    }

    const foundUser: unknown = await this.prisma.user.findFirst({
      where: { email },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
        passwordHash: true,
        provider: true,
      },
    });

    if (!foundUser) {
      throw new UnauthorizedException('Email hoặc mật khẩu không đúng');
    }

    if (!this.isSignInUserRow(foundUser)) {
      throw new InternalServerErrorException('Dữ liệu người dùng không hợp lệ');
    }

    if (foundUser.provider !== 'local') {
      throw new UnauthorizedException(
        'Tài khoản này không đăng nhập bằng mật khẩu',
      );
    }

    if (
      !foundUser.passwordHash ||
      !this.verifyPassword(password, foundUser.passwordHash)
    ) {
      throw new UnauthorizedException('Email hoặc mật khẩu không đúng');
    }

    return {
      user: {
        id: foundUser.id,
        email: foundUser.email,
        fullName: foundUser.fullName,
        avatarUrl: foundUser.avatarUrl,
      },
      accessToken: this.generateAccessToken(foundUser.id, foundUser.email),
    };
  }

  async signInWithGoogle(body: GoogleAuthDto): Promise<AuthResult> {
    const idToken = body.idToken?.trim();

    if (!idToken) {
      throw new BadRequestException('Vui lòng cung cấp idToken');
    }

    const payload = await this.verifyGoogleIdToken(idToken);
    const email = payload.email?.trim().toLowerCase();
    const avatarUrl = payload.picture || null;

    if (!email) {
      throw new UnauthorizedException('Google token không chứa email hợp lệ');
    }

    const fullName = payload.name?.trim() || email.split('@')[0];

    const existingUser: unknown = await this.prisma.user.findFirst({
      where: { email },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
        provider: true,
      },
    });

    if (existingUser) {
      if (!this.isGoogleLookupUserRow(existingUser)) {
        throw new InternalServerErrorException(
          'Dữ liệu người dùng không hợp lệ',
        );
      }

      if (existingUser.provider !== 'google') {
        throw new ConflictException(
          'Email này đã được đăng ký bằng phương thức khác',
        );
      }

      const updatedUser: unknown = await this.prisma.user.update({
        where: { id: existingUser.id },
        data: {
          fullName,
          avatarUrl,
        },
        select: {
          id: true,
          email: true,
          fullName: true,
          avatarUrl: true,
        },
      });

      if (!this.isGoogleUserRow(updatedUser)) {
        throw new InternalServerErrorException(
          'Dữ liệu người dùng không hợp lệ',
        );
      }

      return {
        user: updatedUser,
        accessToken: this.generateAccessToken(
          updatedUser.id,
          updatedUser.email,
        ),
      };
    }

    const createdUser: unknown = await this.prisma.user.create({
      data: {
        email,
        fullName,
        avatarUrl,
        provider: 'google',
      },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
      },
    });

    if (!this.isGoogleUserRow(createdUser)) {
      throw new InternalServerErrorException(
        'Đã xảy ra lỗi khi tạo người dùng',
      );
    }

    return {
      user: createdUser,
      accessToken: this.generateAccessToken(createdUser.id, createdUser.email),
    };
  }

  async forgotPassword(
    body: ForgotPasswordDto,
  ): Promise<{ email: string }> {
    const email = body.email?.trim().toLowerCase();

    if (!email) {
      throw new BadRequestException('Vui lòng cung cấp email');
    }

    if (!this.isValidEmail(email)) {
      throw new BadRequestException('Định dạng email không hợp lệ');
    }

    const foundUser: unknown = await this.prisma.user.findFirst({
      where: { email },
      select: {
        id: true,
        email: true,
        fullName: true,
        provider: true,
      },
    });

    if (!foundUser) {
      throw new NotFoundException('Tài khoản không tồn tại');
    }

    if (!this.isForgotPasswordUserRow(foundUser)) {
      throw new InternalServerErrorException('Dữ liệu người dùng không hợp lệ');
    }

    if (foundUser.provider !== 'local') {
      throw new ForbiddenException(
        'Tài khoản Google không hỗ trợ quên mật khẩu bằng OTP',
      );
    }

    const otp = this.generateResetPasswordOtp();
    const resetPasswordOtpHash = this.hashResetPasswordOtp(otp);
    const resetPasswordExpiresAt = new Date(Date.now() + 10 * 60 * 1000);

    await this.prisma.user.update({
      where: { id: foundUser.id },
      data: {
        resetPasswordOtpHash,
        resetPasswordExpiresAt,
      },
    });

    await this.mailService.sendResetPasswordEmail({
      email: foundUser.email,
      fullName: foundUser.fullName,
      otp,
    });

    return { email };
  }

  async resetPassword(body: ResetPasswordDto): Promise<void> {
    const email = body.email?.trim().toLowerCase();
    const otp = body.otp?.trim();
    const newPassword = body.newPassword;

    if (!email || !otp || !newPassword) {
      throw new BadRequestException(
        'Vui lòng cung cấp email, otp và mật khẩu mới',
      );
    }

    if (!this.isValidEmail(email)) {
      throw new BadRequestException('Định dạng email không hợp lệ');
    }

    if (newPassword.length < 6) {
      throw new BadRequestException('Mật khẩu phải có ít nhất 6 ký tự');
    }

    if (!/^\d{6}$/.test(otp)) {
      throw new BadRequestException('OTP phải gồm đúng 6 chữ số');
    }

    const foundUser: unknown = await this.prisma.user.findFirst({
      where: { email },
      select: {
        id: true,
        email: true,
        fullName: true,
        provider: true,
        resetPasswordOtpHash: true,
        resetPasswordExpiresAt: true,
      },
    });

    if (!foundUser || !this.isResetPasswordUserRow(foundUser)) {
      throw new ForbiddenException('OTP đặt lại mật khẩu không hợp lệ');
    }

    if (foundUser.provider !== 'local') {
      throw new ForbiddenException('Tài khoản Google không hỗ trợ đổi mật khẩu');
    }

    const resetPasswordOtpHash = this.hashResetPasswordOtp(otp);

    if (
      !foundUser.resetPasswordOtpHash ||
      foundUser.resetPasswordOtpHash !== resetPasswordOtpHash
    ) {
      throw new ForbiddenException('OTP đặt lại mật khẩu không hợp lệ');
    }

    if (
      !foundUser.resetPasswordExpiresAt ||
      foundUser.resetPasswordExpiresAt.getTime() < Date.now()
    ) {
      throw new ForbiddenException('OTP đặt lại mật khẩu đã hết hạn');
    }

    await this.prisma.user.update({
      where: { id: foundUser.id },
      data: {
        passwordHash: this.hashPassword(newPassword),
        resetPasswordOtpHash: null,
        resetPasswordExpiresAt: null,
      },
    });
  }

  private validateSignUpInput(input: {
    email?: string;
    fullName?: string;
    password?: string;
  }): void {
    if (!input.email || !input.fullName || !input.password) {
      throw new BadRequestException(
        'Vui lòng cung cấp họ tên, email và mật khẩu',
      );
    }

    if (!this.isValidEmail(input.email)) {
      throw new BadRequestException('Định dạng email không hợp lệ');
    }

    if (input.password.length < 6) {
      throw new BadRequestException('Mật khẩu phải có ít nhất 6 ký tự');
    }
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  private hashPassword(password: string): string {
    const salt = randomBytes(16).toString('hex');
    const hash = scryptSync(password, salt, 64).toString('hex');
    return `${salt}:${hash}`;
  }

  private verifyPassword(password: string, passwordHash: string): boolean {
    const [salt, originalHash] = passwordHash.split(':');

    if (!salt || !originalHash) {
      return false;
    }

    const hashBuffer = scryptSync(password, salt, 64);
    const originalHashBuffer = Buffer.from(originalHash, 'hex');

    if (hashBuffer.length !== originalHashBuffer.length) {
      return false;
    }

    return timingSafeEqual(hashBuffer, originalHashBuffer);
  }

  private generateAccessToken(userId: string, email: string): string {
    const secret = process.env.AUTH_TOKEN_SECRET ?? 'dev-auth-secret';
    const now = Math.floor(Date.now() / 1000);
    const exp = now + 60 * 60 * 24;

    const header = this.toBase64Url(
      JSON.stringify({ alg: 'HS256', typ: 'JWT' }),
    );
    const payload = this.toBase64Url(
      JSON.stringify({ sub: userId, email, iat: now, exp }),
    );

    const signature = createHmac('sha256', secret)
      .update(`${header}.${payload}`)
      .digest('base64url');

    return `${header}.${payload}.${signature}`;
  }

  private generateResetPasswordOtp(): string {
    return randomInt(100000, 1000000).toString();
  }

  private hashResetPasswordOtp(otp: string): string {
    return createHash('sha256').update(otp).digest('hex');
  }

  private toBase64Url(value: string): string {
    return Buffer.from(value).toString('base64url');
  }

  private async verifyGoogleIdToken(idToken: string): Promise<TokenPayload> {
    if (!this.googleClientId) {
      throw new InternalServerErrorException('Missing GOOGLE_CLIENT_ID');
    }

    try {
      const ticket = await this.googleClient.verifyIdToken({
        idToken,
        audience: this.googleClientId,
      });

      const payload = ticket.getPayload();

      if (!payload || !payload.email_verified) {
        throw new UnauthorizedException('Google token không hợp lệ');
      }

      return payload;
    } catch {
      throw new UnauthorizedException('Google token không hợp lệ');
    }
  }

  private isRecord(value: unknown): value is Record<string, unknown> {
    return typeof value === 'object' && value !== null;
  }

  private isSignUpUserRow(value: unknown): value is SignUpUserRow {
    if (!this.isRecord(value)) {
      return false;
    }

    return (
      typeof value.id === 'string' &&
      typeof value.email === 'string' &&
      typeof value.fullName === 'string' &&
      (typeof value.avatarUrl === 'string' || value.avatarUrl === null)
    );
  }

  private isSignInUserRow(value: unknown): value is SignInUserRow {
    if (!this.isSignUpUserRow(value)) {
      return false;
    }

    return (
      'provider' in value &&
      typeof value.provider === 'string' &&
      'passwordHash' in value &&
      (typeof value.passwordHash === 'string' || value.passwordHash === null)
    );
  }

  private isGoogleUserRow(value: unknown): value is PublicUser {
    return this.isSignUpUserRow(value);
  }

  private isGoogleLookupUserRow(
    value: unknown,
  ): value is PublicUser & { provider: string } {
    if (!this.isGoogleUserRow(value)) {
      return false;
    }

    return 'provider' in value && typeof value.provider === 'string';
  }

  private isForgotPasswordUserRow(
    value: unknown,
  ): value is ForgotPasswordUserRow {
    if (!this.isRecord(value)) {
      return false;
    }

    return (
      typeof value.id === 'string' &&
      typeof value.email === 'string' &&
      typeof value.fullName === 'string' &&
      typeof value.provider === 'string'
    );
  }

  private isResetPasswordUserRow(
    value: unknown,
  ): value is ResetPasswordUserRow {
    if (!this.isForgotPasswordUserRow(value) || !this.isRecord(value)) {
      return false;
    }

    return (
      (typeof value.resetPasswordOtpHash === 'string' ||
        value.resetPasswordOtpHash === null) &&
      (value.resetPasswordExpiresAt instanceof Date ||
        value.resetPasswordExpiresAt === null)
    );
  }
}
