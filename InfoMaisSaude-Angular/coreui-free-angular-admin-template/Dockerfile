# Estágio 1: Build
FROM node:18-alpine AS build-stage
WORKDIR /app

RUN npm install -g @angular/cli

# Copia tudo para o container
COPY . .

# Entra na pasta do template
WORKDIR /app/coreui-free-angular-admin-template

# Instala e Builda
RUN npm install
RUN ng build --configuration production

# COMANDO CHAVE: Localiza a pasta 'browser' (não importa onde ela esteja na dist) 
# e move o conteúdo para /app/final_build
RUN mkdir -p /app/final_build && \
    CP_SOURCE=$(find dist -name "browser" -type d | head -n 1) && \
    cp -r $CP_SOURCE/. /app/final_build/

# Estágio 2: Servidor Nginx
FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Agora o COPY é simples e direto, sem caminhos gigantes
COPY --from=build-stage /app/final_build /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]