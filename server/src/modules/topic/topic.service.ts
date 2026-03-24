import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class TopicService {
  constructor(private readonly prisma: PrismaService) {}

  async getAllTopic() {
    return await this.prisma.topic.findMany();
  }
}
