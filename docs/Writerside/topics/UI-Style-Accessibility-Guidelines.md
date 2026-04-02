# UI Style and Accessibility Guidelines

This guide provides UI, UX, and accessibility standards for both developers and AI assistants (like Junie) working on this project.

## 1. Mobile-First & Responsive Design

### Never Use Hover
- **Strict Rule:** Do not use Tailwind's `hover:` utility class for any UI element. 
- **Reason:** Hover states are non-existent or unreliable on mobile/touch devices. Interactive elements should rely on clear visual markers, active states, or focus states instead.

### Responsive Breakpoints
Use the following custom breakpoints defined in `website/tailwind.config.js`:
- `mobile`: 640px
- `tablet`: 768px
- `laptop`: 1024px
- `desktop`: 1280px
- `lScreen`: 1536px
- `xlScreen`: 1824px
- `4k`: 2560px

**Preferred Pattern for Lists:**
- Use card-based grids instead of large data tables for better readability on small screens.
- Example:
  ```html
  <div class="grid grid-cols-1 tablet:grid-cols-2 laptop:grid-cols-3 gap-4">
      @for (item of items; track item.id) {
          <p-card>...</p-card>
      }
  </div>
  ```

## 2. Accessibility (A11y)

### Semantic HTML
- Always use proper heading hierarchies (`h1`, `h2`, `h3`).
- Use `<nav>`, `<header>`, `<main>`, `<section>`, and `<article>` appropriately.

### ARIA & Screen Readers
- Provide `aria-label` for buttons or links that only contain icons.
- Ensure all interactive PrimeNG components have meaningful labels.
- Use `role="button"` on div elements if they act as buttons (though `<p-button>` is preferred).

### Text Truncation
- When using Tailwind's `truncate` class, **always** include a `title` attribute with the full text to ensure accessibility for both mouse and screen reader users.
- Example: `<span class="truncate" [title]="player.name">{{player.name}}</span>`

### Color & Contrast
- Use `surface` variables from PrimeUI for background and text colors to ensure proper contrast in both light and dark modes.
- Example: `bg-surface-50 dark:bg-surface-900/50` for containers.

## 3. Visual Consistency & Components

### PrimeNG + Tailwind
- Leverage PrimeNG components (`p-card`, `p-button`, `p-tabs`, `p-fieldset`) for core functionality.
- Customize layout and spacing with Tailwind CSS.
- Prefer `variant="outlined"` for secondary buttons to reduce visual weight.

### Icons
- Use FontAwesome (`fa-icon`) for visual context.
- Standardized icons:
  - `faCalendarAlt` for dates/times.
  - `faLocationDot` for addresses/locations.
  - `faGlobe` for international/federation info.
  - `faExternalLinkAlt` for links leading outside the app.

### Internationalization (i18n)
- Never hardcode text in templates. Use the `translate` pipe: `{{ 'key' | translate }}`.
- Ensure accuracy in translations (e.g., using "Weiß" instead of "Weiss" in German).

## 4. Layout & Spacing
- Use a standard `gap-4` for grid items.
- Ensure sections have consistent padding (`p-4` or `p-6`).
- Remove redundant outer cards if a component is already nested within a main layout area; use flat containers with custom headers instead.

## 5. Guidance for LLMs
- When suggesting UI changes, prioritize accessibility and responsiveness.
- Check for existing UI patterns in `website/src/app/` to ensure consistency.
- Avoid suggesting `hover:` effects even if it's a standard practice in other projects.
