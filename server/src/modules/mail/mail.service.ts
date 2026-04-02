import { Injectable, InternalServerErrorException } from '@nestjs/common';
import nodemailer, { Transporter } from 'nodemailer';

@Injectable()
export class MailService {
  private transporter: Transporter | null = null;

  private getTransporter(): Transporter {
    if (this.transporter) {
      return this.transporter;
    }

    const user = process.env.MAIL_USER?.trim();
    const clientId = process.env.GMAIL_CLIENT_ID?.trim();
    const clientSecret = process.env.GMAIL_CLIENT_SECRET?.trim();
    const refreshToken = process.env.GMAIL_REFRESH_TOKEN?.trim();

    if (!user || !clientId || !clientSecret || !refreshToken) {
      throw new InternalServerErrorException(
        'Missing Gmail OAuth2 configuration for forgot password email',
      );
    }

    this.transporter = nodemailer.createTransport({
      service: 'gmail',
      auth: {
        type: 'OAuth2',
        user,
        clientId,
        clientSecret,
        refreshToken,
      },
    });

    return this.transporter;
  }

  async sendResetPasswordEmail(input: {
    email: string;
    fullName: string;
    otp: string;
  }): Promise<void> {
    const from = process.env.SMTP_FROM?.trim() || process.env.MAIL_USER?.trim();

    if (!from) {
      throw new InternalServerErrorException(
        'Missing SMTP_FROM or MAIL_USER for forgot password email',
      );
    }

    const transporter = this.getTransporter();

    await transporter.sendMail({
      from,
      to: input.email,
      subject: 'Yêu cầu đặt lại mật khẩu',
      text: [
        `Xin chào ${input.fullName},`,
        '',
        'Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản Linguae của bạn.',
        `Mã OTP đặt lại mật khẩu của bạn là: ${input.otp}`,
        '',
        'Mã OTP này sẽ hết hạn sau 10 phút.',
        'Nếu bạn không yêu cầu đổi mật khẩu, hãy bỏ qua email này.',
      ].join('\n'),
      html: `
        <div style="margin: 0; padding: 24px; background: #f4f7fb; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1f2937;">
          <div style="max-width: 560px; margin: 0 auto; background: #ffffff; border-radius: 20px; overflow: hidden; box-shadow: 0 12px 40px rgba(15, 23, 42, 0.08); border: 1px solid #e5e7eb;">
            <div style="padding: 28px 32px; background: linear-gradient(135deg, #1d4ed8, #2563eb 55%, #60a5fa); color: #ffffff;">
              <div style="font-size: 13px; letter-spacing: 0.08em; text-transform: uppercase; opacity: 0.9;">Linguae</div>
              <h1 style="margin: 12px 0 0; font-size: 28px; line-height: 1.25; font-weight: 700;">Đặt lại mật khẩu</h1>
              <p style="margin: 12px 0 0; font-size: 15px; line-height: 1.6; opacity: 0.95;">
                Sử dụng mã OTP bên dưới để tiếp tục đổi mật khẩu cho tài khoản của bạn.
              </p>
            </div>

            <div style="padding: 32px;">
              <p style="margin: 0 0 16px; font-size: 16px; line-height: 1.7;">
                Xin chào <strong>${this.escapeHtml(input.fullName)}</strong>,
              </p>

              <p style="margin: 0 0 20px; font-size: 15px; line-height: 1.7; color: #4b5563;">
                Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản Linguae của bạn.
                Vui lòng nhập mã OTP dưới đây vào ứng dụng để tiếp tục.
              </p>

              <div style="margin: 24px 0; padding: 24px; border-radius: 18px; background: linear-gradient(180deg, #eff6ff 0%, #dbeafe 100%); border: 1px solid #bfdbfe; text-align: center;">
                <div style="font-size: 13px; color: #1d4ed8; text-transform: uppercase; letter-spacing: 0.12em; font-weight: 700;">
                  Mã OTP của bạn
                </div>
                <div style="margin-top: 12px; font-size: 34px; line-height: 1; font-weight: 800; letter-spacing: 0.22em; color: #0f172a;">
                  ${this.escapeHtml(input.otp)}
                </div>
              </div>

              <div style="margin: 0 0 20px; padding: 16px 18px; border-radius: 14px; background: #fff7ed; border: 1px solid #fed7aa; color: #9a3412; font-size: 14px; line-height: 1.6;">
                Mã OTP này sẽ hết hạn sau <strong>10 phút</strong>.
              </div>

              <p style="margin: 0 0 12px; font-size: 14px; line-height: 1.7; color: #6b7280;">
                Nếu bạn không yêu cầu đổi mật khẩu, bạn có thể bỏ qua email này. Vì lý do bảo mật,
                vui lòng không chia sẻ mã OTP với bất kỳ ai.
              </p>

              <p style="margin: 24px 0 0; font-size: 14px; line-height: 1.7; color: #6b7280;">
                Trân trọng,<br />
                <strong style="color: #111827;">Đội ngũ Linguae</strong>
              </p>
            </div>
          </div>
        </div>
      `,
    });
  }

  private escapeHtml(value: string): string {
    return value
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#39;');
  }
}
