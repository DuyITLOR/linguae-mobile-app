import {
  BadRequestException,
  HttpException,
  Injectable,
  InternalServerErrorException,
  ServiceUnavailableException,
} from '@nestjs/common';
import { AskChatDto } from './dto/ask-chat.dto';

interface AskChatResult {
  answer: string;
  model: string;
}

interface GeminiGenerateContentPayload {
  modelVersion?: unknown;
  candidates?: Array<{
    content?: {
      parts?: Array<{
        text?: unknown;
      }>;
    };
  }>;
  error?: {
    message?: unknown;
  };
  message?: unknown;
}

@Injectable()
export class ChatService {
  private readonly apiBaseUrl =
    process.env.CHAT_API_URL?.trim() ||
    'https://generativelanguage.googleapis.com/v1beta';
  private readonly apiKey = process.env.GOOGLE_AI_STUDIO_API_KEY?.trim() || ''
  private readonly model = process.env.GOOGLE_AI_STUDIO_MODEL?.trim() || ''
  private readonly systemPrompt =
    [
      'Bạn là trợ lý học ngoại ngữ trong ứng dụng Linguae.',
      'Hãy trả lời ngắn gọn, dễ hiểu, thân thiện bằng tiếng Việt.',
      'Nếu người dùng hỏi ngữ pháp hoặc từ vựng, hãy đưa ví dụ đơn giản.',
      'Nếu không chắc, hãy nói rõ là bạn chưa chắc thay vì bịa thông tin.',
    ].join(' ');

  async ask(body: AskChatDto): Promise<AskChatResult> {
    const message = body.message?.trim();

    if (!message) {
      throw new BadRequestException('Vui lòng nhập câu hỏi');
    }

    if (message.length > 4000) {
      throw new BadRequestException('Câu hỏi quá dài, vui lòng rút gọn');
    }

    if (!this.apiKey) {
      throw new InternalServerErrorException(
        'Chưa cấu hình CHAT_API_KEY, GOOGLE_AI_STUDIO_API_KEY hoặc GEMINI_API_KEY',
      );
    }

    const abortController = new AbortController();
    const timeoutId = setTimeout(() => abortController.abort(), 30_000);

    try {
      const response = await fetch(this.buildRequestUrl(), {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          systemInstruction: {
            parts: [
              {
                text: this.systemPrompt,
              },
            ],
          },
          contents: [
            {
              role: 'user',
              parts: [
                {
                  text: message,
                },
              ],
            },
          ],
        }),
        signal: abortController.signal,
      });

      const payload = await this.parseJson(response);

      if (!response.ok) {
        const providerError = this.extractProviderError(payload);
        throw new InternalServerErrorException(
          `Không thể lấy phản hồi từ LLM${providerError ? `: ${providerError}` : ''}`,
        );
      }

      const answer = this.extractAnswer(payload);

      if (!answer) {
        throw new InternalServerErrorException(
          'LLM không trả về nội dung hợp lệ',
        );
      }

      return {
        answer,
        model: this.extractModel(payload) || this.model,
      };
    } catch (error) {
      if (error instanceof HttpException) {
        throw error;
      }

      if (error instanceof Error && error.name === 'AbortError') {
        throw new ServiceUnavailableException(
          'LLM phản hồi quá lâu, vui lòng thử lại',
        );
      }

      if (error instanceof Error) {
        throw new InternalServerErrorException(
          `Không thể gọi LLM: ${error.message}`,
        );
      }

      throw new InternalServerErrorException('Không thể gọi LLM');
    } finally {
      clearTimeout(timeoutId);
    }
  }

  private buildRequestUrl(): string {
    const normalizedBaseUrl = this.apiBaseUrl.endsWith('/')
      ? this.apiBaseUrl.slice(0, -1)
      : this.apiBaseUrl;
    const requestUrl = new URL(
      `${normalizedBaseUrl}/models/${encodeURIComponent(this.model)}:generateContent`,
    );

    requestUrl.searchParams.set('key', this.apiKey);

    return requestUrl.toString();
  }

  private async parseJson(response: Response): Promise<unknown> {
    const rawBody = await response.text();

    if (!rawBody.trim()) {
      return null;
    }

    try {
      return JSON.parse(rawBody) as unknown;
    } catch {
      throw new InternalServerErrorException(
        'LLM trả về dữ liệu không phải JSON hợp lệ',
      );
    }
  }

  private extractAnswer(payload: unknown): string | null {
    if (!payload || typeof payload !== 'object') {
      return null;
    }

    const typedPayload = payload as GeminiGenerateContentPayload;
    const firstCandidate = typedPayload.candidates?.[0];

    return this.normalizeParts(firstCandidate?.content?.parts);
  }

  private extractModel(payload: unknown): string | null {
    if (!payload || typeof payload !== 'object') {
      return null;
    }

    const { modelVersion } = payload as GeminiGenerateContentPayload;

    return typeof modelVersion === 'string' && modelVersion.trim()
      ? modelVersion
      : null;
  }

  private extractProviderError(payload: unknown): string | null {
    if (!payload || typeof payload !== 'object') {
      return null;
    }

    const typedPayload = payload as GeminiGenerateContentPayload;
    const errorMessage = typedPayload.error?.message;

    if (typeof errorMessage === 'string' && errorMessage.trim()) {
      return errorMessage;
    }

    if (
      typeof typedPayload.message === 'string' &&
      typedPayload.message.trim()
    ) {
      return typedPayload.message;
    }

    return null;
  }

  private normalizeParts(parts: unknown): string | null {
    if (!Array.isArray(parts)) {
      return null;
    }

    const normalized = parts
      .map((part) => {
        if (!part || typeof part !== 'object') {
          return '';
        }

        const candidate = part as { text?: unknown };
        return typeof candidate.text === 'string' ? candidate.text : '';
      })
      .join('\n')
      .trim();

    return normalized.length > 0 ? normalized : null;
  }
}
