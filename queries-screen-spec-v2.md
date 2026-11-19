# Queries screen — Build Specification (features + exact styles)

> **Give this file to the LLM/developer together with `nexus-design-system.md` and a screenshot of the canvas board "Queries (new · master–detail)"** (`Queries-B`). The shell (header `#044014`, context bar, collapsible sidebar, draft bar) comes from the design system unchanged. This file defines **every feature, rule, state, string, size and colour** of the Queries page, plus a CSS block (§10) to **append verbatim** to `src/styles.css` after `nexus-ui.css`. Component `.scss` files may contain layout only.
>
> **Precedence:** this file > the screenshot > your own judgement. If something is not described here, **do not add it** (no extra buttons, filters, legends, charts, icons, tooltips, helper paragraphs, animations or colours). If a rule seems missing, leave the behaviour out and raise it as a question instead of inventing one.
>
> **Replaces** the previous Queries spec and the current `/queries` page (24px light title + intro paragraph, "1 · Where to look / 2 · Conditions / 3 · Columns" stacked card, wall of column toggle chips, green "In plain words" banner, separate "Saved queries" card, full-width results card below). The older board "Queries (unified shell)" is superseded.

---

## 0. Glossary (use these words exactly in the UI)

| Term | Meaning |
|---|---|
| Query | Hierarchy + where to look + node type + inactive flag + conditions + columns. |
| Where to look | Hierarchy, **Search in** (Whole hierarchy / Under a node), **Nodes** (All / Leaves / Limbs), **Include inactive nodes**. |
| Condition | One `property · operator · value` test. |
| Columns | Extra property columns shown in the results. Node, path and status are always shown. |
| Saved query | A named query the user saved; loading one runs it. |

Copy style: **sentence case everywhere** ("Oracle GL company", not "Oracle GL Company"; "Finance country", not "Finance Country"). Never number the sections ("1 ·"). Never show "In plain words:".

---

## 1. Purpose

Find nodes by their property values, in one hierarchy, as of the context-bar version and date:
1. **Build** the query on the left (where to look, conditions, columns).
2. **Run** it and read the results on the right, with the query restated as one plain sentence.
3. **Save** it, **load** saved ones, **export** results.

Route: `/queries` (`/queries?saved={id}` loads and runs a saved query). Sidebar item **Queries** has `aria-current="page"`.

Design rules:
- Master–detail like Worklist/Compare/As-Was: builder card 400px left, results card right.
- No intro paragraph under the title; no banners. The results header carries the context.
- Results update only on **Run query** (data sets are large); column changes apply instantly without re-running.
- Text wraps; never truncate names or values with ellipsis.

---

## 2. Domain rules

### 2.1 Data read
Results are computed on the **effective property values** of the **context-bar version**, at the context-bar **as-of date**, in the context-bar **workspace** (Live data or the selected sandbox), for the chosen hierarchy. Only property categories the user may read are offered and returned (server-enforced).

### 2.2 Where to look
- **Hierarchy**: one of the hierarchies the user can see.
- **Search in**: `Whole hierarchy` (default) or `Under a node` — then a node code is required; only **descendants** of that node are searched (the node itself is excluded). Code is upper-cased; must be an existing **limb** in that hierarchy, else the inline error (§6.1).
- **Nodes**: `All` (default) · `Leaves` (no children) · `Limbs` (can have children).
- **Include inactive nodes**: off by default (active only).

### 2.3 Conditions
- 0..20 conditions. **Match** `All of these` (AND, default) or `Any of these` (OR); the switch is shown only when there are ≥ 2 conditions. (No nested groups in v1.)
- Operators (exact labels, in this order): `is` · `is not` · `contains` · `starts with` · `is empty` · `is not empty`.
- Comparison is case-insensitive and trims the value; `is empty` = no value (null or blank).
- A condition whose operator needs a value but has an empty value is **ignored** when running (shown as `“…”` in the sentence).
- No conditions → every node in scope matches.

### 2.4 Columns
- Picked from all readable properties, grouped by category (Finance, HR, General — server order).
- Default: Finance country, Oracle accounting currency, Oracle GL company.
- Result table columns = **properties used in the last run's conditions that are not already picked** (first, in condition order) + **picked columns** (in pick order).
- Columns used by conditions are tinted (§7.4).

### 2.5 Saved queries
- Personal list (per user). Name 1–80 chars, unique per user (saving with an existing name replaces it). Empty name → `Untitled query`.
- Stored: the whole query including columns. Loading one replaces the builder and runs it.

---

## 3. Property catalogue (mock; real list from the API)

| Category | Key | Label |
|---|---|---|
| Finance | COST_CENTRE | Cost centre |
| Finance | FINANCE_COUNTRY | Finance country |
| Finance | ACCT_CCY | Oracle accounting currency |
| Finance | GL | Oracle GL company |
| Finance | LEDGER | Oracle operating ledger |
| Finance | REP_CCY | Reporting currency |
| Finance | SEGMENT | Segment |
| HR | HEADCOUNT | Headcount indicator |
| HR | HR_SOL | HR solutions indicator |
| HR | WORKDAY | Include in Workday |
| HR | HOURS | Standard weekly hours |
| HR | TKA | Time keeping access indicator |
| General | LEGACY | Department legacy code |
| General | DRM_DESC | DRM description |
| General | LEGAL | Legal form |
| General | PEND | Pending inactivation |
| General | INC_REP | Include in reporting |
| General | ORIG_DIV | Originating AM org division |

---

## 4. Layout (1440 × 1000 at design time)

```
┌ .n-header (#044014) [☰] LOGO Nexus Master Data Central …………………… DEVELOPMENT (VA E2E Version Admin One · e2e_vadmin Switch) ┐
├ .n-context  Version [2026-11 · Working ▾]  As of [..]  Work in (Live data|Sandbox) ……………………………………………… Manage versions ┤
├ rail ┬ .n-main ─────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ ⌂    │ Queries  Saved query (UK cost centres ▾) ……………………………………………………… (🔖 Save query) (⤓ Export CSV)        │
│ ⊞    │ ┌ Build query 400 ──────────────┐ ┌ Results ─────────────────────────────────────────────────────────────┐ │
│ ⌕ ■  │ │ Where to look                  │ │ 3 nodes match  ORGTL · 2026-11 Working · as of 18 Nov 2026 · Live data │ │
│ ⇄    │ │ Hierarchy                      │ │ Leaves in ORGTL, active only, where Finance country is “UK”.          │ │
│ ◷    │ │ (ORGTL · Organization – …   ▾) │ ├───────────────────────────────────────────────────────────────────────┤ │
│ ───  │ │ Search in                      │ │ Node            │ Path                    │ Status │ Finance country▒│ … │ │
│ ☰    │ │ ((Whole hierarchy) Under a node)│ │ ○ 31679 UK QNT Tech │ … / AMT / AMQNT / AGQNT · Level 7 │ Active │ UK ▒ │ Open │ │
│ ◫    │ │ Nodes  (All (○Leaves) □Limbs)   │ │ ○ 41210 Equity Research Tools │ … / AM / AMT / AMEQT · Level 6 │ …   │ │
│ ≡    │ │ (◯ ) Include inactive nodes     │ │ ○ 41408 FI Risk Engines …                                            │ │
│ ───  │ │────────────────────────────────│ │                                                                       │ │
│ ⧉    │ │ Conditions                     │ │                                                                       │ │
│ ⚙    │ │ ┌ [Finance country       ▾] ✕ ┐│ │                                                                       │ │
│      │ │ │ [is ▾] [UK              ]   ││ │                                                                       │ │
│      │ │ └─────────────────────────────┘│ │                                                                       │ │
│      │ │ (+ Add condition)              │ ├───────────────────────────────────────────────────────────────────────┤ │
│      │ │────────────────────────────────│ │ Showing 3 of 3 nodes ……………………………………… ▒ Columns your conditions use │ │
│      │ │ Columns                        │ └───────────────────────────────────────────────────────────────────────┘ │
│      │ │ [3 columns: Finance country, … ▾]│                                                                         │
│      │ │ Node, path and status are always shown.                                                                  │
│      │ │────────────────────────────────│                                                                          │
│      │ │ [▶ Run query]  Clear           │                                                                          │
│      │ └────────────────────────────────┘                                                                          │
│      ├ .n-draftbar (global, unchanged) ───────────────────────────────────────────────────────────────────────────┤
└──────┴─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

- Root `.n-app.n-app--fixed` (100vh, min-height 900px, no page scroll).
- `.n-main` column: **toolbar** → **body** (flex 1, min-height 0) → `.n-draftbar`.
- Body (`.n-qy`): flex row, `gap 16px`, `padding 16px 20px`, `align-items: stretch`.
- **Builder card**: `width 400px; flex 0 0 400px`; content scrolls; **Run bar pinned** at the bottom of the card.
- **Results card**: `flex 1 1 auto; min-width 0`; header fixed; table scrolls both ways inside the card; footer fixed.
- Responsive: content width < 1100px → builder `width 360px; flex-basis 360px`. (< 900px viewport: out of scope.)

---

## 5. Toolbar

Container: `flex 0 0 auto; display flex; flex-wrap wrap; align-items center; gap 12px 20px; padding 16px 20px 0`.

Left → right:
1. `h1` **Queries** — 20px / 700, letter-spacing −0.01em, margin 0.
2. **Saved query**: visible label `Saved query` (12px / 600, `#5C5C5C`, gap 8px) + pill select (height 40px, **min-width 260px**, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 36px 0 14px`, 13px / 600, chevron 12×12 stroke 2.4 at `right 14px; top 14px`). Options: **`New query`** (value empty) first, then saved queries newest first. Shows the loaded saved query's name; shows `New query` after **Clear** or when nothing is loaded. Choosing a saved query loads it into the builder **and runs it**; choosing `New query` changes nothing in the builder.
3. Spacer.
4. **Save query** — secondary pill: inline-flex, `gap 8px`, height 40px, `padding 0 16px`, radius 999px, `border 1.5px solid #BDBDBD`, white, 13px / 600; bookmark icon 16×16 stroke 2 path `M6 3h12v18l-6-4-6 4z`. `aria-expanded` reflects the popover.
   - **Save popover** (below the button, right-aligned: `position:absolute; right 0; top 48px; width 320px`; white; `border 1px solid #D9D9D9`; radius 12px; shadow `0 12px 32px rgba(0,0,0,.14)`; `padding 14px`; column `gap 10px`; z-index 30):
     - Label **Name this query** (13px / 600) + text input (height 40px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 14px`, 13px, placeholder `e.g. UK cost centres`; autofocus; prefilled with the loaded saved query's name, else empty).
     - Row right-aligned `gap 8px`: **Cancel** (36px, `padding 0 14px`, transparent, 13px / 600) · **Save** (36px, `padding 0 16px`, radius 999px, bg `#368727`, white, 13px / 600).
     - Enter = Save, Esc = Cancel, click outside = Cancel.
     - After save: popover closes, the select shows the new name, toast **`Saved “{name}”`**.
5. **Export CSV** — secondary pill like Save query; download icon 16×16 stroke 2 (`M12 4v11` `m7 10 5 5 5-5` `M5 20h14`). Disabled (`opacity .5`, `disabled`) when the last run returned 0 rows or nothing has run. Exports **all** rows of the last run with the current table columns; toast **`Exported {n} nodes as CSV`** (`1 node`).

Toast (all on this page): `role="status"`, fixed, `bottom 84px`, centred, bg `#1F1F1F`, white 13px, `padding 10px 18px`, radius 999px, z-index 40, auto-hide 2.5 s.

---

## 6. Builder card (left) — `section[aria-label="Build query"]`

Card: white, `border 1px solid #D9D9D9`, radius 16px, column, overflow hidden. Scroll area `flex 1; min-height 0; overflow auto` holding three sections; then the pinned Run bar.

Section shell: `padding 18px 18px 16px; display flex; flex-direction column; border-bottom 1px solid #ECECEC` (the last section: `padding 18px`, no border). Section heading `h2`: 15px / 700, margin 0.
Field label (above a control): 12px / 600, `#5C5C5C`; label→control gap 6px.

**Segmented control** (used 3×): container inline-flex, `gap 2px`, `padding 3px`, `border 1.5px solid #D9D9D9`, radius 999px, white. Buttons `aria-pressed`: height **32px** (Match: 28px, font 12px), `padding 0 14px` (Match: `0 12px`), radius 999px, border 0, 13px / 600, nowrap. Selected: bg `#1F1F1F`, text white. Unselected: transparent, text `#1F1F1F`; hover `#F2F2F2`.

### 6.1 Where to look (section gap 14px)
- `h2` **Where to look**.
- **Hierarchy**: label + full-width pill select (height 40px, same pill style as toolbar, `width 100%`). Options `{CODE} · {Name}` (same order as the Hierarchies screen).
- **Search in**: label (id-linked to the group) + row (`gap 8px`, wraps): segmented `Whole hierarchy` · `Under a node`; when **Under a node**: node-code input (width 120px, height 40px, `border 1.5px solid #BDBDBD` — error `#B42318`, radius 999px, `padding 0 14px`, JetBrains Mono 13px, placeholder `Node code`, visually hidden label `Node code`, upper-cases on input).
  - Error line under the row (12px / 600, `#B42318`, `aria-live="polite"`): empty → **`Enter the code of a limb, e.g. AMT.`**; unknown/not a limb → **`No node with code “{CODE}” in {HIER}.`** Run is blocked while the error shows (Run does nothing; focus moves to the input).
- **Nodes**: label + segmented `All` · `Leaves` · `Limbs` (left-aligned, not full width). `Leaves` has a leading 8×8 circle, `Limbs` a leading 9×9 square, both `border 1.5px solid currentColor`, gap 6px.
- **Include inactive nodes** — switch: `button[role=switch][aria-checked]`, inline-flex, `gap 10px`, transparent, `padding 4px 0`, 13px `#1F1F1F`. Track 36×20, radius 999px, bg on `#368727` / off `#BDBDBD`; knob 16×16 white circle, shadow `0 1px 2px rgba(0,0,0,.2)`, `top 2px`, `left` 18px on / 2px off; 120 ms transition.

### 6.2 Conditions (section gap 12px)
- Head row (flex, centred, `gap 10px`): `h2` **Conditions** · spacer · **Match** segmented (`aria-label="Match"`): `All of these` · `Any of these` — **only when ≥ 2 conditions**.
- No conditions: one line **`No conditions yet — every node in scope matches.`** (13px, `#5C5C5C`).
- Between conditions: join word **AND** / **OR** (11px / 700, letter-spacing .06em, `#5C5C5C`, `padding-left 4px`).
- **Condition card**: `border 1px solid #E6E6E6; border-radius 12px; padding 10px; background #FAFAF8;` column `gap 8px`:
  - Row 1 (`gap 8px`): **Property** select (flex 1, height 36px, `border 1.5px solid #BDBDBD`, radius **10px**, `padding 0 32px 0 12px`, 13px / 600, white; chevron at `right 12px; top 12px`; `aria-label="Property"`; options = readable properties, grouped with `<optgroup label="Finance|HR|General">`) · **Remove** icon button (32×32 circle, transparent, X icon 16×16 stroke 2 `M6 6l12 12M18 6 6 18`, colour `#5C5C5C`, hover bg `#F2F2F2`, `aria-label`/`title` "Remove condition").
  - Row 2 (`gap 8px`, `padding-right 40px` so it aligns under the select): **Operator** select (`flex 0 0 132px`, height 36px, radius 10px, `padding 0 30px 0 12px`, 13px / 400; `aria-label="Operator"`) · **Value** input (flex 1, height 36px, `border 1.5px solid #BDBDBD`, radius 10px, `padding 0 12px`, 13px, placeholder `Value`, `aria-label="Value"`) — **hidden** for `is empty` / `is not empty`.
- **Add condition** — pill button (height 36px, `padding 0 14px`, radius 999px, `border 1.5px solid #BDBDBD`, white, 13px / 600, left-aligned) with a plus icon 14×14 stroke 2.4 (`M12 5v14M5 12h14`). Adds `Finance country · is · (empty)` and focuses its value input. Hidden at 20 conditions.

### 6.3 Columns (section gap 10px)
- `h2` **Columns**.
- **Picker field** (`button[aria-haspopup="listbox"][aria-expanded]`): flex, `gap 8px`, width 100%, `min-height 40px`, `border 1.5px solid` (`#BDBDBD`; open `#368727`), radius **12px**, `padding 6px 36px 6px 14px`, white, 13px, left-aligned, wraps. Text: **`{n} columns:`** (600) + space + labels joined `, ` (`#5C5C5C`); `1 column:`; none → **`No extra columns`**. Chevron 12×12 at `right 14px; top 14px` — `m6 9 6 6 6-6` closed, `m6 15 6-6 6 6` open.
- **Panel** (opens **in flow** directly below the field, pushing content down — not a floating overlay): `margin-top 8px`, white, `border 1px solid #D9D9D9`, radius 12px, shadow `0 1px 2px rgba(0,0,0,.10)`, column, `max-height 380px`.
  - Search: `padding 10px; border-bottom 1px solid #ECECEC`; input type search, width 100%, height 36px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 14px`, 13px, placeholder **`Find a column`** (hidden label same). Filters options by label (contains, case-insensitive); groups with no match are hidden.
  - List `role="listbox" aria-multiselectable="true" aria-label="Columns"`: `flex 1; overflow auto; padding 4px 6px 8px`.
    - Group heading: `padding 10px 8px 4px`, 11px / 700, letter-spacing .06em, `#5C5C5C`, upper case (`FINANCE`, `HR`, `GENERAL`).
    - Option `button[role=option][aria-selected]`: flex, `gap 10px`, width 100%, `min-height 36px`, `padding 6px 8px`, radius 8px, transparent (hover `#F2F2F2`), 13px `#1F1F1F`. Checkbox visual 16×16, radius 4px, `border 1.5px solid` — checked: border+bg `#368727` with a white check 11×11 stroke 3.4 (`m5 12 5 5 9-10`); unchecked: border `#BDBDBD`, white. Click toggles. Space/Enter toggle when focused; Up/Down move.
  - Footer: `padding 8px 10px; border-top 1px solid #ECECEC;` flex `gap 8px`: **Clear all** (32px, `padding 0 10px`, transparent, 13px / 600, `#4A4A4A`) · spacer · **Done** (32px, `padding 0 16px`, radius 999px, bg `#1F1F1F`, white, 13px / 600) closes the panel. Esc closes; click outside does **not** close (it's in flow).
- Caption under the field (12px, `#5C5C5C`): **`Node, path and status are always shown.`**
- Column changes update the results table immediately (no re-run, no stale prompt).

### 6.4 Run bar (pinned)
`flex 0 0 auto; border-top 1px solid #E6E6E6; padding 12px 18px;` flex, centred, `gap 8px`:
- **Run query** — primary: inline-flex, `gap 8px`, height 44px, `padding 0 22px`, radius 999px, border 0, bg `#368727` (hover `#2E6B21`), white 14px / 600; filled play icon 14×14 (`M7 5v14l12-7z`, `fill currentColor`). Ctrl/Cmd+Enter anywhere in the builder = Run.
- **Clear** — text button: height 44px, `padding 0 12px`, transparent, 14px / 600, `#4A4A4A`. Resets: conditions removed, Search in = Whole hierarchy, Nodes = All, inactive off, Match = all; **keeps** hierarchy and columns; Saved query select → `New query`. Does not re-run (results become stale).

---

## 7. Results card (right) — `section[aria-label="Results"]`

Card: white, `border 1px solid #D9D9D9`, radius 16px, column, overflow hidden.

### 7.1 Not run yet (first visit with nothing loaded)
Centred (`margin auto; padding 40px; gap 6px; text-align center`): **`Results appear here`** (16px / 600) · **`Set where to look and your conditions, then run the query.`** (13px, `#5C5C5C`).

### 7.2 Header (after a run)
`padding 18px 20px 14px; border-bottom 1px solid #ECECEC;` column `gap 6px`:
- Row (flex, wrap, centred, `gap 10px`):
  - `h2` **`{n} nodes match`** / **`1 node matches`** / **`0 nodes match`** — 18px / 700.
  - Context (12px, `#5C5C5C`): **`{HIER} · {version} {Working|Finalized} · as of {d Mon yyyy} · {Live data|Sandbox {name}}`** — the values **used by the last run**.
  - Spacer.
  - **Stale prompt** (only when the builder differs from the last run, ignoring columns): pill inline-flex `gap 8px`, 12px / 600, `#7A4100` on `#FFF1DE`, radius 999px, `padding 3px 4px 3px 12px`: text **`Query changed`** + button **`Run again`** (height 26px, `padding 0 10px`, radius 999px, white bg, `#7A4100`, 12px / 700). Also shown when the context bar's version/date/workspace changes after a run.
- **Sentence** (14px, `#3D3D3D`, line-height 1.5) — the last run restated (§7.5).

### 7.3 No matches
Below the header, centred (`margin auto; padding 40px; gap 6px`): **`No nodes match`** (16px / 600) · **`Try removing a condition or including inactive nodes.`** (13px `#5C5C5C`). No footer.

### 7.4 Table
Scroll box `flex 1; min-height 0; overflow auto` (both directions). `<table>` width 100%, `border-collapse: separate; border-spacing 0`, 13px.
- **Header cells** `th[scope=col]`: sticky `top 0`, z-index 1, left-aligned, `padding 10px 12px` (first: `10px 12px 10px 20px`; last: `10px 20px 10px 12px`), bg `#FAFAF8`, `border-bottom 1px solid #ECECEC`, 12px / 600, `#5C5C5C`, nowrap.
- Columns in order:
  1. **Node** (`min-width 220px`): icon slot 16×20 (limb = 10×10 square `border 1.5px solid #3D3D3D`; leaf = 8×8 circle `border 1.5px solid #6B6B6B`) + code (JetBrains Mono 12px, `#2E6B21`, `margin-right 8px`) + name (14px / 600); line-height 1.45; wraps.
  2. **Path** (`min-width 180px`): JetBrains Mono 12px, `#5C5C5C`, line-height 1.5 — the ancestors from the root, **last 3 only** prefixed `… / ` when deeper (e.g. `… / AMT / AMQNT / AGQNT`), full path when ≤ 3; then ` · Level {n}` in Inter 12px `#6B6B6B` (root = level 1). Full path in the cell's `title`.
  3. **Status**: `Active` (`#1F1F1F`) / `Inactive` (`#6B6B6B`), nowrap.
  4. **Property columns** per §2.4 (`min-width 120px`). Header of a condition column: bg **`#EEF5EB`**, text **`#1F4D16`**. Its cells: bg **`#F4FAF1`**, weight 600. Other cells weight 400. Empty value → `—` in `#9E9E9E`.
  5. **Open** (header text visually hidden "Open"): link **Open** (13px / 600) → `/hierarchies/{HIER}?node={code}`; nowrap.
- Body cells: `padding 12px` (first `12px 12px 12px 20px`, last `12px 20px 12px 12px`), `border-bottom 1px solid #F2F2F2`, `vertical-align top`. Row hover bg `#FAFAF8`.
- Sort: by path (depth-first tree order). Column headers are not sortable in v1.
- Paging: 50 rows per page, server-side; more than 50 → infinite scroll loads the next 50 when the last row is visible.

### 7.5 Sentence (exact grammar)
`{Nodes} in {HIER}[ under {ROOT}], {inactive part}[, where {conditions}].`
- `{Nodes}`: `All nodes` · `Leaves` · `Limbs`.
- `{inactive part}`: `active only` · `including inactive`.
- `{conditions}`: each `{Property label} {operator label}[ “{value}”]` (no value for is empty / is not empty; empty value → `“…”`), joined with ` and ` (All) or ` or ` (Any).
- Examples: `Leaves in ORGTL, active only, where Finance country is “UK”.` · `All nodes in ORGTL under AMT, including inactive, where Oracle operating ledger is “L300”.` · `Limbs in ORGTP, active only.`

### 7.6 Footer
`flex 0 0 auto; border-top 1px solid #ECECEC; padding 10px 20px;` flex, centred, `gap 8px`, 12px, `#5C5C5C`: **`Showing {shown} of {total} nodes`** (`1 node`) · spacer · key: 12×12 swatch (radius 3px, bg `#EEF5EB`, `border 1px solid #B9D9AE`) + **`Columns your conditions use`** (only when the run had conditions).

---

## 8. Behaviour

### 8.1 State
- Builder state = `{hier, scope, root, nodes, inactive, match, conds[], cols[]}`.
- `lastRun` = snapshot of the builder (without `cols`) + the context values used.
- **Stale** = `JSON(builder without cols) !== JSON(lastRun without cols)` or context changed.
- URL: `/queries?saved={id}` when a saved query is loaded; otherwise `/queries`. Builder state is kept in session storage per tab so a refresh restores it.

### 8.2 Defaults (first visit)
Load the user's most recently used saved query and run it; if none: Hierarchy = last used (`nexus.lastHierarchy`) or the first; Whole hierarchy; All; active only; no conditions; default columns; **not run** (§7.1).

### 8.3 Loading & errors
- Run: keep the old results visible with `opacity .5` and the header title replaced by **`Running…`**; if > 150 ms, show 6 skeleton rows instead (bars 12px tall, widths 60/40/20/30%, bg `#F2F2F2`, radius 6px). A new run cancels the previous one.
- Run error: results area centred **`Couldn't run this query. Try again`** (13px, "Try again" as a link button) + reference id in JetBrains Mono 12px.
- Saved list load error: select shows only `New query`; no message.

### 8.4 Keyboard
Ctrl/Cmd+Enter = Run. Esc closes the save popover / column panel. All controls reachable by Tab in visual order.

---

## 9. Data & API (Spring Boot)

```
GET    /api/properties?visibleTo=me              → [{ key, label, category, categoryLabel, order }]
GET    /api/hierarchies?version={code}           → [{ code, name }]
POST   /api/queries/run                          → QueryResult
POST   /api/queries/export.csv                   → text/csv (all rows; same body; columns = table columns)
GET    /api/saved-queries                        → [{ id, name, query, savedAt, lastRunAt }]   (newest first)
POST   /api/saved-queries        {name, query}   → saved item (replaces same name)
DELETE /api/saved-queries/{id}                   → 204   (no UI in v1)
```

```ts
interface QueryBody {
  version: string; asOf: string; workspace: { type: 'LIVE' } | { type: 'SANDBOX', id: string };
  hierarchy: string;
  scope: { type: 'ALL' } | { type: 'UNDER', root: string };
  nodes: 'ALL' | 'LEAVES' | 'LIMBS';
  includeInactive: boolean;
  match: 'ALL' | 'ANY';
  conditions: { property: string; op: 'IS'|'IS_NOT'|'CONTAINS'|'STARTS_WITH'|'IS_EMPTY'|'IS_NOT_EMPTY'; value?: string }[];
  columns: string[];                 // table property columns, in display order
  page: number; size: number;        // size 50
}
interface QueryResult {
  total: number;
  rows: { code: string; name: string; nodeType: 'LIMB'|'LEAF'; status: 'Active'|'Inactive';
          path: string[];            // ancestors root → parent
          level: number; values: Record<string, string | null> }[];
}
```
Errors: `400 ROOT_NOT_FOUND {root}` (shown as the §6.1 error) · `400 ROOT_NOT_LIMB` (same message) · `500 {traceId}`.
Server notes: evaluate in SQL over effective property values (`node_property_effective(version, node, key, value, as_of)`); descendants via recursive CTE on the hierarchy's relationships; drop conditions on properties the user can't read; enforce category visibility on returned `values`.

---

## 10. CSS to append to `src/styles.css` (after `nexus-ui.css`)

```css
/* ===== Queries ===== */
.n-qy-toolbar{flex:0 0 auto;display:flex;flex-wrap:wrap;align-items:center;gap:12px 20px;padding:16px 20px 0}
.n-qy-toolbar h1{margin:0;font-size:20px;font-weight:700;letter-spacing:-.01em}
.n-qy-field{display:flex;align-items:center;gap:8px}
.n-qy-label{font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-qy-select{position:relative}
.n-qy-select select{appearance:none;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 36px 0 14px;font-size:13px;font-weight:600;color:var(--n-text);background:var(--n-surface);cursor:pointer}
.n-qy-select--saved select{min-width:260px}
.n-qy-select--block select{width:100%}
.n-qy-select svg{position:absolute;right:14px;top:14px;width:12px;height:12px;stroke:var(--n-text);stroke-width:2.4;fill:none;stroke-linecap:round;stroke-linejoin:round;pointer-events:none}
.n-qy-pillbtn{display:inline-flex;align-items:center;gap:8px;height:40px;padding:0 16px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);font-size:13px;font-weight:600;color:var(--n-text);white-space:nowrap}
.n-qy-pillbtn:hover{background:var(--n-surface-sunk)}
.n-qy-pillbtn:disabled{opacity:.5}
.n-qy-pillbtn svg{width:16px;height:16px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-qy-savepop{position:absolute;right:0;top:48px;width:320px;background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-tile);box-shadow:var(--n-shadow-overlay);padding:14px;display:flex;flex-direction:column;gap:10px;z-index:30}
.n-qy-savepop label{font-size:13px;font-weight:600}
.n-qy-savepop input{height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 14px;font-size:13px}
.n-qy-savepop__actions{display:flex;justify-content:flex-end;gap:8px}
.n-qy-savepop__cancel{height:36px;padding:0 14px;border:0;background:transparent;font-size:13px;font-weight:600}
.n-qy-savepop__save{height:36px;padding:0 16px;border-radius:var(--n-r-pill);border:0;background:var(--n-brand);color:#fff;font-size:13px;font-weight:600}
/* body */
.n-qy{flex:1 1 auto;min-height:0;display:flex;gap:16px;padding:16px 20px;align-items:stretch}
.n-qy-card{background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);display:flex;flex-direction:column;overflow:hidden;min-width:0}
.n-qy-builder{width:400px;flex:0 0 400px}
.n-qy-results{flex:1 1 auto}
.n-qy-builder__scroll{flex:1 1 auto;min-height:0;overflow:auto}
.n-qy-sec{padding:18px 18px 16px;display:flex;flex-direction:column;gap:14px;border-bottom:1px solid var(--n-divider)}
.n-qy-sec--conds{gap:12px}
.n-qy-sec--cols{padding:18px;gap:10px;border-bottom:0}
.n-qy-sec h2{margin:0;font-size:15px;font-weight:700}
.n-qy-stack{display:flex;flex-direction:column;gap:6px}
.n-qy-seg{display:inline-flex;align-self:flex-start;gap:2px;padding:3px;border:1.5px solid var(--n-border);border-radius:var(--n-r-pill);background:var(--n-surface)}
.n-qy-seg button{display:inline-flex;align-items:center;gap:6px;height:32px;padding:0 14px;border-radius:var(--n-r-pill);border:0;background:transparent;color:var(--n-text);font-size:13px;font-weight:600;white-space:nowrap}
.n-qy-seg button:hover{background:var(--n-surface-sunk)}
.n-qy-seg button[aria-pressed="true"]{background:var(--n-text);color:#fff}
.n-qy-seg--sm button{height:28px;padding:0 12px;font-size:12px}
.n-qy-mark-leaf{width:8px;height:8px;border-radius:50%;border:1.5px solid currentColor}
.n-qy-mark-limb{width:9px;height:9px;border:1.5px solid currentColor}
.n-qy-root{width:120px;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 14px;font-family:var(--n-mono);font-size:13px;background:var(--n-surface)}
.n-qy-root[aria-invalid="true"]{border-color:var(--n-danger)}
.n-qy-err{font-size:12px;font-weight:600;color:var(--n-danger)}
.n-qy-switch{display:inline-flex;align-items:center;gap:10px;align-self:flex-start;background:transparent;border:0;padding:4px 0;font-size:13px;color:var(--n-text)}
.n-qy-switch__track{width:36px;height:20px;flex:0 0 36px;border-radius:var(--n-r-pill);background:var(--n-border-input);position:relative;transition:background .12s ease}
.n-qy-switch__knob{position:absolute;top:2px;left:2px;width:16px;height:16px;border-radius:50%;background:#fff;box-shadow:0 1px 2px rgba(0,0,0,.2);transition:left .12s ease}
.n-qy-switch[aria-checked="true"] .n-qy-switch__track{background:var(--n-brand)}
.n-qy-switch[aria-checked="true"] .n-qy-switch__knob{left:18px}
.n-qy-condhead{display:flex;align-items:center;gap:10px;flex-wrap:wrap}
.n-qy-condhead h2{flex:1 1 auto}
.n-qy-hint{font-size:13px;color:var(--n-text-muted)}
.n-qy-join{font-size:11px;font-weight:700;letter-spacing:.06em;color:var(--n-text-muted);padding-left:4px}
.n-qy-cond{border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);padding:10px;display:flex;flex-direction:column;gap:8px;background:var(--n-surface-alt)}
.n-qy-cond__row{display:flex;gap:8px;align-items:center}
.n-qy-cond__row--b{padding-right:40px}
.n-qy-cond select,.n-qy-cond input{height:36px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-row);font-size:13px;color:var(--n-text);background:var(--n-surface)}
.n-qy-cond .n-qy-prop{flex:1 1 auto;min-width:0;position:relative}
.n-qy-cond .n-qy-prop select{width:100%;appearance:none;padding:0 32px 0 12px;font-weight:600}
.n-qy-cond .n-qy-op{flex:0 0 132px;position:relative}
.n-qy-cond .n-qy-op select{width:100%;appearance:none;padding:0 30px 0 12px}
.n-qy-cond .n-qy-prop svg,.n-qy-cond .n-qy-op svg{position:absolute;right:12px;top:12px;width:12px;height:12px;stroke:var(--n-text);stroke-width:2.4;fill:none;pointer-events:none}
.n-qy-cond input{flex:1 1 auto;min-width:0;padding:0 12px}
.n-qy-x{width:32px;height:32px;flex:0 0 32px;border-radius:50%;border:0;background:transparent;display:flex;align-items:center;justify-content:center;color:var(--n-text-muted);padding:0}
.n-qy-x:hover{background:var(--n-surface-sunk)}
.n-qy-x svg{width:16px;height:16px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round}
.n-qy-add{align-self:flex-start;display:inline-flex;align-items:center;gap:6px;height:36px;padding:0 14px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);font-size:13px;font-weight:600;color:var(--n-text)}
.n-qy-add svg{width:14px;height:14px;stroke:currentColor;stroke-width:2.4;fill:none;stroke-linecap:round}
.n-qy-picker{position:relative;display:flex;align-items:center;gap:8px;width:100%;min-height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-tile);padding:6px 36px 6px 14px;background:var(--n-surface);font-size:13px;color:var(--n-text);text-align:left}
.n-qy-picker[aria-expanded="true"]{border-color:var(--n-brand)}
.n-qy-picker__text{flex:1 1 auto;min-width:0;line-height:1.4}
.n-qy-picker__n{font-weight:600}
.n-qy-picker__list{color:var(--n-text-muted)}
.n-qy-picker svg{position:absolute;right:14px;top:14px;width:12px;height:12px;stroke:var(--n-text);stroke-width:2.4;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-qy-panel{margin-top:8px;background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-tile);box-shadow:0 1px 2px rgba(0,0,0,.10);display:flex;flex-direction:column;max-height:380px}
.n-qy-panel__search{padding:10px;border-bottom:1px solid var(--n-divider)}
.n-qy-panel__search input{width:100%;height:36px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 14px;font-size:13px}
.n-qy-panel__list{flex:1 1 auto;min-height:0;overflow:auto;padding:4px 6px 8px}
.n-qy-panel__group{padding:10px 8px 4px;font-size:11px;font-weight:700;letter-spacing:.06em;color:var(--n-text-muted);text-transform:uppercase}
.n-qy-opt{display:flex;align-items:center;gap:10px;width:100%;min-height:36px;padding:6px 8px;border:0;border-radius:var(--n-r-tree);background:transparent;font-size:13px;color:var(--n-text);text-align:left}
.n-qy-opt:hover{background:var(--n-surface-sunk)}
.n-qy-check{width:16px;height:16px;flex:0 0 16px;border-radius:4px;border:1.5px solid var(--n-border-input);background:#fff;display:flex;align-items:center;justify-content:center}
.n-qy-check svg{width:11px;height:11px;stroke:#fff;stroke-width:3.4;fill:none;stroke-linecap:round;stroke-linejoin:round;visibility:hidden}
.n-qy-opt[aria-selected="true"] .n-qy-check{border-color:var(--n-brand);background:var(--n-brand)}
.n-qy-opt[aria-selected="true"] .n-qy-check svg{visibility:visible}
.n-qy-panel__foot{padding:8px 10px;border-top:1px solid var(--n-divider);display:flex;align-items:center;gap:8px}
.n-qy-panel__clear{height:32px;padding:0 10px;border:0;background:transparent;font-size:13px;font-weight:600;color:var(--n-text-3)}
.n-qy-panel__done{height:32px;padding:0 16px;border-radius:var(--n-r-pill);border:0;background:var(--n-text);color:#fff;font-size:13px;font-weight:600}
.n-qy-caption{font-size:12px;color:var(--n-text-muted)}
.n-qy-runbar{flex:0 0 auto;border-top:1px solid var(--n-divider-2);padding:12px 18px;display:flex;align-items:center;gap:8px}
.n-qy-run{display:inline-flex;align-items:center;gap:8px;height:44px;padding:0 22px;border-radius:var(--n-r-pill);border:0;background:var(--n-brand);color:#fff;font-size:14px;font-weight:600}
.n-qy-run:hover{background:var(--n-brand-700)}
.n-qy-run svg{width:14px;height:14px;fill:currentColor}
.n-qy-clear{height:44px;padding:0 12px;border:0;background:transparent;font-size:14px;font-weight:600;color:var(--n-text-3)}
/* results */
.n-qy-blank{margin:auto;display:flex;flex-direction:column;align-items:center;gap:6px;padding:40px;text-align:center}
.n-qy-blank strong{font-size:16px;font-weight:600}
.n-qy-blank span{font-size:13px;color:var(--n-text-muted)}
.n-qy-reshead{padding:18px 20px 14px;border-bottom:1px solid var(--n-divider);display:flex;flex-direction:column;gap:6px}
.n-qy-reshead__row{display:flex;align-items:center;gap:10px;flex-wrap:wrap}
.n-qy-reshead h2{margin:0;font-size:18px;font-weight:700}
.n-qy-ctx{font-size:12px;color:var(--n-text-muted);flex:1 1 auto}
.n-qy-stale{display:inline-flex;align-items:center;gap:8px;font-size:12px;font-weight:600;color:var(--n-status);background:var(--n-status-bg);border-radius:var(--n-r-pill);padding:3px 4px 3px 12px}
.n-qy-stale button{height:26px;padding:0 10px;border-radius:var(--n-r-pill);border:0;background:#fff;color:var(--n-status);font-size:12px;font-weight:700}
.n-qy-sentence{font-size:14px;color:var(--n-text-2);line-height:1.5}
.n-qy-tablebox{flex:1 1 auto;min-height:0;overflow:auto}
.n-qy-table{width:100%;border-collapse:separate;border-spacing:0;font-size:13px}
.n-qy-table th{position:sticky;top:0;z-index:1;text-align:left;padding:10px 12px;background:var(--n-surface-alt);border-bottom:1px solid var(--n-divider);font-size:12px;font-weight:600;color:var(--n-text-muted);white-space:nowrap}
.n-qy-table td{padding:12px;border-bottom:1px solid var(--n-surface-sunk);vertical-align:top}
.n-qy-table th:first-child,.n-qy-table td:first-child{padding-left:20px}
.n-qy-table th:last-child,.n-qy-table td:last-child{padding-right:20px}
.n-qy-table tbody tr:hover td{background:var(--n-surface-alt)}
.n-qy-th--node{min-width:220px}.n-qy-th--path{min-width:180px}.n-qy-th--prop{min-width:120px}
.n-qy-th--match{background:var(--n-brand-40) !important;color:var(--n-brand-800) !important}
.n-qy-td--match{background:var(--n-brand-20);font-weight:600}
.n-qy-td--empty{color:var(--n-separator)}
.n-qy-node{display:flex;align-items:flex-start;gap:8px}
.n-qy-node__icon{width:16px;flex:0 0 16px;height:20px;display:flex;align-items:center;justify-content:center}
.n-qy-limb{width:10px;height:10px;border:1.5px solid var(--n-text-2)}
.n-qy-leaf{width:8px;height:8px;border-radius:50%;border:1.5px solid var(--n-text-faint)}
.n-qy-node__text{line-height:1.45}
.n-qy-code{font-family:var(--n-mono);font-size:12px;color:var(--n-brand-700);margin-right:8px}
.n-qy-name{font-size:14px;font-weight:600}
.n-qy-path{font-family:var(--n-mono);font-size:12px;color:var(--n-text-muted);line-height:1.5}
.n-qy-level{font-family:var(--n-font);color:var(--n-text-faint)}
.n-qy-inactive{color:var(--n-text-faint)}
.n-qy-open{font-size:13px;white-space:nowrap}
.n-qy-foot{flex:0 0 auto;border-top:1px solid var(--n-divider);padding:10px 20px;display:flex;align-items:center;gap:8px;font-size:12px;color:var(--n-text-muted)}
.n-qy-key{display:inline-flex;align-items:center;gap:6px;margin-left:auto}
.n-qy-key i{width:12px;height:12px;border-radius:3px;background:var(--n-brand-40);border:1px solid var(--n-brand-border)}
.n-qy-toast{position:fixed;bottom:84px;left:50%;transform:translateX(-50%);background:var(--n-text);color:#fff;border-radius:var(--n-r-pill);padding:10px 18px;font-size:13px;z-index:40}
@media (max-width:1240px){.n-qy-builder{width:360px;flex-basis:360px}}
```

---

## 11. HTML contract (Angular template skeleton)

```html
<div class="n-qy-toolbar">
  <h1>Queries</h1>
  <div class="n-qy-field"><label class="n-qy-label" for="q-saved">Saved query</label>
    <span class="n-qy-select n-qy-select--saved"><select id="q-saved" [formControl]="savedSel">
      <option value="">New query</option><option *ngFor="let s of saved" [value]="s.id">{{s.name}}</option></select>
      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg></span></div>
  <span class="n-spacer"></span>
  <span style="position:relative">
    <button type="button" class="n-qy-pillbtn" [attr.aria-expanded]="saveOpen" (click)="toggleSave()"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M6 3h12v18l-6-4-6 4z"/></svg>Save query</button>
    <div *ngIf="saveOpen" class="n-qy-savepop" role="dialog" aria-label="Save query">
      <label for="q-name">Name this query</label>
      <input id="q-name" type="text" placeholder="e.g. UK cost centres" [formControl]="saveName" cdkFocusInitial (keydown.enter)="save()" (keydown.escape)="toggleSave()">
      <div class="n-qy-savepop__actions"><button type="button" class="n-qy-savepop__cancel" (click)="toggleSave()">Cancel</button><button type="button" class="n-qy-savepop__save" (click)="save()">Save</button></div>
    </div>
  </span>
  <button type="button" class="n-qy-pillbtn" [disabled]="!result?.total" (click)="export()"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 4v11"/><path d="m7 10 5 5 5-5"/><path d="M5 20h14"/></svg>Export CSV</button>
</div>

<div class="n-qy">
  <section class="n-qy-card n-qy-builder" aria-label="Build query">
    <div class="n-qy-builder__scroll">
      <div class="n-qy-sec">
        <h2>Where to look</h2>
        <div class="n-qy-stack"><label class="n-qy-label" for="q-hier">Hierarchy</label>
          <span class="n-qy-select n-qy-select--block"><select id="q-hier" [formControl]="f.hier">…</select><svg …/></span></div>
        <div class="n-qy-stack"><span class="n-qy-label" id="q-scope-l">Search in</span>
          <div style="display:flex;gap:8px;align-items:center;flex-wrap:wrap">
            <div class="n-qy-seg" role="group" aria-labelledby="q-scope-l">
              <button type="button" [attr.aria-pressed]="f.scope==='ALL'" (click)="setScope('ALL')">Whole hierarchy</button>
              <button type="button" [attr.aria-pressed]="f.scope==='UNDER'" (click)="setScope('UNDER')">Under a node</button></div>
            <ng-container *ngIf="f.scope==='UNDER'"><label class="n-sr-only" for="q-root">Node code</label>
              <input id="q-root" class="n-qy-root" placeholder="Node code" [formControl]="f.root" [attr.aria-invalid]="!!rootError" aria-describedby="q-root-err"></ng-container>
          </div>
          <span *ngIf="rootError" id="q-root-err" class="n-qy-err" aria-live="polite">{{rootError}}</span></div>
        <div class="n-qy-stack"><span class="n-qy-label" id="q-nodes-l">Nodes</span>
          <div class="n-qy-seg" role="group" aria-labelledby="q-nodes-l">
            <button type="button" [attr.aria-pressed]="f.nodes==='ALL'" (click)="f.nodes='ALL'">All</button>
            <button type="button" [attr.aria-pressed]="f.nodes==='LEAVES'" (click)="f.nodes='LEAVES'"><span class="n-qy-mark-leaf" aria-hidden="true"></span>Leaves</button>
            <button type="button" [attr.aria-pressed]="f.nodes==='LIMBS'" (click)="f.nodes='LIMBS'"><span class="n-qy-mark-limb" aria-hidden="true"></span>Limbs</button></div></div>
        <button type="button" class="n-qy-switch" role="switch" [attr.aria-checked]="f.inactive" (click)="f.inactive=!f.inactive">
          <span class="n-qy-switch__track" aria-hidden="true"><span class="n-qy-switch__knob"></span></span>Include inactive nodes</button>
      </div>

      <div class="n-qy-sec n-qy-sec--conds">
        <div class="n-qy-condhead"><h2>Conditions</h2>
          <div *ngIf="f.conds.length > 1" class="n-qy-seg n-qy-seg--sm" role="group" aria-label="Match">
            <button type="button" [attr.aria-pressed]="f.match==='ALL'" (click)="f.match='ALL'">All of these</button>
            <button type="button" [attr.aria-pressed]="f.match==='ANY'" (click)="f.match='ANY'">Any of these</button></div></div>
        <span *ngIf="!f.conds.length" class="n-qy-hint">No conditions yet — every node in scope matches.</span>
        <ng-container *ngFor="let c of f.conds; let i = index">
          <span *ngIf="i>0" class="n-qy-join">{{f.match==='ALL' ? 'AND' : 'OR'}}</span>
          <div class="n-qy-cond">
            <div class="n-qy-cond__row">
              <span class="n-qy-prop"><select aria-label="Property" [(ngModel)]="c.property">
                <optgroup *ngFor="let g of propGroups" [label]="g.label"><option *ngFor="let p of g.items" [value]="p.key">{{p.label}}</option></optgroup></select><svg …/></span>
              <button type="button" class="n-qy-x" aria-label="Remove condition" title="Remove condition" (click)="remove(i)"><svg viewBox="0 0 24 24"><path d="M6 6l12 12M18 6 6 18"/></svg></button>
            </div>
            <div class="n-qy-cond__row n-qy-cond__row--b">
              <span class="n-qy-op"><select aria-label="Operator" [(ngModel)]="c.op"><option *ngFor="let o of ops" [value]="o.key">{{o.label}}</option></select><svg …/></span>
              <input *ngIf="c.op!=='IS_EMPTY' && c.op!=='IS_NOT_EMPTY'" aria-label="Value" placeholder="Value" [(ngModel)]="c.value">
            </div>
          </div>
        </ng-container>
        <button *ngIf="f.conds.length < 20" type="button" class="n-qy-add" (click)="addCond()"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>Add condition</button>
      </div>

      <div class="n-qy-sec n-qy-sec--cols">
        <h2>Columns</h2>
        <div>
          <button type="button" class="n-qy-picker" aria-haspopup="listbox" [attr.aria-expanded]="colsOpen" (click)="colsOpen=!colsOpen">
            <span class="n-qy-picker__text"><span class="n-qy-picker__n">{{colsCountText}}</span> <span class="n-qy-picker__list">{{colsListText}}</span></span>
            <svg viewBox="0 0 24 24" aria-hidden="true"><path [attr.d]="colsOpen ? 'm6 15 6-6 6 6' : 'm6 9 6 6 6-6'"/></svg></button>
          <div *ngIf="colsOpen" class="n-qy-panel">
            <div class="n-qy-panel__search"><label class="n-sr-only" for="q-colsearch">Find a column</label><input id="q-colsearch" type="search" placeholder="Find a column" [formControl]="colQ"></div>
            <div class="n-qy-panel__list" role="listbox" aria-multiselectable="true" aria-label="Columns">
              <ng-container *ngFor="let g of filteredGroups">
                <div class="n-qy-panel__group">{{g.label}}</div>
                <button *ngFor="let p of g.items" type="button" role="option" class="n-qy-opt" [attr.aria-selected]="isCol(p.key)" (click)="toggleCol(p.key)">
                  <span class="n-qy-check" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="m5 12 5 5 9-10"/></svg></span>{{p.label}}</button>
              </ng-container>
            </div>
            <div class="n-qy-panel__foot"><button type="button" class="n-qy-panel__clear" (click)="f.cols=[]">Clear all</button><span class="n-spacer"></span><button type="button" class="n-qy-panel__done" (click)="colsOpen=false">Done</button></div>
          </div>
        </div>
        <span class="n-qy-caption">Node, path and status are always shown.</span>
      </div>
    </div>
    <div class="n-qy-runbar">
      <button type="button" class="n-qy-run" (click)="run()"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 5v14l12-7z"/></svg>Run query</button>
      <button type="button" class="n-qy-clear" (click)="clear()">Clear</button>
    </div>
  </section>

  <section class="n-qy-card n-qy-results" aria-label="Results">
    <div *ngIf="!lastRun" class="n-qy-blank"><strong>Results appear here</strong><span>Set where to look and your conditions, then run the query.</span></div>
    <ng-container *ngIf="lastRun">
      <div class="n-qy-reshead">
        <div class="n-qy-reshead__row"><h2>{{titleText}}</h2><span class="n-qy-ctx">{{ctxText}}</span>
          <span *ngIf="stale" class="n-qy-stale">Query changed <button type="button" (click)="run()">Run again</button></span></div>
        <span class="n-qy-sentence">{{sentence}}</span>
      </div>
      <div *ngIf="!result.total" class="n-qy-blank"><strong>No nodes match</strong><span>Try removing a condition or including inactive nodes.</span></div>
      <ng-container *ngIf="result.total">
        <div class="n-qy-tablebox"><table class="n-qy-table">
          <thead><tr>
            <th scope="col" class="n-qy-th--node">Node</th><th scope="col" class="n-qy-th--path">Path</th><th scope="col">Status</th>
            <th *ngFor="let k of tableCols" scope="col" class="n-qy-th--prop" [class.n-qy-th--match]="condProps.has(k)">{{label[k]}}</th>
            <th scope="col"><span class="n-sr-only">Open</span></th></tr></thead>
          <tbody><tr *ngFor="let r of result.rows">
            <td><span class="n-qy-node"><span class="n-qy-node__icon" aria-hidden="true"><span [ngClass]="r.nodeType==='LIMB' ? 'n-qy-limb' : 'n-qy-leaf'"></span></span>
              <span class="n-qy-node__text"><span class="n-qy-code">{{r.code}}</span><span class="n-qy-name">{{r.name}}</span></span></span></td>
            <td class="n-qy-path" [title]="r.path.join(' / ')">{{shortPath(r.path)}}<span class="n-qy-level"> · Level {{r.level}}</span></td>
            <td [class.n-qy-inactive]="r.status==='Inactive'">{{r.status}}</td>
            <td *ngFor="let k of tableCols" [class.n-qy-td--match]="condProps.has(k)" [class.n-qy-td--empty]="!r.values[k]">{{r.values[k] || '—'}}</td>
            <td><a class="n-qy-open" [routerLink]="['/hierarchies', lastRun.hierarchy]" [queryParams]="{node: r.code}">Open</a></td>
          </tr></tbody>
        </table></div>
        <div class="n-qy-foot"><span>Showing {{result.rows.length}} of {{result.total}} {{result.total===1 ? 'node' : 'nodes'}}</span>
          <span *ngIf="condProps.size" class="n-qy-key"><i aria-hidden="true"></i>Columns your conditions use</span></div>
      </ng-container>
    </ng-container>
  </section>
</div>
<div *ngIf="toast" class="n-qy-toast" role="status">{{toast}}</div>
<app-draft-bar />
```

---

## 12. Mock data (use exactly this for demos and tests)

Context: version 2026-11 Working, as of 18 Nov 2026, Live data. Paths (ORGTL): root `ORGTL`, then `OC005 / AM / AMT / …`. ORGTP: `ORGTP / BUTCH / AMT / …`; AMBUS: `AMBUS / AMT / …`. E2E_H and E2E_MGMT have no matching nodes.

| Code | Name | Type | Status | Parent | Finance country | Acct ccy | GL | Ledger |
|---|---|---|---|---|---|---|---|---|
| AMQNT | Quant Technology | Limb | Active | AMT | — | — | — | — |
| AGQNT | US Quant Technology | Limb | Active | AMQNT | — | — | — | — |
| AGCOE | AM AI Center of Excellence | Limb | Active | AMQNT | — | — | — | — |
| AMFIT | FI Technology | Limb | Active | AMT | — | — | — | — |
| AMEQT | Equity Technology | Limb | Active | AMT | — | — | — | — |
| 31679 | UK QNT Tech | Leaf | Active | AGQNT | UK | GBP | 0444 | L200 |
| 34295 | QNT Engineering | Leaf | Active | AGQNT | US | USD | 0444 | L100 |
| 34299 | QNT Leadership | Leaf | Active | AGQNT | US | USD | 0610 | L100 |
| 34301 | QNT Analytics Platform | Leaf | **Inactive** | AGQNT | US | USD | 0512 | L100 |
| 34312 | India QNT Hub | Leaf | Active | AGQNT | IN | INR | 0701 | L300 |
| 34307 | QNT Quant Dev | Leaf | Active | AGCOE | US | USD | 0512 | L100 |
| 41210 | Equity Research Tools | Leaf | Active | AMEQT | UK | GBP | 0444 | L200 |
| 41640 | UK Legacy Ops | Leaf | **Inactive** | AMEQT | UK | GBP | 0444 | L200 |
| 41402 | FI Trading Applications | Leaf | Active | AMFIT | US | USD | 0512 | L100 |
| 41408 | FI Risk Engines | Leaf | Active | AMFIT | UK | GBP | 0512 | L200 |
| 41415 | FI Analytics | Leaf | Active | AMFIT | IN | INR | 0701 | L300 |

All leaves also have: Cost centre = code, Reporting currency USD, Segment AM, Headcount indicator Y, HR solutions indicator N, Include in Workday Y, Standard weekly hours 40 (IN: 45), Time keeping access indicator Y, Legal form LLC, Pending inactivation N, Include in reporting Y, Originating AM org division AMT. Limbs have no property values; Department legacy code and DRM description are empty for all.

Saved queries (newest first):

| Name | Query | Columns |
|---|---|---|
| UK cost centres | ORGTL · Whole · Leaves · active only · Finance country is “UK” | Finance country, Oracle accounting currency, Oracle GL company |
| India ledger L300 under AMT | ORGTL · Under AMT · All · including inactive · Oracle operating ledger is “L300” | Oracle operating ledger, Oracle accounting currency |
| Non-USD accounting currency | ORGTL · Whole · Leaves · active only · Oracle accounting currency is not “USD” | Oracle accounting currency, Finance country, Oracle GL company |

Expected results:
- **First render** (UK cost centres loaded and run): "3 nodes match"; sentence `Leaves in ORGTL, active only, where Finance country is “UK”.`; rows 31679 (`… / AMT / AMQNT / AGQNT · Level 7`, UK / GBP / 0444), 41210 (`… / AM / AMT / AMEQT · Level 6`, UK / GBP / 0444), 41408 (`… / AM / AMT / AMFIT · Level 6`, UK / GBP / 0512). 41640 excluded (inactive). Finance country column tinted.
- **India ledger L300 under AMT**: 2 rows — 34312, 41415 (L300 / INR). Columns: Oracle operating ledger (tinted), Oracle accounting currency.
- **Non-USD accounting currency**: 5 rows — 31679, 34312, 41210, 41408, 41415. Oracle accounting currency tinted.
- **Add condition** (empty value) after a run → "Query changed · Run again"; running gives the same rows (empty-value condition ignored); sentence ends `and Finance country is “…”.`
- **Hierarchy E2E_H**, run → "0 nodes match" + No nodes match state; Export disabled.
- **Under a node** with `XYZ` → error `No node with code “XYZ” in ORGTL.`; Run blocked.
- Column picker: 3 groups (Finance 7, HR 5, General 6); field reads `3 columns: Finance country, Oracle accounting currency, Oracle GL company`.

---

## 13. Accessibility
- One `h1` (Queries); section and card headings `h2`.
- Every input/select has a visible or visually hidden label; icon-only buttons have `aria-label`.
- Segmented controls: `role="group"` + `aria-pressed` buttons; switch: `role="switch"` + `aria-checked`.
- Column picker: `aria-haspopup="listbox"`, `aria-expanded`; options `role="option"` + `aria-selected`; keyboard per §6.3.
- Root error linked with `aria-describedby`, announced `aria-live="polite"`.
- Table: real `<table>` with `th scope="col"`; full path in `title`.
- Contrast ≥ 4.5:1 for all text; empty-value dash `#9E9E9E` is decorative (the cell also has `aria-label="No value"`).

---

## 14. Do NOT
- Don't show the column options as a wall of toggle chips; use the checkbox dropdown.
- Don't add an intro paragraph, numbered section titles, or the green "In plain words" banner.
- Don't put Saved queries in a separate card; it's the toolbar select.
- Don't auto-run on every builder change; only Run, saved-query load, and Run again run.
- Don't re-run for column changes.
- Don't use Title Case labels; don't truncate with ellipsis.
- Don't add nested condition groups, sorting by column header, or row selection checkboxes (v1).
- Don't invent extra operators, colours or messages.

---

## 15. Acceptance checklist
- [ ] Shell identical to other screens; Queries active in the rail.
- [ ] Toolbar: h1 20/700, Saved query select (min 260px) with "New query", Save query popover, Export CSV (disabled at 0).
- [ ] Builder 400px, scrolls; Run bar pinned with Run query (44px green, play icon) and Clear.
- [ ] Where to look: full-width hierarchy select, Search in segmented + mono code input with exact errors, Nodes segmented with shape marks, Include inactive switch.
- [ ] Conditions: cards with property / operator / value, AND/OR join words, Match segmented only at ≥ 2, Add condition pill, empty hint.
- [ ] Columns: checkbox dropdown (in-flow panel) with search, groups, Clear all, Done; field summary text; caption.
- [ ] Results: title + context + stale prompt; sentence per §7.5; sticky-header table with Node / Path (short + level) / Status / property columns (condition columns tinted) / Open; footer count + key.
- [ ] Column changes apply instantly; builder changes make results stale.
- [ ] States: not run, no matches, running, error.
- [ ] Mock data reproduces §12 exactly.
- [ ] Pixel check vs. the "Queries (new · master–detail)" board at 1440px: computed styles match §5–§7 and §10.
