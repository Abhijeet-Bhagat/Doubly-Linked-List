# Queries screen — Build Specification (exact styles)

> **Give this file to the LLM/developer together with `nexus-design-system.md` and a screenshot of the canvas board "Queries (unified shell)".** The page body is specified down to every size, colour and state, and §5 contains a CSS block to **append verbatim** to `src/styles.css` after `nexus-ui.css`. Component `.scss` files may contain layout only. If this file and the screenshot disagree, this file wins.
>
> **Shell note:** the Queries board was drawn before two shell updates. Build it with the *current* shell from the design system: header background `#044014` with the hamburger, collapsible sidebar (64px rail by default, **Queries** active), context bar, and the global draft bar at the bottom (same as Home/Hierarchies). Everything below the context bar is as specified here.

---

## 1. Purpose and users
Find nodes by their **property values** — the replacement for MDC's *Query* module. Business users (stewards, finance reference-data team) use it to answer questions like:
- "Which cost centres are in the UK?"
- "Which nodes under AMT use ledger L300, including inactive ones?"
- "Which leaves have a non-USD accounting currency?"

**What makes it simpler than MDC**
| MDC today | Here |
|---|---|
| Query builder needs knowing property names and operator syntax | Plain-language operators ("is", "contains", "ancestor has") and property labels with their keys only as columns |
| Global vs local queries, Above/Below operators are jargon | "Whole hierarchy / Under a node" toggle; **ancestor has / descendant has** operators |
| Hard to tell what a query will do | Live **"In plain words"** sentence under the conditions |
| Columns picked from a Ctrl-click list box | Toggle chips |
| Results go stale silently when you edit | Amber "results are from the last run" bar with **Run again** |
| Saved queries buried | Saved queries panel beside the builder, one click to load and run |

Route: `/queries` (optional `?saved=<id>` loads and runs a saved query).

---

## 2. Layout (design width 1440px)

```
┌ .n-header (#044014) ─ [☰] LOGO Nexus  Master Data Central …… DEVELOPMENT (user chip · Switch) ┐
├ .n-context ─ Version [2026-11 · Working ▾]  As of [16/11/2026]  Work in (Live data|Sandbox) … Manage versions ┤
├ rail ┬ .n-main ──────────────────────────────────────────────────────────────────────────┤
│      │ .n-q-page  (padding 24px 28px 32px · gap 20px · max-width 1360px · scrolls)        │
│      │  Queries                                                                           │
│      │  Find nodes by their properties. Results reflect the version, date and workspace above. │
│      │  ┌ Builder card (flex 999 1 640px) ───────────────────┐ ┌ Saved queries (1 1 300px) ┐│
│      │  │ 1 · Where to look                                   │ │ Saved queries   3 saved   ││
│      │  │ [Hierarchy ▾] (Whole|Under a node)[AMT] (All|○Leaves|□Limbs) ◯ Include inactive │ │ UK cost centres  ORGTL ││
│      │  │──────────────────────────────────────────────────── │ │ …                         ││
│      │  │ 2 · Conditions  Match (All of these|Any of these)   │ │                           ││
│      │  │ [Finance country ▾][is ▾][UK        ] ×             │ │                           ││
│      │  │ + Add condition   + Add group                       │ │                           ││
│      │  │ 🔍 In plain words: Find leaves (cost centres) in ORGTL, active only, where … │ │       ││
│      │  │──────────────────────────────────────────────────── │ │                           ││
│      │  │ 3 · Columns  (✓Finance country)(✓Accounting currency)(✓Oracle GL company)( Operating ledger)( Reporting currency) │ ││
│      │  │──────────────────────────────────────────────────── │ │                           ││
│      │  │ [▶ Run query] [Save query…] Clear                   │ └───────────────────────────┘│
│      │  └─────────────────────────────────────────────────────┘                              │
│      │  ┌ Results card (full width) ─────────────────────────────────────────────────────┐ │
│      │  │ 3 nodes match  ORGTL · 2026-11 Working · as of 16 Nov 2026 · Live data  [⤓ Export CSV] │
│      │  │ Node | Status | Path in ORGTL | Finance country | Accounting currency | Oracle GL company | Open │
│      │  └────────────────────────────────────────────────────────────────────────────────┘ │
│      ├ .n-draftbar ……                                                                      ┤
└──────┴────────────────────────────────────────────────────────────────────────────────────┘
```
- `.n-main` = column: `.n-q-scroll` (flex 1, overflow auto) containing `.n-q-page`, then `.n-draftbar`.
- Builder and Saved queries sit in a wrapping flex row, `gap: 20px; align-items: flex-start`. Below ~980px content width the Saved queries card drops under the builder.
- Results card is full width below.

---

## 3. Components (exact)

### 3.1 Page header
- `h1.n-page__title` "Queries": 24px / 700 / letter-spacing −0.01em / line-height 1.25.
- Intro `p.n-page__intro`: "Find nodes by their properties. Results reflect the version, date and workspace above." 14px, `#5C5C5C`.
- Gap between title and intro: 4px.

### 3.2 Builder card — shell
`section.n-card` (white, 1px `#D9D9D9`, radius 16px, overflow hidden) with four stacked parts. Parts 1–3 have `border-bottom: 1px solid #ECECEC`.
Section titles (`h2.n-q-step`): 15px / 700, margin 0, format **"1 · Where to look"**, **"2 · Conditions"**, **"3 · Columns"** (middle dot U+00B7 with spaces).

### 3.3 Part 1 — Where to look
Container: padding **20px**, column gap **14px**. Controls row: flex-wrap, `gap: 12px 16px`, `align-items: flex-end`. Every control has a label above it (12px / 600, `#5C5C5C`, gap 6px).

| Control | Spec |
|---|---|
| **Hierarchy** (`select`, label "Hierarchy") | `.n-select` pill: height **40px**, min-width **210px**, border 1.5px `#BDBDBD`, radius 999px, padding `0 36px 0 14px`, 14px / 600. Chevron drawn by `.n-select::after`. Options: `ORGTL · Primary`, `ORGTP · Alternate`, `AMBUS · Alternate` (+ any other hierarchy the user can read, e.g. `E2E_H · E2E Legal Group`). |
| **Search in** (label) | Raised segmented `.n-seg`: track `#F2F2F2`, radius 999px, padding 3px, gap 2px. Buttons 34px high, padding 0 12px, 13px / 600, `#1F1F1F`; active = white background + shadow `0 1px 2px rgba(0,0,0,.14)`. Options **Whole hierarchy** / **Under a node**. |
| Node code (only when "Under a node") | Text input, width **96px**, height 40px, border 1.5px `#BDBDBD`, radius 999px, padding 0 12px, JetBrains Mono 13px / 600, `text-transform: uppercase`, placeholder "e.g. AMT", sits 8px right of the segmented control. Visually hidden label "Node code". Default value `AMT` when switched on. Invalid code (not in the hierarchy) → border `#B42318` and `.n-error` "Not found in ORGTL" below. |
| **Nodes** (label) | Raised `.n-seg` with three options, each with a marker before its text (gap 6px): **All**, **○ Leaves** (7×7 circle, 1.5px `#3D3D3D`), **□ Limbs** (8×8 square, 1.5px `#3D3D3D`). |
| **Include inactive** | `.n-toggle` (40px high, label 13px): track 32×18 radius 999px (`#BDBDBD` off / `#368727` on), knob 14px white at left 2px / 16px. Default off. |

### 3.4 Part 2 — Conditions
Container: padding **20px**, column gap **12px**.

**Header row** (flex, gap 10px, wrap): `h2` "2 · Conditions" · "Match" (13px, `#5C5C5C`) · small raised segmented (`.n-seg.n-seg--sm`, buttons 30px high, 12px / 600): **All of these** / **Any of these**.

**Empty state** (no conditions): box with border `1.5px dashed #D4D4D4`, radius 12px, padding 16px, text 13px `#5C5C5C`: "No conditions yet — every node in scope will match."

**Join word** between top-level items (from the 2nd item on): 11px / 700, letter-spacing 0.06em, `#6B6B6B`, padding-left 14px, text **AND** (All) or **OR** (Any).

**Condition row** (`.n-q-cond`): flex-wrap, align centre, gap **8px**:
1. **Property** select — `.n-select.n-select--box`: height **40px**, width **210px**, radius **10px**, border 1.5px `#BDBDBD`, padding `0 30px 0 12px`, 13px / 400. `aria-label="Property"`.
2. **Operator** select — same style, width **170px**. `aria-label="Operator"`.
3. **Value** input — height 40px, `flex: 1 1 140px`, min-width 120px, radius 10px, border 1.5px `#BDBDBD`, padding 0 12px, 13px. Placeholder from the property (§4.1). **Hidden** for operators "is empty" / "is not empty". `aria-label="Value"`.
4. **Remove** — ghost icon button 36×36 circle, colour `#5C5C5C`, × icon 16px (`M6 6l12 12M18 6 6 18`), `aria-label="Remove condition"`.

**Group box** (`.n-q-group`): background `#F7F8F6`, border 1px `#E3E6E0`, radius **12px**, padding **12px 14px**, column gap 10px.
- Header: "Group · match" (12px / 700, `#3D3D3D`) · mini segmented (track `#EAEAEA`, radius 999px, padding 3px; buttons 26px high, padding 0 10px, 12px / 600; active background white, no shadow) **All** / **Any** · spacer · text button "Remove group" (12px / 600, `#4A4A4A`, height 32px).
- Rows: a join label column (width **30px**, 11px / 700, `#6B6B6B`) showing **IF** on the first row, then **AND**/**OR** per the group's match · property select (height **38px**, width **190px**) · operator select (38px, **150px**) · value input (38px, `flex 1 1 120px`, min 100px, white background) · remove icon button 34×34 (× 15px).
- Footer: text button "+ Add to group" (13px / 600, `#2E6B21`, height 32px, left-aligned). New group rows default to *Accounting currency · is · (empty)*.
- One nesting level only (no groups inside groups).

**Add buttons** (row, gap 8px): **+ Add condition** — `.n-btn.n-btn--sm.n-btn--outline-dark` (40px, white, border 1.5px `#1F1F1F`, 13px / 600); new rows default to *Oracle GL company · is · (empty)*. **+ Add group** — `.n-btn--text` (13px / 600, `#2E6B21`); a new group starts with match **Any** and one row.

**"In plain words" box** (`.n-banner--info`): background `#F2F8EF`, radius 12px, padding 12px 14px, flex with gap 10px, `align-items: flex-start`. Search icon 16px stroke `#2E6B21` (margin-top 2px). Text 13px, colour `#1F4D16`, line-height 1.5, prefix **"In plain words:"** in 700. Updates live on every change (§4.3).

### 3.5 Part 3 — Columns
Container: padding 20px, column gap 10px. Header row (baseline, gap 8px): `h2` "3 · Columns" + caption 12px `#5C5C5C` "Code, name, type, status and path are always shown".
Chips row (wrap, gap 6px): one `.n-chip-toggle` per property column — height **34px**, padding 0 12px, radius 999px, border 1.5px, gap 6px, 13px:
- Off: border `#DCDCDC`, background white, text `#3D3D3D`.
- On: border `#368727`, background `#EEF5EB`, text `#1F4D16`, plus a 13px check icon (`m5 12 5 5 9-10`, stroke 2.6) before the label.
Order: Finance country, Accounting currency, Oracle GL company, Operating ledger, Reporting currency. Default on: the first three.

### 3.6 Part 4 — Actions footer
Padding **16px 20px**, flex-wrap, align centre, gap 8px:
- **Run query** — `.n-btn--primary` 44px high, padding `0 22px`, 14px / 600, with a 14px filled play icon (`M7 5v14l12-7z`, `fill: currentColor`).
- **Save query…** — `.n-btn--secondary` 44px.
- **Clear** — text button 44px, padding 0 10px, 14px / 600, `#4A4A4A`. Clears conditions, scope back to whole hierarchy, nodes "All", inactive off; keeps hierarchy and columns.
- **Inline save** (after "Save query…"): container `flex 1 1 280px`, gap 8px: text input (height 40px, border **2px `#368727`**, radius 999px, padding 0 14px, 13px, placeholder "Name this query", visually hidden label "Query name") + **Save** `.n-btn--dark` 40px (13px / 600). Empty name saves as "Untitled query". Enter = save, Esc = cancel.

### 3.7 Saved queries card
`aside.n-card` (`flex: 1 1 300px`).
- Header (padding 16px 20px, border-bottom 1px `#ECECEC`, baseline, gap 8px): "Saved queries" 15px / 700 + count 12px `#5C5C5C` "3 saved".
- List padding **8px**. Each item is a `button` (full width, left aligned, padding **12px**, radius **12px**, column gap 4px, colour `#1F1F1F`):
  - Line 1: name 14px / 600 (flex 1) + hierarchy code JetBrains Mono 11px `#5C5C5C` (right).
  - Line 2: summary 12px `#4A4A4A`, line-height 1.45 — the conditions in plain words, first letter capitalised (e.g. "Finance country is “UK”").
  - Line 3: meta 11px `#6B6B6B` — "Saved 12 Nov · run 6 times".
  - Hover background `#FAFAF8`; the item matching the current builder state has background `#F2F8EF`.
  - Click = load into the builder **and run** it.
- Empty: 13px `#5C5C5C`, padding 16px 12px: "No saved queries yet. Build one and choose “Save query…”."
- Row menu (stretch goal): rename, delete, share with my team.

### 3.8 Results card
`section.n-card`, full width.

**Not run yet:** centred block, padding 40px 20px, gap 6px: "Results appear here" (15px / 600) + "Set where to look and your conditions, then run the query." (13px `#5C5C5C`).

**Header** (padding 16px 20px, border-bottom `#ECECEC`, flex-wrap, gap `8px 14px`, align centre):
- `h2` 18px / 700: "**3 nodes match**" ("1 node matches", "0 nodes match").
- Context 13px `#5C5C5C`: `ORGTL · 2026-11 Working · as of 16 Nov 2026 · Live data` (taken from the run, not the live controls).
- Spacer · **Export CSV** `.n-btn--secondary` height **38px**, padding 0 14px, 13px / 600, 15px download icon.

**Stale bar** (when the builder differs from the last run): `role="status"`, background `#FFF8EE`, border-bottom 1px `#F3E2C6`, padding 10px 20px, 13px, colour `#5A3500`, gap 10px: 8×8 diamond (`#C77700`, rotated 45°) · "You changed the query. These results are from the last run." (flex 1) · **Run again** `.n-btn--dark` height 32px, padding 0 14px, 12px / 600.

**No results:** padding 32px 20px, centred, 14px `#5C5C5C`: "No nodes match. Try “Any of these”, include inactive nodes, or widen the scope."

**Table** (`.n-table-wrap` → `table.n-table`, min-width **860px**, 13px, border-collapse):
| Column | Header text | Cell |
|---|---|---|
| Node | "Node" | marker (limb 10×10 square / leaf 8×8 circle, 1.5px `#3D3D3D`) · code JetBrains Mono 13px `#2E6B21` (min-width 50px) · name **13px / 600** `#1F1F1F` (`#5C5C5C` if inactive); gap 10px |
| Status | "Status" | pill 12px / 600, padding 3px 10px, radius 999px — Active `#1F4D16` on `#E3F0DE`, Inactive `#4A4A4A` on `#ECECEC` |
| Path | "Path in ORGTL" | JetBrains Mono 12px `#4A4A4A`: last three path segments prefixed with "… / " when the path is longer than 4, then two spaces, "· L7" (level) |
| Property columns | property label, nowrap | JetBrains Mono 12px; value or "—". **Columns used in the conditions are tinted**: background `#EEF5EB`, weight 600 (`td.n-hit`) |
| Actions | visually hidden "Actions" | right-aligned link "Open" (13px / 600, `#2E6B21`) → `/hierarchies/<hier>?node=<code>` |
- Header cells: 12px / 600, `#5C5C5C`, background `#FAFAF8`, padding 10px 12px, border-bottom 1px `#ECECEC`, nowrap. First column padding-left 20px, last column padding-right 20px.
- Body cells: padding 10px 12px, border-bottom 1px `#F2F2F2`, vertical-align middle.
- Wide tables scroll horizontally inside the card (never the page).
- Sorting (stretch goal): click a header to sort; arrow 12px after the label.
- Paging: show up to 100 rows, then "Show 100 more" text button centred in the footer; CSV always exports all.

**Footer note** (padding 12px 20px, border-top 1px `#ECECEC`, 12px `#5C5C5C`): "Tinted cells are the values your conditions matched on. “Open” shows the node in Hierarchies, where you can edit it."

---

## 4. Behaviour

### 4.1 Properties (condition list and column chips)
| Key | Label | Placeholder | In columns? |
|---|---|---|---|
| `FINANCE_COUNTRY` | Finance country | e.g. UK | yes |
| `ACCOUNTING_CURRENCY` | Accounting currency | e.g. GBP | yes |
| `GL_COMPANY` | Oracle GL company | e.g. 0512 | yes |
| `LEDGER_ID` | Operating ledger | e.g. L200 | yes |
| `REPORTING_CCY` (`E2E.Currency`) | Reporting currency | e.g. USD | yes |
| `NAME` | Node name | e.g. Quant | no (always shown) |
| `CODE` | Node code | e.g. 343 | no (always shown) |
In production the list comes from `GET /api/properties?visibleTo=me` (categories the user can't see are excluded).

### 4.2 Operators
| Value | Label in select | Meaning (case-insensitive, trimmed) | Needs value |
|---|---|---|---|
| `is` | is | equals | yes |
| `not` | is not | not equal | yes |
| `contains` | contains | substring | yes |
| `starts` | starts with | prefix | yes |
| `empty` | is empty | no value | no |
| `filled` | is not empty | has a value | no |
| `above` | ancestor has | any ancestor (in this hierarchy) has the property **equal** to the value (MDC *Above*) | yes |
| `below` | descendant has | any descendant has it equal to the value (MDC *Below*) | yes |
Property values are the **effective** values (overridden, inherited through ORGTL, default or derived) as of the context date.

### 4.3 "In plain words" sentence
`Find <what> <where><inactive>[, where <conditions>].`
- what: "all nodes" | "leaves (cost centres)" | "limbs"
- where: " in ORGTL" | " under AMT in ORGTL"
- inactive: ", active only" | ", including inactive ones"
- condition: `<property label, lower case> <operator phrase> “<value>”` — phrases: is · is not · contains · starts with · is empty · is not empty · **has an ancestor where it is** · **has a descendant where it is**; missing value shows “…”.
- top-level join " and " / " or "; a group is wrapped in parentheses with its own join.
- No conditions: `Find <what> <where><inactive>.`
Example: *Find leaves (cost centres) in ORGTL, active only, where finance country is “UK”.*

### 4.4 Running
- **Run query** evaluates against the version, as-of date and workspace in the context bar; results are stored with that context (header text) and the exact query.
- Any later change to the builder (scope, conditions, match, nodes, inactive, hierarchy, columns) shows the stale bar until rerun. Changing columns only re-renders columns without staleness (stretch: treat columns as presentation).
- Changing the context bar (version/date/workspace) also marks results stale.
- Empty value on a value-operator: the condition matches nothing (no error), and the plain-words sentence shows “…”.

### 4.5 State
| State | Default |
|---|---|
| hierarchy | `ORGTL` |
| scope / root | whole hierarchy / `AMT` (used when "Under a node") |
| nodes | `leaves` |
| includeInactive | false |
| match | `all` |
| items | `[ { prop: FINANCE_COUNTRY, op: is, val: "UK" } ]` |
| columns | Finance country, Accounting currency, Oracle GL company |
| lastRun | the default query (results visible on load, as in the board) |
| saving | false |
Item shape: `{ id, kind: 'cond', prop, op, val }` or `{ id, kind: 'group', match: 'all'|'any', conds: [{ id, prop, op, val }] }`.

### 4.6 Keyboard & accessibility
- Every control is a native `select`/`input`/`button`; segmented and chip buttons use `aria-pressed`; selects and inputs without visible labels carry `aria-label`.
- Ctrl/⌘+Enter anywhere in the builder runs the query.
- Results table uses `<th scope="col">`; the stale bar and result count are announced (`role="status"`).

---

## 5. CSS to append to `src/styles.css` (after `nexus-ui.css`)

```css
/* ===== Queries ===== */
.n-q-scroll{flex:1 1 auto;min-height:0;overflow:auto}
.n-q-page{padding:24px 28px 32px;display:flex;flex-direction:column;gap:20px;max-width:1360px;box-sizing:border-box}
.n-q-head{display:flex;flex-direction:column;gap:4px}
.n-q-top{display:flex;flex-wrap:wrap;gap:20px;align-items:flex-start}
.n-q-builder{flex:999 1 640px;min-width:0}
.n-q-saved{flex:1 1 300px;min-width:0}
.n-q-part{padding:20px;display:flex;flex-direction:column;gap:12px;border-bottom:1px solid var(--n-divider)}
.n-q-part--where{gap:14px}
.n-q-part--cols{gap:10px}
.n-q-step{margin:0;font-size:15px;font-weight:700}
.n-q-controls{display:flex;flex-wrap:wrap;gap:12px 16px;align-items:flex-end}
.n-q-scope{display:flex;align-items:center;gap:8px}
.n-q-root{width:96px;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 12px;font-family:var(--n-mono);font-size:13px;font-weight:600;text-transform:uppercase;background:var(--n-surface)}
.n-q-root.n-input--error{border-color:var(--n-danger)}
.n-q-condhead{display:flex;flex-wrap:wrap;align-items:center;gap:10px}
.n-q-match-label{font-size:13px;color:var(--n-text-muted)}
.n-q-empty{border:1.5px dashed #D4D4D4;border-radius:var(--n-r-tile);padding:16px;font-size:13px;color:var(--n-text-muted)}
.n-q-join{font-size:11px;font-weight:700;letter-spacing:.06em;color:var(--n-text-faint);padding-left:14px}
.n-q-cond{display:flex;flex-wrap:wrap;align-items:center;gap:8px}
.n-q-cond .n-select--box select{height:40px;width:210px}
.n-q-cond .n-q-op select{width:170px}
.n-q-value{flex:1 1 140px;min-width:120px;height:40px;border:1.5px solid var(--n-border-input);border-radius:10px;padding:0 12px;font-size:13px;background:var(--n-surface);color:var(--n-text)}
.n-q-group{background:#F7F8F6;border:1px solid #E3E6E0;border-radius:var(--n-r-tile);padding:12px 14px;display:flex;flex-direction:column;gap:10px}
.n-q-group__head{display:flex;align-items:center;gap:8px;flex-wrap:wrap}
.n-q-group__label{font-size:12px;font-weight:700;color:var(--n-text-2)}
.n-q-miniseg{display:inline-flex;gap:2px;background:#EAEAEA;border-radius:var(--n-r-pill);padding:3px}
.n-q-miniseg button{background:transparent;border:0;border-radius:var(--n-r-pill);padding:0 10px;height:26px;font-size:12px;font-weight:600;color:var(--n-text)}
.n-q-miniseg button[aria-pressed="true"]{background:#fff}
.n-q-group__remove{background:transparent;border:0;height:32px;font-size:12px;font-weight:600;color:var(--n-text-3)}
.n-q-group .n-q-cond .n-select--box select{height:38px;width:190px;background:#fff}
.n-q-group .n-q-cond .n-q-op select{width:150px}
.n-q-group .n-q-value{height:38px;flex-basis:120px;min-width:100px}
.n-q-group .n-icon-btn--sm{width:34px;height:34px}
.n-q-rowjoin{width:30px;font-size:11px;font-weight:700;color:var(--n-text-faint)}
.n-q-addtogroup{align-self:flex-start;background:transparent;border:0;padding:0 4px;height:32px;font-size:13px;font-weight:600;color:var(--n-brand-700)}
.n-q-adds{display:flex;flex-wrap:wrap;gap:8px;align-items:center}
.n-q-plain{display:flex;gap:10px;align-items:flex-start;background:var(--n-brand-30);border-radius:var(--n-r-tile);padding:12px 14px}
.n-q-plain svg{width:16px;height:16px;flex:0 0 16px;margin-top:2px;stroke:var(--n-brand-700);stroke-width:2;fill:none;stroke-linecap:round}
.n-q-plain__text{font-size:13px;color:var(--n-brand-800);line-height:1.5}
.n-q-plain__text b{font-weight:700}
.n-q-colhead{display:flex;align-items:baseline;gap:8px}
.n-q-colhead span{font-size:12px;color:var(--n-text-muted)}
.n-q-chips{display:flex;flex-wrap:wrap;gap:6px}
.n-chip-toggle svg{width:13px;height:13px;stroke:currentColor;stroke-width:2.6;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-q-actions{padding:16px 20px;display:flex;flex-wrap:wrap;align-items:center;gap:8px}
.n-q-run{padding:0 22px}
.n-q-run svg{width:14px;height:14px;fill:currentColor;stroke:none}
.n-q-clear{height:44px;padding:0 10px;border:0;background:transparent;font-size:14px;font-weight:600;color:var(--n-text-3)}
.n-q-savebox{display:flex;align-items:center;gap:8px;flex:1 1 280px}
.n-q-savebox input{flex:1 1 auto;height:40px;border:2px solid var(--n-brand);border-radius:var(--n-r-pill);padding:0 14px;font-size:13px}
/* saved queries */
.n-q-saved__head{padding:16px 20px;border-bottom:1px solid var(--n-divider);display:flex;align-items:baseline;gap:8px}
.n-q-saved__title{margin:0;font-size:15px;font-weight:700}
.n-q-saved__count{font-size:12px;color:var(--n-text-muted)}
.n-q-saved__list{padding:8px}
.n-q-saved-item{display:flex;flex-direction:column;gap:4px;width:100%;text-align:left;background:transparent;border:0;border-radius:var(--n-r-tile);padding:12px;color:var(--n-text)}
.n-q-saved-item:hover{background:var(--n-surface-alt)}
.n-q-saved-item[aria-current="true"]{background:var(--n-brand-30)}
.n-q-saved-item__top{display:flex;align-items:center;gap:8px;width:100%}
.n-q-saved-item__name{font-size:14px;font-weight:600;flex:1 1 auto}
.n-q-saved-item__hier{font-family:var(--n-mono);font-size:11px;color:var(--n-text-muted)}
.n-q-saved-item__sum{font-size:12px;color:var(--n-text-3);line-height:1.45}
.n-q-saved-item__meta{font-size:11px;color:var(--n-text-faint)}
.n-q-saved__empty{padding:16px 12px;font-size:13px;color:var(--n-text-muted)}
/* results */
.n-q-results__head{display:flex;flex-wrap:wrap;align-items:center;gap:8px 14px;padding:16px 20px;border-bottom:1px solid var(--n-divider)}
.n-q-results__title{margin:0;font-size:18px;font-weight:700}
.n-q-results__ctx{font-size:13px;color:var(--n-text-muted)}
.n-q-results__export{height:38px;padding:0 14px;font-size:13px}
.n-q-stale{display:flex;align-items:center;gap:10px;padding:10px 20px;background:var(--n-warn-bg);border-bottom:1px solid #F3E2C6;font-size:13px;color:#5A3500}
.n-q-stale::before{content:"";width:8px;height:8px;background:var(--n-warn);transform:rotate(45deg);flex:0 0 8px}
.n-q-stale__text{flex:1 1 auto}
.n-q-noresults{padding:32px 20px;text-align:center;font-size:14px;color:var(--n-text-muted)}
.n-q-table{min-width:860px}
.n-q-node{display:flex;align-items:center;gap:10px}
.n-q-node__code{font-family:var(--n-mono);font-size:13px;color:var(--n-brand-700);min-width:50px}
.n-q-node__name{font-size:13px;font-weight:600}
.n-q-node--inactive .n-q-node__name{color:var(--n-text-muted)}
.n-q-path,.n-q-val{font-family:var(--n-mono);font-size:12px}
.n-q-path{color:var(--n-text-3)}
.n-q-status{font-size:12px;font-weight:600;border-radius:var(--n-r-pill);padding:3px 10px}
.n-q-open{text-align:right;white-space:nowrap}
.n-q-results__foot{padding:12px 20px;border-top:1px solid var(--n-divider);font-size:12px;color:var(--n-text-muted)}
```

---

## 6. HTML contract (Angular template skeleton)

```html
<div class="n-q-scroll"><div class="n-q-page">
  <div class="n-q-head">
    <h1 class="n-page__title">Queries</h1>
    <p class="n-page__intro">Find nodes by their properties. Results reflect the version, date and workspace above.</p>
  </div>

  <div class="n-q-top">
    <section class="n-card n-q-builder" aria-label="Build a query">
      <div class="n-q-part n-q-part--where">
        <h2 class="n-q-step">1 · Where to look</h2>
        <div class="n-q-controls">
          <div class="n-field"><label class="n-label" for="q-hier">Hierarchy</label>
            <span class="n-select"><select id="q-hier" style="min-width:210px">…</select></span></div>
          <div class="n-field"><span class="n-label">Search in</span>
            <div class="n-q-scope">
              <div class="n-seg" role="group" aria-label="Search scope">
                <button class="n-seg__btn" [attr.aria-pressed]="scope==='all'">Whole hierarchy</button>
                <button class="n-seg__btn" [attr.aria-pressed]="scope==='under'">Under a node</button>
              </div>
              @if (scope==='under') { <label class="n-sr-only" for="q-root">Node code</label><input id="q-root" class="n-q-root" placeholder="e.g. AMT"> }
            </div></div>
          <div class="n-field"><span class="n-label">Nodes</span>
            <div class="n-seg" role="group" aria-label="Node type">
              <button class="n-seg__btn" aria-pressed="false">All</button>
              <button class="n-seg__btn" aria-pressed="true"><span class="n-mark-leaf" style="width:7px;height:7px;border-color:#3D3D3D"></span>Leaves</button>
              <button class="n-seg__btn" aria-pressed="false"><span class="n-mark-limb" style="width:8px;height:8px"></span>Limbs</button>
            </div></div>
          <button class="n-toggle" aria-pressed="false"><span class="n-toggle__track"><span class="n-toggle__knob"></span></span>Include inactive</button>
        </div>
      </div>

      <div class="n-q-part">
        <div class="n-q-condhead"><h2 class="n-q-step">2 · Conditions</h2><span class="n-q-match-label">Match</span>
          <div class="n-seg n-seg--sm" role="group" aria-label="Match">…All of these / Any of these…</div></div>
        <!-- empty: <div class="n-q-empty">No conditions yet — every node in scope will match.</div> -->
        <!-- per item: optional <span class="n-q-join">AND</span>, then .n-q-cond or .n-q-group -->
        <div class="n-q-cond">
          <span class="n-select n-select--box"><select aria-label="Property">…</select></span>
          <span class="n-select n-select--box n-q-op"><select aria-label="Operator">…</select></span>
          <input class="n-q-value" aria-label="Value" placeholder="e.g. UK">
          <button class="n-icon-btn n-icon-btn--ghost n-icon-btn--sm" aria-label="Remove condition"><svg viewBox="0 0 24 24"><path d="M6 6l12 12M18 6 6 18"/></svg></button>
        </div>
        <div class="n-q-adds">
          <button class="n-btn n-btn--sm n-btn--outline-dark">+ Add condition</button>
          <button class="n-btn n-btn--text">+ Add group</button>
        </div>
        <div class="n-q-plain"><svg viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
          <span class="n-q-plain__text"><b>In plain words:</b> {{ sentence }}</span></div>
      </div>

      <div class="n-q-part n-q-part--cols">
        <div class="n-q-colhead"><h2 class="n-q-step">3 · Columns</h2><span>Code, name, type, status and path are always shown</span></div>
        <div class="n-q-chips">…button.n-chip-toggle[aria-pressed]…</div>
      </div>

      <div class="n-q-actions">
        <button class="n-btn n-btn--primary n-q-run"><svg viewBox="0 0 24 24"><path d="M7 5v14l12-7z"/></svg>Run query</button>
        <button class="n-btn n-btn--secondary">Save query…</button>
        <button class="n-q-clear">Clear</button>
      </div>
    </section>

    <aside class="n-card n-q-saved" aria-label="Saved queries">
      <div class="n-q-saved__head"><h2 class="n-q-saved__title">Saved queries</h2><span class="n-q-saved__count">3 saved</span></div>
      <div class="n-q-saved__list">…button.n-q-saved-item…</div>
    </aside>
  </div>

  <section class="n-card" aria-label="Results">
    <div class="n-q-results__head"><h2 class="n-q-results__title">3 nodes match</h2>
      <span class="n-q-results__ctx">ORGTL · 2026-11 Working · as of 16 Nov 2026 · Live data</span>
      <span class="n-spacer"></span>
      <button class="n-btn n-btn--secondary n-q-results__export"><svg viewBox="0 0 24 24"><path d="M12 4v11M7 10l5 5 5-5M5 20h14"/></svg>Export CSV</button></div>
    <!-- .n-q-stale when stale -->
    <div class="n-table-wrap"><table class="n-table n-q-table">…</table></div>
    <div class="n-q-results__foot">Tinted cells are the values your conditions matched on. “Open” shows the node in Hierarchies, where you can edit it.</div>
  </section>
</div></div>
<app-draft-bar />
```

---

## 7. Sample data (matches the board)

### 7.1 Nodes (ORGTL branch under AMT) — effective property values
| Code | Name | Type | Status | Country | Acct ccy | GL co. | Ledger | Rep. ccy |
|---|---|---|---|---|---|---|---|---|
| AMT | Asset Management Technology | LIMB | Active | US | USD | 0444 | L100 | USD |
| AMEQT | AM Equity Technology | LIMB | Active | US | USD | 0444 | L100 | USD |
| AMFIT | Fixed Income, Finance & Trading | LIMB | Active | US | USD | 0444 | L100 | USD |
| AMDSC | Data Services, Compliance & Operations | LIMB | Active | IN | INR | 0610 | L300 | USD |
| AMQNT | Quant Technology | LIMB | Active | US | USD | 0444 | L100 | USD |
| AGCOE | AM AI Center of Excellence | LIMB | Active | US | USD | 0444 | L100 | USD |
| AGQNT | US Quant Technology | LIMB | Active | US | USD | 0444 | L100 | USD |
| 31679 | UK QNT Tech | LEAF | Active | UK | GBP | 0512 | L200 | USD |
| 34295 | QNT Engineering | LEAF | Active | US | USD | 0444 | L100 | USD |
| 34299 | QNT Leadership | LEAF | Active | US | USD | 0444 | L100 | USD |
| 34301 | QNT Analytics Platform | LEAF | **Inactive** | US | USD | 0444 | L100 | USD |
| 34307 | QNT Quant Dev | LEAF | Active | US | USD | 0444 | L100 | USD |
| 34322 | QNT Contingent | LEAF | **Inactive** | IN | INR | 0610 | L300 | USD |
| 99301 | QNT Research | LEAF | Active | US | USD | 0444 | L100 | USD |
| 41210 | Equity Research Tools | LEAF | Active | UK | GBP | 0512 | L200 | USD |
| 41215 | Equity Data Operations | LEAF | Active | IN | INR | 0610 | L300 | USD |
| 41402 | FI Trading Applications | LEAF | Active | US | USD | 0444 | L100 | USD |
| 41408 | FI Risk Engines | LEAF | Active | UK | GBP | 0512 | L200 | USD |
| 41633 | Data Platform Engineering | LEAF | Active | IN | INR | 0610 | L300 | USD |
| 41640 | Compliance Reporting | LEAF | **Inactive** | UK | GBP | 0512 | L200 | USD |
Tree (ORGTL): ORGTL → OC005 → AM → AMT; AMT → AMEQT, AMFIT, AMDSC, AMQNT; AMEQT → 41210, 41215; AMFIT → 41402, 41408; AMDSC → 41633, 41640; AMQNT → AGCOE, AGQNT; AGCOE → 34307; AGQNT → 31679, 34295, 34299, 34301, 34322, 99301.

### 7.2 Default query result (on load)
Leaves in ORGTL, active only, Finance country is "UK" → **3 nodes**: 31679 UK QNT Tech, 41210 Equity Research Tools, 41408 FI Risk Engines (41640 excluded because inactive). Finance country column tinted.

### 7.3 Saved queries
| Name | Hierarchy | Query | Meta |
|---|---|---|---|
| UK cost centres | ORGTL | leaves, active, Finance country is “UK”; columns country/acct ccy/GL | Saved 12 Nov · run 6 times |
| Indian ledger L300 under AMT | ORGTL | all nodes under AMT, incl. inactive, Operating ledger is “L300”; columns ledger/acct ccy | Saved 10 Nov · run 2 times |
| Non-USD accounting currency | ORGTL | leaves, active, group(ANY: Accounting currency is “GBP”, Accounting currency is “INR”); columns acct ccy/country/GL | Saved 4 Nov · run 11 times |

---

## 8. API (Spring Boot)
| Method & path | Body / params | Returns |
|---|---|---|
| `GET /api/properties?visibleTo=me` | — | `[{key,label,category,placeholder}]` |
| `POST /api/queries/run` | `{ version, asOf, workspace, hierarchy, scope:{type:'all'|'under', root?}, nodes:'all'|'leaves'|'limbs', includeInactive, match:'all'|'any', items:[…], columns:[…], page, size }` | `{ total, rows:[{code,name,type,status,path:[…],level,values:{KEY:value}}], matchedColumns:[…] }` |
| `POST /api/queries/export.csv` | same body | CSV (all rows) |
| `GET /api/saved-queries` | — | `[{id,name,hierarchy,query,savedAt,runCount}]` |
| `POST /api/saved-queries` · `DELETE /api/saved-queries/{id}` | `{name, query}` | saved item |
Backend: evaluate in SQL over effective property values (view `node_property_effective(version, node, key, value, as_of)`); `above`/`below` via recursive CTE on `hierarchy_node` for the chosen hierarchy; enforce category visibility server-side.

## 9. Angular structure
```
features/queries/
  queries-page.component.ts       // state signals (§4.5), lastRun, stale = computed(JSON(query) !== JSON(lastRun.query))
  where-section.component.ts
  conditions-section.component.ts // items, groups, add/remove
  condition-row.component.ts      // property/operator/value/remove (inputs: compact for groups)
  plain-words.pipe.ts             // §4.3 sentence
  columns-section.component.ts
  saved-queries.component.ts
  results-card.component.ts       // header, stale bar, table, footer
```

## 10. Acceptance checklist
- [ ] Current shell (header `#044014`, rail with Queries active, context bar, draft bar).
- [ ] Page title/intro; builder (640 basis) and saved queries (300 basis) side by side, wrapping.
- [ ] Part 1 controls with exact sizes: hierarchy select 40×210, scope segmented + 96px mono code field, nodes segmented with markers, include-inactive toggle.
- [ ] Part 2: match toggle, AND/OR join words, condition rows (210 / 170 / flexible value / remove), value hidden for empty/not-empty, groups with IF/AND/OR labels, add buttons, live plain-words box.
- [ ] Part 3: column chips with check icon when on.
- [ ] Actions: Run (play icon), Save query… with inline name field, Clear.
- [ ] Saved queries load and run; current one highlighted.
- [ ] Results: headline, run context, Export CSV, stale bar with Run again, table with tinted matched columns, short path with level, Open links, footer note; not-run and no-results states.
- [ ] Default load shows the 3 UK cost centres.
- [ ] Pixel check vs. the board at 1440px: computed styles match §3 and §5.
