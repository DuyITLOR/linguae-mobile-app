# Linguae Mobile App

Linguae là đồ án ứng dụng học tiếng Anh trên Android, hỗ trợ người dùng học từ vựng theo chủ đề, luyện tập bằng flashcard, bài điền từ, bài nối từ, làm bài TOEIC, theo dõi tiến độ học tập và duy trì nhiệm vụ học mỗi ngày.

Project gồm 2 phần chính:

- `client`: ứng dụng Android native viết bằng Kotlin và Jetpack Compose.
- `server`: REST API viết bằng NestJS, dùng Prisma để làm việc với PostgreSQL.

## Chức năng chính

- Đăng ký, đăng nhập và đăng nhập bằng Google.
- Học từ vựng theo chủ đề.
- Tra cứu và lưu từ vựng yêu thích.
- Ôn tập bằng flashcard.
- Luyện tập dạng cloze test và matching.
- Nhiệm vụ học hằng ngày, streak và thống kê tiến độ.
- Làm bài luyện TOEIC.
- Chat hỗ trợ học tập.
- Quản trị nội dung: chủ đề, từ vựng, câu hỏi luyện tập, đề TOEIC và người dùng.
- Cấu hình nhắc nhở học tập và thông báo.

## Công nghệ sử dụng

### Mobile

- Kotlin
- Android SDK
- Jetpack Compose
- Material 3
- Retrofit
- OkHttp
- Coil
- Google Sign-In

### Backend

- Node.js
- NestJS
- TypeScript
- Prisma
- PostgreSQL
- Supabase Storage
- Google AI Studio API
- Gmail OAuth SMTP

## Cấu trúc thư mục

```text
linguae-mobile-app/
├── client/   # Android app
└── server/   # NestJS backend
```

## Yêu cầu cài đặt

- Node.js
- pnpm
- PostgreSQL hoặc database PostgreSQL cloud
- Android Studio
- JDK tương thích với Android Gradle Plugin
- Thiết bị Android thật hoặc Android Emulator

## Cấu hình backend

Tạo file `.env` trong thư mục `server`:

```env
DATABASE_URL="postgresql://USER:PASSWORD@HOST:PORT/DATABASE"
DIRECT_URL="postgresql://USER:PASSWORD@HOST:PORT/DATABASE"

PORT=5050
AUTH_REQUIRED=true
AUTH_TOKEN_SECRET="your-auth-secret"

GOOGLE_CLIENT_ID="your-google-client-id"

MAIL_USER="your-email@gmail.com"
GMAIL_CLIENT_ID="your-gmail-client-id"
GMAIL_CLIENT_SECRET="your-gmail-client-secret"
GMAIL_REFRESH_TOKEN="your-gmail-refresh-token"

SUPABASE_URL="your-supabase-url"
SUPABASE_SERVICE_ROLE_KEY="your-supabase-service-role-key"

GOOGLE_AI_STUDIO_API_KEY="your-google-ai-studio-api-key"
GOOGLE_AI_STUDIO_MODEL="gemini-model-name"
```

Nếu chỉ chạy thử local và muốn bỏ kiểm tra token cho các API cần đăng nhập, có thể đặt:

```env
AUTH_REQUIRED=false
```

## Chạy backend

Di chuyển vào thư mục server:

```bash
cd server
```

Cài dependency:

```bash
pnpm install
```

Generate Prisma Client:

```bash
pnpm prisma generate
```

Đồng bộ schema với database:

```bash
pnpm prisma db push
```

Chạy server ở chế độ development:

```bash
pnpm run dev
```

Mặc định server chạy tại:

```text
http://localhost:5050
```

Kiểm tra server:

```text
GET http://localhost:5050/health
```

## Cấu hình Android app

Tạo hoặc cập nhật file `client/local.properties`:

```properties
sdk.dir=/path/to/Android/Sdk
GOOGLE_CLIENT_ID=your-google-client-id
BASE_URL=http://10.0.2.2:5050/
```

Ghi chú:

- Khi chạy bằng Android Emulator, dùng `http://10.0.2.2:5050/` để trỏ về backend trên máy tính.
- Khi chạy bằng điện thoại thật, đổi `BASE_URL` thành IP LAN của máy đang chạy server, ví dụ `http://192.168.1.10:5050/`.
- `GOOGLE_CLIENT_ID` phải trùng với client id đã cấu hình cho Google Sign-In.

## Chạy Android app

Cách 1: Chạy bằng Android Studio

1. Mở thư mục `client` bằng Android Studio.
2. Chờ Gradle sync hoàn tất.
3. Chọn emulator hoặc thiết bị thật.
4. Nhấn Run.

Cách 2: Chạy bằng terminal

```bash
cd client
./gradlew assembleDebug
```

Sau khi build xong, file APK debug nằm trong:

```text
client/app/build/outputs/apk/debug/
```

## Một số lệnh hữu ích

Backend:

```bash
cd server
pnpm run dev       # chạy development
pnpm run build     # build production
pnpm run lint      # kiểm tra và fix lint
pnpm run test      # chạy test
```

Android:

```bash
cd client
./gradlew assembleDebug      # build APK debug
./gradlew test               # chạy unit test
./gradlew connectedAndroidTest
```

## Tác giả

Đồ án Linguae Mobile App.
