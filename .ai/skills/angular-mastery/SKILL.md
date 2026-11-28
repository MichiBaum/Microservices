---
name: "angular-mastery"
description: "Advanced knowledge of Angular framework, components, signals, and RxJS. Use when building or modifying frontend components, services, routing, or state management in the website/ module."
---
# Angular Mastery

## When to Use
- Creating or modifying Angular components, services, or modules in `website/`.
- Working with RxJS observables, signals, or state management.
- Configuring routing, guards, or interceptors.
- Optimizing frontend performance or bundle size.

## Angular Architecture
- The frontend lives in `website/` and uses Angular with standalone components.
- Use Angular Signals for reactive state where possible (preferred over RxJS for local state).
- Use RxJS for async streams, HTTP calls, and complex event handling.
- Keep components small and focused; extract shared logic into services.

## Component Design
- Use standalone components (no NgModules for new components).
- Use `OnPush` change detection strategy for all new components.
- Keep templates clean; move complex logic to the component class or a service.
- Use `@Input()` and `@Output()` for parent-child communication.
- Use services with dependency injection for cross-component state.

## Routing & Navigation
- Define routes lazily using `loadComponent` for code splitting.
- Use route guards for authentication and authorization checks.
- Use route resolvers for pre-fetching data when appropriate.

## HTTP & API Communication
- Use Angular `HttpClient` for all API calls.
- Centralize API base URLs in environment configuration.
- Use HTTP interceptors for authentication tokens and error handling.
- **Resource APIs**: When using `rxResource()` or `httpResource()`, strictly adhere to documented signatures (`loader: () => ...` returning an Observable). Never pass speculative properties such as `request` that trigger compiler `TS2769` errors.

## Image & Asset Optimization
- Use `NgOptimizedImage` (`ngSrc`) for all non-trivial images with explicit `width`/`height` or `fill`.
- Ensure custom image loaders correctly support image placeholders and remote CDN endpoints.

## Constraints & Verification
- All frontend commands run from the `website/` directory.
- Install dependencies: `npm install`
- Start dev server: `npm start`
- Run tests: `npm test`
- Build: `npm run build`
- Always verify TypeScript compiler status (`npm run build` or `npm test`) before concluding frontend changes.
