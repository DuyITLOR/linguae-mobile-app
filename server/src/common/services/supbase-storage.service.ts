import { Injectable, InternalServerErrorException } from '@nestjs/common';
import { createClient, SupabaseClient } from '@supabase/supabase-js';

interface UploadFileInput {
  bucket: string;
  path: string;
  file: Express.Multer.File;
  upsert?: boolean;
  cacheControl?: string;
}

@Injectable()
export class SupabaseStorageService {
  private readonly client: SupabaseClient;

  constructor() {
    const url = process.env.SUPABASE_URL?.trim();
    const serviceRoleKey = process.env.SUPABASE_SERVICE_ROLE_KEY?.trim();

    if (!url || !serviceRoleKey) {
      throw new Error('Thiếu SUPABASE_URL hoặc SUPABASE_SERVICE_ROLE_KEY');
    }

    this.client = createClient(url, serviceRoleKey, {
      auth: {
        persistSession: false,
        autoRefreshToken: false,
      },
    });
  }

  async uploadFile(input: UploadFileInput): Promise<{ path: string; publicUrl: string }> {
    const { bucket, path, file, upsert = true, cacheControl = '3600' } = input;

    const { error } = await this.client.storage
      .from(bucket)
      .upload(path, file.buffer, {
        contentType: file.mimetype,
        upsert,
        cacheControl,
      });

    if (error) {
      throw new InternalServerErrorException(`Upload file thất bại: ${error.message}`);
    }

    const { data } = this.client.storage.from(bucket).getPublicUrl(path);

    return {
      path,
      publicUrl: `${data.publicUrl}?t=${Date.now()}`,
    };
  }
}
