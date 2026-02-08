# k0s etcd member-list-bug

TODO Docs are following. Currently these are only notes.

```bash
/usr/local/bin/k0s etcd member-list --data-dir=/var/lib/k0s

WARN[2026-02-07 19:45:45] [core] [Channel #1 SubChannel #2] grpc: addrConn.createTransport failed to connect to {Addr: "127.0.0.1:2379", ServerName: "127.0.0.1:2379", }. Err: connection error: desc = "transport: authentication handshake failed: context canceled"  component=grpc
{"members":{"35591.hostserv.eu":"https://185.229.90.248:2380","nbs1":"https://91.99.143.250:2380","nbs2":"https://5.75.129.76:2380"}}
```

```bash
mv /usr/local/bin/k0s /usr/local/bin/k0s-original
```

```bash
nano /usr/local/bin/k0s

#!/bin/sh
# If the command is 'etcd member-list', hide errors (stderr)
if echo "$@" | grep -q "etcd member-list"; then
exec /usr/local/bin/k0s-original "$@" 2>/dev/null
else
exec /usr/local/bin/k0s-original "$@"
fi
```

```bash
chmod +x /usr/local/bin/k0s
```

```bash
/usr/local/bin/k0s etcd member-list --data-dir=/var/lib/k0s

{"members":{"35591.hostserv.eu":"https://185.229.90.248:2380","nbs1":"https://91.99.143.250:2380","nbs2":"https://5.75.129.76:2380"}}
```