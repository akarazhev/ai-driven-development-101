# Starter Prompt for Rule Generation

Use this prompt to automatically generate Cursor rules for libraries and frameworks in your project.

## Technology-Agnostic Prompt Template

```text
Generate Cursor rules for significant libraries in @package.json (or @requirements.txt, @pom.xml, @Gemfile, or equivalent dependency file)

Create a file for each library covered focusing on:

1. Library Selection Criteria:
   - DO NOT generate rules for libraries not in dependency file
   - DO NOT generate rules that already exist
   - DO NOT include initialization/configuration code

2. Rule Content Requirements:
   - DO NOT include library initialization/setup code
   - DO NOT include configuration examples
   - FOCUS on usage patterns and best practices
   - INCLUDE common pitfalls
   - INCLUDE performance considerations

3. For Each Library Rule:
   - Integration patterns
   - Best practices
   - Common issues
   - Performance optimization
   - Security considerations
   - Testing strategies

4. Rule Format:
   - Clear metadata
   - Glob patterns (if Auto-Apply)
   - Descriptive sections
   - Practical examples of USAGE only
   - Error prevention guidelines

5. Specific Exclusions:
   - Configuration code
   - Setup instructions
   - Initialization patterns
   - Module imports
   - Provider configurations
   - Framework bootstrap code

## Library Categories and Rules

### State Management
- Redux, Zustand, MobX, Jotai, Pinia, Vuex, NgRx, NGXS, etc.
- Store architecture, action patterns, selector patterns, async operations

### UI Component Libraries
- Material-UI, Ant Design, Chakra UI, Mantine, PrimeNG, Angular Material, etc.
- Component usage, theming, accessibility, proper component patterns

### Testing Libraries
- Jest, Vitest, Mocha, Jasmine, Testing Library, Playwright, Cypress, etc.
- Testing patterns, mocking strategies, coverage, E2E patterns

### Forms Libraries
- React Hook Form, Formik, Final Form, Angular Forms, Vue Formulate, etc.
- Form patterns, validation, field configuration, form generation

### Data Fetching
- React Query, SWR, Apollo Client, RTK Query, Axios patterns, Fetch patterns, etc.
- Query patterns, caching strategies, invalidation, error handling

### Routing
- React Router, Next.js Router, Vue Router, Angular Router, etc.
- Route patterns, navigation, guards, route configuration

### Internationalization
- i18next, react-intl, vue-i18n, ngx-translate, etc.
- Translation patterns, locale management, lazy loading

### Build & Architecture (DO NOT ADD)
- Webpack, Vite, Nx, Angular CLI, Create React App
- These are build tools, not usage patterns

### Data Visualization
- D3, Chart.js, Recharts, Victory, NGX-Charts, etc.
- Chart patterns, data binding, responsive charts, animations

### Authentication
- Auth0, Firebase Auth, JWT patterns, OAuth patterns, etc.
- Authentication flows, token management, security patterns

### Validation
- Zod, Yup, Joi, class-validator, etc.
- Validation patterns, schema definitions, error handling

### Styling
- Tailwind CSS, Styled Components, Emotion, CSS Modules, SCSS patterns, etc.
- Styling patterns, theming, responsive design

After generating each rule, specify which metadata should be used:

- File name: `{library-name}-{type}.mdc`
- description: (The description for Agent Select rules, keep under 200 chars)
- alwaysApply: (true for Always rules, false for others)
- globs: (The glob pattern for Auto-Apply rules, blank for others)

## Example of what NOT to include (initialization/configuration code):

```typescript
// ❌ DON'T include setup code like this:
export const store = configureStore({
  reducer: {
    counter: counterReducer,
  },
});

// ✅ DO include usage patterns like this:
// Use selectors to access state
const count = useSelector((state) => state.counter.value);

// Dispatch actions with proper typing
dispatch(increment());
```

```python
# ❌ DON'T include initialization code like this:
from flask import Flask
app = Flask(__name__)

# ✅ DO include usage patterns like this:
# Use route decorators with proper error handling
@app.route('/api/users', methods=['GET'])
def get_users():
    try:
        users = db.get_all_users()
        return jsonify(users), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
```

## Notes

- Focus on HOW to use the library, not HOW to set it up
- Include real-world patterns and anti-patterns
- Provide examples that show correct usage
- Consider performance implications
- Address security concerns where relevant
- Include testing strategies for the library
```

## Customization Tips

### For Frontend Projects

Add these categories:
- Component libraries specific to your framework
- Styling solutions you use
- Form libraries
- State management

### For Backend Projects

Add these categories:
- ORM patterns (SQLAlchemy, TypeORM, Prisma, etc.)
- API frameworks (Express, FastAPI, Spring, etc.)
- Database patterns
- Authentication/Authorization libraries

### For Full-Stack Projects

Include both frontend and backend categories, organized by area:
- `.cursor/rules/frontend/` for frontend libraries
- `.cursor/rules/backend/` for backend libraries
- `.cursor/rules/shared/` for shared utilities

## Usage Instructions

1. **Identify your dependency file**: Find your `package.json`, `requirements.txt`, `pom.xml`, or equivalent
2. **Copy the prompt**: Use the template above
3. **Customize categories**: Add or remove library categories based on your stack
4. **Run the prompt**: Paste it into Cursor chat
5. **Review generated rules**: Check each rule for accuracy
6. **Refine as needed**: Adjust rules based on your specific needs
7. **Organize**: Ensure rules are in correct folders with proper naming

## Expected Output

After running the prompt, you should get:
- Multiple rule files (one per significant library)
- Proper metadata for each rule
- Organized in appropriate folders
- Focused on usage patterns, not setup

## Next Steps

After generating rules:
1. Review each rule for accuracy
2. Test rules by asking AI to use the libraries
3. Refine rules based on your team's preferences
4. Add project-specific patterns
5. Document any custom conventions

