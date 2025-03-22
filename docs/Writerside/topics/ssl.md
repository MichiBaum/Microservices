# SSL

An SSL certificate is needed for https.
This SSL certificate is created with letsencrypt and certbot.

## Create certificates
Because of wildcard certificate you need to verify ownership of the Domain. This is done with adding a TXT record to the DNS entry.

```Bash
certbot certonly --manual --preferred-challenges=dns --email security@michibaum.ch --server https://acme-v02.api.letsencrypt.org/directory --agree-tos -d michibaum.ch -d *.michibaum.ch -d michibaum.com -d *.michibaum.com -d michibaum.eu -d *.michibaum.eu -d michibaum.org -d *.michibaum.org -d michibaum.app -d *.michibaum.app -d michibaum.me -d *.michibaum.me -d michibaum.net -d *.michibaum.net -d michibaum.info -d *.michibaum.info -d michibaum.xyz -d *.michibaum.xyz
```

## Result
The certificates are exported to `/etc/letsencrypt`

```Bash
Successfully received certificate.
Certificate is saved at: /etc/letsencrypt/live/michibaum.ch/fullchain.pem
Key is saved at:         /etc/letsencrypt/live/michibaum.ch/privkey.pem
This certificate expires on 2025-04-04.
These files will be updated when the certificate renews.

NEXT STEPS:
- This certificate will not be renewed automatically.
Autorenewal of --manual certificates requires the use of an authentication hook script (--manual-auth-hook) but one was not provided.
To renew this certificate, repeat this same certbot command before the certificate's expiry date.
```


## Create pkcs12
Go into directory from [result](#result) where .pem files are saved and execute following:

```Bash
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name michibaum -CAfile chain.pem
```