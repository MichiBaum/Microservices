# Gateway Service

## Create SSl certificate
    certbot certonly --manual --preferred-challenges=dns --email security@michibaum.ch --server https://acme-v02.api.letsencrypt.org/directory --agree-tos -d michibaum.ch -d *.michibaum.ch
    openssl pkcs12 -export -in fullchain.pem -inkey privkey.pem -out keystore.p12 -name michibaum -CAfile chain.pem -caname root
