export interface ILog {
  id: number;
  date: number;
  formattedMessage: string;
  loggerName: string;
  level: string;
  threadName: string;
  arg0: string;
  arg1: string;
  arg2: string;
  arg3: string;
  callerFilename: string;
  callerClass: string;
  callerMethod: string;
  callerLine: string;
  seen: boolean;
}

export interface IExportLog {
  id?: number;
  date: number;
  formattedMessage: string;
  loggerName: string;
  level: string;
  threadName?: string;
  arg0?: string;
  arg1?: string;
  arg2?: string;
  arg3?: string;
  callerFilename: string;
  callerClass: string;
  callerMethod: string;
  callerLine: string;
  seen?: boolean;
}
