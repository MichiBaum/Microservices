export enum Status {
  UP = 'UP',
  DOWN = 'DOWN',
  OUT_OF_SERVICE = 'OUT_OF_SERVICE',
  UNKNOWN = 'UNKNOWN'
}

export interface Application {
  name: string
  buildVersion: string | null;
  status: Status
  statusTimestamp: string;
  instances: Instance[];
}

export interface Instance {
  id: string;
  version: number;
  registration: Registration;
  registered: boolean;
  statusInfo: StatusInfo;
  statusTimestamp: string;
  info: Info;
  endpoints: Endpoint[];
  buildVersion: string | null;
  tags: Record<string, unknown>;
}

export interface Registration {
  name: string;
  managementUrl: string;
  healthUrl: string;
  serviceUrl: string;
  source: string;
  metadata: Record<string, string>;
}

export interface StatusInfo {
  status: Status;
  details: Record<string, StatusDetail | undefined>;
}

export interface StatusDetail {
  status: Status;
  description?: string;
  details?: Record<string, unknown>;
  components?: Record<string, StatusDetail>;
}

export interface Info {
  java?: JavaInfo;
  os?: OSInfo;
  process?: ProcessInfo;
  ssl?: SSLInfo;
}

export interface JavaInfo {
  version: string;
  vendor: VendorInfo;
  runtime: RuntimeInfo;
  jvm: JVMInfo;
}

export interface VendorInfo {
  name: string;
}

export interface RuntimeInfo {
  name: string;
  version: string;
}

export interface JVMInfo {
  name: string;
  vendor: string;
  version: string;
}

export interface OSInfo {
  name: string;
  version: string;
  arch: string;
}

export interface ProcessInfo {
  pid: number;
  parentPid: number;
  owner: string;
  cpus: number;
  memory: MemoryInfo;
}

export interface MemoryInfo {
  heap: HeapInfo;
  nonHeap: HeapInfo;
}

export interface HeapInfo {
  max: number;
  committed: number;
  used: number;
  init: number;
}

export interface SSLInfo {
  bundles: unknown[];
}

export interface Endpoint {
  id: string;
  url: string;
}
