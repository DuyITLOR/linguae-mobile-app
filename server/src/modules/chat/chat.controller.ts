import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { HttpResponseBody, HttpResponseService } from '../../common';
import { AskChatDto } from './dto/ask-chat.dto';
import { ChatService } from './chat.service';

interface AskChatPayload {
  answer: string;
  model: string;
}

@Controller('chat')
export class ChatController {
  constructor(
    private readonly chatService: ChatService,
    private readonly httpResponse: HttpResponseService,
  ) {}

  @Post('ask')
  @HttpCode(HttpStatus.OK)
  async ask(
    @Body() body: AskChatDto,
  ): Promise<HttpResponseBody<AskChatPayload>> {
    const result = await this.chatService.ask(body);
    return this.httpResponse.ok(result, 'Lấy phản hồi chatbot thành công');
  }
}
