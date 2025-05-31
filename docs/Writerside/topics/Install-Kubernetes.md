# Install Kubernetes on Ubuntu

This document provides step-by-step instructions for installing Kubernetes on Ubuntu 22.04 LTS, setting up a cluster, and joining worker nodes to the cluster.

## Prerequisites

Before you begin, ensure that:

- You have at least two Ubuntu 22.04 LTS servers (one for the control plane and at least one for worker nodes)
- Each server has at least 2 CPUs, 2GB RAM, and 20GB disk space
- All servers have static IP addresses and can communicate with each other
- You have sudo or root access to all servers
- All servers have unique hostnames

## System Preparation (All Nodes)

Perform these steps on all nodes (control plane and workers).

### Update the System

```bash
sudo apt update
sudo apt upgrade -y
```

### Disable Swap

Kubernetes requires swap to be disabled:

```bash
sudo swapoff -a
```

To permanently disable swap, edit the `/etc/fstab` file and comment out any swap lines:

```bash
sudo sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab
```

### Install Container Runtime (containerd)

1. Install required packages:

```bash
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common gnupg2
```

2. Add Docker's official GPG key:

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
```

3. Add Docker repository:

```bash
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

4. Install containerd:

```bash
sudo apt update
sudo apt install -y containerd.io
```

5. Configure containerd to use systemd as cgroup driver:

```bash
sudo mkdir -p /etc/containerd
containerd config default | sudo tee /etc/containerd/config.toml > /dev/null
sudo sed -i 's/SystemdCgroup \= false/SystemdCgroup \= true/g' /etc/containerd/config.toml
```

6. Restart containerd:

```bash
sudo systemctl restart containerd
sudo systemctl enable containerd
```

### Configure Kernel Modules and sysctl Parameters

1. Load required kernel modules:

```bash
cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
overlay
br_netfilter
EOF

sudo modprobe overlay
sudo modprobe br_netfilter
```

2. Set required sysctl parameters:

```bash
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-iptables  = 1
net.bridge.bridge-nf-call-ip6tables = 1
net.ipv4.ip_forward                 = 1
EOF

sudo sysctl --system
```

## Install Kubernetes Components (All Nodes)

Perform these steps on all nodes (control plane and workers).

1. Add Kubernetes GPG key:

```bash
sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg
```

2. Add Kubernetes repository:

```bash
echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
```

3. Install Kubernetes components:

```bash
sudo apt update
sudo apt install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl
```

## Initialize the Control Plane Node

Perform these steps only on the control plane node.

1. Initialize the Kubernetes cluster:

```bash
sudo kubeadm init --pod-network-cidr=10.244.0.0/16
```

2. Set up kubeconfig for the current user:

```bash
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

3. Install a pod network add-on (Calico):

```bash
kubectl apply -f https://docs.projectcalico.org/manifests/calico.yaml
```

4. Verify the control plane node is ready:

```bash
kubectl get nodes
```

5. Get the join command for worker nodes:

```bash
kubeadm token create --print-join-command
```

This command will output a join command that looks like:

```
kubeadm join 192.168.1.100:6443 --token abcdef.0123456789abcdef --discovery-token-ca-cert-hash sha256:1234...cdef
```

Save this command as you'll need it to join worker nodes to the cluster.

## Join Worker Nodes to the Cluster

Perform these steps on each worker node.

1. Run the join command that was output from the control plane node:

```bash
sudo kubeadm join <control-plane-ip>:6443 --token <token> --discovery-token-ca-cert-hash sha256:<hash>
```

Replace `<control-plane-ip>`, `<token>`, and `<hash>` with the values from the join command.

2. On the control plane node, verify that the worker node has joined the cluster:

```bash
kubectl get nodes
```

## Verify the Cluster

On the control plane node, run the following commands to verify that the cluster is working properly:

1. Check node status:

```bash
kubectl get nodes
```

All nodes should show as `Ready`.

2. Check system pods:

```bash
kubectl get pods -n kube-system
```

All pods should be in the `Running` state.

## Additional Configuration

### Configure kubectl on Your Local Machine

If you want to manage the Kubernetes cluster from your local machine:

1. Install kubectl on your local machine

Installation guide can be found on [kubernetes.io](https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/)

2. Copy the kubeconfig file from the control plane node:

```bash
scp username@control-plane-ip:~/.kube/config ~/.kube/config
```

3. Update the server address in the kubeconfig file if needed:

```bash
sed -i 's/127.0.0.1/<control-plane-ip>/g' ~/.kube/config
```

### Install Kubernetes Dashboard (Optional)

1. Deploy the dashboard:

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
```

2. Create an admin user and role binding:

```bash
cat <<EOF | kubectl apply -f -
apiVersion: v1
kind: ServiceAccount
metadata:
  name: admin-user
  namespace: kubernetes-dashboard
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: admin-user
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
- kind: ServiceAccount
  name: admin-user
  namespace: kubernetes-dashboard
EOF
```

3. Get the token for the admin user:

```bash
kubectl -n kubernetes-dashboard create token admin-user
```

4. Start the kubectl proxy:

```bash
kubectl proxy
```

5. Access the dashboard at:
   http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/

## Troubleshooting

### Node Not Ready

If a node shows as `NotReady`:

1. Check kubelet status:

```bash
sudo systemctl status kubelet
```

2. View kubelet logs:

```bash
sudo journalctl -u kubelet
```

### Pod Network Issues

If pods can't communicate:

1. Check Calico pods:

```bash
kubectl get pods -n kube-system | grep calico
```

2. Check Calico logs:

```bash
kubectl logs -n kube-system calico-node-xxxxx
```

### Reset a Node

If you need to reset a node and rejoin it to the cluster:

```bash
sudo kubeadm reset
```

Then follow the join procedure again.

## Upgrading Kubernetes

To upgrade Kubernetes to a newer version:

1. On the control plane node, check available versions:

```bash
apt update
apt-cache madison kubeadm
```

2. Upgrade kubeadm:

```bash
sudo apt-mark unhold kubeadm
sudo apt update
sudo apt install -y kubeadm=<version>
sudo apt-mark hold kubeadm
```

3. Plan the upgrade:

```bash
sudo kubeadm upgrade plan
```

4. Apply the upgrade:

```bash
sudo kubeadm upgrade apply v<version>
```

5. Upgrade kubelet and kubectl:

```bash
sudo apt-mark unhold kubelet kubectl
sudo apt update
sudo apt install -y kubelet=<version> kubectl=<version>
sudo apt-mark hold kubelet kubectl
```

6. Restart kubelet:

```bash
sudo systemctl daemon-reload
sudo systemctl restart kubelet
```

7. Repeat steps 2, 5, and 6 on worker nodes (using `kubeadm upgrade node` instead of `kubeadm upgrade apply`).

## Conclusion

You now have a functioning Kubernetes cluster with a control plane node and worker nodes. You can deploy applications to this cluster using kubectl commands or by applying YAML manifests.

For more information on deploying applications to your Kubernetes cluster, refer to the [Deploy Kubernetes](Deploy-Kubernetes.md) documentation.
