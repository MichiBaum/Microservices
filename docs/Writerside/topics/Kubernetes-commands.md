# Kubernetes commands

TODO Docs are following. Currently these are only notes.

```bash
k0s kubectl logs -n traefik-system -l app.kubernetes.io/name=traefik --tail=50
```

```bash
k0s kubectl get pods -n traefik-system -o wide
```

```bash
kubectl get charts -n kube-system -w
```

```bash
k0s kubectl delete chart traefik -n kube-system

k0s kubectl get secrets -n traefik-system
k0s kubectl delete secret -n traefik-system -l owner=helm,name=traefik
```

```bash
systemctl restart k0scontroller
```

```bash
k0s kubectl describe chart traefik -n kube-system
```

