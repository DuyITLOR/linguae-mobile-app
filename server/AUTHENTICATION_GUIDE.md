# Authentication & API Guard Setup Guide

## Tổng quan

Dự án sử dụng **2 layer authentication**:

1. **x-api-key** - Xác minh app/service gọi API
2. **Bearer Token** - Xác minh user

Cả 2 đều được kiểm tra trong `ApiKeyGuard`.

---

## 1. Tệp cấu hình

File `.env` cần các biến sau:

```env
PORT=5050

# Enable/disable authentication
AUTH_REQUIRED="true"

# Layer 1: App identification
INTERNAL_API_KEY="dev-local-key"

# Layer 2: User token signing
AUTH_TOKEN_SECRET="replace-with-a-strong-secret"

# Database
DATABASE_URL="..."
DIRECT_URL="..."
```

---

## 2. Cách gắn Guard vào Endpoint

### Option 1: Gắn vào endpoint cụ thể

```typescript
import { Controller, Get, Param, UseGuards } from '@nestjs/common';
import { ApiKeyGuard } from 'src/common/guards/api-key.guard';

@Controller('users')
export class UsersController {
  @UseGuards(ApiKeyGuard)
  @Get(':id')
  getUser(@Param('id') id: string) {
    return { id, name: 'John' };
  }
}
```

### Option 2: Gắn vào tất cả endpoint trong controller

```typescript
import { Controller, Get, UseGuards } from '@nestjs/common';
import { ApiKeyGuard } from 'src/common/guards/api-key.guard';

@UseGuards(ApiKeyGuard)
@Controller('users')
export class UsersController {
  @Get(':id')
  getUser(@Param('id') id: string) {
    return { id, name: 'John' };
  }

  @Get()
  getAllUsers() {
    return [];
  }
}
```

### Option 3: Bỏ qua authentication cho endpoint PUBLIC

```typescript
import { Controller, Get, UseGuards } from '@nestjs/common';
import { Public } from 'src/common/decorators/public.decorator';
import { ApiKeyGuard } from 'src/common/guards/api-key.guard';

@UseGuards(ApiKeyGuard)
@Controller('users')
export class UsersController {
  @Public() // ← Bỏ qua authentication
  @Get('public-info')
  getPublicInfo() {
    return { info: 'public data' };
  }

  @Get(':id') // ← Cần authentication
  getUser(@Param('id') id: string) {
    return { id };
  }
}
```

---

## 3. Luồng xác thực Request

```
Request tới endpoint
    ↓
[1] Kiểm tra @Public() decorator?
    - Có → PASS ✅
    - Không → tiếp tục
    ↓
[2] AUTH_REQUIRED = false?
    - Có → PASS ✅
    - Không → tiếp tục
    ↓
[3] Kiểm tra x-api-key header
    - INTERNAL_API_KEY được config?
      - Không → bỏ qua
      - Có → kiểm tra x-api-key == INTERNAL_API_KEY?
        - Không match → ❌ THROW ERROR
        - Match → tiếp tục
    ↓
[4] Kiểm tra Authorization header
    - Phải bắt đầu với "Bearer "
    - Extract token từ vị trí sau "Bearer "
    - Token rỗng? → ❌ THROW ERROR
    ↓
[5] PASS ✅
```

---

## 4. File .rest - Hướng dẫn

### Setup VS Code Extension

Cài đặt extension **REST Client** từ Marketplace:

- Tìm kiếm: "REST Client" by Huachao Mao
- Bấm Install

### Cách viết .rest file

**File: test/auth.rest**

```rest
### Variables
@baseUrl = http://localhost:5050
@apiKey = dev-local-key
@token = eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMTIzIiwibmFtZSI6IkpvaG4iLCJpYXQiOjE2NDcwODAwMDB9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ

### 1. GET user (với API key + Bearer token)
GET {{baseUrl}}/users/123
x-api-key: {{apiKey}}
Authorization: Bearer {{token}}

### 2. GET user (chỉ dùng Authorization)
GET {{baseUrl}}/users/456
Authorization: Bearer {{token}}

### 3. POST request - Tạo user mới
POST {{baseUrl}}/users
Content-Type: application/json
x-api-key: {{apiKey}}
Authorization: Bearer {{token}}

{
  "name": "John Doe",
  "email": "john@example.com"
}

### 4. PUT request - Cập nhật user
PUT {{baseUrl}}/users/123
Content-Type: application/json
x-api-key: {{apiKey}}
Authorization: Bearer {{token}}

{
  "name": "Jane Doe",
  "email": "jane@example.com"
}

### 5. DELETE request
DELETE {{baseUrl}}/users/123
x-api-key: {{apiKey}}
Authorization: Bearer {{token}}

### 6. Public endpoint (không cần token)
GET {{baseUrl}}/public/info
x-api-key: {{apiKey}}
```

---

## 5. Cách chạy .rest file

1. **Mở file .rest** (ví dụ: `test/auth.rest`)
2. **Bấm "Send Request"** trên từng request
   - Hoặc bấm Ctrl+Alt+R (⌘+⌥+R trên Mac)
3. **Xem response** ở tab bên phải

---

## 6. Error Messages

| Error                                     | Nguyên nhân                      | Giải pháp                              |
| ----------------------------------------- | -------------------------------- | -------------------------------------- |
| `Invalid or missing x-api-key header`     | x-api-key sai/thiếu              | Thêm header `x-api-key: dev-local-key` |
| `Missing or invalid Authorization header` | Thiếu/sai định dạng Bearer token | Thêm `Authorization: Bearer {TOKEN}`   |
| `Bearer token is empty`                   | Token rỗng                       | Kiểm tra token có đúng không           |
| `Unauthorized`                            | Token không hợp lệ               | Verify lại token                       |

---

## 7. Cách tạo Bearer Token (JWT)

```bash
# Dùng online JWT generator: https://jwt.io/

# Hoặc dùng Node.js:
node -e "
const jwt = require('jsonwebtoken');
const token = jwt.sign(
  { sub: 'user123', name: 'John' },
  'your-secret-key',
  { expiresIn: '1h' }
);
console.log(token);
"
```

---

## 8. Flow cơ bản

```
Mobile App
  ↓ (gửi request)
POST /api/users
x-api-key: dev-local-key
Authorization: Bearer eyJhb...
  ↓
[ApiKeyGuard]
  ✓ Kiểm tra x-api-key
  ✓ Kiểm tra Bearer token
  ↓
[Controller]
  → Xử lý request
  → Trả về response
```

---

## 9. Disable authentication (Development)

Nếu muốn test endpoint mà không cần token:

**Function 1: Set AUTH_REQUIRED=false trong .env**

```env
AUTH_REQUIRED="false"
```

**Function 2: Thêm @Public() decorator**

```typescript
@Public()
@Get('test')
testEndpoint() {
  return { test: 'ok' };
}
```

---

## 10. Ví dụ real-world

**File: src/modules/auth/auth.controller.ts**

```typescript
import { Controller, Post, Body, UseGuards } from '@nestjs/common';
import { ApiKeyGuard } from 'src/common/guards/api-key.guard';
import { Public } from 'src/common/decorators/public.decorator';

@Controller('auth')
export class AuthController {
  // Public endpoint - không cần token
  @Public()
  @Post('login')
  login(@Body() credentials: { email: string; password: string }) {
    return { token: 'eyJhb...' };
  }

  // Protected endpoint - cần API key + Bearer token
  @UseGuards(ApiKeyGuard)
  @Post('logout')
  logout() {
    return { message: 'Logged out' };
  }

  // Protected endpoint - tất cả endpoint trong controller
  @UseGuards(ApiKeyGuard)
  @Post('refresh-token')
  refreshToken() {
    return { token: 'new-token' };
  }
}
```

**File: test/auth.rest**

```rest
@baseUrl = http://localhost:5050
@apiKey = dev-local-key
@token = eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

### Public - Không cần token
POST {{baseUrl}}/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

### Protected - Cần token
POST {{baseUrl}}/auth/logout
x-api-key: {{apiKey}}
Authorization: Bearer {{token}}

### Protected - Cần token
POST {{baseUrl}}/auth/refresh-token
x-api-key: {{apiKey}}
Authorization: Bearer {{token}}
```

---

## Tóm tắt

| Yếu tố                    | Chi tiết                                         |
| ------------------------- | ------------------------------------------------ |
| **Guard vào endpoint**    | `@UseGuards(ApiKeyGuard)`                        |
| **Guard vào controller**  | `@UseGuards(ApiKeyGuard) @Controller(...)`       |
| **Bỏ qua authentication** | `@Public()`                                      |
| **Headers trong .rest**   | `x-api-key` và `Authorization: Bearer {token}`   |
| **Chạy .rest**            | Cài REST Client extension → Click "Send Request" |
