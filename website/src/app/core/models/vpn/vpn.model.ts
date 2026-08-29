export interface DeploymentDto {
  name: string;
  namespace: string;
  replicas?: number;
  readyReplicas?: number;
  creationTimestamp?: string;
  containers: string[];
}
