export interface DeploymentDto {
  name: string;
  creationTimestamp?: string;
  containers: string[];
  port?: number;
  nodePort?: number;
}
