### STAGE 1: Build ###
FROM node:14.4.0-alpine AS BUILD
MAINTAINER Michael Baumberger
WORKDIR /usr/src/app
COPY package.json ./
RUN npm install npm@latest -g
RUN npm install
COPY . .
RUN npm run build -- --prod --output-hashing=all

### STAGE 2: Run ###
FROM nginx:1.19.0-alpine
MAINTAINER Michael Baumberger
COPY --from=BUILD /usr/src/app/dist/lifemanagement-frontend /usr/share/nginx/html/lifemanagement
RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*
RUN rm -r /usr/share/nginx/html
RUN rm -f /var/log/nginx/*