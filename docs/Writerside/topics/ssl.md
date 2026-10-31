# SSL

An SSL certificate is needed for HTTPS. This project uses **ZeroSSL** as the Certificate Authority (CA), managed via **acme.sh** for account registration and **Lego** for certificate issuance and renewal.

## Why ZeroSSL & Decoupled Certificate Scope?

ZeroSSL is used instead of Let's Encrypt because:
- **No strict rate limits**: Unlike Let's Encrypt, ZeroSSL does not enforce harsh lockout rules for failed validations or duplicate certificates.
- **ACME Support**: ZeroSSL supports the ACME protocol, allowing for free and unlimited wildcard certificates via the API.
- **DNS-01 Validation**: Mandatory for wildcard certificates (`*.domain.ch`).

### Why Decouple Certificates?
Instead of bundling all domains into a single monolithic certificate ("multi-wildcard"), certificates are **decoupled into granular per-domain certificates** (e.g., `michibaum-ch`, `michibaum-com`, etc.).

Decoupling provides several advantages:
1. **Isolated Risk**: A failure or delay during ACME validation for one domain will not block certificate issuance/renewal for other domains.
2. **Eliminates `badNonce` and Timeout Errors**: Large SAN certificates require long polling windows during ACME challenge validation, which frequently leads to expired replay nonces (`400 badNonce`) and DNS resolution timeouts.
3. **Faster Renewal Execution**: Processing individual domain pairs completes in seconds rather than tens of minutes.

## Prerequisites

- Access to the DNS provider API (this project uses **Hosttech**).
- `acme.sh` installed for account management.
- `lego` (v5+) binary installed for certificate issuance.

## 1. Install & Account Registration (acme.sh)

Before issuing certificates, register an account with ZeroSSL using `acme.sh`:

```Bash
curl https://get.acme.sh | sh -s email=YOUR_EMAIL@GMAIL.COM
acme.sh --register-account -m YOUR_EMAIL@GMAIL.COM --server zerossl
```

### Install Lego
```Bash
curl -s https://api.github.com/repos/go-acme/lego/releases/latest | grep "browser_download_url.*linux_amd64.tar.gz" | cut -d '"' -f 4 | wget -qi -
tar -xzf lego_*_linux_amd64.tar.gz
```

## 2. Configuration Files

The configuration files are located in the `./ssl` directory of the project.

### `.lego.yml`
This file defines the CA server, account details, DNS challenge provider, and separate granular certificate definitions for each apex/wildcard domain pair.

File can be found under `./ssl/.lego.yml`:

```yaml
servers:
  zerossl:
    url: https://acme.zerossl.com/v2/DV90

accounts:
  default:
    email: YOUR_EMAIL@GMAIL.COM
    server: zerossl

challenges:
  hosttech-dns:
    dns:
      provider: hosttech
      envFile: .env.hosttech
      resolvers:
        - "ns1.hosttech.eu:53"
        - "ns2.hosttech.eu:53"

certificates:
  michibaum-ch:
    challenge: hosttech-dns
    domains:
      - michibaum.ch
      - "*.michibaum.ch"

  michibaum-com:
    challenge: hosttech-dns
    domains:
      - michibaum.com
      - "*.michibaum.com"

  # ... additional granular domain targets (michibaum-eu, michibaum-org, etc.)
```

### `.env.hosttech`
Contains the API credentials and timeout settings for Hosttech DNS.

File can be found under `./ssl/.env.hosttech`.

## 3. Issuing Decoupled Certificates (Lego)

To issue or renew all decoupled certificates, run Lego from the `ssl` directory:

```Bash
./lego --config .lego.yml --log.level debug
```

Lego will process each certificate target defined under `certificates:` sequentially:
1. Contact ZeroSSL to start challenges for the target domain pair.
2. Use the Hosttech API to create required `_acme-challenge` TXT records.
3. Validate ownership and output distinct `.crt` and `.key` files in `.lego/certificates/`:
   - `michibaum-ch.crt` / `michibaum-ch.key`
   - `michibaum-com.crt` / `michibaum-com.key`
   - ...etc.

To issue or renew a single specific certificate target without touching others:
```Bash
./lego --config .lego.yml --cert-name michibaum-ch --log.level debug
```

## 4. Kubernetes & Traefik Integration

### Creating Granular Kubernetes TLS Secrets

Create a dedicated TLS Secret in Kubernetes for each generated certificate target in the `microservices` namespace:

```bash
# Create individual secrets for each domain certificate
k0s kubectl create secret tls cert-michibaum-ch --cert=/data/ssl/.lego/certificates/michibaum-ch.crt --key=/data/ssl/.lego/certificates/michibaum-ch.key -n microservices
k0s kubectl create secret tls cert-michibaum-com --cert=/data/ssl/.lego/certificates/michibaum-com.crt --key=/data/ssl/.lego/certificates/michibaum-com.key -n microservices
k0s kubectl create secret tls cert-michibaum-eu --cert=/data/ssl/.lego/certificates/michibaum-eu.crt --key=/data/ssl/.lego/certificates/michibaum-eu.key -n microservices
k0s kubectl create secret tls cert-michibaum-org --cert=/data/ssl/.lego/certificates/michibaum-org.crt --key=/data/ssl/.lego/certificates/michibaum-org.key -n microservices
k0s kubectl create secret tls cert-michibaum-me --cert=/data/ssl/.lego/certificates/michibaum-me.crt --key=/data/ssl/.lego/certificates/michibaum-me.key -n microservices
k0s kubectl create secret tls cert-michibaum-net --cert=/data/ssl/.lego/certificates/michibaum-net.crt --key=/data/ssl/.lego/certificates/michibaum-net.key -n microservices
k0s kubectl create secret tls cert-michibaum-info --cert=/data/ssl/.lego/certificates/michibaum-info.crt --key=/data/ssl/.lego/certificates/michibaum-info.key -n microservices
k0s kubectl create secret tls cert-michibaum-xyz --cert=/data/ssl/.lego/certificates/michibaum-xyz.crt --key=/data/ssl/.lego/certificates/michibaum-xyz.key -n microservices
k0s kubectl create secret tls cert-baumberger-software-ch --cert=/data/ssl/.lego/certificates/baumberger-software-ch.crt --key=/data/ssl/.lego/certificates/baumberger-software-ch.key -n microservices
```

### Configuring Traefik Helm Chart Values (`traefik.yaml`)

Traefik uses a `TLSStore` resource to serve TLS certificates dynamically based on Server Name Indication (SNI). In `kubernetes/traefik/traefik.yaml`, list all granular secrets under `tlsStore.default` values:

```yaml
tlsStore:
  default:
    defaultCertificate:
      secretName: cert-michibaum-ch
    certificates:
      - secretName: cert-michibaum-ch
      - secretName: cert-michibaum-com
      - secretName: cert-michibaum-eu
      - secretName: cert-michibaum-org
      - secretName: cert-michibaum-me
      - secretName: cert-michibaum-net
      - secretName: cert-michibaum-info
      - secretName: cert-michibaum-xyz
      - secretName: cert-baumberger-software-ch
```

### Configuring Gateway Ingress (`gateway-service.yaml`)

In `kubernetes/microservices/gateway-service.yaml`, update the `spec.tls` section of the Ingress to map each domain (and wildcard host) directly to its corresponding TLS secret:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: gateway-ingress
  namespace: microservices
  annotations:
    traefik.ingress.kubernetes.io/router.tls: "true"
spec:
  ingressClassName: "traefik"
  tls:
  - hosts:
    - michibaum.ch
    - "*.michibaum.ch"
    secretName: cert-michibaum-ch
  - hosts:
    - michibaum.com
    - "*.michibaum.com"
    secretName: cert-michibaum-com
  - hosts:
    - michibaum.eu
    - "*.michibaum.eu"
    secretName: cert-michibaum-eu
  - hosts:
    - michibaum.org
    - "*.michibaum.org"
    secretName: cert-michibaum-org
  - hosts:
    - michibaum.me
    - "*.michibaum.me"
    secretName: cert-michibaum-me
  - hosts:
    - michibaum.net
    - "*.michibaum.net"
    secretName: cert-michibaum-net
  - hosts:
    - michibaum.info
    - "*.michibaum.info"
    secretName: cert-michibaum-info
  - hosts:
    - michibaum.xyz
    - "*.michibaum.xyz"
    secretName: cert-michibaum-xyz
  - hosts:
    - baumberger-software.ch
    - "*.baumberger-software.ch"
    secretName: cert-baumberger-software-ch
  rules:
    - http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: gateway-service
                port:
                  number: 80
```

## Troubleshooting

### DNS Propagation Timeout
If Lego fails with a "time limit exceeded" error, increase the `HOSTTECH_PROPAGATION_TIMEOUT` in `.env.hosttech`. Hosttech nameservers can sometimes take several minutes to propagate.

### Local Resolver Caching
If you see errors pointing to `127.0.0.53`, it means your local DNS cache is serving stale results. The `.lego.yml` is configured to use public resolvers (`1.1.1.1`) to bypass this. You can also flush your local cache:

```bash
sudo resolvectl flush-caches
```

### Stale TXT Records
If multiple attempts fail, manually log in to the Hosttech DNS editor and delete any leftover `_acme-challenge` TXT records.

