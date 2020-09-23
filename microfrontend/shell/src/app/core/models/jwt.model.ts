import {PermissionEnum} from './enum/permission.enum';

export interface IJWT {
  headerName: string;
  token: string;
  expiresAt: number;
  permissions: PermissionEnum[];
  username: string;
}
