# Install K3s on Ubuntu

This document provides step-by-step instructions for installing K3s (a lightweight Kubernetes distribution) on Ubuntu 22.04 LTS, setting up a server, and joining agent nodes to the cluster.

## Prerequisites

Before you begin, ensure that:

- You have at least one Ubuntu 22.04 LTS server (for a single-node setup) or multiple servers (for a multi-node cluster)
- Each server has at least 1 CPU, 1GB RAM, and 10GB disk space (K3s is much lighter than full Kubernetes)
- All servers have static IP addresses and can communicate with each other
- You have sudo or root access to all servers
- All servers have unique hostnames

## System Preparation (All Nodes)

Perform these steps on all nodes (server and agents).

### Update the System

```bash
sudo apt update
sudo apt upgrade -y
```

### Install Required Packages

```bash
sudo apt install -y curl openssh-server
```

## Install K3s Server (Control Plane)

Perform these steps on the server node that will act as the control plane.

### Install K3s Server

The simplest way to install K3s is to use the installation script:

```bash
curl -sfL https://get.k3s.io | sh -
```

This command downloads and runs the K3s installation script with default settings. The script installs K3s as a service and starts it automatically.

### Verify the Installation

Check that K3s is running:

```bash
sudo systemctl status k3s
```

Verify that the node is ready:

```bash
sudo kubectl get nodes
```

The output should show your server node with status `Ready`.

### Get the Node Token

To join agent nodes to the cluster, you'll need the node token:

```bash
sudo cat /var/lib/rancher/k3s/server/node-token
```

Save this token as you'll need it to join agent nodes to the cluster.

## Join Agent Nodes to the Cluster (Optional)

Perform these steps on each agent node you want to add to the cluster.

### Install K3s Agent

Use the installation script with the K3S_URL and K3S_TOKEN environment variables:

```bash
curl -sfL https://get.k3s.io | K3S_URL=https://<server-ip>:6443 K3S_TOKEN=<node-token> sh -
```

Replace `<server-ip>` with the IP address of your K3s server and `<node-token>` with the token you retrieved in the previous step.

### Verify the Agent Node

On the server node, check that the agent node has joined the cluster:

```bash
sudo kubectl get nodes
```

All nodes should show as `Ready`.

## Using kubectl

K3s comes with its own version of kubectl that is installed as `kubectl`. You can use it directly:

```bash
sudo kubectl get pods --all-namespaces
```

### Configure kubectl for Regular User

To use kubectl as a regular user:

1. Create the kubectl config directory:

```bash
mkdir -p ~/.kube
```

2. Copy the K3s config file:

```bash
sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config
```

3. Update the server address if needed:

```bash
sed -i 's/127.0.0.1/<server-ip>/g' ~/.kube/config
```

4. Now you can use kubectl without sudo:

```bash
kubectl get nodes
```

## Configure kubectl on Your Local Machine

If you want to manage the K3s cluster from your local machine:

1. Install kubectl on your local machine

Installation guide can be found on [kubernetes.io](https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/)

2. Copy the kubeconfig file from the server node:

```bash
scp username@server-ip:~/.kube/config ~/.kube/config
```

3. Update the server address in the kubeconfig file:

```bash
sed -i 's/127.0.0.1/<server-ip>/g' ~/.kube/config
```

## Additional Configuration

## Troubleshooting

### Node Not Ready

If a node shows as `NotReady`:

1. Check K3s service status:

```bash
sudo systemctl status k3s    # On server node
sudo systemctl status k3s-agent    # On agent nodes
```

2. View K3s logs:

```bash
sudo journalctl -u k3s    # On server node
sudo journalctl -u k3s-agent    # On agent nodes
```

### Reset a Node

If you need to reset a node:

1. Uninstall K3s:

```bash
# On server node
/usr/local/bin/k3s-uninstall.sh

# On agent node
/usr/local/bin/k3s-agent-uninstall.sh
```

2. Then follow the installation procedure again.

## Upgrading K3s

To upgrade K3s to a newer version:

1. On the server node:

```bash
curl -sfL https://get.k3s.io | sh -
```

2. On agent nodes:

```bash
curl -sfL https://get.k3s.io | K3S_URL=https://<server-ip>:6443 K3S_TOKEN=<node-token> sh -
```

The installation script will detect the existing installation and upgrade it.

## Disabling Components (Optional)

K3s comes with several built-in components that you can disable if not needed:

```bash
curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC="--disable traefik" sh -
```

Available options include:
- `--disable traefik`: Disable the built-in Traefik ingress controller
- `--disable servicelb`: Disable the built-in ServiceLB load balancer
- `--disable metrics-server`: Disable the built-in metrics server

## Conclusion

You now have a functioning K3s cluster. K3s is a lightweight Kubernetes distribution that is perfect for edge computing, IoT, CI, development, and ARM. It's packaged as a single binary and has minimal resource requirements.

For more information on deploying applications to your K3s cluster, refer to the [Deploy on K3s](Deploy-K3s.md) documentation.
