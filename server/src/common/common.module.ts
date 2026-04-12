import { Global, Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { ApiKeyGuard } from './guards/api-key.guard';
import { HttpResponseService } from './services/http-response.service';
import { SupabaseStorageService } from './services/supbase-storage.service';

@Global()
@Module({
  providers: [
    HttpResponseService,
    SupabaseStorageService,
    {
      provide: APP_GUARD,
      useClass: ApiKeyGuard,
    },
  ],
  exports: [HttpResponseService, SupabaseStorageService],
})
export class CommonModule {}
