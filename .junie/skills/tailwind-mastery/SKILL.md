---
name: "tailwind-mastery"
description: "Expertise in utility-first CSS using Tailwind CSS. Use when styling components, implementing responsive layouts, or configuring Tailwind theme and breakpoints."
---
# Tailwind Mastery

## When to Use
- Styling new or existing Angular components.
- Implementing responsive layouts with Tailwind utility classes.
- Configuring or extending the Tailwind theme (colors, spacing, breakpoints).
- Reviewing CSS/styling code for consistency and best practices.

## Utility-First Approach
- Use Tailwind utility classes directly in templates instead of writing custom CSS.
- Compose complex styles by combining multiple utility classes.
- Extract repeated patterns into Angular components, not CSS classes.
- Only use `@apply` in rare cases where component extraction is not feasible.

## Responsive Design with Tailwind
- Use mobile-first breakpoint prefixes: `sm:`, `md:`, `lg:`, `xl:`, `2xl:`.
- Default styles (no prefix) apply to mobile; add prefixes for larger screens.
- Use project-specific breakpoints defined in `tailwind.config.js`.

## Critical Rules
- **NEVER use `hover:` prefix.** This project is mobile-first; use `focus:` and `active:` instead.
- Prefer Tailwind's spacing scale (`p-4`, `m-2`, `gap-3`) over arbitrary values (`p-[17px]`).
- Use Tailwind's color palette consistently; do not introduce arbitrary hex colors.

## Common Patterns
- Flexbox: `flex items-center justify-between gap-4`
- Grid: `grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6`
- Typography: `text-sm md:text-base font-medium text-gray-700`
- Spacing: `p-4 md:p-6 lg:p-8`
