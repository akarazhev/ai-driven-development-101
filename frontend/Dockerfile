# Frontend Dockerfile
FROM node:20-alpine AS build
WORKDIR /app
ARG NG_APP_API_BASE=http://localhost:8000
ENV NG_APP_API_BASE=${NG_APP_API_BASE}
COPY frontend/package.json ./
RUN npm install
COPY frontend ./
RUN sed -i "s|apiBase: '.*'|apiBase: '${NG_APP_API_BASE}'|g" src/environments/environment.prod.ts
RUN npm run build

FROM nginx:alpine AS runtime
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/social-media-automation-app/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]