# Confluence Publisher - Spring Boot Backend

Современный Spring Boot бекенд для приложения публикации страниц в Confluence.

## Технологический стек

- **Spring Boot 3.2.0** - современный фреймворк
- **Java 21** - последняя LTS версия
- **Gradle 8.5** - система сборки
- **Spring Data JPA** - работа с БД
- **SQLite** - база данных
- **Lombok** - уменьшение boilerplate кода
- **Bean Validation** - валидация данных

## Архитектура

Проект следует лучшим практикам Spring Boot:

- **Слоистая архитектура**: Controllers → Services → Repositories → Entities
- **DTO паттерн**: отдельные классы для запросов/ответов
- **Dependency Injection**: через конструкторы (Lombok @RequiredArgsConstructor)
- **Транзакционность**: @Transactional на уровне сервисов
- **Обработка ошибок**: GlobalExceptionHandler
- **Конфигурация**: через @ConfigurationProperties
- **Планировщик**: Spring @Scheduled

## Структура проекта

```
src/main/java/com/confluence/publisher/
├── controller/      # REST контроллеры
├── service/         # Бизнес-логика
├── repository/      # Spring Data JPA репозитории
├── entity/          # JPA сущности
├── dto/             # Data Transfer Objects
├── config/          # Конфигурационные классы
├── exception/       # Обработка исключений
├── provider/        # Провайдеры Confluence
└── scheduler/       # Планировщик задач
```

## API Endpoints

- `GET /api/health` - проверка здоровья
- `POST /api/pages` - создание страницы
- `GET /api/pages/{id}` - получение страницы
- `POST /api/attachments` - загрузка вложений
- `POST /api/schedules` - создание расписания
- `GET /api/schedules/{id}` - получение расписания
- `GET /api/schedules` - список расписаний
- `POST /api/confluence/publish` - публикация страницы в Confluence
- `POST /api/ai/improve-content` - улучшение контента
- `POST /api/ai/generate-summary` - генерация summary

## Запуск

### Локально

```bash
./gradlew bootRun
```

### Docker

```bash
docker-compose up backend
```

## Конфигурация

Настройки в `application.yml`:

- `app.database-url` - URL базы данных
- `app.attachment-dir` - директория для вложений
- `app.confluence-url` - URL Confluence instance
- `app.confluence-default-space` - пространство Confluence по умолчанию
- `app.confluence-api-token` - API токен для аутентификации
- `app.cors-origins` - разрешенные источники CORS
- `app.provider` - провайдер публикаций (confluence-stub)
- `app.scheduler-interval-seconds` - интервал проверки расписания

## Особенности

- ✅ Современный Spring Boot 3.x
- ✅ Java 21 с новыми возможностями
- ✅ Типобезопасная конфигурация
- ✅ Валидация входных данных
- ✅ Централизованная обработка ошибок
- ✅ Логирование через SLF4J
- ✅ Готовность к production (Docker, health checks)

