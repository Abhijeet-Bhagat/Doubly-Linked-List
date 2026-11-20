# Nexus loading states — setup and per-page usage

Three layers, used together on every page:

| Layer | What the user sees | When |
|---|---|---|
| **1. Universal bar** `<nx-top-progress>` | 3px green (#368727) bar right under the context bar, creeping left→right, then completing and fading | Automatically on **every** HTTP call and route change. Appears only if the work takes >150ms; once shown stays ≥300ms (no flicker). |
| **2. Skeletons** `<nx-skeleton variant=…>` | Grey shimmering shapes that look like the rows/table/tree that is coming, **inside the panel that is loading** | First load of a panel, or when its data is replaced (new filter, new tab, new selection). |
| **3. Busy buttons** `[nxBusy]` | Spinner inside the button, same width, can't double-click | After the user clicks an action that calls the server (Compare, Run query, Approve, Submit…). |

Rules
- No full-page overlays or centred spinners. The page frame (header, title, filters) always renders immediately; only the data areas show skeletons.
- Never replace data that's already on screen with a skeleton just to refresh it in the background — use the top bar only (pass `SKIP_GLOBAL_LOADER` for silent polling).
- Keep the skeleton the **same size** as the real content (same row count you expect, same columns) so nothing jumps when data arrives.
- Remove plain "Loading…" text everywhere.
- Errors replace the skeleton with: `Couldn’t load this.` + link `Try again` (14px, #5C5C5C), inside the same panel.

---

## 1. Install (once)

Copy `src/app/shared/loading/` and `src/styles/_nexus-busy.scss` into your project.

**Global styles** — `src/styles.scss`:
```scss
@import 'styles/nexus-busy';
```

**Register the interceptor** — standalone app (`app.config.ts`):
```ts
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { LoadingInterceptor } from './shared/loading';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true },
  ],
};
```
NgModule app (`app.module.ts`): add `{ provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true }` to `providers`, and add `TopProgressComponent, SkeletonComponent, BusyDirective` to `imports` of the modules that use them (they're standalone).

**Shell** — `app.component.html`, directly after the header + context bar, before `<router-outlet>`:
```html
<app-header></app-header>
<app-context-bar></app-context-bar>
<nx-top-progress></nx-top-progress>
<router-outlet></router-outlet>
<app-draft-bar></app-draft-bar>
```

**Silent calls** (typeahead, polling notifications):
```ts
this.http.get(url, { context: new HttpContext().set(SKIP_GLOBAL_LOADER, true) });
```

---

## 2. Pattern for every data area

Give each panel its own state; don't use one `loading` flag for the whole page.

```ts
type Load<T> = { status: 'loading' | 'ready' | 'error'; data?: T };

attention = signal<Load<Request[]>>({ status: 'loading' });

loadAttention() {
  this.attention.set({ status: 'loading' });
  this.api.getAttention().subscribe({
    next: (data) => this.attention.set({ status: 'ready', data }),
    error: () => this.attention.set({ status: 'error' }),
  });
}
```
```html
<ng-container [ngSwitch]="attention().status">
  <nx-skeleton *ngSwitchCase="'loading'" variant="list" [rows]="4" label="Loading requests" />
  <div *ngSwitchCase="'error'" class="nx-load-error">Couldn’t load this. <a (click)="loadAttention()">Try again</a></div>
  <ng-container *ngSwitchCase="'ready'"><!-- real rows --></ng-container>
</ng-container>
```
```scss
.nx-load-error { padding: 20px; font-size: 14px; color: #5C5C5C; a { color: #2B6A1F; text-decoration: underline; cursor: pointer; } }
```

Skeleton variants: `list` (title + meta rows), `table` (header + rows×cols, optional `columns` grid string), `detail` (tag, title, facts grid, small table), `tree` (indented rows), `timeline` (dot + 2 lines), `cells` (snapshot strip), `facts` (label/value row).

---

## 3. Per page

### Home
| Area | Loading UI |
|---|---|
| Title row, search | render immediately (no skeleton) |
| Snapshot strip | `<nx-skeleton variant="cells" [cols]="4" />` inside the strip panel |
| Needs your attention | `variant="list" [rows]="4"` |
| Hierarchies | `variant="list" [rows]="5"` |
| Recent activity | `variant="timeline" [rows]="5"` (inside the panel, below its header) |
| Learn Nexus | static — no loader |
| Search button | `[nxBusy]="searching"` |

### Hierarchies
| Area | Loading UI |
|---|---|
| Hierarchy picker | shows the last-known name instantly; list in the popover: `variant="list" [rows]="6"` while fetching |
| Tree (navigator) | `variant="tree" [rows]="14"` on first load and when switching hierarchy |
| Expanding a big branch | replace that branch's children area with `variant="tree" [rows]="3"`, indented one level |
| Node search results popover | top bar only; if >300ms, `variant="list" [rows]="3"` in the popover |
| Node panel (on select) | path ribbon + name area: `variant="detail"`; keep the panel frame |
| Where it lives | `variant="list" [rows]="4"` |
| Properties tab | `variant="table" [rows]="9" columns="1fr .8fr 1fr 80px"` |
| Children tab | `variant="list" [rows]="5"` |
| History tab | `variant="timeline" [rows]="3"` |
| Add to draft / Move / Add child confirm | `[nxBusy]` on the confirm button |

### Queries
| Area | Loading UI |
|---|---|
| Builder | renders immediately; property lists cached |
| Saved queries select | top bar only |
| Results after **Run query** | keep the results panel; header text becomes skeleton line; body `variant="table" [rows]="8" [cols]="3 + chosenColumns"` |
| Run query button | `[nxBusy]="running"` |
| Export CSV / Save | `[nxBusy]` |

### Compare
| Area | Loading UI |
|---|---|
| Before first Compare | empty state (no skeleton): "Choose what to compare" |
| After **Compare** click | Compare button `[nxBusy]="comparing"`; differences panel shows tab row skeleton (5 short lines) + left `variant="list" [rows]="6"` + right `variant="detail"` |
| Selecting another change | right side only: `variant="detail"` if the detail is fetched separately |
| Export CSV | `[nxBusy]` |

### As-Was
| Area | Loading UI |
|---|---|
| Day strip (month change) | keep bars, fade them to 40% opacity while loading (top bar shows progress) |
| Changes on this day | `variant="list" [rows]="4"` |
| Tree at this moment | `variant="tree" [rows]="12"` on hierarchy/moment change |

### Worklist
| Area | Loading UI |
|---|---|
| Tab counts | show "–" until loaded |
| List | `variant="list" [rows]="5"` |
| Detail | `variant="detail"` |
| Discussion | `variant="timeline" [rows]="2"` |
| Approve / Send back / Reject / Resubmit / Withdraw / Send | `[nxBusy]` on the clicked button; other action buttons get `aria-disabled` until done |

### Sandboxes
| Area | Loading UI |
|---|---|
| List | `variant="list" [rows]="3"` |
| Head facts | `variant="facts" [cols]="3"` |
| Changes tab | `variant="table" [rows]="6" columns="96px 1fr 80px 80px"` |
| Data diff tab | `variant="table" [rows]="6" [cols]="4"` |
| Conflicts tab | `variant="list" [rows]="2"` |
| Validation tab | `variant="list" [rows]="3"` |
| Readiness box | 4 skeleton lines + button placeholder (use `variant="list" [rows]="2"`) |
| New sandbox / Refresh from live / Keep mine / Use live value / Submit as request / Discard | `[nxBusy]` |

### Versions
| Area | Loading UI |
|---|---|
| Table | `variant="table" [rows]="3" columns="2.2fr 140px 120px 190px 120px 260px"` |
| Submit / Switch to / Copy to new / New version | `[nxBusy]` |

---

## 4. Busy button example (Compare)
```html
<button type="button" class="btn-primary btn-lg" [nxBusy]="comparing()" (click)="compare()">Compare</button>
```
```ts
comparing = signal(false);
compare() {
  if (this.from === this.to) return;
  this.comparing.set(true);
  this.diff.set({ status: 'loading' });
  this.api.compare(this.hier, this.from, this.to)
    .pipe(finalize(() => this.comparing.set(false)))
    .subscribe({ next: (d) => this.diff.set({ status: 'ready', data: d }), error: () => this.diff.set({ status: 'error' }) });
}
```

## 5. Check
- [ ] Click between header tabs: green bar runs under the context bar, never a blank white page.
- [ ] Each panel shows its own skeleton shaped like its content; no "Loading…" text anywhere.
- [ ] Fast responses (<150ms) show nothing.
- [ ] Action buttons show a spinner and can't be double-clicked.
- [ ] With "reduce motion" on, shimmer stops.
