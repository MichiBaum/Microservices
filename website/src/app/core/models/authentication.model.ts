export interface Authentication {
  username: string;
  password: string;
}

export interface AuthenticationResponse {
  username: string
  jwt: string;
}

export interface Register {
  username: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  state: RegisterState,
  username: String,
  email: String
}
export enum RegisterState {
  SUCCESS,
  ERROR
}
