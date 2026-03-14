# Estágio 1: Build
FROM node:18-alpine AS build-stage
WORKDIR /app

# 1. Instala o CLI global
RUN npm install -g @angular/cli

# 2. Copia tudo
COPY . .

# 3. Entra na pasta do template
WORKDIR /app/coreui-free-angular-admin-template

# 4. Instala dependências
RUN npm install

# 5. FORÇA O OUTPUT: Vamos obrigar o Angular a jogar o build na /app/saida_final
RUN npx ng build --configuration production --output-path=/app/saida_final

# Estágio 2: Servidor Nginx
FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 6. Agora não tem erro: os arquivos ESTARÃO em /app/saida_final/browser
# (O Angular 17+ sempre adiciona a pasta /browser no final)
COPY --from=build-stage /app/saida_final/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]