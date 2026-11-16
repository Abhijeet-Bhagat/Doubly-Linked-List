# Hierarchies screen — Build Specification (master–detail, Nexus design language)

> **Give this file to the LLM/developer together with `nexus-design-system.md` and a screenshot of the canvas board "Hierarchies · master–detail (tree left, tabs right)".** Every visual comes from the design-system classes; this file defines layout, structure, content, states and behaviour. Component `.scss` files may contain **layout only**. If this file and the screenshot disagree, this file wins.
>
> *Supersedes the earlier "Option D in unified shell" version of this spec (stacked layout). Reason: with the tree on top and details below, users lose their place; a fixed two-pane master–detail keeps the tree and the selected node visible at the same time.*

---

## 1. Context
- **App:** *Nexus – Master Data Central*, our MDC (Oracle DRM) replacement. Team theme: **Shape the Tree**.
- **Route (unchanged from the current build):** `/hierarchies/:code?node=<id>` — e.g. `/hierarchies/ORGTL?node=34307`. Selecting a node updates `?node=` (replaceState) so the URL is shareable.
- **Governance:** edits are **not** applied directly. Each edit is added to the user's **draft request**; nothing changes in the version until governance approves ("Review and submit"). This matches the existing build's draft bar.
- **User:** business operations data steward (e.g. `e2e_steward`). Monthly tasks: find a node → understand where it sits and what its properties are → propose changes → submit for approval.

## 2. Layout (1440 × 980, fixed viewport, internal scrolling)

```
┌ n-header ─────────────────────────────────────────────────────────────────────┐
├ n-context: Version · As of · Work in (Live|Sandbox)                Manage versions ┤
├ n-sidebar ┬ n-main ───────────────────────────────────────────────────────────┤
│           │ n-tabbar: ORGTL · Primary | ORGTP · Alternate | AMBUS · Alternate  │
│ Home      │ n-workspace (padding 16px 20px, gap 16px)                         │
│▸Hierarch. │ ┌ n-pane--tree 400px ─┐ ┌ n-pane--detail (fills) ───────────────┐ │
│ Queries   │ │ search + recents    │ │ breadcrumb                             │ │
│ Compare   │ │─────────────────────│ │ [icon] Name            [Active]        │ │
│ As-Was    │ │ tree (scrolls)      │ │ code · type · description              │ │
│ CHANGE    │ │                     │ │ [In your draft: … Undo]                │ │
│ Worklist  │ │                     │ │ [Edit details][Move…][+Add child][Add to another hierarchy…]   [Remove…] │
│ Sandboxes │ │                     │ │ (inline panel for the chosen action)   │ │
│ Audit     │ │                     │ │ PARENT | CHILDREN | LEVEL | IN HIERARCHIES │
│ ADMIN     │ │                     │ │ Properties | Relationships | History | Ownership │
│ Versions  │ │─────────────────────│ │ tab panel (scrolls)                    │ │
│ Settings  │ │ legend · Hide inact.│ │                                        │ │
│           │ └─────────────────────┘ └────────────────────────────────────────┘ │
│           │ n-draftbar: (2) Your draft request · 2 changes … View changes · Discard · [Review and submit] │
└───────────┴───────────────────────────────────────────────────────────────────┘
```
- **Sidebar is collapsible** from the header hamburger (`.n-burger`). Default = 64px icon rail so the tree gets the room; expanded = 216px with labels. Tree pane width: **520px with the rail, 440px with the expanded menu**.
- Root: `.n-app.n-app--fixed` (100vh, min 900px, no page scroll). Only the tree list and the detail tab panel scroll.
- `.n-main` = `.n-tabbar` + `.n-workspace` + `.n-draftbar` (column; workspace `flex:1; min-height:0`).
- Below 1100px the panes stack (tree 420px tall, then detail) and the workspace scrolls.

## 3. Global shell
Exactly as `nexus-design-system.md` §4.1–4.2. Sidebar item **Hierarchies** has `aria-current="page"`. Header starts with the hamburger; the sidebar opens/closes in place (no overlay above 900px). User chip shows the signed-in "log in as" user (screenshot: **E2E Steward · e2e_steward**).

## 4. Hierarchy tab bar — `.n-tabbar`
Tabs: `ORGTL · Primary` (Total Fidelity · 2,818 nodes · 14 levels), `ORGTP · Alternate` (Total Fidelity Technology · 516 nodes · 12 levels), `AMBUS · Alternate` (Asset Management BU Alternate · 1,501 nodes · 12 levels). Data from `GET /hierarchies`; the current build's other hierarchies (e.g. *E2E Legal Group*) appear as more tabs; if more than 5, show the first 4 and a "More ▾" secondary pill listing the rest. Switching keeps the selected node when it exists in the target hierarchy, else selects its top node.

## 5. Tree pane — `section.n-pane.n-pane--tree`
**Top `.n-pane__top`**
- `.n-search.n-search--compact`: placeholder `Search <HIER> by code or name`; popover `.n-popover` (label `N MATCHES IN ORGTL`, up to 6 `.n-result` rows with marker, name, mono code, mono path). Selecting: select node, expand ancestors, scroll into view, clear query.
- Recent row: label "Recent" (12px muted) + up to 4 `.n-chip.n-chip--sm` (mono code + name truncated to 14 chars + "…"). Most-recent first; stored per user.

**List `.n-pane__scroll > .n-tree.n-tree--compact`** (virtual scroll with **variable row height**, min 40px)
- Row parts identical to the design system tree: guides per depth (**16px**), limb toggle box `+/−` (13px), leaf circle, `button.n-tree-row__body` (padding 8px) with code (mono 12, fixed 48px column), name (13px, **wraps onto extra lines — never truncated with an ellipsis**, line-height 1.35, `.n-tree-row__name--wrap`), flags, child count (collapsed limbs only, number only e.g. `8`). The row also carries `title="<code> · <name>"`.
- With 16px indents a level-14 leaf still leaves ≥ 220px for its name in the 520px pane.
- Flags: `.n-flag--draft` "DRAFT" when the node is touched by the user's draft (rename/property/status are global → flag in every hierarchy; move/add/remove → only in that hierarchy); `.n-flag--inactive` "INACTIVE".
- The tree shows the **draft-applied view** (a moved node appears under its new parent with DRAFT) so users see the outcome of their proposal.
- Selected row: `aria-selected="true"` (green tint, bold, green code). Body click selects; box click expands (lazy `children` API).

**Footer `.n-pane__foot`**: legend `□ Limb`, `○ Leaf`, `▲ Draft` · spacer · `.n-toggle` "Hide inactive" (12px label).

## 6. Detail pane — `section.n-pane.n-pane--detail`

### 6.1 Header `.n-detail-head`
1. Breadcrumb `.n-crumbs` (12px variant): path from top node; ancestors clickable.
2. `.n-detail-title`: `.n-icon-tile` (44px; limb square / leaf circle in brand) · column with `h1.n-detail-title__name` (24px) and `.n-detail-title__meta` = mono code · type ("Limb" / "Leaf · cost centre") · description · right: `.n-status--active|inactive`.
3. `.n-pending` (only if the user's draft touches this node): orange triangle · "**In your draft:** Move 34307 · AGQNT → AGCOE in ORGTL" (multiple joined by " · ") · `Undo` text link (removes those draft items).
4. Action row (wrap, gap 8):
   | Button | Class | Shown | Opens |
   |---|---|---|---|
   | ✎ Edit details | `.n-btn.n-btn--sm.n-btn--outline-brand` (`aria-pressed` while open) | always | Edit panel |
   | → Move in <HIER>… | `.n-btn.n-btn--sm.n-btn--secondary` | always | Move picker |
   | + Add child… | `.n-btn--sm.n-btn--secondary` | limbs only | Add panel |
   | Add to another hierarchy… | `.n-btn--sm.n-btn--secondary` | always | Place panel |
   | (spacer) 🗑 Remove… / Reactivate or remove… | `.n-btn--sm.n-btn--danger` | always, right-aligned | Remove panel |
   Mapping to the current build: *Edit details* = Edit details; *Place under another parent* = **Add to another hierarchy…**; *Move to…* = **Move…**; *Remove from hierarchy* + *Delete node* (+ Inactivate) = one **Remove…** panel with an impact preview.
   Read-only (Finalized version or VIEWER role): all action buttons `disabled`.
5. Inline panel (one at a time, below the actions; closes on node change):
   - **Edit details** `.n-panel.n-panel--brand` (padding 14): title "Edit details · name changes apply in ORGTL, ORGTP and AMBUS"; fields Name (`.n-input`, maxlength 60, label shows "n / 60") and Description; buttons Cancel (secondary sm) / **Add to draft** (primary sm).
   - **Move picker** `.n-panel--brand`: head "Pick a new parent in <HIER>" + Cancel; list max-height 220px of limbs (`.n-pick` valid: square · code · name · "Level N"; `.n-pick--blocked` with `.n-reason-chip` "This node" / "Inside this node" / "Current parent"); foot "Only limbs can be parents, and a node can't move inside itself. Other hierarchies keep their own position." Click = add move to draft.
   - **Add child** `.n-panel--brand`: title "Add a child under <code> in <HIER>"; Code (120px, mono, uppercase, maxlength 5), Name (maxlength 60), Type `.n-seg` (○ Leaf / □ Limb); errors `.n-error` + `.n-input--error`: "Enter a code and a name.", "CODE_UNIQUE · 99302 already exists in this version."; buttons Cancel / **Add to draft**.
   - **Add to another hierarchy** `.n-panel--brand`: head "Place <code> in another hierarchy (same node, new position)" + Close; one row per hierarchy: mono code · current path or "Not placed yet — pick a parent in X" · `.n-reason-chip` "Already placed · Level N" / "Available" (Available rows open the move-picker style parent list for that hierarchy); foot "A node exists once per version; placing it in another hierarchy reuses it, so its name and global properties stay in sync."
   - **Remove** `.n-panel.n-panel--warn`: title "What should happen to <code> <name>?"; `[role=radiogroup]` of three `.n-option` cards:
     1. **Inactivate** (`.n-tag-rec` "Most common") — "Keeps the node in place in ORGTL, ORGTP, AMBUS. It is flagged inactive and can be filtered out of exports; N active nodes below it stay as they are." (If already inactive: **Reactivate**.)
     2. **Remove from <HIER> only** — "Takes it out of ORGTL. It stays in the version and in ORGTP, AMBUS. N active nodes below it move out with it."
     3. **Delete from all hierarchies** — "Removes it from ORGTL, ORGTP, AMBUS. Its change history is kept for audit."
     Buttons: Keep as is (secondary sm) / confirm (`.n-btn--dark` "Add inactivation to draft" / "Add removal to draft"; `.n-btn--danger-solid` "Add delete to draft").

### 6.2 Fact strip `.n-facts.n-facts--strip` (4 columns)
| Label | Value |
|---|---|
| `PARENT IN <HIER>` | `.n-fact__link` "AGCOE · AM AI Center of Excellence" (selects parent) / "Top node" |
| `CHILDREN` | "8 · 1 inactive" / "None yet" / "None · leaf node" |
| `LEVEL IN <HIER>` | "Level 7 of 14" |
| `IN HIERARCHIES` | "3 of 3" |

### 6.3 Detail tabs `.n-dtabs` + `.n-tabpanel`
Tabs (in this order, default **Properties**): Properties · Relationships · History (`.n-dtab__count` = number of draft items for this node) · Ownership.

**Properties**
- Toolbar: filter input (`.n-input.n-input--pill.n-input--sm`, 300px, placeholder "Filter properties") · `.n-toggle` "Hide empty".
- `.n-props` table, columns **Property | Value | Source | (action)**; grouped by category header rows `.n-props__cat` (FINANCE, HR, REPORTING) — categories the user cannot see are omitted.
- Row: `.n-props__name` (label 14 + mono key 11, e.g. "Cost centre" / `E2E.CostCentre`) · `.n-props__value` (mono 600) or `.n-props__value--empty` "Not set" · source pill · action text button.
- Source pill & action:
  | Origin | Pill | Action label |
  |---|---|---|
  | Set on this node (overridden) | `.n-source--set` "Set on this node" | Edit |
  | Inherited from ancestor in ORGTL | `.n-source--inherited` "Inherited · AMT" | Override |
  | Admin default | `.n-source--default` "Default" | Override |
  | Derived (formula) | `.n-source--derived` "Derived" / "Derived from code" | "Derived" (disabled) |
  | No value | `.n-source--none` "Not set" | Set |
  | Value pending in draft | `.n-source--draft` "In your draft" + row `.n-props__row--draft` | Edit |
- Inline edit: the Value+Source+Action cells become one cell with a mono input (36px, 2px brand border, radius 10) + Cancel + **Add to draft**.
- Note under table (12px muted): "Inherited values come from the nearest ancestor in ORGTL (the controlling hierarchy). Overriding a value applies to this node and the nodes that inherit from it."

**Relationships** (two columns, wrap)
- Left: "Position in every hierarchy" (15px/700) + sub (13px muted) + `button.n-position` rows (current hierarchy `aria-current="true"`); click switches tab to that hierarchy.
- Right: "Children in <HIER>" — bordered list (radius 12, padding 6) of 40px rows: marker · mono code (green) · name · "INACTIVE" flag; click selects. Empty: dashed box "No children yet. Use “Add child…” to create one." / "Leaf nodes can’t have children."

**History** — vertical list: 28px `.n-type-dot` (type colour) · text (14/600) · meta (12 muted); draft items first with pill "IN YOUR DRAFT" (`.n-source--draft` style, uppercase 11px), then approved changes ("HR flag set to Y · e2e_steward · 5 Nov 2026, 14:20 · approved by u_approver1"), last "Copied from 2026-10 baseline · system · 1 Nov 2026, 09:00". Footer link "Open full audit for 34307 →".

**Ownership** — 2-column bordered grid (radius 12) of facts (label 11px uppercase muted, value 14/600, note 12 muted): Data steward · Owning team · Approver group ("no self-approval") · Node access group (ORG_EDITORS / AM_EDITORS, "Can edit leaves in ORGTL") · Visible categories · Last reviewed. Note: "Changes to this node are approved by the approver group before they reach the version."

## 7. Draft bar `.n-draftbar` (always visible at the bottom of `.n-main`)
- `.n-draftbar__count` (number; `--zero` when 0) · text block: title "Your draft request · 2 changes" / "No changes in your draft"; sub "Nothing changes until governance approves · Version 2026-11 Working" / "Edits you make are collected here and sent for approval together." · transient `.n-draftbar__toast` ("Move added to your draft", 4s) · `View changes` / `Hide changes` text button · `Discard` secondary sm (disabled at 0) · **Review and submit** primary (disabled at 0; opens the governance submit flow — out of scope here).
- `View changes` popover `.n-draftbar__list`: header "CHANGES IN YOUR DRAFT REQUEST"; rows `.n-type-dot` · text (click = jump to node) · × icon button (remove item).
- Draft item texts: `Move 34307 · AGQNT → AGCOE in ORGTL`, `Edit 34301 · name “QNT Analytics Platform”`, `Set Finance country = UK on 34307`, `Add 99302 “QNT Platform Ops” under AGQNT in ORGTL`, `Inactivate 34307 QNT Quant Dev`, `Remove 34307 from ORGTL`, `Delete 34307 from all hierarchies`.
- Type dots: move → `--n-moved`, edit → `--n-renamed`, property/add → `--n-added`, inactivate/reactivate → `--n-status`, remove → `--n-removed`, delete → `--n-danger` on `--n-danger-bg`.

## 8. Default state (matches the screenshot)
ORGTL tab; version 2026-11 Working; Live data; selected **34307 QNT Quant Dev**; expanded: ORGTL, OC005, AM, AMT, AMQNT, AGCOE, AGQNT; Properties tab; no panel open; draft holds **2 changes**: Move 34307 AGQNT → AGCOE (ORGTL) and Edit 34301 name → "QNT Analytics Platform". So the header shows the "In your draft" note for 34307 and the tree shows 34307 under AGCOE and 34301 renamed, both with DRAFT.

## 9. Sample data
Nodes and hierarchies as in `tree-explorer-option-d-spec.md` §8, plus **34301 QNT Analytics** (leaf under AGQNT). Before drafts, 34307 sits under AGQNT in all three hierarchies.
Properties (overrides; everything else inherits through ORGTL or uses defaults):
| Node | Overrides |
|---|---|
| ORGTL | STD_HOURS 40.00 |
| AMT | FINANCE_COUNTRY US, ACCOUNTING_CURRENCY USD, GL_COMPANY 0444, LEDGER_ID L100 |
| 31679 | FINANCE_COUNTRY UK, ACCOUNTING_CURRENCY GBP, GL_COMPANY 0512, LEDGER_ID L200, STD_HOURS 37.50 |
| 34307 | HR_FLAG Y |
| 34322 | FINANCE_COUNTRY IN, ACCOUNTING_CURRENCY INR, GL_COMPANY 0610, LEDGER_ID L300 |
Defaults: `E2E.Currency` USD, `HR_FLAG` N, `INCLUDE_IN_REPORTING` Y. Derived: `E2E.CostCentre` = code for leaves; `FULL_NAME` = code + " " + name.
Property catalogue: FINANCE — Cost centre (`E2E.CostCentre`), Finance country (`FINANCE_COUNTRY`), Oracle accounting currency (`ACCOUNTING_CURRENCY`), Oracle GL company (`GL_COMPANY`), Oracle operating ledger (`LEDGER_ID`), Reporting currency (`E2E.Currency`); HR — Standard working hours (`STD_HOURS`), HR flag (`HR_FLAG`); REPORTING — Include in reporting (`INCLUDE_IN_REPORTING`), Full name (`FULL_NAME`).

## 10. Business rules
- Integrity (validated when an item is added to the draft and again on approval): `CODE_UNIQUE`, `PARENT_EXISTS`, `PARENT_IS_LIMB`, `NO_CYCLES`; errors `{rule, message}` shown in the open panel.
- Global vs local: name, description, status and properties are global; move/add/remove are local to one hierarchy; delete affects all.
- Edits only in a Working version; Finalized → read-only.
- Approval writes change records (who, when, before, after, approver) used by Compare, As-Was and Audit.

## 11. API
| Method & path | Use |
|---|---|
| `GET /api/versions/{v}/hierarchies` | Tabs |
| `GET /api/versions/{v}/hierarchies/{h}/nodes/{id}/children?draft={draftId}` | Lazy tree with draft applied (+ `draftTouched`) |
| `GET …/hierarchies/{h}/nodes/{id}/path` · `GET …/hierarchies/{h}/search?q=` | Breadcrumb, expand-to, search |
| `GET /api/versions/{v}/nodes/{id}?draft=` | Header, facts |
| `GET /api/versions/{v}/nodes/{id}/properties?draft=` | `[{category,key,label,value,origin,inheritedFrom,editable}]` |
| `GET /api/versions/{v}/nodes/{id}/positions` · `/children` · `/history` · `/ownership` | Tabs |
| `GET …/hierarchies/{h}/nodes/{id}/move-targets` · `GET /nodes/{id}/impact` | Move picker, remove panel |
| `GET /api/drafts/current` | Draft bar |
| `POST /api/drafts/current/items` `{kind, nodeId, hierarchy?, payload}` | Add to draft (validates rules) |
| `DELETE /api/drafts/current/items/{itemId}` · `DELETE /api/drafts/current` | Remove item / discard |
| `POST /api/drafts/current/submit` | Review and submit |

## 12. Angular structure
```
features/hierarchies/
  hierarchies-page.component.ts      // route + ?node; signals: hierarchy, selectedId, openSet, panel, tab
  hierarchy-tabbar.component.ts
  tree-pane/ tree-pane.component.ts, node-search.component.ts, tree-row.component.ts (cdk virtual scroll, 40px)
  detail-pane/ detail-header.component.ts, action-panels/{edit,move,add,place,remove}.component.ts,
               fact-strip.component.ts, properties-tab.component.ts, relationships-tab.component.ts,
               history-tab.component.ts, ownership-tab.component.ts
shared/draft-bar.component.ts         // lives in the shell, visible on every editing page
core/context.service.ts · core/draft.service.ts (signals: items, count; add/remove/discard/submit)
```

## 13. Acceptance checklist
- [ ] Hamburger collapses/expands the sidebar (rail 64px ↔ 216px); tree pane 520px / 440px; no node name is cut off.
- [ ] Fixed two-pane layout: tree left, detail right; only the tree list and tab panel scroll; draft bar always visible.
- [ ] Search with full paths and recents; selecting expands and scrolls to the node.
- [ ] Tree: compact rows, guides, square/circle markers, DRAFT and INACTIVE flags, child counts, Hide inactive.
- [ ] Header: breadcrumb, icon tile, name, code · type · description, status, "In your draft" note with Undo.
- [ ] Actions: Edit details, Move…, Add child (limbs), Add to another hierarchy, Remove… — each opens its inline panel; all add to the draft, never apply directly.
- [ ] Remove panel offers Inactivate / Remove from this hierarchy / Delete with impact text.
- [ ] Fact strip; tabs Properties (grouped, source pills, inline override), Relationships, History, Ownership.
- [ ] Draft bar count, View changes list with remove, Discard, Review and submit; toast after each add.
- [ ] Read-only on Finalized versions.
- [ ] Pixel check against the mock-up at 1440 × 980 using design-system classes only.
