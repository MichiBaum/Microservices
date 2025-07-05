# k0s and k0sctl

This document provides information about k0s, a lightweight Kubernetes distribution, and k0sctl, a tool for managing k0s clusters.

## Overview

k0s is a complete Kubernetes distribution packaged as a single binary, designed to be simple to install and operate.

k0sctl is a command-line tool that helps deploy and manage k0s clusters across multiple nodes. It uses SSH to connect to the target hosts and automates the installation and configuration of k0s.

## Prerequisites

Before you begin, ensure that:

- You have at least one server for a single-node setup or multiple servers for a multi-node cluster
- Each server has at least 1 CPU, 1GB RAM, and 10GB disk space
- All servers have static IP addresses and can communicate with each other
- You have SSH access to all servers
- All servers have unique hostnames

## k0sctl Configuration

The k0sctl tool uses a YAML configuration file to define the cluster. 

### Configuration Explanation

The configuration file defines:

1. **Cluster Metadata**: The name of the cluster (`microservice-cluster`).

2. **Hosts**: The servers that will form the cluster:
   - A controller node at 5.75.129.76
   - Two controller+worker nodes at 185.229.90.248 and 91.99.143.250
   - Each host has SSH connection details including address, user, port, and SSH key path

3. **k0s Configuration**:
   - Version: v1.33.2+k0s.0
   - Network configuration with node local load balancing enabled using EnvoyProxy
   - Storage type set to etcd
   - Telemetry enabled

4. **Options**:
   - Wait for operations to complete
   - Drain settings for node maintenance
   - Concurrency settings for parallel operations
   - Eviction taint configuration


## Deploying a k0s Cluster with k0sctl

1. Create a k0sctl configuration file (as shown above) and save it as `k0sctl.yaml`.

2. Apply the configuration to create or update the cluster:

```bash
k0sctl apply --config k0sctl.yaml
```

3. Verify the cluster status:

```bash
kubectl get nodes
kubectl get pods -A
```

## Accessing the Cluster

After deploying the cluster, k0sctl can generate a kubeconfig file for accessing it:

```bash
k0sctl kubeconfig --config k0sctl.yaml > ~/.kube/microservices-config
```

Now you can use kubectl to interact with your cluster:

```bash
kubectl get nodes
kubectl get pods -A
```

## Upgrading the Cluster

To upgrade a k0s cluster:

1. Update the version in your k0sctl.yaml file:

```yaml
k0s:
  version: v1.33.3+k0s.0  # New version
```

2. Apply the updated configuration:

```bash
k0sctl apply --config k0sctl.yaml
```

## Resetting the Cluster

If you need to reset the cluster:

```bash
k0sctl reset --config k0sctl.yaml
```

This will uninstall k0s from all hosts defined in the configuration.

## Troubleshooting

### Connection Issues

If you encounter SSH connection issues:
- Verify that the SSH key paths are correct
- Ensure the target hosts are reachable
- Check that the SSH user has sufficient permissions

### Cluster Status Issues

If nodes are not joining the cluster:
- Check the k0s logs on the problematic nodes:
  ```bash
  ssh user@node-address "sudo journalctl -u k0scontroller.service"
  ```
  or
  ```bash
  ssh user@node-address "sudo journalctl -u k0sworker.service"
  ```

## Conclusion

k0s with k0sctl provides a lightweight and easy-to-manage Kubernetes distribution for running the microservices application. The configuration used in this project sets up a three-node cluster with high availability (multiple controllers) and worker nodes for running workloads.

For more information on deploying applications to your k0s cluster, refer to the Kubernetes documentation or the k0s project documentation at [k0sproject.io](https://k0sproject.io/).