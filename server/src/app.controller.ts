import { Controller, Get } from '@nestjs/common';
import { HttpResponseService, Public } from './common';
import type { HttpResponseBody } from './common';
import { AppService } from './app.service';

@Controller()
export class AppController {
  constructor(
    private readonly appService: AppService,
    private readonly httpResponse: HttpResponseService,
  ) {}

  @Public()
  @Get('health')
  health(): HttpResponseBody<{ message: string }> {
    return this.httpResponse.ok(
      { message: this.appService.getHello() },
      'Server is running',
    );
  }
}
