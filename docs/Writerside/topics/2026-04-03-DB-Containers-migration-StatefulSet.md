# 2026-04-03 DB Containers migration StatefulSet

## Why Databases are better as StatefulSet than Deployment

While `Deployment` is great for stateless applications, `StatefulSet` provides several critical benefits for databases:

1.  **Stable Network Identifiers:** Each pod in a `StatefulSet` gets a unique, ordinal index (e.g., `db-0`, `db-1`) that persists across restarts. This is essential for clustering and replication where nodes need to address each other reliably.
2.  **Stable Storage:** Pods are bound to their `PersistentVolumeClaims` (PVCs). If a pod is deleted and recreated, it will always reconnect to the same volume.
3.  **Ordered Deployment and Scaling:** Pods are created and updated in a predictable, sequential order, ensuring controlled state changes.

## Changes in Kubernetes Database Files

The migration involves changing the `kind` of the database resource and the `Service` configuration:

### 1. Change Kind to StatefulSet
The `Deployment` is replaced with `StatefulSet`. In the `spec` section, a `serviceName` is added to link it to the governing service.

### 2. Headless Service
The associated `Service` is changed to a **Headless Service** by setting `clusterIP: None`. This allows DNS to return the IP addresses of individual pods directly rather than a single cluster IP, which is necessary for stable pod addressing.

Example change:
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: authentication-db
spec:
  serviceName: "authentication-db"
  # ... other specs ...
---
apiVersion: v1
kind: Service
metadata:
  name: authentication-db
spec:
  clusterIP: None
  # ... other specs ...
```

## Why Data is Safe

During this migration, your data remains secure for the following reasons:

- **PVC Independence:** Deleting a `Deployment` does not delete the `PersistentVolumeClaim` (PVC). The new `StatefulSet` is configured to use the existing PVC.
- **PV Reclaim Policy:** The `PersistentVolume` (PV) uses `persistentVolumeReclaimPolicy: Retain`, ensuring data on the host disk is never automatically deleted by Kubernetes.
- **Node Affinity:** Since the databases use `local` storage on a specific node, the data remains on that physical disk, and `nodeAffinity` ensures the new `StatefulSet` pods are scheduled on that same node.

## Recommended Migration Steps

To perform the migration with minimal risk:

1.  **Delete the existing Deployment and Service:**
    ```bash
    kubectl delete deployment <db-name> -n microservices
    kubectl delete service <db-name> -n microservices
    ```
    *(Note: Do NOT delete the PVC or PV.)*

2.  **Apply the new StatefulSet configuration:**
    ```bash
    kubectl apply -f <db-name>.yaml
    ```

3.  **Verify the new Pod:**
    ```bash
    kubectl get pods -n microservices -l app=<db-name>
    # You should see <db-name>-0 starting and mounting the existing volume.
    ```
