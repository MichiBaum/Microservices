import {Permission} from './permission.model';

export interface User {
  id: number;
  name: string;
  emailAddress: string;
  enabled: boolean;
  lastLogin: number;
  permissions: Permission[];
}

export interface ExportUser {
  id: number;
  name: string;
  emailAddress: string;
  password: string;
  enabled: boolean;
  lastLogin: number;
  permissions: number[];
}
