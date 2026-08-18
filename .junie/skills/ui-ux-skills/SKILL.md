---
name: "ui-ux-skills"
description: "Guidelines for building mobile-first, accessible user interfaces. Use when designing layouts, implementing responsive components, or reviewing UI code for accessibility and mobile compatibility."
---
# UI & UX Skills

## When to Use
- Designing or reviewing page layouts and component structure.
- Implementing responsive designs that work across screen sizes.
- Ensuring accessibility compliance (ARIA, keyboard navigation, screen readers).
- Reviewing UI code for mobile-first best practices.

## Mobile-First Design Rules
- **NEVER use `hover:` classes** in Tailwind or CSS. All interactions must be touch-friendly.
- Design for small screens first, then enhance for larger screens using responsive breakpoints.
- Use `focus:` and `active:` states instead of hover for interactive feedback.
- Ensure all interactive elements have a minimum touch target of 44x44px.

## Accessibility Standards
- Use semantic HTML elements (`<nav>`, `<main>`, `<article>`, `<button>`, etc.) instead of generic `<div>`.
- Add ARIA labels (`aria-label`, `aria-describedby`, `aria-live`) for dynamic content and non-text elements.
- Ensure all form inputs have associated `<label>` elements.
- Maintain sufficient color contrast ratios (WCAG AA minimum: 4.5:1 for text).
- Support full keyboard navigation: all interactive elements must be focusable and operable via keyboard.

## Responsive Layout Guidelines
- Use CSS Grid or Flexbox for layouts; avoid fixed pixel widths.
- Use project-specific Tailwind breakpoints for responsive behavior.
- Test layouts at mobile (320px), tablet (768px), and desktop (1024px+) widths.
- Images and media must be responsive (`max-w-full`, `h-auto`).

## Constraints
- Refer to `docs/Writerside/topics/UI-Style-Accessibility-Guidelines.md` for the full UI style guide.
- Do not add custom CSS unless Tailwind utilities are insufficient.
- Do not use pixel-based font sizes; use Tailwind's type scale.
