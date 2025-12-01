# 02. When to Use AI for Complex Tasks

Learn when AI assistance is most valuable and how to break down complex tasks into manageable prompts. This chapter focuses on practical decision-making: when to use AI, when to code manually, and how to structure complex requests.

## Learning objectives

- Decide when AI helps vs when it might hinder
- Break complex tasks into smaller, manageable prompts
- Structure multi-step requests effectively
- Recognize when to iterate vs when to start over
- Apply AI to real-world development scenarios

## Prerequisites

- Chapter 01 completed (understanding basic prompting)
- Familiarity with your project structure

## Outline

- When AI helps and when it doesn't
- Breaking down complex tasks
- Multi-step workflows with AI
- Real-world scenarios
- Common pitfalls and how to avoid them

## When AI helps and when it doesn't

### ✅ AI is great for:

**Repetitive tasks**
- Writing boilerplate code
- Creating similar components/functions
- Generating tests
- Refactoring similar patterns

**Learning and exploration**
- Understanding unfamiliar code
- Learning new libraries/frameworks
- Getting examples of patterns
- Exploring different approaches

**Debugging assistance**
- Analyzing error messages
- Finding potential issues
- Suggesting fixes
- Explaining complex code

**Code generation**
- Creating utilities from specifications
- Implementing standard patterns
- Writing documentation
- Generating configuration files

### ❌ AI struggles with:

**Highly specific business logic**
- Domain-specific rules
- Complex state machines
- Custom algorithms
- Project-specific patterns not in codebase

**Real-time debugging**
- Runtime issues that need live inspection
- Performance problems requiring profiling
- Race conditions and timing issues

**Architectural decisions**
- System design choices
- Technology selection
- Scalability decisions
- Team preferences

**When you don't understand the problem**
- If you can't explain it clearly, AI can't help
- Vague requirements lead to wrong solutions
- Need domain knowledge first

## Breaking down complex tasks

Complex tasks often fail with AI because they're too large. The solution: **break them into smaller pieces**.

### The breakdown strategy

1. **Identify the goal**: What are you trying to achieve?
2. **List dependencies**: What needs to happen first?
3. **Create steps**: Each step should be a single, clear task
4. **Order logically**: Do prerequisites first
5. **Test incrementally**: Verify each step before moving on

### Example: Building a feature

**❌ Too complex**:
```text
Build a user authentication system with login, registration, password reset, 
email verification, OAuth integration, and session management.
```

**✅ Broken down**:

**Step 1**: Create user model/schema
```text
Role: Backend developer
Task: Create user data model
Constraints:
- Include: email, password hash, verification status
- Follow existing database patterns
- Add appropriate indexes
Inputs: [existing model examples]
Output format: Model definition
```

**Step 2**: Implement registration endpoint
```text
Role: Backend developer  
Task: Create user registration endpoint
Constraints:
- Validate email format
- Hash password before storing
- Return appropriate status codes
- Follow existing API patterns
Inputs: [user model from step 1, existing API examples]
Output format: Endpoint code with validation
```

**Step 3**: Implement login endpoint
```text
Role: Backend developer
Task: Create login endpoint
Constraints:
- Verify password hash
- Generate session token
- Handle invalid credentials
- Follow existing auth patterns
Inputs: [user model, existing auth examples]
Output format: Login endpoint code
```

Continue breaking down until each step is manageable.

## Multi-step workflows

For tasks that require multiple related changes, structure your prompt to handle the workflow:

### Template: Multi-step feature

```text
Role: Full-stack developer
Task: Implement [feature name] with these steps:
1. [First step - e.g., Create data model]
2. [Second step - e.g., Create API endpoint]
3. [Third step - e.g., Create UI component]
4. [Fourth step - e.g., Add tests]

Constraints:
- Complete all steps in order
- Each step should build on previous ones
- Follow existing project patterns
- Include error handling at each step

Inputs:
- Step 1: [context needed]
- Step 2: [context from step 1 + additional]
- Step 3: [context from steps 1-2 + additional]
- Step 4: [all previous context]

Output format: 
- Complete implementation for all steps
- Brief explanation of how steps connect

Evaluation:
- Each step works independently
- Steps integrate correctly
- Follows project conventions
```

### When to do steps separately

Sometimes it's better to do steps one at a time:

- **Complex logic**: Each step needs careful review
- **Uncertain requirements**: Test each step before proceeding
- **Large changes**: Easier to review smaller diffs
- **Learning**: Understand each step before moving on

### When to combine steps

You can combine steps when:

- **Simple and related**: Steps are straightforward and connected
- **Well-defined**: You're confident about requirements
- **Standard pattern**: Following a common pattern you know works

## Real-world scenarios

### Scenario 1: Adding a new API endpoint

**Breakdown**:
1. Define data model (if needed)
2. Create route/controller
3. Add validation
4. Implement business logic
5. Add error handling
6. Write tests
7. Update documentation

**Prompt structure**:
```text
Role: Backend developer
Task: Add [endpoint name] API endpoint
Constraints:
- Follow existing API patterns in [reference files]
- Include input validation
- Add error handling
- Write unit tests
- Update API docs

Steps:
1. Review existing endpoint patterns
2. Create route/controller structure
3. Add validation logic
4. Implement core functionality
5. Add error handling
6. Write tests
7. Update documentation

Inputs: [existing API examples, data model if needed]
Output format: Complete implementation with tests
Evaluation: Endpoint works, tests pass, follows patterns
```

### Scenario 2: Refactoring a large component

**Breakdown**:
1. Understand current structure
2. Identify refactoring goals
3. Extract smaller pieces
4. Refactor incrementally
5. Update tests
6. Verify behavior unchanged

**Prompt structure**:
```text
Role: Code refactoring specialist
Task: Refactor [component name] for [goal: readability/maintainability/performance]

Current state: [describe or reference file]
Desired state: [describe improvements]

Constraints:
- Preserve exact behavior
- Improve [specific aspect]
- Keep same public API
- Maintain test coverage

Steps:
1. Analyze current structure
2. Identify extraction opportunities
3. Extract [specific pieces]
4. Refactor [specific parts]
5. Update tests if needed
6. Verify no behavior changes

Inputs: [component code, test file]
Output format: Refactored code + test updates + explanation
Evaluation: All tests pass, code improved, behavior identical
```

### Scenario 3: Debugging a complex issue

**Breakdown**:
1. Reproduce the issue
2. Identify affected code
3. Analyze root cause
4. Propose fix
5. Test the fix
6. Verify no regressions

**Prompt structure**:
```text
Role: Debugging specialist
Task: Fix [issue description]

Symptoms:
- [What happens]
- [When it happens]
- [Error messages if any]

Steps to reproduce:
1. [Step 1]
2. [Step 2]
3. [Step 3]

Affected code: [file paths]
Related code: [file paths]

Constraints:
- Minimal fix (don't refactor unrelated code)
- Preserve existing behavior for other cases
- Add regression test

Steps:
1. Analyze error/behavior
2. Identify root cause
3. Propose minimal fix
4. Add test to prevent regression
5. Verify fix works

Inputs: [error logs, stack traces, relevant code]
Output format: 
- Root cause explanation
- Minimal fix
- Regression test
- Verification steps

Evaluation: Issue fixed, test passes, no regressions
```

## Common pitfalls and how to avoid them

### Pitfall 1: Asking for too much at once

**Problem**: "Build a complete authentication system"

**Solution**: Break into steps (model → registration → login → etc.)

### Pitfall 2: Not providing enough context

**Problem**: "Add validation to the form"

**Solution**: Include form code, validation patterns, error handling examples

### Pitfall 3: Vague requirements

**Problem**: "Make it better"

**Solution**: Specify what "better" means (faster, more readable, fewer bugs)

### Pitfall 4: Ignoring project patterns

**Problem**: AI suggests patterns that don't match your project

**Solution**: Always reference existing code patterns in your prompts

### Pitfall 5: Not testing incrementally

**Problem**: Building everything then discovering it doesn't work

**Solution**: Test each step before moving to the next

## Ready-to-use prompt templates

### Template: Feature implementation

```text
Role: [Your role]
Task: Implement [feature name]

Breakdown:
1. [Step 1 description]
2. [Step 2 description]
3. [Step 3 description]

Constraints:
- Follow patterns in [reference files]
- Include [specific requirements]
- Handle [edge cases]

Inputs: [context for each step]
Output format: [what you need]
Evaluation: [how to verify success]
```

### Template: Refactoring

```text
Role: Refactoring specialist
Task: Refactor [code] for [goal]

Current issues: [what's wrong]
Desired improvements: [what should be better]

Constraints:
- Preserve behavior
- Improve [specific aspects]
- Follow [project standards]

Steps:
1. Analyze current code
2. Identify improvements
3. Apply refactoring
4. Verify no changes

Inputs: [code to refactor]
Output format: Refactored code + explanation
Evaluation: Tests pass, code improved
```

### Template: Debugging

```text
Role: Debugging assistant
Task: Fix [issue]

Problem: [description]
Steps to reproduce: [list]
Error: [if applicable]

Affected code: [files]
Constraints:
- Minimal fix
- Add regression test
- Explain root cause

Inputs: [error info, code]
Output format: Fix + test + explanation
Evaluation: Issue resolved, test added
```

## Exercises

### Exercise 1: Break down a complex task

**Goal**: Practice breaking down a large task

**Task**: "Build a blog system with posts, comments, user management, and search"

**Your assignment**:
1. Break this into 5-7 smaller steps
2. Write a prompt for the first step
3. Identify what context each step needs
4. Define how to verify each step

### Exercise 2: Multi-step implementation

**Goal**: Implement a feature using step-by-step approach

**Choose a small feature** (e.g., "add pagination to list view")

**Steps**:
1. Break it into 2-3 steps
2. Implement step 1 with AI
3. Test step 1
4. Implement step 2 with AI
5. Test integration
6. Document what worked

### Exercise 3: Refactoring workflow

**Goal**: Refactor code using structured approach

**Steps**:
1. Find a function/component that needs refactoring
2. Write a refactoring prompt with clear steps
3. Execute with AI
4. Review and test
5. Document improvements

### Exercise 4: Debugging workflow

**Goal**: Use AI to debug a real issue

**Steps**:
1. Find or create a bug in your code
2. Write a debugging prompt
3. Let AI identify and fix
4. Verify the fix
5. Add regression test

## Knowledge check (self-assessment)

Before moving forward, verify you can:

- [ ] Identify when AI helps vs when it doesn't
- [ ] Break complex tasks into smaller steps
- [ ] Structure multi-step prompts effectively
- [ ] Recognize common pitfalls
- [ ] Apply templates to real scenarios

## Troubleshooting

### AI gives incomplete solutions

**Problem**: AI implements part of a feature but misses important parts

**Solution**:
- Break task into smaller, more specific steps
- Add explicit checklist in constraints
- Review output and ask for missing pieces separately

### AI changes too much unrelated code

**Problem**: When asking for one thing, AI modifies many files

**Solution**:
- Be explicit: "Only modify [specific files]"
- Add constraint: "Don't change [unrelated areas]"
- Select specific code before prompting

### AI doesn't follow project patterns

**Problem**: Generated code doesn't match your architecture

**Solution**:
- Provide more examples of existing patterns
- Reference specific files in inputs
- Add constraint: "Follow exact pattern from [file]"
- Use project rules (see Chapter 00)

### Multi-step task gets confusing

**Problem**: AI loses track of steps or combines them incorrectly

**Solution**:
- Do steps one at a time instead of all at once
- Provide clear step numbers and dependencies
- Test each step before moving on
- Reference previous step outputs explicitly

## Checkpoint: What should work

After completing this chapter, you should be able to:

✅ Recognize when AI is helpful vs when to code manually  
✅ Break complex tasks into manageable steps  
✅ Structure multi-step prompts effectively  
✅ Avoid common pitfalls  
✅ Apply templates to real development scenarios  

**If you can do all of these**, you're ready for Chapter 03!

## References

- Chapter 01: Basic prompting skills
- Your project's architecture and patterns
- Cursor documentation on complex workflows
