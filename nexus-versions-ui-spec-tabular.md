# Nexus – Versions screen · UI specification

**Route:** `/versions?status=<All|Working|Submitted|Finalized|Expired>` · **Header tab:** Versions (also opened by "Manage versions" in the context bar) · **Mockup board:** "Versions (new)" — `Versions.dc.html`
**Purpose:** see every version in one table, know which one you are working in, and take the one next step each version allows. Compact, no explanatory text.

Backend reminder (not shown in the UI): working versions share one bitemporal store; a committed change appears in every working version whose business date is on or after the change's effective date. Finalized versions are frozen (business date + recorded time) and never change.

---

## Part A — Shared foundations (identical on every Nexus screen)

> This part is the same in all 8 screen specs. Do not change any value here for one screen only. If a value here conflicts with something in Part B, Part A wins unless Part B says it overrides it.

### A1. Design tokens

Use these exact values. Do not add new colours, shades, shadows or radii.

| Token | Value | Use |
|---|---|---|
| `--nx-header-bg` | `#044014` | Top header band only. Never used for buttons or highlights in the page body. |
| `--nx-brand` | `#368727` | Every filled primary button, active tab underline in the page body, selected toggle segment, switch "on", checkbox fill, selected day bar, timeline dot "approved", selected-row left edge. White text on it (contrast 4.5:1). |
| `--nx-link` | `#2B6A1F` | All text links (underlined, `text-underline-offset: 2px`), green tag text, positive status text. |
| `--nx-link-hover` | `#1E4F15` | Link hover. |
| `--nx-tint` | `#F3F6F1` | Selected row background, "new value" cell background, current-step background. |
| `--nx-tint-2` | `#E6EFE2` | Selected option background in dropdown lists, hierarchy icon tile. |
| `--nx-tint-3` | `#E8F1E5` | Draft-count badge background; sandbox strip background. |
| `--nx-tint-border` | `#A8CF9C` | Border of green outlined tags; header active-tab underline; header badge background. |
| `--nx-tint-border-2` | `#DCE6D8` / `#C5D9BE` | Border of pale-green boxes (draft callout, sandbox strip). |
| `--nx-page` | `#F9F7F5` | Page background under everything below the header. |
| `--nx-surface` | `#FFFFFF` | Panels, inputs, menus. |
| `--nx-surface-2` | `#FCFBFA` | Table header rows, group header rows, inset boxes, separator rows. |
| `--nx-border` | `#E5E1DC` | Panel borders, table borders, tab-row bottom border. |
| `--nx-divider` | `#EEEAE5` | Row dividers inside panels, section dividers. |
| `--nx-divider-soft` | `#F1EEEA` | Very light row dividers (property rows), neutral chip background. |
| `--nx-control-border` | `#C9C4BE` | Input/select/outline-button borders, unselected switch track. |
| `--nx-text` | `#141414` | Primary text. |
| `--nx-text-2` | `#5C5C5C` | Secondary text, field labels, meta lines. |
| `--nx-text-3` | `#8A8580` | Tertiary text: codes under names, counts in tabs, timestamps, placeholders for empty values. |
| `--nx-muted-dot` | `#B5AFA8` | Neutral timeline dots, leaf dots. |
| `--nx-danger` | `#B42318` | Only: overdue, errors, conflicts, Reject / Delete / Discard-sandbox actions. Never decorative. |
| `--nx-danger-border` | `#E8B4AE` | Border of a red outlined tag / blocked card. |
| `--nx-notify` | `#F2B33D` | Only the notification-count bubble in the header. |

**Colour rules (hard):**
1. One accent: green. No blue, purple or amber anywhere in the page body.
2. A change type (added, moved, renamed …) is shown with **words**, never a colour per type.
3. Red appears only when something is wrong or destructive.
4. Old value = `--nx-text-3` + `line-through`. New value = `font-weight: 600` on `--nx-tint`.

### A2. Typography

- Font stack: `'Fidelity Sans', 'Hind', Helvetica, Arial, sans-serif`. Load Hind 400/500/600/700 from Google Fonts as the fallback; if the app already ships Fidelity Sans (as the Fidelity Shares app does), use it.
- No monospace font anywhere (codes use the normal font).
- Base text colour `--nx-text`.

| Role | Size / weight | Notes |
|---|---|---|
| Page title (h1) | 30px / 700, letter-spacing −0.01em | One per page. Exception: Hierarchies uses 28px. |
| Detail title (h2 inside a panel) | 28px / 700, line-height 1.25 | Selected item's name. Hierarchies node name 30px. |
| Section title (h3) | 16px / 700 | "Request details", "What changed" … sentence case. |
| Panel heading (h2 in a list panel) | 18px / 700 | "Needs your attention", "Recent activity" … |
| Group header in a list | 14px / 700 + count 13px `--nx-text-3` | On `--nx-surface-2` background. |
| Body | 14–15px / 400 | Row titles 15px / 600. |
| Field label | 13px / 600 `--nx-text-2`, sentence case | "Hierarchy", "From version" … Never ALL CAPS. |
| Meta line | 13px / 400 `--nx-text-2` or `--nx-text-3` | |
| Tiny caps label | 12px / 700, letter-spacing 0.08em, `--nx-text-3` | Only allowed on Home snapshot labels, Hierarchies "WHERE IT LIVES"/property category headers, dropdown group headers (PRIMARY / ALTERNATES, Finance/HR column groups). Nowhere else. |
| Big fact value | 18–20px / 700 | Facts rows (Sandboxes, Versions). |

Copy rules: sentence case; no exclamation marks; no "AI-style" helper sentences under titles. A line under a page title is allowed only when it states live state (e.g. "3 requests need your decision · 1 overdue"). Use `·` (middle dot with spaces) to join short facts.

### A3. Shape, spacing, elevation

| Element | Radius | Height | Border |
|---|---|---|---|
| Panel / card / table container | 8px | — | 1px `--nx-border` |
| Input, select, date, textarea, joined toggle group, square icon button | 4px | 36px (38px for page-level search) | 1px `--nx-control-border` |
| Button (filled or outline) | 999px (pill) | 38px default, 34px in bars, 32px in rows/cards | outline = 1px `--nx-control-border`, white bg |
| Small tag (status/ownership) | 4px | auto, padding 1px 8px, 13px/600 | 1px tint-border |
| Menu / popover | 6–8px | — | 1px `--nx-border` or `--nx-control-border`, shadow `0 8px 24px rgba(0,0,0,0.10)` |
| Count badge (draft) | 999px | 26px min-width | none |

- Page content column: `max-width: 1320px; margin: 0 auto; padding: 26px 24px 28px; gap: 20px` (vertical stack). Hierarchies uses `padding: 24px; gap: 18px`.
- Inside panels: horizontal padding 20px (lists) / 28px (detail areas); rows 14px vertical padding.
- No shadows on panels. Shadows only on popovers/menus, the sticky draft bar (`0 -2px 8px rgba(0,0,0,0.04)`), and toasts.
- Selected row pattern (used everywhere): `background: --nx-tint; box-shadow: inset 3px 0 0 --nx-brand;` unselected rows white.

### A4. Components (reuse exactly)

**A4.1 Primary button** — bg `--nx-brand`, text white 14px/600, pill, 38px, padding 0 20px, no border.
**A4.2 Secondary button** — white bg, 1px `--nx-control-border`, text `--nx-text` 14px/600, pill.
**A4.3 Danger text button** — transparent, no border, text `--nx-danger` 14px/600 (Reject, Discard sandbox, Withdraw).
**A4.4 Danger filled** — bg/border `--nx-danger`, white text (only the confirm step of Reject).
**A4.5 Text link** — `--nx-link`, underline, offset 2px, 13–14px.
**A4.6 Select** — `appearance: none`, 36px, 4px radius, padding `0 32px 0 10px`, 14px/600, chevron SVG 12px stroke `#5C5C5C` 2.4 absolutely positioned right 12px, `pointer-events: none`.
**A4.7 Joined toggle (segmented)** — container `display:inline-flex; border:1px solid --nx-control-border; border-radius:4px; overflow:hidden`. Segments 34px (30px in context bar, 28px small) tall, padding 0 14px, 14px/600; selected = `--nx-brand` bg + white text; unselected = white + `--nx-text`; every segment after the first has `border-left: 1px solid --nx-control-border`. `aria-pressed` on each.
**A4.8 Switch** — `role="switch" aria-checked`, track 34×20 radius 999, on `--nx-brand` / off `--nx-control-border`, knob 16×16 white, left 2px (off) / 16px (on), shadow `0 1px 2px rgba(0,0,0,.2)`; label text 14px to its right, gap 10px.
**A4.9 Tab row (in-panel)** — `role="tablist"`, row padding `0 12px`, gap 4px, bottom border 1px `--nx-border`. Tab: 46px tall (44px on Sandboxes), padding 0 10px, `border-bottom: 3px solid` (`--nx-brand` when selected, transparent otherwise), label 14px weight 700 selected / 500 not, colour `--nx-text` selected / `--nx-text-2` not; count after label 13px/400 `--nx-text-3` (red/700 when it represents blocking problems).
**A4.10 Master–detail split** — single panel (A3), internal grid `grid-template-columns: <list width> minmax(0,1fr)`, list column `border-right: 1px solid --nx-border`. List widths: Compare/Worklist 400px, Versions none (table), Sandboxes 340px, As-Was 400px (separate panels).
**A4.11 List row (button)** — full width, `display:flex; flex-direction:column; gap:3–4px; padding:14px 20px; border-bottom:1px solid --nx-divider; text-align:left`, title 15px/600, then 1–2 meta lines. Selected pattern from A3. `aria-current="true"` when selected.
**A4.12 Before/after table** — container A3; header row on `--nx-surface-2`, 13px/600 `--nx-text-2`, cell padding 10px 12–16px; body rows 14px, top border `--nx-divider` from the 2nd row; Before cell `--nx-text-3` + line-through (no strike when the before value is "—"/"New"/"Not in live"); After cell 600 on `--nx-tint`.
**A4.13 Timeline** — container `position:relative; padding-left:22px`; vertical line 2px `--nx-divider` at left 5px; each item: dot 12×12 circle, 2px white border, `box-shadow:0 0 0 1px <dot colour>`, placed at left −22px top 4–5px; text 14px/600 then meta 12px `--nx-text-3`. Dot colours: approved `--nx-brand`, comment `--nx-text-3`, other/system `--nx-muted-dot`.
**A4.14 Toast** — fixed, bottom 76px (24px on pages without draft bar), centred, bg `--nx-text`, white 14px, radius 4px, padding 10px 16px, auto-hide 2500ms, `role="status"`. Worklist toast adds an **Undo** button (30px, radius 4px, bg rgba(255,255,255,.14)).
**A4.15 Empty state** — centred in its area, title 18px/700, one line 14px `--nx-text-2`, padding 40–56px.
**A4.16 Status tag** — inline, 13px/600, 1px border, 4px radius, padding 1px 8px. Green variant: text `--nx-link`, border `--nx-tint-border`. Neutral: text `--nx-text-2`, border `--nx-control-border`. Red: text `--nx-danger`, border `--nx-danger-border`.

### A5. App shell (top of every page)

Root: `min-height:100vh; background:--nx-page; display:flex; flex-direction:column; font-family: A2`.

**A5.1 Header band** (`<header>`, bg `--nx-header-bg`, white text)
- Row 1: inner `max-width:1320px; margin:0 auto; padding:12px 24px 0; display:flex; align-items:center; gap:16px`:
  1. Logo placeholder: dashed 1.5px rgba(255,255,255,.7) box, radius 4px, padding 2px 8px, "LOGO" 12px/700 (replace with the real Fidelity logo asset).
  2. "Nexus" 24px/700.
  3. "Master Data Central" 13px rgba(255,255,255,.75).
  4. Flexible spacer.
  5. Help icon button 32×32 (question-mark-in-circle icon 20px, stroke 1.7) `aria-label="Help"`.
  6. Notifications icon button 32×32 (bell icon) with count bubble: absolute top 1px right 1px, min 15×15, radius 999, bg `--nx-notify`, text `#141414` 10px/700. `aria-label="Notifications, 3 new"`.
  7. Environment pill "Development": 12px, 1px rgba(255,255,255,.5) border, radius 999, padding 3px 12px.
  8. Vertical divider 1×22 rgba(255,255,255,.25).
  9. User button: avatar circle 30×30 rgba(255,255,255,.16) with initials 12px/700 ("VA"), then name 13px ("E2E Version Admin").
- Row 2 — main navigation `<nav aria-label="Main">`: `max-width:1320px; margin:0 auto; padding:4px 16px 0; height:42px; display:flex; gap:4px`. Each tab is an `<a>`: padding 0 12px, 14px, `border-bottom:3px solid` (`--nx-tint-border` when current, transparent otherwise), weight 700 current / 500 other, colour white current / rgba(255,255,255,.78) other, `aria-current="page"` on current.
  - Tab order: **Home · Hierarchies · Worklist [3] · Queries · Compare · As-Was · Sandboxes · Versions** (the Admin entry used in early boards is replaced by Sandboxes + Versions).
  - Worklist badge: 11px/700, text `--nx-header-bg`, bg `--nx-tint-border`, radius 999, padding 0 6px, line-height 16px. Shows the number of requests waiting for the user's decision; hidden when 0.

**A5.2 Context bar** (white, bottom border 1px `--nx-border`): inner `max-width:1320px; padding:10px 24px; display:flex; align-items:center; gap:24px`.
1. Label "Version" 13px `--nx-text-2` + select (32px, 4px radius, 13px/600): options "2026-11 · Working", "2026-10 · Finalized". Changing it re-scopes every screen.
2. Label "Viewing as of" + date input (32px, 4px radius, 13px). Default today.
3. "Work in" + joined toggle (A4.7, 30px, 13px): **Live data** | **Sandbox**. Selected = `--nx-brand`.
4. Spacer, then link "Manage versions" (13px, `--nx-link`) → Versions screen.

**A5.3 Sticky draft bar** (all screens except Sandboxes): `position:sticky; bottom:0; background:#FFF; border-top:1px solid --nx-border; box-shadow:0 -2px 8px rgba(0,0,0,.04); z-index:20`. Inner `max-width:1320px; padding:10px 24px; display:flex; align-items:center; gap:14px; font-size:14px`.
1. Count badge 26px pill: with drafts bg `--nx-tint-3` text `--nx-link`; empty bg `--nx-divider-soft` text `--nx-text-2`. 13px/700.
2. Text: **"Changes in your draft"** (700; singular "Change in your draft"; empty "No changes in your draft") + ` · nothing changes until approved` (`--nx-text-2`; empty: "· edits you make are collected here").
3. Spacer.
4. Link-style button "View changes" / "Hide changes" toggles a popover (`aria-expanded`).
5. Secondary 34px "Discard".
6. Primary 34px "Review and submit".
- When empty, Discard and Review and submit get `aria-disabled="true"` and `opacity:.5` (do not use the boolean `disabled` attribute binding).
- Popover: absolute, bottom calc(100% + 8px), left 24px, width 480px, radius 8px, shadow A3, padding 8px 0. Header "Changes in your draft" 13px/600 `--nx-text-2` padding 6px 16px. Each item row min-height 42px, top divider, text button 14px (clicking navigates to the node), remove × icon button 30×30 `aria-label="Remove from draft"`. Empty: "Your draft is empty." 14px `--nx-text-2`.
- Example items: "Move 34307 · AGQNT → AGCOE in ORGTL", "Edit 34301 · name “QNT Analytics Platform”".

### A6. Accessibility & behaviour baseline
- Every input has a `<label>` (visually hidden class when the design shows none: `position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0)`).
- Tabs use `role="tablist"/"tab"` + `aria-selected`; segmented toggles use `aria-pressed`; switches `role="switch"` + `aria-checked`; list selection `aria-current`.
- Decorative icons/dots `aria-hidden="true"`; meaning is always also in text.
- Disabled-looking actions use `aria-disabled` + reduced opacity + a visible reason somewhere nearby.
- Text never truncates with ellipsis in lists/detail; long names wrap (`overflow-wrap:anywhere` on names/paths).
- All numbers use `en-US` grouping (2,818). Dates: "18 Nov 2026"; times 24h "10:30".

### A7. Shared demo data (use the same values on every screen)
- User: E2E Version Admin (`e2e_vadmin`, initials VA). Other people: u_editor1, u_approver1, u_admin, E2E Steward.
- Versions: **2026-11** Working (business date 18 Nov 2026, created from 2026-10), **E2E working** Working (15 Nov 2026), **2026-10** Finalized (28 Oct 2026, frozen 18 Nov 2026 06:22).
- Hierarchies: ORGTL "Total Fidelity" Primary 2,818 nodes 14 levels; ORGTP "Total Fidelity Technology" Alternate 516/12; AMBUS "Asset Management BU Alternate" Alternate 1,501/12; plus 12 alternates: E2E_H E2E Legal Group 142/8, E2E_MGMT E2E Management View 96/7, FINRPT Finance Reporting 1,210/10, LEGENT Legal Entities 388/6, GEOREG Geographic Regions 240/5, CCOWN Cost Centre Owners 2,236/9, HRORG HR Organisation 1,890/11, TAXREP Tax Reporting 610/8, OPSDEL Operations Delivery 744/9, PRODLN Product Lines 455/7, RISKV Risk View 302/6, PROJTL Project Portfolio 128/5 (15 total).
- Key nodes: ORGTL › OC005 Asset Management › AM Asset Management Executive › AMT Asset Management Technology › AMQNT Quant Technology › {AGCOE AM AI Center of Excellence, AGQNT US Quant Technology}. Leaves under AGQNT: 31679 UK QNT Tech, 34295 QNT Engineering, 34299 QNT Leadership, 34301 QNT Analytics Platform, 34322 QNT Contingent (Inactive). 34307 QNT Quant Dev (under AGCOE in ORGTL; under AGQNT in ORGTP/AMBUS).
- Draft (pending, not submitted): Move 34307 AGQNT → AGCOE in ORGTL; Rename 34301 → "QNT Analytics Platform".
- Integrity rules: CODE_UNIQUE, PARENT_EXISTS, PARENT_IS_LIMB, NO_CYCLES.

---

## Part B — Versions screen

### B0. What changed from the previous Versions spec
- Real `<table>` with **fixed column widths** (`table-layout: fixed`) and **vertical column dividers**.
- The version **name is the link** to Hierarchies. The separate "Open" / "Compare" text links are gone from the row.
- Actions column holds only **one row button** + a **⋯ menu**, so it can never overflow into other columns.
- ⋯ menu contains only: Open in Hierarchies, Compare with <previous>, Edit details, Export as CSV, and for Submitted versions "Reopen for changes".
- A blocked reason (e.g. "1 request in review") shows as a small line **under the status**, never inside the actions cell.
- No inline `<details>`/"▶ More" disclosure anywhere (it changed row height and broke the layout).
- Loading: skeleton rows inside the table body + the universal top bar (see the loading kit).

### B1. Page structure
```
[A5.1 Header — tab "Versions" current] [A5.2 Context bar]
[nx-top-progress]
┌ content column (max 1320, padding 26 24 28, gap 20) ─────────────────────────────────────┐
│ B2  Versions ............................................................... [New version] │
│ B3  ┌ panel ────────────────────────────────────────────────────────────────────────────┐ │
│     │ tabs: All 6 · Working 3 · Submitted 0 · Finalized 2 · Expired 1                    │ │
│     ├───────────────────────────┬───────────┬──────────┬──────────┬──────────┬──────────┤ │
│     │ Version                   │ Business  │ Status   │ Frozen   │ Created  │ Actions  │ │
│     │                           │ date      │          │          │ from     │          │ │
│     ├───────────────────────────┼───────────┼──────────┼──────────┼──────────┼──────────┤ │
│     │ 2026-11  [Current]        │18 Nov 2026│ Working  │ —        │ 2026-10  │[Submit](⋯)│ │
│     │ November month-end · …    │           │ 1 request│          │          │          │ │
│     │                           │           │ in review│          │          │          │ │
│     └───────────────────────────┴───────────┴──────────┴──────────┴──────────┴──────────┘ │
└──────────────────────────────────────────────────────────────────────────────────────────┘
[A5.3 Draft bar]
```

### B2. Title row
- Container: `display:flex; align-items:center; gap:16px`.
- h1 "Versions" — 30px / 700 / letter-spacing −0.01em / `--nx-text`, `flex:1 1 auto`.
- Right: **New version** — primary pill (A4.1), 38px, padding `0 20px`. Version admins only (hidden for others).
- No sub line, no description, no info banner.

### B3. Panel
`<section aria-label="Versions">`: background `#FFFFFF`, border `1px solid --nx-border (#E5E1DC)`, radius 8px. **Do not set `overflow:hidden`** on this section (it would clip the ⋯ menu). If you need horizontal scrolling, put it on an inner wrapper and use a CDK overlay menu (B7).

#### B3.1 Status tabs
A4.9 tab row, `role="tablist" aria-label="Filter by status"`:
- Row: `display:flex; gap:4px; padding:0 12px; border-bottom:1px solid #E5E1DC`.
- Tabs in this order: **All · Working · Submitted · Finalized · Expired**, each followed by its count (count shown even when 0).
- Tab: height 46px, padding `0 10px`, `border-bottom:3px solid` (#368727 when selected, transparent otherwise), label 14px weight 700 selected / 500 not, colour #141414 selected / #5C5C5C not; count 13px/400 #8A8580.
- Selecting a tab filters rows and updates the URL `?status=`. Default "All".

#### B3.2 Table — element and widths
```html
<div class="vt-wrap">
  <table class="vt">
    <caption class="visually-hidden">Versions</caption>
    <colgroup>
      <col>                         <!-- Version: takes all remaining width -->
      <col style="width:128px">     <!-- Business date -->
      <col style="width:168px">     <!-- Status -->
      <col style="width:176px">     <!-- Frozen -->
      <col style="width:140px">     <!-- Created from -->
      <col style="width:196px">     <!-- Actions -->
    </colgroup>
    <thead>…</thead>
    <tbody>…</tbody>
  </table>
</div>
```
- `table-layout: fixed; width:100%; min-width:1100px; border-collapse:separate; border-spacing:0; font-size:14px`.
- `.vt-wrap { overflow-x:auto }` — only takes effect below ~1150px viewport; at 1440 the table fits without scrolling.
- Column widths never depend on content. Long content makes the **row taller**, never the column wider.

#### B3.3 Header row (`<thead>`)
| Column | Text | Align |
|---|---|---|
| 1 | Version | left |
| 2 | Business date | left |
| 3 | Status | left |
| 4 | Frozen | left |
| 5 | Created from | left |
| 6 | Actions | **right** |

Each `<th scope="col">`: `position:sticky; top:0; z-index:2; padding:10px 12px; background:#FCFBFA; border-bottom:1px solid #E5E1DC; font-size:13px; font-weight:600; color:#5C5C5C; white-space:nowrap; text-align:left`. First th `padding-left:20px`; last th `padding-right:20px; text-align:right`.

#### B3.4 Grid lines
- Horizontal: every body cell `border-top:1px solid #EEEAE5`, except cells in the first body row (the header's bottom border already separates).
- **Vertical:** every th and td except the first in its row: `border-left:1px solid #E5E1DC` (`th + th, td + td`).
- No outer vertical lines (the panel border is the frame). No zebra striping.

#### B3.5 Body cells
All `<td>`: `vertical-align:top; padding:14px 12px`. First column `padding-left:20px`; last `padding-right:20px`.

**Col 1 — Version**
- Line 1, `display:flex; flex-wrap:wrap; align-items:center; gap:4px 8px`:
  - **Name as link** → Hierarchies at that version (`/hierarchies?v=<id>`): 15px / 700 / line-height 1.35 / colour #141414 / `text-decoration:underline; text-decoration-color:#C9C4BE; text-underline-offset:3px; overflow-wrap:anywhere`. Hover: `text-decoration-color:#141414`. `title="Open <name> in Hierarchies"`.
  - **Current tag** (only for the version selected in the context bar): 12px/600, colour #2B6A1F, border `1px solid #A8CF9C`, radius 4px, padding `0 6px`, `white-space:nowrap`, text "Current".
- Line 2 (description): `margin-top:3px; font-size:13px; color:#8A8580; line-height:1.45; overflow-wrap:anywhere`. Full text, wraps, never truncated. If empty, omit the line.

**Col 2 — Business date**: `white-space:nowrap`, format `d MMM y` → "18 Nov 2026".

**Col 3 — Status**
- Line 1: status word, weight 600. Colours: Working #2B6A1F · Submitted #141414 · Finalized #141414 · Expired #8A8580.
- Line 2 (only when something blocks the row's main action): `margin-top:2px; font-size:12px; color:#8A8580; line-height:1.35`, e.g. "1 request in review", "2 requests in review".

**Col 4 — Frozen**: date-time `d MMM y, HH:mm` → "18 Nov 2026, 06:22" (#141414); if not frozen show "—" in #8A8580. Show times in the user's time zone; append the zone only if it differs from the user's ("06:22 GMT-5").

**Col 5 — Created from**: #5C5C5C, `overflow-wrap:anywhere`. Values: a version code ("2026-10"), "DRM export", or a longer source label ("DRM export (initial load, 28 Aug 2026)"); "—" when none.

**Col 6 — Actions**: `padding:9px 20px 9px 12px`. Inner `display:flex; align-items:center; justify-content:flex-end; gap:8px; flex-wrap:wrap` (wrap is a safety net; at 196px both items always fit).
1. **Row button** (optional) — secondary pill: height 32px, padding `0 14px`, radius 999px, border `1px solid #C9C4BE`, background #FFFFFF, 13px/600 #141414, `white-space:nowrap`.
2. **⋯ menu button** — 32×32 circle, border `1px solid #C9C4BE`, white, icon = three filled dots 16px (#141414). `aria-label="More actions for <name>"`, `aria-haspopup="menu"`, `aria-expanded`. When open: border #368727, background #F3F6F1.

#### B3.6 Current row
The row whose version equals the context-bar version: `tr` background `#F3F6F1`; first cell `box-shadow: inset 3px 0 0 #368727`. If it is the last row, give the first/last cells bottom radius 7px so the tint follows the panel corner.

#### B3.7 Row order
Status order **Working → Submitted → Finalized → Expired**, then business date **newest first** within each status.

#### B3.8 Empty filter
One row: `<td colspan="6">` padding 20px, 14px #5C5C5C, text `No <status lower-case> versions.` (e.g. "No expired versions.").

### B4. Row button — exactly one per row

| Row condition | Label | Who sees it | Click |
|---|---|---|---|
| Working, and it is the month-end version currently open (e.g. 2026-11) | **Submit** | Version admin | Status → Submitted. Toast "2026-11 submitted for finalization". |
| Working, any other working version | **Switch to** | Everyone | Sets the context-bar version to this one (it becomes Current). Toast "Now working in <name>". |
| Submitted | **Finalize** | Version admin | Freezes it (Frozen = now) → Finalized. Toast "<name> finalized". |
| Finalized | **Copy to new** | Version admin | Opens the New version dialog prefilled with "Created from = <name>". |
| Expired | — (none) | — | — |

**Blocked state (Submit):** when requests in that version are still open (in review / sent back):
- Button stays visible with `aria-disabled="true"`, opacity 0.5, `title="1 request is still in review"`; do **not** use the `disabled` attribute (it can't show a tooltip or receive focus).
- Status cell shows the note line "1 request in review".
- Clicking it does not submit; it shows toast "Finish or withdraw CR-1045 first" (name the request when there is exactly one; otherwise "Finish or withdraw the 2 open requests first").

Users without permission for a row action don't see that button at all (no disabled ghost).

### B5. ⋯ menu — the only items

Order and availability:

| # | Item | Shown for | Action |
|---|---|---|---|
| 1 | Open in Hierarchies | all | Navigate `/hierarchies?v=<id>` |
| 2 | Compare with `<previous>` | all with a previous version | Navigate `/compare?from=<previous>&to=<id>` (the user still presses Compare there) |
| 3 | Edit details | all except Expired; version admins | Opens Edit dialog (B6) |
| 4 | Export as CSV | all | Downloads all hierarchies of that version; toast "Exported <name>" |
| — | divider | Submitted only | |
| 5 | Reopen for changes | Submitted only; version admins | Status → Working; toast "<name> reopened" |

"Previous" = the version this one was created from, or else the newest Finalized version with an earlier business date.

Menu panel: width 230px; white; border `1px solid #E5E1DC`; radius 8px; shadow `0 8px 24px rgba(0,0,0,0.10)`; padding `4px 0`; positioned **below the ⋯ button, right edges aligned, 6px gap**; flips above when there's no room below. Items: `role="menuitem"`, full-width buttons, padding `9px 14px`, 14px #141414, left aligned, hover background #F9F7F5. Divider: 1px #EEEAE5, margin 4px 0.
Behaviour: opens on click/Enter/Space; ↑/↓ moves focus; Esc or outside click closes and returns focus to ⋯; choosing an item closes the menu first, then runs the action. Only one menu open at a time.

### B6. Dialogs
**New version** (primary button, and "Copy to new"):
- Modal 480px, radius 8px, padding 24px; title 18px/700 "New version".
- Fields (label 13px/600 #5C5C5C, inputs 36px, 4px radius):
  1. Name — text, required, max 40, placeholder "e.g. 2026-12"
  2. Business date — date, required
  3. Created from — select of Finalized versions (default the newest; prefilled when opened from "Copy to new")
  4. Description — textarea 3 rows, optional, max 200
- Footer right: secondary "Cancel", primary "Create version" (busy spinner while saving, A4/nxBusy).
- Errors under the field in 13px #B42318: "Enter a name", "A version with this name already exists", "Pick a business date".
- On success: dialog closes, new row appears (Working), toast "2026-12 created from 2026-11".

**Edit details**: same modal, title "Edit <name>", fields Name (read-only for Finalized/Submitted), Description. Primary "Save".

### B7. Angular implementation

**Data model**
```ts
export type VersionStatus = 'Working' | 'Submitted' | 'Finalized' | 'Expired';
export interface VersionRow {
  id: string; name: string; description?: string;
  businessDate: string;      // ISO date
  status: VersionStatus;
  frozenAt?: string | null;  // ISO date-time
  createdFrom?: string | null;
  previousId?: string | null;
  openRequests: { id: string }[];   // requests still in review/sent back in this version
  isMonthEnd: boolean;              // the working version that gets submitted
}
```

**Template (`versions.component.html`)**
```html
<div class="page">
  <div class="title-row">
    <h1>Versions</h1>
    <button *ngIf="isAdmin" class="btn-primary" (click)="openNew()">New version</button>
  </div>

  <section class="panel" aria-label="Versions">
    <div class="tabs" role="tablist" aria-label="Filter by status">
      <button *ngFor="let t of tabs" role="tab" [attr.aria-selected]="t.key === filter" [class.on]="t.key === filter"
              (click)="setFilter(t.key)">{{ t.label }} <span class="count">{{ count(t.key) }}</span></button>
    </div>

    <div class="vt-wrap">
      <table class="vt">
        <caption class="visually-hidden">Versions</caption>
        <colgroup><col><col class="c-date"><col class="c-status"><col class="c-frozen"><col class="c-from"><col class="c-actions"></colgroup>
        <thead><tr>
          <th scope="col">Version</th><th scope="col">Business date</th><th scope="col">Status</th>
          <th scope="col">Frozen</th><th scope="col">Created from</th><th scope="col" class="ta-r">Actions</th>
        </tr></thead>

        <tbody *ngIf="state() === 'loading'">
          <tr *ngFor="let i of [1,2,3,4]">
            <td *ngFor="let w of ['70%','70%','55%','70%','60%','80%']"><span class="nx-sk" [style.width]="w"></span></td>
          </tr>
        </tbody>

        <tbody *ngIf="state() === 'error'">
          <tr><td colspan="6" class="empty">Couldn’t load versions. <a (click)="load()">Try again</a></td></tr>
        </tbody>

        <tbody *ngIf="state() === 'ready'">
          <tr *ngIf="!rows().length"><td colspan="6" class="empty">No {{ filter | lowercase }} versions.</td></tr>
          <tr *ngFor="let v of rows(); trackBy: byId" [class.is-current]="v.id === currentId()">
            <td>
              <div class="name-line">
                <a class="name" [routerLink]="['/hierarchies']" [queryParams]="{ v: v.id }" [title]="'Open ' + v.name + ' in Hierarchies'">{{ v.name }}</a>
                <span class="tag" *ngIf="v.id === currentId()">Current</span>
              </div>
              <div class="desc" *ngIf="v.description">{{ v.description }}</div>
            </td>
            <td class="nowrap">{{ v.businessDate | date:'d MMM y' }}</td>
            <td>
              <div class="status" [attr.data-s]="v.status">{{ v.status }}</div>
              <div class="note" *ngIf="blockedNote(v) as n">{{ n }}</div>
            </td>
            <td [class.muted]="!v.frozenAt">{{ v.frozenAt ? (v.frozenAt | date:'d MMM y, HH:mm') : '—' }}</td>
            <td class="wrap muted-2">{{ v.createdFrom || '—' }}</td>
            <td class="c-act">
              <div class="act">
                <button *ngIf="primary(v) as p" class="btn-sm" [class.is-disabled]="p.blocked"
                        [attr.aria-disabled]="p.blocked" [title]="p.reason || ''" [nxBusy]="busyId() === v.id"
                        (click)="p.run()">{{ p.label }}</button>
                <button class="icon-btn" [cdkMenuTriggerFor]="menu" [attr.aria-label]="'More actions for ' + v.name">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                    <circle cx="5" cy="12" r="1.8"/><circle cx="12" cy="12" r="1.8"/><circle cx="19" cy="12" r="1.8"/></svg>
                </button>
                <ng-template #menu>
                  <div class="menu" cdkMenu>
                    <button cdkMenuItem (cdkMenuItemTriggered)="openIn(v)">Open in Hierarchies</button>
                    <button cdkMenuItem *ngIf="v.previousId" (cdkMenuItemTriggered)="compare(v)">Compare with {{ v.previousId }}</button>
                    <button cdkMenuItem *ngIf="isAdmin && v.status !== 'Expired'" (cdkMenuItemTriggered)="openEdit(v)">Edit details</button>
                    <button cdkMenuItem (cdkMenuItemTriggered)="exportCsv(v)">Export as CSV</button>
                    <ng-container *ngIf="isAdmin && v.status === 'Submitted'">
                      <hr><button cdkMenuItem (cdkMenuItemTriggered)="reopen(v)">Reopen for changes</button>
                    </ng-container>
                  </div>
                </ng-template>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</div>
```

**Component logic (`versions.component.ts`, key parts)**
```ts
filter: 'All' | VersionStatus = 'All';
tabs = [{ key: 'All', label: 'All' }, { key: 'Working', label: 'Working' }, { key: 'Submitted', label: 'Submitted' },
        { key: 'Finalized', label: 'Finalized' }, { key: 'Expired', label: 'Expired' }];
private order: VersionStatus[] = ['Working', 'Submitted', 'Finalized', 'Expired'];

rows = computed(() => this.all()
  .filter(v => this.filter === 'All' || v.status === this.filter)
  .sort((a, b) => this.order.indexOf(a.status) - this.order.indexOf(b.status)
               || +new Date(b.businessDate) - +new Date(a.businessDate)));

count(k: string) { return k === 'All' ? this.all().length : this.all().filter(v => v.status === k).length; }

blockedNote(v: VersionRow) {
  const n = v.openRequests.length;
  return v.status === 'Working' && v.isMonthEnd && n ? `${n} ${n === 1 ? 'request' : 'requests'} in review` : null;
}

primary(v: VersionRow) {
  if (v.status === 'Working' && v.isMonthEnd && this.isAdmin) {
    const n = v.openRequests.length;
    return { label: 'Submit', blocked: n > 0,
      reason: n ? `${n} ${n === 1 ? 'request is' : 'requests are'} still in review` : '',
      run: () => n ? this.toast(n === 1 ? `Finish or withdraw ${v.openRequests[0].id} first` : `Finish or withdraw the ${n} open requests first`)
                   : this.submit(v) };
  }
  if (v.status === 'Working' && v.id !== this.currentId()) return { label: 'Switch to', blocked: false, run: () => this.switchTo(v) };
  if (v.status === 'Submitted' && this.isAdmin) return { label: 'Finalize', blocked: false, run: () => this.finalize(v) };
  if (v.status === 'Finalized' && this.isAdmin) return { label: 'Copy to new', blocked: false, run: () => this.openNew(v.id) };
  return null;
}
compare(v: VersionRow) { this.router.navigate(['/compare'], { queryParams: { from: v.previousId, to: v.id } }); }
openIn(v: VersionRow) { this.router.navigate(['/hierarchies'], { queryParams: { v: v.id } }); }
byId = (_: number, v: VersionRow) => v.id;
```
Imports: `CdkMenu, CdkMenuItem, CdkMenuTrigger` from `@angular/cdk/menu`, `RouterLink`, `DatePipe`, `LowerCasePipe`, `NgFor`, `NgIf`, `BusyDirective` (loading kit).

**Styles (`versions.component.scss`)**
```scss
.page { max-width:1320px; margin:0 auto; padding:26px 24px 28px; display:flex; flex-direction:column; gap:20px; }
.title-row { display:flex; align-items:center; gap:16px; h1 { flex:1; margin:0; font-size:30px; font-weight:700; letter-spacing:-.01em; } }
.panel { background:#fff; border:1px solid #E5E1DC; border-radius:8px; }          /* no overflow:hidden */
.tabs { display:flex; gap:4px; padding:0 12px; border-bottom:1px solid #E5E1DC;
  button { height:46px; padding:0 10px; border:0; border-bottom:3px solid transparent; background:none;
    font:500 14px inherit; color:#5C5C5C; cursor:pointer; white-space:nowrap;
    &.on { border-bottom-color:#368727; font-weight:700; color:#141414; } }
  .count { font-size:13px; font-weight:400; color:#8A8580; margin-left:4px; } }

.vt-wrap { overflow-x:auto; }
.vt { width:100%; min-width:1100px; table-layout:fixed; border-collapse:separate; border-spacing:0; font-size:14px; }
.c-date{width:128px} .c-status{width:168px} .c-frozen{width:176px} .c-from{width:140px} .c-actions{width:196px}
.vt th { position:sticky; top:0; z-index:2; text-align:left; padding:10px 12px; background:#FCFBFA;
  border-bottom:1px solid #E5E1DC; font-size:13px; font-weight:600; color:#5C5C5C; white-space:nowrap; }
.vt td { vertical-align:top; padding:14px 12px; border-top:1px solid #EEEAE5; }
.vt tbody tr:first-child td { border-top:0; }
.vt th + th, .vt td + td { border-left:1px solid #E5E1DC; }
.vt th:first-child, .vt td:first-child { padding-left:20px; }
.vt th:last-child, .vt td:last-child { padding-right:20px; }
.vt tr.is-current { background:#F3F6F1; td:first-child { box-shadow:inset 3px 0 0 #368727; } }
.vt tbody tr:last-child td:first-child { border-bottom-left-radius:7px; }
.vt tbody tr:last-child td:last-child { border-bottom-right-radius:7px; }
.ta-r { text-align:right !important; }

.name-line { display:flex; flex-wrap:wrap; align-items:center; gap:4px 8px; }
.name { font-size:15px; font-weight:700; line-height:1.35; color:#141414; overflow-wrap:anywhere;
  text-decoration:underline; text-decoration-color:#C9C4BE; text-underline-offset:3px;
  &:hover { text-decoration-color:#141414; } }
.tag { font-size:12px; font-weight:600; color:#2B6A1F; border:1px solid #A8CF9C; border-radius:4px; padding:0 6px; white-space:nowrap; }
.desc { margin-top:3px; font-size:13px; color:#8A8580; line-height:1.45; overflow-wrap:anywhere; }
.status { font-weight:600; color:#141414;
  &[data-s="Working"] { color:#2B6A1F; } &[data-s="Expired"] { color:#8A8580; } }
.note { margin-top:2px; font-size:12px; color:#8A8580; line-height:1.35; }
.nowrap { white-space:nowrap; } .wrap { overflow-wrap:anywhere; }
.muted { color:#8A8580; } .muted-2 { color:#5C5C5C; }
.empty { padding:20px !important; color:#5C5C5C; a { color:#2B6A1F; text-decoration:underline; cursor:pointer; } }

.c-act { padding-top:9px !important; padding-bottom:9px !important; }
.act { display:flex; align-items:center; justify-content:flex-end; gap:8px; flex-wrap:wrap; }
.btn-sm { height:32px; padding:0 14px; border-radius:999px; border:1px solid #C9C4BE; background:#fff;
  font:600 13px inherit; color:#141414; cursor:pointer; white-space:nowrap;
  &.is-disabled { opacity:.5; cursor:not-allowed; } }
.icon-btn { width:32px; height:32px; flex:0 0 32px; border-radius:50%; border:1px solid #C9C4BE; background:#fff;
  display:flex; align-items:center; justify-content:center; padding:0; color:#141414; cursor:pointer;
  &[aria-expanded="true"] { border-color:#368727; background:#F3F6F1; } }

.nx-sk { display:block; height:12px; border-radius:4px; background:linear-gradient(90deg,#EEEAE5 0%,#F7F5F2 40%,#EEEAE5 80%);
  background-size:200% 100%; animation:nxsh 1.4s ease-in-out infinite; }
@keyframes nxsh { 0% { background-position:100% 0 } 100% { background-position:-100% 0 } }
.visually-hidden { position:absolute; width:1px; height:1px; overflow:hidden; clip:rect(0 0 0 0); }
```
**Global styles** (the CDK menu renders in an overlay outside the component, so put this in `styles.scss`):
```scss
@import '@angular/cdk/overlay-prebuilt.css';
.menu { width:230px; background:#fff; border:1px solid #E5E1DC; border-radius:8px; box-shadow:0 8px 24px rgba(0,0,0,.10); padding:4px 0;
  button { display:block; width:100%; text-align:left; padding:9px 14px; border:0; background:none; font:14px inherit; color:#141414; cursor:pointer;
    &:hover, &:focus-visible { background:#F9F7F5; outline:none; } }
  hr { border:0; height:1px; background:#EEEAE5; margin:4px 0; } }
```

### B8. API mapping (expected endpoints)
| UI | Call |
|---|---|
| Load table | `GET /api/versions` → `VersionRow[]` (include `openRequests`, `previousId`) |
| Submit | `POST /api/versions/{id}/submit` (409 with open request ids → show the blocked toast) |
| Finalize | `POST /api/versions/{id}/finalize` → returns `frozenAt` |
| Reopen | `POST /api/versions/{id}/reopen` |
| Switch to | client side: set context-bar version (persist in user prefs) |
| New / Copy to new | `POST /api/versions { name, businessDate, createdFrom, description }` |
| Edit details | `PATCH /api/versions/{id} { name?, description? }` |
| Export as CSV | `GET /api/versions/{id}/export.csv` (file download) |

After any mutation, reload the list and keep the current filter.

### B9. Loading, errors, toasts
- First load: universal top bar + 4 skeleton rows inside `<tbody>` (header row stays real).
- Row action in progress: `[nxBusy]` spinner on that row's button; the ⋯ menu stays usable.
- Load error: one full-width row "Couldn’t load versions. Try again".
- Toast (A4.14, bottom 76px, 2.5s): "2026-11 submitted for finalization", "Now working in E2E working", "2026-11 finalized", "2026-11 reopened", "Exported 2026-10", "2026-12 created from 2026-11", "Finish or withdraw CR-1045 first".

### B10. Demo data (matches the mockup)
| Version | Description | Business date | Status | Note | Frozen | Created from | Row button |
|---|---|---|---|---|---|---|---|
| **2026-11** (Current) | November month-end · open until 30 Nov | 18 Nov 2026 | Working | 1 request in review | — | 2026-10 | Submit (blocked) |
| E2E working | Testing only | 15 Nov 2026 | Working | | — | — | Switch to |
| Q4 2026 restructure dry run – Finance and Technology alignment | Trial of the Q4 reorganisation across ORGTL, AMBUS and CCOWN before it goes into the month-end version. Owned by E2E Steward; do not use for reporting. | 12 Nov 2026 | Working | | — | E2E working | Switch to |
| 2026-10 | October month-end baseline | 28 Oct 2026 | Finalized | | 18 Nov 2026, 06:22 | DRM export | Copy to new |
| 2026-09 | September month-end baseline | 29 Sep 2026 | Finalized | | 21 Oct 2026, 18:05 | 2026-08 | Copy to new |
| 2026-08 | Kept for history only | 28 Aug 2026 | Expired | | 22 Sep 2026, 07:40 | DRM export (initial load, 28 Aug 2026) | — |

Tab counts: All 6 · Working 3 · Submitted 0 · Finalized 2 · Expired 1. The open request blocking 2026-11 is CR-1045.

### B11. Edge cases
- **Very long name/description:** wraps inside column 1; row grows; no other column moves. Test with the Q4 row.
- **Long "Created from":** wraps within 140px.
- **Narrow screens (<1150px):** the table scrolls sideways inside its wrapper; header stays aligned; the ⋯ menu still shows fully (CDK overlay).
- **Many versions (50+):** header stays visible while the page scrolls (sticky `th`). Above 100 rows add pagination of 50 under the table (text buttons "Previous" / "Next", 13px, right-aligned, plus "1–50 of 132").
- **Current version filtered out by the tab:** fine; the Current tag shows again under All.
- **Non-admin user:** no New version, no Submit/Finalize/Copy to new buttons, no Edit/Reopen menu items; Switch to and the read actions remain.

### B12. Acceptance checklist
- [ ] Six columns with the exact widths; Version column flexible.
- [ ] Vertical and horizontal grid lines as in B3.4; header tinted #FCFBFA and sticky.
- [ ] Name is a link; there are no Open/Compare text links in the row.
- [ ] Actions cell = max one pill button + ⋯; never overlaps the Created from column.
- [ ] ⋯ menu has only: Open in Hierarchies, Compare with …, Edit details, Export as CSV (+ Reopen for changes on Submitted).
- [ ] Blocked Submit: faded, tooltip, note under the status, toast on click.
- [ ] Current row tinted with a green left edge and a "Current" tag.
- [ ] Long text wraps, never cut; nothing overlaps at 1100–1440px widths.
- [ ] Skeleton rows on load; spinner on row actions.
