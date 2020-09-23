#!/bin/sh
# Create a self signed default certificate, so Ngix can start before we have
# any real certificates.

#Ensure we have folders available

echo "Start entrypoint script"

if [ ! -f /usr/share/nginx/certificates/fullchain.pem ];then
    mkdir -p /usr/share/nginx/certificates
fi

echo "Start certificates generation"

### If certificates don't exist yet we must ensure we create them to start nginx
if [ ! -f /usr/share/nginx/certificates/fullchain.pem ]; then
    openssl genrsa -out /usr/share/nginx/certificates/privkey.pem 4096
    openssl req -new -key /usr/share/nginx/certificates/privkey.pem -out /usr/share/nginx/certificates/cert.csr -nodes -subj \
    "/C=CH/ST=Graubuenden/L=Chur/O='.'/OU='.' Department/CN=michibaum.ch"
    openssl x509 -req -days 365 -in /usr/share/nginx/certificates/cert.csr -signkey /usr/share/nginx/certificates/privkey.pem -out /usr/share/nginx/certificates/fullchain.pem
fi

echo "End certificates generation"

### Send certbot Emission/Renewal to background
$(while :; do /opt/certbot.sh; sleep "12h"; done;) &

### Check for changes in the certificate (i.e renewals or first start) and send this process to background
$(while inotifywait -e close_write /usr/share/nginx/certificates; do nginx -s reload; done) &

### Start nginx with daemon off as our main pid
nginx -g "daemon off;"

echo "End entrypoint script"