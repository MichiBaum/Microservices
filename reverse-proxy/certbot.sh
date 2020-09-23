if [ ! -f /var/www/certbot ]; then
    mkdir -p /var/www/certbot
fi
# --test-cert or --staging for testing
certbot certonly \
        --config-dir "/etc/letsencrypt" \
		--agree-tos \
		--domains "michibaum.ch" \
		--email "michael_baumberger@gmx.ch" \
		--expand \
		--noninteractive \
		--webroot \
		--webroot-path /var/www/certbot \
		$OPTIONS || true

if [ -f "/etc/letsencrypt/live/michibaum.ch/privkey.pem" ]; then
    cp "/etc/letsencrypt/live/michibaum.ch/privkey.pem" /usr/share/nginx/certificates/privkey.pem
    cp "/etc/letsencrypt/live/michibaum.ch/fullchain.pem" /usr/share/nginx/certificates/fullchain.pem
fi