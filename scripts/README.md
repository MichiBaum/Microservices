# Certbot DNS Verification Scripts for hosttech

This directory contains scripts for automating the DNS verification process for Let's Encrypt certificates using the hosttech API.

## Files

- `hosttech-config.sh`: Configuration file for API credentials and settings
- `certbot-dns-hosttech-auth.sh`: Script for adding DNS TXT records (manual-auth-hook)
- `certbot-dns-hosttech-cleanup.sh`: Script for removing DNS TXT records (manual-cleanup-hook)

## Setup

1. Make the scripts executable:
   ```bash
   chmod +x certbot-dns-hosttech-*.sh
   ```

2. Edit the configuration file to add your hosttech API token:
   ```bash
   nano hosttech-config.sh
   ```
   
   Replace `YOUR_HOSTTECH_API_TOKEN` with your actual API token from hosttech.

3. You can also adjust the DNS propagation wait time (default is 60 seconds) if needed.

## Usage

The scripts are designed to be used with certbot's `--manual-auth-hook` and `--manual-cleanup-hook` options:

```bash
certbot certonly \
  --manual \
  --preferred-challenges=dns \
  --email your-email@example.com \
  --server https://acme-v02.api.letsencrypt.org/directory \
  --agree-tos \
  --manual-auth-hook /path/to/scripts/certbot-dns-hosttech-auth.sh \
  --manual-cleanup-hook /path/to/scripts/certbot-dns-hosttech-cleanup.sh \
  -d example.com -d *.example.com
```

Replace `/path/to/scripts/` with the actual path to the scripts directory.

## How it works

1. When certbot needs to verify domain ownership, it calls the `certbot-dns-hosttech-auth.sh` script
2. The script uses the hosttech API to add a TXT record with the verification token
3. After verification, certbot calls the `certbot-dns-hosttech-cleanup.sh` script
4. The cleanup script removes the TXT record

## Automatic renewal

With these scripts in place, certificates can be renewed automatically:

```bash
certbot renew
```

You can also set up a cron job to automatically renew certificates:

```bash
# Add to crontab (crontab -e)
# Run twice daily (recommended by Let's Encrypt)
0 0,12 * * * certbot renew --quiet
```

## Troubleshooting

- If you encounter errors, check that your API token is correct
- Ensure the scripts have execute permissions
- Check that the domain is managed by hosttech
- Increase the DNS_WAIT_TIME if DNS propagation is taking longer than expected