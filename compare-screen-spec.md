# Compare screen — Build Specification (features + exact styles)

> **Give this file to the LLM/developer together with `nexus-design-system.md` and a screenshot of the canvas board "Compare (new · master–detail)"** (`Compare-D`). The shell (header `#044014`, context bar, collapsible sidebar, draft bar) comes from the design system unchanged. This file defines **every feature, rule, state, string, size and colour** of the Compare page, plus a CSS block (§10) to **append verbatim** to `src/styles.css` after `nexus-ui.css`. Component `.scss` files may contain layout only.
>
> **Precedence:** this file > the screenshot > your own judgement. If something is not described here, **do not add it** (no extra buttons, columns, filters, legends, icons, tooltips, helper paragraphs, animations or colours). If a rule seems missing, leave the behaviour out and raise it as a question instead of inventing one.
>
> **Replaces** the current `/compare` page (hierarchy tabs + FROM/TO + "Run comparison" button + "Run a comparison" empty card). Older canvas boards "Version Compare · list / tree walkthrough / refined" and `version-compare-spec.md` are superseded by this file.

---

## 0. Glossary (use these words exactly in the UI)

| Term | Meaning |
|---|---|
| From / To | The two versions being compared. "Before" = From, "After" = To. |
| Difference | One change between From and To for one node (a node can have several, e.g. renamed **and** status). |
| Hierarchy | One tree (ORGTL, ORGTP, AMBUS, E2E_H, E2E_MGMT). Nodes are shared across hierarchies; placement (parent) is per hierarchy. |
| Limb / Leaf | Node with / without children. Shape language: **square = limb, circle = leaf**. |
| Global / Local | Global = the node's own data (name, status, global properties) — same in every hierarchy. Local = placement in one hierarchy (added, removed, moved). |

Never show the words "Run comparison", "Version comparison", "Review structural and property changes…", "Something went wrong" on this screen.

---

## 1. Purpose

Compare answers, for one hierarchy and two versions:
1. **How much changed?** — a count, split by change type.
2. **What exactly changed for this node?** — before → after values.
3. **Where is it in the tree, before and after?** — the parent and siblings on both sides.
4. **Who changed it and why?** — changed by, when, and the governance request.

Route: `/compare` with query params (§8.1). Sidebar item **Compare** has `aria-current="page"`.

Design rules for this page:
- **No "Run" button.** The comparison runs automatically whenever Hierarchy, From or To changes.
- Master–detail: differences list on the left, details of the selected difference on the right (same pattern as Worklist and Hierarchies).
- Each fact appears once. Text wraps; never truncate with ellipsis.
- Colour is used only for change types (§3), the selected row and version status pills.

---

## 2. Domain rules — how differences are computed (backend)

Nodes are matched **by code** (never by name). For the selected hierarchy `h`:

| Type key | Label | Condition | Scope |
|---|---|---|---|
| `added` | Added | node is in `h` in To but not in From | Local |
| `removed` | Removed | node is in `h` in From but not in To | Local |
| `moved` | Moved | node is in `h` in both, parent code differs | Local |
| `renamed` | Renamed | node in both, `name` differs | Global |
| `status` | Status | node in both, `status` (Active/Inactive) differs | Global |
| `property` | Property | node in both, any other property value differs — **one difference per changed property** | Global for global properties, Local for hierarchy-specific properties |

Rules:
- A node can produce several differences; each is its own list item. Key = `{type}:{code}` (property: `property:{code}:{propertyKey}`).
- A moved node whose descendants moved with it produces **one** `moved` difference (for the node itself), not one per descendant.
- An added/removed limb produces one difference per added/removed node in its subtree.
- Sibling sort-order changes are **not** reported.
- Name/status/property differences are only reported for nodes present in `h` in both versions.
- Swapping From and To inverts everything (added ↔ removed, before ↔ after). The "changed by" record is the same event either way.
- From = To → no computation; show the "Pick two different versions" state (§7.2).
- Comparison is read-only and works for Working and Finalized versions alike.

Sort order of the list: by type in the order **added, removed, moved, renamed, status, property**; within a type by depth-first tree order (To tree; for `removed`, From tree); property differences of the same node by property label A→Z.

---

## 3. Change-type tokens (exact — use everywhere on this page)

| Type | Label | Symbol | Text / icon colour (`fg`) | Tint (`bg`) |
|---|---|---|---|---|
| added | Added | `+` | `#2E6B21` | `#E3F0DE` |
| removed | Removed | `−` (U+2212) | `#3D3D3D` | `#E6E6E6` |
| moved | Moved | `→` | `#1D5FA8` | `#E4EEFA` |
| renamed | Renamed | `Aa` | `#6B3FA0` | `#EFE7F8` |
| status | Status | `▲` | `#7A4100` | `#FFF1DE` |
| property | Property | `≡` | `#1F4D16` | `#F2F4EF` |

(`property` uses design-system tokens `--n-brand-800` / `--n-chip`.)

---

## 4. Layout (1440 × 980 at design time)

```
┌ .n-header (#044014) [☰] LOGO Nexus Master Data Central ……………… DEVELOPMENT (VA E2E Version Admin One · e2e_vadmin Switch) ┐
├ .n-context  Version [2026-11 · Working ▾]  As of [..]  Work in (Live data|Sandbox) ……………………………………… Manage versions ┤
├ rail 64 ┬ .n-main ──────────────────────────────────────────────────────────────────────────────────────────────┤
│ ⌂       │ Compare   Hierarchy (ORGTL · Organization – Total Fidelity ▾)  From (2026-10 · Finalized ▾) (⇄) To (2026-11 · Working ▾) ……… (⤓ Export CSV) │
│ ⊞       │ ┌ Differences [6] ──────────────┐ ┌ (+ Added)  Only in ORGTL ……………………………………………… View in tree ┐ │
│ ⌕       │ │ (All 6)(+ Added 1)(− Removed 1)│ │ 99301 QNT Research                     (h2 24/700)           │ │
│ ⇄ ■     │ │ (→ Moved 1)(Aa Renamed 1)      │ │ What changed                                                 │ │
│ ◷       │ │ (▲ Status 1)(≡ Property 1)     │ │ ┌──────────┬ Before · 2026-10 ┬ After · 2026-11 ┐          │ │
│ ───     │ ├────────────────────────────────┤ │ │ Parent   │ —                │ AGQNT (tinted)  │          │ │
│ ☰ (3)   │ │▌[+] 99301 QNT Research         │ │ │ Node type│ —                │ Leaf            │          │ │
│ ◫       │ │     Added under AGQNT          │ │ └──────────┴──────────────────┴─────────────────┘          │ │
│ ≡       │ │ [−] 34322 QNT Contingent       │ │ Where it sits in ORGTL                                       │ │
│ ───     │ │     Removed from AGQNT         │ │ ┌ Before · 2026-10 [Finalized] ┐ ┌ After · 2026-11 [Working] ┐│ │
│ ⧉       │ │ [→] 34307 QNT Quant Dev        │ │ │ Not in ORGTL in 2026-10      │ │ ORGTL / OC005 / … / AMQNT /││ │
│ ⚙       │ │     Moved AGQNT → AGCOE        │ │ │                              │ │ ■ AGQNT US Quant Technology││ │
│         │ │ …                              │ │ └──────────────────────────────┘ │   ○ 99301 QNT Research (Added)│ │
│         │ │                                │ │ Change record ……………………………………………………… View in audit │ │
│         │ │                                │ │ Changed by │ When │ Request (CR-1038)                       │ │
│         │ └────────────────────────────────┘ └──────────────────────────────────────────────────────────────┘ │
│         ├ .n-draftbar (global, unchanged) ───────────────────────────────────────────────────────────────────────┤
└─────────┴────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

- Root: `.n-app.n-app--fixed` (100vh, min-height 900px, **no page scroll**).
- `.n-main` is a column: **toolbar** (fixed height, auto) → **body** (flex 1, min-height 0) → `.n-draftbar`.
- Body (`.n-cmp`): flex row, `gap 16px`, `padding 16px 20px`, `align-items: stretch`.
- **List card**: `width 400px; flex 0 0 400px`. Header fixed; list scrolls.
- **Detail card**: `flex 1 1 auto; min-width 0`; body scrolls. Inner column `max-width 860px`, **left-aligned**, `padding 28px 32px 32px`, `gap 24px`.
- Sidebar: collapsed rail by default; Worklist badge as elsewhere.
- Responsive: content width < 1100px → list card `width 340px; flex-basis 340px`; detail "Where it sits" panels stack to 1 column below 760px detail width. (< 900px viewport: out of scope.)

---

## 5. Toolbar

Container: `flex 0 0 auto; display flex; flex-wrap wrap; align-items center; gap 12px 20px; padding 16px 20px 0`.

Order left → right:
1. `h1` **Compare** — 20px / 700, letter-spacing −0.01em, margin 0.
2. Controls group — flex, wrap, `gap 10px 16px`:
   - **Hierarchy**: visible `<label for="cmp-hier">Hierarchy</label>` (12px / 600, `#5C5C5C`, gap 8px to the select) + select.
   - **From** label + select, **swap button**, **To** label + select (all in one flex row, `gap 8px`).
3. Spacer (`flex 1 1 auto`).
4. **Export CSV** button.

**Select (all three)**: native `<select>`, `appearance:none`, height 40px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 36px 0 14px`, 13px / 600, `#1F1F1F` on `#FFFFFF`. Wrapper `position:relative`; chevron SVG 12×12, stroke `#1F1F1F` width 2.4, round caps, path `m6 9 6 6 6-6`, `position:absolute; right:14px; top:14px; pointer-events:none`. Width = content (no fixed width; options never truncated).

**Option text formats (exact):**
- Hierarchy: `{CODE} · {Name}` — e.g. `ORGTL · Organization – Total Fidelity`, `ORGTP · Total Fidelity Technology`, `AMBUS · Asset Management BU Alternate`, `E2E_H · E2E Legal Group`, `E2E_MGMT · E2E Management View`. Order = same as the Hierarchies screen list (primary first, then alphabetical by code).
- Version: `{code} · {Working|Finalized}` — newest first: `2026-11 · Working`, `2026-10 · Finalized`, `2026-09 · Finalized`.

**Swap button**: 40×40 circle, `border 1.5px solid #BDBDBD`, bg `#FFFFFF`, icon 16×16 stroke `currentColor` (`#1F1F1F`) width 2, paths `M7 7h12` `m15 3 4 4-4 4` `M17 17H5` `m9 13-4 4 4 4`. `aria-label` and `title` = "Swap From and To". Click swaps the two values (and runs the comparison).

**Export CSV button**: inline-flex, `gap 8px`, height 40px, `padding 0 16px`, radius 999px, `border 1.5px solid #BDBDBD`, bg `#FFFFFF`, 13px / 600, `white-space:nowrap`; leading icon 16×16 stroke 2, paths `M12 4v11` `m7 10 5 5 5-5` `M5 20h14`. Label **Export CSV**. Disabled (`opacity .5`) when there are 0 differences or the comparison is loading/failed.

**Defaults on first open** (no URL params):
- Hierarchy = the hierarchy last opened on Hierarchies/Compare (per user, localStorage `nexus.lastHierarchy`), else the first in the list.
- To = the context-bar version.
- From = the newest **Finalized** version older than To; if To is Finalized, the next older Finalized version; if none exists, From = To (→ §7.2 state).
- This screen **never changes** the context-bar version, as-of date or workspace.

---

## 6. List card (left) — Differences

Card: bg `#FFFFFF`, `border 1px solid #D9D9D9`, radius 16px, column, overflow hidden.

### 6.1 Header
- Container `padding 18px 18px 14px`, column, `gap 12px`, `border-bottom 1px solid #ECECEC`.
- Row (`gap 8px`, centred): `h2` **Differences** 16px / 700, margin 0 + count pill (total, unfiltered) 12px / 700, white text on `#368727`, `padding 1px 8px`, radius 999px.
- **Filter chips** — `div[role=group][aria-label="Filter by change type"]`, flex, wrap, `gap 6px`:
  - First chip **All {total}**, then one chip per type **that has ≥ 1 difference**, in type order (§2). Types with 0 are not shown.
  - Chip: `button[aria-pressed]`, inline-flex, `gap 6px`, height 32px, `padding 0 12px 0 5px` (All: `0 12px`), radius 999px, `border 1.5px solid`, 13px, `#1F1F1F`, `white-space:nowrap`. Content: symbol circle (22×22, radius 50%, bg = type tint, colour = type fg, 11px / 700, centred; All has none) · label · space · count (700).

| Chip state | Border | Bg | Weight |
|---|---|---|---|
| All, active (no filter) | `#1F1F1F` | `#F2F2F2` | 600 |
| Type, active | type fg | type tint | 600 |
| Inactive | `#DCDCDC` | `#FFFFFF` | 500 |

  - Single-select: clicking a type chip filters to that type; clicking the active type chip or **All** clears the filter. Filtering resets selection to the first visible row.

### 6.2 List body
- `flex 1 1 auto; min-height 0; overflow:auto; padding 8px 10px 14px`.
- Row = whole-row `<button>`: flex, `align-items:flex-start`, `gap 12px`, width 100%, `text-align:left`, border 0, radius 12px, `padding 12px`, `margin-bottom 2px`, `#1F1F1F`.
  - Background: selected `#E3F0DE` (`aria-current="true"`), hover `#F2F2F2`, else transparent.
  - Type square: 30×30, radius 9px, bg type tint, colour type fg, 13px / 700, symbol centred, `aria-hidden`.
  - Text column (`gap 3px`, `min-width 0`):
    - Line 1 (14px / 600, line-height 1.4, wraps): code (JetBrains Mono 13px / 500, `#2E6B21`, `margin-right 8px`) + node name.
    - Line 2 (13px, `#4A4A4A`, line-height 1.4, wraps): description (exact formats):

| Type | Line 2 |
|---|---|
| added | `Added under {newParentCode}` |
| removed | `Removed from {oldParentCode}` |
| moved | `Moved {oldParentCode} → {newParentCode}` |
| renamed | `Renamed “{oldName}” → “{newName}”` (curly quotes) |
| status | `Status {Active} → {Inactive}` |
| property | `{Property label} {oldValue} → {newValue}` (empty value shown as `—`) |

  - Node name on line 1 = name in To (removed: name in From).
- More than 200 differences: list is virtualised (row height is variable; use CDK virtual scroll with autosize), counts still exact. No paging UI.
- Keyboard: Up/Down move focus between rows, Enter/Space selects.

---

## 7. Detail card (right) and page states

Card: bg `#FFFFFF`, `border 1px solid #D9D9D9`, radius 16px, column, overflow hidden. Scroll body `flex 1; min-height 0; overflow:auto`; inner column per §4.

### 7.1 Detail of the selected difference

**(a) Header** — column, `gap 10px`:
- Row (flex, wrap, centred, `gap 10px`):
  - Type pill: inline-flex, `gap 7px`, `padding 3px 12px 3px 4px`, radius 999px, 12px / 700, bg type tint, colour type fg; inner circle 20×20 white, symbol 11px.
  - Scope text 12px `#5C5C5C`: **`Global · same in every hierarchy`** (renamed, status, global property) or **`Only in {HIER}`** (added, removed, moved, local property).
  - Spacer.
  - Link **View in tree** (13px / 600) → `/hierarchies/{h}?node={code}&version={toCode}` (removed: `version={fromCode}`).
- `h2` (24px / 700, line-height 1.3, letter-spacing −0.01em, margin 0, wraps): code (JetBrains Mono 18px / 600, `#2E6B21`, `margin-right 10px`) + node name (as list line 1).

**(b) What changed** (`section aria-label="What changed"`)
- `h3` "What changed" 15px / 700, margin 0; gap 8px.
- Grid: `grid-template-columns: 120px minmax(0,1fr) minmax(0,1fr)`, `border 1px solid #E6E6E6`, radius 12px, overflow hidden, 14px.
- Header row cells: `padding 10px 16px`, bg `#FAFAF8`, `border-bottom 1px solid #ECECEC`; first cell empty; then **`Before · {fromCode}`** and **`After · {toCode}`** (12px / 600, `#5C5C5C`).
- Data rows (`border-top 1px solid #ECECEC` between rows, none on the first):
  - Label cell: `padding 12px 16px`, 12px / 600, `#5C5C5C`.
  - Before cell: `padding 12px 16px`, `#6B6B6B`, `text-decoration: line-through` (**none** when the value is `—`), `overflow-wrap:anywhere`.
  - After cell: `padding 12px 16px`, bg type tint, `#1F1F1F`, weight 600, `overflow-wrap:anywhere`.
  - Codes and property values in JetBrains Mono 13px; names/statuses/types in Inter 14px.
- Rows per type (exact, nothing else):

| Type | Rows (label: before → after) |
|---|---|
| added | Parent: `—` → new parent code (mono) · Node type: `—` → Limb/Leaf |
| removed | Parent: old parent code (mono) → `—` · Node type: Limb/Leaf → `—` |
| moved | Parent: old → new (mono). Add **Level: n → m** only if the depth differs. |
| renamed | Name: old → new |
| status | Status: old → new |
| property | {Property label}: old → new (mono) |

**(c) Where it sits** (`section aria-label="Where it sits"`)
- `h3` **Where it sits in {HIER}** (15px / 700); gap 8px.
- Two panels side by side: `grid-template-columns: repeat(2, minmax(0,1fr)); gap 12px`. Left = Before (From), right = After (To).
- Panel: `border 1px solid #E6E6E6`, radius 12px, overflow hidden, column.
  - Head: `padding 10px 14px`, bg `#FAFAF8`, `border-bottom 1px solid #ECECEC`, flex, `gap 8px`: label **`Before · {fromCode}`** / **`After · {toCode}`** (12px / 600, `#5C5C5C`) + version status pill (11px / 600, `padding 1px 8px`, radius 999px; Working `#1F4D16` on `#E3F0DE`; Finalized `#4A4A4A` on `#ECECEC`).
  - **Node absent in that version** (added → Before; removed → After): body `padding 28px 14px`, 13px, `#6B6B6B`, centred: **`Not in {HIER} in {versionCode}`**.
  - **Node present**: body `padding 10px 10px 12px`, column, `gap 2px`:
    - Path line: JetBrains Mono 11px, `#6B6B6B`, `padding 0 6px 6px`, `overflow-wrap:anywhere` — the ancestor codes **above the parent**, root first, joined with ` / `, ending with ` /` (e.g. `ORGTL / OC005 / AM / AMT / AMQNT /`). When the parent **is** the hierarchy root there are no ancestors: omit the path line entirely.
    - Rows: the **parent** (first row) then **all its children in that version** in sibling order. (Max 25 children; beyond that show the 10 children around the selected node and a last row "+{n} more" 12px `#6B6B6B`.)
    - Row: flex, centred, `gap 8px`, `min-height 34px`, `padding 4px 8px 4px {8px parent | 32px child}`, radius 8px.
      - Icon slot 16px: limb = 12×12 square, `border 1.5px solid #3D3D3D`, white fill; leaf = 8×8 circle, `border 1.5px solid #6B6B6B`.
      - Code: JetBrains Mono 12px, `#5C5C5C`, no shrink.
      - Name: 13px, line-height 1.35, wraps, weight 600 for the parent row and the selected node, 400 otherwise.
      - Selected node row: bg = type tint, plus a tag pill on the right: 11px / 700, colour = type fg, bg `#FFFFFF`, `border 1px solid` type fg, `padding 1px 8px`, radius 999px, nowrap.
      - Inactive nodes that are not the selected node: `opacity .6`.
    - Tag text (exact):

| Type | Before panel tag | After panel tag |
|---|---|---|
| added | — (panel shows "Not in …") | Added |
| removed | Removed | — (panel shows "Not in …") |
| moved | Moved out | Moved in |
| renamed | Old name | New name |
| status | Was {active/inactive} | Now {active/inactive} |
| property | {short label} {old} (e.g. `GL 0512`) | {short label} {new} (e.g. `GL 0444`) |

    - Names in each panel are the names **in that version** (a renamed node shows the old name on the left).
    - Rows are not interactive in v1.

**(d) Change record** (`section aria-label="Change record"`)
- Head row (flex, centred, `gap 8px`): `h3` "Change record" 15px / 700 · spacer · link **View in audit** (13px / 600) → `/audit?node={code}&from={fromCode}&to={toCode}`.
- `<dl>` grid: `repeat(3, minmax(0,1fr))`, `gap 18px 24px`, `border 1px solid #E6E6E6`, radius 12px, `padding 18px 20px`, margin 0. Each item column `gap 4px`: `<dt>` 12px `#5C5C5C`; `<dd>` margin 0, 14px / 600, `#1F1F1F`.
  - **Changed by** — display name of the user who made the change.
  - **When** — `13 Nov, 10:31` (24 h; add year if not current year).
  - **Request** — request ID as a link (JetBrains Mono 13px, link colours) → `/worklist/{id}`.
- Source: the **latest** audit event between From and To that produced this difference. If the change has no request (e.g. data load), Request = `—` and Changed by = `System load`. If no audit event is found, all three show `—`.

### 7.2 Page states (replace the whole body area — list + detail — with one card)
Card: inside body padding `16px 20px`, `flex 1`, bg white, `border 1px solid #D9D9D9`, radius 16px, centred column, `gap 6px`, `padding 40px`, `text-align:center`. Title 16px / 600; sub 13px `#5C5C5C`.

| State | Title | Sub | Extra |
|---|---|---|---|
| From = To | Pick two different versions | From and To are both {code}. | — |
| No differences | No differences | {HIER} is the same in {from} and {to}. | — |
| Hierarchy missing in a version (`404 HIERARCHY_NOT_IN_VERSION`) | {HIER} doesn't exist in {code} | Pick another version or hierarchy. | — |
| Server error | Couldn't compare these versions | Try again. If it keeps happening, share reference {traceId}. | Secondary pill button **Try again** (40px, `margin-top 10px`) re-runs the request. Reference in JetBrains Mono 12px. |

### 7.3 Loading
- On every run: keep the toolbar interactive; replace list rows with 6 skeleton rows (30×30 square + bars 14px×60% and 10px×40%, bg `#F2F2F2`, radius 6px/9px) and the detail with skeleton blocks (pill 24×110, title 24px×50%, a 3-row table block 132px tall, two panel blocks 180px tall). Count pill shows "–"; chips hidden.
- A newer run cancels the in-flight request (switchMap). No spinner, no "Comparing…" text.
- Skeletons appear only if the response takes > 150 ms.

---

## 8. Behaviour

### 8.1 URL & state
- `/compare?h={HIER}&from={code}&to={code}&type={typeKey?}&sel={differenceKey?}` — always kept in sync (replaceUrl). Opening such a URL restores everything.
- Changing Hierarchy clears `type` and `sel`. Changing From/To or swapping keeps `type` if that type still has differences, clears `sel`.
- Selection default = first row of the (filtered) list. If `sel` no longer exists after a run, fall back to the first row.
- Last-used hierarchy is stored (`nexus.lastHierarchy`).

### 8.2 Export CSV
- `GET /api/compare/export.csv?hierarchy=&from=&to=` (exports **all** differences, ignores the chip filter).
- File name: `compare_{HIER}_{from}_{to}.csv`. UTF-8 with BOM. Columns (header row exact):
  `Hierarchy,From,To,Change type,Code,Name,Field,Before,After,Scope,Changed by,Changed at,Request`
  - Change type = label from §3; Field = "Parent" / "Name" / "Status" / property label / "Node"; Scope = Global/Local; Changed at = ISO-8601.
- After the download starts: toast **"Exported {n} differences as CSV"** ("difference" when n = 1).
- Toast: `role="status"`, fixed, `bottom 84px`, centred, bg `#1F1F1F`, white 13px, `padding 10px 18px`, radius 999px, z-index 40; auto-hide after 2.5 s; no buttons.

### 8.3 Interactions summary
| Action | Result |
|---|---|
| Change Hierarchy / From / To | Run comparison immediately; reset per §8.1 |
| Swap | From ↔ To, run |
| Chip | Filter list, select first visible |
| Row click | Show its detail; list scroll position unchanged |
| View in tree | Navigate to Hierarchies (node selected, version per §7.1a) |
| Request link | Navigate to Worklist with that request selected |
| View in audit | Navigate to Audit filtered to node and version range |

---

## 9. Data & API (Spring Boot)

```
GET /api/versions                                   → [{ code, status: 'WORKING'|'FINALIZED', createdAt }]  (newest first)
GET /api/hierarchies?version={code}                 → [{ code, name, primary: boolean }]
GET /api/compare?hierarchy=&from=&to=               → CompareResult
    400 SAME_VERSION | 404 HIERARCHY_NOT_IN_VERSION {version} | 500 {traceId}
GET /api/compare/placement?hierarchy=&version=&node= → Placement   (lazy, per selected difference & side)
GET /api/compare/export.csv?hierarchy=&from=&to=    → text/csv
```

```ts
type DiffType = 'added' | 'removed' | 'moved' | 'renamed' | 'status' | 'property';
interface CompareResult {
  hierarchy: string; from: { code: string; status: string }; to: { code: string; status: string };
  counts: Record<DiffType, number>; total: number;
  differences: Difference[];              // already sorted per §2
}
interface Difference {
  key: string;                            // "moved:34307", "property:34295:ORACLE_GL_COMPANY"
  type: DiffType; code: string;
  name: string;                           // name in To (removed: in From)
  nodeType: 'LIMB' | 'LEAF';
  scope: 'GLOBAL' | 'LOCAL';
  field?: { key: string; label: string; shortLabel: string };   // property only, e.g. {ORACLE_GL_COMPANY, "Oracle GL company", "GL"}
  before: string | null; after: string | null;                  // parent code / name / status / property value
  oldParent?: string; newParent?: string; oldLevel?: number; newLevel?: number;
  record?: { changedBy: string; changedAt: string; requestId?: string };
}
interface Placement {                       // node absent → 204 No Content
  ancestors: string[];                      // codes from root to the parent's parent
  parent: { code: string; name: string; type: 'LIMB'|'LEAF'; status: string };
  children: { code: string; name: string; type: 'LIMB'|'LEAF'; status: string }[];   // sibling order
}
```
Server notes: compute with set operations on `(code, parent)` per version for the hierarchy, then join node attributes; cache results per `(h, from, to)` for Finalized-vs-Finalized pairs. Swapped requests reuse the cache by inverting.

---

## 10. CSS to append to `src/styles.css` (after `nexus-ui.css`)

```css
/* ===== Compare ===== */
.n-cmp-toolbar{flex:0 0 auto;display:flex;flex-wrap:wrap;align-items:center;gap:12px 20px;padding:16px 20px 0}
.n-cmp-toolbar h1{margin:0;font-size:20px;font-weight:700;letter-spacing:-.01em}
.n-cmp-controls{display:flex;flex-wrap:wrap;align-items:center;gap:10px 16px}
.n-cmp-field{display:flex;align-items:center;gap:8px}
.n-cmp-field label{font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-cmp-select{position:relative}
.n-cmp-select select{appearance:none;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 36px 0 14px;font-size:13px;font-weight:600;color:var(--n-text);background:var(--n-surface);cursor:pointer}
.n-cmp-select svg{position:absolute;right:14px;top:14px;width:12px;height:12px;stroke:var(--n-text);stroke-width:2.4;fill:none;stroke-linecap:round;stroke-linejoin:round;pointer-events:none}
.n-cmp-swap{width:40px;height:40px;border-radius:50%;border:1.5px solid var(--n-border-input);background:var(--n-surface);display:flex;align-items:center;justify-content:center;padding:0;color:var(--n-text)}
.n-cmp-swap svg,.n-cmp-export svg{width:16px;height:16px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-cmp-export{display:inline-flex;align-items:center;gap:8px;height:40px;padding:0 16px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);font-size:13px;font-weight:600;white-space:nowrap}
.n-cmp-export:hover,.n-cmp-swap:hover{background:var(--n-surface-sunk)}
.n-cmp{flex:1 1 auto;min-height:0;display:flex;gap:16px;padding:16px 20px;align-items:stretch}
.n-cmp-card{background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);display:flex;flex-direction:column;overflow:hidden;min-width:0}
.n-cmp-list{width:400px;flex:0 0 400px}
.n-cmp-detail{flex:1 1 auto}
.n-cmp-list__head{padding:18px 18px 14px;display:flex;flex-direction:column;gap:12px;border-bottom:1px solid var(--n-divider)}
.n-cmp-list__title{display:flex;align-items:center;gap:8px}
.n-cmp-list__title h2{margin:0;font-size:16px;font-weight:700}
.n-cmp-total{font-size:12px;font-weight:700;color:#fff;background:var(--n-brand);border-radius:var(--n-r-pill);padding:1px 8px}
.n-cmp-chips{display:flex;flex-wrap:wrap;gap:6px}
.n-cmp-chip{display:inline-flex;align-items:center;gap:6px;height:32px;padding:0 12px 0 5px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-soft);background:var(--n-surface);font-size:13px;font-weight:500;color:var(--n-text);white-space:nowrap}
.n-cmp-chip--all{padding:0 12px}
.n-cmp-chip--all[aria-pressed="true"]{border-color:var(--n-text);background:var(--n-surface-sunk);font-weight:600}
.n-cmp-chip[aria-pressed="true"]{font-weight:600}
.n-cmp-chip__sym{width:22px;height:22px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:700}
.n-cmp-chip__n{font-weight:700}
.n-cmp-list__body{flex:1 1 auto;min-height:0;overflow:auto;padding:8px 10px 14px}
.n-cmp-row{display:flex;align-items:flex-start;gap:12px;width:100%;background:transparent;border:0;border-radius:var(--n-r-tile);padding:12px;margin-bottom:2px;text-align:left;color:var(--n-text)}
.n-cmp-row:hover{background:var(--n-surface-sunk)}
.n-cmp-row[aria-current="true"]{background:var(--n-brand-50)}
.n-cmp-sq{width:30px;height:30px;flex:0 0 30px;border-radius:9px;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:13px}
.n-cmp-row__text{display:flex;flex-direction:column;gap:3px;flex:1 1 auto;min-width:0}
.n-cmp-row__name{font-size:14px;font-weight:600;line-height:1.4}
.n-cmp-code{font-family:var(--n-mono);font-size:13px;font-weight:500;color:var(--n-brand-700);margin-right:8px}
.n-cmp-row__desc{font-size:13px;color:var(--n-text-3);line-height:1.4}
/* type colours: set --t-fg / --t-bg on any element with .n-t-{type} */
.n-t-added{--t-fg:#2E6B21;--t-bg:#E3F0DE}.n-t-removed{--t-fg:#3D3D3D;--t-bg:#E6E6E6}.n-t-moved{--t-fg:#1D5FA8;--t-bg:#E4EEFA}
.n-t-renamed{--t-fg:#6B3FA0;--t-bg:#EFE7F8}.n-t-status{--t-fg:#7A4100;--t-bg:#FFF1DE}.n-t-property{--t-fg:#1F4D16;--t-bg:#F2F4EF}
.n-cmp-sq,.n-cmp-chip__sym{color:var(--t-fg);background:var(--t-bg)}
.n-cmp-chip[aria-pressed="true"]:not(.n-cmp-chip--all){border-color:var(--t-fg);background:var(--t-bg)}
.n-cmp-detail__scroll{flex:1 1 auto;min-height:0;overflow:auto}
.n-cmp-detail__inner{max-width:860px;padding:28px 32px 32px;display:flex;flex-direction:column;gap:24px}
.n-cmp-head{display:flex;flex-direction:column;gap:10px}
.n-cmp-head__line{display:flex;align-items:center;gap:10px;flex-wrap:wrap}
.n-cmp-typepill{display:inline-flex;align-items:center;gap:7px;padding:3px 12px 3px 4px;border-radius:var(--n-r-pill);font-size:12px;font-weight:700;color:var(--t-fg);background:var(--t-bg)}
.n-cmp-typepill span{width:20px;height:20px;border-radius:50%;background:#fff;display:flex;align-items:center;justify-content:center;font-size:11px}
.n-cmp-scope{font-size:12px;color:var(--n-text-muted)}
.n-cmp-head a,.n-cmp-section__head a{font-size:13px}
.n-cmp-head h2{margin:0;font-size:24px;font-weight:700;line-height:1.3;letter-spacing:-.01em}
.n-cmp-head__code{font-family:var(--n-mono);font-size:18px;font-weight:600;color:var(--n-brand-700);margin-right:10px}
.n-cmp-section{display:flex;flex-direction:column;gap:8px}
.n-cmp-section h3{margin:0;font-size:15px;font-weight:700}
.n-cmp-section__head{display:flex;align-items:center;gap:8px}
.n-cmp-section__head h3{flex:1 1 auto}
.n-cmp-diff{border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);overflow:hidden;display:grid;grid-template-columns:120px minmax(0,1fr) minmax(0,1fr);font-size:14px}
.n-cmp-diff__th{padding:10px 16px;background:var(--n-surface-alt);border-bottom:1px solid var(--n-divider);font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-cmp-diff__label,.n-cmp-diff__before,.n-cmp-diff__after{padding:12px 16px;border-top:1px solid var(--n-divider);overflow-wrap:anywhere}
.n-cmp-diff__first{border-top-color:transparent}
.n-cmp-diff__label{font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-cmp-diff__before{color:var(--n-text-faint);text-decoration:line-through}
.n-cmp-diff__before--empty{text-decoration:none}
.n-cmp-diff__after{background:var(--t-bg);color:var(--n-text);font-weight:600}
.n-cmp-mono{font-family:var(--n-mono);font-size:13px}
.n-cmp-sides{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:12px}
.n-cmp-side{min-width:0;border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);overflow:hidden;display:flex;flex-direction:column}
.n-cmp-side__head{padding:10px 14px;background:var(--n-surface-alt);border-bottom:1px solid var(--n-divider);display:flex;align-items:center;gap:8px;font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-cmp-vpill{font-size:11px;font-weight:600;border-radius:var(--n-r-pill);padding:1px 8px;color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-cmp-vpill--working{color:var(--n-brand-800);background:var(--n-brand-50)}
.n-cmp-side__missing{padding:28px 14px;font-size:13px;color:var(--n-text-faint);text-align:center}
.n-cmp-side__body{padding:10px 10px 12px;display:flex;flex-direction:column;gap:2px}
.n-cmp-side__path{padding:0 6px 6px;font-family:var(--n-mono);font-size:11px;color:var(--n-text-faint);overflow-wrap:anywhere}
.n-cmp-node{display:flex;align-items:center;gap:8px;min-height:34px;padding:4px 8px 4px 32px;border-radius:var(--n-r-tree)}
.n-cmp-node--parent{padding-left:8px}
.n-cmp-node--sel{background:var(--t-bg)}
.n-cmp-node--inactive{opacity:.6}
.n-cmp-node__icon{width:16px;flex:0 0 16px;display:flex;align-items:center;justify-content:center}
.n-cmp-limb{width:12px;height:12px;border:1.5px solid var(--n-text-2);background:#fff}
.n-cmp-leaf{width:8px;height:8px;border-radius:50%;border:1.5px solid var(--n-text-faint)}
.n-cmp-node__code{font-family:var(--n-mono);font-size:12px;color:var(--n-text-muted);flex:0 0 auto}
.n-cmp-node__name{font-size:13px;line-height:1.35;flex:1 1 auto;min-width:0}
.n-cmp-node--parent .n-cmp-node__name,.n-cmp-node--sel .n-cmp-node__name{font-weight:600}
.n-cmp-node__tag{font-size:11px;font-weight:700;color:var(--t-fg);background:#fff;border:1px solid var(--t-fg);border-radius:var(--n-r-pill);padding:1px 8px;white-space:nowrap}
.n-cmp-more{padding:4px 8px 0 32px;font-size:12px;color:var(--n-text-faint)}
.n-cmp-record{margin:0;display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:18px 24px;border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);padding:18px 20px}
.n-cmp-record div{display:flex;flex-direction:column;gap:4px;min-width:0}
.n-cmp-record dt{font-size:12px;color:var(--n-text-muted)}
.n-cmp-record dd{margin:0;font-size:14px;font-weight:600}
.n-cmp-record dd a{font-family:var(--n-mono);font-size:13px}
.n-cmp-state{flex:1 1 auto;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:6px;padding:40px;text-align:center}
.n-cmp-state strong{font-size:16px;font-weight:600}
.n-cmp-state span{font-size:13px;color:var(--n-text-muted)}
.n-cmp-state code{font-family:var(--n-mono);font-size:12px}
.n-cmp-state .n-btn{margin-top:10px}
.n-cmp-toast{position:fixed;bottom:84px;left:50%;transform:translateX(-50%);background:var(--n-text);color:#fff;border-radius:var(--n-r-pill);padding:10px 18px;font-size:13px;z-index:40}
@media (max-width:1240px){.n-cmp-list{width:340px;flex-basis:340px}}
@container (max-width:760px){.n-cmp-sides{grid-template-columns:1fr}}
```
(Put `container-type:inline-size` on `.n-cmp-detail__inner` for the container query.)

---

## 11. HTML contract (Angular template skeleton)

```html
<div class="n-cmp-toolbar">
  <h1>Compare</h1>
  <div class="n-cmp-controls">
    <div class="n-cmp-field"><label for="cmp-hier">Hierarchy</label>
      <span class="n-cmp-select"><select id="cmp-hier" [formControl]="hier">
        <option *ngFor="let h of hierarchies" [value]="h.code">{{h.code}} · {{h.name}}</option></select>
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg></span></div>
    <div class="n-cmp-field">
      <label for="cmp-from">From</label>
      <span class="n-cmp-select"><select id="cmp-from" [formControl]="from">
        <option *ngFor="let v of versions" [value]="v.code">{{v.code}} · {{v.statusLabel}}</option></select>
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg></span>
      <button type="button" class="n-cmp-swap" aria-label="Swap From and To" title="Swap From and To" (click)="swap()">
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 7h12"/><path d="m15 3 4 4-4 4"/><path d="M17 17H5"/><path d="m9 13-4 4 4 4"/></svg></button>
      <label for="cmp-to">To</label>
      <span class="n-cmp-select"><select id="cmp-to" [formControl]="to">…same options…</select><svg …/></span>
    </div>
  </div>
  <span class="n-spacer"></span>
  <button type="button" class="n-cmp-export" [disabled]="!result?.total" (click)="export()">
    <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 4v11"/><path d="m7 10 5 5 5-5"/><path d="M5 20h14"/></svg>Export CSV</button>
</div>

<div class="n-cmp">
  <!-- page state (§7.2) -->
  <div *ngIf="state" class="n-cmp-card n-cmp-state" style="flex:1">
    <strong>{{state.title}}</strong><span>{{state.sub}} <code *ngIf="state.traceId">{{state.traceId}}</code></span>
    <button *ngIf="state.retry" class="n-btn n-btn--secondary n-btn--sm" (click)="run()">Try again</button>
  </div>

  <ng-container *ngIf="!state && result">
    <section class="n-cmp-card n-cmp-list" aria-label="Differences">
      <div class="n-cmp-list__head">
        <div class="n-cmp-list__title"><h2>Differences</h2><span class="n-cmp-total">{{result.total}}</span></div>
        <div class="n-cmp-chips" role="group" aria-label="Filter by change type">
          <button class="n-cmp-chip n-cmp-chip--all" [attr.aria-pressed]="!type" (click)="setType(null)">All <span class="n-cmp-chip__n">{{result.total}}</span></button>
          <button *ngFor="let t of presentTypes" class="n-cmp-chip" [ngClass]="'n-t-'+t.key" [attr.aria-pressed]="type===t.key" (click)="setType(t.key)">
            <span class="n-cmp-chip__sym" aria-hidden="true">{{t.sym}}</span>{{t.label}} <span class="n-cmp-chip__n">{{t.count}}</span></button>
        </div>
      </div>
      <div class="n-cmp-list__body">
        <button *ngFor="let d of shown" class="n-cmp-row" [ngClass]="'n-t-'+d.type" [attr.aria-current]="d.key===selKey" (click)="select(d.key)">
          <span class="n-cmp-sq" aria-hidden="true">{{sym[d.type]}}</span>
          <span class="n-cmp-row__text">
            <span class="n-cmp-row__name"><span class="n-cmp-code">{{d.code}}</span>{{d.name}}</span>
            <span class="n-cmp-row__desc">{{describe(d)}}</span>
          </span>
        </button>
      </div>
    </section>

    <section class="n-cmp-card n-cmp-detail" aria-label="Change detail" *ngIf="sel as d" [ngClass]="'n-t-'+d.type">
      <div class="n-cmp-detail__scroll"><div class="n-cmp-detail__inner">
        <div class="n-cmp-head">
          <div class="n-cmp-head__line">
            <span class="n-cmp-typepill"><span aria-hidden="true">{{sym[d.type]}}</span>{{label[d.type]}}</span>
            <span class="n-cmp-scope">{{scopeText(d)}}</span><span class="n-spacer"></span>
            <a [routerLink]="['/hierarchies', h]" [queryParams]="treeParams(d)">View in tree</a>
          </div>
          <h2><span class="n-cmp-head__code">{{d.code}}</span>{{d.name}}</h2>
        </div>
        <section class="n-cmp-section" aria-label="What changed"><h3>What changed</h3>
          <div class="n-cmp-diff">
            <span class="n-cmp-diff__th"></span><span class="n-cmp-diff__th">Before · {{from}}</span><span class="n-cmp-diff__th">After · {{to}}</span>
            <ng-container *ngFor="let r of rows(d); let first = first">
              <span class="n-cmp-diff__label" [class.n-cmp-diff__first]="first">{{r.label}}</span>
              <span class="n-cmp-diff__before" [class.n-cmp-diff__first]="first" [class.n-cmp-diff__before--empty]="r.before==='—'" [class.n-cmp-mono]="r.mono">{{r.before}}</span>
              <span class="n-cmp-diff__after" [class.n-cmp-diff__first]="first" [class.n-cmp-mono]="r.mono">{{r.after}}</span>
            </ng-container>
          </div>
        </section>
        <section class="n-cmp-section" aria-label="Where it sits"><h3>Where it sits in {{h}}</h3>
          <div class="n-cmp-sides">
            <div *ngFor="let s of sides" class="n-cmp-side">
              <div class="n-cmp-side__head">{{s.label}} <span class="n-cmp-vpill" [class.n-cmp-vpill--working]="s.working">{{s.status}}</span></div>
              <div *ngIf="!s.placement" class="n-cmp-side__missing">Not in {{h}} in {{s.code}}</div>
              <div *ngIf="s.placement as p" class="n-cmp-side__body">
                <span *ngIf="p.ancestors.length" class="n-cmp-side__path">{{p.ancestors.join(' / ')}} /</span>
                <div class="n-cmp-node n-cmp-node--parent">…parent icon/code/name…</div>
                <div *ngFor="let c of p.children" class="n-cmp-node" [class.n-cmp-node--sel]="c.code===d.code" [class.n-cmp-node--inactive]="c.status==='Inactive' && c.code!==d.code">
                  <span class="n-cmp-node__icon" aria-hidden="true"><span [ngClass]="c.type==='LIMB' ? 'n-cmp-limb' : 'n-cmp-leaf'"></span></span>
                  <span class="n-cmp-node__code">{{c.code}}</span><span class="n-cmp-node__name">{{c.name}}</span>
                  <span *ngIf="c.code===d.code" class="n-cmp-node__tag">{{tag(d, s.side)}}</span>
                </div>
              </div>
            </div>
          </div>
        </section>
        <section class="n-cmp-section" aria-label="Change record">
          <div class="n-cmp-section__head"><h3>Change record</h3><a routerLink="/audit" [queryParams]="auditParams(d)">View in audit</a></div>
          <dl class="n-cmp-record">
            <div><dt>Changed by</dt><dd>{{d.record?.changedBy || '—'}}</dd></div>
            <div><dt>When</dt><dd>{{(d.record?.changedAt | nxDateTime) || '—'}}</dd></div>
            <div><dt>Request</dt><dd><a *ngIf="d.record?.requestId; else dash" [routerLink]="['/worklist', d.record.requestId]">{{d.record.requestId}}</a><ng-template #dash>—</ng-template></dd></div>
          </dl>
        </section>
      </div></div>
    </section>
  </ng-container>
</div>
<div *ngIf="toast" class="n-cmp-toast" role="status">{{toast}}</div>
<app-draft-bar />
```

---

## 12. Mock data (use exactly this for demos and tests)

Versions: `2026-11` Working, `2026-10` Finalized, `2026-09` Finalized (2026-09 identical to 2026-10).
Default view: Hierarchy **ORGTL**, From **2026-10**, To **2026-11**.

Tree (2026-10, ORGTL; same lower branch under ORGTP via `ORGTP / BUTCH / AMT / AMQNT` and AMBUS via `AMBUS / AMT / AMQNT`):
```
ORGTL Organization – Total Fidelity
└ OC005 OC005
  └ AM Asset Management
    └ AMT Asset Management Technology
      └ AMQNT Quant Technology
        ├ AGCOE AM AI Center of Excellence      (limb, no children)
        └ AGQNT US Quant Technology
          ├ 31679 UK QNT Tech
          ├ 34295 QNT Engineering               (Oracle GL company 0512)
          ├ 34299 QNT Leadership
          ├ 34301 QNT Analytics                 (Active)
          ├ 34307 QNT Quant Dev
          └ 34322 QNT Contingent                (Inactive)
```
2026-11 changes: 99301 *QNT Research* added under AGQNT · 34322 removed from AGQNT (ORGTL only) · 34307 moved AGQNT → AGCOE (ORGTL only) · 34301 renamed to *QNT Analytics Platform* and set Inactive (global) · 34295 Oracle GL company 0512 → 0444 (global).

Expected differences — ORGTL, 2026-10 → 2026-11 (**6**, in this order):

| # | Type | Code | Name | Line 2 | Record (by · when · request) |
|---|---|---|---|---|---|
| 1 | added | 99301 | QNT Research | Added under AGQNT | u_editor1 · 13 Nov, 10:31 · CR-1038 |
| 2 | removed | 34322 | QNT Contingent | Removed from AGQNT | E2E Steward · 12 Nov, 16:05 · CR-1036 |
| 3 | moved | 34307 | QNT Quant Dev | Moved AGQNT → AGCOE | u_editor1 · 13 Nov, 10:42 · CR-1038 |
| 4 | renamed | 34301 | QNT Analytics Platform | Renamed “QNT Analytics” → “QNT Analytics Platform” | u_editor1 · 13 Nov, 10:48 · CR-1038 |
| 5 | status | 34301 | QNT Analytics Platform | Status Active → Inactive | u_editor1 · 13 Nov, 10:52 · CR-1038 |
| 6 | property | 34295 | QNT Engineering | Oracle GL company 0512 → 0444 | E2E Steward · 9 Nov, 14:20 · CR-1035 |

Other expected results:
- ORGTP or AMBUS, 2026-10 → 2026-11: **3** differences (renamed 34301, status 34301, property 34295).
- E2E_H / E2E_MGMT: "No differences".
- 2026-09 → 2026-10: "No differences". From = To: "Pick two different versions".
- Swapped (2026-11 → 2026-10, ORGTL): 6 differences — 99301 *Removed from AGQNT*, 34322 *Added under AGQNT*, 34307 *Moved AGCOE → AGQNT*, rename and status reversed, GL 0444 → 0512; records unchanged.
- First render: 99301 selected; Before panel "Not in ORGTL in 2026-10"; After panel path `ORGTL / OC005 / AM / AMT / AMQNT /`, parent AGQNT with children 31679, 34295, 34299, 34301, 99301, 99301 tinted green with tag "Added".

---

## 13. Accessibility
- One `h1` (Compare). "Differences" and the node title are `h2`; detail sections `h3`.
- All three selects have visible labels. Swap button has `aria-label`.
- Chips: `aria-pressed`. Rows: `aria-current="true"` on the selected one; detail section `aria-label="Change detail"`.
- Type is never conveyed by colour alone: every chip/row/pill carries the label or symbol, and line 2 starts with the verb.
- The before/after grid: give the three header cells `role="columnheader"` and the grid `role="table"` with rows wrapped in `role="row"` (or use a real `<table>` with the same styles).
- Struck-through "before" values get visually hidden prefix "Before:"; after values "After:" (`.n-sr-only`).
- Toast `role="status"`. Contrast ≥ 4.5:1 for all text.

---

## 14. Do NOT
- Don't add a "Run comparison" button, hierarchy tabs, a page subtitle, or an "eyebrow" label above the title.
- Don't show full side-by-side trees of the whole hierarchy — only the parent + children panels per selected difference.
- Don't put a summary sentence or chart above the list; the count pill and chips are the summary.
- Don't truncate names, paths or values with ellipsis — wrap.
- Don't make tree rows in "Where it sits" clickable or expandable (v1).
- Don't change the context-bar version from this page.
- Don't invent extra change types, colours, columns or CSV fields.
- Don't show "Something went wrong" — use the §7.2 error state.

---

## 15. Acceptance checklist
- [ ] Shell identical to other screens; Compare active in the rail.
- [ ] Toolbar: h1 20/700, Hierarchy / From / ⇄ / To pill selects (40px), Export CSV right-aligned; no Run button.
- [ ] Changing any control re-runs immediately; skeleton only after 150 ms; in-flight request cancelled.
- [ ] Defaults per §5; URL params per §8.1 round-trip.
- [ ] List card 400px: Differences + total pill; chips only for present types; All/active styles per §6.1.
- [ ] Rows: 30px type square, mono code + name (wrapping), line 2 per §6.2 table; selected `#E3F0DE`.
- [ ] Detail header: type pill, scope text, View in tree, mono code + 24/700 name.
- [ ] What changed grid: 120px label column, Before struck (except `—`), After tinted + 600; rows per type table.
- [ ] Where it sits: two panels, version pills, path line, parent + children, selected row tinted with exact tag text, "Not in … in …" for absent side.
- [ ] Change record: 3 facts, request links to Worklist, View in audit link.
- [ ] States: same version, no differences, hierarchy missing, error with Try again + reference.
- [ ] Export CSV: exact columns and file name; toast text; disabled when 0.
- [ ] Mock data produces exactly the §12 results.
- [ ] Pixel check vs. the "Compare (new · master–detail)" board at 1440px: computed styles match §5–§7 and §10.
