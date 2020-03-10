### STAGE 1: Build ###
FROM node:13.10.1-alpine AS BUILD
MAINTAINER Michael Baumberger
WORKDIR /usr/src/app
COPY package.json ./
RUN npm install npm@latest -g
COPY . .
RUN npm run build -- --prod --output-hashing=all

### STAGE 2: Run ###
FROM nginx:1.17.1-alpine
MAINTAINER Michael Baumberger
COPY --from=BUILD /usr/src/app/dist/lifemanagement-frontend /usr/share/nginx/html
