# Home screen — Build Specification (exact styles)

> **Give this file to the LLM/developer together with `nexus-design-system.md` and a screenshot of the canvas board "Home".** The shell (header, context bar, collapsible sidebar, draft bar) comes from the design system unchanged. This file defines the Home page body down to every size and colour, plus a small CSS block (§4) to **append verbatim** to `src/styles.css` after `nexus-ui.css`. Component `.scss` files may contain layout only. If this file and the screenshot disagree, this file wins.

---

## 1. Purpose
The landing page after sign-in. It answers three questions in one glance, with as little text as possible:
1. **What needs me?** → *Needs you* (requests waiting for my approval or response).
2. **Where are my own changes?** → *Your requests* (my draft and submitted requests).
3. **Where am I?** → current version status + my hierarchies.

Plus one universal search for nodes and request IDs.

Design rules for this page: no helper paragraphs, no legends, one line of meta per row, one action per row, empty states are one short sentence.

Route: `/` (or `/home`). Sidebar item **Home** has `aria-current="page"`.

---

## 2. Layout (1440 × 1100 at design time)

```
┌ .n-header (#044014) ─ [☰] LOGO  Nexus  Master Data Central ……… DEVELOPMENT  (ES  E2E Steward · e2e_steward  Switch) ┐
├ .n-context ─ Version [2026-11 · Working ▾]  As of [16/11/2026]  Work in (Live data|Sandbox) …… Manage versions ┤
├ rail 64px ┬ .n-main ───────────────────────────────────────────────────────────────────────────┤
│ ⌂ (active)│  .n-home (max-width 1240, padding 32px, gap 24px, scrolls)                          │
│ ⊞         │   Good evening, Steward                                                             │
│ ⌕         │   ( 🔍 Search nodes or requests                               )  ← max-width 680px  │
│ ⇄         │                                                                                    │
│ ◷         │   ┌ Needs you (2) ───────── Worklist ┐   ┌ 2026-11            [Working] ┐           │
│ ───       │   │ title / CR-1046 · from …  2 days left [Review] │ │ ███████░░░░░░░░             │ │
│ ☰ (2)     │   │ title / CR-1044 · pushed back by …   [Respond] │ │ Day 16 of 30 · finalise 30 Nov│ │
│ ◫         │   └──────────────────────────────────┘   └──────────────────────────────┘           │
│ ≡         │   ┌ Your requests ────────── See all ┐   ┌ Your hierarchies ────────────┐           │
│ ───       │   │ 2 changes not submitted  Draft  › │   │ ORGTL   Organization – …   › │           │
│ ⧉         │   │ Inactivate 34322 …   In approval › │   │ E2E_H   E2E Legal Group (1 open) › │     │
│ ⚙         │   │ Add 99301 …          Committed   › │   │ …                            │           │
│           │   └──────────────────────────────────┘   └──────────────────────────────┘           │
│           ├ .n-draftbar  (2) Your draft request · 2 changes … View changes  Discard  [Review and submit] ┤
└───────────┴────────────────────────────────────────────────────────────────────────────────────┘
```

- Root: `.n-app.n-app--fixed` (100vh, min-height 900px, no page scroll).
- `.n-main` is a column: **`.n-home-scroll`** (flex 1, scrolls) + **`.n-draftbar`** (fixed at the bottom of the main column).
- Sidebar: collapsed rail by default (64px). Worklist shows the *Needs you* count as a rail badge (`.n-nav-item__dot`), hidden when 0.
- Columns: left `flex: 999 1 560px`, right `flex: 1 1 360px`, gap 20px, `align-items: flex-start`, wrapping. Below ~960px content width the right column drops under the left.

---

## 3. Components (exact)

### 3.1 Greeting
```html
<h1 class="n-home__greeting">Good evening, Steward</h1>
```
- 26px / 700 / letter-spacing −0.01em / colour `#1F1F1F`, margin 0.
- Text: "Good morning" (before 12:00), "Good afternoon" (12:00–16:59), "Good evening" (17:00+) + ", " + the user's **first name / display short name** (for `e2e_steward` → "Steward").
- Gap to search: 16px.

### 3.2 Universal search
```html
<div class="n-search n-home__search">
  <label class="n-sr-only" for="home-search">Search nodes or requests</label>
  <svg class="n-search__icon" viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
  <input id="home-search" class="n-search__input" type="search" placeholder="Search nodes or requests">
  <div class="n-popover"> …results… </div>
</div>
```
- Wrapper max-width **680px**. Input: height 52px, radius 999px, border 1.5px `#BDBDBD`, padding `0 20px 0 50px`, font 16px, background `#FFFFFF`. Icon 20×20 at left 18px / top 16px, stroke `#3D3D3D` width 2.
- Popover (shown when query ≥ 2 chars): absolute, top 60px, full width, white, border 1px `#D9D9D9`, radius 16px, shadow `0 12px 32px rgba(0,0,0,.14)`, padding 8px, z-index 20.
- Result rows (no group headers): `a.n-home-hit` — flex, gap 12px, padding 10px, radius 10px, min-height 48px, colour `#1F1F1F`, no underline; hover background `#F2F8EF`.
  - **Node hit:** marker (limb = 10×10 square, 1.5px `#3D3D3D`; leaf = 8×8 circle, 1.5px `#6B6B6B`) · code (JetBrains Mono 12px, `#2E6B21`, min-width 52px) · name (14px / 600). Links to `/hierarchies/<hier>?node=<code>`.
  - **Request hit:** request ID (mono 12px / 600, `#2E6B21`, min-width 64px) · title (14px, flex 1) · status pill (§3.6). Links to the request.
  - Order: up to 5 node hits, then up to 4 request hits. Empty: single line "No matches." (13px, `#5C5C5C`, padding 12px).
- Behaviour: debounce 200ms; a query matching `^CR-\d+$` shows the request first. Enter opens the first result. ↑/↓ move, Esc closes.
- API: `GET /api/search?q=` → `{ nodes:[{code,name,type,hierarchy}], requests:[{id,title,status}] }` (nodes searched in the user's last-used hierarchy, ORGTL by default).

### 3.3 Card shell (all four cards)
- `section.n-card`: background `#FFFFFF`, border 1px `#D9D9D9`, radius 16px, overflow hidden.
- **Card header** `.n-home-card__head`: flex, align centre, gap 10px, padding **16px 20px**, **no bottom border**. Title `h2` 16px / 700, margin 0. Right-side link (`.n-home-card__link`): 13px / 600, colour `#2E6B21`, no underline (underline on hover).
- **Rows** start with a top divider `1px solid #F0F0F0`; row padding **14px 20px** (hierarchies: 12px 20px).
- Gap between cards in a column: 20px.

### 3.4 Needs you (left, first)
Header: title "Needs you" + **count badge** + spacer + link "Worklist" → `/worklist`.
- Count badge `.n-home-count`: 12px / 700, radius 999px, padding `2px 9px`. When > 0: text `#FFFFFF` on `#1F1F1F`. When 0: text `#4A4A4A` on `#ECECEC`.

Row `.n-home-row`:
```
[ title (14/600)                         ]  [due]  [action pill]
[ CR-1046 · from e2e_steward (12, muted) ]
```
- Flex, align centre, gap 14px.
- Text column: flex 1, min-width 0, gap 3px. Title 14px / 600 `#1F1F1F`. Meta 12px `#5C5C5C`: request ID in JetBrains Mono, then " · " and **one** short fact (who it's from, or "pushed back by <user>").
- Due text (optional, only when a deadline applies): 12px / 600 `#7A4100`, nowrap, e.g. "2 days left". Omit entirely otherwise (no placeholder).
- Action pill (`a`, 36px high, padding 0 16px, radius 999px, 13px / 600, border 1.5px):
  - **Review** (approval needed): background `#368727`, text `#FFFFFF`, border `#368727`.
  - **Respond** (pushed back to me): background `#FFFFFF`, text `#1F1F1F`, border `#BDBDBD`.
- Max 5 rows; more → the "Worklist" link.
- Empty: one line "You're all caught up." — padding 20px, top divider, 14px `#5C5C5C`.

Sample rows:
| ID | Title | Meta | Due | Action |
|---|---|---|---|---|
| CR-1046 | Move 3 cost centres from AMFIT to AMEQT | from e2e_steward | 2 days left | Review |
| CR-1044 | Rename AGCOE to “AM AI Centre of Excellence” | pushed back by u_approver1 | — | Respond |

### 3.5 Your requests (left, second)
Header: "Your requests" + spacer + link "See all" → `/requests?mine=true`.

Row = whole-row link `a.n-home-row` (no underline, colour `#1F1F1F`, hover background `#FAFAF8`):
```
[ title (14/600)              ]  [status pill]  ›
[ CR-1045 · ORGTL · 15 Nov    ]
```
- Text column as in §3.4. Meta: ID (mono) · hierarchy · date (e.g. "15 Nov"); for the draft: "Draft · ORGTL · today".
- Status pill §3.6. Chevron 16×16, stroke `#9E9E9E` width 2, path `m9 6 6 6-6 6`.
- Order: the **draft first** (if it has items), then newest requests. Max 5.
- Draft row links to the Hierarchies page with the draft list open.
- Empty: "No requests yet." (same style as §3.4 empty).

Sample rows:
| ID | Title | Meta | Status |
|---|---|---|---|
| Draft | 2 changes not submitted | ORGTL · today | Draft |
| CR-1045 | Inactivate 34322 QNT Contingent | ORGTL · 15 Nov | In approval |
| CR-1039 | Add 99301 QNT Research under AGQNT | ORGTL · 13 Nov | Committed |

### 3.6 Status pills (requests)
12px / 600, radius 999px, padding `3px 10px`, nowrap.
| Status | Text | Background |
|---|---|---|
| Draft | `#4A4A4A` | `#ECECEC` |
| Submitted | `#1D5FA8` | `#E4EEFA` |
| In approval | `#1D5FA8` | `#E4EEFA` |
| Pushed back | `#7A4100` | `#FFF1DE` |
| Committed | `#1F4D16` | `#E3F0DE` |
| Rejected | `#B42318` | `#FDECEA` |

### 3.7 Current version card (right, first)
No header row; the card has padding **20px** and a column gap of **12px**.
```
2026-11                         [Working]
████████░░░░░░░░░░░░░░  (8px bar)
Day 16 of 30 · finalise 30 Nov
```
- Top row: version code JetBrains Mono **20px / 700** `#1F1F1F` (flex 1) + status pill: 12px / 600, padding `4px 12px`, radius 999px — Working `#1F4D16` on `#E3F0DE`; Finalized `#4A4A4A` on `#ECECEC`.
- Progress bar: `role="progressbar"` (aria-valuemin 1, aria-valuemax = days in month, aria-valuenow = today's day), height 8px, radius 999px, track `#ECECEC`; fill `#368727`, radius 999px, width = day ÷ days in month (16/30 → 53%). Finalized version → 100%.
- Caption 13px `#5C5C5C`: Working → "Day 16 of 30 · finalise 30 Nov"; Finalized → "Finalized · read-only".
- Reflects the version chosen in the context bar.

### 3.8 Your hierarchies (right, second)
Header: title only ("Your hierarchies", 16/700, padding 16px 20px).
Row = `a` link to `/hierarchies/<code>`, flex, gap 12px, padding **12px 20px**, top divider `#F0F0F0`, hover background `#FAFAF8`:
- Code: JetBrains Mono 12px `#5C5C5C`, min-width **72px**.
- Name: 14px / 500 `#1F1F1F`, flex 1, min-width 0 (wraps if long).
- Open-requests pill **only when > 0**: 12px / 600, `#1D5FA8` on `#E4EEFA`, padding `2px 9px`, radius 999px, text "1 open".
- Chevron as §3.5.
- Order: last used first, then the rest alphabetically by code. Show all hierarchies the user can see.

Sample rows: `ORGTL` Organization – Total Fidelity · `E2E_H` E2E Legal Group (1 open) · `E2E_MGMT` E2E Management View · `ORGTP` Total Fidelity Technology · `AMBUS` Asset Management BU Alternate.

### 3.9 Draft bar
Exactly the design-system `.n-draftbar` (same as Hierarchies). Shown on Home because the draft is global. Count 2 → "Your draft request · 2 changes" / "Nothing changes until governance approves · Version 2026-11 Working"; buttons View changes · Discard · **Review and submit**. 0 → zero variant.

---

## 4. CSS to append to `src/styles.css` (after `nexus-ui.css`)

```css
/* ===== Home ===== */
.n-home-scroll{flex:1 1 auto;min-height:0;overflow:auto}
.n-home{max-width:1240px;padding:32px;display:flex;flex-direction:column;gap:24px;box-sizing:border-box}
.n-home__start{display:flex;flex-direction:column;gap:16px}
.n-home__greeting{margin:0;font-size:26px;font-weight:700;letter-spacing:-.01em;line-height:1.2}
.n-home__search{max-width:680px}
.n-home__cols{display:flex;flex-wrap:wrap;gap:20px;align-items:flex-start}
.n-home__col{min-width:0;display:flex;flex-direction:column;gap:20px}
.n-home__col--main{flex:999 1 560px}
.n-home__col--side{flex:1 1 360px}

.n-home-hit{display:flex;align-items:center;gap:12px;padding:10px;border-radius:10px;min-height:48px;color:var(--n-text);font-weight:400;text-decoration:none}
.n-home-hit:hover{background:var(--n-brand-30);text-decoration:none;color:var(--n-text)}
.n-home-hit__code{font-family:var(--n-mono);font-size:12px;color:var(--n-brand-700);min-width:52px}
.n-home-hit__id{font-family:var(--n-mono);font-size:12px;font-weight:600;color:var(--n-brand-700);min-width:64px}
.n-home-hit__name{font-size:14px;font-weight:600}
.n-home-hit__title{font-size:14px;flex:1 1 auto}
.n-home-nohit{padding:12px;font-size:13px;color:var(--n-text-muted)}

.n-home-card__head{display:flex;align-items:center;gap:10px;padding:16px 20px}
.n-home-card__title{margin:0;font-size:16px;font-weight:700}
.n-home-card__link{font-size:13px;font-weight:600;color:var(--n-brand-700);text-decoration:none}
.n-home-count{font-size:12px;font-weight:700;border-radius:var(--n-r-pill);padding:2px 9px;color:#fff;background:var(--n-text)}
.n-home-count--zero{color:var(--n-text-3);background:var(--n-neutral-bg)}

.n-home-row{display:flex;align-items:center;gap:14px;padding:14px 20px;border-top:1px solid #F0F0F0;color:var(--n-text);font-weight:400;text-decoration:none}
a.n-home-row:hover{background:var(--n-surface-alt);color:var(--n-text);text-decoration:none}
.n-home-row--compact{gap:12px;padding:12px 20px}
.n-home-row__text{display:flex;flex-direction:column;gap:3px;flex:1 1 auto;min-width:0}
.n-home-row__title{font-size:14px;font-weight:600}
.n-home-row__meta{font-size:12px;color:var(--n-text-muted)}
.n-home-row__meta .n-mono{font-size:12px}
.n-home-row__due{font-size:12px;font-weight:600;color:var(--n-warn-text);white-space:nowrap}
.n-home-row__chev{width:16px;height:16px;flex:0 0 16px;stroke:var(--n-separator);stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-home-row__code{font-family:var(--n-mono);font-size:12px;color:var(--n-text-muted);min-width:72px}
.n-home-row__name{font-size:14px;font-weight:500;flex:1 1 auto;min-width:0}
.n-home-empty{padding:20px;border-top:1px solid #F0F0F0;font-size:14px;color:var(--n-text-muted)}

.n-home-action{display:inline-flex;align-items:center;height:36px;padding:0 16px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);color:var(--n-text);font-size:13px;font-weight:600;text-decoration:none;white-space:nowrap}
.n-home-action--primary{background:var(--n-brand);border-color:var(--n-brand);color:#fff}
.n-home-action:hover{text-decoration:none}

.n-req-status{display:inline-block;font-size:12px;font-weight:600;border-radius:var(--n-r-pill);padding:3px 10px;white-space:nowrap}
.n-req-status--draft{color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-req-status--submitted,.n-req-status--approval{color:var(--n-moved);background:var(--n-moved-bg)}
.n-req-status--pushed{color:var(--n-warn-text);background:var(--n-status-bg)}
.n-req-status--committed{color:var(--n-brand-800);background:var(--n-brand-50)}
.n-req-status--rejected{color:var(--n-danger);background:var(--n-danger-bg)}
.n-open-pill{font-size:12px;font-weight:600;color:var(--n-moved);background:var(--n-moved-bg);border-radius:var(--n-r-pill);padding:2px 9px;white-space:nowrap}

.n-version-card{padding:20px;display:flex;flex-direction:column;gap:12px}
.n-version-card__top{display:flex;align-items:center;gap:10px}
.n-version-card__code{font-family:var(--n-mono);font-size:20px;font-weight:700;flex:1 1 auto}
.n-version-card__bar{height:8px;border-radius:var(--n-r-pill);background:var(--n-neutral-bg);overflow:hidden}
.n-version-card__fill{height:100%;background:var(--n-brand);border-radius:var(--n-r-pill)}
.n-version-card__caption{font-size:13px;color:var(--n-text-muted)}
```

---

## 5. HTML contract (Angular template skeleton)

```html
<div class="n-home-scroll">
  <div class="n-home">
    <section class="n-home__start" aria-label="Start">
      <h1 class="n-home__greeting">{{ greeting }}</h1>
      <div class="n-search n-home__search"> …§3.2… </div>
    </section>

    <div class="n-home__cols">
      <div class="n-home__col n-home__col--main">
        <section class="n-card" aria-label="Needs you">
          <div class="n-home-card__head">
            <h2 class="n-home-card__title">Needs you</h2>
            <span class="n-home-count" [class.n-home-count--zero]="needs.length===0">{{ needs.length }}</span>
            <span class="n-spacer"></span>
            <a class="n-home-card__link" routerLink="/worklist">Worklist</a>
          </div>
          @for (n of needs; track n.id) {
            <div class="n-home-row">
              <div class="n-home-row__text">
                <span class="n-home-row__title">{{ n.title }}</span>
                <span class="n-home-row__meta"><span class="n-mono">{{ n.id }}</span> · {{ n.meta }}</span>
              </div>
              @if (n.due) { <span class="n-home-row__due">{{ n.due }}</span> }
              <a class="n-home-action" [class.n-home-action--primary]="n.kind==='approve'" [routerLink]="['/requests', n.id]">{{ n.kind==='approve' ? 'Review' : 'Respond' }}</a>
            </div>
          } @empty { <div class="n-home-empty">You're all caught up.</div> }
        </section>

        <section class="n-card" aria-label="Your requests">
          <div class="n-home-card__head"><h2 class="n-home-card__title">Your requests</h2><span class="n-spacer"></span>
            <a class="n-home-card__link" routerLink="/requests" [queryParams]="{mine:true}">See all</a></div>
          @for (r of requests; track r.id) {
            <a class="n-home-row" [routerLink]="r.link">
              <span class="n-home-row__text"><span class="n-home-row__title">{{ r.title }}</span>
                <span class="n-home-row__meta"><span class="n-mono">{{ r.id }}</span> · {{ r.meta }}</span></span>
              <span class="n-req-status" [ngClass]="'n-req-status--' + r.status">{{ r.statusLabel }}</span>
              <svg class="n-home-row__chev" viewBox="0 0 24 24" aria-hidden="true"><path d="m9 6 6 6-6 6"/></svg>
            </a>
          } @empty { <div class="n-home-empty">No requests yet.</div> }
        </section>
      </div>

      <div class="n-home__col n-home__col--side">
        <section class="n-card n-version-card" aria-label="Current version">
          <div class="n-version-card__top"><span class="n-version-card__code">{{ version.code }}</span>
            <span class="n-status" [class.n-status--working]="version.working" [class.n-status--finalized]="!version.working">{{ version.working ? 'Working' : 'Finalized' }}</span></div>
          <div class="n-version-card__bar" role="progressbar" aria-label="Month progress" aria-valuemin="1" [attr.aria-valuemax]="version.days" [attr.aria-valuenow]="version.day">
            <div class="n-version-card__fill" [style.width.%]="version.percent"></div></div>
          <span class="n-version-card__caption">{{ version.caption }}</span>
        </section>

        <section class="n-card" aria-label="Your hierarchies">
          <div class="n-home-card__head"><h2 class="n-home-card__title">Your hierarchies</h2></div>
          @for (h of hierarchies; track h.code) {
            <a class="n-home-row n-home-row--compact" [routerLink]="['/hierarchies', h.code]">
              <span class="n-home-row__code">{{ h.code }}</span>
              <span class="n-home-row__name">{{ h.name }}</span>
              @if (h.open > 0) { <span class="n-open-pill">{{ h.open }} open</span> }
              <svg class="n-home-row__chev" viewBox="0 0 24 24" aria-hidden="true"><path d="m9 6 6 6-6 6"/></svg>
            </a>
          }
        </section>
      </div>
    </div>
  </div>
</div>
<app-draft-bar />
```

---

## 6. Data & API
| Data | Endpoint | Fields |
|---|---|---|
| Needs you | `GET /api/worklist?assignedTo=me&limit=5` | id, title, kind (`approve` \| `pushed`), from, dueAt |
| Your requests | `GET /api/requests?mine=true&limit=5` + `GET /api/drafts/current` | id, title, status, hierarchy, updatedAt / draft item count |
| Version | context service (`version`, status, createdAt, finaliseAt) | code, status, day, days |
| Hierarchies | `GET /api/versions/{v}/hierarchies?withOpenRequests=true` | code, name, openRequests, lastUsed |
| Search | `GET /api/search?q=` | see §3.2 |

Formatting: dates "15 Nov" (same year) or "15 Nov 2025"; today → "today". Due: "2 days left", "Due today", "Overdue" (overdue uses `#B42318`).

## 7. States
- **Loading:** cards render their headers; rows show 2 skeleton bars each (height 14px and 10px, widths 60% / 35%, background `#F2F2F2`, radius 6px, row padding as normal).
- **Empty (new user):** Needs you "You're all caught up.", Your requests "No requests yet.", draft bar zero variant.
- **Finalized version selected:** version card shows Finalized pill, full bar, "Finalized · read-only".
- **Error per card:** replace rows with one line "Couldn't load. Retry" (14px, Retry as `.n-home-card__link`).

## 8. Accessibility
- One `h1` (greeting); card titles `h2`. Each card `section` has `aria-label`.
- Whole-row links have an accessible name from their title text; action pills are separate links.
- Count badge is followed by visually hidden text "requests need you" (`<span class="n-sr-only">`).
- Contrast: all text ≥ 4.5:1 (muted `#5C5C5C`, due `#7A4100`).

## 9. Acceptance checklist
- [ ] Shell identical to other screens (header `#044014`, rail sidebar with Home active, context bar, draft bar).
- [ ] Greeting 26px/700; search 680px wide, 52px tall, popover with node and request hits.
- [ ] Two columns (560 / 360 basis, gap 20) that wrap on narrow screens.
- [ ] Needs you: count badge, two-line rows, due text only where relevant, Review (green) / Respond (outline).
- [ ] Your requests: draft first, status pills per §3.6, chevrons.
- [ ] Version card: mono 20px code, status pill, 8px progress bar, one-line caption.
- [ ] Your hierarchies: code column 72px, open pill only when > 0.
- [ ] No extra helper text anywhere; empty states are one line.
- [ ] Pixel check vs. the "Home" board at 1440px: computed styles match §3–§4.
