# web — Frontend Web

React 18 + TypeScript + Vite. Tuân [.claude/rules/50-frontend-web.md](../.claude/rules/50-frontend-web.md) và [90-i18n-accessibility.md](../.claude/rules/90-i18n-accessibility.md).

## Thư viện chính
- UI: Ant Design 5 · State: TanStack Query + Zustand
- Bản đồ: MapLibre GL JS (+ deck.gl clustering) · Biểu đồ: Apache ECharts
- i18n: react-i18next (vi/EN) · Realtime: WebSocket (STOMP)/SSE
- Auth: Keycloak (OIDC) · API client sinh từ OpenAPI

## Lệnh (sau khi khởi tạo)
```bash
npm install
npm run dev        # dev server
npm run build      # build production
npm run test       # unit test (Vitest) + Playwright (E2E)
npm run lint && npm run typecheck
```

Trạng thái: skeleton — khởi tạo trong Phase 1 ([docs/PLAN.md](../docs/PLAN.md)).
