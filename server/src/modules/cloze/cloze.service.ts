import {
    Injectable,
    Logger,
} from '@nestjs/common';

import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class ClozeService {
    constructor(private readonly prismaService: PrismaService) {}

    async getClozeQuestions() {
        const questions = await this.prismaService.clozeQuestion.findMany({
            take: 10,
        });
        return questions;
    }

    async getClozeOptionsWithIds(ids: number[]) {
        Logger.log("IDs received in service:", ids);
        const options = await this.prismaService.clozeOptions.findMany({
            where: { questionId: { in: ids } },
        });
        return options;
    }

    async getClozeOptionsWithId(id: number) {
        const options = await this.prismaService.clozeOptions.findMany({
            where: { id }
        });
        return options;
    }

    async getClozeQuestionByTopicId(topicId: string) {
        const question = await this.prismaService.clozeQuestion.findMany({
            where: { topicId }
        });
        return question;
    }
}
