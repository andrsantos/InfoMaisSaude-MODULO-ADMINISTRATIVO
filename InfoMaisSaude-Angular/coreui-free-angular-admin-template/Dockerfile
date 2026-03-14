# Estágio 1: Build
FROM node:18-alpine AS build-stage
WORKDIR /app

# 1. Instala o CLI global
RUN npm install -g @angular/cli

# 2. Copia tudo para o container
COPY . .

# 3. Entra na pasta do template
WORKDIR /app/coreui-free-angular-admin-template

# 4. Instala e Builda
RUN npm install
RUN ng build --configuration production

# Estágio 2: Servidor Nginx
FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 5. Caminho Manual e Direto (O que você confirmou que existe)
RUN ls -R /app/coreui-free-angular-admin-template/dist

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]