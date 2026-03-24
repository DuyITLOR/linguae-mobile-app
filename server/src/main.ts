import 'dotenv/config';
import { NestFactory } from '@nestjs/core';
import { NextFunction, Request, Response } from 'express';
import { AppModule } from './app.module';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  app.use((req: Request, res: Response, next: NextFunction) => {
    const startedAt = Date.now();

    res.on('finish', () => {
      const durationMs = Date.now() - startedAt;
      const timestamp = new Date().toISOString();
      console.log(
        `[${timestamp}] ${req.method} ${req.originalUrl} ${res.statusCode} ${durationMs}ms`,
      );
    });

    next();
  });

  const port = process.env.PORT ?? 3000;
  await app.listen(port);

  console.log(`Server is running at: http://localhost:${process.env.PORT}`);
}
bootstrap();
