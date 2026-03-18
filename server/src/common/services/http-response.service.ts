import { HttpStatus, Injectable } from '@nestjs/common';

export interface HttpResponseBody<T = unknown> {
  success: boolean;
  statusCode: number;
  message: string;
  data?: T;
}

@Injectable()
export class HttpResponseService {
  ok<T>(data: T, message = 'Success'): HttpResponseBody<T> {
    return {
      success: true,
      statusCode: HttpStatus.OK,
      message,
      data,
    };
  }

  created<T>(data: T, message = 'Created'): HttpResponseBody<T> {
    return {
      success: true,
      statusCode: HttpStatus.CREATED,
      message,
      data,
    };
  }

  fail(message: string, statusCode = HttpStatus.BAD_REQUEST): HttpResponseBody {
    return {
      success: false,
      statusCode,
      message,
    };
  }
}
