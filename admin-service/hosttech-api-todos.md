# Hosttech API Integration - TODOs & Analysis

## Overview of the `hosttech-api` Branch

The `hosttech-api` branch aims to integrate Hosttech DNS API management into the system, enabling administrative users to view DNS zones and perform automated DNS record operations (such as cleaning up ACME challenge records for SSL certificates).

---

## What Has Been Completed

1. **OpenAPI Specification & Code Generation**:
   - Added OpenAPI 3.0 specification for Hosttech DNS API (`admin-service/src/main/resources/domain/hosttech_api.yaml`).
   - Standardized YAML schema indentation for `TLSARecord` and other models.
   - Configured `openapi-generator-maven-plugin` in `admin-service/pom.xml` to generate Java/Kotlin client APIs (`com.michibaum.admin_service.app.domain.api` and `com.michibaum.admin_service.app.domain.model`).

2. **Backend API Integration (`admin-service`)**:
   - Created `HosttechAuthContext` to manage thread-local Hosttech authentication tokens.
   - Created `HosttechConfiguration` to configure `ApiClient` targeting `https://api.ns1.hosttech.eu` with Bearer token authentication headers.
   - Created `HosttechDomainService` with initial support for:
     - `getAllZones(token: String)`: Fetches DNS zones from Hosttech.
     - `removeAllAcmeChallengeRecords(token: String)`: Automated scanning across all zones to remove `_acme-challenge` TXT records.
   - Created `HosttechRestController` exposing Spring REST endpoints:
     - `GET /api/hosttech/zones`
     - `POST /api/hosttech/remove-acme-challenge-records`
   - Added unit test coverage in `HosttechAuthContextTest`, `HosttechDomainServiceTest`, and `HosttechRestControllerTest`.
   - Refactored `KubernetesClusterController` to `KubernetesClusterRestController`.

3. **Frontend Routing & Navigation Setup (`website`)**:
   - Replaced legacy `CookieAdminComponent` with `DnsComponent`.
   - Configured route `/dns` in `app.routes.ts` protected by `ADMIN_SERVICE` permissions.
   - Configured navigation entry `Sides.dns` in `sides.ts` and updated `navigation.component.ts`.

---

## What Is Unfinished & Needs To Be Done

### 1. Frontend Implementation (`website`)
- **UI Component (`DnsComponent`)**:
  - `website/src/app/dns/dns.component.ts` is currently an empty class stub.
  - `website/src/app/dns/dns.component.html` is empty.
  - Component template and logic must be implemented to display DNS zones, list zone records, and provide user actions (e.g., triggering ACME challenge cleanup).
- **Angular HTTP Service**:
  - Implement an Angular service (e.g., `DnsService` / `HosttechService`) to communicate with backend endpoints `/api/hosttech/zones` and `/api/hosttech/remove-acme-challenge-records`.
- **i18n Translation Keys**:
  - Routes and navigation reference `dns.title`, `dns.tab-title`, and `dns.meta-description`.
  - Add missing keys to all translation files (`en.json`, `de.json`, `es.json`, `zh.json`).

### 2. Backend Implementation (`admin-service`)
- **Granular DNS Record Management**:
  - Implement endpoints and service methods for fetching individual zone records (A, AAAA, CNAME, MX, TXT) and CRUD operations (add, edit, delete specific records).
- **Pagination & Retrieval Limits**:
  - `getAllZones` currently calls `zonesApi.apiUserV1ZonesGet("*", 100, 0)` with a hardcoded limit of 100 and no handling for multi-page pagination when accounts hold more than 100 zones.
- **Error & Exception Handling**:
  - Add custom exception mapping or fallback mechanisms for upstream Hosttech API errors (e.g., expired tokens, 401/403 auth failures, rate-limiting).

---

## Recommended Action Plan

1. **Frontend**: Implement `DnsService`, build out `DnsComponent` UI & HTML template, and add missing translation strings in `i18n/*.json`.
2. **Backend**: Implement CRUD methods for DNS records in `HosttechDomainService` / `HosttechRestController` and add pagination support.
3. **Verification**: Run end-to-end integration tests between frontend and backend against the Hosttech API.
