# Frontend Dockerfile
FROM node:20-alpine AS build
WORKDIR /app
ARG NG_APP_API_BASE=http://localhost:8000
ENV NG_APP_API_BASE=${NG_APP_API_BASE}
COPY frontend/package.json frontend/package-lock.json* ./
RUN npm ci
COPY frontend ./
RUN node replace-env.js
RUN npm run build

FROM nginx:alpine AS runtime
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/social-media-automation-app/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]