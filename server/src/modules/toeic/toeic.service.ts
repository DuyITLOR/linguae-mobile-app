import {
  ForbiddenException,
  Injectable,
  InternalServerErrorException,
  NotFoundException,
} from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { CreateToeicDto } from './dto/createToeic.dto';
import { UpdateToeicDto } from './dto/updateToeic.dto';

@Injectable()
export class ToeicService {
  constructor(private readonly prisma: PrismaService) {}

  // ==================== Get Section ====================
  async getAllToeics() {
    try {
      const toeics = await this.prisma.toeic.findMany();
      return toeics;
    } catch (error) {
      const msg =
        error instanceof NotFoundException
          ? 'Toeics not found'
          : 'Failed to get toeics';
      throw new InternalServerErrorException(msg);
    }
  }

  async getToeicById(id: string) {
    try {
      const toeic = await this.prisma.toeic.findUnique({
        where: { id },
      });

      if (!toeic) {
        throw new NotFoundException('Toeic not found');
      }

      return toeic;
    } catch (error) {
      if (error instanceof NotFoundException) {
        throw error;
      }
      throw new InternalServerErrorException('Failed to get toeic');
    }
  }

  // ==================== Create section ======================

  async createToeic(dto: CreateToeicDto) {
    await this.checkPermission(dto.userId);

    const toeic = await this.prisma.toeic.create({
      data: {
        title: dto.title,
        level: dto.level,
      },
      select: {
        id: true,
        title: true,
        level: true,
      },
    });

    return toeic;
  }

  // ==================== Update section ======================
  async updateToeic(dto: UpdateToeicDto) {
    try {
      await this.checkPermission(dto.userId);
      const existingToeic = await this.prisma.toeic.findUnique({
        where: { id: dto.id },
      });

      if (!existingToeic) {
        throw new NotFoundException('Toeic not found');
      }

      const toeic = await this.prisma.toeic.update({
        where: { id: dto.id },
        data: {
          title: dto.title,
          level: dto.level,
        },
      });

      return toeic;
    } catch (error) {
      if (error instanceof NotFoundException) {
        throw error;
      }
      throw new InternalServerErrorException('Failed to update toeic');
    }
  }

  // ==================== Delete section ======================
  async deleteToeic(id: string, userId: string) {
    try {
      await this.checkPermission(userId);
      const existingToeic = await this.prisma.toeic.findUnique({
        where: { id },
      });

      if (!existingToeic) {
        throw new NotFoundException('Toeic not found');
      }

      await this.prisma.toeic.delete({
        where: { id },
      });

      return { message: 'Toeic deleted successfully' };
    } catch (error) {
      if (error instanceof NotFoundException) {
        throw error;
      }
      throw new InternalServerErrorException('Failed to delete toeic');
    }
  }

  // ==================== Helper functions ======================
  async checkPermission(userId: string) {
    try {
      const user = await this.prisma.user.findUnique({
        where: { id: userId },
      });
      if (!user) {
        throw new NotFoundException('User not found');
      }
      if (user.role !== 'ADMIN') {
        throw new ForbiddenException('User is not an admin');
      }
    } catch (error) {
      const msg =
        error instanceof NotFoundException ||
        error instanceof ForbiddenException;
      throw new InternalServerErrorException(msg);
    }
  }
}
