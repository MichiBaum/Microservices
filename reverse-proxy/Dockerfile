FROM nginx:1.17.10
LABEL version="1.0"
LABEL maintainer="Michael Baumberger"

# Remove default shit
RUN rm /etc/nginx/conf.d/default.conf && \
    rm /etc/nginx/nginx.conf && \
    rm -f /var/log/nginx/* && \
    rm -r /usr/share/nginx/html

#Update and install everything
RUN apt-get update && \
    apt-get update && \
    apt-get -y upgrade && \
    apt-get install -y apache2-utils && \
    apt-get -y install libncursesw5-dev gcc make && \
    apt-get -y install libncursesw5-dev libgeoip-dev libmaxminddb-dev && \
    apt-get install -y inotify-tools certbot openssl && \
    apt-get install -y cron && \
    apt-get install -y wget && \
    apt-get update && \
    apt-get clean  && \
    apt-get autoclean && \
    apt-get autoremove && \
    apt-get --purge autoremove

#Copy all neccessary files from host to docker
COPY ./robots.txt /etc/nginx/robots.txt
COPY ./nginx.conf /etc/nginx/nginx.conf
COPY ./entrypoint.sh /opt/nginx-letsencrypt/entrypoint.sh
COPY ./certbot.sh /opt/certbot.sh
COPY ./default.conf /etc/nginx/conf.d/default.conf
COPY ./ssl-options/options-nginx-ssl.conf /etc/ssl-options/options-nginx-ssl.conf
COPY ./goaccess/goaccess_custom.conf /etc/goacces_custom.conf

#Run ssl configuration
RUN openssl dhparam -out /etc/ssl-options/ssl-dhparams.pem 2048
RUN chmod +x /opt/nginx-letsencrypt/entrypoint.sh && \
    chmod +x /opt/certbot.sh

#Install Goaccess
RUN wget http://tar.goaccess.io/goaccess-1.4.tar.gz && \
    tar -xzvf goaccess-1.4.tar.gz && \
    ./goaccess-1.4/configure --enable-utf8 && \
    mkdir ./resources && \
    make && \
    make install && \
    ln -s /usr/local/bin/goaccess /usr/bin/goaccess

# TODO change goaccess config
RUN mkdir -p /var/www/goaccess/
# TODO create random password
RUN htpasswd -b -c /var/www/goaccess/.htpasswd admin admin

# Copy goaccess-conjob file to the cron.d directory
COPY ./goaccess/goaccess-conjob /etc/crond.d/goaccess-conjob
# Give execution rights on the cron job
RUN chmod 0644 /etc/crond.d/goaccess-conjob
# Apply cron job
RUN crontab /etc/crond.d/goaccess-conjob
# Create the log file
RUN touch /var/log/cron.log

CMD cron && /opt/nginx-letsencrypt/entrypoint.sh