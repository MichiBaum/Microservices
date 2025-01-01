export interface Application {
  name: string
  instances: Instance[]
  status: Status
}

export interface Instance {
  id: string
  version: number
  statusInfo: StatusInfo
}

export interface StatusInfo {
  status: Status
  details: any // TODO how
}

export enum Status {
  UP = 'UP',
  DOWN = 'DOWN',
  OUT_OF_SERVICE = 'OUT_OF_SERVICE',
  UNKNOWN = 'UNKNOWN'
}
