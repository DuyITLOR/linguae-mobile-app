import {
  BadRequestException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateMyProfileDto } from './dto/update-my-profile.dto';

interface PublicUserProfile {
  id: string;
  email: string;
  fullName: string;
  avatarUrl: string | null;
}

@Injectable()
export class UserService {
  constructor(private readonly prisma: PrismaService) {}

  async getMyProfile(userId: string): Promise<PublicUserProfile> {
    const foundUser: unknown = await this.prisma.user.findUnique({
      where: { id: userId },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
      },
    });

    if (!this.isPublicUserProfile(foundUser)) {
      throw new NotFoundException('Không tìm thấy người dùng');
    }

    return foundUser;
  }

  async updateMyProfile(
    userId: string,
    body: UpdateMyProfileDto,
  ): Promise<PublicUserProfile> {
    const fullName =
      typeof body.fullName === 'string' ? body.fullName.trim() : undefined;
    const avatarUrl = this.normalizeAvatarUrl(body.avatarUrl);

    if (fullName === undefined && body.avatarUrl === undefined) {
      throw new BadRequestException(
        'Vui lòng cung cấp thông tin để cập nhật',
      );
    }

    if (fullName !== undefined && fullName.length === 0) {
      throw new BadRequestException('Tên hiển thị không được để trống');
    }

    const updatedUser: unknown = await this.prisma.user.update({
      where: { id: userId },
      data: {
        ...(fullName !== undefined ? { fullName } : {}),
        ...(body.avatarUrl !== undefined ? { avatarUrl } : {}),
        updatedAt: new Date(),
      },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
      },
    });

    if (!this.isPublicUserProfile(updatedUser)) {
      throw new InternalServerErrorException(
        'Dữ liệu người dùng sau cập nhật không hợp lệ',
      );
    }

    return updatedUser;
  }

  private normalizeAvatarUrl(
    avatarUrl: string | null | undefined,
  ): string | null | undefined {
    if (avatarUrl === undefined) {
      return undefined;
    }

    if (avatarUrl === null) {
      return null;
    }

    const normalizedAvatarUrl = avatarUrl.trim();
    return normalizedAvatarUrl.length > 0 ? normalizedAvatarUrl : null;
  }

  private isPublicUserProfile(value: unknown): value is PublicUserProfile {
    if (!value || typeof value !== 'object') {
      return false;
    }

    const candidate = value as Record<string, unknown>;

    return (
      typeof candidate.id === 'string' &&
      typeof candidate.email === 'string' &&
      typeof candidate.fullName === 'string' &&
      (typeof candidate.avatarUrl === 'string' || candidate.avatarUrl === null)
    );
  }
}
