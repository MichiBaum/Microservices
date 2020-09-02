### STAGE 1: Build ###
FROM node:14.4.0-alpine AS BUILD
LABEL maintainer="Michael Baumberger"
WORKDIR /usr/src/app
COPY package.json ./
RUN npm install npm@latest -g
RUN npm install
COPY . .
RUN npm run build -- --prod --build-optimizer --output-hashing=all

### STAGE 2: Run ###
FROM nginx:1.17.10-alpine
LABEL maintainer="Michael Baumberger"
RUN rm -r /usr/share/nginx/html && \
    rm -f /var/log/nginx/* && \
    mkdir /usr/share/nginx/html/ && \
    rm /etc/nginx/conf.d/default.conf
COPY --from=BUILD /usr/src/app/dist/lifemanagement-frontend /usr/share/nginx/html/lifemanagement
COPY ./default.conf /etc/nginx/conf.d/default.conf
RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*
