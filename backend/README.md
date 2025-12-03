# Social Media Automation - Spring Boot Backend

Современный Spring Boot бекенд для приложения автоматизации публикаций в социальных сетях.

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
src/main/java/com/socialmedia/automation/
├── controller/      # REST контроллеры
├── service/         # Бизнес-логика
├── repository/      # Spring Data JPA репозитории
├── entity/          # JPA сущности
├── dto/             # Data Transfer Objects
├── config/          # Конфигурационные классы
├── exception/       # Обработка исключений
├── provider/        # Провайдеры публикаций
└── scheduler/       # Планировщик задач
```

## API Endpoints

- `GET /api/health` - проверка здоровья
- `POST /api/posts` - создание поста
- `GET /api/posts/{id}` - получение поста
- `POST /api/media` - загрузка медиа
- `POST /api/schedules` - создание расписания
- `GET /api/schedules/{id}` - получение расписания
- `GET /api/schedules` - список расписаний
- `POST /api/providers/{accountId}/publish` - публикация поста
- `POST /api/ai/variants` - генерация вариантов текста
- `POST /api/ai/alt-text` - генерация alt-текста

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
- `app.media-dir` - директория для медиафайлов
- `app.cors-origins` - разрешенные источники CORS
- `app.provider` - провайдер публикаций (stub)
- `app.scheduler-interval-seconds` - интервал проверки расписания

## Особенности

- ✅ Современный Spring Boot 3.x
- ✅ Java 21 с новыми возможностями
- ✅ Типобезопасная конфигурация
- ✅ Валидация входных данных
- ✅ Централизованная обработка ошибок
- ✅ Логирование через SLF4J
- ✅ Готовность к production (Docker, health checks)

