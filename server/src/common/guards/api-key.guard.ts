import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { Request } from 'express';
import { IS_PUBLIC_KEY } from '../decorators/public.decorator';
import { FirebaseAuthService } from '../services/firebase-auth.service';

@Injectable()
export class ApiKeyGuard implements CanActivate {
  constructor(
    private readonly reflector: Reflector,
    private readonly firebaseAuthService: FirebaseAuthService,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const isPublic = this.reflector.getAllAndOverride<boolean>(IS_PUBLIC_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (isPublic) {
      return true;
    }

    const request = context.switchToHttp().getRequest<Request>();
    const authRequired = process.env.AUTH_REQUIRED !== 'false';

    if (!authRequired) {
      return true;
    }

    const configuredApiKey = process.env.INTERNAL_API_KEY;
    const incomingApiKey = request.headers['x-api-key'];

    if (configuredApiKey && incomingApiKey !== configuredApiKey) {
      throw new UnauthorizedException('Invalid or missing x-api-key header');
    }

    const authHeader = request.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new UnauthorizedException(
        'Missing or invalid Authorization header (Bearer token required)',
      );
    }

    const idToken = authHeader.slice(7).trim();

    if (!idToken) {
      throw new UnauthorizedException('Bearer token is empty');
    }

    const decodedToken = await this.firebaseAuthService.verifyIdToken(idToken);

    // Attach user context for downstream handlers/services.
    request['user'] = decodedToken;

    return true;
  }
}
