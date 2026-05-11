import { IsIn, IsNotEmpty } from 'class-validator';

export class UpdateUserRoleDto {
  @IsNotEmpty()
  @IsIn(['USER', 'ADMIN'])
  role: 'USER' | 'ADMIN';
}
