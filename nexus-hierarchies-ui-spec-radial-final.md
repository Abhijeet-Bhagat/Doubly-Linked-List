# Nexus – Hierarchies screen + Explore tree · UI specification

**Route:** `/hierarchies` (query `?h=<code>&node=<code>`) · **Header tab:** Hierarchies · **Mockup board:** "Hierarchies (aligned with new Home)" — `Hierarchies-F.dc.html`
**Purpose:** find a node fast, understand where it sits (in this and every other hierarchy), see its properties and history, and add changes to the draft. Must scale to 15+ hierarchies and trees of thousands of nodes.


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

## Part B — Hierarchies screen

### B1. Page structure
```
[A5.1 Header] [A5.2 Context bar]
┌ content column (max 1320, padding 24, gap 18) ─────────────────────────────────┐
│ B2 Title row:  "Hierarchies"  Find a node, see where it lives, propose a change.  Export │
│ ┌ B3 Navigator (400px) ┐  ┌ B4 Node panel (flex 1) ──────────────────────────┐ │
│ │ hierarchy picker      │  │ path ribbon                                      │ │
│ │ node search           │  │ name / meta            [Edit details][Move][⋯]  │ │
│ │ "Showing n" Hide inact│  │ pending-in-draft callout (when relevant)         │ │
│ │ tree                  │  │ inline edit form (when open)                     │ │
│ │                       │  │ WHERE IT LIVES list                              │ │
│ │                       │  │ tabs: Properties | Children | History            │ │
│ └───────────────────────┘  └──────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────────────┘
[A5.3 Draft bar]
```
Two panels side by side: `display:flex; gap:20px; align-items:stretch; flex:1; min-height:660px`.

### B2. Title row
`display:flex; align-items:baseline; gap:14px`:
- h1 **28px**/700 "Hierarchies".
- 14px `--nx-text-2`: "Find a node, see where it lives, propose a change."
- spacer; link 13px "Export" (exports the current hierarchy as CSV).
- Then (gap 14px, `align-self:center`) secondary pill **"Explore tree"**: height 34px, padding `0 14px`, gap 8px, white, 1px `--nx-control-border`, 14px/600 `--nx-text`, with a 15px "network" icon (centre circle + 4 small circles + connectors, stroke `--nx-brand` 2px) before the label. Navigates to `/hierarchies/explore?h=<hier>&centre=<parent of selected node>&sel=<selected node>` (Part C).

### B3. Navigator panel (left)
`<section aria-label="Navigator">`, width 400px fixed, panel style, `display:flex; flex-direction:column; overflow:hidden`.

**B3.1 Top block** `padding:14px 14px 12px; display:flex; flex-direction:column; gap:10px; border-bottom:1px solid --nx-divider`.

**(a) Hierarchy picker** — a button that opens a listbox popover (this replaces any tab strip of hierarchies so it scales to 15+).
- Button: `display:flex; align-items:center; gap:10px; width:100%; min-height:48px; padding:6px 12px; border:1px solid (open ? --nx-brand : --nx-control-border); border-radius:6px; background:#fff; text-align:left`. `aria-haspopup="listbox" aria-expanded`.
  1. Icon tile 32×32, radius 6, bg `--nx-tint-2`, colour `--nx-brand`, hierarchy (org-chart) icon 18px.
  2. Text column: hierarchy name 15px/700 line-height 1.25 ("Total Fidelity"); under it 12px `--nx-text-2` "ORGTL · Primary · 2,818 nodes · 14 levels".
  3. 12px `--nx-text-2` nowrap "1 of 15".
  4. Chevron 14px (down when closed, up when open).
- Popover: absolute top 54px, left/right 0, white, 1px `--nx-control-border`, radius 6, shadow `0 10px 24px rgba(0,0,0,.14)`, z 30, `max-height:420px`, column.
  - Filter box: padding 10px, bottom divider; search input 34px, 4px radius, placeholder "Find a hierarchy" (hidden label same).
  - Listbox (`role="listbox" aria-label="Hierarchies"`, scrolls, padding 4px 0 6px):
    - Group header tiny caps 11px/700 0.08em `--nx-text-3`, padding 10px 14px 4px: "PRIMARY" then "ALTERNATES · <n>".
    - Option (`role="option" aria-selected`): `display:flex; align-items:center; gap:10px; padding:8px 14px`; selected bg `--nx-tint-2`, else white.
      - code 13px/700 fixed width 74px
      - name 14px flex 1
      - green dot 7×7 `--nx-brand` **only if the hierarchy contains the selected node** (`title="Contains the selected node"`)
      - node count 12px `--nx-text-3` right-aligned width 72px ("2,818 nodes")
    - No match: 13px `--nx-text-2` "No hierarchy matches “<q>”."
  - Footer: padding 8px 14px, top divider, 12px `--nx-text-2`: green dot + "Contains <selected node code>" (legend).
  - Picking an option: switches hierarchy, closes popover, clears filter, keeps the same node selected if it exists there (else selects the hierarchy top node).

**(b) Node search**
- Input 38px, 4px radius, padding `0 12px 0 34px`, search icon left, placeholder "Find by code or name", hidden label "Find a node".
- At ≥2 characters a results popover appears: absolute top 42px, full width, radius 6, shadow `0 8px 20px rgba(0,0,0,.12)`, z 20. Up to **6** results, each a button with `padding:9px 12px`, top divider from 2nd: line 1 14px "**<name>** <code in --nx-text-2>"; line 2 12px `--nx-text-2` full path "ORGTL / OC005 / AM / AMT / AMQNT / AGCOE / 34307". Picking selects the node, expands its path, clears the box. No results: "No nodes match “<q>”."

**(c) Count + filter row** `display:flex; align-items:center; gap:8px; 12px --nx-text-2`:
- "Showing <visible rows> of <total nodes> nodes" (e.g. "Showing 14 of 2,818 nodes").
- spacer
- Checkbox label 13px `--nx-text`: checkbox 14×14 `accent-color:--nx-brand` + "Hide inactive". When on, inactive nodes are hidden except the selected one.

**B3.2 Tree** `role="tree" aria-label="<hier code>"`, `flex:1; overflow:auto; padding:6px 6px 10px`. Rendered rows only for expanded branches (virtualise for >500 visible rows).
- Row (`role="treeitem" aria-selected`): `display:flex; align-items:center; min-height:34px; border-radius:4px`; selected bg `--nx-tint-2`.
  1. Indent spacer width `4 + depth×16` px with vertical guide lines: `background-image: repeating-linear-gradient(to right, transparent 0 11px, #E5E1DC 11px 12px, transparent 12px 16px)` (none at depth 0).
  2. Expander 22×22: chevron (right closed / down open), colour `--nx-text-2` (selected row: `--nx-header-bg`), `aria-label="Expand|Collapse <code>"`, `aria-expanded`. Leaf rows show instead a 5×5 dot (`#9E9890`, inactive `#D6D1CB`).
  3. Select button (flex 1, `align-items:baseline; gap:8px; padding:6px 8px 6px 2px`):
     - name 14px (selected 700, else 400) line-height 1.35, `overflow-wrap:anywhere` (never truncated)
     - code 12px (`--nx-text-3`; selected `--nx-link`)
     - spacer
     - **draft marker**: 7×7 white circle with `box-shadow: inset 0 0 0 2px --nx-brand`, `title="Pending in your draft"`, only on nodes with a pending draft change in this hierarchy
     - collapsed-child count 12px `--nx-text-3` (shown only when the node has children and is collapsed).
  - Inactive nodes: text `--nx-text-3`.
- Default: the path to the selected node is expanded. Expansion state is remembered per hierarchy.
- Keyboard: ↑/↓ move, →/← expand/collapse, Enter selects.

### B4. Node panel (right)
`<section aria-label="Node">`, panel, `flex:1; min-width:0; display:flex; flex-direction:column; overflow:hidden`. Top area `padding:20px 26px 0; column; gap:16px`.

**B4.1 Path ribbon** `<nav aria-label="Path">`, wraps (`row-gap:8px`). For each ancestor + the node:
- Pill button 28px tall, padding `0 10px 0 4px`, radius 999, 13px, `title=<name>`:
  - level badge: min 20×20 circle, 11px/700, number = depth (1 = top); ancestors bg `#F3EFEA` text `--nx-text-2`; current bg rgba(255,255,255,.2) text white
  - then the code.
  - Ancestors: white bg, 1px `--nx-border`, 500 weight, `--nx-text`. Current (`aria-current="page"`): bg `--nx-brand`, border `--nx-brand`, white, 700.
- Between pills a 14×1 line `--nx-control-border`.
- Clicking an ancestor selects it.

**B4.2 Identity row** `display:flex; align-items:flex-start; gap:20px; flex-wrap:wrap`:
- Left (`flex:1 1 340px`): h2 30px/700 line-height 1.15 node name (e.g. "QNT Quant Dev"); meta 15px `#4A4A4A` "34307 · Leaf · Quant development · **Active**" — status 600 in `--nx-link` (Active) or `--nx-text-3` (Inactive).
- Right actions (gap 8px):
  1. Primary 38px "Edit details" (toggles B4.4).
  2. Secondary 38px "Move" (opens a parent picker that only lists valid limbs; invalid parents are not selectable — rule PARENT_IS_LIMB / NO_CYCLES).
  3. Secondary 38px "Add child" — **only for Limb nodes**.
  4. Round 38×38 outline "More actions" button (⋯ icon), `aria-haspopup="menu"`. Menu (`role="menu"`, absolute right 0 top 44px, width 250px, radius 6, shadow) items 14px padding 9px 14px:
     - "Add to another hierarchy"
     - "Inactivate"
     - "Remove from <hier>"
     - divider
     - "Delete from all hierarchies" in `--nx-danger`.

**B4.3 Pending-in-draft callout** (only when the selected node has a draft change relevant to this hierarchy): `display:flex; align-items:center; gap:14px; flex-wrap:wrap; background:--nx-tint; border:1px solid #DCE6D8; border-radius:6px; padding:12px 14px`:
- draft marker 8×8
- "Pending in your draft" 14px/700
- chips: old value chip (white, 1px `#DCE6D8`, radius 999, padding 2px 10px, `#6B6B6B`, line-through) → arrow icon → new value chip (white, 1px `--nx-control-border`, 700)
- scope text 13px: "new parent in ORGTL only" / "new name in every hierarchy"
- spacer; link-button "Undo" (removes the change from the draft).
- Data: 34307 → "AGQNT" → "AGCOE" · "new parent in ORGTL only". 34301 → "QNT Analytics" → "QNT Analytics Platform" · "new name in every hierarchy".

**B4.4 Inline edit form** (when "Edit details" toggled): box 1px `--nx-control-border`, radius 6, padding 14, bg `--nx-surface-2`, column gap 12.
- Two fields side by side (`flex:1 1 240px`, gap 14): "Name" (label 13px/600; input 38px, 4px radius, maxlength 60) and "Description".
- Footer row: 13px `--nx-text-2` hint "A new name shows in every hierarchy this node is in." (flex 1); secondary 36px "Cancel"; primary 36px "Add to draft".
- Saving never writes to live data; it adds to the draft and shows B4.3.

**B4.5 WHERE IT LIVES** (column gap 8):
- Header: tiny caps "WHERE IT LIVES" + 13px `--nx-text-2` "In 7 of 15 hierarchies".
- List container 1px `--nx-border`, radius 6. Row button (`aria-current` on the current hierarchy): `display:grid; grid-template-columns:84px minmax(0,1fr) 132px 64px minmax(0,1.1fr); gap:12px; padding:9px 12px; align-items:center`; top divider from 2nd row; current row = selected pattern (`--nx-tint` + 3px `--nx-brand` edge).
  1. code 13px/700
  2. hierarchy name 13px `#3D3D3D`
  3. **depth bar**: one 6px-tall segment per level of that hierarchy (flex 1 each, gap 2px, radius 2): filled up to the node's level (`--nx-brand` on the current hierarchy, `#8DB487` on others), the rest `--nx-divider`. `aria-hidden` (the level text carries the meaning).
  4. level 13px/700 "7 / 14"
  5. parent 12px `--nx-text-2` "under AGCOE · AM AI Center of Excellence" (or "Top node").
- Order: current hierarchy first, then others in master order. Show **4** rows; if more, link-button 13px "Show all 7" / "Show fewer".
- Clicking a row switches the navigator to that hierarchy (same node).
- No percentages anywhere.

**B4.6 Tabs** `role="tablist" aria-label="Node information"`, `display:flex; gap:22px; padding:0 26px; margin-top:18px; border-bottom:1px solid --nx-divider`. Tab 40px, no padding, `border-bottom:2px solid` (`--nx-brand` selected), label 14px + count 400 `--nx-text-3`:
- "Properties 9", "Children <n>", "History <n>".

**B4.7 Tab panel** `role="tabpanel"`, `flex:1; overflow:auto; padding:8px 26px 22px`.

*Properties* — grouped list:
- Category header: tiny caps, padding 16px 0 6px: "FINANCE", "HR", "REPORTING".
- Row: `display:grid; grid-template-columns:minmax(0,1fr) minmax(0,.8fr) minmax(0,1fr) 80px; gap:12px; min-height:40px; border-bottom:1px solid --nx-divider-soft; font-size:14px; align-items:center`:
  1. label `#3D3D3D`
  2. value 700
  3. source 13px `--nx-text-3` in plain words: "Derived from code" / "Inherited from " + link-button to the ancestor code (e.g. **AMT**, clicking selects that ancestor) / "Set on this node" / "Default"
  4. action right-aligned 13px: "Override" or "Edit" as underlined `--nx-link` links; "Derived" as plain text `--nx-muted-dot`.
- Rows (data for 34307):
  - FINANCE: Cost centre 34307 (Derived from code, Derived); Finance country US (Inherited from AMT, Override); Oracle accounting currency USD (AMT, Override); Oracle GL company 0444 (AMT, Override); Oracle operating ledger L100 (AMT, Override).
  - HR: Standard working hours 40.00 (Inherited from ORGTL, Override); HR flag Y (Set on this node, Edit).
  - REPORTING: Reporting currency USD (Default, Override); Include in reporting Y (Default, Override).

*Children* — rows (button, select child): `display:flex; align-items:baseline; gap:10px; min-height:40px; padding:10px 0; border-bottom:1px solid --nx-divider-soft`: name 14px/600, code 12px `--nx-text-3`, spacer, note 12px `--nx-text-3` = "In your draft" / "Inactive" / "<n> below" (limb) / "Leaf". Empty: 14px `--nx-text-2` "Leaf nodes have no children." or "No children yet."

*History* — timeline (A4.13), container padding 14px 0 0 22px. Items newest first:
- (if draft) "Move 34307 from AGQNT to AGCOE in ORGTL" — "In your draft · not submitted" — dot `--nx-muted-dot` (outlined style allowed)
- "HR flag set to Y" — "5 Nov 2026 · approved by u_approver1" — green
- "Copied from the 2026-10 baseline" — "1 Nov 2026 · system" — grey.

### B5. Behaviour summary
- URL keeps `h` and `node`; back/forward restores them.
- Version/as-of in the context bar change the tree to that picture (read only when a Finalized version is selected: hide Edit/Move/Add child/⋯ and show the tag "Read only" next to the title).
- Sandbox workspace: tree shows sandbox data and the Sandboxes strip (see Sandboxes spec) appears under the context bar.
- All edits go to the draft (A5.3). Nothing writes live data directly.

### B6. Large hierarchies (10,000+ nodes) — navigator rules
The navigator must stay fast and readable with 10k–50k nodes. None of this changes the look; it changes how data is loaded and drawn.

**B6.1 Lazy children** — never load a whole hierarchy. Each expand calls
`GET /api/hierarchies/{h}/children?parent={code}&after={lastCode}&limit=100&version=&asOf=&workspace=&includeInactive=`
→ `{ items: [{ code, name, type, status, childCount, descendantCount, hasDraft }], nextAfter: string|null, total: number }` (ordered by code, keyset paging).

**B6.2 Virtual scrolling** — render the visible tree as a flat list of rows (`{ code, depth, expanded, kind:'node'|'more'|'filter' }`) inside Angular CDK `cdk-virtual-scroll-viewport` with `itemSize=34`. Only ~40 rows exist in the DOM at any time. Keyboard navigation works on the flat list.

**B6.3 Big child lists** — when a limb has more than 100 children:
- Show the first 100 rows, then a "more" row (indent = child depth, 34px, 13px `--nx-link` underlined button): **"Show 100 more · 412 left"**. Clicking fetches the next page.
- Directly under the expanded limb, a **filter row** (indent = child depth, input 28px, 4px radius, 12px, placeholder "Filter 512 children") filters that limb's children on the server (`&q=`). Clear (×) restores paging.

**B6.4 Search** — server side only: debounce 250ms, min 2 characters, `GET /api/hierarchies/{h}/search?q=&limit=8` → `{ items: [{ code, name, level, path: string[] }], total }`. Results show "Showing 8 of 214 — refine your search" when `total > 8`. Picking a result loads only the ancestors' children needed to reveal it (`GET .../path?code=` returns the ancestor chain with each ancestor's first page of children), expands that path, scrolls the row into view and selects it.

**B6.5 Counts** — "2,818 nodes", "n below", collapsed child counts all come from precomputed `childCount` / `descendantCount` per node per version (maintained by the backend on commit). The browser never counts subtrees.

**B6.6 Caching** — cache child pages per `(h, version, asOf, workspace, parent, after)`; Finalized versions can be cached for the session; Working versions are invalidated when the user's own request is approved or on manual refresh.

**B6.7 Other tabs** — Children tab pages 50 at a time with the same "Show 50 more" row; Where-it-lives already capped at 4 rows + "Show all".

### B7. Acceptance checklist
- [ ] 15 hierarchies reachable from the picker with filter and Primary/Alternates groups; "1 of 15" shown.
- [ ] Green dot marks hierarchies containing the node.
- [ ] Names wrap, never cut off, in tree, ribbon titles and lists.
- [ ] Draft marker is a green ring (no amber).
- [ ] Where-it-lives shows depth bars + "7 / 14" text, max 4 rows then "Show all".
- [ ] Property source written in words with clickable ancestor.
- [ ] With a 10k-node test hierarchy: first paint < 1s, expanding a 500-child limb shows 100 rows + "Show 100 more · 400 left" + filter row, scrolling stays smooth (virtual scroll), search returns in < 300ms.


---

## Part C — Explore tree (Tree + Radial)

**Route:** `/hierarchies/explore?h=<hier>&view=<tree|radial>&centre=<code>&sel=<code>&levels=<1-4>&q=<highlighted search>` · **Header tab:** Hierarchies (stays current) · **Mockup board:** "Hierarchies · Explore tree (Tree + Radial)" — `Explore.dc.html`
**Purpose:** see the *shape* of a hierarchy around one node — depth (Tree) or breadth and balance (Radial) — and act on a node without going back to the list. It complements the navigator; it never replaces it.

### C0. Hard rules
1. **Never draw the whole hierarchy.** Draw a **centre** node plus up to `levels` levels below it (default 2, max 4).
2. **Node budget: 150 drawn elements** (a group node counts as 1). Levels are filled breadth-first; any branch that would exceed the budget is drawn **collapsed** with a "+n" pill, and the page says so (C5).
3. **Leaf groups:** if a limb has **more than 20 leaf children** in view, draw **one group node** ("67 leaves") instead of the individual leaves. Limb children are still drawn individually.
4. Collapsed branches show a **"+n" pill** (n = all nodes below, formatted `en-US`, e.g. "+1,240"). Clicking opens that branch one more level (still within the budget rules).
5. **Visual language only from Part A:** limbs = green (`#368727`); leaves = white with grey border; selected = green ring + green path; in your draft = green ring dot; inactive = faded + dashed; search match = green halo. No orange, cyan, black or per-level colours.
6. **No truncation with "…"** in Tree cards; Radial labels max 3 lines and hidden below 55% zoom (codes stay visible).
7. Everything is read-only except the node panel actions, which **add to the draft** (never write live data). In a Finalized version those actions are hidden.

### C1. Page structure
```
[A5.1 Header — Hierarchies current] [A5.2 Context bar] [nx-top-progress]
┌ content column (max 1320, padding 22 24 28, gap 16) ─────────────────────────────────────────────┐
│ C2  Hierarchies › Explore tree / "Explore tree" / live line                         [Back to list]│
│ ┌ C3 Explorer panel ───────────────────────────────────────────────────────────────────────────┐ │
│ │ C4 [Tree|Radial] ...... [ORGTL ▾] [🔍 Find a node] Levels[1|2|3|4] (o)Inactive [⤓ Export image]│ │
│ │ C5 Centred on: ORGTL › OC005 › AM › (AMT) ..... Level 7 partly shown · 2 branches folded ·   │ │
│ │                                                  Show all at this level | Showing 58 of 2,818 │ │
│ │ C6 (only while highlighting) ● 8 matches for “QNT” · 3 in view ........ 1 of 8 ‹ › Clear    │ │
│ │ ┌ C7 Canvas (flex 1 × 700) ──────────────────────────────────┐┌ C12 Node panel 320 ───────┐│ │
│ │ │ dotted grid · level labels · edges · nodes · +n pills       ││ (node or group details)    ││ │
│ │ │ [legend]                         [mini-map 180×120]         ││                            ││ │
│ │ │                                  [− 82% + | Fit]            ││                            ││ │
│ │ └─────────────────────────────────────────────────────────────┘└────────────────────────────┘│ │
│ └──────────────────────────────────────────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────────────────────────────────────┘
[A5.3 Draft bar]
```

### C2. Title row
`display:flex; align-items:flex-end; gap:12px 16px; flex-wrap:wrap`.
- Left column (gap 6, `flex:1; min-width:0`):
  - `<nav aria-label="Breadcrumb">` 13px `--nx-text-2`: link **Hierarchies** (`--nx-link`, underline, offset 2px) → `/hierarchies?h=&node=`; separator "›" (`--nx-muted-dot`, `aria-hidden`); text "Explore tree".
  - h1 28px/700, −0.01em: **"Explore tree"**.
  - Live line 15px `--nx-text-2`: `<HIER> · <centre name> and <levels> levels below` ("1 level" singular). Example: "ORGTL · Asset Management Technology and 2 levels below".
- Right: secondary pill 38px, padding 0 16px **"Back to list"** → `/hierarchies?h=<hier>&node=<sel ?? centre>`.

Entry point: the **Explore tree** button on the Hierarchies title row (B2) opens this page with `centre = parent of the selected node` (or the node itself if it is the root / a top-level child) and `sel = selected node`.

### C3. Explorer panel
Panel A3: white, 1px `--nx-border`, radius 8, `overflow:hidden`, `display:flex; flex-direction:column`.

### C4. Toolbar
Row `display:flex; align-items:center; gap:16px; padding:0 16px 0 12px; border-bottom:1px solid --nx-border; flex-wrap:wrap`. Items left → right:

1. **View tabs** (A4.9, 46px tall): "Tree", "Radial". `role="tablist" aria-label="Layout"`. Default **Radial**; remember the last choice in `localStorage['nx.explore.view']`. Switching keeps centre / sel / levels / expansions and refits.
2. Spacer (`flex:1`).
3. **Hierarchy select** — 32px, 4px radius, padding `0 30px 0 10px`, 13px/600, chevron 11px; hidden label "Hierarchy". Options = all hierarchies (code · name), same order as the navigator picker. On change: keep `centre`/`sel` if they exist in the new hierarchy, else centre = root, sel = null; clear expansions; refit.
4. **Search** (C9) — input 220×32, 4px radius, search icon 14px at left 10px top 9px, padding-left 30px, 13px, placeholder "Find a node", `role="combobox"`, `aria-expanded`, `aria-controls="ex-sug"`, `autocomplete="off"`. Border turns `--nx-brand` while suggestions are open.
5. **Levels** — label 13px `--nx-text-2` "Levels" (`id` used by `aria-labelledby`) + joined toggle (A4.7) segments 30px tall, min-width 32px, 13px/600: 1 · 2 · 3 · 4. Selected = green. Changing: clear expansions, clear "Show all" override, refit.
6. **Inactive switch** (A4.8 small: track 30×18, knob 14, knob left 2px / 14px) label "Inactive" 13px. Off by default → inactive nodes hidden except the selected node.
7. **Export image** — secondary pill 32px, padding 0 12px, gap 6px, 13px/600, download icon 14px (arrow down into a tray). Exports **exactly what is in the canvas viewport** (C14).

### C5. "Centred on" row
`display:flex; align-items:center; gap:6px; padding:10px 16px; border-bottom:1px solid --nx-divider; flex-wrap:wrap; font-size:13px`.
1. "Centred on" — `--nx-text-3`, margin-right 4px.
2. Path pills root → centre, separated by "›" (`--nx-muted-dot`, `aria-hidden`). Pill: 26px tall, padding 0 10px, radius 999, 12px, shows the **code**, `title` = full name. Ancestors: white, 1px `--nx-border`, weight 500. Centre: bg + border `--nx-brand`, white, 700, `aria-current="page"`. Click → that node becomes the centre (clear expansions, refit).
3. Spacer.
4. **Fold notice** (only when the budget folded something): `--nx-text-2` "Level 7 partly shown · 2 branches folded" (level = absolute level of the first folded row; singular "1 branch") + link-button **"Show all at this level"** (13px, `--nx-link`, underlined) + a "|" separator (`#C9C4BE`). "Show all" lifts the budget to **1,000** for the current centre/levels; if the result still exceeds 1,000, show toast "This level has 3,412 nodes. Pick a smaller branch to centre on." and keep folding.
5. Count — `--nx-text-3`: "Showing <n> of <total> nodes" where n counts every node represented (group members included), e.g. "Showing 58 of 2,818 nodes".

### C6. Search highlight bar (only in highlight mode)
`role="status"`, `display:flex; align-items:center; gap:10px; padding:8px 16px; background:--nx-tint; border-bottom:1px solid #DCE6D8; font-size:13px`:
- 10px ring dot (inset 2px `--nx-brand`).
- "**8 matches** for “QNT” · 3 in view" (count bold; "1 match" singular; "in view" = matches currently drawn, including inside group nodes).
- Spacer; position `--nx-text-2` "1 of 8".
- Previous / Next: 28×28 squares, radius 4, 1px `--nx-control-border`, white, "‹" / "›", `aria-label="Previous match" / "Next match"`.
- "Clear" text button 13px/600 `--nx-link`.

### C7. Canvas
**Container:** `position:relative; flex:1; min-width:0; height:700px; overflow:hidden; cursor:grab; background-color:#FCFBFA; background-image:radial-gradient(#E5E1DC 1px, transparent 1px); background-size:22px 22px`. While dragging: `cursor:grabbing`. Focusable (`tabindex="0"`, `aria-label="Tree canvas. Arrow keys move between nodes. Plus and minus zoom. 0 fits."`, focus ring `inset 0 0 0 2px #368727`).

**Layers (inside one transformed layer, `transform-origin:0 0`, `transform: translate(tx,ty) scale(k)`, `transition: transform .25s ease` except while dragging or with reduced motion):**
1. SVG (overflow visible, `aria-hidden`):
   - **Level rings** (Radial only): one dashed circle per drawn depth, `stroke:#E5E1DC; stroke-width:1; stroke-dasharray:4 5; fill:none`.
   - **Edges**: one `<path>` with all edges, `stroke:#C9C4BE; stroke-width:1.5; fill:none`.
   - **Selected path**: second `<path>` with only the edges on the path centre → selected node, `stroke:#368727; stroke-width:2.5`.
2. **Level labels** (HTML): pill `padding:1px 8px; radius 999; background:#FCFBFA; 11px/600; #8A8580; white-space:nowrap; translate(-50%,-50%); pointer-events:none`, text "Level <absolute level>".
   - Radial: one per ring, at the top of the ring (x = 0, y = −r).
   - Tree: one per row, 70px left of the leftmost card, vertically centred on the row's first card.
3. **Nodes** (HTML buttons, C10/C11) with draft dots and "+n" pills.

**Fixed overlays (not transformed):**
- **Legend** — bottom-left 14px: white 92% bg, 1px `--nx-border`, radius 6, padding 8px 12px, gap 14, 12px `--nx-text-2`: green rounded square 12px "Limb" · white circle 12px with 1.5px `#8A8580` border "Leaf" · 10px ring dot "In your draft" · mini pill "+3" "Hidden below".
- **Mini-map** (C13) — bottom-right 14px, 56px from the bottom.
- **Zoom control** — bottom-right 14px: joined white box, 1px `--nx-control-border`, radius 6: "−" (34×32, 18px), percentage (min-width 48px, 12px/600 `--nx-text-2`, `line-height:32px`, dividers left/right `--nx-border`), "+" (34×32), "Fit" (padding 0 12px, 12px/600, divider left). `aria-label`s "Zoom out" / "Zoom in".

**Pan & zoom:**
- Drag on empty canvas (not on buttons) pans.
- Ctrl/⌘ + wheel, or pinch, zooms around the pointer; plain wheel scrolls the page.
- Zoom multiplier range ×0.4 – ×4 of the fit scale; each button step ×1.25. Percentage shows the real scale `round(k*100)%`.
- **Fit** = bounding box of nodes + level labels + 30px padding fitted to the canvas, max 115%. Fit runs after every layout change (view, levels, centre, hierarchy, expansions do not refit — they keep the camera; "Fit" button resets).
- Keyboard: ←/→/↑/↓ move selection (parent, first child, previous/next sibling); Enter opens the panel; `+` / `−` zoom; `0` fit; `/` focuses search; Esc closes suggestions / clears highlight / closes the panel (in that order).

### C8. Building the visible tree (algorithm)
```
input: centre, levels L, expanded:Set, showInactive, budget B (150, or 1000 after "Show all")
queue = [(centre, 0)]; drawn = 1; folded = 0; foldLevel = null
while queue not empty:
  (c, d) = queue.shift()
  kids = children(c) filtered by showInactive (always keep the selected node)
  if kids empty: continue
  open = d < L or expanded.has(c)
  if not open: c.hiddenBelow = descendantCount(c); continue
  leaves = kids where type = Leaf; limbs = kids where type = Limb
  group = leaves.length > 20
  need = limbs.length + (group ? 1 : leaves.length)
  if drawn + need > B: c.hiddenBelow = descendantCount(c); folded++; foldLevel = level(c) + 1; continue
  for k in (group ? limbs : kids): draw k at depth d+1; queue.push((k, d+1))
  if group: draw group node "G:<c>" at depth d+1 with members = leaves
  drawn += need
```
- Order children by code (same as the navigator).
- Clicking "+n" on c adds c to `expanded` (its children are fetched if not loaded, C17).

### C9. Search (in Explore)
**Suggestions** (≥ 2 characters, debounce 250ms, server search over the whole hierarchy):
- Panel `id="ex-sug" role="listbox"`: absolute, right-aligned to the input, top 38px, width 360px, white, 1px `--nx-border`, radius 8, shadow `0 8px 24px rgba(0,0,0,0.12)`, z-index 40, padding 4px 0.
- Up to **8** options (`role="option"`), each a full-width button padding 8px 14px:
  - Line 1 (`align-items:baseline; gap:8px`): name 14px/600 `#141414` · code 12px `--nx-text-3` · spacer · "Level 7" 12px `--nx-text-2` nowrap.
  - Line 2: 12px `--nx-text-3`, up to the 3 nearest ancestors joined " / " (e.g. "AMT / AMQNT / AGQNT"), wraps.
- Footer button (top border `--nx-divider`, bg `--nx-surface-2`, padding 9px 14px, 13px/600 `--nx-link`):
  - more than 1 match: **"Highlight all <total> matches"**
  - exactly 1: "Go to this node"; none: "No matches" (not clickable).
- ↑/↓ move through options, Enter picks; Esc closes and clears.

**Picking a suggestion (or Enter with exactly one match):** centre = the match's parent (unless that is the hierarchy root), sel = match, expansions cleared, refit; the node gets a 2-pulse green ring animation (skip with reduced motion). Suggestions close and the input clears.

**Highlight mode** (footer button, or Enter with more than one match):
- Sets `q` in the URL; shows the C6 bar.
- Drawn nodes that match: halo `box-shadow:0 0 0 3px #fff, 0 0 0 6px rgba(54,135,39,.55)` (combined with the selection ring when both apply). Group nodes get the halo when any member matches.
- All other nodes (except the centre): opacity **.35**.
- Collapsed "+n" pills that contain matches show a green count bubble inside the pill: `+29 [3]` (bubble: white 600 text on `--nx-brand`, radius 999, padding 0 5px, margin-left 4px).
- ‹ › step through matches in server order: selects the match; if it is not drawn, re-centre on its parent (C9 rule) and refit; position text updates "3 of 8".
- Matches also appear on the mini-map as dark dots.
- Clear / Esc: leaves highlight mode, keeps current centre and selection.

### C10. Radial view
**Layout:** centre at (0,0). Ring radius by depth below centre: **1 → 170, 2 → 300, 3 → 420, 4 → 540** (px, unscaled; beyond 4: depth × 130). Start angle −90° (12 o'clock), clockwise, full circle. Each child's angular span ∝ its number of drawn leaf slots (a group node counts as 2 slots so it gets room for its pill). Edges are straight lines centre-to-centre.

**Shapes** (all are `<button>`s, centred horizontally in a 128px-wide wrapper positioned at (x − 64, y − size/2)):
| Kind | Shape | Size | Fill / border | Inside |
|---|---|---|---|---|
| Centre | circle | 124 | `#368727`, no border | code 13px/700 white + name 12px/600 white, centred, padding 0 10px, wraps |
| Limb | rounded square r 8 | 46 | `#368727` (selected `#2B6A1F`) | code 10px/700 white (9px when code > 5 chars) |
| Leaf | circle | 40 | white (selected `--nx-tint`); border 1.5px `#8A8580` (selected 2.5px `#368727`; inactive dashed) | code 10px/700 `#141414` |
| Group | pill r 999 | 96 × 34 | white, 1.5px `#8A8580` border, "stack" shadow `3px 3px 0 -1px #fff, 3px 3px 0 0 #C9C4BE` | "67 leaves" 12px/700 `#141414` |
- Default shadow `0 1px 2px rgba(0,0,0,.08)`. Selected ring `0 0 0 3px #fff, 0 0 0 5px #368727`.
- **Label** (limbs and leaves, not centre/group): name under the shape, margin-top 5px, width 128px, 11px, line-height 1.25, centred, max 3 lines, `#141414` (selected `--nx-link`), `pointer-events:none`. Hidden when zoom < 55%.
- **Draft dot**: 10×10, white, `box-shadow: inset 0 0 0 2.5px #368727, 0 0 0 2px #fff`, at the shape's top-right (wrapper x = 64 + size/2 − 7, y = −3). `title="Pending in your draft"`.
- **"+n" pill**: right of the shape, vertically centred (x = 64 + shapeWidth/2 + 4, y = size/2 − 10); see C11 pill style.
- Inactive: wrapper opacity .55.

### C11. Tree view
**Layout constants:** card 184 × 66, horizontal gap between sibling subtrees 28, vertical gap between levels 70, stacked-child indent 22, stacked-child pitch **84** (66 card + 18 gap).
- **Stacking rule:** if every drawn child of a node is terminal (leaf, group, or collapsed), the children are stacked vertically: x = parent.x + 22, y = parent.y + 66 + 20 + i × 84. Otherwise children sit side by side and the parent is centred above them.
- **Edges** (orthogonal):
  - side-by-side: `M parentCenterX,parentBottom V midY H childCenterX V childTop` (midY = parentBottom + 35).
  - stacked: one rail at x = parent.x + 16 from the parent bottom to the last child's middle, plus a stub `H child.x` into each child's left edge at its middle.
- Width function: leaf/terminal = 184; stacked parent = 184 + 22; otherwise Σ child widths + 28 × (n − 1).

**Card** (`<button>`, `display:block; width:184px; height:66px; box-sizing:border-box; padding:9px 12px; border-radius:6px; text-align:left; background:#fff; font-family:inherit`):
- Line 1 (`display:flex; align-items:center; gap:6px; 11px/600`): kind square 8×8 (limb: `#368727`, radius 2; leaf: white, 1.5px `#8A8580` border) · code (limb `--nx-link`, leaf `--nx-text-2`) · spacer · for an **expanded** limb only: "1,240 below" (400, `--nx-text-3`). (Hidden when collapsed — the "+n" pill says it.)
- Line 2: name 13px/600 `#141414`, line-height 1.3, margin-top 3px, wraps (2 lines fit; a 3rd line grows the card — set `min-height:66px` and recompute stack pitch from the measured height if your data has very long names).
- Border: 1px `--nx-control-border`; **limb: border-top 3px `#368727`**; inactive: 1px dashed; shadow `0 1px 2px rgba(0,0,0,.04)`.
- Selected: bg `--nx-tint`, 2px `#368727` all round, shadow `0 2px 8px rgba(54,135,39,.18)`.
- **Group card**: same box, content = "67 leaves" (12px/700) + "Show list" (11px `--nx-text-2`), stacked-paper shadow `3px 3px 0 -1px #fff, 3px 3px 0 0 #C9C4BE, 6px 6px 0 -1px #fff, 6px 6px 0 0 #D6D1CB`.
- **Draft dot**: top-right corner (x = 176, y = −4).
- **"+n" pill (fixed placement)**: anchored to the card's **bottom-right corner, half inside / half outside the bottom edge**: `left: 174px; top: 56px; transform: translateX(-100%)` → right edge 10px in from the card's right edge, vertical centre on the card's bottom border. z-index 2. This never overlaps the next stacked card (18px gap) or neighbouring columns.

**"+n" pill style (both views):** height 20px, padding 0 7px, radius 999, white bg, 1px `--nx-control-border`, shadow `0 1px 3px rgba(0,0,0,.12)`, 11px/700 `--nx-link`, `white-space:nowrap`, `aria-label="Show <n> more under <code>"`. Optional match bubble (C9).

### C12. Node panel (right, 320px, only when something is selected)
`<aside aria-label="Selected node">`, `width:320px; flex:0 0 320px; border-left:1px solid --nx-border; padding:18px 20px; display:flex; flex-direction:column; gap:14px`. Opening/closing changes the canvas width; keep the camera centred on the same point (adjust pan by ±160px) rather than refitting.

**Header row** (gap 8): kind tag (A4.16 small: 12px/600, padding 0 7px — Limb green variant, Leaf/Group neutral) · code 13px `--nx-text-3` (group: "under DATEN") · spacer · close "×" 28×28 transparent, 18px `--nx-text-3`, `aria-label="Close"`.

**Title:** h2 20px/700 line-height 1.25 — node name, or "67 leaves under Data Engineering" for a group.

**Node (limb/leaf):**
1. Facts `<dl>` grid 2×2, gap 12, padding 12px 0, top & bottom border `--nx-divider`; `dt` 12px `--nx-text-2`, `dd` 15px/700 margin-top 2px:
   - Level — "5 of 14" (absolute level of the node / depth of the hierarchy)
   - Status — Active (`--nx-link`) / Inactive (`--nx-text-3`)
   - Children — direct child count
   - All below — descendant count (en-US format)
2. Path 12px `--nx-text-3`, line-height 1.5, `overflow-wrap:anywhere`: "ORGTL / OC005 / AM / AMT / AMQNT / AGCOE / 34307".
3. Draft callout (if `hasDraft`): flex gap 8, padding 10px 12px, bg `--nx-tint`, 1px `#DCE6D8`, radius 6, 13px line-height 1.4, ring dot 8px + text (e.g. "Move from AGQNT to AGCOE · in your draft").
4. Actions (column gap 8):
   - Primary 36px full-width **"Centre tree here"** — limbs only, hidden if it is already the centre.
   - Secondary 36px full-width link **"Open in Hierarchies"** → `/hierarchies?h=&node=`.
   - Row of two secondary 34px buttons (flex 1 each, 13px/600): **"Move"** (opens the valid-parent picker from Hierarchies) and **"Add child"** (limbs only). Hidden in Finalized versions.

**Group:**
1. Filter input 34px, 4px radius, 13px, placeholder "Filter these leaves" (hidden label "Filter leaves"); filters by code or name (server-side `children?parent=&q=` for real data).
2. List container 1px `--nx-border`, radius 6, overflow hidden; **10 rows per page**; row = button `display:flex; align-items:baseline; gap:8px; padding:8px 12px; top border --nx-divider (not first)`: name 13px/600 (flex 1) + code 12px `--nx-text-3`. Click → re-centre on that leaf's parent and select the leaf.
3. Pager row (12px `--nx-text-2`, gap 8): "1–10 of 67" (or "No leaves match") · spacer · "Previous" · "Next" (28px pills, 12px/600; opacity .4 when not available).

### C13. Mini-map
- Visible only when the view is zoomed in (multiplier > 1.05) or panned more than 40px; hidden at Fit.
- Box: absolute right 14px, bottom 56px; 180 × 120; bg `rgba(255,255,255,.96)`; 1px `--nx-control-border`; radius 6; shadow `0 2px 8px rgba(0,0,0,.08)`; `overflow:hidden`; `role="img"` with `aria-label="Mini map. Click to move the view."`; cursor pointer.
- Contents (SVG 180×120, 6px inner padding, uniform scale to fit the layout's bounding box, centred):
  - edges: `#D6D1CB`, 0.8px
  - leaf/group dots r 2.2 `#B5AFA8`; limb dots r 2.2 `#368727`; centre dot r 4 `#368727`
  - search matches r 3 `#141414`
  - viewport rectangle: fill `rgba(54,135,39,.08)`, stroke `#368727` 1.5px, rx 2; clamped to the box, min 8×8.
- Click → pan so the clicked point becomes the canvas centre (zoom unchanged). Drag inside the rectangle → pan continuously (pointer capture).

### C14. Export image
- Button in the toolbar (C4.7). Exports the **current viewport** as PNG at 2× device pixels, white background (not the dotted grid), including level labels, selection, highlight and draft dots; excludes the legend, mini-map, zoom control and the node panel.
- Footer stamp in the image (bottom-left, 11px `#8A8580`): "ORGTL · centred on AMT · 2026-11 Working · as of 21 Nov 2026 · Nexus".
- File name: `<hier>-<centre>-<view>-<yyyymmdd>.png`, e.g. `ORGTL-AMT-radial-20261121.png`.
- Implementation: `html-to-image` (`toPng(node, { pixelRatio: 2, backgroundColor: '#FFFFFF', filter })`) on the canvas element, where `filter` drops elements with `data-export="skip"` (legend, mini-map, zoom). Button shows the `nxBusy` spinner while rendering; toast "Saved ORGTL-AMT-radial-20261121.png".

### C15. Interactions summary
| Action | Result |
|---|---|
| Click node | Select (panel opens/updates), green path drawn. Camera unchanged. |
| Double-click limb | Centre tree here. |
| Click "+n" | Expand that branch one level (C8 rules), camera unchanged. |
| Click group | Select group; panel shows its paged list. |
| Click "Centred on" pill | Re-centre there; clear expansions; refit. |
| Change view / levels / hierarchy / inactive | Recompute; refit (view, levels, hierarchy) or keep camera (inactive). |
| Show all at this level | Budget → 1,000 for this centre/levels; refit. |
| Search suggestion | Re-centre on parent, select, pulse; refit. |
| Highlight all | Highlight mode (C9). |
| Drag canvas | Pan. Mini-map appears. |
| Esc | Close suggestions → clear highlight → close panel. |
| Context bar version / as-of / workspace change | Reload the window for that picture; keep centre/sel if present. |

### C16. State & URL
```ts
export interface ExploreState {
  hier: string;                    // 'ORGTL'
  view: 'tree' | 'radial';         // default 'radial' (or last used)
  centre: string;                  // default AMT in the demo
  levels: 1 | 2 | 3 | 4;           // default 2
  showInactive: boolean;           // false
  expanded: Set<string>;           // branches opened via "+n"
  budgetOverride: boolean;         // "Show all at this level"
  sel: string | null;              // node code or 'G:<parentCode>' for a group
  highlight: { q: string; index: number } | null;
  groupList: { q: string; page: number };
  camera: { zoom: number; panX: number; panY: number };   // zoom = multiplier on fit
}
```
URL keeps `h, view, centre, sel, levels, q`. Back/forward restores them; camera, expansions and group paging are not in the URL.

### C17. Data / API
Load only the window being drawn.
```
GET /api/hierarchies/{h}/window
    ?root={centre}&depth={levels}&budget={150|1000}&groupAt=20
    &version=&asOf=&workspace=&includeInactive=&expanded=AGCOE,DATEN
→ {
  path: [{ code, name, level }],                  // hierarchy root → centre
  totalNodes: 2818, depthOfHierarchy: 14,
  nodes: [{ code, name, type, status, parent, level, childCount, descendantCount, hasDraft, draftText? }],
  groups: [{ parent, count }],                    // leaf groups (members not included)
  folded: [{ code, descendantCount }], foldLevel: 7
}
GET /api/hierarchies/{h}/children?parent=&q=&after=&limit=10      // group list paging
GET /api/hierarchies/{h}/search?q=&limit=8                         // suggestions (items + total)
GET /api/hierarchies/{h}/search/all?q=&limit=500                   // highlight mode: codes + ancestor codes
```
- The server applies the C8 algorithm so the browser never receives more than the budget.
- For highlight bubbles on "+n" pills, use the ancestor codes from `search/all` to count matches per folded branch.
- Cache window responses by all query params (last 20). Finalized versions cache for the session.

### C18. Angular implementation
**Packages:** `@angular/cdk` (overlay for suggestions, a11y), `html-to-image` (export). No D3 needed (layouts below), though `d3-hierarchy` may be used for the radial angle split.

**Components**
```
ExplorePageComponent        route; URL ↔ state (signals); loads window; owns camera
 ├─ ExploreToolbarComponent  tabs, hierarchy, search+suggestions, levels, inactive, export
 ├─ CentreTrailComponent     Centred-on pills, fold notice, count
 ├─ HighlightBarComponent    C6
 ├─ TreeCanvasComponent      layout + render + pan/zoom + keyboard; inputs: window, view, sel, highlight, camera
 │    ├─ MiniMapComponent    C13 (inputs: layout, camera, viewport)
 │    └─ legend / zoom (template, data-export="skip")
 └─ NodePanelComponent       C12 (node or group)
```

**Layout service (pure, unit-tested)**
```ts
export interface DrawNode { code: string; kind: 'Limb' | 'Leaf' | 'Group'; depth: number; children: string[]; hiddenBelow: number; members?: number; }
export interface Placed { code: string; x: number; y: number; w: number; h: number; }
export interface Layout { placed: Map<string, Placed>; edges: string; rings: string; levelMarks: { x: number; y: number; label: string }[];
  bbox: { minX: number; minY: number; maxX: number; maxY: number }; }

const RING = [0, 170, 300, 420, 540];

export function radial(nodes: Map<string, DrawNode>, centre: string, centreLevel: number): Layout {
  const placed = new Map<string, Placed>(); let edges = '', rings = ''; const marks: Layout['levelMarks'] = [];
  const slots = (c: string): number => { const n = nodes.get(c)!; return n.children.length ? n.children.reduce((s, k) => s + slots(k), 0) : (n.kind === 'Group' ? 2 : 1); };
  const place = (c: string, a0: number, a1: number) => {
    const n = nodes.get(c)!; const r = RING[n.depth] ?? n.depth * 130; const a = (a0 + a1) / 2;
    placed.set(c, { code: c, x: n.depth ? r * Math.cos(a) : 0, y: n.depth ? r * Math.sin(a) : 0, w: 128, h: n.depth ? 90 : 124 });
    let s = a0; const tot = slots(c);
    for (const k of n.children) { const span = (a1 - a0) * slots(k) / tot; place(k, s, s + span); s += span; }
  };
  place(centre, -Math.PI / 2, Math.PI * 1.5);
  let maxD = 0;
  nodes.forEach((n, c) => { maxD = Math.max(maxD, n.depth); const p = placed.get(c)!;
    n.children.forEach(k => { const q = placed.get(k)!; edges += `M${p.x.toFixed(1)},${p.y.toFixed(1)}L${q.x.toFixed(1)},${q.y.toFixed(1)}`; }); });
  for (let d = 1; d <= maxD; d++) { const r = RING[d] ?? d * 130;
    rings += `M${r},0A${r},${r} 0 1,0 ${-r},0A${r},${r} 0 1,0 ${r},0`; marks.push({ x: 0, y: -r, label: `Level ${centreLevel + d}` }); }
  return { placed, edges, rings, levelMarks: marks, bbox: bbox(placed, marks) };
}

export function tree(nodes: Map<string, DrawNode>, centre: string, centreLevel: number): Layout {
  const CW = 184, CH = 66, GX = 28, GY = 70, SX = 22, SH = 84;
  const placed = new Map<string, Placed>(); let edges = ''; const rowY = new Map<number, number>();
  const term = (c: string) => !nodes.get(c)!.children.length;
  const stacks = (c: string) => { const k = nodes.get(c)!.children; return k.length > 0 && k.every(term); };
  const width = (c: string): number => { const n = nodes.get(c)!; if (!n.children.length) return CW;
    return stacks(c) ? CW + SX : n.children.reduce((w, k) => w + width(k), 0) + GX * (n.children.length - 1); };
  const place = (c: string, x0: number, y: number) => {
    const n = nodes.get(c)!; const x = stacks(c) ? x0 : x0 + (width(c) - CW) / 2;
    placed.set(c, { code: c, x, y, w: CW, h: CH }); if (!rowY.has(n.depth)) rowY.set(n.depth, y);
    if (!n.children.length) return;
    if (stacks(c)) {
      const rail = x + 16; let last = 0;
      n.children.forEach((k, i) => { const ky = y + CH + 20 + i * SH; placed.set(k, { code: k, x: x + SX, y: ky, w: CW, h: CH });
        if (!rowY.has(n.depth + 1)) rowY.set(n.depth + 1, ky); last = ky + CH / 2; edges += `M${rail},${last}H${x + SX}`; });
      edges += `M${rail},${y + CH}V${last}`;
    } else {
      let cx = x0; const mid = y + CH + GY / 2;
      n.children.forEach(k => { const kw = width(k); place(k, cx, y + CH + GY); const kx = placed.get(k)!.x + CW / 2;
        edges += `M${x + CW / 2},${y + CH}V${mid}H${kx}V${y + CH + GY}`; cx += kw + GX; });
    }
  };
  place(centre, 0, 0);
  const minX = Math.min(...[...placed.values()].map(p => p.x));
  const marks = [...rowY].map(([d, y]) => ({ x: minX - 70, y: y + CH / 2, label: `Level ${centreLevel + d}` }));
  return { placed, edges, rings: '', levelMarks: marks, bbox: bbox(placed, marks) };
}
```
`bbox` = min/max over placed (x, y, x+w, y+h) and level marks (x−40, y−12). Selected-path edges = the same path strings for links whose both ends are on `ancestors(sel) ∪ {sel}`.

**Camera**
```ts
fitScale = Math.min(vw / (bw + 60), vh / (bh + 60), 1.15);
k  = fitScale * camera.zoom;
tx = vw / 2 - cx * k + camera.panX;     // cx, cy = bbox centre
ty = vh / 2 - cy * k + camera.panY;
transform = `translate(${tx}px, ${ty}px) scale(${k})`;
```
`vw` from a `ResizeObserver` on the canvas. Pan/zoom only update `camera` (no re-layout). Throttle pointer moves with `requestAnimationFrame`.

**Canvas template (core)**
```html
<div class="canvas" #cv tabindex="0" [attr.aria-label]="canvasLabel" [class.dragging]="dragging()"
     (pointerdown)="down($event)" (pointermove)="move($event)" (pointerup)="up()" (pointerleave)="up()"
     (wheel)="wheel($event)" (keydown)="key($event)">
  <div class="layer" [class.animate]="!dragging()" [style.transform]="transform()">
    <svg class="lines" aria-hidden="true">
      <path class="ring" [attr.d]="layout().rings"/><path class="edge" [attr.d]="layout().edges"/><path class="edge-sel" [attr.d]="selEdges()"/>
    </svg>
    <span *ngFor="let m of layout().levelMarks" class="lvl" [style.left.px]="m.x" [style.top.px]="m.y">{{ m.label }}</span>
    <div *ngFor="let n of drawList(); trackBy: byCode" class="node" [style.left.px]="n.left" [style.top.px]="n.top"
         [class.dim]="n.dim" [class.inactive]="n.inactive">
      <button class="shape" [ngClass]="n.cls" [attr.aria-pressed]="n.selected" [attr.aria-label]="n.aria"
              (click)="select(n.code)" (dblclick)="centreIfLimb(n.code)">
        <ng-container [ngSwitch]="n.template">…card / dot / centre / group content…</ng-container>
      </button>
      <span *ngIf="n.label && zoomPct() >= 55" class="label">{{ n.name }}</span>
      <span *ngIf="n.hasDraft" class="draft" [style.left.px]="n.draftX" [style.top.px]="n.draftY" title="Pending in your draft"></span>
      <button *ngIf="n.hiddenBelow" class="more" [class.tree]="view() === 'tree'" [style.left.px]="n.moreX" [style.top.px]="n.moreY"
              [attr.aria-label]="'Show ' + n.hiddenBelow + ' more under ' + n.code" (click)="expand(n.code)">
        +{{ n.hiddenBelow | number }}<span *ngIf="n.hits" class="hits">{{ n.hits }}</span>
      </button>
    </div>
  </div>
  <div class="legend" data-export="skip">…</div>
  <nx-mini-map *ngIf="showMini()" data-export="skip" [layout]="layout()" [camera]="camera()" [viewport]="viewport()" (jump)="panTo($event)"/>
  <div class="zoom" data-export="skip">…</div>
</div>
```

**Styles (core, in addition to Part A tokens)**
```scss
.canvas { position:relative; flex:1; min-width:0; height:700px; overflow:hidden; cursor:grab; outline:none;
  background:#FCFBFA radial-gradient(#E5E1DC 1px, transparent 1px) 0 0 / 22px 22px;
  &.dragging { cursor:grabbing; } &:focus-visible { box-shadow:inset 0 0 0 2px #368727; } }
.layer { position:absolute; left:0; top:0; width:1px; height:1px; transform-origin:0 0;
  &.animate { transition:transform .25s ease; } @media (prefers-reduced-motion: reduce) { transition:none; } }
.lines { position:absolute; left:0; top:0; width:1px; height:1px; overflow:visible; }
.ring { fill:none; stroke:#E5E1DC; stroke-width:1; stroke-dasharray:4 5; }
.edge { fill:none; stroke:#C9C4BE; stroke-width:1.5; } .edge-sel { fill:none; stroke:#368727; stroke-width:2.5; }
.lvl { position:absolute; transform:translate(-50%,-50%); padding:1px 8px; border-radius:999px; background:#FCFBFA;
  font-size:11px; font-weight:600; color:#8A8580; white-space:nowrap; pointer-events:none; }
.node { position:absolute; width:128px; &.inactive { opacity:.55; } &.dim { opacity:.35; } }
.node.tree-node { width:184px; }
.shape { font-family:inherit; cursor:pointer; box-shadow:0 1px 2px rgba(0,0,0,.08);
  &[aria-pressed="true"] { box-shadow:0 0 0 3px #fff, 0 0 0 5px #368727; }
  &.hit { box-shadow:0 0 0 3px #fff, 0 0 0 6px rgba(54,135,39,.55); }
  &.hit[aria-pressed="true"] { box-shadow:0 0 0 3px #fff, 0 0 0 5px #368727, 0 0 0 8px rgba(54,135,39,.35); } }
.centre { width:124px; height:124px; border-radius:50%; background:#368727; color:#fff; border:0; margin:0 auto; display:flex; flex-direction:column; align-items:center; justify-content:center; }
.limb   { width:46px; height:46px; border-radius:8px; background:#368727; color:#fff; border:0; margin:0 auto; font:700 10px/1.1 inherit; &[aria-pressed="true"] { background:#2B6A1F; } }
.leaf   { width:40px; height:40px; border-radius:50%; background:#fff; color:#141414; border:1.5px solid #8A8580; margin:0 auto; font:700 10px/1.1 inherit;
  .inactive & { border-style:dashed; } &[aria-pressed="true"] { background:#F3F6F1; border:2.5px solid #368727; } }
.group  { width:96px; height:34px; border-radius:999px; background:#fff; border:1.5px solid #8A8580; margin:0 auto; font:700 12px inherit;
  box-shadow:3px 3px 0 -1px #fff, 3px 3px 0 0 #C9C4BE; }
.card   { display:block; width:184px; min-height:66px; padding:9px 12px; border-radius:6px; text-align:left; background:#fff; color:#141414;
  border:1px solid #C9C4BE; box-shadow:0 1px 2px rgba(0,0,0,.04);
  &.limb-card { border-top:3px solid #368727; } .inactive & { border-style:dashed; }
  &[aria-pressed="true"] { background:#F3F6F1; border:2px solid #368727; box-shadow:0 2px 8px rgba(54,135,39,.18); }
  &.group-card { box-shadow:3px 3px 0 -1px #fff, 3px 3px 0 0 #C9C4BE, 6px 6px 0 -1px #fff, 6px 6px 0 0 #D6D1CB; } }
.label { display:-webkit-box; -webkit-line-clamp:3; -webkit-box-orient:vertical; overflow:hidden; margin-top:5px; width:128px;
  font-size:11px; line-height:1.25; text-align:center; color:#141414; pointer-events:none; }
.draft { position:absolute; width:10px; height:10px; border-radius:50%; background:#fff; box-shadow:inset 0 0 0 2.5px #368727, 0 0 0 2px #fff; }
.more { position:absolute; z-index:2; height:20px; padding:0 7px; border-radius:999px; border:1px solid #C9C4BE; background:#fff;
  box-shadow:0 1px 3px rgba(0,0,0,.12); font:700 11px inherit; color:#2B6A1F; cursor:pointer; white-space:nowrap;
  &.tree { transform:translateX(-100%); }      /* tree: left = 174, top = cardHeight − 10 */
  .hits { margin-left:4px; padding:0 5px; border-radius:999px; background:#368727; color:#fff; font-weight:600; } }
```
**Positions:** Radial wrapper `left = x − 64`, `top = y − size/2`; draft `(64 + size/2 − 7, −3)`; pill `(64 + shapeW/2 + 4, size/2 − 10)`. Tree wrapper `left = x`, `top = y`; draft `(176, −4)`; pill `(174, cardHeight − 10)` with `translateX(-100%)`.

**Performance**
- ≤ 150 node elements (1,000 with "Show all"); edges are 2–3 `<path>` strings.
- `trackBy` code; OnPush; layout recomputed only when the window, view, levels, expansions or inactive change — never on pan/zoom/selection.
- If more than 500 nodes are drawn ("Show all"), compute layouts in a Web Worker and hide labels until zoom ≥ 55%.

### C19. Loading & errors
- Window load: universal top bar + canvas skeleton (centred: 90px circle, a column of three 44px rounded squares, a column of four 36px circles, all shimmering `nx-sk`). Toolbar, trail and panel stay usable.
- Expanding "+n": that pill shows the 12px `nx-spinner` in place of the text until loaded.
- Search: suggestions show 3 skeleton lines while loading.
- Export: button busy spinner.
- Error in the canvas: centred 14px `--nx-text-2` "Couldn’t load this part of the tree." + link "Try again".

### C20. Accessibility
- Nodes are `<button>`s: `aria-label="<name>, <code>, Limb|Leaf[, inactive][, in your draft][, matches search]"`, `aria-pressed` for selection. Group: "67 leaves under DATEN".
- A visually hidden `aria-live="polite"` region announces selection ("QNT Quant Dev selected, level 7 of 14") and highlight results ("8 matches, 3 in view").
- Meaning is never colour-only: kind = shape; draft = dot + panel text; inactive = dashed + panel status; match = halo + bar count.
- "Back to list" gives the equivalent non-visual route.
- Reduced motion: no camera transition, no pulse.

### C21. Demo data (mockup)
- Hierarchy ORGTL (2,818 nodes, 14 levels). Centre **AMT Asset Management Technology** (level 4; path ORGTL › OC005 › AM › AMT). Default: Radial, levels 2, AGCOE expanded, **34307 selected** (draft "Move from AGQNT to AGCOE · in your draft").
- Under AMT:
  - AMQNT Quant Technology → AGCOE (34307 ●draft, 99302 AI Platform Ops, 99303 AI Research Tools) · AGQNT (31679, 34295, 34299, 34301 ●draft, 34322 inactive) · AGQUK (41701, 41702)
  - AMEQT Equity Technology → EQRES Equity Research Tech (41210, 41211, 41212 + 26 "Equity Quant Model 01–26" → **group of 29 leaves**) · EQTRD (41301, 41302)
  - AMFIT Fixed Income Technology → FIRSK (41408, 41409) · FITRD (41402, 41415)
  - AMDAT AM Data Platform → DATEN Data Engineering (42001–42003 + 64 "<Domain> Data Product 01–64" → **group of 67 leaves**) · DATGV (42101)
  - AMOTH AM Technology All Other (Inactive) → 49001 Legacy Applications (Inactive)
- The mockup uses a **demo budget of 30** so folding is visible at Levels 3 ("Level 7 partly shown · 2 branches folded"); production budget is **150**.
- Search "QNT" → 8 matches; "data" shows 8 suggestions with "Highlight all … matches".

### C22. Acceptance checklist
- [ ] Explore tree button on Hierarchies opens this page centred on the selected node's parent with that node selected.
- [ ] Only Tree and Radial tabs; choice remembered.
- [ ] Budget respected (150); folded branches show "+n"; fold notice with "Show all at this level".
- [ ] Limbs with > 20 leaves show a group node; its panel lists leaves 10 per page with filter.
- [ ] Level labels on rings (Radial) and down the left (Tree) show absolute levels.
- [ ] Search: 8 suggestions with level + path; single match recentres; "Highlight all" dims non-matches, shows bar with ‹ › and in-view count, bubbles on folded pills.
- [ ] Mini-map appears when zoomed/panned, shows viewport and matches, click/drag moves the view.
- [ ] Tree "+n" pills sit on the card's bottom-right edge and never overlap other cards.
- [ ] Export image saves the viewport as PNG with the stamp; overlays excluded.
- [ ] Names never cut off in Tree; Radial labels max 3 lines, hidden < 55% zoom.
- [ ] Node panel: facts, path, draft callout, Centre tree here, Open in Hierarchies, Move, Add child (hidden for Finalized).
- [ ] With a 10k-node hierarchy the page stays responsive (window API, ≤150 elements, no full-tree fetch).
