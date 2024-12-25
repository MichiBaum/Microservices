# SSL

An SSL certificate is needed for https. Currently only for the Domain https://michibaum.ch  
This SSL certificate is created with letsencrypt and certbot.

## Create certificates
Because of wildcard certificate you need to verify ownership of the Domain. This is done with adding a TXT record to the DNS entry.

```Bash
certbot certonly --manual --preferred-challenges=dns --email michael_baumberger@gmx.ch --server https://acme-v02.api.letsencrypt.org/directory --agree-tos -d michibaum.ch -d *.michibaum.ch
```

## Locations of certificates
The certificates are exported to ``/etc/letsencrypt``


## Create pkcs12

```Bash
openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name michibaum -CAfile chain.pem -caname root
```
