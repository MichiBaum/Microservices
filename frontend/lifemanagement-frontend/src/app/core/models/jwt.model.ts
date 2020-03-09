import {Permission} from '../security/permission.enum';

export interface JWT {
  headerName: string;
  token: string;
  expiresAt: number;
  permissions: Permission[];
  username: string;
}
