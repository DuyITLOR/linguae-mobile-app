import {
    Injectable,
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
        const options = await this.prismaService.clozeOptions.findMany({
            where: { id: { in: ids } },
        });

        const grouped = options.reduce((acc, option) => {
        const key = option.questionId;
        if (!acc[key]) acc[key] = [];
        acc[key].push(option);
        return acc;
        }, {} as Record<number, typeof options>);

    return Object.values(grouped);
    }

    async getClozeOptionsWithId(id: number) {
        const options = await this.prismaService.clozeOptions.findMany({
            where: { id }
        });
        return options;
    }
}
