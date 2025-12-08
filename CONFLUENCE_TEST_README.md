# Confluence API Posting Test Script

Python скрипт для проверки возможности публикации контента в Confluence через REST API.

## Описание шагов проверки

Скрипт выполняет следующие шаги:

### Step 1: Проверка подключения
- Проверяет доступность Confluence сервера
- Получает информацию о версии сервера
- Использует endpoint: `/rest/api/serverInfo`

### Step 2: Проверка аутентификации
- Проверяет валидность учетных данных (username + API token)
- Получает информацию о текущем пользователе
- Использует endpoint: `/rest/api/user/current`

### Step 3: Получение информации о пространстве
- Проверяет существование указанного пространства (space key)
- Получает метаданные пространства
- Использует endpoint: `/rest/api/space?keys={SPACE_KEY}`

### Step 4: Проверка прав доступа
- Проверяет возможность чтения контента из пространства
- Использует endpoint: `/rest/api/content?spaceKey={SPACE_KEY}`

### Step 5: Создание тестовой страницы
- Создает тестовую страницу в указанном пространстве
- Проверяет права на запись (write permissions)
- Использует endpoint: `POST /rest/api/content`
- **Важно**: Страница будет создана с заголовком "API Test Page - Please Delete"

### Step 6: Удаление тестовой страницы (опционально)
- Удаляет созданную тестовую страницу
- Использует endpoint: `DELETE /rest/api/content/{pageId}`
- Выполняется автоматически, если `cleanup=True`

## Требования

- Python 3.6+
- Библиотека `requests`

## Установка зависимостей

```bash
pip install requests
```

Или используйте requirements.txt:

```bash
pip install -r requirements.txt
```

## Использование

### Базовое использование

```bash
python check_confluence_posting.py
```

### Программное использование

```python
from check_confluence_posting import ConfluenceTester

# Создать экземпляр тестера
tester = ConfluenceTester(
    base_url="https://pmc-stage.specific-group.eu/confluence",
    username="spg.academy",
    api_token="YOUR_API_TOKEN"
)

# Запустить полный тест
success = tester.run_full_test(space_key="SPGAC", cleanup=True)

# Или выполнить шаги по отдельности
tester.test_connection()
tester.test_authentication()
tester.get_space_info("SPGAC")
tester.check_space_permissions("SPGAC")
page_id = tester.create_test_page("SPGAC")
if page_id:
    tester.delete_test_page(page_id)
```

## Конфигурация

Для использования скрипта настройте следующие параметры в `.env` файле или переменных окружения:

- **Base URL**: `CONFLUENCE_URL` (например, `https://your-domain.atlassian.net/confluence`)
- **Username**: `CONFLUENCE_USERNAME` (ваш username)
- **API Token**: `CONFLUENCE_API_TOKEN` (ваш API token)
- **Space Key**: `CONFLUENCE_SPACE_KEY` или `CONFLUENCE_DEFAULT_SPACE` (ключ пространства)

⚠️ **Важно**: Не коммитьте реальные токены в репозиторий! Используйте `.env` файл, который уже в `.gitignore`.

## Возможные проблемы и решения

### Проблема: Connection failed
- **Причина**: Сервер недоступен или неправильный URL
- **Решение**: Проверьте доступность сервера и правильность базового URL

### Проблема: Authentication failed (401)
- **Причина**: Неверные учетные данные
- **Решение**: Проверьте username и API token

### Проблема: Space not found (404)
- **Причина**: Неверный space key
- **Решение**: Проверьте правильность ключа пространства

### Проблема: Permission denied (403)
- **Причина**: Недостаточно прав для выполнения операции
- **Решение**: Убедитесь, что пользователь имеет права на создание контента в пространстве

### Проблема: Failed to create test page
- **Причина**: Нет прав на запись или проблемы с форматом данных
- **Решение**: Проверьте права пользователя и формат запроса

## API Endpoints используемые в скрипте

1. `GET /rest/api/serverInfo` - информация о сервере
2. `GET /rest/api/user/current` - текущий пользователь
3. `GET /rest/api/space?keys={key}` - информация о пространстве
4. `GET /rest/api/content?spaceKey={key}` - контент пространства
5. `POST /rest/api/content` - создание страницы
6. `DELETE /rest/api/content/{id}` - удаление страницы

## Безопасность

⚠️ **Важно**: 
- Не коммитьте API токены в репозиторий
- Используйте переменные окружения для хранения чувствительных данных
- Регулярно обновляйте API токены

## Пример использования переменных окружения

```python
import os

# Load from .env file or environment variables
BASE_URL = os.getenv("CONFLUENCE_URL")
USERNAME = os.getenv("CONFLUENCE_USERNAME")
API_TOKEN = os.getenv("CONFLUENCE_API_TOKEN")

# Validate required variables
if not all([BASE_URL, USERNAME, API_TOKEN]):
    raise ValueError("Missing required environment variables: CONFLUENCE_URL, CONFLUENCE_USERNAME, CONFLUENCE_API_TOKEN")
```

Запуск:
```bash
export CONFLUENCE_API_TOKEN="your_token_here"
python check_confluence_posting.py
```

## Дополнительная информация

- [Confluence REST API Documentation](https://developer.atlassian.com/server/confluence/confluence-server-rest-api/)
- [Confluence REST API Examples](https://developer.atlassian.com/server/confluence/confluence-rest-api-examples/)
- [Authentication with Personal Access Tokens](https://confluence.atlassian.com/enterprise/using-personal-access-tokens-1026032365.html)

