export interface IValidationError {
  error: IError;
  headers: any;
  message: string;
  name: string;
  ok: boolean;
  status: number;
  statusText: string;
  url: string;
}

export interface IError {
  details: string;
  exceptionClass: string;
  message: string;
  timestamp: number;
  validationErrors: string[];
}
