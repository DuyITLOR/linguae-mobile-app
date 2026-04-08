import { Module } from "@nestjs/common";
import { PrismaModule } from "../prisma/prisma.module";
import { ClozeService } from "./cloze.service";
import { ClozeController } from "./cloze.controller";


@Module({
    imports: [PrismaModule],
    controllers: [ClozeController],
    providers: [ClozeService],
})
export class ClozeModule {}