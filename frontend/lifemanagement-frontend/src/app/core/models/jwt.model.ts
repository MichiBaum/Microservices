import {PermissionEnum} from './enum/permission.enum';

export interface JWT {
  headerName: string;
  token: string;
  expiresAt: number;
  permissions: PermissionEnum[];
  username: string;
}
