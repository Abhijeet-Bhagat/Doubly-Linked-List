# Hierarchies screen — Build Specification (features + exact styles)

> **Give this file to the LLM/developer together with `nexus-design-system.md` and a screenshot of the canvas board "Hierarchies (new · tidy top)"** (`Hierarchies-C`). The shell (header `#044014`, context bar, collapsible sidebar, draft bar) comes from the design system. This file defines **every feature, rule, state, string, size and colour** of the Hierarchies page, plus a CSS block (§12) to **append verbatim** to `src/styles.css` after `nexus-ui.css`. Component `.scss` files may contain layout only.
>
> **Precedence:** this file > the screenshot > your own judgement. If something is not described here, **do not add it** (no extra buttons, chips, legends, tooltips, helper paragraphs, animations or colours). If a rule seems missing, leave the behaviour out and raise it as a question instead of inventing one.
>
> **Replaces** the previous Hierarchies spec and fixes the current `/hierarchies` page clutter: five hierarchy tabs of different widths across the top, search + "Recent" chips inside the tree card, a Limb/Leaf/Draft legend and the "Hide inactive" switch in the tree footer, and six action buttons (Edit details · Move to… · Add child · Place under another parent · Remove from hierarchy · Delete node) wrapping over two lines.

---

## 0. Glossary (use these words exactly in the UI)

| Term | Meaning |
|---|---|
| Hierarchy | One tree in the version (ORGTL primary; ORGTP, AMBUS alternates; E2E_H, E2E_MGMT as loaded). |
| Node | A member. **Limb** = can have children (drawn as a **square**). **Leaf** = no children (drawn as a **circle**). |
| Global / local | Name, description, status and properties are **global** (same in every hierarchy). Position (parent) is **local** to one hierarchy. |
| Draft request | The user's pending changes. Edits are **never applied directly**; they are added to the draft and take effect only after governance approval ("Review and submit"). |
| Inactivate / Remove / Delete | Inactivate = keep in place, flag inactive (most common). Remove = take out of **this** hierarchy only. Delete = take out of **all** hierarchies. |

Copy style: sentence case. Never show "Place under another parent", "Remove from hierarchy", "Delete node" as separate buttons (they live in **More ▾**, §7.4).

---

## 1. Purpose

1. **Find** a node fast (search by code or name; expand straight to it).
2. **See where it sits** (breadcrumb, level, parent, children, position in every hierarchy).
3. **Understand it** (properties with their source, history, ownership).
4. **Propose changes** (edit, move, add child, add to another hierarchy, inactivate/remove/delete) → all go to the draft request.

Route: `/hierarchies/:code?node={nodeCode}` (e.g. `/hierarchies/ORGTL?node=34307`). Selecting a node updates `?node=` with `replaceState`. Sidebar item **Hierarchies** has `aria-current="page"`.

Covers the Shape the Tree demo steps for search + breadcrumbs (step 2), alternate hierarchies (34307: level 7 ORGTL, 6 ORGTP, 5 AMBUS), and core edits (add leaf, move, rename, inactivate).

Design rules:
- **One toolbar row** at the top (title · hierarchy · search), identical in pattern to Compare / As-Was / Queries.
- Master–detail: tree card left, node card right; only the tree list and the tab panel scroll.
- **One action row** on the node card: three visible actions + **More ▾**.
- Names and values **wrap**; never truncate with ellipsis.

---

## 2. Domain rules

- **Version & workspace:** everything shows the context-bar version, as-of date and workspace. Edits only in a **Working** version, at **today**, in Live data or a sandbox. Finalized version, a past as-of date, or VIEWER role → **read-only** (§7.6).
- **Draft-applied view:** the tree and node card show the user's draft applied (a moved node appears under its new parent, a renamed node shows its new name), each marked **DRAFT**.
- **Integrity rules** (checked when an item is added to the draft, again on approval): `CODE_UNIQUE`, `PARENT_EXISTS`, `PARENT_IS_LIMB`, `NO_CYCLES`. Failures are shown inside the open panel as `{RULE} · {message}`; nothing is added.
- **Global vs local:** edit details / status / properties are global (DRAFT flag in every hierarchy); move / add child / remove are local (DRAFT flag only in that hierarchy); delete affects all hierarchies.
- **Add to another hierarchy** places the **same node** (same code, same global data) under a parent in another hierarchy where it is not yet placed.
- Approval writes change records (who, when, before, after, approver) used by Compare, As-Was and Audit.

---

## 3. Layout (1440 × 980 at design time)

```
┌ .n-header (#044014) [☰] LOGO Nexus Master Data Central ………………………… DEVELOPMENT (ES E2E Steward · e2e_steward Switch) ┐
├ .n-context  Version [2026-11 · Working ▾]  As of [..]  Work in (Live data|Sandbox) ……………………………………………… Manage versions ┤
├ rail ┬ .n-main ─────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ ⌂    │ Hierarchies  Hierarchy (ORGTL · Total Fidelity ▾) Primary · 2,818 nodes · 14 levels ……… (🔍 Search ORGTL by code or name) │
│ ⊞ ■  │ ┌ Tree 520 ──────────── Hide inactive ◯┐ ┌ ORGTL / OC005 / AM / AMT / AMQNT / AGCOE / 34307 ────────────────┐ │
│ ⌕    │ │ □ ORGTL Total Fidelity                │ │ (○) QNT Quant Dev                                      [Active]   │ │
│ ⇄    │ │ │ □ OC005 OC005                       │ │     34307 · Leaf · cost centre · Quant development             │ │
│ ◷    │ │ │ │ □ AM Asset Management             │ │ ▲ In your draft: Move 34307 · AGQNT → AGCOE in ORGTL     Undo │ │
│ ───  │ │ │ │ │ □ AMT Asset Management Tech…    │ │ (✎ Edit details) (→ Move…) (More ▾)                            │ │
│ ☰    │ │ │ │ │ │ □ AMQNT Quant Technology      │ ├───────────────────────────────────────────────────────────────────┤ │
│ ◫    │ │ │ │ │ │ │ □ AGCOE AM AI Center…       │ │ PARENT IN ORGTL │ CHILDREN │ LEVEL IN ORGTL │ IN HIERARCHIES     │ │
│ ≡    │ │ │ │ │ │ │ │ ○ 34307 QNT Quant Dev ▲DRAFT│ ├───────────────────────────────────────────────────────────────────┤ │
│ ───  │ │ │ │ │ │ │ □ AGQNT US Quant Technology │ │ Properties | Relationships | History (1) | Ownership             │ │
│ ⧉    │ │ │ │ │ │ │ │ ○ 34301 QNT Analytics Pl… ▲DRAFT │ [Filter properties] ◯ Hide empty                        │ │
│ ⚙    │ │                                        │ │ Property │ Value │ Source │ (action)  … (scrolls)             │ │
│      │ └────────────────────────────────────────┘ └───────────────────────────────────────────────────────────────────┘ │
│      ├ .n-draftbar (2) Your draft request · 2 changes … View changes  (Discard) [Review and submit] ───────────────────┤
└──────┴─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

- Root `.n-app.n-app--fixed` (100vh, min-height 900px, no page scroll).
- `.n-main` column: **toolbar** → **body** (flex 1, min-height 0) → **draft bar**.
- Body (`.n-hy`): flex row, `gap 16px`, `padding 16px 20px`, `align-items: stretch`.
- **Tree card**: `width 520px; flex 0 0 520px` with the 64px rail; **440px** when the sidebar is expanded (216px).
- **Node card**: `flex 1 1 auto; min-width 0`.
- Below 1100px content width: the cards stack (tree 420px tall, then node card) and the body scrolls. (< 900px viewport: out of scope.)

---

## 4. Toolbar

Container: `flex 0 0 auto; display flex; flex-wrap wrap; align-items center; gap 12px 20px; padding 16px 20px 0`.

Left → right:
1. `h1` **Hierarchies** — 20px / 700, letter-spacing −0.01em, margin 0. (The node name in the node card is an `h2`.)
2. Field group (flex, centred, `gap 8px`):
   - Label **Hierarchy** (`<label for="h-sel">`, 12px / 600, `#5C5C5C`).
   - Pill select: height 40px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 36px 0 14px`, 13px / 600, `#1F1F1F` on white, `appearance none`; chevron 12×12 stroke `#1F1F1F` width 2.4, path `m6 9 6 6 6-6`, `position absolute; right 14px; top 14px`. Options `{CODE} · {top node name}` in this order: primary first, then alternates in load order: `ORGTL · Total Fidelity`, `ORGTP · Total Fidelity Technology`, `AMBUS · Asset Management BU Alternate`, then any others the user can see (e.g. `E2E_H · E2E Legal Group`, `E2E_MGMT · E2E Management View`).
   - Meta (12px, `#5C5C5C`, nowrap): **`{Primary|Alternate} · {n} nodes · {d} levels`** — e.g. `Primary · 2,818 nodes · 14 levels`, `Alternate · 516 nodes · 12 levels`, `Alternate · 1,501 nodes · 12 levels`.
3. Spacer.
4. **Search** (wrapper `position relative; flex 0 1 380px; min-width 260px`):
   - Visually hidden label `Search in this hierarchy`.
   - Icon 18×18 stroke `#3D3D3D` width 2 (`<circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>`), `position absolute; left 14px; top 11px`.
   - Input `type="search"`: width 100%, height 40px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 16px 0 40px`, 13px, placeholder **`Search {HIER} by code or name`**.
   - Starts searching at 2 characters (debounce 150 ms); matches code (contains) or name (contains), case-insensitive, within the current hierarchy; up to 6 results.
   - **Results popover**: `position absolute; top 48px; left 0; right 0; min-width 380px;` white, `border 1px solid #D9D9D9`, radius 16px, shadow `0 12px 32px rgba(0,0,0,.14)`, `padding 8px`, z-index 40.
     - Heading (11px / 700, letter-spacing .06em, `#5C5C5C`, `padding 6px 10px`): **`{n} MATCHES IN {HIER}`** / `1 MATCH IN {HIER}`.
     - Result button: flex, `gap 12px`, width 100%, `padding 10px`, radius 10px, `min-height 52px`, left-aligned, hover `#F2F2F2`: marker (limb 10×10 square `border 1.5px solid #3D3D3D`; leaf 8×8 circle `border 1.5px solid #6B6B6B`) · column `gap 2px`: line 1 name 14px / 600 + space + code JetBrains Mono 12px / 400 `#5C5C5C`; line 2 full path JetBrains Mono 11px `#5C5C5C` joined with ` / ` (wraps).
     - No match: `Nothing in {HIER} matches.` (13px, `#5C5C5C`, `padding 12px`).
     - Choosing a result: select the node, expand all its ancestors, scroll it into view (centre), clear the query, close the popover. Up/Down move through results, Enter selects, Esc clears.

---

## 5. Tree card (left) — `section[aria-label="Tree"]`

Card: white, `border 1px solid #D9D9D9`, radius 16px, column, overflow hidden.

### 5.1 Header
`padding 14px 16px 12px; border-bottom 1px solid #ECECEC;` flex, centred, `gap 10px`:
- `h2` **Tree** (15px / 700, margin 0) · spacer ·
- **Hide inactive** switch — `button[role=switch][aria-checked]`: flex, `gap 8px`, transparent, border 0, height 32px, 13px `#1F1F1F`. Track 32×18, radius 999px, bg on `#368727` / off `#BDBDBD`; knob 14×14 white, shadow `0 1px 2px rgba(0,0,0,.2)`, `top 2px`, `left` 16px on / 2px off. Default **off**; remembered per user (`nexus.hideInactive`). When on, inactive nodes are hidden except the selected node.

(No search, no "Recent" chips, no legend, no footer in this card.)

### 5.2 Tree list
`flex 1; min-height 0; overflow auto; padding 8px 10px 12px`; `role="tree"`, `aria-label="{HIER}"`. Virtual scroll with variable row height (min 40px).

**Row** (`role="treeitem"`, `aria-level`, `aria-expanded` for limbs, `aria-selected`): flex, `align-items stretch`, `min-height 40px`, radius 8px; bg selected `#E3F0DE`, hover `#F2F2F2`, else transparent.
1. **Guides**: one 16px column per depth level, each with a centred 1px line `#D4D4D4`.
2. **Toggle / marker** (28px column):
   - Limb with children: `<button aria-label="Expand {code}|Collapse {code}" aria-expanded>` containing a 13×13 square, `border 1.5px solid #3D3D3D`, white, JetBrains Mono 11px `+` / `−`, line-height 1.
   - Leaf, or limb without children: 8×8 circle (leaf) / 9×9 square (empty limb), `border 1.5px solid #6B6B6B` (inactive: `#9E9E9E`), not interactive.
3. **Body** `<button>` (flex 1, flex, centred, `gap 10px`, `padding 8px`, transparent, left-aligned, `title="{code} · {name}"`):
   - Code: JetBrains Mono 12px, `flex 0 0 48px`; colour `#2E6B21` when selected, else `#5C5C5C`.
   - Name: 13px, line-height 1.35, **wraps** (`overflow-wrap anywhere`); weight 700 selected / 500 otherwise; colour `#1F1F1F`, inactive `#6B6B6B`; struck through when the draft removes/deletes it.
   - Flags (nowrap):
     - **DRAFT** (node touched by the draft, §2): 10px / 700, letter-spacing .05em, `#7A4100`, preceded by an 8px-tall orange triangle (`border-bottom 8px solid #C77700`, sides 4px transparent), gap 5px, `title="Pending in your draft request"`.
     - **INACTIVE**: 10px / 700, letter-spacing .05em, `#5C5C5C`.
   - Child count (collapsed limbs only): number, 11px `#6B6B6B` (e.g. `8`).
- Body click selects (updates the node card and `?node=`); toggle click expands/collapses (lazy-loads children). Keyboard: Up/Down move, Right expands / goes to first child, Left collapses / goes to parent, Enter selects.
- With 16px guides a level-14 leaf still leaves ≥ 220px for its name in the 520px card.

---

## 6. Node card (right) — header — `section[aria-label="Node details"]`

Card: white, `border 1px solid #D9D9D9`, radius 16px, column, overflow hidden.

Header block: `padding 18px 24px 16px; border-bottom 1px solid #ECECEC;` column, `gap 12px`.

### 6.1 Breadcrumb
`nav[aria-label="Breadcrumb"]`, flex wrap. Each ancestor: `<button>` transparent, `padding 4px 3px`, JetBrains Mono 12px, weight 500, `#2E6B21`, underlined (offset 3px) — click selects it. Current node: same font, weight 700, `#1F1F1F`, no underline, not clickable (`aria-current="page"`). Separator `/` (12px, `#9E9E9E`, `padding 0 4px`, `aria-hidden`).

### 6.2 Title row
Flex, `align-items flex-start`, `gap 14px`, wraps:
- Icon tile 44×44, radius 12px, bg `#EEF5EB`; limb = 15×15 square `border 2px solid #368727`; leaf = 13×13 circle `border 2px solid #368727`.
- Column (`flex 1 1 280px`, `gap 4px`): `h2` **{name}** (24px / 700, line-height 1.2, letter-spacing −0.01em, wraps) + meta line (flex wrap, `gap 8px`, 13px `#5C5C5C`): code (JetBrains Mono 600 `#2E6B21`) · `·` · type (`Limb` / `Leaf · cost centre`) · `·` · description.
- Status pill (right): 12px / 600, `padding 4px 12px`, radius 999px — Active `#1F4D16` on `#E3F0DE`; Inactive `#4A4A4A` on `#ECECEC`.

### 6.3 "In your draft" note (only when the draft touches this node)
Flex, centred, `gap 10px`, bg `#FFF8EE`, `border 1px solid #F3E2C6`, radius 12px, `padding 10px 14px`, 13px `#5A3500`: orange triangle (9px tall, `#C77700`) · text **In your draft:** (700) + the draft item texts joined with ` · ` (§9.3) · **Undo** (text button 13px / 600, `#2E6B21`, underlined, height 32px) — removes those draft items.

### 6.4 Action row
Flex wrap, `gap 8px`, centred. All buttons: inline-flex, `gap 8px`, height 40px, radius 999px, 13px / 600, icon 15×15 stroke 2.

| # | Button | Style | Shown | Opens |
|---|---|---|---|---|
| 1 | ✎ **Edit details** (icon `M4 20h4L19 9l-4-4L4 16v4z` + `m14 6 4 4`) | `border 1.5px solid #368727`; off: white bg, `#1F4D16` text; open (`aria-pressed=true`): bg `#368727`, white | always | §7.1 |
| 2 | → **Move…** (icon `M5 12h14` + `m13 6 6 6-6 6`) | `border 1.5px solid #BDBDBD`; off white / `#1F1F1F`; open: bg `#1F1F1F`, white | always (not on the top node) | §7.2 |
| 3 | + **Add child…** (icon 14×14 stroke 2.4 `M12 5v14M5 12h14`) | as Move | **limbs only** | §7.3 |
| 4 | **More ▾** (label + chevron 12×12 stroke 2.4 `m6 9 6 6 6-6`) | `border 1.5px solid #BDBDBD`, `padding 0 14px`, `gap 6px`; bg `#F2F2F2` while open | always | menu §6.5 |

Padding: buttons 1–3 `0 16px`. One panel open at a time; clicking the open button again closes it; selecting another node closes it.

### 6.5 More menu
`div[role=menu][aria-label="More actions"]`: `position absolute; left 0; top 46px; width 260px;` white, `border 1px solid #D9D9D9`, radius 12px, shadow `0 12px 32px rgba(0,0,0,.14)`, `padding 6px`, z-index 30. Items `button[role=menuitem]`: flex, `gap 10px`, width 100%, `min-height 40px`, `padding 0 12px`, radius 8px, transparent (hover `#F2F2F2`), 13px, left-aligned, icon 15×15 stroke 2:
1. **Add to another hierarchy…** (`#1F1F1F`; hierarchy icon `M10 3h4v5h-4zM4 16h4v5H4zM16 16h4v5h-4zM12 8v4M6 16v-4h12v4`) → §7.4.
2. Divider: 1px `#ECECEC`, `margin 4px 6px`.
3. **Inactivate or remove…** (active node) / **Reactivate or remove…** (inactive node) — 600, `#B42318`, trash icon `M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13` → §7.5.
- Opens on click; closes on item choice, Esc, or click outside. Up/Down move between items; focus returns to **More** on close.

---

## 7. Action panels (inline, under the action row, inside the header block)

Common panel shell (Edit, Move, Add, Place): `border 1.5px solid #368727; border-radius 14px;` Remove panel: `border 1.5px solid #C77700; background #FFF8EE`. Panel buttons: **Cancel** / **Keep as is** = secondary (height 40px, `padding 0 16px`, `border 1.5px solid #BDBDBD`, white, 13px / 600); confirm = primary (height 40px, `padding 0 18px`, border 0, bg `#368727`, white, 13px / 600) unless stated. Every confirm **adds to the draft**, closes the panel, and shows the draft-bar toast (§9.2).

### 7.1 Edit details
`padding 14px`, column `gap 12px`.
- Title (13px / 700): **`Edit details · name changes apply in {list of hierarchies the node is in}`** (e.g. `ORGTL, ORGTP and AMBUS`).
- Fields (wrap, `gap 12px`, each `flex 1 1 260px`, label 12px / 600 `#5C5C5C`, gap 6px):
  - **Name · {n} / 60** — input height 40px, `border 1.5px solid #BDBDBD`, radius 10px, `padding 0 12px`, 14px, maxlength 60, prefilled.
  - **Description** — same input, prefilled.
- Buttons right-aligned `gap 8px`: Cancel · **Add to draft**. Unchanged values or empty name → closes without adding.
- Draft item: `Edit {code} · name “{new name}”` (and/or `· description “…”`).

### 7.2 Move…
Panel `overflow hidden`:
- Head (`padding 12px 14px; background #F2F8EF;` flex): **`Pick a new parent in {HIER}`** (13px / 700, flex 1) + **Cancel** (text 13px / 600 `#2E6B21`, height 32px).
- List (`max-height 220px; overflow auto; padding 6px`) of every limb in the hierarchy, tree order:
  - Valid target `<button>`: flex, `gap 10px`, width 100%, `min-height 44px`, `padding 0 10px`, radius 10px, hover `#F2F2F2`: 10×10 square `border 1.5px solid #3D3D3D` · code (JetBrains Mono 12px `#2E6B21`, min-width 52px) · name (14px, flex 1, wraps) · `Level {n}` (12px `#5C5C5C`). Click = add the move.
  - Blocked (not a button; colour `#6B6B6B`; square border `#BDBDBD`) with reason chip (11px / 600, `#4A4A4A` on `#ECECEC`, `padding 3px 10px`, radius 999px): **`This node`** · **`Inside this node`** (NO_CYCLES) · **`Current parent`**.
- Foot (`padding 10px 14px; border-top 1px solid #E6EFE2;` 12px `#4A4A4A`): **`Only limbs can be parents, and a node can't move inside itself. Other hierarchies keep their own position.`**
- Draft item: `Move {code} · {old parent} → {new parent} in {HIER}`.

### 7.3 Add child…
`padding 14px`, column `gap 12px`.
- Title (13px / 700): **`Add a child under {code} in {HIER}`**.
- Row (wrap, `gap 12px`, `align-items flex-end`):
  - **Code** (width 120px): input height 40px, radius 10px, JetBrains Mono 13px, upper-case, maxlength 5, placeholder `99302`.
  - **Name** (`flex 1 1 220px`): input height 40px, radius 10px, 14px, maxlength 60, placeholder `e.g. QNT Platform Ops`.
  - **Type**: segmented (`role=group aria-label="Node type"`; track `#F2F2F2`, radius 999px, `padding 3px`, `gap 2px`; buttons height 34px, `padding 0 12px`, 13px / 600, radius 999px; selected white with shadow `0 1px 2px rgba(0,0,0,.14)`): ○ **Leaf** (default) · □ **Limb**.
- Errors (12px / 600, `#B42318`; field border `#B42318`): **`Enter a code and a name.`** · **`CODE_UNIQUE · {CODE} already exists in this version.`**
- Buttons: Cancel · **Add to draft**.
- Draft item: `Add {CODE} “{name}” under {parent} in {HIER}`.

### 7.4 Add to another hierarchy… (from More)
Panel `overflow hidden`:
- Head (`padding 12px 14px; background #F2F8EF`): **`Place {code} in another hierarchy (same node, new position)`** (13px / 700) + **Close** (text button).
- One row per other hierarchy (`padding 0 10px`, `min-height 44px`, flex `gap 10px`): code (JetBrains Mono 13px / 600, min-width 60px) · text (13px `#4A4A4A`, flex 1): current path, or **`Not placed yet — pick a parent in {HIER}`** · chip (style as §7.2 reason chip): **`Already placed · Level {n}`** or **`Available`**. Clicking an **Available** row replaces the list with the §7.2 parent picker for that hierarchy; picking a parent adds the item.
- Foot (12px `#4A4A4A`): **`A node exists once per version; placing it in another hierarchy reuses it, so its name and global properties stay in sync.`**
- Draft item: `Place {code} under {parent} in {HIER}`.

### 7.5 Inactivate or remove… (from More)
`padding 14px`, column `gap 10px`, warn styling (above).
- Title (14px / 700): **`What should happen to {code} {name}?`**
- `div[role=radiogroup][aria-label="How to remove"]` of three option cards (`button[role=radio][aria-checked]`: flex `gap 12px`, width 100%, white, `border 1.5px solid` (selected `#1F1F1F`, else `#E6E6E6`), radius 12px, `padding 12px 14px`, left-aligned). Radio dot: 18×18 circle `border 2px solid` (selected `#1F1F1F` / `#BDBDBD`), inner 8×8 dot `#1F1F1F` when selected. Label 14px / 600; impact 13px `#4A4A4A`, line-height 1.45:
  1. **Inactivate** + chip **Most common** (11px / 700, `#1F4D16` on `#E3F0DE`, `padding 2px 8px`, radius 999px) — `Keeps the node in place in {hierarchies}. It is flagged inactive and can be filtered out of exports; {n} active nodes below it stay as they are.` (the `; {n} …` part only when it has active descendants). Inactive node: **Reactivate** — `Makes the node active again in every hierarchy.` (no chip). Default selected.
  2. **Remove from {HIER} only** — `Takes it out of {HIER}. It stays in the version and in {other hierarchies}.` + ` {n} active nodes below it move out with it.` when applicable.
  3. **Delete from all hierarchies** — `Removes it from {hierarchies}. Its change history is kept for audit.`
- Buttons right-aligned: **Keep as is** · confirm: Inactivate → `Add inactivation to draft` (bg `#1F1F1F`); Reactivate → `Add reactivation to draft` (`#1F1F1F`); Remove → `Add removal to draft` (`#1F1F1F`); Delete → `Add delete to draft` (bg `#B42318`).
- Draft items: `Inactivate {code} {name}` · `Reactivate {code} {name}` · `Remove {code} from {HIER}` · `Delete {code} from all hierarchies`.

### 7.6 Read-only
Finalized version, past as-of date, or VIEWER role: all four action buttons `disabled` (opacity .5, `cursor not-allowed`), More menu never opens, property Edit/Override/Set buttons hidden, and the header shows one line under the title row: **`Read-only — {reason}.`** (13px, `#5C5C5C`) where reason is `this version is finalized` / `you're viewing a past date` / `your role can view only`.

---

## 8. Node card — facts and tabs

### 8.1 Fact strip
`display grid; grid-template-columns: repeat(4, minmax(0,1fr)); border-bottom 1px solid #ECECEC`. Cell: `padding 12px 24px`, `border-right 1px solid #ECECEC` (last: none), column `gap 3px`, min-width 0. Label: 11px / 600, letter-spacing .06em, `#5C5C5C`, upper case. Value: 14px / 600, **wraps**.

| Label | Value |
|---|---|
| `PARENT IN {HIER}` | link button (`#2E6B21`, underlined, offset 3px) **`{code} · {name}`** → selects the parent; top node: plain **`Top node`** |
| `CHILDREN` | limb: **`{n} · {m} inactive`**, or **`None yet`**; leaf: **`None · leaf node`** |
| `LEVEL IN {HIER}` | **`Level {n} of {depth}`** |
| `IN HIERARCHIES` | **`{n} of {total}`** |

### 8.2 Tabs
`div[role=tablist][aria-label="Node information"]`: flex, `gap 4px`, `padding 0 20px`, `border-bottom 1px solid #ECECEC`. Tab `button[role=tab][aria-selected]`: transparent, `border-bottom 2px solid` (selected `#368727`, else transparent), `padding 0 12px`, height 46px, 14px, weight 700 / 500, colour `#1F1F1F` / `#5C5C5C`. Order: **Properties** (default) · **Relationships** · **History** (count chip = this node's draft items: 11px / 700, `#3D3D3D` on `#EEEEEE`, `padding 1px 7px`, radius 999px; hidden at 0) · **Ownership**. Arrow keys move between tabs. Tab choice persists while changing nodes.

Panel `role=tabpanel`: `flex 1; min-height 0; overflow auto; padding 16px 24px 24px`.

### 8.3 Properties tab
- Toolbar (wrap, `gap 10px`, `margin-bottom 12px`): filter input (`flex 0 1 300px`, height 36px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 14px`, 13px, placeholder **`Filter properties`**, hidden label same) · **Hide empty** switch (track 32×18 as §5.1, label 13px).
- Table box: `border 1px solid #E6E6E6`, radius 12px, overflow hidden. Grid columns `minmax(0,1.2fr) minmax(0,1fr) minmax(0,1fr) 88px`.
  - Header row: bg `#FAFAF8`, `border-bottom 1px solid #ECECEC`, 12px / 600 `#5C5C5C`: **Property** (`padding 10px 16px`) · **Value** · **Source** · (empty) (`padding 10px 12px`).
  - Category row: `padding 10px 16px 6px`, 11px / 700, letter-spacing .06em, `#6B6B6B`, `border-top 1px solid #F0F0F0`: **FINANCE**, **HR**, **REPORTING** (categories the user can't see are omitted).
  - Property row: `min-height 52px`, `border-top 1px solid #F2F2F2`, centred; bg `#FFFCF6` when the value is pending in the draft.
    - Property cell (`padding 8px 16px`, column `gap 2px`): label 14px / 500 + key JetBrains Mono 11px `#6B6B6B`.
    - Value cell (`padding 8px 12px`, 13px): value in JetBrains Mono / 600 / `#1F1F1F`; empty → **`Not set`** in Inter / 400 / `#6B6B6B`.
    - Source cell: pill (12px / 600, `padding 3px 10px`, radius 999px, nowrap):

| Origin | Pill text | Text | Bg | Action |
|---|---|---|---|---|
| Set on this node | `Set on this node` | `#1F4D16` | `#E3F0DE` | **Edit** |
| Inherited | `Inherited · {ancestor code}` | `#1D5FA8` | `#E4EEFA` | **Override** |
| Default | `Default` | `#4A4A4A` | `#ECECEC` | **Override** |
| Derived | `Derived` / `Derived from code` | `#6B3FA0` | `#EFE7F8` | `Derived` (disabled, `#9E9E9E`) |
| No value | `Not set` | `#4A4A4A` | `#F2F2F2` | **Set** |
| Pending in draft | `In your draft` | `#7A4100` | `#FFF1DE` | **Edit** |

    - Action cell (right-aligned, `padding 8px 12px`): text button 13px / 600 `#2E6B21`, height 32px.
  - **Inline edit**: Value + Source + Action cells merge (`grid-column span 3`, `padding 6px 12px`, flex `gap 6px`): input (flex 1, height 36px, `border 2px solid #368727`, radius 10px, `padding 0 12px`, JetBrains Mono 13px, hidden label `New value`) · **Cancel** (36px, `padding 0 14px`, secondary, 12px / 600) · **Add to draft** (36px, primary, 12px / 600). Enter = add, Esc = cancel. Draft item: `Set {Property label} = {value} on {code}`.
- Note (12px `#5C5C5C`, `margin-top 10px`): **`Inherited values come from the nearest ancestor in ORGTL (the controlling hierarchy). Overriding a value applies to this node and the nodes that inherit from it.`**

### 8.4 Relationships tab
Two columns (flex wrap, `gap 24px`, `align-items flex-start`):
- **Left** (`flex 1 1 340px`, column `gap 10px`): `h3` **Position in every hierarchy** (15px / 700) + sub (13px `#5C5C5C`, line-height 1.5): **`One global node: a rename shows everywhere. Moves apply to one hierarchy only.`** + one `<button>` per hierarchy (column `gap 6px`, `padding 12px 14px`, radius 12px, `border 1px solid` — current: border `#B9D9AE`, bg `#F4FAF1`, `aria-current="true"`; others `#E6E6E6` / white): row 1 (flex `gap 8px`): code 13px / 700 · role 12px `#5C5C5C` · spacer · level chip (12px / 600, `#2E6B21` on `#EEF5EB`, `padding 3px 10px`, radius 999px) **`Level {n}`** or **`Not placed`**; row 2: path JetBrains Mono 12px `#3D3D3D`, line-height 1.6, wraps, or `Not placed in {HIER}`. Click switches the hierarchy (keeps the node selected).
- **Right** (`flex 1 1 300px`): `h3` **Children in {HIER}** + bordered list (`border 1px solid #E6E6E6`, radius 12px, `padding 6px`) of child buttons (flex `gap 10px`, `min-height 40px`, `padding 0 10px`, radius 10px; marker; code JetBrains Mono 12px `#2E6B21` min-width 48px; name 13px flex 1 (inactive `#6B6B6B`); flag `INACTIVE` 11px / 700 `#5C5C5C`). Click selects. Empty (dashed box `border 1.5px dashed #D4D4D4`, radius 12px, `padding 14px`, 13px `#5C5C5C`): **`No children yet. Use “Add child…” to create one.`** / **`Leaf nodes can’t have children.`**

### 8.5 History tab
List; each entry: flex `gap 14px`, `padding 12px 0`, `border-top 1px solid #EEEEEE`:
- Dot 28×28 circle, type tint bg + type colour symbol (11px / 700): move `→` `#1D5FA8`/`#E4EEFA` · edit `Aa` `#6B3FA0`/`#EFE7F8` · property/add `=`/`+` `#2E6B21`/`#E3F0DE` · status `▲` `#7A4100`/`#FFF1DE` · remove `−` `#3D3D3D`/`#E6E6E6` · delete `×` `#B42318`/`#FDECEA` · system `•` `#3D3D3D`/`#F2F2F2`.
- Text (14px / 600) + meta (12px `#5C5C5C`).
- Pending items first, with pill **IN YOUR DRAFT** (11px / 700, `#7A4100` on `#FFF1DE`, `padding 3px 10px`, radius 999px, right) and meta `{user} · not yet submitted`; then approved changes newest first (meta `{user} · {d Mon yyyy, HH:mm} · approved by {approver}`); last `Copied from {previous version} baseline` (`system · {date}`).
- Footer link (13px / 600, `padding-top 12px`, `border-top 1px solid #EEEEEE`): **`Open full audit for {code} →`** → Audit filtered to the node.

### 8.6 Ownership tab
Grid 2 columns, `border 1px solid #E6E6E6`, radius 12px, overflow hidden; cell `padding 14px 16px`, `border-bottom 1px solid #F0F0F0`, `border-right 1px solid #F0F0F0` (right column none); label 11px / 600 letter-spacing .06em `#5C5C5C` upper case; value 14px / 600; note 12px `#5C5C5C`:

| Label | Value | Note |
|---|---|---|
| DATA STEWARD | E2E Steward | e2e_steward |
| OWNING TEAM | Finance Reference Data | MDC Business Ops |
| APPROVER GROUP | Finance Approvers | u_approver1 + 2 others · no self-approval |
| NODE ACCESS GROUP | ORG_EDITORS (AMBUS: AM_EDITORS) | Can edit leaves in {HIER} (limb: `limbs and leaves`) |
| VISIBLE CATEGORIES | Finance, HR, Reporting | HR is hidden from VIEWER role |
| LAST REVIEWED | 30 Oct 2026 | Quarterly ownership review |

Note under the grid (12px `#5C5C5C`): **`Changes to this node are approved by the approver group before they reach the version.`**

---

## 9. Draft bar (bottom of `.n-main`, always visible)

### 9.1 Bar
`position relative; flex 0 0 auto; background #FFFFFF; border-top 1px solid #D9D9D9;` flex wrap, centred, `gap 10px 14px`, `padding 10px 24px`:
- Count pill: min-width 28px, height 28px, radius 999px, 13px / 700, `padding 0 8px` — n > 0: white on `#368727`; 0: `#4A4A4A` on `#ECECEC`.
- Text column (`flex 1 1 360px`, `gap 1px`): title 14px / 700 **`Your draft request · {n} changes`** (`1 change`) / **`No changes in your draft`**; sub 12px `#5C5C5C` **`Nothing changes until governance approves · Version {version} Working`** / **`Edits you make are collected here and sent for approval together.`**
- Toast (§9.2) · **View changes** / **Hide changes** (text button 13px / 600 `#2E6B21`, height 40px, `aria-expanded`) · **Discard** (secondary 40px; disabled at 0) · **Review and submit** (primary 44px, `padding 0 20px`, 14px / 600; disabled at 0; opens the submit flow — out of scope).

### 9.2 Toast
Inline in the bar, `role="status"`: inline-flex `gap 8px`, bg `#1F1F1F`, white 12px, radius 999px, `padding 6px 14px`, leading check 14×14 stroke `#A8CF9C` width 2.6. Texts: `Added to your draft request` (default), `Move added to your draft`, `Edit added to your draft`, `Child added to your draft`, `Inactivation added to your draft`. Auto-hide 4 s.

### 9.3 Changes popover
`position absolute; bottom calc(100% + 8px); left 24px; width 520px; max-width calc(100% - 48px);` white, `border 1px solid #D9D9D9`, radius 16px, shadow overlay, `padding 8px`, z-index 30.
- Heading **CHANGES IN YOUR DRAFT REQUEST** (11px / 700, letter-spacing .06em, `#5C5C5C`, `padding 6px 10px`).
- Row (flex `gap 10px`, `min-height 48px`, `padding 0 6px 0 10px`, radius 10px): type dot 26×26 (colours §8.5) · text button (13px, flex 1, wraps; click selects the node and its hierarchy) · remove icon button 32×32 (X 14×14, `aria-label="Remove from draft"`).
- Empty: **`Your draft is empty.`** (13px `#5C5C5C`, `padding 12px 10px`).
- Item texts (exact): `Move 34307 · AGQNT → AGCOE in ORGTL` · `Edit 34301 · name “QNT Analytics Platform”` · `Set Finance country = UK on 34307` · `Add 99302 “QNT Platform Ops” under AGQNT in ORGTL` · `Place 34307 under AMT in AMBUS` · `Inactivate 34307 QNT Quant Dev` · `Reactivate 34322 QNT Contingent` · `Remove 34307 from ORGTL` · `Delete 34307 from all hierarchies`.

---

## 10. Behaviour summary

| Action | Result |
|---|---|
| Change hierarchy | Keep the selected node if it is placed in the new hierarchy (expand to it), else select that hierarchy's top node. Update route. |
| Search result | Select, expand ancestors, scroll into view, clear search |
| Tree row | Select; close any open panel |
| Breadcrumb / parent link / child row | Select that node |
| Position card (Relationships) | Switch hierarchy, keep node |
| Edit / Move / Add child / More items | Open one panel; confirm adds to the draft + toast; tree and card show the draft-applied state with DRAFT flags |
| Undo (in your draft note) | Remove this node's draft items |
| Discard | Empty the draft (no confirm) |

Default state (matches the board): ORGTL; version 2026-11 Working; Live data; selected **34307 QNT Quant Dev**; expanded ORGTL, OC005, AM, AMT, AMQNT, AGCOE, AGQNT; Properties tab; no panel open; draft holds **2 changes**: `Move 34307 · AGQNT → AGCOE in ORGTL` and `Edit 34301 · name “QNT Analytics Platform”` — so 34307 shows under AGCOE and 34301 renamed, both with DRAFT, and the "In your draft" note shows on 34307.

Loading: tree rows lazy-load per limb (show 3 skeleton rows under an expanding limb: bar 12px × 50–70%, `#F2F2F2`, radius 6px); node card shows skeletons for title (24px × 50%), facts and 6 property rows. Errors: area shows **`Couldn't load this. Try again`** (13px, link button).

---

## 11. Data & API (Spring Boot)

```
GET  /api/versions/{v}/hierarchies                                   → [{ code, name, role: 'PRIMARY'|'ALTERNATE', nodeCount, depth }]
GET  /api/versions/{v}/hierarchies/{h}/nodes/{code}/children?draft=  → [{ code, name, type, status, childCount, draftTouched }]
GET  /api/versions/{v}/hierarchies/{h}/nodes/{code}/path             → [codes root → node]
GET  /api/versions/{v}/hierarchies/{h}/search?q=&limit=6             → [{ code, name, type, path: [codes] }]
GET  /api/versions/{v}/nodes/{code}?draft=                           → { code, name, description, type, status, typeLabel }
GET  /api/versions/{v}/nodes/{code}/properties?draft=                → [{ category, key, label, value, origin: 'SET'|'INHERITED'|'DEFAULT'|'DERIVED'|'NONE', inheritedFrom?, pendingInDraft, editable }]
GET  /api/versions/{v}/nodes/{code}/positions                        → [{ hierarchy, role, level, path: [codes] | null }]
GET  /api/versions/{v}/nodes/{code}/history                          → [{ kind, text, user, at, approvedBy?, pending }]
GET  /api/versions/{v}/nodes/{code}/ownership                        → { steward, team, approverGroup, accessGroup, categories, lastReviewed }
GET  /api/versions/{v}/hierarchies/{h}/nodes/{code}/move-targets     → [{ code, name, level, blocked?: 'SELF'|'DESCENDANT'|'CURRENT_PARENT' }]
GET  /api/versions/{v}/nodes/{code}/impact                           → { hierarchies: [..], activeDescendants: { [hier]: n } }
GET  /api/drafts/current                                             → { items: [{ id, kind, code, hierarchy?, payload, text }] }
POST /api/drafts/current/items  { kind, code, hierarchy?, payload }  → item | 422 { rule, message }
DELETE /api/drafts/current/items/{id} · DELETE /api/drafts/current
POST /api/drafts/current/submit
```
`kind`: `EDIT | MOVE | ADD | PLACE | PROPERTY | INACTIVATE | REACTIVATE | REMOVE | DELETE`. All reads accept the context `asOf` and `workspace`.

---

## 12. CSS to append to `src/styles.css` (after `nexus-ui.css`)

```css
/* ===== Hierarchies ===== */
.n-hy-toolbar{flex:0 0 auto;display:flex;flex-wrap:wrap;align-items:center;gap:12px 20px;padding:16px 20px 0}
.n-hy-toolbar h1{margin:0;font-size:20px;font-weight:700;letter-spacing:-.01em}
.n-hy-field{display:flex;align-items:center;gap:8px}
.n-hy-field label{font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-hy-select{position:relative}
.n-hy-select select{appearance:none;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 36px 0 14px;font-size:13px;font-weight:600;color:var(--n-text);background:var(--n-surface);cursor:pointer}
.n-hy-select svg{position:absolute;right:14px;top:14px;width:12px;height:12px;stroke:var(--n-text);stroke-width:2.4;fill:none;stroke-linecap:round;stroke-linejoin:round;pointer-events:none}
.n-hy-meta{font-size:12px;color:var(--n-text-muted);white-space:nowrap}
.n-hy-search{position:relative;flex:0 1 380px;min-width:260px}
.n-hy-search svg{position:absolute;left:14px;top:11px;width:18px;height:18px;stroke:var(--n-text-2);stroke-width:2;fill:none;stroke-linecap:round}
.n-hy-search input{width:100%;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 16px 0 40px;font-size:13px;color:var(--n-text);background:var(--n-surface)}
.n-hy-results{position:absolute;top:48px;left:0;right:0;min-width:380px;background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);box-shadow:var(--n-shadow-overlay);padding:8px;z-index:40}
.n-hy-results__head{display:block;padding:6px 10px;font-size:11px;font-weight:700;letter-spacing:.06em;color:var(--n-text-muted)}
.n-hy-result{display:flex;align-items:center;gap:12px;width:100%;text-align:left;background:transparent;border:0;border-radius:var(--n-r-row);padding:10px;min-height:52px}
.n-hy-result:hover,.n-hy-result[aria-selected="true"]{background:var(--n-surface-sunk)}
.n-hy-result__name{font-size:14px;font-weight:600}
.n-hy-result__code{font-family:var(--n-mono);font-size:12px;font-weight:400;color:var(--n-text-muted)}
.n-hy-result__path{font-family:var(--n-mono);font-size:11px;color:var(--n-text-muted)}
.n-hy-noresult{padding:12px;font-size:13px;color:var(--n-text-muted)}
/* markers */
.n-hy-limb{width:10px;height:10px;flex:0 0 10px;border:1.5px solid var(--n-text-2)}
.n-hy-leaf{width:8px;height:8px;flex:0 0 8px;border-radius:50%;border:1.5px solid var(--n-text-faint)}
/* body */
.n-hy{flex:1 1 auto;min-height:0;display:flex;gap:16px;padding:16px 20px;align-items:stretch}
.n-hy-card{background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);display:flex;flex-direction:column;overflow:hidden;min-width:0}
.n-hy-tree{width:520px;flex:0 0 520px}
.n-app--nav-open .n-hy-tree{width:440px;flex-basis:440px}
.n-hy-node{flex:1 1 auto}
.n-hy-tree__head{padding:14px 16px 12px;display:flex;align-items:center;gap:10px;border-bottom:1px solid var(--n-divider)}
.n-hy-tree__head h2{margin:0;font-size:15px;font-weight:700;flex:1 1 auto}
.n-hy-switch{display:flex;align-items:center;gap:8px;background:transparent;border:0;padding:0;height:32px;font-size:13px;color:var(--n-text)}
.n-hy-switch__track{width:32px;height:18px;border-radius:var(--n-r-pill);background:var(--n-border-input);position:relative;display:inline-block;transition:background .12s ease}
.n-hy-switch__knob{position:absolute;top:2px;left:2px;width:14px;height:14px;border-radius:50%;background:#fff;box-shadow:0 1px 2px rgba(0,0,0,.2);transition:left .12s ease}
.n-hy-switch[aria-checked="true"] .n-hy-switch__track{background:var(--n-brand)}
.n-hy-switch[aria-checked="true"] .n-hy-switch__knob{left:16px}
.n-hy-tree__list{flex:1 1 auto;min-height:0;overflow:auto;padding:8px 10px 12px}
.n-hy-row{display:flex;align-items:stretch;min-height:40px;border-radius:var(--n-r-tree)}
.n-hy-row:hover{background:var(--n-surface-sunk)}
.n-hy-row[aria-selected="true"]{background:var(--n-brand-50)}
.n-hy-guide{width:16px;flex:0 0 16px;display:flex;justify-content:center}
.n-hy-guide::before{content:"";width:1px;background:var(--n-guide)}
.n-hy-toggle{width:28px;flex:0 0 28px;border:0;background:transparent;display:flex;align-items:center;justify-content:center;padding:0}
.n-hy-toggle span{width:13px;height:13px;border:1.5px solid var(--n-text-2);display:flex;align-items:center;justify-content:center;font-family:var(--n-mono);font-size:11px;line-height:1;color:var(--n-text);background:#fff}
.n-hy-marker{width:28px;flex:0 0 28px;display:flex;align-items:center;justify-content:center}
.n-hy-row--inactive .n-hy-leaf{border-color:var(--n-separator)}
.n-hy-row__body{flex:1 1 auto;min-width:0;display:flex;align-items:center;gap:10px;background:transparent;border:0;padding:8px;text-align:left;color:var(--n-text)}
.n-hy-row__code{font-family:var(--n-mono);font-size:12px;flex:0 0 48px;color:var(--n-text-muted)}
.n-hy-row[aria-selected="true"] .n-hy-row__code{color:var(--n-brand-700)}
.n-hy-row__name{flex:1 1 auto;min-width:0;font-size:13px;line-height:1.35;font-weight:500;overflow-wrap:anywhere}
.n-hy-row[aria-selected="true"] .n-hy-row__name{font-weight:700}
.n-hy-row--inactive .n-hy-row__name{color:var(--n-text-faint)}
.n-hy-row--struck .n-hy-row__name{text-decoration:line-through}
.n-hy-flag{font-size:10px;font-weight:700;letter-spacing:.05em;white-space:nowrap}
.n-hy-flag--draft{display:inline-flex;align-items:center;gap:5px;color:var(--n-status)}
.n-hy-flag--draft::before,.n-hy-tri{content:"";width:0;height:0;border-left:4px solid transparent;border-right:4px solid transparent;border-bottom:8px solid var(--n-warn)}
.n-hy-flag--inactive{color:var(--n-text-muted)}
.n-hy-row__count{font-size:11px;color:var(--n-text-faint);white-space:nowrap}
/* node header */
.n-hy-head{padding:18px 24px 16px;display:flex;flex-direction:column;gap:12px;border-bottom:1px solid var(--n-divider)}
.n-hy-crumbs{display:flex;flex-wrap:wrap;align-items:center}
.n-hy-crumb{background:transparent;border:0;padding:4px 3px;font-family:var(--n-mono);font-size:12px;font-weight:500;color:var(--n-brand-700);text-decoration:underline;text-underline-offset:3px}
.n-hy-crumb[aria-current="page"]{font-weight:700;color:var(--n-text);text-decoration:none;cursor:default}
.n-hy-crumb-sep{color:var(--n-separator);padding:0 4px;font-size:12px}
.n-hy-title{display:flex;align-items:flex-start;gap:14px;flex-wrap:wrap}
.n-hy-tile{width:44px;height:44px;flex:0 0 44px;border-radius:12px;background:var(--n-brand-40);display:flex;align-items:center;justify-content:center}
.n-hy-tile--limb::before{content:"";width:15px;height:15px;border:2px solid var(--n-brand)}
.n-hy-tile--leaf::before{content:"";width:13px;height:13px;border-radius:50%;border:2px solid var(--n-brand)}
.n-hy-title__text{display:flex;flex-direction:column;gap:4px;flex:1 1 280px;min-width:0}
.n-hy-title h2{margin:0;font-size:24px;font-weight:700;line-height:1.2;letter-spacing:-.01em}
.n-hy-title__meta{display:flex;flex-wrap:wrap;align-items:center;gap:8px;font-size:13px;color:var(--n-text-muted)}
.n-hy-title__code{font-family:var(--n-mono);font-weight:600;color:var(--n-brand-700)}
.n-hy-status{font-size:12px;font-weight:600;border-radius:var(--n-r-pill);padding:4px 12px;color:var(--n-brand-800);background:var(--n-brand-50)}
.n-hy-status--inactive{color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-hy-pending{display:flex;align-items:center;gap:10px;background:var(--n-warn-bg);border:1px solid #F3E2C6;border-radius:var(--n-r-tile);padding:10px 14px;font-size:13px;color:#5A3500}
.n-hy-pending b{font-weight:700}
.n-hy-pending__text{flex:1 1 auto}
.n-hy-pending button{background:transparent;border:0;font-size:13px;font-weight:600;color:var(--n-brand-700);height:32px;text-decoration:underline;text-underline-offset:3px}
.n-hy-readonly{font-size:13px;color:var(--n-text-muted)}
.n-hy-actions{display:flex;flex-wrap:wrap;gap:8px;align-items:center}
.n-hy-act{display:inline-flex;align-items:center;gap:8px;height:40px;padding:0 16px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);color:var(--n-text);font-size:13px;font-weight:600}
.n-hy-act svg{width:15px;height:15px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-hy-act:hover:not(:disabled){background:var(--n-surface-sunk)}
.n-hy-act[aria-pressed="true"]{background:var(--n-text);border-color:var(--n-text);color:#fff}
.n-hy-act--brand{border-color:var(--n-brand);color:var(--n-brand-800)}
.n-hy-act--brand[aria-pressed="true"]{background:var(--n-brand);border-color:var(--n-brand);color:#fff}
.n-hy-act--more{padding:0 14px;gap:6px}
.n-hy-act--more[aria-expanded="true"]{background:var(--n-surface-sunk)}
.n-hy-act--more svg{width:12px;height:12px;stroke-width:2.4}
.n-hy-act:disabled{opacity:.5;cursor:not-allowed}
.n-hy-more{position:relative}
.n-hy-menu{position:absolute;left:0;top:46px;width:260px;background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-tile);box-shadow:var(--n-shadow-overlay);padding:6px;z-index:30}
.n-hy-menu__item{display:flex;align-items:center;gap:10px;width:100%;min-height:40px;padding:0 12px;border:0;border-radius:var(--n-r-tree);background:transparent;font-size:13px;color:var(--n-text);text-align:left}
.n-hy-menu__item:hover,.n-hy-menu__item:focus-visible{background:var(--n-surface-sunk)}
.n-hy-menu__item svg{width:15px;height:15px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-hy-menu__item--danger{color:var(--n-danger);font-weight:600}
.n-hy-menu__sep{height:1px;background:var(--n-divider);margin:4px 6px}
/* panels */
.n-hy-panel{border:1.5px solid var(--n-brand);border-radius:var(--n-r-panel);overflow:hidden}
.n-hy-panel--pad{padding:14px;display:flex;flex-direction:column;gap:12px}
.n-hy-panel--warn{border-color:var(--n-warn);background:var(--n-warn-bg);padding:14px;display:flex;flex-direction:column;gap:10px}
.n-hy-panel__title{font-size:13px;font-weight:700}
.n-hy-panel--warn .n-hy-panel__title{font-size:14px}
.n-hy-panel__head{padding:12px 14px;background:var(--n-brand-30);display:flex;align-items:center;gap:8px}
.n-hy-panel__head .n-hy-panel__title{flex:1 1 auto}
.n-hy-panel__textbtn{background:transparent;border:0;font-size:13px;font-weight:600;color:var(--n-brand-700);height:32px}
.n-hy-panel__foot{padding:10px 14px;border-top:1px solid #E6EFE2;font-size:12px;color:var(--n-text-3)}
.n-hy-panel__btns{display:flex;gap:8px;justify-content:flex-end}
.n-hy-fields{display:flex;flex-wrap:wrap;gap:12px;align-items:flex-end}
.n-hy-fld{display:flex;flex-direction:column;gap:6px;flex:1 1 260px}
.n-hy-fld--code{flex:0 0 120px}
.n-hy-fld--name{flex:1 1 220px}
.n-hy-fld label,.n-hy-fld__label{font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-hy-fld input{height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-row);padding:0 12px;font-size:14px;color:var(--n-text)}
.n-hy-fld--code input{font-family:var(--n-mono);font-size:13px;text-transform:uppercase}
.n-hy-fld input[aria-invalid="true"]{border-color:var(--n-danger)}
.n-hy-err{font-size:12px;font-weight:600;color:var(--n-danger)}
.n-hy-seg{display:inline-flex;gap:2px;background:var(--n-surface-sunk);border-radius:var(--n-r-pill);padding:3px}
.n-hy-seg button{display:flex;align-items:center;gap:6px;background:transparent;border:0;border-radius:var(--n-r-pill);padding:0 12px;height:34px;font-size:13px;font-weight:600}
.n-hy-seg button[aria-pressed="true"]{background:#fff;box-shadow:var(--n-shadow-raised)}
.n-hy-btn{height:40px;padding:0 16px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);font-size:13px;font-weight:600}
.n-hy-btn--primary{padding:0 18px;border:0;background:var(--n-brand);color:#fff}
.n-hy-btn--dark{padding:0 18px;border:0;background:var(--n-text);color:#fff}
.n-hy-btn--danger{padding:0 18px;border:0;background:var(--n-danger);color:#fff}
.n-hy-picklist{max-height:220px;overflow:auto;padding:6px}
.n-hy-pick{display:flex;align-items:center;gap:10px;width:100%;background:transparent;border:0;border-radius:var(--n-r-row);padding:0 10px;min-height:44px;text-align:left}
.n-hy-pick:hover{background:var(--n-surface-sunk)}
.n-hy-pick__code{font-family:var(--n-mono);font-size:12px;color:var(--n-brand-700);min-width:52px}
.n-hy-pick__name{font-size:14px;flex:1 1 auto}
.n-hy-pick__lvl{font-size:12px;color:var(--n-text-muted)}
.n-hy-pick--blocked{color:var(--n-text-faint)}
.n-hy-pick--blocked .n-hy-pick__code{color:var(--n-text-faint)}
.n-hy-pick--blocked .n-hy-limb{border-color:var(--n-border-input)}
.n-hy-chip{font-size:11px;font-weight:600;color:var(--n-text-3);background:var(--n-neutral-bg);border-radius:var(--n-r-pill);padding:3px 10px;white-space:nowrap}
.n-hy-option{display:flex;align-items:flex-start;gap:12px;width:100%;text-align:left;background:#fff;border:1.5px solid var(--n-divider-2);border-radius:var(--n-r-tile);padding:12px 14px;color:var(--n-text)}
.n-hy-option[aria-checked="true"]{border-color:var(--n-text)}
.n-hy-radio{width:18px;height:18px;flex:0 0 18px;border-radius:50%;border:2px solid var(--n-border-input);display:flex;align-items:center;justify-content:center;margin-top:1px}
.n-hy-option[aria-checked="true"] .n-hy-radio{border-color:var(--n-text)}
.n-hy-option[aria-checked="true"] .n-hy-radio::after{content:"";width:8px;height:8px;border-radius:50%;background:var(--n-text)}
.n-hy-option__label{display:flex;align-items:center;gap:8px;font-size:14px;font-weight:600}
.n-hy-option__impact{font-size:13px;color:var(--n-text-3);line-height:1.45}
.n-hy-rec{font-size:11px;font-weight:700;color:var(--n-brand-800);background:var(--n-brand-50);border-radius:var(--n-r-pill);padding:2px 8px}
/* facts + tabs */
.n-hy-facts{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));border-bottom:1px solid var(--n-divider)}
.n-hy-fact{padding:12px 24px;border-right:1px solid var(--n-divider);display:flex;flex-direction:column;gap:3px;min-width:0}
.n-hy-fact:last-child{border-right:0}
.n-hy-fact__label{font-size:11px;font-weight:600;letter-spacing:.06em;color:var(--n-text-muted);text-transform:uppercase}
.n-hy-fact__value{font-size:14px;font-weight:600;overflow-wrap:anywhere}
.n-hy-fact__link{background:transparent;border:0;padding:0;text-align:left;font-size:14px;font-weight:600;color:var(--n-brand-700);text-decoration:underline;text-underline-offset:3px;overflow-wrap:anywhere}
.n-hy-tabs{display:flex;gap:4px;padding:0 20px;border-bottom:1px solid var(--n-divider);flex:0 0 auto}
.n-hy-tab{background:transparent;border:0;border-bottom:2px solid transparent;padding:0 12px;height:46px;font-size:14px;font-weight:500;color:var(--n-text-muted);display:flex;align-items:center;gap:6px}
.n-hy-tab[aria-selected="true"]{border-bottom-color:var(--n-brand);font-weight:700;color:var(--n-text)}
.n-hy-tab__count{font-size:11px;font-weight:700;color:var(--n-text-2);background:var(--n-divider-3);border-radius:var(--n-r-pill);padding:1px 7px}
.n-hy-panelbody{flex:1 1 auto;min-height:0;overflow:auto;padding:16px 24px 24px}
/* properties */
.n-hy-ptools{display:flex;flex-wrap:wrap;align-items:center;gap:10px;margin-bottom:12px}
.n-hy-ptools input{flex:0 1 300px;height:36px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 14px;font-size:13px}
.n-hy-props{border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);overflow:hidden}
.n-hy-prow{display:grid;grid-template-columns:minmax(0,1.2fr) minmax(0,1fr) minmax(0,1fr) 88px;align-items:center;border-top:1px solid var(--n-surface-sunk);min-height:52px}
.n-hy-prow--head{background:var(--n-surface-alt);border-top:0;border-bottom:1px solid var(--n-divider);font-size:12px;font-weight:600;color:var(--n-text-muted);min-height:0}
.n-hy-prow--head span{padding:10px 12px}
.n-hy-prow--head span:first-child{padding-left:16px}
.n-hy-prow--draft{background:var(--n-draft-row)}
.n-hy-pcat{padding:10px 16px 6px;font-size:11px;font-weight:700;letter-spacing:.06em;color:var(--n-text-faint);border-top:1px solid #F0F0F0}
.n-hy-pname{padding:8px 16px;display:flex;flex-direction:column;gap:2px;min-width:0}
.n-hy-pname b{font-size:14px;font-weight:500}
.n-hy-pname code{font-family:var(--n-mono);font-size:11px;color:var(--n-text-faint)}
.n-hy-pval{padding:8px 12px;font-family:var(--n-mono);font-size:13px;font-weight:600;color:var(--n-text);overflow-wrap:anywhere}
.n-hy-pval--empty{font-family:var(--n-font);font-weight:400;color:var(--n-text-faint)}
.n-hy-psrc{padding:8px 12px}
.n-hy-src{display:inline-flex;font-size:12px;font-weight:600;border-radius:var(--n-r-pill);padding:3px 10px;white-space:nowrap}
.n-hy-src--set{color:var(--n-brand-800);background:var(--n-brand-50)}
.n-hy-src--inherited{color:var(--n-moved);background:var(--n-moved-bg)}
.n-hy-src--default{color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-hy-src--derived{color:var(--n-renamed);background:var(--n-renamed-bg)}
.n-hy-src--none{color:var(--n-text-3);background:var(--n-surface-sunk)}
.n-hy-src--draft{color:var(--n-status);background:var(--n-status-bg)}
.n-hy-pact{padding:8px 12px;text-align:right}
.n-hy-pact button{background:transparent;border:0;padding:0 4px;height:32px;font-size:13px;font-weight:600;color:var(--n-brand-700)}
.n-hy-pact button:disabled{color:var(--n-separator)}
.n-hy-pedit{padding:6px 12px;display:flex;gap:6px;grid-column:span 3;align-items:center}
.n-hy-pedit input{flex:1 1 auto;height:36px;border:2px solid var(--n-brand);border-radius:var(--n-r-row);padding:0 12px;font-family:var(--n-mono);font-size:13px}
.n-hy-pedit button{height:36px;padding:0 14px;border-radius:var(--n-r-pill);font-size:12px;font-weight:600;border:1.5px solid var(--n-border-input);background:#fff}
.n-hy-pedit button.n-hy-pedit__save{border:0;background:var(--n-brand);color:#fff}
.n-hy-note{margin:10px 0 0;font-size:12px;color:var(--n-text-muted)}
/* relationships, history, ownership */
.n-hy-rel{display:flex;flex-wrap:wrap;gap:24px;align-items:flex-start}
.n-hy-rel__col{flex:1 1 340px;min-width:0;display:flex;flex-direction:column;gap:10px}
.n-hy-rel__col--kids{flex-basis:300px}
.n-hy-rel h3{margin:0;font-size:15px;font-weight:700}
.n-hy-rel__sub{margin:0;font-size:13px;color:var(--n-text-muted);line-height:1.5}
.n-hy-pos{border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);padding:12px 14px;display:flex;flex-direction:column;gap:6px;background:#fff;text-align:left;width:100%;color:var(--n-text)}
.n-hy-pos[aria-current="true"]{border-color:var(--n-brand-border);background:var(--n-brand-20)}
.n-hy-pos__top{display:flex;align-items:center;gap:8px;width:100%}
.n-hy-pos__code{font-size:13px;font-weight:700}
.n-hy-pos__role{font-size:12px;color:var(--n-text-muted);flex:1 1 auto}
.n-hy-pos__lvl{font-size:12px;font-weight:600;color:var(--n-brand-700);background:var(--n-brand-40);border-radius:var(--n-r-pill);padding:3px 10px}
.n-hy-pos__path{font-family:var(--n-mono);font-size:12px;color:var(--n-text-2);line-height:1.6;overflow-wrap:anywhere}
.n-hy-kids{border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);padding:6px}
.n-hy-kid{display:flex;align-items:center;gap:10px;width:100%;min-height:40px;padding:0 10px;border:0;border-radius:var(--n-r-row);background:transparent;text-align:left;color:var(--n-text)}
.n-hy-kid:hover{background:var(--n-surface-sunk)}
.n-hy-kid__code{font-family:var(--n-mono);font-size:12px;color:var(--n-brand-700);min-width:48px}
.n-hy-kid__name{font-size:13px;flex:1 1 auto}
.n-hy-kid--inactive .n-hy-kid__name{color:var(--n-text-faint)}
.n-hy-empty{border:1.5px dashed var(--n-guide);border-radius:var(--n-r-tile);padding:14px;font-size:13px;color:var(--n-text-muted)}
.n-hy-hist{display:flex;flex-direction:column}
.n-hy-hitem{display:flex;gap:14px;padding:12px 0;border-top:1px solid var(--n-divider-3)}
.n-hy-hdot{width:28px;height:28px;flex:0 0 28px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:700}
.n-hy-htext{display:flex;flex-direction:column;gap:3px;flex:1 1 auto}
.n-hy-htext b{font-size:14px;font-weight:600}
.n-hy-htext span{font-size:12px;color:var(--n-text-muted)}
.n-hy-hpend{align-self:center;font-size:11px;font-weight:700;color:var(--n-status);background:var(--n-status-bg);border-radius:var(--n-r-pill);padding:3px 10px}
.n-hy-haudit{font-size:13px;padding-top:12px;border-top:1px solid var(--n-divider-3)}
.n-hy-own{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);overflow:hidden}
.n-hy-own > div{padding:14px 16px;border-bottom:1px solid #F0F0F0;border-right:1px solid #F0F0F0;display:flex;flex-direction:column;gap:4px}
.n-hy-own > div:nth-child(2n){border-right:0}
.n-hy-own__label{font-size:11px;font-weight:600;letter-spacing:.06em;color:var(--n-text-muted);text-transform:uppercase}
.n-hy-own__value{font-size:14px;font-weight:600}
.n-hy-own__note{font-size:12px;color:var(--n-text-muted)}
@media (max-width:1100px){.n-hy{flex-direction:column;overflow:auto}.n-hy-tree{width:auto;flex:0 0 420px}}
```
(The draft bar uses the design-system `.n-draftbar` classes unchanged.)

---

## 13. HTML contract (Angular template skeleton)

```html
<div class="n-hy-toolbar">
  <h1>Hierarchies</h1>
  <div class="n-hy-field">
    <label for="h-sel">Hierarchy</label>
    <span class="n-hy-select"><select id="h-sel" [formControl]="hier">
      <option *ngFor="let h of hierarchies" [value]="h.code">{{h.code}} · {{h.name}}</option></select>
      <svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg></span>
    <span class="n-hy-meta">{{cur.role==='PRIMARY' ? 'Primary' : 'Alternate'}} · {{cur.nodeCount | number}} nodes · {{cur.depth}} levels</span>
  </div>
  <span class="n-spacer"></span>
  <div class="n-hy-search">
    <label class="n-sr-only" for="h-search">Search in this hierarchy</label>
    <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
    <input id="h-search" type="search" [placeholder]="'Search ' + hier.value + ' by code or name'" [formControl]="q"
           role="combobox" aria-controls="h-results" [attr.aria-expanded]="showResults">
    <div *ngIf="showResults" id="h-results" class="n-hy-results" role="listbox">
      <span class="n-hy-results__head">{{results.length}} {{results.length===1 ? 'MATCH' : 'MATCHES'}} IN {{hier.value}}</span>
      <button *ngFor="let r of results" type="button" role="option" class="n-hy-result" (click)="goTo(r)">
        <span [ngClass]="r.type==='LIMB' ? 'n-hy-limb' : 'n-hy-leaf'" aria-hidden="true"></span>
        <span style="display:flex;flex-direction:column;gap:2px;min-width:0">
          <span><span class="n-hy-result__name">{{r.name}}</span> <span class="n-hy-result__code">{{r.code}}</span></span>
          <span class="n-hy-result__path">{{r.path.join(' / ')}}</span></span>
      </button>
      <div *ngIf="!results.length" class="n-hy-noresult">Nothing in {{hier.value}} matches.</div>
    </div>
  </div>
</div>

<div class="n-hy">
  <section class="n-hy-card n-hy-tree" aria-label="Tree">
    <div class="n-hy-tree__head"><h2>Tree</h2>
      <button type="button" class="n-hy-switch" role="switch" [attr.aria-checked]="hideInactive" (click)="toggleHideInactive()">
        <span class="n-hy-switch__track" aria-hidden="true"><span class="n-hy-switch__knob"></span></span>Hide inactive</button></div>
    <cdk-virtual-scroll-viewport autosize class="n-hy-tree__list" role="tree" [attr.aria-label]="hier.value">
      <div *cdkVirtualFor="let n of rows" class="n-hy-row" role="treeitem" [attr.aria-level]="n.depth+1" [attr.aria-selected]="n.code===selected"
           [attr.aria-expanded]="n.hasKids ? n.open : null" [class.n-hy-row--inactive]="n.status==='Inactive'" [class.n-hy-row--struck]="n.removedInDraft">
        <span *ngFor="let g of n.guides" class="n-hy-guide" aria-hidden="true"></span>
        <button *ngIf="n.hasKids" type="button" class="n-hy-toggle" [attr.aria-label]="(n.open ? 'Collapse ' : 'Expand ') + n.code" (click)="toggle(n)"><span>{{n.open ? '−' : '+'}}</span></button>
        <span *ngIf="!n.hasKids" class="n-hy-marker" aria-hidden="true"><span [ngClass]="n.type==='LIMB' ? 'n-hy-limb' : 'n-hy-leaf'"></span></span>
        <button type="button" class="n-hy-row__body" [title]="n.code + ' · ' + n.name" (click)="select(n.code)">
          <span class="n-hy-row__code">{{n.code}}</span><span class="n-hy-row__name">{{n.name}}</span>
          <span *ngIf="n.draftTouched" class="n-hy-flag n-hy-flag--draft" title="Pending in your draft request">DRAFT</span>
          <span *ngIf="n.status==='Inactive'" class="n-hy-flag n-hy-flag--inactive">INACTIVE</span>
          <span *ngIf="n.hasKids && !n.open" class="n-hy-row__count">{{n.childCount}}</span>
        </button>
      </div>
    </cdk-virtual-scroll-viewport>
  </section>

  <section class="n-hy-card n-hy-node" aria-label="Node details">
    <div class="n-hy-head">
      <nav class="n-hy-crumbs" aria-label="Breadcrumb">…button.n-hy-crumb + span.n-hy-crumb-sep…</nav>
      <div class="n-hy-title">
        <span class="n-hy-tile" [ngClass]="node.type==='LIMB' ? 'n-hy-tile--limb' : 'n-hy-tile--leaf'" aria-hidden="true"></span>
        <div class="n-hy-title__text"><h2>{{node.name}}</h2>
          <div class="n-hy-title__meta"><span class="n-hy-title__code">{{node.code}}</span><span aria-hidden="true">·</span><span>{{node.typeLabel}}</span><span aria-hidden="true">·</span><span>{{node.description}}</span></div></div>
        <span class="n-hy-status" [class.n-hy-status--inactive]="node.status==='Inactive'">{{node.status}}</span>
      </div>
      <span *ngIf="readOnlyReason" class="n-hy-readonly">Read-only — {{readOnlyReason}}.</span>
      <div *ngIf="pending.length" class="n-hy-pending"><span class="n-hy-tri" aria-hidden="true"></span>
        <span class="n-hy-pending__text"><b>In your draft:</b> {{pendingText}}</span><button type="button" (click)="undoPending()">Undo</button></div>
      <div class="n-hy-actions">
        <button type="button" class="n-hy-act n-hy-act--brand" [disabled]="readOnly" [attr.aria-pressed]="panel==='edit'" (click)="open('edit')"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 20h4L19 9l-4-4L4 16v4z"/><path d="m14 6 4 4"/></svg>Edit details</button>
        <button *ngIf="!isTop" type="button" class="n-hy-act" [disabled]="readOnly" [attr.aria-pressed]="panel==='move'" (click)="open('move')"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14"/><path d="m13 6 6 6-6 6"/></svg>Move…</button>
        <button *ngIf="node.type==='LIMB'" type="button" class="n-hy-act" [disabled]="readOnly" [attr.aria-pressed]="panel==='add'" (click)="open('add')"><svg viewBox="0 0 24 24" aria-hidden="true" style="stroke-width:2.4"><path d="M12 5v14M5 12h14"/></svg>Add child…</button>
        <div class="n-hy-more">
          <button type="button" class="n-hy-act n-hy-act--more" [disabled]="readOnly" aria-haspopup="menu" [attr.aria-expanded]="moreOpen" (click)="moreOpen=!moreOpen">More<svg viewBox="0 0 24 24" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg></button>
          <div *ngIf="moreOpen" class="n-hy-menu" role="menu" aria-label="More actions">
            <button type="button" role="menuitem" class="n-hy-menu__item" (click)="open('place')"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M10 3h4v5h-4zM4 16h4v5H4zM16 16h4v5h-4zM12 8v4M6 16v-4h12v4"/></svg>Add to another hierarchy…</button>
            <div class="n-hy-menu__sep" aria-hidden="true"></div>
            <button type="button" role="menuitem" class="n-hy-menu__item n-hy-menu__item--danger" (click)="open('remove')"><svg viewBox="0 0 24 24" aria-hidden="true"><path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13"/></svg>{{node.status==='Inactive' ? 'Reactivate or remove…' : 'Inactivate or remove…'}}</button>
          </div>
        </div>
      </div>
      <!-- one of: app-edit-panel | app-move-panel | app-add-panel | app-place-panel | app-remove-panel (§7) -->
    </div>
    <div class="n-hy-facts">…4× div.n-hy-fact (§8.1)…</div>
    <div class="n-hy-tabs" role="tablist" aria-label="Node information">…button.n-hy-tab[role=tab]…</div>
    <div class="n-hy-panelbody" role="tabpanel">…§8.3–8.6…</div>
  </section>
</div>
<app-draft-bar />
```

---

## 14. Mock data (use exactly this for demos and tests)

Hierarchies: ORGTL (Primary · 2,818 nodes · 14 levels; top node *Total Fidelity*), ORGTP (Alternate · 516 · 12; *Total Fidelity Technology*), AMBUS (Alternate · 1,501 · 12; *Asset Management BU Alternate*). Paths to the quant branch: ORGTL `ORGTL / OC005 / AM / AMT / AMQNT`, ORGTP `ORGTP / BUTCH / AMT / AMQNT`, AMBUS `AMBUS / AMT / AMQNT`.

Branch: `AMQNT Quant Technology` → `AGCOE AM AI Center of Excellence` (limb), `AGQNT US Quant Technology` (limb) → `31679 UK QNT Tech`, `34295 QNT Engineering`, `34299 QNT Leadership`, `34301 QNT Analytics`, `34307 QNT Quant Dev` (desc *Quant development*), `34322 QNT Contingent` (**Inactive**). Other AMT children: `AMACE AMT Common Engineering`, `AMATF Architecture & Technology Foundations` (limbs).

Expected with the default draft (move 34307 → AGCOE; rename 34301):
- 34307 selected: breadcrumb `ORGTL / OC005 / AM / AMT / AMQNT / AGCOE / 34307`; title *QNT Quant Dev*; meta `34307 · Leaf · cost centre · Quant development`; Active; note `In your draft: Move 34307 · AGQNT → AGCOE in ORGTL`; actions Edit details · Move… · More (no Add child — leaf); facts `AGCOE · AM AI Center of Excellence` · `None · leaf node` · `Level 7 of 14` · `3 of 3`; History count 1.
- Relationships: ORGTL Level 7 (current), ORGTP Level 6, AMBUS Level 5 (ORGTP/AMBUS paths still show AGQNT — moves are local).
- 34301 shows *QNT Analytics Platform* with DRAFT in all three hierarchies.
- Choose AMBUS in the toolbar: 34307 stays selected; meta `Alternate · 1,501 nodes · 12 levels`; breadcrumb `AMBUS / AMT / AMQNT / AGQNT / 34307`.
- Search `quant dev` in ORGTL: `1 MATCH IN ORGTL` — QNT Quant Dev `34307`, path `ORGTL / OC005 / AM / AMT / AMQNT / AGCOE / 34307` (draft-applied).
- Properties (34307): Cost centre `34307` Derived from code · Finance country `US` Inherited · AMT · Oracle accounting currency `USD` Inherited · AMT · Oracle GL company `0444` Inherited · AMT · Oracle operating ledger `L100` Inherited · AMT · Reporting currency `USD` Default · Standard working hours `40.00` Inherited · ORGTL · HR flag `Y` Set on this node · Include in reporting `Y` Default · Full name `34307 QNT Quant Dev` Derived.

---

## 15. Accessibility
- One `h1` (Hierarchies). Tree card heading and node name are `h2`; tab sections `h3`.
- Hierarchy select and search have labels; search is a combobox with a listbox popover.
- Tree uses `role=tree/treeitem` with `aria-level`, `aria-expanded`, `aria-selected`; toggle buttons have "Expand/Collapse {code}".
- More menu: `aria-haspopup="menu"`, `aria-expanded`, `role=menu/menuitem`, focus management per §6.5.
- Remove options: `role=radiogroup/radio` with `aria-checked`.
- Toasts `role="status"`; panel errors `aria-live="polite"`.
- Never colour alone: DRAFT/INACTIVE have text; limb/leaf have distinct shapes.
- Contrast ≥ 4.5:1 for all text.

---

## 16. Do NOT
- Don't put hierarchy tabs across the top — use the toolbar select.
- Don't put search or "Recent" chips inside the tree card; don't add a legend or footer to the tree card.
- Don't show more than three action buttons + **More** on the node card; never show separate "Remove from hierarchy" / "Delete node" buttons.
- Don't apply any edit directly — everything goes to the draft.
- Don't truncate node names, paths or fact values with ellipsis.
- Don't add confirm dialogs (panels + draft + Undo replace them).
- Don't invent extra flags, colours or actions.

---

## 17. Acceptance checklist
- [ ] Shell identical to other screens; Hierarchies active in the rail; tree card 520px (440px with the menu open).
- [ ] Toolbar: h1 20/700, Hierarchy pill select + role/count meta, right-aligned 380px search with results popover (heading, name+code, full path).
- [ ] Tree card: "Tree" header with Hide inactive switch; rows with 16px guides, square/circle markers, wrapping names, DRAFT/INACTIVE flags, child counts; keyboard navigation.
- [ ] Node header: breadcrumb, icon tile, h2 name, meta, status, "In your draft" note with Undo, read-only line when applicable.
- [ ] Action row: Edit details · Move… · Add child… (limbs) · More ▾ (Add to another hierarchy… / Inactivate or remove…).
- [ ] Panels per §7 with exact texts; each confirm adds to the draft with a toast; integrity errors inline.
- [ ] Fact strip (wrapping); tabs Properties / Relationships / History (count) / Ownership with exact content.
- [ ] Draft bar with count, View changes popover, Discard, Review and submit.
- [ ] Switching hierarchy keeps the node when placed there.
- [ ] Mock data reproduces §14.
- [ ] Pixel check vs. the "Hierarchies (new · tidy top)" board at 1440px: computed styles match §4–§9 and §12.
