export interface Authentication {
  username: string;
  password: string;
}

export interface AuthenticationResponse {
  username: string
  jwt: string;
}
