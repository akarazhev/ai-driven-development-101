# 01. Introduction to AI-Assisted Development

A practical guide to using AI assistants for software development. Learn how to write effective prompts, understand AI
capabilities and limitations, and integrate AI into your daily workflow.

This chapter focuses on **practical skills** you can use immediately: writing better prompts, evaluating AI output, and
building effective workflows.

## Learning objectives

- Write effective prompts using a proven template
- Understand when AI helps and when it doesn't
- Evaluate AI output and iterate on prompts
- Apply safety practices: avoid secrets, validate output
- Build a habit of prompt refinement and testing

## Prerequisites

- Cursor editor installed (see Chapter 03)
- Basic familiarity with your technology stack
- Understanding of your project structure

## Outline

- What is AI-assisted development?
- The prompt template that works
- Good vs bad prompts (with examples)
- The evaluation loop: test, refine, repeat
- Safety and best practices
- Ready-to-use prompt examples

## What is AI-assisted development?

AI-assisted development means using AI tools (like Cursor) to help you write code faster and better. Think of AI as a *
*pair programmer** that:

- Understands your codebase context
- Suggests implementations based on patterns
- Helps debug and refactor code
- Generates boilerplate and tests
- Explains complex code

**Key insight**: AI doesn't replace your thinking—it amplifies it. You still need to understand what you're building,
but AI handles the repetitive parts.

## The prompt template that works

After analyzing thousands of successful prompts, this template consistently produces better results:

```text
Role: [who is the assistant?]
Task: [what to produce?]
Constraints: [rules on style, scope, limits]
Inputs: [data/context the model must use]
Steps: [optional short plan]
Output format: [e.g., code file, JSON, markdown]
Evaluation: [acceptance criteria or tests]
```

### Why this template works

- **Role**: Sets context and expertise level
- **Task**: Clear, specific goal
- **Constraints**: Prevents scope creep and unwanted changes
- **Inputs**: Provides necessary context
- **Steps**: Breaks down complex tasks
- **Output format**: Ensures usable results
- **Evaluation**: Defines success criteria

## Good vs bad prompts

### ❌ Bad prompt

```text
Write code to count words.
```

**Problems**:

- Too vague
- No context about language or requirements
- No constraints or style preferences
- No way to evaluate success

### ✅ Good prompt

```text
Role: Senior software engineer
Task: Implement a function to count words in a text string
Constraints:
- Use your preferred language (TypeScript, Python, Java, etc.)
- Include type hints/annotations
- Ignore punctuation, treat multiple whitespace as one separator
- Handle edge cases (empty string, null, etc.)
- Add a brief docstring/comment
Inputs: Text string to process
Output format: Complete function code with example usage
Evaluation: Function handles "Hello,  world!" → returns 2
```

**Why it's better**:

- Clear role and task
- Specific constraints prevent unwanted behavior
- Handles edge cases
- Includes evaluation criteria
- Technology-agnostic

## Real-world prompt examples

### Example 1: Create a utility function

```text
Role: Senior developer
Task: Create a utility function to format dates in a user-friendly way
Constraints:
- Use your project's date library (moment, date-fns, etc.)
- Support relative time (e.g., "2 hours ago") and absolute format
- Handle timezone correctly
- Include error handling for invalid dates
- Follow your project's code style
Inputs: ISO date string
Output format: Function code with JSDoc/TypeDoc comments
Evaluation: 
- "2024-01-15T10:30:00Z" → "2 hours ago" (if current time is 12:30)
- Invalid date → returns "Invalid date" or throws appropriate error
```

### Example 2: Refactor existing code

```text
Role: Code refactoring specialist
Task: Improve readability and maintainability of this function
Constraints:
- Preserve exact behavior (no functional changes)
- Improve variable names and structure
- Add comments where logic is complex
- Keep same performance characteristics
- Follow project's coding standards
Inputs: [paste function code here]
Output format: Refactored code with brief explanation of changes
Evaluation: 
- All existing tests still pass
- Code is more readable
- No performance regression
```

### Example 3: Debug an issue

```text
Role: Debugging assistant
Task: Identify root cause of this error
Constraints:
- Analyze error message and stack trace
- Check related code files
- Propose minimal fix (don't refactor unrelated code)
- Explain why the error occurs
Inputs: 
- Error message: [paste error]
- Stack trace: [paste trace]
- Relevant files: [file paths]
Output format: 
1. Root cause explanation
2. Minimal fix with code changes
3. How to verify the fix
Evaluation: Fix resolves the error without breaking existing functionality
```

### Example 4: Add a feature

```text
Role: Full-stack developer
Task: Add user authentication endpoint
Constraints:
- Follow existing API patterns in the project
- Use project's authentication library
- Include input validation
- Add error handling
- Write unit tests
- Update API documentation
Inputs: 
- Existing auth patterns: [file paths]
- API structure: [file paths]
Output format: Complete implementation with tests
Evaluation:
- Endpoint works with valid credentials
- Returns appropriate errors for invalid input
- Tests pass
- Follows project conventions
```

## The evaluation loop

AI output isn't always perfect on the first try. Use this iterative process:

### Step 1: Define success criteria

Before asking AI, know what "done" looks like:

- What should the code do?
- What edge cases must it handle?
- What tests should pass?
- What style/patterns should it follow?

### Step 2: Run the prompt

Execute your prompt and capture the output.

### Step 3: Evaluate against criteria

Compare output to your success criteria:

- ✅ Does it meet requirements?
- ✅ Are edge cases handled?
- ✅ Does it follow project patterns?
- ✅ Are there any issues?

### Step 4: Refine and repeat

If output doesn't meet criteria:

- Identify specific gaps
- Add constraints to address them
- Provide examples of desired behavior
- Run the prompt again

**Example iteration**:

**First attempt**:

```text
Create a function to validate email addresses.
```

**Output**: Basic regex, no error handling

**Refined prompt**:

```text
Role: Senior developer
Task: Create email validation function
Constraints:
- Return clear error messages for invalid formats
- Handle edge cases (null, empty string, whitespace)
- Use standard email regex pattern
- Include type annotations
Inputs: Email string
Output format: Function with error handling
Evaluation: 
- Valid email → returns true
- Invalid format → returns false with specific error message
- Null/empty → returns false with appropriate error
```

## Safety and best practices

### ⚠️ Never include secrets

**❌ Bad**:

```text
Connect to database: postgresql://user:password123@host/db
```

**✅ Good**:

```text
Connect to database using environment variables:
- DB_HOST from process.env
- DB_USER from process.env  
- DB_PASSWORD from process.env
```

### ✅ Validate AI output

- **Run tests**: Don't trust code without testing
- **Review changes**: Check diffs before committing
- **Verify logic**: Understand what the code does
- **Check dependencies**: Ensure new libraries are appropriate

### ✅ Use version control

- Commit frequently
- Review AI-generated code before merging
- Keep prompts in commit messages for traceability

### ✅ Respect licenses

- Check licenses of AI-suggested code
- Don't copy proprietary code
- Attribute when required

## Ready-to-use prompt templates

### Template: Create a new feature

```text
Role: [Your role - e.g., Senior Full-Stack Developer]
Task: Implement [specific feature name]
Constraints:
- Follow existing patterns in [reference files]
- Use [specific libraries/frameworks from project]
- Include [specific requirements]
- Handle [edge cases]
Inputs: [Relevant context files]
Output format: [Code files, tests, documentation]
Evaluation: [Specific acceptance criteria]
```

### Template: Fix a bug

```text
Role: Debugging specialist
Task: Fix [specific bug description]
Constraints:
- Minimal change (don't refactor unrelated code)
- Preserve existing behavior for other cases
- Add regression test
Inputs: 
- Error: [error message]
- Code: [file paths]
- Steps to reproduce: [description]
Output format: Fix + test + explanation
Evaluation: Bug is fixed, test passes, no regressions
```

### Template: Refactor code

```text
Role: Code quality specialist
Task: Refactor [specific code] for [goal: readability/performance/maintainability]
Constraints:
- Preserve exact behavior
- Improve [specific aspect]
- Follow [project standards]
- Keep same API/interface
Inputs: [Code to refactor]
Output format: Refactored code + explanation
Evaluation: Tests pass, code is improved, no breaking changes
```

### Template: Write tests

```text
Role: Test engineer
Task: Write tests for [component/function]
Constraints:
- Use [testing framework from project]
- Cover [nominal cases, edge cases, error cases]
- Follow [project's testing patterns]
Inputs: [Code to test]
Output format: Test file with comprehensive coverage
Evaluation: All tests pass, edge cases covered
```

## Exercises

### Exercise 1: Your first AI prompt

**Goal**: Create a utility function using AI

**Steps**:

1. Choose a simple function (e.g., format currency, validate phone number, slugify text)
2. Write a prompt using the template
3. Run it in Cursor
4. Test the output
5. Refine if needed

**Deliverable**: Working function + the prompt you used

### Exercise 2: Refine a weak prompt

**Goal**: Improve a vague prompt

**Starting prompt**:

```text
Make the code better.
```

**Your task**:

- Identify what's wrong with this prompt
- Rewrite it using the template
- Include specific constraints and evaluation criteria
- Test the improved prompt

### Exercise 3: Debug with AI

**Goal**: Use AI to find and fix a bug

**Steps**:

1. Create a simple function with an intentional bug
2. Write a debugging prompt
3. Let AI identify and fix the issue
4. Verify the fix works

### Exercise 4: Build a small feature

**Goal**: Implement a complete feature using AI

**Steps**:

1. Choose a small feature (e.g., "add validation to form field")
2. Write a comprehensive prompt
3. Implement with AI assistance
4. Test and refine
5. Document what worked and what didn't

## Knowledge check (self-assessment)

Before moving to the next chapter, verify you can:

- [ ] Write a prompt using the template
- [ ] Identify problems with weak prompts
- [ ] Refine prompts based on output
- [ ] Apply safety practices (no secrets, validate output)
- [ ] Use the evaluation loop effectively

## Troubleshooting

### AI output is too generic

**Problem**: AI gives generic solutions that don't fit your project

**Solution**:

- Add more context about your project structure
- Reference existing code patterns
- Specify your technology stack
- Include examples of desired style

### AI changes too much code

**Problem**: AI modifies unrelated files or makes unnecessary changes

**Solution**:

- Add constraint: "Only modify [specific files]"
- Use: "Minimal change, preserve existing behavior"
- Select specific code before prompting
- Be explicit about scope

### AI output has bugs

**Problem**: Generated code doesn't work or has errors

**Solution**:

- Always test AI output
- Add evaluation criteria to prompt
- Request error handling in constraints
- Iterate: ask AI to fix specific issues

### AI doesn't understand context

**Problem**: AI suggests solutions that don't match your architecture

**Solution**:

- Provide more context in "Inputs" section
- Reference existing similar code
- Specify your patterns explicitly
- Use project rules (see Chapter 00)

## Checkpoint: What should work

After completing this chapter, you should be able to:

✅ Write a clear, structured prompt  
✅ Get useful output from AI on first try  
✅ Refine prompts when output isn't perfect  
✅ Avoid common mistakes (secrets, invalid code)  
✅ Use ready-made templates for common tasks

**If you can do all of these**, you're ready for Chapter 02!

## References

- Cursor documentation on prompting
- Your project's coding standards
- Your organization's AI usage guidelines
- Prompt engineering best practices

## Resources

- [Cursor Learn — Official Course](https://cursor.com/learn)
- [Cursor Directory: Rules](https://cursor.directory/rules)
