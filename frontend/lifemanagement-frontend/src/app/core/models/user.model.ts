import {IPermission} from './permission.model';

export interface IUser {
  id: number;
  name: string;
  emailAddress: string;
  enabled: boolean;
  lastLogin: number;
  permissions: IPermission[];
}

export interface IExportUser {
  id: number;
  name: string;
  emailAddress: string;
  password: string;
  enabled: boolean;
  lastLogin: number;
  permissions: number[];
}
