# 05. AI-Driven Software Development

A practical playbook for using AI across the entire software development lifecycle. Learn repeatable workflows,
effective prompt patterns, and best practices for building production-quality software with AI assistance.

## Learning objectives

- Apply AI to all stages of development: design, coding, testing, debugging, refactoring
- Use proven prompt patterns for common development tasks
- Establish repeatable, measurable workflows
- Integrate safety, security, and quality practices
- Build complete features using AI assistance

## Prerequisites

- Chapters 00-03 completed (or 00-03 and skipped 04)
- Cursor set up and working
- Basic familiarity with your technology stack

## Outline

- Principles for AI-driven development
- Complete development workflow
- Prompt patterns for every stage
- Testing and quality assurance
- Security and compliance
- Real-world examples
- Troubleshooting

## Principles for AI-driven development

### 1. Small, focused changes

✅ **Do**: Make one clear change at a time  
❌ **Don't**: Ask AI to rewrite entire modules

**Why**: Easier to review, test, and debug

### 2. Explicit acceptance criteria

✅ **Do**: Define exactly what "done" means  
❌ **Don't**: Use vague requirements

**Example**:

- ✅ "Function returns user object with id, name, email fields"
- ❌ "Make it work"

### 3. Test-driven approach

✅ **Do**: Write or update tests with every change  
❌ **Don't**: Skip testing and hope it works

**Why**: Tests verify AI output and prevent regressions

### 4. Iterate rapidly

✅ **Do**: Make small changes, test, refine  
❌ **Don't**: Try to get everything perfect in one prompt

**Why**: Faster feedback, easier debugging

### 5. Review everything

✅ **Do**: Review all AI-generated code  
❌ **Don't**: Accept changes blindly

**Why**: AI can make mistakes; you're responsible for the code

## Complete development workflow

### Step 1: Plan

**Goal**: Define what you're building

**Activities**:

- Break feature into small tasks
- Define acceptance criteria
- Identify dependencies
- Plan implementation order

**AI can help**:

- Break down complex features
- Suggest implementation approach
- Identify potential issues

**Prompt example**:

```text
Role: Technical architect
Task: Break down [feature name] into implementable tasks
Constraints:
- Each task should be completable in 1-2 hours
- Tasks should have clear dependencies
- Include acceptance criteria for each
Inputs: [Feature requirements, existing codebase structure]
Output format: List of tasks with acceptance criteria
Evaluation: Tasks are clear, ordered, and testable
```

### Step 2: Design (if needed)

**Goal**: Create a brief design for non-trivial features

**When to skip**: Simple features that follow existing patterns

**AI can help**:

- Draft design documents
- Suggest architecture patterns
- Identify risks

**Prompt example**:

```text
Role: Tech lead
Task: Draft design for [feature name]
Constraints:
- Include: goals, data model, API/interface, risks, test strategy
- Keep it practical (1-2 pages)
- Reference existing patterns where possible
Inputs: [Requirements, existing architecture]
Output format: Markdown design document
Evaluation: Design addresses goals and has concrete acceptance criteria
```

### Step 3: Implement

**Goal**: Write the code

**AI can help**:

- Generate implementation
- Follow existing patterns
- Handle edge cases

**Prompt example**:

```text
Role: Senior developer
Task: Implement [specific feature/task]
Constraints:
- Follow patterns in [reference files]
- Include error handling
- Add type annotations/hints
- Keep changes minimal and focused
Inputs: [Design doc, existing code patterns, relevant files]
Output format: Complete implementation
Evaluation: [Specific acceptance criteria]
```

### Step 4: Test

**Goal**: Verify it works

**AI can help**:

- Write tests
- Identify edge cases
- Create test data

**Prompt example**:

```text
Role: Test engineer
Task: Write tests for [feature/function]
Constraints:
- Use [testing framework from project]
- Cover: happy path, edge cases, error cases
- Follow existing test patterns
Inputs: [Code to test, existing test examples]
Output format: Test file with comprehensive coverage
Evaluation: Tests pass and cover all scenarios
```

### Step 5: Review

**Goal**: Ensure quality and correctness

**AI can help**:

- Review code for issues
- Suggest improvements
- Check for security problems

**Prompt example**:

```text
Role: Code reviewer
Task: Review this code for correctness, security, and quality
Constraints:
- Focus on: bugs, security issues, performance, maintainability
- Suggest specific improvements
- Avoid nitpicks
Inputs: [Code diff or files]
Output format: Bullet list of findings with severity
Evaluation: Actionable feedback, no false positives
```

### Step 6: Refine

**Goal**: Improve based on feedback

**AI can help**:

- Implement suggested improvements
- Refactor for clarity
- Optimize performance

**Prompt example**:

```text
Role: Refactoring specialist
Task: Improve [specific aspect] of this code
Constraints:
- Preserve exact behavior
- Improve [readability/performance/maintainability]
- Keep tests passing
Inputs: [Code to improve, review feedback]
Output format: Improved code + explanation
Evaluation: Tests pass, code improved, no regressions
```

### Step 7: Document

**Goal**: Update documentation

**AI can help**:

- Write documentation
- Update README
- Create examples

**Prompt example**:

```text
Role: Technical writer
Task: Document [feature/API/function]
Constraints:
- Include: purpose, usage examples, parameters, return values
- Follow existing documentation style
- Add code examples
Inputs: [Code to document, existing docs]
Output format: Documentation in project's format
Evaluation: Documentation is clear and complete
```

## Prompt patterns for every stage

### Pattern: Create new feature

```text
Role: Full-stack developer
Task: Implement [feature name]

Requirements:
- [Specific requirement 1]
- [Specific requirement 2]
- [Specific requirement 3]

Constraints:
- Follow patterns in [reference files]
- Use [specific libraries/frameworks]
- Include error handling
- Add tests
- Update documentation

Inputs:
- Design: [design doc or description]
- Patterns: [reference files]
- Related code: [file paths]

Output format: 
- Implementation files
- Test files
- Documentation updates

Evaluation:
- [ ] Feature works as specified
- [ ] Tests pass
- [ ] Follows project patterns
- [ ] Documentation updated
```

### Pattern: Add new endpoint/route

```text
Role: Backend developer
Task: Add [endpoint name] endpoint

Endpoint specification:
- Method: [GET/POST/PUT/DELETE]
- Path: [path]
- Request body: [structure]
- Response: [structure]
- Status codes: [list]

Constraints:
- Follow existing API patterns in [reference files]
- Include input validation
- Add error handling
- Write unit tests
- Update API documentation

Inputs:
- Existing API examples: [file paths]
- Data models: [file paths]
- Validation patterns: [file paths]

Output format: Complete endpoint implementation + tests
Evaluation: Endpoint works, tests pass, follows patterns
```

### Pattern: Create UI component

```text
Role: Frontend developer
Task: Create [component name] component

Component specification:
- Purpose: [what it does]
- Props: [list of props]
- Behavior: [how it works]
- Styling: [styling requirements]

Constraints:
- Use [framework/library] patterns
- Follow existing component structure
- Include accessibility features
- Add unit tests
- Make it responsive

Inputs:
- Similar components: [file paths]
- Design: [design reference]
- Styling system: [CSS/styling files]

Output format: Component file + test file + styles
Evaluation: Component works, tests pass, accessible
```

### Pattern: Refactor code

```text
Role: Refactoring specialist
Task: Refactor [code] for [goal: readability/performance/maintainability]

Current issues:
- [Issue 1]
- [Issue 2]

Desired improvements:
- [Improvement 1]
- [Improvement 2]

Constraints:
- Preserve exact behavior (no functional changes)
- Keep same public API/interface
- Maintain or improve test coverage
- Follow [project standards]

Inputs: [Code to refactor, tests]
Output format: Refactored code + updated tests + explanation
Evaluation: 
- All tests pass
- Behavior unchanged
- Code improved
- No performance regression
```

### Pattern: Fix bug

```text
Role: Debugging specialist
Task: Fix [bug description]

Bug details:
- Symptoms: [what happens]
- Steps to reproduce: [list]
- Expected behavior: [what should happen]
- Actual behavior: [what actually happens]
- Error message: [if any]

Constraints:
- Minimal fix (don't refactor unrelated code)
- Preserve existing behavior for other cases
- Add regression test
- Explain root cause

Inputs:
- Error logs: [paste error]
- Stack trace: [paste trace]
- Affected code: [file paths]
- Related code: [file paths]

Output format:
1. Root cause explanation
2. Minimal fix
3. Regression test
4. Verification steps

Evaluation:
- Bug is fixed
- Test passes
- No regressions
- Root cause explained
```

### Pattern: Write tests

```text
Role: Test engineer
Task: Write comprehensive tests for [component/function/feature]

What to test:
- Happy path: [normal usage]
- Edge cases: [boundary conditions]
- Error cases: [failure scenarios]
- Integration: [if applicable]

Constraints:
- Use [testing framework] from project
- Follow existing test patterns
- Achieve good coverage
- Tests should be maintainable

Inputs:
- Code to test: [file paths]
- Existing tests: [file paths for examples]
- Test utilities: [file paths]

Output format: Complete test file
Evaluation:
- All tests pass
- Good coverage
- Tests are clear and maintainable
```

### Pattern: Code review

```text
Role: Senior code reviewer
Task: Review this code change

Focus areas:
- Correctness: Does it work correctly?
- Security: Any security issues?
- Performance: Any performance concerns?
- Maintainability: Is it easy to understand?
- Testing: Are tests adequate?

Constraints:
- Provide actionable feedback
- Prioritize critical issues
- Avoid nitpicks
- Suggest specific improvements

Inputs: [Code diff or files]
Output format:
- Critical issues (must fix)
- Important issues (should fix)
- Suggestions (nice to have)
- Overall assessment

Evaluation: Feedback is actionable and prioritized
```

## Real-world examples

### Example 1: Adding user authentication

**Step 1: Plan**

```text
Role: Technical architect
Task: Break down user authentication into implementable tasks
Constraints:
- Include: registration, login, password reset, session management
- Each task should be independent and testable
Inputs: [Project structure, existing auth patterns if any]
Output: List of tasks with acceptance criteria
```

**Step 2: Implement registration**

```text
Role: Backend developer
Task: Implement user registration endpoint
Constraints:
- Validate email format
- Hash password before storing
- Return appropriate status codes
- Follow existing API patterns
Inputs: [User model, existing API examples]
Output: Registration endpoint + tests
```

**Step 3: Implement login**

```text
Role: Backend developer
Task: Implement login endpoint
Constraints:
- Verify password hash
- Generate session token
- Handle invalid credentials
- Follow existing auth patterns
Inputs: [User model, registration code, auth examples]
Output: Login endpoint + tests
```

Continue with remaining steps...

### Example 2: Creating a data table component

**Step 1: Design**

```text
Role: Frontend architect
Task: Design data table component
Constraints:
- Include: sorting, filtering, pagination
- Make it reusable
- Consider accessibility
Inputs: [Design requirements, existing component patterns]
Output: Component design
```

**Step 2: Implement**

```text
Role: Frontend developer
Task: Implement data table component
Constraints:
- Use [framework] patterns
- Include sorting, filtering, pagination
- Make it accessible
- Add unit tests
Inputs: [Design, similar components, styling system]
Output: Component + tests + styles
```

### Example 3: Refactoring a large function

**Step 1: Analyze**

```text
Role: Code analyst
Task: Analyze this function and suggest refactoring approach
Constraints:
- Identify extraction opportunities
- Suggest improvements
- Preserve behavior
Inputs: [Function code]
Output: Refactoring plan
```

**Step 2: Refactor**

```text
Role: Refactoring specialist
Task: Refactor function based on analysis
Constraints:
- Extract smaller functions
- Improve readability
- Keep tests passing
Inputs: [Function code, refactoring plan, tests]
Output: Refactored code + updated tests
```

## Testing and quality assurance

### Testing strategy

1. **Unit tests**: Test individual functions/components
2. **Integration tests**: Test how parts work together
3. **E2E tests**: Test complete user flows
4. **Regression tests**: Prevent bugs from returning

### AI for testing

**Write tests**:

```text
Role: Test engineer
Task: Write unit tests for [function/component]
Constraints:
- Cover: happy path, edge cases, error cases
- Use [testing framework]
- Follow existing test patterns
Inputs: [Code to test, existing tests]
Output: Test file
```

**Identify edge cases**:

```text
Role: QA specialist
Task: Identify edge cases for [feature]
Constraints:
- Consider: boundary conditions, error scenarios, edge inputs
Inputs: [Feature specification, code]
Output: List of edge cases to test
```

### Quality checks

- **Linters**: Run linters on AI-generated code
- **Type checking**: Verify types are correct
- **Security scanning**: Check for security issues
- **Performance**: Verify no performance regressions

## Security and compliance

### Security practices

✅ **Do**:

- Never paste secrets in prompts
- Use environment variables
- Validate all inputs
- Sanitize outputs
- Review security-sensitive code carefully

❌ **Don't**:

- Hardcode credentials
- Trust AI-generated security code without review
- Skip security reviews
- Ignore security warnings

### Compliance

- **Licenses**: Check licenses of AI-suggested code
- **Privacy**: Ensure PII handling is compliant
- **Standards**: Follow your organization's standards
- **Documentation**: Document security decisions

## Troubleshooting

### Issue: AI changes too much

**Problem**: AI modifies unrelated files or makes unnecessary changes

**Solutions**:

- Select specific code before prompting
- Add constraint: "Only modify [specific files]"
- Be explicit about scope
- Use project rules (Chapter 00)

### Issue: Generated code doesn't work

**Problem**: AI-generated code has bugs or doesn't compile

**Solutions**:

- Always test AI output
- Provide more specific constraints
- Reference working examples
- Break complex requests into smaller steps
- Ask AI to fix specific errors

### Issue: Code doesn't follow patterns

**Problem**: AI suggests code that doesn't match your architecture

**Solutions**:

- Provide more context about your patterns
- Reference existing code examples
- Use project rules (Chapter 00)
- Be explicit about your stack and conventions

### Issue: Tests are missing or inadequate

**Problem**: AI doesn't write enough tests or tests are wrong

**Solutions**:

- Explicitly request comprehensive tests
- Provide examples of existing tests
- Specify test coverage requirements
- Review and refine test prompts

### Issue: Documentation is incomplete

**Problem**: AI-generated documentation is missing information

**Solutions**:

- Specify what documentation is needed
- Provide examples of existing docs
- Request specific sections
- Review and refine documentation prompts

## Exercises

### Exercise 1: Complete feature implementation

**Goal**: Implement a feature using the full workflow

**Steps**:

1. Choose a small feature
2. Plan using AI
3. Design (if needed)
4. Implement with AI
5. Write tests with AI
6. Review with AI
7. Refine based on feedback
8. Document

**Deliverable**: Complete feature with tests and documentation

### Exercise 2: TDD workflow

**Goal**: Practice test-driven development with AI

**Steps**:

1. Write tests first (using AI)
2. Implement to make tests pass (using AI)
3. Refactor (using AI)
4. Repeat

**Deliverable**: Feature built using TDD with AI

### Exercise 3: Refactoring workflow

**Goal**: Refactor code using AI assistance

**Steps**:

1. Identify code to refactor
2. Analyze with AI
3. Plan refactoring
4. Refactor with AI
5. Verify tests pass
6. Review improvements

**Deliverable**: Refactored code with improved quality

### Exercise 4: Bug fix workflow

**Goal**: Fix a bug using AI

**Steps**:

1. Reproduce bug
2. Debug with AI
3. Fix with AI
4. Add regression test
5. Verify fix

**Deliverable**: Fixed bug with regression test

## Knowledge check (self-assessment)

Before moving forward, verify you can:

- [ ] Use AI for all stages of development
- [ ] Apply appropriate prompt patterns for each stage
- [ ] Write effective prompts for your tasks
- [ ] Test AI-generated code
- [ ] Review and refine AI output
- [ ] Follow security best practices
- [ ] Troubleshoot common issues

## Checkpoint: What should work

After completing this chapter, you should be able to:

✅ Use AI for planning, design, implementation, testing, and review  
✅ Apply appropriate prompt patterns for each development stage  
✅ Build complete features using AI assistance  
✅ Write comprehensive tests with AI  
✅ Review and refine AI-generated code  
✅ Follow security and quality practices  
✅ Troubleshoot common AI development issues

**If you can do all of these**, you're ready for Chapter 06 (the final project)!

## References

- Your project's testing framework documentation
- Your organization's coding standards
- Security best practices (OWASP, etc.)
- Your technology stack documentation

## Resources

- [Cursor Learn — Official Course](https://cursor.com/learn)
- [Cursor Directory: Rules](https://cursor.directory/rules)
