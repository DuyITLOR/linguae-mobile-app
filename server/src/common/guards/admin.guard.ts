import {
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { Request } from 'express';
import { PrismaService } from '../../modules/prisma/prisma.service';

@Injectable()
export class AdminGuard implements CanActivate {
  constructor(private readonly prisma: PrismaService) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request = context.switchToHttp().getRequest<Request>();
    const authHeader = request.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new UnauthorizedException('Missing or invalid Authorization header');
    }

    const token = authHeader.slice(7).trim();

    let userId: string;
    try {
      const parts = token.split('.');
      if (parts.length !== 3) throw new Error();
      const payload = JSON.parse(Buffer.from(parts[1], 'base64url').toString());
      userId = payload.sub;
      if (!userId) throw new Error();
    } catch {
      throw new UnauthorizedException('Invalid token');
    }

    const user = await this.prisma.user.findUnique({
      where: { id: userId },
      select: { role: true },
    });

    if (!user) {
      throw new UnauthorizedException('User not found');
    }

    if (user.role !== 'ADMIN') {
      throw new ForbiddenException('Admin access required');
    }

    return true;
  }
}
