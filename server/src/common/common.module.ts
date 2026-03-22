import { Global, Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { ApiKeyGuard } from './guards/api-key.guard';
import { HttpResponseService } from './services/http-response.service';

@Global()
@Module({
  providers: [
    HttpResponseService,
    {
      provide: APP_GUARD,
      useClass: ApiKeyGuard,
    },
  ],
  exports: [HttpResponseService],
})
export class CommonModule {}
