# SSL

An SSL certificate is needed for HTTPS. This project uses **ZeroSSL** as the Certificate Authority (CA), managed via **acme.sh** for account registration and **Lego** for certificate issuance and renewal.

## Why ZeroSSL?

ZeroSSL is used instead of Let's Encrypt because:
- **No strict rate limits**: Unlike Let's Encrypt, ZeroSSL does not enforce harsh lockout rules for failed validations or duplicate certificates.
- **ACME Support**: ZeroSSL supports the ACME protocol, allowing for free and unlimited wildcard certificates via the API.
- **DNS-01 Validation**: Mandatory for wildcard certificates (\*.domain.ch).

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
This file defines the CA server, account details, DNS challenge provider, and the domains to be included in the multi-wildcard certificate.

File can be found under `./ssl/.lego.yml`.

### `.env.hosttech`
Contains the API credentials and timeout settings for Hosttech DNS.

File can be found under `./ssl/.env.hosttech`.

## 3. Issuing Certificates (Lego)

To issue or renew the certificates, run Lego from the `ssl` directory:

```Bash
./lego --config .lego.yml
```

Lego will:
1. Contact ZeroSSL to start the challenge.
2. Use the Hosttech API to add the required `_acme-challenge` TXT records.
3. Wait for DNS propagation (using the configured resolvers like `1.1.1.1`).
4. Validate ownership and download the certificates.

The generated certificates will be stored in `.lego/certificates/`.

## 4. Kubernetes Integration

Once the certificates are generated, they can be imported into Kubernetes as a Secret:

```bash
kubectl create secret tls michibaum-tls \
  --cert=/data/ssl/.lego/certificates/multi-wildcard.pem \
  --key=/data/ssl/.lego/certificates/multi-wildcard.key \
  -n microservices
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

