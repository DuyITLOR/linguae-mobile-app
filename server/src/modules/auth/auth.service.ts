import {
  BadRequestException,
  ConflictException,
  InternalServerErrorException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { createHmac, randomBytes, scryptSync, timingSafeEqual } from 'crypto';
import { PrismaService } from '../prisma/prisma.service';
import { SignInDto } from './dto/sign-in.dto';
import { SignUpDto } from './dto/sign-up.dto';

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
}

@Injectable()
export class AuthService {
  constructor(private readonly prisma: PrismaService) {}

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
      throw new ConflictException('Email is already registered');
    }

    const passwordHash = this.hashPassword(password);

    const createdUser: unknown = await this.prisma.user.create({
      data: {
        email,
        fullName,
        passwordHash,
      },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
      },
    });

    if (!this.isSignUpUserRow(createdUser)) {
      throw new InternalServerErrorException('Created user payload is invalid');
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
      throw new BadRequestException('Email and password are required');
    }

    const foundUser: unknown = await this.prisma.user.findFirst({
      where: { email },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
        passwordHash: true,
      },
    });

    if (!foundUser) {
      throw new UnauthorizedException('Invalid email or password');
    }

    if (!this.isSignInUserRow(foundUser)) {
      throw new InternalServerErrorException('User payload is invalid');
    }

    const user = foundUser;

    if (
      !user?.passwordHash ||
      !this.verifyPassword(password, user.passwordHash)
    ) {
      throw new UnauthorizedException('Invalid email or password');
    }

    return {
      user: {
        id: user.id,
        email: user.email,
        fullName: user.fullName,
        avatarUrl: user.avatarUrl,
      },
      accessToken: this.generateAccessToken(user.id, user.email),
    };
  }

  private validateSignUpInput(input: {
    email?: string;
    fullName?: string;
    password?: string;
  }): void {
    if (!input.email || !input.fullName || !input.password) {
      throw new BadRequestException(
        'Full name, email and password are required',
      );
    }

    if (!this.isValidEmail(input.email)) {
      throw new BadRequestException('Email format is invalid');
    }

    if (input.password.length < 6) {
      throw new BadRequestException('Password must be at least 6 characters');
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

  private toBase64Url(value: string): string {
    return Buffer.from(value).toString('base64url');
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
      'passwordHash' in value &&
      (typeof value.passwordHash === 'string' || value.passwordHash === null)
    );
  }
}
