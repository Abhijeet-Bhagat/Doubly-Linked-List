# As-Was screen — Build Specification (features + exact styles)

> **Give this file to the LLM/developer together with `nexus-design-system.md` and a screenshot of the canvas board "As-Was (new)"** (`AsWas-B`). The shell (header `#044014`, context bar, collapsible sidebar, draft bar) comes from the design system unchanged. This file defines **every feature, rule, state, string, size and colour** of the As-Was page, plus a CSS block (§10) to **append verbatim** to `src/styles.css` after `nexus-ui.css`. Component `.scss` files may contain layout only.
>
> **Precedence:** this file > the screenshot > your own judgement. If something is not described here, **do not add it** (no extra buttons, filters, legends, charts, icons, tooltips, helper paragraphs, animations or colours). If a rule seems missing, leave the behaviour out and raise it as a question instead of inventing one.
>
> **Replaces** the current `/as-was` page (big month line chart with a drag handle, type-filter pills Added/Moved/Renamed/Status/Removed/Property/Attribute, "Read-only snapshot" card with a collapsed root, "Recorded changes · 0 shown" panel, "Already happened / Happens later" legend, and the "the data on … as recorded at …" banner). The older canvas board "As-Was time slider" and `as-was-time-slider-spec.md` are superseded by this file.

---

## 0. Glossary (use these words exactly in the UI)

| Term | Meaning |
|---|---|
| Moment | The date + time the tree is shown at. One value, chosen by the user. |
| Now | The latest moment (current time for a Working version; the frozen time for a Finalized version — see §2.3). |
| Change | One committed change record (audit row) in the selected hierarchy: Added, Removed, Moved, Renamed, Status, Property. |
| Later change | A change committed **after** the moment. |
| Ghost row | A dashed placeholder row in the tree for a node that is not in the tree at the moment (added later, or just removed). |

Never show on this screen: "business date", "recorded as of", "valid period", "picture", "bitemporal", "Read-only snapshot" card title, "Already happened", "Happens later", "Drag the handle", "Recorded changes", "0 shown", "Future" legend, internal ids/hashes (e.g. `5095dcc9`) — always show node **codes**.

---

## 1. Purpose

As-Was answers: **"What did this hierarchy look like at a past moment, and what changed around then?"**
1. **Pick a moment** — type it, click a day, click a change, or press Now.
2. **See the tree at that moment** — opened at the place where things changed.
3. **See that day's changes in order**, with a line showing where the tree is in that sequence.
4. **Optionally see what will change later** — marked on the tree, without changing the moment.

Route: `/as-was` with query params (§8.1). Sidebar item **As-Was** has `aria-current="page"`. Covers Shape the Tree demo step 4 ("ORGTL before the core edits → 34307 under AGQNT, 99301 doesn't exist").

Design rules for this page:
- No chart with a drag handle. Navigation is: date-time box, day strip, change list, Now.
- Master–detail like Worklist and Compare: changes on the left, tree on the right.
- The page is read-only. No edit buttons anywhere on it.
- Each fact appears once; text wraps (never ellipsis); colour only for change types, the moment marker and selection.

---

## 2. Domain rules (backend + frontend)

### 2.1 What the tree shows
The tree at moment `at` is the **committed** data of the **context-bar version** for the selected hierarchy, read with the store's two time axes set from that one moment:
`picture(version, hierarchy, asOf = date(at), recordedAsOf = at)`.
- Drafts, pending requests and sandboxes are never included ("Committed changes only").
- The context-bar "Viewing as of" date and workspace are **not** changed by this page. If the user arrives with a context "as of" date set, it becomes the initial moment (`{date}T00:00`), otherwise Now.

### 2.2 What counts as a change
A change is one committed change record of the version whose **recorded (commit) time** falls in the time range, affecting the selected hierarchy:
- **Local** changes (Added, Removed, Moved) count only for the hierarchy they were made in.
- **Global** changes (Renamed, Status, Property) count for every hierarchy the node is placed in.
- One record per change; a request with 4 changes produces 4 records (same request id).
- If a change was committed on day D but is **effective later** (effective date > D), it still lists on day D, line 3 ends with ` · effective {d Mon}`, and it enters the tree only from that effective date.

### 2.3 Moment range
- **Max (Now):** Working version → current time; Finalized version → its frozen time (`recorded_as_of`). For a Finalized version the **Now** button label is **Latest**.
- **Min:** 00:00 on the first day of the month of the version's earliest change record (or of its creation, whichever is earlier).
- Moments outside the range are clamped to it. Future days are disabled.

### 2.4 Time zone and format
All times in the user's time zone, 24 h. Day labels `13 Nov`; long form `13 Nov 2026, 10:30`; the moment `{date}T00:00` is shown as **start of day**.

---

## 3. Change-type tokens (identical to Compare)

| Type | Label | Symbol | `fg` | `bg` (tint) |
|---|---|---|---|---|
| added | Added | `+` | `#2E6B21` | `#E3F0DE` |
| removed | Removed | `−` (U+2212) | `#3D3D3D` | `#E6E6E6` |
| moved | Moved | `→` | `#1D5FA8` | `#E4EEFA` |
| renamed | Renamed | `Aa` | `#6B3FA0` | `#EFE7F8` |
| status | Status | `▲` | `#7A4100` | `#FFF1DE` |
| property | Property | `≡` | `#1F4D16` | `#F2F4EF` |

Brand `#368727`; brand light bar `#B9D9AE`; selection tint `#E3F0DE`; toggle-on bg `#EEF5EB`.

---

## 4. Layout (1440 × 1080 at design time)

```
┌ .n-header (#044014) [☰] LOGO Nexus Master Data Central ………………… DEVELOPMENT (VA E2E Version Admin One · e2e_vadmin Switch) ┐
├ .n-context  Version [2026-11 · Working ▾]  As of [..]  Work in (Live data|Sandbox) ………………………………………… Manage versions ┤
├ rail ┬ .n-main ────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ ⌂    │ As-Was  Hierarchy (ORGTL · Organization – Total Fidelity ▾)  Show it as it was on (13/11/2026 10:30) (Now) …… (⇄ Compare with now) │
│ ⊞    │ ┌ Day strip ───────────────────────────────────────────────────────────────────────────────────────────┐ │
│ ⌕    │ │ ‹ November 2026 ›  8 changes this month                                                               │ │
│ ⇄    │ │  ▁   ▁   ▁   ▃   ▁ … ▃ … ▃ ▃ [█] ▁ ▁ ▁ ▃ ░ ░ ░ … (bars per day, selected day tinted, future greyed)     │ │
│ ◷ ■  │ │  1   2   3   4   5    9    12 13  14 15 16 17 18 19 …                                                │ │
│ ───  │ └───────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│ ☰    │ ┌ Friday, 13 Nov [4] ───────────┐ ┌ ORGTL on 13 Nov 2026, 10:30 [Read-only] ……… (✓ Show later changes) ┐ │
│ ◫    │ │ (Tree shows 10:30)━━━━━━━━━━━  │ │ Committed changes only · 5 changes happened after this moment.        │ │
│ ≡    │ │ 10:31 [+] 99301 QNT Research   │ │ □ ORGTL Organization – Total Fidelity                                  │ │
│ ───  │ │       Added under AGQNT        │ │   □ OC005 Asset Management                                             │ │
│ ⧉    │ │       u_editor1 · CR-1038      │ │     □ AM … □ AMT … □ AMQNT Quant Technology                            │ │
│ ⚙    │ │ 10:42 [→] 34307 QNT Quant Dev  │ │           □ AGCOE …   ┆○ 99302 AI Platform Ops  (Added 17 Nov)┆        │ │
│      │ │ 10:48 [Aa] 34301 …             │ │           □ AGQNT …   ○ 34307 QNT Quant Dev (Moves to AGCOE · 13 Nov)  │ │
│      │ │ 10:52 [▲] 34301 …              │ │                       ┆○ 99301 QNT Research (Added 13 Nov)┆           │ │
│      │ └────────────────────────────────┘ └────────────────────────────────────────────────────────────────────────┘ │
│      ├ .n-draftbar (global, unchanged) ─────────────────────────────────────────────────────────────────────────────┤
└──────┴──────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

- Root `.n-app.n-app--fixed` (100vh, min-height **1000px**, no page scroll).
- `.n-main` column: **toolbar** → **day strip** → **body** (flex 1, min-height 0) → `.n-draftbar`.
- Body (`.n-aw`): flex row, `gap 16px`, `padding 16px 20px`, `align-items: stretch`.
- **Changes card** (left): `width 400px; flex 0 0 400px`; header fixed, list scrolls.
- **Tree card** (right): `flex 1 1 auto; min-width 0`; header fixed, tree scrolls.
- Responsive: content width < 1100px → changes card `width 340px; flex-basis 340px`; day numbers hide for every day except 1, 5, 10, 15, 20, 25, 30, today and the selected day when a cell is < 28px wide. (< 900px viewport: out of scope.)

---

## 5. Toolbar

Container: `flex 0 0 auto; display flex; flex-wrap wrap; align-items center; gap 12px 20px; padding 16px 20px 0`.

Left → right:
1. `h1` **As-Was** — 20px / 700, letter-spacing −0.01em, margin 0.
2. Controls group (flex, wrap, `gap 10px 16px`):
   - **Hierarchy**: visible label `Hierarchy` (12px / 600, `#5C5C5C`, gap 8px) + pill select — identical to Compare: height 40px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 36px 0 14px`, 13px / 600, chevron 12×12 stroke 2.4 at `right 14px; top 14px`. Options `{CODE} · {Name}` (same list and order as Hierarchies/Compare).
   - **Moment**: visible label **`Show it as it was on`** (12px / 600, `#5C5C5C`) + `<input type="datetime-local">` (height 40px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 14px`, 13px / 600, `#1F1F1F` on white; `min`/`max` = §2.3 range, step 60 s) + **Now** button.
   - **Now** button: height 40px, `padding 0 16px`, radius 999px, `border 1.5px solid`, 13px / 600. When the moment **is** Now: bg `#368727`, border `#368727`, text white (`aria-pressed="true"`). Otherwise white bg, border `#BDBDBD`, text `#1F1F1F`. Label **Now** (Finalized version: **Latest**).
3. Spacer.
4. **Compare with now** — an `<a>` styled as a secondary pill: inline-flex, `gap 8px`, height 40px, `padding 0 16px`, radius 999px, `border 1.5px solid #BDBDBD`, white, 13px / 600, `#1F1F1F`, no underline, nowrap; leading swap icon 16×16 stroke 2 (`M7 7h12` `m15 3 4 4-4 4` `M17 17H5` `m9 13-4 4 4 4`). Target: `/compare?h={HIER}&from={version}@{at}&to={version}` — if the Compare API doesn't accept a moment, link to `/compare?h={HIER}` with From = the Finalized version before the context version and To = the context version (Compare defaults).

---

## 6. Day strip

`section[aria-label="Days with changes"]`: `flex 0 0 auto; margin 16px 20px 0; bg #FFFFFF; border 1px solid #D9D9D9; radius 16px; padding 12px 16px 10px;` column, `gap 8px`.

### 6.1 Header row (flex, centred, `gap 6px`)
- **Previous month** button: 32×32 circle, transparent, border 0, chevron icon 16×16 stroke 2 path `m15 6-6 6 6 6`, `aria-label="Previous month"`.
- `h2` month label **`November 2026`**: 14px / 700, `min-width 116px`, centred.
- **Next month** button: same, path `m9 6 6 6-6 6`, `aria-label="Next month"`.
- Disabled arrow (outside §2.3 range): `opacity .35`, `aria-disabled="true"`, no action.
- Count text (12px, `#5C5C5C`, `margin-left 6px`): **`{n} changes this month`** / **`1 change this month`** / **`No changes this month`**.
- Month arrows change only the displayed month — **not** the moment.

### 6.2 Day cells
Grid: `grid-template-columns: repeat({daysInMonth}, minmax(0,1fr)); gap 4px`. One `<button>` per day:
- `height 72px; border 0; border-radius 8px; padding 6px 2px 5px;` column, `justify-content flex-end`, `align-items center`, `gap 5px`.
- Bar: `width 12px; border-radius 3px;` height and colour:

| Day | Bar height | Bar colour |
|---|---|---|
| Has n ≥ 1 changes, selected day | `min(10 + 9·n, 46)` px | `#368727` |
| Has n ≥ 1 changes, other day | same | `#B9D9AE` |
| No changes (past/today) | 3px | `#E6E6E6` |
| Future | 3px | `#F2F2F2` |

- Day number: 11px; weight **700** for the selected day and today, else 500; colour selected `#1F4D16`, future `#9E9E9E`, else `#4A4A4A`.
- Cell background: selected day `#E3F0DE`, else transparent; hover (past/today, not selected) `#F2F2F2`.
- `aria-label` = `13 Nov · 4 changes` / `· 1 change` / `· 0 changes`; future `20 Nov · future`. `aria-pressed` = selected. Future days: `aria-disabled="true"`, `cursor default`, no action.
- **Click** a past day → moment = that day **00:00** ("start of day"). Click **today** → moment = Now.
- The "selected day" is always the date part of the moment.

---

## 7. Body

### 7.1 Changes card (left) — `section[aria-label="Changes on this day"]`
Card: white, `border 1px solid #D9D9D9`, radius 16px, column, overflow hidden.
- **Header**: `padding 16px 18px 12px; border-bottom 1px solid #ECECEC;` flex, centred, `gap 8px`: `h2` **`{Weekday}, {d Mon}`** (e.g. `Friday, 13 Nov`) 16px / 700, margin 0 + count pill 12px / 700, `padding 1px 8px`, radius 999px (n ≥ 1: white on `#368727`; 0: `#4A4A4A` on `#ECECEC`).
- **List**: `flex 1; min-height 0; overflow auto; padding 8px 10px 14px`. Changes of the selected day in **ascending time**.
- **Empty day**: one line **`No changes on this day.`** (13px, `#5C5C5C`, `padding 16px 8px`); no marker.
- **Moment marker** (`div[role=separator][aria-label="Tree shows …"]`): flex, centred, `gap 8px`, `margin 6px 4px`:
  - Pill: 12px / 700, white on `#368727`, `padding 3px 10px`, radius 999px, nowrap — text **`Tree shows {HH:MM}`**, **`Tree shows start of day`** (moment = 00:00), **`Tree shows now`** (moment = Now).
  - Line: `flex 1; height 2px; background #368727; border-radius 2px`.
  - Position: **before the first change whose time is after the moment**; after the last change if none is after it. A change exactly at the moment is **above** the marker (it's already in the tree).
- **Change row** (`<button>`, `aria-current="true"` when its time equals the moment): flex, `align-items flex-start`, `gap 10px`, width 100%, `padding 10px`, radius 12px, `margin-bottom 2px`, `text-align left`, `#1F1F1F`; bg `#E3F0DE` when current, hover `#F2F2F2`, else transparent.
  - Time: JetBrains Mono 12px, `#5C5C5C`, `width 40px; flex 0 0 40px; padding-top 6px` — `HH:MM`.
  - Type square: 28×28, radius 8px, tint bg, `fg` colour, 12px / 700, symbol centred, `aria-hidden`.
  - Text column (`gap 2px`, `min-width 0`):
    - Line 1: 14px / 600, line-height 1.4 — code (JetBrains Mono 13px / 500, `#2E6B21`, `margin-right 8px`) + node name **after** the change (renamed: new name).
    - Line 2: 13px, `#4A4A4A`, line-height 1.4 — same descriptions as Compare: `Added under {parent}` · `Removed from {parent}` · `Moved {old} → {new}` · `Renamed “{old}” → “{new}”` · `Status {old} → {new}` · `{Property label} {old} → {new}`.
    - Line 3: 12px, `#6B6B6B` — `{changed by} · {request id}` (no request: `{changed by}`; effective later: append ` · effective {d Mon}`).
  - **Click** a row → moment = that change's time (the tree shows the state **just after** it). The marker moves below the row.
- Keyboard: Up/Down between rows, Enter selects.

### 7.2 Tree card (right) — `section[aria-label="Tree at this moment"]`
Card as above.
- **Header**: `padding 16px 20px 12px; border-bottom 1px solid #ECECEC;` column, `gap 4px`.
  - Row (flex, wrap, centred, `gap 10px`):
    - `h2` 18px / 700: **`{HIER} on {d Mon yyyy}, {HH:MM}`** · **`{HIER} on {d Mon yyyy}, start of day`** · **`{HIER} now`** (Finalized at Latest: **`{HIER} at freeze`**).
    - Pill **Read-only**: 12px / 600, `#4A4A4A` on `#ECECEC`, `padding 2px 10px`, radius 999px.
    - Spacer.
    - **Show later changes** toggle — `button[aria-pressed]`: inline-flex, `gap 8px`, height 32px, `padding 0 12px 0 6px`, radius 999px, `border 1.5px solid` (on `#368727` / off `#DCDCDC`), bg (on `#EEF5EB` / off white), 13px / 600 `#1F1F1F`; leading 20×20 circle (on `#368727` / off `#BDBDBD`) with a white check icon 12×12 stroke 3 path `m5 12 5 5 9-10`. Default **on**; remembered per user (localStorage `nexus.asWas.showLater`).
  - Sub line (12px, `#5C5C5C`): **`Committed changes only · {n} changes happened after this moment.`** / `… · 1 change happened after this moment.` / **`Committed changes only · Nothing has changed since — this is how it looks now.`** (`n` = changes in this hierarchy between the moment and Now.)
- **Tree body**: `flex 1; min-height 0; overflow auto; padding 10px 14px 16px`.

**Which nodes are shown (expansion rules):**
1. The **ancestor chain** from the hierarchy root down to every *focus parent* is expanded, one row per ancestor (siblings of ancestors are **not** listed).
2. **Focus parents** = the old and new parents of every change on the selected day (added → parent; removed → old parent; moved → both; renamed/status/property → the node's current parent). If the day has no changes: the parents from the nearest earlier day with changes; if none, the root's children are shown collapsed.
3. Under each focus parent, **all its children at the moment** are listed (sibling order). Limb children of a focus parent that are themselves focus parents are expanded; other limbs are collapsed.
4. Any collapsed limb can be expanded/collapsed by clicking its square (lazy-load children at the same moment). Expansion state resets when the selected day changes.
5. Max 200 rows rendered; beyond that, virtual scroll.

**Row** (`div`, not interactive except the expand square):
- flex, centred, `gap 8px`, `min-height 38px`, `padding 2px 10px 2px {10 + 22·depth}px`, radius 8px, `margin-bottom 2px`, `border 1.5px solid transparent`.
- Icon slot 16px: **limb** = 12×12 square, `border 1.5px solid #3D3D3D`, white, containing `−` (expanded) or `+` (collapsed) in JetBrains Mono 10px — rendered as a `<button aria-expanded aria-label="Expand {code}">` when collapsible; **leaf** = 8×8 circle, `border 1.5px solid #6B6B6B`.
- Code: JetBrains Mono 12px, `#5C5C5C`, `min-width 52px`.
- Name: 14px, line-height 1.35, wraps; weight **600** for the root row and the highlighted row, **500** otherwise; colour `#1F1F1F`. **Inactive** nodes: colour `#6B6B6B` and suffix **` · Inactive`** (12px / 400, `#6B6B6B`).
- Names, status and placement are those **at the moment** (a node renamed later shows its old name).

**Highlights and tags** (tag pill: 11px / 700, `padding 1px 8px`, radius 999px, white bg, text and 1px border in the type `fg`; **solid** border = happened at the moment, **dashed** border = happens later; nowrap; right-aligned after the name; max one solid + one dashed tag per row):

| Situation | Row | Tag (exact text) |
|---|---|---|
| A change at exactly the moment (row clicked) — added / moved / renamed / status / property | bg = type tint; name 600 | solid: `Added · 10:31` · `Moved in · 10:42` · `Renamed · 10:48` · `Now inactive · 10:52` / `Now active · …` · `{short label} {new value} · 14:20` (e.g. `GL 0444 · 14:20`) |
| A removal at exactly the moment | **ghost row** under the old parent, name struck through, colour `#6B6B6B` | solid: `Removed · 16:05` |
| Show later changes **on**: node changes later (first upcoming change only) | normal row | dashed: `Moves to {CODE} · {d Mon}` · `Renamed {d Mon}` · `Inactive from {d Mon}` / `Active from {d Mon}` · `{short label} changes {d Mon}` (e.g. `GL changes 13 Nov`) · `Removed {d Mon}` |
| Show later changes **on**: node added later under a shown parent | **ghost row** (dashed `#BDBDBD` border, name `#6B6B6B`) | dashed: `Added {d Mon}` |

- Ghost order under a parent: real children → just-removed ghost → later-added ghosts (by time).
- A later-added node whose parent is not shown is not listed.
- Toggle **off** removes all dashed tags and later-added ghosts; solid highlights stay.

---

## 8. Behaviour

### 8.1 URL & state
`/as-was?h={HIER}&at={yyyy-MM-ddTHH:mm}&later={1|0}`; `at` omitted = Now. Kept in sync with `replaceUrl`. Displayed month = month of `at`, unless the user browsed with the arrows (not in the URL).

### 8.2 Interactions
| Action | Result |
|---|---|
| Change Hierarchy | Keep the moment; reload strip counts, day list and tree |
| Type a moment | Clamp to range; moment = value; displayed month follows |
| Now / Latest | Moment = Now; selected day = today (or freeze day) |
| Click past day | Moment = that day 00:00 |
| Click today | Moment = Now |
| Click change row | Moment = its time; marker below it; row + tree node highlighted |
| Month arrows | Change displayed month only |
| Toggle Show later changes | Show/hide dashed tags and later ghosts |
| Compare with now | Navigate per §5 |

### 8.3 Loading & errors
- Each of the three areas loads independently; a newer request cancels the older one (switchMap). Skeletons only after 150 ms:
  - Strip: bars as 3px `#F2F2F2` stubs; count text "–".
  - Changes: 4 skeleton rows (28×28 square + bars 14px×60%, 10px×40%, 10px×30%; `#F2F2F2`, radius 6/8px).
  - Tree: 8 skeleton rows (16px icon + bar 12px × 30–70%, indented 10/32/54px…).
- Error (any area): that area shows **`Couldn't load this. Try again`** (13px, `#5C5C5C`; "Try again" as a link button). Tree error also shows the reference id in JetBrains Mono 12px.
- No spinners, no toasts on this page.

---

## 9. Data & API (Spring Boot)

```
GET /api/as-was/range?version=&hierarchy=                    → { min: ISO, max: ISO, maxLabel: 'NOW'|'LATEST' }
GET /api/as-was/days?version=&hierarchy=&month=yyyy-MM       → [{ date: 'yyyy-MM-dd', count: number }]   (every day of the month; future days count 0)
GET /api/as-was/changes?version=&hierarchy=&date=yyyy-MM-dd  → ChangeRecord[]   (ascending recordedAt)
GET /api/as-was/tree?version=&hierarchy=&at=ISO&day=yyyy-MM-dd&later=true
                                                             → { rows: TreeRow[], laterCount: number }
GET /api/as-was/children?version=&hierarchy=&at=ISO&parent={code}&later=true → TreeRow[]   (expand on click)
```

```ts
type ChangeType = 'added' | 'removed' | 'moved' | 'renamed' | 'status' | 'property';
interface ChangeRecord {
  recordedAt: string;            // commit time, ISO-8601
  effectiveFrom: string;         // yyyy-MM-dd
  type: ChangeType; code: string;
  name: string;                  // name after the change
  oldParent?: string; newParent?: string;
  before?: string; after?: string;                   // name / status / property value
  property?: { key: string; label: string; shortLabel: string };   // e.g. ORACLE_GL_COMPANY, "Oracle GL company", "GL"
  changedBy: string; requestId?: string;
}
interface TreeRow {
  code: string; name: string; nodeType: 'LIMB' | 'LEAF'; status: 'Active' | 'Inactive';
  depth: number; expanded?: boolean; hasChildren?: boolean;
  ghost?: 'REMOVED_NOW' | 'ADDED_LATER';
  now?: { type: ChangeType; at: string; text: string };      // solid tag (change at exactly `at`)
  later?: { type: ChangeType; at: string; text: string };    // dashed tag (first upcoming change)
}
```
Server notes:
- Tree = `picture(version, hierarchy, asOf = date(at), recordedAsOf = at)` (§2.1); use the same picture loader as Hierarchies.
- `laterCount` = change records in the hierarchy with `at < recordedAt ≤ range.max`.
- Tags text may be built on the client from `type`, `at`, before/after; the server returns the data.
- Index: change records by `(version, hierarchy, recorded_at)`; days query is a `GROUP BY date(recorded_at)`.

---

## 10. CSS to append to `src/styles.css` (after `nexus-ui.css`)

```css
/* ===== As-Was ===== */
.n-aw-toolbar{flex:0 0 auto;display:flex;flex-wrap:wrap;align-items:center;gap:12px 20px;padding:16px 20px 0}
.n-aw-toolbar h1{margin:0;font-size:20px;font-weight:700;letter-spacing:-.01em}
.n-aw-controls{display:flex;flex-wrap:wrap;align-items:center;gap:10px 16px}
.n-aw-field{display:flex;align-items:center;gap:8px}
.n-aw-field label{font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-aw-select{position:relative}
.n-aw-select select{appearance:none;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 36px 0 14px;font-size:13px;font-weight:600;color:var(--n-text);background:var(--n-surface);cursor:pointer}
.n-aw-select svg{position:absolute;right:14px;top:14px;width:12px;height:12px;stroke:var(--n-text);stroke-width:2.4;fill:none;stroke-linecap:round;stroke-linejoin:round;pointer-events:none}
.n-aw-when{height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 14px;font-size:13px;font-weight:600;color:var(--n-text);background:var(--n-surface)}
.n-aw-now{height:40px;padding:0 16px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);color:var(--n-text);font-size:13px;font-weight:600}
.n-aw-now[aria-pressed="true"]{background:var(--n-brand);border-color:var(--n-brand);color:#fff}
.n-aw-cmp{display:inline-flex;align-items:center;gap:8px;height:40px;padding:0 16px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);font-size:13px;font-weight:600;color:var(--n-text);text-decoration:none;white-space:nowrap}
.n-aw-cmp:hover{background:var(--n-surface-sunk);color:var(--n-text);text-decoration:none}
.n-aw-cmp svg{width:16px;height:16px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
/* day strip */
.n-aw-strip{flex:0 0 auto;margin:16px 20px 0;background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);padding:12px 16px 10px;display:flex;flex-direction:column;gap:8px}
.n-aw-strip__head{display:flex;align-items:center;gap:6px}
.n-aw-strip__head h2{margin:0;font-size:14px;font-weight:700;min-width:116px;text-align:center}
.n-aw-arrow{width:32px;height:32px;border-radius:50%;border:0;background:transparent;display:flex;align-items:center;justify-content:center;padding:0;color:var(--n-text)}
.n-aw-arrow[aria-disabled="true"]{opacity:.35;cursor:default}
.n-aw-arrow svg{width:16px;height:16px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-aw-strip__count{font-size:12px;color:var(--n-text-muted);margin-left:6px}
.n-aw-days{display:grid;gap:4px} /* grid-template-columns set inline: repeat(N, minmax(0,1fr)) */
.n-aw-day{height:72px;border:0;border-radius:var(--n-r-tree);background:transparent;padding:6px 2px 5px;display:flex;flex-direction:column;justify-content:flex-end;align-items:center;gap:5px}
.n-aw-day:hover:not([aria-pressed="true"]):not([aria-disabled="true"]){background:var(--n-surface-sunk)}
.n-aw-day[aria-pressed="true"]{background:var(--n-brand-50)}
.n-aw-day[aria-disabled="true"]{cursor:default}
.n-aw-day__bar{width:12px;border-radius:3px;background:var(--n-divider-2);height:3px}
.n-aw-day--has .n-aw-day__bar{background:var(--n-brand-border)}
.n-aw-day--has[aria-pressed="true"] .n-aw-day__bar{background:var(--n-brand)}
.n-aw-day[aria-disabled="true"] .n-aw-day__bar{background:var(--n-surface-sunk)}
.n-aw-day__num{font-size:11px;font-weight:500;color:var(--n-text-3)}
.n-aw-day--today .n-aw-day__num,.n-aw-day[aria-pressed="true"] .n-aw-day__num{font-weight:700}
.n-aw-day[aria-pressed="true"] .n-aw-day__num{color:var(--n-brand-800)}
.n-aw-day[aria-disabled="true"] .n-aw-day__num{color:var(--n-separator)}
/* body */
.n-aw{flex:1 1 auto;min-height:0;display:flex;gap:16px;padding:16px 20px;align-items:stretch}
.n-aw-card{background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);display:flex;flex-direction:column;overflow:hidden;min-width:0}
.n-aw-changes{width:400px;flex:0 0 400px}
.n-aw-tree{flex:1 1 auto}
.n-aw-changes__head{padding:16px 18px 12px;border-bottom:1px solid var(--n-divider);display:flex;align-items:center;gap:8px}
.n-aw-changes__head h2{margin:0;font-size:16px;font-weight:700}
.n-aw-count{font-size:12px;font-weight:700;border-radius:var(--n-r-pill);padding:1px 8px;color:#fff;background:var(--n-brand)}
.n-aw-count--zero{color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-aw-changes__list{flex:1 1 auto;min-height:0;overflow:auto;padding:8px 10px 14px}
.n-aw-empty{padding:16px 8px;font-size:13px;color:var(--n-text-muted)}
.n-aw-marker{display:flex;align-items:center;gap:8px;margin:6px 4px}
.n-aw-marker__pill{font-size:12px;font-weight:700;color:#fff;background:var(--n-brand);border-radius:var(--n-r-pill);padding:3px 10px;white-space:nowrap}
.n-aw-marker__line{flex:1 1 auto;height:2px;background:var(--n-brand);border-radius:2px}
.n-aw-change{display:flex;align-items:flex-start;gap:10px;width:100%;background:transparent;border:0;border-radius:var(--n-r-tile);padding:10px;margin-bottom:2px;text-align:left;color:var(--n-text)}
.n-aw-change:hover{background:var(--n-surface-sunk)}
.n-aw-change[aria-current="true"]{background:var(--n-brand-50)}
.n-aw-change__time{font-family:var(--n-mono);font-size:12px;color:var(--n-text-muted);width:40px;flex:0 0 40px;padding-top:6px}
.n-aw-sq{width:28px;height:28px;flex:0 0 28px;border-radius:8px;display:flex;align-items:center;justify-content:center;font-weight:700;font-size:12px;color:var(--t-fg);background:var(--t-bg)}
.n-aw-change__text{display:flex;flex-direction:column;gap:2px;flex:1 1 auto;min-width:0}
.n-aw-change__name{font-size:14px;font-weight:600;line-height:1.4}
.n-aw-code{font-family:var(--n-mono);font-size:13px;font-weight:500;color:var(--n-brand-700);margin-right:8px}
.n-aw-change__desc{font-size:13px;color:var(--n-text-3);line-height:1.4}
.n-aw-change__who{font-size:12px;color:var(--n-text-faint)}
/* type colours (same as Compare) */
.n-t-added{--t-fg:#2E6B21;--t-bg:#E3F0DE}.n-t-removed{--t-fg:#3D3D3D;--t-bg:#E6E6E6}.n-t-moved{--t-fg:#1D5FA8;--t-bg:#E4EEFA}
.n-t-renamed{--t-fg:#6B3FA0;--t-bg:#EFE7F8}.n-t-status{--t-fg:#7A4100;--t-bg:#FFF1DE}.n-t-property{--t-fg:#1F4D16;--t-bg:#F2F4EF}
/* tree */
.n-aw-tree__head{padding:16px 20px 12px;border-bottom:1px solid var(--n-divider);display:flex;flex-direction:column;gap:4px}
.n-aw-tree__title{display:flex;align-items:center;gap:10px;flex-wrap:wrap}
.n-aw-tree__title h2{margin:0;font-size:18px;font-weight:700}
.n-aw-ro{font-size:12px;font-weight:600;color:var(--n-text-3);background:var(--n-neutral-bg);border-radius:var(--n-r-pill);padding:2px 10px}
.n-aw-later{display:inline-flex;align-items:center;gap:8px;height:32px;padding:0 12px 0 6px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-soft);background:var(--n-surface);font-size:13px;font-weight:600;color:var(--n-text)}
.n-aw-later__dot{width:20px;height:20px;border-radius:50%;background:var(--n-border-input);color:#fff;display:flex;align-items:center;justify-content:center}
.n-aw-later__dot svg{width:12px;height:12px;stroke:currentColor;stroke-width:3;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-aw-later[aria-pressed="true"]{border-color:var(--n-brand);background:var(--n-brand-40)}
.n-aw-later[aria-pressed="true"] .n-aw-later__dot{background:var(--n-brand)}
.n-aw-tree__sub{font-size:12px;color:var(--n-text-muted)}
.n-aw-tree__body{flex:1 1 auto;min-height:0;overflow:auto;padding:10px 14px 16px}
.n-aw-node{display:flex;align-items:center;gap:8px;min-height:38px;padding:2px 10px;border-radius:var(--n-r-tree);border:1.5px solid transparent;margin-bottom:2px} /* padding-left inline: 10 + 22*depth px */
.n-aw-node--now{background:var(--t-bg)}
.n-aw-node--ghost{border-style:dashed;border-color:var(--n-border-input)}
.n-aw-node__icon{width:16px;flex:0 0 16px;display:flex;align-items:center;justify-content:center}
.n-aw-limb{width:12px;height:12px;border:1.5px solid var(--n-text-2);background:#fff;display:flex;align-items:center;justify-content:center;font-family:var(--n-mono);font-size:10px;line-height:1;padding:0;color:var(--n-text)}
.n-aw-leaf{width:8px;height:8px;border-radius:50%;border:1.5px solid var(--n-text-faint)}
.n-aw-node__code{font-family:var(--n-mono);font-size:12px;color:var(--n-text-muted);min-width:52px}
.n-aw-node__name{font-size:14px;font-weight:500;line-height:1.35;flex:1 1 auto;min-width:0;color:var(--n-text)}
.n-aw-node--root .n-aw-node__name,.n-aw-node--now .n-aw-node__name{font-weight:600}
.n-aw-node--inactive .n-aw-node__name,.n-aw-node--ghost .n-aw-node__name{color:var(--n-text-faint)}
.n-aw-node--removed .n-aw-node__name{text-decoration:line-through}
.n-aw-node__suffix{font-size:12px;font-weight:400;color:var(--n-text-faint)}
.n-aw-tag{font-size:11px;font-weight:700;color:var(--t-fg);background:#fff;border:1px solid var(--t-fg);border-radius:var(--n-r-pill);padding:1px 8px;white-space:nowrap}
.n-aw-tag--later{border-style:dashed}
@media (max-width:1240px){.n-aw-changes{width:340px;flex-basis:340px}}
```

---

## 11. HTML contract (Angular template skeleton)

```html
<div class="n-aw-toolbar">
  <h1>As-Was</h1>
  <div class="n-aw-controls">
    <div class="n-aw-field"><label for="aw-hier">Hierarchy</label>
      <span class="n-aw-select"><select id="aw-hier" [formControl]="hier">
        <option *ngFor="let h of hierarchies" [value]="h.code">{{h.code}} · {{h.name}}</option></select>
        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg></span></div>
    <div class="n-aw-field"><label for="aw-when">Show it as it was on</label>
      <input id="aw-when" class="n-aw-when" type="datetime-local" step="60" [min]="range.min" [max]="range.max" [formControl]="at">
      <button type="button" class="n-aw-now" [attr.aria-pressed]="isNow" (click)="goNow()">{{range.maxLabel==='LATEST' ? 'Latest' : 'Now'}}</button></div>
  </div>
  <span class="n-spacer"></span>
  <a class="n-aw-cmp" [routerLink]="'/compare'" [queryParams]="compareParams"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M7 7h12"/><path d="m15 3 4 4-4 4"/><path d="M17 17H5"/><path d="m9 13-4 4 4 4"/></svg>Compare with now</a>
</div>

<section class="n-aw-strip" aria-label="Days with changes">
  <div class="n-aw-strip__head">
    <button type="button" class="n-aw-arrow" aria-label="Previous month" [attr.aria-disabled]="!canPrev" (click)="prevMonth()"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="m15 6-6 6 6 6"/></svg></button>
    <h2>{{monthLabel}}</h2>
    <button type="button" class="n-aw-arrow" aria-label="Next month" [attr.aria-disabled]="!canNext" (click)="nextMonth()"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="m9 6 6 6-6 6"/></svg></button>
    <span class="n-aw-strip__count">{{monthCountText}}</span>
  </div>
  <div class="n-aw-days" [style.grid-template-columns]="'repeat(' + days.length + ', minmax(0,1fr))'">
    <button *ngFor="let d of days" type="button" class="n-aw-day" [class.n-aw-day--has]="d.count>0" [class.n-aw-day--today]="d.today"
            [attr.aria-pressed]="d.selected" [attr.aria-disabled]="d.future" [attr.aria-label]="d.aria" (click)="pickDay(d)">
      <span class="n-aw-day__bar" aria-hidden="true" [style.height.px]="d.count ? min(10 + 9*d.count, 46) : 3"></span>
      <span class="n-aw-day__num">{{d.day}}</span>
    </button>
  </div>
</section>

<div class="n-aw">
  <section class="n-aw-card n-aw-changes" aria-label="Changes on this day">
    <div class="n-aw-changes__head"><h2>{{dayTitle}}</h2><span class="n-aw-count" [class.n-aw-count--zero]="!dayChanges.length">{{dayChanges.length}}</span></div>
    <div class="n-aw-changes__list">
      <div *ngIf="!dayChanges.length" class="n-aw-empty">No changes on this day.</div>
      <ng-container *ngFor="let item of dayItems">
        <div *ngIf="item.marker" class="n-aw-marker" role="separator" [attr.aria-label]="markerText">
          <span class="n-aw-marker__pill">{{markerText}}</span><span class="n-aw-marker__line" aria-hidden="true"></span></div>
        <button *ngIf="item.change as c" type="button" class="n-aw-change" [ngClass]="'n-t-'+c.type" [attr.aria-current]="c.recordedAt===at" (click)="setAt(c.recordedAt)">
          <span class="n-aw-change__time">{{c.recordedAt | nxTime}}</span>
          <span class="n-aw-sq" aria-hidden="true">{{sym[c.type]}}</span>
          <span class="n-aw-change__text">
            <span class="n-aw-change__name"><span class="n-aw-code">{{c.code}}</span>{{c.name}}</span>
            <span class="n-aw-change__desc">{{describe(c)}}</span>
            <span class="n-aw-change__who">{{who(c)}}</span>
          </span>
        </button>
      </ng-container>
    </div>
  </section>

  <section class="n-aw-card n-aw-tree" aria-label="Tree at this moment">
    <div class="n-aw-tree__head">
      <div class="n-aw-tree__title">
        <h2>{{treeTitle}}</h2><span class="n-aw-ro">Read-only</span><span class="n-spacer"></span>
        <button type="button" class="n-aw-later" [attr.aria-pressed]="showLater" (click)="toggleLater()">
          <span class="n-aw-later__dot" aria-hidden="true"><svg viewBox="0 0 24 24"><path d="m5 12 5 5 9-10"/></svg></span>Show later changes</button>
      </div>
      <span class="n-aw-tree__sub">{{sinceText}}</span>
    </div>
    <div class="n-aw-tree__body" role="tree" [attr.aria-label]="treeTitle">
      <div *ngFor="let n of rows" role="treeitem" [attr.aria-level]="n.depth+1" [attr.aria-expanded]="n.nodeType==='LIMB' ? !!n.expanded : null"
           class="n-aw-node" [style.padding-left.px]="10 + 22*n.depth"
           [ngClass]="[n.now ? 'n-aw-node--now n-t-'+n.now.type : '', n.ghost ? 'n-aw-node--ghost' : '', n.ghost==='REMOVED_NOW' ? 'n-aw-node--removed' : '', n.status==='Inactive' ? 'n-aw-node--inactive' : '', n.depth===0 ? 'n-aw-node--root' : '']">
        <span class="n-aw-node__icon">
          <button *ngIf="n.nodeType==='LIMB'" type="button" class="n-aw-limb" [attr.aria-label]="(n.expanded ? 'Collapse ' : 'Expand ') + n.code" (click)="toggle(n)">{{n.expanded ? '−' : '+'}}</button>
          <span *ngIf="n.nodeType==='LEAF'" class="n-aw-leaf" aria-hidden="true"></span>
        </span>
        <span class="n-aw-node__code">{{n.code}}</span>
        <span class="n-aw-node__name">{{n.name}}<span *ngIf="n.status==='Inactive' && !n.ghost" class="n-aw-node__suffix"> · Inactive</span></span>
        <span *ngIf="n.now" class="n-aw-tag" [ngClass]="'n-t-'+n.now.type">{{n.now.text}}</span>
        <span *ngIf="showLater && n.later" class="n-aw-tag n-aw-tag--later" [ngClass]="'n-t-'+n.later.type">{{n.later.text}}</span>
      </div>
    </div>
  </section>
</div>
<app-draft-bar />
```

---

## 12. Mock data (use exactly this for demos and tests)

Context version **2026-11 · Working**. Now = **18 Nov 2026, 06:22**. Range min = 1 Oct 2026 00:00 (2026-10 has no change records → October strip shows "No changes this month").

Tree before any November change (ORGTL; ORGTP path `ORGTP / BUTCH / AMT / AMQNT`, AMBUS path `AMBUS / AMT / AMQNT`):
```
ORGTL Organization – Total Fidelity
└ OC005 Asset Management
  └ AM Asset Management Executive
    └ AMT Asset Management Technology
      └ AMQNT Quant Technology
        ├ AGCOE AM AI Center of Excellence     (limb, no children)
        └ AGQNT US Quant Technology
          ├ 31679 UK QNT Tech
          ├ 34295 QNT Engineering               (GL 0512)
          ├ 34299 QNT Leadership                (GL 0512)
          ├ 34301 QNT Analytics                 (Active)
          ├ 34307 QNT Quant Dev
          └ 34322 QNT Contingent                (Inactive)
```

Change records (ORGTL; local changes are ORGTL only, global ones apply to all three hierarchies):

| # | Recorded | Type | Code | Detail | By · Request |
|---|---|---|---|---|---|
| 1 | 04 Nov 11:20 | property | 34299 | Oracle GL company 0512 → 0610 | E2E Steward · CR-1033 |
| 2 | 09 Nov 14:20 | property | 34295 | Oracle GL company 0512 → 0444 | E2E Steward · CR-1035 |
| 3 | 12 Nov 16:05 | removed | 34322 | Removed from AGQNT | E2E Steward · CR-1036 |
| 4 | 13 Nov 10:31 | added | 99301 QNT Research | Added under AGQNT | u_editor1 · CR-1038 |
| 5 | 13 Nov 10:42 | moved | 34307 | Moved AGQNT → AGCOE | u_editor1 · CR-1038 |
| 6 | 13 Nov 10:48 | renamed | 34301 | “QNT Analytics” → “QNT Analytics Platform” | u_editor1 · CR-1038 |
| 7 | 13 Nov 10:52 | status | 34301 | Active → Inactive | u_editor1 · CR-1038 |
| 8 | 17 Nov 15:30 | added | 99302 AI Platform Ops | Added under AGCOE | u_editor1 · CR-1047 |

Demo opening moment: **13 Nov 2026, 10:30** (`/as-was?h=ORGTL&at=2026-11-13T10:30`). Expected results:

**A — 13 Nov, 10:30 (later changes on):**
- Strip: "8 changes this month"; bars on 4, 9, 12, 13 (13 selected, `#368727`, height 46px), 17; 19–30 future.
- Left: "Friday, 13 Nov [4]"; marker **Tree shows 10:30** at the top; then 10:31 99301, 10:42 34307, 10:48 34301 (name shown "QNT Analytics Platform"), 10:52 34301.
- Title "ORGTL on 13 Nov 2026, 10:30"; sub "… · 5 changes happened after this moment."
- Tree rows: ORGTL, OC005, AM, AMT, AMQNT, AGCOE, ┆99302 AI Platform Ops┆ `Added 17 Nov`, AGQNT, 31679, 34295, 34299, 34301 QNT Analytics `Renamed 13 Nov`, 34307 QNT Quant Dev `Moves to AGCOE · 13 Nov`, ┆99301 QNT Research┆ `Added 13 Nov`. **34322 is absent** (removed 12 Nov).

**B — click 10:42 row:**
- Marker below 10:42 ("Tree shows 10:42"); 10:42 row tinted.
- Title "ORGTL on 13 Nov 2026, 10:42"; "3 changes happened after this moment."
- AGCOE now contains 34307 (blue tint, solid `Moved in · 10:42`) and ghost 99302; AGQNT contains 31679, 34295, 34299, 34301 (`Renamed 13 Nov`), 99301 (normal row).

**C — click day 12:**
- Moment "12 Nov 2026, start of day"; left "Thursday, 12 Nov [1]", marker at top, then 16:05 34322.
- Tree: 34322 QNT Contingent · Inactive `Removed 12 Nov`; 34301 `Renamed 13 Nov`; 34307 `Moves to AGCOE · 13 Nov`; ghosts 99301 `Added 13 Nov` (under AGQNT) and 99302 `Added 17 Nov` (under AGCOE); 34295 has **no** tag (its GL change on 9 Nov is already applied); "6 changes happened after this moment."

**D — click a 16:05 row on 12 Nov:** ghost row 34322 struck through under AGQNT with solid `Removed · 16:05`.

**E — Now:** "Wednesday, 18 Nov [0]", "No changes on this day.", title "ORGTL now", sub "Nothing has changed since — this is how it looks now.", Now button green; AGCOE: 34307, 99302; AGQNT: 31679, 34295, 34299, 34301 QNT Analytics Platform · Inactive, 99301; no tags.

**F — AMBUS at Now:** rows AMBUS, AMT, AMQNT, AGCOE (no children — local moves/adds were ORGTL only), AGQNT, 31679, 34295, 34299, 34301 QNT Analytics Platform · Inactive, 34307, 34322 · Inactive.

---

## 13. Accessibility
- One `h1` (As-Was). Month label, day title and tree title are `h2`.
- All inputs have visible labels; icon-only buttons have `aria-label`.
- Day cells: `aria-pressed`, `aria-disabled` for future, descriptive `aria-label`.
- Marker: `role="separator"` with its text as `aria-label`.
- Tree: `role="tree"` / `treeitem` with `aria-level`, `aria-expanded`; expand squares are real buttons.
- Change type is never colour-only: rows carry the symbol and a verb; tags carry text.
- Contrast ≥ 4.5:1 for all text (future day numbers `#9E9E9E` are disabled and exempt).

---

## 14. Do NOT
- Don't draw a line chart, drag handle, "Now" flag, hatched future area or zoom.
- Don't add type-filter pills (Added/Moved/…/Attribute) — the day list is short enough; Compare filters by type.
- Don't show a banner "You are viewing … not today / as recorded at …" on this page; the tree title says the moment.
- Don't show internal ids (hashes) instead of node codes; don't open the tree collapsed at the root.
- Don't include drafts, pending requests or sandbox changes.
- Don't add edit actions; don't change the context-bar version/as-of/workspace from here.
- Don't truncate names or descriptions with ellipsis — wrap.
- Don't invent extra tag texts, colours or legends.

---

## 15. Acceptance checklist
- [ ] Shell identical to other screens; As-Was active in the rail.
- [ ] Toolbar: h1, Hierarchy select, "Show it as it was on" datetime pill, Now (green when active / "Latest" for Finalized), Compare with now link-button.
- [ ] Day strip: month nav with range-limited arrows, count text, one cell per day with bar heights/colours per §6.2, selected day tinted, today bold, future disabled.
- [ ] Day click → start of day (today → Now); arrows don't change the moment.
- [ ] Left list: weekday title + count, ascending changes with time/type square/code+name/description/who·request, moment marker positioned per §7.1, row click sets the moment, current row tinted.
- [ ] Tree: title per §7.2, Read-only pill, Show later changes toggle (default on, remembered), sub line count, ancestor chain + focus parents expanded, limbs expandable.
- [ ] Solid tags/tint for a change at the moment (incl. removed ghost); dashed tags and later-added ghosts only when the toggle is on.
- [ ] Inactive names muted with " · Inactive"; names/status/placement as at the moment.
- [ ] URL `h`, `at`, `later` round-trip; context bar untouched.
- [ ] Loading skeletons after 150 ms per area; per-area error with Try again.
- [ ] Mock data reproduces §12 A–F exactly.
- [ ] Pixel check vs. the "As-Was (new)" board at 1440px: computed styles match §5–§7 and §10.
