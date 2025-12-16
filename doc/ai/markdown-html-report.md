# Markdown to HTML Conversion Report for AI-Driven Development Course

## Purpose

This document describes, in detail, how the course content in Markdown format under `doc/course` was converted into HTML
under `html/course`.

It is written to be:

- **A precise, reproducible runbook** for re-converting updated Markdown content.
- **A reliable prompt** for an AI assistant, avoiding hallucinations by grounding every step in concrete file paths and
  commands that were actually used.

All steps and file mappings below reflect operations already performed in this repository.

---

## Source and target locations

- **Source root (Markdown):** `doc/course`
- **Target root (HTML):** `html/course`

The directory structure under `html/course` mirrors the structure under `doc/course`, with each `*.md` file converted to
a corresponding `*.html` file in the same relative path.

---

## Enumerated source Markdown files

At the time of conversion, the following Markdown files existed under `doc/course`:

1. `doc/course/README.md`
2. `doc/course/00-rules-and-prompt-optimization/README.md`
3. `doc/course/00-rules-and-prompt-optimization/starter-prompt-template.md`
4. `doc/course/01-introduction-to-ai/README.md`
5. `doc/course/02-agents/README.md`
6. `doc/course/03-setup-cursor-ai/README.md`
7. `doc/course/04-setup-context7/README.md`
8. `doc/course/05-ai-driven-software-development/README.md`
9. `doc/course/06-project-confluence-publisher-app/README.md`
10. `doc/course/06-project-confluence-publisher-app/06.1-milestone-1-scaffold.md`
11. `doc/course/06-project-confluence-publisher-app/06.2-milestone-2-core-flows.md`
12. `doc/course/06-project-confluence-publisher-app/06.3-milestone-3-ai-features.md`
13. `doc/course/06-project-confluence-publisher-app/06.4-milestone-4-polishing.md`

These were identified using:

```bash
find doc/course -name '*.md'
```

All paths in this report refer to the repository root:

```bash
/Users/andrey.karazhev/Developer/training/ai-driven-development-101
```

---

## Enumerated target HTML files

After conversion, the following HTML files exist under `html/course`:

1. `html/course/README.html`
2. `html/course/00-rules-and-prompt-optimization/README.html`
3. `html/course/00-rules-and-prompt-optimization/starter-prompt-template.html`
4. `html/course/01-introduction-to-ai/README.html`
5. `html/course/02-agents/README.html`
6. `html/course/03-setup-cursor-ai/README.html`
7. `html/course/04-setup-context7/README.html`
8. `html/course/05-ai-driven-software-development/README.html`
9. `html/course/06-project-confluence-publisher-app/README.html`
10. `html/course/06-project-confluence-publisher-app/06.1-milestone-1-scaffold.html`
11. `html/course/06-project-confluence-publisher-app/06.2-milestone-2-core-flows.html`
12. `html/course/06-project-confluence-publisher-app/06.3-milestone-3-ai-features.html`
13. `html/course/06-project-confluence-publisher-app/06.4-milestone-4-polishing.html`

These were verified using:

```bash
find html/course -name '*.html' | sort
```

There is a strict 1:1 mapping between the 13 source `*.md` files and the 13 generated `*.html` files, with matching
relative paths and file basenames.

---

## Conversion tooling

- **Converter:** `pandoc`
- **Version (as detected on this machine):**

  ```bash
  pandoc --version
  ```

  Example output at the time of this report:

  ```
  pandoc 3.8.2.1
  Features: +server +lua
  Scripting engine: Lua 5.4
  ```

- **Conversion mode:** Standalone HTML documents, i.e., each output file is a complete HTML page with:
    - `<!DOCTYPE html>`
    - `<html>`, `<head>`, `<body>`
    - Default Pandoc HTML5 CSS in the `<style>` block

No custom templates, CSS overrides, or additional options were used beyond the defaults shown in the commands below.

---

## Core conversion procedure

### High-level description

For every `*.md` file under `doc/course`:

1. Compute the destination path under `html/course` by replacing the `doc/` prefix with `html/` and the `.md` extension
   with `.html`.
2. Ensure the destination directory exists (create it if necessary).
3. Run `pandoc` to convert the Markdown file to a standalone HTML file.

### Exact shell pipeline

From the repository root (`/Users/andrey.karazhev/Developer/training/ai-driven-development-101`), the following command
was executed:

```bash
find doc/course -name '*.md' -print0 | \
  while IFS= read -r -d '' src; do
    dest="html/${src#doc/}"
    dest_dir="$(dirname "$dest")"
    mkdir -p "$dest_dir"
    pandoc -s "$src" -o "${dest%.md}.html"
  done
```

**Explanation of variables and steps:**

- `src`: Full path to a source Markdown file under `doc/course` (relative to repo root).
- `dest`: The target path under `html/` with the same relative structure as `src`.
    - `${src#doc/}` strips the leading `doc/` path segment from the source.
    - Prefixing with `html/` gives a mirrored path under `html/`.
- `dest_dir`: Directory portion of `dest`, created with `mkdir -p` to ensure the path exists.
- `pandoc -s "$src" -o "${dest%.md}.html"`:
    - `-s` tells Pandoc to produce a standalone HTML document.
    - `${dest%.md}.html` replaces the `.md` extension with `.html` for the output file.

This loop ensures that the directory tree and filenames are mirrored precisely from `doc/course` to `html/course`.

---

## Source → target mapping (explicit)

For clarity, here is the explicit mapping applied during conversion:

- `doc/course/README.md`
  → `html/course/README.html`

- `doc/course/00-rules-and-prompt-optimization/README.md`
  → `html/course/00-rules-and-prompt-optimization/README.html`

- `doc/course/00-rules-and-prompt-optimization/starter-prompt-template.md`
  → `html/course/00-rules-and-prompt-optimization/starter-prompt-template.html`

- `doc/course/01-introduction-to-ai/README.md`
  → `html/course/01-introduction-to-ai/README.html`

- `doc/course/02-agents/README.md`
  → `html/course/02-agents/README.html`

- `doc/course/03-setup-cursor-ai/README.md`
  → `html/course/03-setup-cursor-ai/README.html`

- `doc/course/04-setup-context7/README.md`
  → `html/course/04-setup-context7/README.html`

- `doc/course/05-ai-driven-software-development/README.md`
  → `html/course/05-ai-driven-software-development/README.html`

- `doc/course/06-project-confluence-publisher-app/README.md`
  → `html/course/06-project-confluence-publisher-app/README.html`

- `doc/course/06-project-confluence-publisher-app/06.1-milestone-1-scaffold.md`
  → `html/course/06-project-confluence-publisher-app/06.1-milestone-1-scaffold.html`

- `doc/course/06-project-confluence-publisher-app/06.2-milestone-2-core-flows.md`
  → `html/course/06-project-confluence-publisher-app/06.2-milestone-2-core-flows.html`

- `doc/course/06-project-confluence-publisher-app/06.3-milestone-3-ai-features.md`
  → `html/course/06-project-confluence-publisher-app/06.3-milestone-3-ai-features.html`

- `doc/course/06-project-confluence-publisher-app/06.4-milestone-4-polishing.md`
  → `html/course/06-project-confluence-publisher-app/06.4-milestone-4-polishing.html`

---

## Content preservation and verification

### 1. File coverage check

To ensure no Markdown file was missed and no extra HTML file was created, the following checks were performed:

- List all Markdown sources:

  ```bash
  find doc/course -name '*.md'
  ```

- List all HTML outputs:

  ```bash
  find html/course -name '*.html' | sort
  ```

Then, the sets of paths were compared logically:

- Each `doc/course/.../*.md` has a corresponding `html/course/.../*.html`.
- There are exactly 13 Markdown files and 13 HTML files, with matching relative paths and basenames.

### 2. Spot-checking HTML content

Representative HTML files were opened and inspected to confirm that all content from the source Markdown was preserved:

- `html/course/README.html`
    - Contains the "AI-Driven Development Course" title.
    - Includes the introductory paragraph about the curriculum.
    - Lists all authors: "Alexey Mikhalchenkov", "Klim Izmaikov", "Andrey Karazhev".
    - Contains the syllabus with numbered items 0–6 and their descriptions.
    - Includes sections "How to use this course" and "Additional resources" with their bullet points and links.

- `html/course/06-project-confluence-publisher-app/README.html`
    - Contains the full description of the Confluence Publisher App project.
    - Includes sections for learning objectives, prerequisites, project overview, architecture overview, data model, API
      endpoints, testing strategy, milestones, tips, troubleshooting, knowledge check, next steps, references, and
      resources.
    - Renders inline code (e.g., API endpoints like `POST /api/pages`) as `<code>` elements.
    - Renders lists of user stories, non-goals, entities, and milestones accurately.

In both cases, all observed structure (headings, paragraphs, bullet lists, numbered lists, code/preformatted blocks,
task lists) matched the original Markdown semantics, simply expressed in HTML.

### 3. Behavior of internal links

Pandoc preserves the link destinations as they appear in the Markdown.

- Example from `doc/course/README.md` syllabus:
    - `./00-rules-and-prompt-optimization/README.md`
    - `./01-introduction-to-ai/README.md`
    - etc.

In the generated `html/course/README.html`, these links still point to the `.md` paths:

- `./00-rules-and-prompt-optimization/README.md`
- `./01-introduction-to-ai/README.md`
- etc.

**Important:** This report describes what actually happened. No automatic `.md` → `.html` link rewriting was done as
part of the Pandoc run.

If a future workflow requires internal links to point to `.html` files instead of `.md`, that will require an
additional, explicit post-processing step (not covered by the current procedure).

---

## How to re-run the conversion safely

This section can be used as an exact prompt or script for re-running the conversion when Markdown content is updated.

### Preconditions

1. You are in the repository root:

   ```bash
   cd /Users/andrey.karazhev/Developer/training/ai-driven-development-101
   ```

2. `pandoc` is installed and available in `PATH`.

   ```bash
   pandoc --version
   ```

3. Source Markdown files are located under `doc/course` with the same or similar structure.

### Recommended command (idempotent for updated content)

Run this from the repository root:

```bash
find doc/course -name '*.md' -print0 | \
  while IFS= read -r -d '' src; do
    dest="html/${src#doc/}"
    dest_dir="$(dirname "$dest")"
    mkdir -p "$dest_dir"
    pandoc -s "$src" -o "${dest%.md}.html"
  done
```

**Notes:**

- This will:
    - Create `html/course` and any necessary subdirectories if they do not already exist.
    - Overwrite existing `.html` files with updated conversions of the corresponding `.md` files.
- It is safe to re-run whenever Markdown content changes.

### Optional: limiting to changed files

If you want to convert only a specific Markdown file (for example, while iterating on one chapter), you can run:

```bash
src="doc/course/01-introduction-to-ai/README.md"
dest="html/${src#doc/}"
dest_dir="$(dirname "$dest")"
mkdir -p "$dest_dir"
pandoc -s "$src" -o "${dest%.md}.html"
```

Adjust `src` to point to the Markdown file you are updating.

---

## Potential future enhancements (not yet implemented)

The following are **not** part of the current implementation but are realistic next steps if needed. They are listed
here explicitly to avoid confusion between what *was done* and what *might be done*.

1. **Internal link rewriting**
    - Goal: Update internal links in generated HTML to point to `.html` files instead of `.md` files.
    - Example transformation (inside HTML):
        - `./01-introduction-to-ai/README.md` → `./01-introduction-to-ai/README.html`
    - This would require:
        - Either a Pandoc template/filters step, or
        - A post-processing script that rewrites `href` attributes in the generated HTML.

2. **Custom HTML template / styling**
    - Goal: Replace default Pandoc styling with a custom template or CSS.
    - Approach:
        - Provide a `--template` HTML file and/or `--css` file to Pandoc.

These enhancements are intentionally not described as completed work; they are potential extensions to the pipeline.

---

## Summary

- All Markdown course files under `doc/course` (13 files) were converted to HTML under `html/course` with a 1:1 path
  mapping.
- Conversion used `pandoc -s` in a `find` + `while` pipeline from the repository root.
- Verification confirmed:
    - Every `*.md` has a corresponding `*.html`.
    - Representative HTML outputs preserve headings, lists, code blocks, and other structure.
    - Internal links remain pointing to `.md` files, matching the original Markdown link targets.
- The commands and mappings in this report can be used as a precise prompt or runbook to re-convert updated Markdown
  content without guessing or hallucinating steps.
