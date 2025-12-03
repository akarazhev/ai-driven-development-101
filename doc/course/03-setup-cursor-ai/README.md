# 03. Setup Cursor AI

Step-by-step guide to setting up Cursor for effective AI pair programming. Learn how to install, configure, and use
Cursor to maximize your productivity with AI assistance.

## Learning objectives

- Install Cursor and verify AI features work
- Configure workspace and project settings
- Set up your development environment
- Understand Cursor's key features and shortcuts
- Apply best practices for AI-assisted coding
- Troubleshoot common issues

## Prerequisites

- Basic familiarity with code editors
- A project to work on (any technology stack)
- Git installed (for version control)

## Outline

- Installation and first launch
- Workspace configuration
- Environment setup
- Key features and shortcuts
- Best practices
- Troubleshooting guide
- Ready-to-use configurations

## Installation and first launch

### Step 1: Download Cursor

1. Visit [cursor.sh](https://cursor.sh)
2. Click "Download" for your operating system
3. Run the installer
4. Follow installation prompts

**Note**: Cursor is available for macOS, Windows, and Linux.

### Step 2: First launch and sign-in

1. Launch Cursor
2. Sign in with your account (create one if needed)
3. Verify AI features are enabled:
    - Look for AI chat icon in sidebar
    - Check that "Composer" feature is available
    - Confirm you can access AI suggestions

**Screenshot placeholder**: [Add screenshot of Cursor welcome screen with AI features visible]

### Step 3: Verify installation

Test that AI features work:

1. Open any code file
2. Select some code
3. Press `Cmd+K` (Mac) or `Ctrl+K` (Windows/Linux)
4. You should see AI suggestions appear

If this works, installation is successful!

## Workspace configuration

### Step 1: Open your project

1. File → Open Folder (or `Cmd+O` / `Ctrl+O`)
2. Navigate to your project directory
3. Click "Open"

### Step 2: Trust the workspace

When opening a project, Cursor may ask:

- "Do you trust the authors of the files in this folder?"
- Click "Yes, I trust the authors"

**Why this matters**: Cursor needs to index your codebase to provide context-aware suggestions.

### Step 3: Enable indexing

1. Cursor will start indexing your project automatically
2. Check status in bottom-right corner
3. Wait for indexing to complete (may take a few minutes for large projects)

**Screenshot placeholder**: [Add screenshot showing indexing status]

### Step 4: Verify project structure

1. Check that file tree appears in sidebar
2. Try searching for files (`Cmd+P` / `Ctrl+P`)
3. Verify code navigation works

## Environment setup

### Setting up your development environment

Cursor works with any technology stack. Here's how to configure it:

### For any project:

1. **Select interpreter/runtime** (if applicable):
    - For Python: Select Python interpreter
    - For Node.js: Ensure Node is in PATH
    - For Java: Configure JDK
    - For other languages: Configure as needed

2. **Install dependencies**:
    - Use your project's package manager
    - Ensure dependencies are installed
    - Cursor will use these for context

3. **Configure build tools** (if needed):
    - Set up your build system
    - Configure test runners
    - Cursor can help with these tasks

### Example: Python project

```bash
# In your project directory
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

Then in Cursor:

1. `Cmd+Shift+P` → "Python: Select Interpreter"
2. Choose `.venv/bin/python`

### Example: Node.js project

```bash
# In your project directory
npm install
```

Cursor will automatically detect Node.js if it's in your PATH.

### Example: Java project

1. Ensure JDK is installed and in PATH
2. Cursor will detect Java projects automatically
3. Configure build tool (Maven/Gradle) as needed

## Key features and shortcuts

### Chat with AI

**Shortcut**: `Cmd+L` (Mac) or `Ctrl+L` (Windows/Linux)

- Ask questions about your code
- Get explanations
- Request code changes
- Debug issues

**Screenshot placeholder**: [Add screenshot of AI chat interface]

### Composer (Multi-file editing)

**Shortcut**: `Cmd+I` (Mac) or `Ctrl+I` (Windows/Linux)

- Edit multiple files at once
- Make coordinated changes across codebase
- Great for refactoring and feature implementation

**Screenshot placeholder**: [Add screenshot of Composer interface]

### Inline suggestions

**Shortcut**: `Tab` to accept, `Esc` to dismiss

- AI suggests code as you type
- Context-aware completions
- Press `Tab` to accept, `Esc` to dismiss

### Code selection and AI

**Shortcut**: Select code → `Cmd+K` (Mac) or `Ctrl+K` (Windows/Linux)

- Select specific code
- Press `Cmd+K` / `Ctrl+K`
- Ask AI to modify just that selection
- Keeps changes focused and minimal

**Screenshot placeholder**: [Add screenshot showing code selection with AI prompt]

### Command palette

**Shortcut**: `Cmd+Shift+P` (Mac) or `Ctrl+Shift+P` (Windows/Linux)

- Access all Cursor commands
- Search for features
- Configure settings

## Best practices

### 1. Work in small batches

✅ **Do**: Make focused, small changes  
❌ **Don't**: Ask AI to rewrite entire files at once

**Example**:

- ✅ "Add error handling to this function"
- ❌ "Refactor this entire module"

### 2. Select code before prompting

✅ **Do**: Select the specific code you want to change  
❌ **Don't**: Ask AI to find and modify code without context

**How**:

1. Select the code block
2. Press `Cmd+K` / `Ctrl+K`
3. Type your request
4. AI will only modify selected code

### 3. Review diffs carefully

✅ **Do**: Review all changes before accepting  
❌ **Don't**: Accept changes blindly

**Check**:

- Are changes what you asked for?
- Did AI modify unrelated code?
- Are there any obvious bugs?

### 4. Use clear, specific prompts

✅ **Do**: "Add input validation to this function"  
❌ **Don't**: "Fix this"

**Better prompts include**:

- What to do (specific task)
- Where to do it (file/function)
- How to do it (constraints, patterns)

### 5. Commit frequently

✅ **Do**: Commit after each successful change  
❌ **Don't**: Make many changes before committing

**Why**: Easier to revert if something goes wrong

### 6. Never paste secrets

✅ **Do**: Use environment variables, placeholders  
❌ **Don't**: Paste API keys, passwords, tokens

**Example**:

- ✅ "Use API_KEY from environment variables"
- ❌ "API key is abc123xyz"

## Model and parameter settings

### Choosing a model

Cursor offers different AI models. Choose based on your needs:

- **Claude/GPT-4**: Best for complex reasoning, large refactors
- **Claude Sonnet**: Good balance of speed and quality
- **GPT-3.5**: Faster, good for simple tasks

**How to change**:

1. Open settings (`Cmd+,` / `Ctrl+,`)
2. Search for "Model"
3. Select your preferred model

### Temperature settings

- **Low (0.0-0.3)**: More deterministic, consistent code
- **Medium (0.4-0.7)**: Balanced creativity and consistency
- **High (0.8-1.0)**: More creative, less predictable

**Recommendation**: Use low temperature (0.0-0.3) for code, higher for brainstorming

### Context window

- Larger context = more codebase understanding
- But also = slower responses and higher costs
- Default settings usually work well

## Ready-to-use prompt templates

### Template: Modify selected code

```text
Role: [Your role - e.g., Senior Developer]
Task: [Specific change]
Constraints:
- Only modify the selected code
- Follow existing patterns in [reference file]
- Include [specific requirements]
Inputs: [Selected code is the input]
Output format: Modified code only
Evaluation: [How to verify]
```

### Template: Explain code

```text
Explain this code:
- What does it do?
- How does it work?
- Are there any potential issues?
- How could it be improved?
```

### Template: Add feature

```text
Role: Full-stack developer
Task: Add [feature name]
Constraints:
- Follow patterns in [reference files]
- Include error handling
- Add tests
- Update documentation
Inputs: [Relevant files]
Output format: Complete implementation
Evaluation: [Acceptance criteria]
```

### Template: Fix bug

```text
Role: Debugging specialist
Task: Fix [bug description]
Constraints:
- Minimal change
- Preserve existing behavior
- Add regression test
Inputs: [Error message, stack trace, code]
Output format: Fix + test + explanation
Evaluation: Bug fixed, test passes
```

## Troubleshooting

### Issue: AI features not working

**Symptoms**: No AI suggestions, chat doesn't respond

**Solutions**:

1. Check internet connection
2. Verify you're signed in
3. Restart Cursor
4. Check Cursor status page for outages
5. Update Cursor to latest version

### Issue: Slow or no responses

**Symptoms**: AI takes very long to respond or times out

**Solutions**:

1. Reduce context window size
2. Select smaller code blocks
3. Simplify your prompt
4. Check your internet connection
5. Try a different model (some are faster)

### Issue: AI changes too much code

**Symptoms**: AI modifies files you didn't ask it to change

**Solutions**:

1. Select specific code before prompting
2. Add constraint: "Only modify [specific files]"
3. Be more explicit about scope
4. Review diffs before accepting
5. Use project rules (see Chapter 00) to set boundaries

### Issue: AI doesn't understand context

**Symptoms**: Suggestions don't match your project patterns

**Solutions**:

1. Provide more context in prompt
2. Reference existing code patterns
3. Use project rules (see Chapter 00)
4. Select relevant code before prompting
5. Be explicit about your stack and patterns

### Issue: Indexing not working

**Symptoms**: Cursor doesn't seem to understand your codebase

**Solutions**:

1. Wait for indexing to complete (check status)
2. Re-index: `Cmd+Shift+P` → "Index Workspace"
3. Check `.cursorignore` file (may be excluding files)
4. Ensure workspace is trusted
5. Restart Cursor

### Issue: Authentication errors

**Symptoms**: "Authentication failed" or "Please sign in"

**Solutions**:

1. Sign out and sign back in
2. Check your account status
3. Verify subscription/plan (if applicable)
4. Clear Cursor cache and restart
5. Contact Cursor support if persistent

### Issue: Code suggestions are wrong

**Symptoms**: AI suggests code that doesn't work or is incorrect

**Solutions**:

1. Always test AI-generated code
2. Provide more specific constraints
3. Reference working examples from your codebase
4. Break complex requests into smaller steps
5. Use evaluation criteria in prompts

### Issue: Can't find files

**Symptoms**: File search or navigation doesn't work

**Solutions**:

1. Wait for indexing to complete
2. Check if files are in `.gitignore` or `.cursorignore`
3. Re-index workspace
4. Verify workspace is opened correctly
5. Try using `Cmd+P` / `Ctrl+P` for file search

## Exercises

### Exercise 1: Complete setup

**Goal**: Get Cursor fully configured

**Steps**:

1. Install Cursor
2. Sign in
3. Open your project
4. Trust workspace
5. Wait for indexing
6. Test AI chat (`Cmd+L` / `Ctrl+L`)
7. Test code selection (`Cmd+K` / `Ctrl+K`)

**Deliverable**: Working Cursor setup with AI features enabled

### Exercise 2: First AI interaction

**Goal**: Use AI to modify code

**Steps**:

1. Open a file in your project
2. Select a function
3. Press `Cmd+K` / `Ctrl+K`
4. Ask: "Add error handling to this function"
5. Review the changes
6. Accept or refine

**Deliverable**: Successfully modified code using AI

### Exercise 3: Explore features

**Goal**: Learn Cursor's key features

**Steps**:

1. Try AI chat (`Cmd+L` / `Ctrl+L`)
2. Try Composer (`Cmd+I` / `Ctrl+I`)
3. Try inline suggestions (just type and wait)
4. Try command palette (`Cmd+Shift+P` / `Ctrl+Shift+P`)
5. Explore settings

**Deliverable**: Familiarity with Cursor's main features

### Exercise 4: Configure for your stack

**Goal**: Set up Cursor for your technology

**Steps**:

1. Identify your tech stack
2. Configure interpreter/runtime if needed
3. Install project dependencies
4. Test that Cursor understands your code
5. Create a simple rule (see Chapter 00) for your stack

**Deliverable**: Cursor configured and working with your stack

## Knowledge check (self-assessment)

Before moving forward, verify you can:

- [ ] Install and launch Cursor
- [ ] Open and trust a workspace
- [ ] Use AI chat (`Cmd+L` / `Ctrl+L`)
- [ ] Use code selection with AI (`Cmd+K` / `Ctrl+K`)
- [ ] Access Composer (`Cmd+I` / `Ctrl+I`)
- [ ] Troubleshoot common issues
- [ ] Configure Cursor for your tech stack

## Checkpoint: What should work

After completing this chapter, you should be able to:

✅ Cursor is installed and running  
✅ You can open your project in Cursor  
✅ AI chat responds to questions  
✅ Code selection with AI works  
✅ You can modify code using AI  
✅ Cursor understands your codebase (indexing complete)  
✅ You know how to troubleshoot common issues

**If all of these work**, you're ready for Chapter 04!

## References

- [Cursor official website](https://cursor.sh)
- [Cursor documentation](https://docs.cursor.sh)
- [Cursor keyboard shortcuts](https://docs.cursor.sh/shortcuts)
- Your project's technology documentation
- [Cursor Learn — Official Course](https://cursor.com/learn)
