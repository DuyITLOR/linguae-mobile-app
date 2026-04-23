import {
    Injectable,
    Logger,
} from '@nestjs/common';

import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class ClozeService {
    constructor(private readonly prismaService: PrismaService) { }

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
            where: { topicId },
            include: { ClozeOptions: true }
        });
        return question;
    }

    async createClozeQuestion(data: any) {
        // Ensure TopicPracticeConfig exists for this topic and has 'CLOZE' type
        const config = await this.prismaService.topicPracticeConfig.findFirst({
            where: { topicId: data.topicId }
        });

        if (config) {
            if (!config.questionType.includes('Cloze')) {
                await this.prismaService.topicPracticeConfig.update({
                    where: { id: config.id },
                    data: {
                        questionType: {
                            push: 'Cloze'
                        }
                    }
                });
            }
        } else {
            await this.prismaService.topicPracticeConfig.create({
                data: {
                    topicId: data.topicId,
                    questionType: ['Cloze']
                }
            });
        }

        return this.prismaService.clozeQuestion.create({
            data: {
                topicId: data.topicId,
                sentence: data.sentence,
                ClozeOptions: {
                    create: data.options.map((opt: any, index: number) => ({
                        id: index + 1,
                        optionText: opt.optionText,
                        isCorrect: opt.isCorrect,
                        blankIndex: opt.blankIndex
                    }))
                }
            },
            include: {
                ClozeOptions: true
            }
        });
    }

    async deleteClozeQuestion(id: number) {
        // Delete options first due to cascade or manual cleanup
        await this.prismaService.clozeOptions.deleteMany({
            where: { questionId: id }
        });
        return this.prismaService.clozeQuestion.delete({
            where: { id }
        });
    }
}
