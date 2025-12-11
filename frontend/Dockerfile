# Frontend Dockerfile
FROM node:20-alpine AS build
WORKDIR /app
ARG NG_APP_API_BASE=http://localhost:8080
ENV NG_APP_API_BASE=${NG_APP_API_BASE}
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend ./
RUN sed -i "s|apiBase: '.*'|apiBase: '${NG_APP_API_BASE}'|g" src/environments/environment.prod.ts
RUN npm run build

FROM nginx:alpine AS runtime
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/confluence-publisher-app/browser /usr/share/nginx/html
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]