# 00. Rules and Prompt Optimization

Learn how to create and manage Cursor rules to simplify your prompts and maintain consistency across projects. This
chapter teaches you to build a rule-based system that makes AI assistance more effective and reduces repetitive
prompting.

## Learning objectives

- Understand what Cursor rules are and why they matter
- Learn the four types of rules: Always, Auto-Apply, Agent Select, and Manual
- Create your first rule using the standard template
- Generate rules for libraries and frameworks automatically
- Organize rules effectively in your project structure
- Apply rules to reduce prompt complexity and improve consistency

## Prerequisites

- Cursor editor installed and configured (see Chapter 03)
- Basic understanding of project structure
- Familiarity with your technology stack

## Outline

- What are Cursor rules and why use them
- Rule types and when to use each
- Rule structure and metadata
- Creating your first rule
- Generating rules for libraries and frameworks
- Organizing rules in your project
- Best practices and common patterns

## What are Cursor rules?

Cursor rules are configuration files that define how AI should behave in your project. They act as persistent context
that:

- **Reduce prompt complexity**: Instead of repeating instructions in every prompt, define them once in a rule
- **Maintain consistency**: Ensure AI follows your project's patterns and conventions
- **Improve accuracy**: Provide context about your stack, architecture, and preferences
- **Save tokens**: Rules are included automatically, reducing the need for long prompts

Think of rules as "project memory" for AI - they remember your preferences and patterns across all conversations.

## Rule types

Cursor supports four types of rules, each with different application patterns:

### Always Rules (`-always.mdc`)

- **When to use**: Rules that should apply to EVERY chat and command
- **Example**: Code style preferences, emoji usage, general project conventions
- **Location**: Must be in `.cursor/rules/global/`
- **Metadata**: `alwaysApply: true`, empty `description` and `globs`

### Auto-Apply Rules (`-auto.mdc`)

- **When to use**: Rules that apply automatically to files matching a glob pattern
- **Example**: TypeScript conventions for `*.ts` files, component patterns for `*.component.ts`
- **Metadata**: `alwaysApply: false`, requires `globs` pattern, empty `description`

### Agent Select Rules (`-agent.mdc`)

- **When to use**: Rules that should be applied occasionally based on context
- **Example**: Testing patterns, state management, specific library usage
- **Metadata**: `alwaysApply: false`, requires descriptive `description`, empty `globs`

### Manual Rules (`-manual.mdc`)

- **When to use**: Rules that should be manually requested by the user
- **Example**: Advanced patterns, optional optimizations, specialized workflows
- **Metadata**: `alwaysApply: false`, empty `description` and `globs`

## Rule structure

All rules must follow this standard structure:

```mdc
---
description: `Comprehensive description that provides full context and clearly indicates when this rule should be applied. Include key scenarios, impacted areas, and why following this rule is important. While being thorough, remain focused and relevant. The description should be detailed enough that the agent can confidently determine whether to apply the rule in any given situation.`

globs: *.ts OR blank

alwaysApply: true or false

---

# Rule Title

## Critical Rules

- Concise, bulleted list of actionable rules the agent MUST follow
- Rule with Example

<example>
{valid rule application}
</example>

<example type="invalid">
{invalid rule application}
</example>
```

### Metadata fields

- **description**: Keep under 200 characters. For Agent Select rules, this determines when the rule is applied
- **globs**: File pattern (e.g., `*.ts`, `**/*.component.ts`). Required for Auto-Apply rules
- **alwaysApply**: `true` for Always rules, `false` for others

## Organizing rules

Rules should be organized by area of focus, not by library:

```
.cursor/rules/
  global/              # Always rules, rule generation rules
  frontend/            # Frontend framework rules
  backend/             # Backend framework rules
  state/               # State management rules
  testing/             # Testing rules
  typescript/          # TypeScript rules
  styles/              # CSS/SCSS rules
```

**File naming**: `{area-of-focus}-{type}.mdc`

Examples:

- `.cursor/rules/global/rule-generation-always.mdc`
- `.cursor/rules/frontend/component-patterns-auto.mdc`
- `.cursor/rules/state/redux-patterns-agent.mdc`
- `.cursor/rules/testing/unit-testing-manual.mdc`

## Creating your first rule

### Step 1: Choose the rule type

Ask yourself:

- Should this apply to everything? → Always
- Should this apply to specific file types? → Auto-Apply
- Should this apply based on context? → Agent Select
- Should this be on-demand? → Manual

### Step 2: Write the rule content

1. Start with a clear title
2. List critical rules as bullet points
3. Add examples for clarity
4. Keep it focused and actionable

### Step 3: Set metadata

- Fill in `description` (if Agent Select)
- Set `globs` (if Auto-Apply)
- Set `alwaysApply` correctly

### Step 4: Place in correct folder

- Always rules → `.cursor/rules/global/`
- Others → appropriate area folder

## Generating rules for libraries

Instead of manually creating rules for each library, use AI to generate them automatically.

### When to generate rules

- Starting a new project with multiple libraries
- Adding a significant new library to existing project
- Refactoring and need to establish patterns
- Onboarding new team members

### Starter prompt for rule generation

Use this prompt template to generate rules for your project's libraries:

```text
Generate Cursor rules for significant libraries in @package.json (or equivalent dependency file)

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

## Library Categories and Rules

### State Management
- Redux, Zustand, MobX, Jotai, etc.
- Store architecture, action patterns, selector patterns

### UI Component Libraries
- Material-UI, Ant Design, Chakra UI, etc.
- Component usage, theming, accessibility

### Testing Libraries
- Jest, Vitest, Testing Library, Playwright, Cypress
- Testing patterns, mocking, coverage strategies

### Forms Libraries
- React Hook Form, Formik, Final Form
- Form patterns, validation, field configuration

### Data Fetching
- React Query, SWR, Apollo Client, RTK Query
- Query patterns, caching, invalidation

### Routing
- React Router, Next.js Router, Vue Router
- Route patterns, navigation, guards

### Internationalization
- i18next, react-intl, vue-i18n
- Translation patterns, locale management

After generating each rule, specify which metadata should be used:
- File name: `{library-name}-{type}.mdc`
- description: (if Agent Select)
- alwaysApply: (true/false)
- globs: (if Auto-Apply, the glob pattern)
```

## Best practices

### Do's

✅ **Check existing rules first**: Always check `.cursor/rules/` before creating new ones  
✅ **Update instead of duplicate**: Modify existing rules when possible  
✅ **Keep descriptions clear**: For Agent Select rules, be specific about when to apply  
✅ **Use examples**: Include valid and invalid examples for clarity  
✅ **Organize by area**: Group related rules, not by library  
✅ **Keep rules focused**: One rule per concern, not everything in one file

### Don'ts

❌ **Don't include setup code**: Rules should focus on usage, not initialization  
❌ **Don't create duplicates**: Check existing rules first  
❌ **Don't mix concerns**: Keep rules focused on one area  
❌ **Don't use vague descriptions**: Agent Select rules need clear criteria  
❌ **Don't forget metadata**: Always set correct `alwaysApply`, `globs`, `description`

## Common patterns

### Pattern 1: Framework conventions

**Type**: Auto-Apply  
**Glob**: `*.component.tsx` (or equivalent)  
**Content**: Component structure, naming, props patterns

### Pattern 2: Testing standards

**Type**: Agent Select  
**Description**: "When writing or modifying tests"  
**Content**: Testing patterns, mocking strategies, assertions

### Pattern 3: Code style

**Type**: Always  
**Location**: `.cursor/rules/global/`  
**Content**: General style preferences, formatting, naming

### Pattern 4: Library-specific patterns

**Type**: Agent Select  
**Description**: "When using [Library Name] for [specific purpose]"  
**Content**: Usage patterns, best practices, common pitfalls

## Exercises

### Exercise 1: Create your first rule

Create an Always rule for code style preferences:

1. Choose 3-5 style preferences (e.g., "Use const for immutable values", "Prefer arrow functions")
2. Write the rule following the template
3. Place it in `.cursor/rules/global/code-style-always.mdc`
4. Test it by asking AI to write code and verify it follows your rules

### Exercise 2: Auto-Apply rule for components

Create an Auto-Apply rule for component files:

1. Identify your component file pattern (e.g., `*.component.tsx`, `*.vue`)
2. Define 3-5 component conventions
3. Create the rule with appropriate glob pattern
4. Test by creating a new component

### Exercise 3: Generate rules for your stack

Use the starter prompt to generate rules for your project:

1. Identify 3-5 key libraries in your project
2. Use the generation prompt
3. Review generated rules
4. Refine and organize them

### Exercise 4: Agent Select rule

Create an Agent Select rule for a specific use case:

1. Choose a context (e.g., "When writing API endpoints", "When creating forms")
2. Write a clear description
3. Define the rules and examples
4. Test by asking AI to work in that context

## Knowledge check (self-assessment)

- What are the four types of rules and when should you use each?
- How do you organize rules in your project structure?
- What metadata is required for each rule type?
- How can rules reduce prompt complexity?
- When should you generate rules automatically vs. create them manually?

## Troubleshooting

### Rule not applying

- **Check metadata**: Verify `alwaysApply`, `globs`, and `description` are correct
- **Check location**: Always rules must be in `global/` folder
- **Check glob pattern**: Test your glob pattern matches intended files
- **Check description**: Agent Select rules need clear, specific descriptions

### Too many rules applying

- **Review Always rules**: Only use Always for truly universal rules
- **Refine globs**: Make Auto-Apply patterns more specific
- **Improve descriptions**: Make Agent Select descriptions more specific

### Rules conflicting

- **Consolidate**: Merge overlapping rules
- **Prioritize**: Use more specific rules to override general ones
- **Organize**: Better folder structure can prevent conflicts

## References

- Cursor documentation on rules
- Your project's existing rules in `.cursor/rules/`
- Rule generation prompt template (included in this chapter)
- [Cursor Directory: Rules](https://cursor.directory/rules)
