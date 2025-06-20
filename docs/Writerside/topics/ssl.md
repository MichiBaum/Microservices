# SSL

An SSL certificate is needed for https.
This SSL certificate is created with letsencrypt and certbot.

## Create certificates
Because of wildcard certificate you need to verify ownership of the Domain. This is done with adding a TXT record to the DNS entry.

### Automated DNS verification with hosttech API
The repository includes scripts to automate the DNS verification process using the hosttech API. These scripts will automatically add and remove the required TXT records for domain verification.

#### Setup
1. Make sure the scripts are executable:
   ```Bash
   chmod +x scripts/certbot-dns-hosttech-*.sh
   ```

2. Edit the configuration file to add your hosttech API token:
   ```Bash
   nano scripts/hosttech-config.sh
   ```

   Replace `YOUR_HOSTTECH_API_TOKEN` with your actual API token from hosttech.

3. You can also adjust the DNS propagation wait time (default is 60 seconds) if needed.

#### How it works
- The `certbot-dns-hosttech-auth.sh` script is called by certbot to add the TXT record for verification
- The `certbot-dns-hosttech-cleanup.sh` script is called by certbot to remove the TXT record after verification
- Both scripts use the configuration in `hosttech-config.sh`

With these scripts, certificate renewal can be automated, and you won't need to manually add DNS records.

```Bash
# Before running this command, make sure to:
# 1. Set your API token in scripts/hosttech-config.sh
# 2. Make the scripts executable: chmod +x scripts/certbot-dns-hosttech-*.sh

certbot certonly \
  --manual \
  --preferred-challenges=dns \
  --email security@michibaum.ch \
  --server https://acme-v02.api.letsencrypt.org/directory \
  --agree-tos \
  --manual-auth-hook /path/to/scripts/certbot-dns-hosttech-auth.sh \
  --manual-cleanup-hook /path/to/scripts/certbot-dns-hosttech-cleanup.sh \
  -d michibaum.ch -d *.michibaum.ch \
  -d michibaum.com -d *.michibaum.com \
  -d michibaum.eu -d *.michibaum.eu \
  -d michibaum.org -d *.michibaum.org \
  -d michibaum.app -d *.michibaum.app \
  -d michibaum.me -d *.michibaum.me \
  -d michibaum.net -d *.michibaum.net \
  -d michibaum.info -d *.michibaum.info \
  -d michibaum.xyz -d *.michibaum.xyz
```

## Result
The certificates are exported to `/etc/letsencrypt`

```Bash
Successfully received certificate.
Certificate is saved at: /etc/letsencrypt/live/michibaum.ch/fullchain.pem
Key is saved at:         /etc/letsencrypt/live/michibaum.ch/privkey.pem
This certificate expires on 2025-04-04.
These files will be updated when the certificate renews.
```

With the auth and cleanup hooks in place, the certificate can be renewed automatically using certbot's built-in renewal process:

```Bash
certbot renew
```

You can also set up a cron job to automatically renew certificates when they're close to expiration:

```Bash
# Add to crontab (crontab -e)
# Run twice daily (recommended by Let's Encrypt)
0 0,12 * * * certbot renew --quiet
```


## Create pkcs12
Go into directory from [result](#result) where .pem files are saved and execute following:

```Bash
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name michibaum -CAfile chain.pem
```
