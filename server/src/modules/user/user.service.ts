import {
  BadRequestException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { UserRoles } from '@prisma/client';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateMyProfileDto } from './dto/update-my-profile.dto';
import { SupabaseStorageService } from '../../common/services/supbase-storage.service';
interface PublicUserProfile {
  id: string;
  email: string;
  fullName: string;
  avatarUrl: string | null;
  role: string;
}

@Injectable()
export class UserService {
  constructor(
    private readonly prisma: PrismaService,
    private readonly supabaseStorage: SupabaseStorageService,
  ) {}

  async getMyProfile(userId: string): Promise<PublicUserProfile> {
    const foundUser: unknown = await this.prisma.user.findUnique({
      where: { id: userId },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
        role: true,
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
    avatar?: Express.Multer.File,
  ): Promise<PublicUserProfile> {
    const data: Record<string, unknown> = {
      updatedAt: new Date(),
    };

    if (body.fullName !== undefined) {
      const fullName = body.fullName.trim();
      if (!fullName) {
        throw new BadRequestException('Tên hiển thị không được để trống');
      }
      data.fullName = fullName;
    }

    if (avatar) {
      const allowedMimeTypes = ['image/jpeg', 'image/png', 'image/webp'];
      if (!allowedMimeTypes.includes(avatar.mimetype)) {
        throw new BadRequestException('Avatar chỉ hỗ trợ JPG, PNG hoặc WEBP');
      }

      if (avatar.size > 5 * 1024 * 1024) {
        throw new BadRequestException('Avatar tối đa 5MB');
      }

      const uploadResult = await this.supabaseStorage.uploadFile({
        bucket: 'avatar',
        path: `users/${userId}/avatar`,
        file: avatar,
      });

      data.avatarUrl = uploadResult.publicUrl;
    }

    if (Object.keys(data).length === 1) {
      throw new BadRequestException('Vui lòng cung cấp thông tin để cập nhật');
    }

    const updatedUser = await this.prisma.user.update({
      where: { id: userId },
      data,
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
        role: true,
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
      (typeof candidate.avatarUrl === 'string' || candidate.avatarUrl === null) &&
      typeof candidate.role === 'string'
    );
  }

  async updateUserRole(userId: string, role: UserRoles) {
    const user = await this.prisma.user.findUnique({ where: { id: userId } });
    if (!user) throw new NotFoundException('Không tìm thấy người dùng');

    return this.prisma.user.update({
      where: { id: userId },
      data: { role, updatedAt: new Date() },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
        role: true,
        isActive: true,
      },
    });
  }

  async deleteUser(userId: string) {
    const user = await this.prisma.user.findUnique({ where: { id: userId } });
    if (!user) throw new NotFoundException('Không tìm thấy người dùng');

    await this.prisma.$transaction(async (tx) => {
      await tx.flashcardReview.deleteMany({ where: { FlashcardSession: { userId } } });
      await tx.flashcardSession.deleteMany({ where: { userId } });

      await tx.practiceAnswer.deleteMany({ where: { PracticeQuestion: { PracticeSession: { userId } } } });
      await tx.practiceQuestion.deleteMany({ where: { PracticeSession: { userId } } });
      await tx.practiceSession.deleteMany({ where: { userId } });

      await tx.dailyTaskCompletion.deleteMany({ where: { DailyTask: { DailyMission: { userId } } } });
      await tx.dailyTask.deleteMany({ where: { DailyMission: { userId } } });
      await tx.dailyMission.deleteMany({ where: { userId } });

      await tx.learningHistory.deleteMany({ where: { userId } });
      await tx.notificationLog.deleteMany({ where: { userId } });
      await tx.reminder.deleteMany({ where: { userId } });
      await tx.favoriteVocabulary.deleteMany({ where: { userId } });
      await tx.userDailyGoal.deleteMany({ where: { userId } });
      await tx.userDeviceToken.deleteMany({ where: { userId } });
      await tx.userLearningPlan.deleteMany({ where: { userId } });
      await tx.userSetting.deleteMany({ where: { userId } });
      await tx.userVocabularyProgress.deleteMany({ where: { userId } });

      await tx.user.delete({ where: { id: userId } });
    });
  }

  async getAllUsers() {
    return this.prisma.user.findMany({
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
        role: true,
        isActive: true,
        createdAt: true,
        updatedAt: true,
        provider: true,
        UserDailyGoal: true,
        UserSetting: true,
        UserLearningPlan: {
          where: { isCurrent: true },
        },
        _count: {
          select: {
            PracticeSession: true,
            FlashcardSession: true,
            FavoriteVocabulary: true,
            LearningHistory: true,
            UserVocabularyProgress: true,
          },
        },
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  async searchUsers(query: string) {
    return this.prisma.user.findMany({
      where: {
        OR: [
          { email: { contains: query, mode: 'insensitive' } },
          { fullName: { contains: query, mode: 'insensitive' } },
        ],
      },
      select: {
        id: true,
        email: true,
        fullName: true,
        avatarUrl: true,
        role: true,
        isActive: true,
        createdAt: true,
        updatedAt: true,
        provider: true,
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  async getNumberOfUsers(): Promise<number> {
    const count = await this.prisma.user.count();
    return count;
  }
}
