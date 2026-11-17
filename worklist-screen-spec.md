# Worklist screen — Build Specification (features + exact styles)

> **Give this file to the LLM/developer together with `nexus-design-system.md` and a screenshot of the canvas board "Worklist".** The shell (header `#044014`, context bar, collapsible sidebar, draft bar) comes from the design system unchanged. This file defines **every feature, rule, state, string, size and colour** of the Worklist page, plus a CSS block (§9) to **append verbatim** to `src/styles.css` after `nexus-ui.css`. Component `.scss` files may contain layout only.
>
> **Precedence:** this file > the screenshot > your own judgement. If something is not described here, **do not add it** (no extra buttons, columns, filters, icons, tooltips, helper text, animations or colours). If a rule here seems to be missing, leave the behaviour out and raise it as a question rather than inventing one.

---

## 0. Glossary (use these words exactly in the UI)

| Term | Meaning |
|---|---|
| Request | A governance change request (`CR-####`) containing 1..n changes to one version. Oracle DRM calls this a DRG request. |
| Change | One item inside a request: Move, Rename, Add, Property, Status. |
| Decide | Approve, Send back or Reject a request someone else submitted. |
| Send back | DRM's "Push back". **Never show the words "Push back", "Claim", "Unclaim", "Escalate", "Enrich" in this screen.** |
| Withdraw | The submitter cancels their own open request. |
| Resubmit | The submitter sends a sent-back request for review again. |

---

## 1. Purpose

The Worklist answers three questions, one per tab:
1. **To decide** — what is waiting for *my* decision?
2. **My requests** — what happened to the requests *I* submitted (sent back to me / still in review)?
3. **Done** — what was recently finished?

Selecting a request shows **everything needed to decide** on the right (details, changes before → after, integrity result, timeline) and **one decision bar** at the bottom.

Route: `/worklist` (optional `/worklist/:requestId` deep link, see §7.1). Sidebar item **Worklist** has `aria-current="page"` and a badge = number of requests in *To decide*.

Design rules for this page:
- Each fact appears **once** on the right side. No summary sentence that repeats the details grid. No coloured "alert" boxes.
- Colour carries meaning only for: due date (overdue/soon), status pill, outcome text, the integrity line, destructive actions.
- One line of meta per list row.

---

## 2. Domain rules (backend + frontend must both enforce)

### 2.1 Request states (server) and how they map to UI groups

| Server state | Who sees it | UI group (tab) |
|---|---|---|
| `SUBMITTED` / `IN_REVIEW` | Users in the approver group of the current workflow stage, **excluding the submitter** | `decide` (To decide) |
| `SUBMITTED` / `IN_REVIEW` | The submitter | `waiting` (My requests) |
| `SENT_BACK` | The submitter | `sentback` (My requests) |
| `APPROVED` (committed) / `REJECTED` / `WITHDRAWN` / `SENT_BACK` | The submitter and the person who decided, for **30 days** after the decision | `done` (Done) |
| `DRAFT` | Not shown on this screen — drafts live in the draft bar | — |

Notes:
- A `SENT_BACK` request appears in **Done** for the *approver* who sent it back ("Sent back by you") and in **My requests → Sent back to you** for the submitter.
- **No self-approval:** a user never sees their own request in *To decide*, even if they are in the approver group. The API must also reject `approve/sendBack/reject` from the submitter with `403 SELF_APPROVAL`.
- **No claiming in the UI.** DRM's claim step is implicit: the decision call claims and decides atomically on the server. If another approver decided first, the server returns `409 ALREADY_DECIDED` (see §7.6).
- **Approve = commit.** Approving the final workflow stage commits all changes of the request to the request's version. (Single-stage workflow for the buildathon.)
- A request can only be decided while its version is **Working**. If the version is Finalized, decision buttons are hidden and the footer text is "This version is finalized. Requests can't be decided." (see §6.6).

### 2.2 Notes (comments) rules
- **Send back** requires a note (DRM rule). **Reject** requires a note (our rule). **Approve** does not.
- Note: trimmed length 1..1000 characters. Empty → inline error (§6.5). Server returns `400 NOTE_REQUIRED` if missing.
- Anyone who can see a request can add a timeline comment (§6.4). Comments are plain text, 1..1000 chars, no formatting, no attachments, no @mentions, no edit/delete.

### 2.3 Integrity
Each request carries the result of the tree integrity rules `CODE_UNIQUE`, `PARENT_EXISTS`, `PARENT_IS_LIMB`, `NO_CYCLES`, evaluated against the version **at the time the request is opened** (server re-validates on approve). Pass → green line; fail → red line and Approve disabled (§6.3.3).

---

## 3. Layout (1440 × 980 at design time)

```
┌ .n-header (#044014) [☰] LOGO Nexus Master Data Central ………… DEVELOPMENT (VA E2E Version Admin One · e2e_vadmin Switch) ┐
├ .n-context  Version [2026-11 · Working ▾]  As of [..]  Work in (Live data|Sandbox) ……………………………… Manage versions ┤
├ rail 64 ┬ .n-main ─────────────────────────────────────────────────────────────────────────────────────────────┤
│ ⌂       │ padding 16px 20px, gap 16px                                                                        │
│ ⊞       │ ┌ List card 400px ─────────────┐ ┌ Detail card (flex 1) ──────────────────────────────────────────┐ │
│ ⌕       │ │ Worklist (h1 20/700)          │ │ CR-1041 [Awaiting your approval]                                │ │
│ ⇄       │ │ (🔍 Search by title or req…)  │ │ Update GL company for 41408   (h2 24/700)                       │ │
│ ◷       │ ├───────────────────────────────┤ │ Request details                                                 │ │
│ ───     │ │ To decide 3 | My requests 2 | Done 1 │ ┌ Requested by │ Submitted │ Due ────────────────┐       │ │
│ ☰ (3)   │ ├───────────────────────────────┤ │ │ Request type │ Hierarchy │ Version                │       │ │
│ ◫       │ │ ▌Update GL company for 41408  │ │ │ Reason for change (full width)                  │       │ │
│ ≡       │ │  CR-1041 · from u_editor1 · Overdue by 1 day │ └──────────────────────────────────────┘       │ │
│ ───     │ │  Move 3 cost centres …        │ │ Changes (1)                                                     │ │
│ ⧉       │ │  CR-1046 · from E2E Steward · Due in 2 days │ ┌ Property 41408 FI Risk Engines · …  View in tree ┐ │ │
│ ⚙       │ │  Add 2 cost centres …         │ │ │ 0̶5̶1̶2̶ → 0444                                       │ │ │
│         │ │  CR-1048 · from E2E Steward · Due in 4 days │ └─────────────────────────────────────────────┘ │ │
│         │ │                               │ │ ✓ All changes pass the tree integrity checks                    │ │
│         │ │                               │ │ Timeline  (E1) u_editor1 submitted this request · 10 Nov, 15:40 │ │
│         │ │                               │ │ ( Ask a question or add a comment            ) [Send]           │ │
│         │ │                               │ ├─────────────────────────────────────────────────────────────────┤ │
│         │ │                               │ │ Approving commits this change to 2026-11.  Reject (Send back) [Approve] │
│         │ └───────────────────────────────┘ └────────────────────────────────────────────────────────────────┘ │
│         ├ .n-draftbar (global, unchanged) ───────────────────────────────────────────────────────────────────┤
└─────────┴─────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

- Root: `.n-app.n-app--fixed` (100vh, min-height 900px, **no page scroll**).
- `.n-main` is a column: `.n-wl` (flex 1, min-height 0) + `.n-draftbar` (bottom).
- `.n-wl`: flex row, `gap 16px`, `padding 16px 20px`, `align-items: stretch`.
- **List card**: fixed `width 400px; flex 0 0 400px`. Header (title + search) and tabs are fixed; the list body scrolls.
- **Detail card**: `flex 1 1 auto; min-width 0`. Body scrolls; decision bar is pinned to the bottom of the card (not the window).
- Detail content column: `max-width 820px`, left-aligned (not centred), `padding 28px 32px 32px`, `gap 24px` between sections.
- Sidebar: collapsed rail by default. Worklist rail badge = To decide count; hidden when 0.
- Responsive (< 1100px content width): list card becomes `width 340px; flex-basis 340px`. (< 900px: out of scope for the buildathon — do not design it.)

---

## 4. List card (left)

### 4.1 Header
- Container: `padding 18px 18px 14px`, column, `gap 12px`, `border-bottom 1px solid #ECECEC`.
- Title `h1` "Worklist": 20px / 700, letter-spacing −0.01em, colour `#1F1F1F`, margin 0.
- Search (pill input with leading icon):
  - Wrapper `position: relative`. Visually hidden `<label for="wl-search">Search requests</label>`.
  - Icon: 16×16 SVG, stroke `#5C5C5C`, width 2, round caps/joins, `position:absolute; left:14px; top:12px`, paths `<circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>`.
  - Input `type="search"`, placeholder **"Search by title or request number"**: width 100%, height 40px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 14px 0 38px`, font 13px, bg `#FFFFFF`, text `#1F1F1F`. Focus: design-system focus ring.
  - Behaviour: case-insensitive *contains* match on request ID **or** title; filters **only the active tab**; 150 ms debounce; does not change tab counts.

### 4.2 Tabs
- `div[role=tablist][aria-label="Filter requests"]`: flex, `gap 4px`, `padding 0 12px`, `border-bottom 1px solid #ECECEC`.
- Three `button[role=tab]`, in this order, equal width (`flex 1 1 0`):

| Key | Label | Contains groups |
|---|---|---|
| `decide` (default) | **To decide** | decide |
| `mine` | **My requests** | sentback, waiting |
| `done` | **Done** | done |

- Tab style: height 44px, `padding 0 6px`, inline-flex centred, `gap 6px`, transparent bg, no border except `border-bottom: 3px solid` (`#368727` when active, `transparent` otherwise), `margin-bottom -1px` (sits on the divider), font 13px, weight **700 active / 500 inactive**, colour **`#1F1F1F` active / `#5C5C5C` inactive**, `white-space: nowrap`.
- Count pill inside each tab (always shown, even "0"): 11px / 700, `padding 1px 7px`, radius 999px:

| Tab | Condition | Pill bg | Pill text |
|---|---|---|---|
| To decide | count > 0 | `#368727` | `#FFFFFF` |
| My requests | at least one request is *Sent back to you* | `#FFF1DE` | `#7A4100` |
| any | otherwise | `#ECECEC` | `#4A4A4A` |

- Counts are the **unfiltered** totals per tab (search doesn't change them).
- Switching tab: clears selection, closes any open note, clears comment input; then auto-selects the first row of the new tab (§7.1). Tab choice is kept in the URL query `?tab=decide|mine|done`.
- Keyboard: Left/Right arrows move between tabs (roving tabindex), Enter/Space activates.

### 4.3 List body
- Scroll container: `flex 1 1 auto; min-height 0; overflow:auto; padding 6px 10px 14px`.
- **Group headers are shown only in the My requests tab** (two groups). In single-group tabs, render an 8px spacer instead of a header.
- Group header: `padding 14px 8px 6px`, flex, `gap 8px`, align centre.
  - `h2` 13px / 700, colour per group (below), margin 0.
  - Count pill 12px / 700, `padding 1px 8px`, radius 999px, bg `#ECECEC`, text `#4A4A4A`.

| Group | Header text | Header colour | Empty text (only when no search) |
|---|---|---|---|
| decide | Needs your decision | `#1F1F1F` | Nothing waiting on you. |
| sentback | Sent back to you | `#7A4100` | Nothing sent back. |
| waiting | Waiting on others | `#1F1F1F` | Nothing in review. |
| done | Recently handled | `#5C5C5C` | Nothing yet. |

- Empty group text: 13px, `#6B6B6B`, `padding 4px 8px 8px`.
- Search with no hits in the active tab: one line **No requests match "{query}".** — 13px, `#5C5C5C`, `padding 24px 8px` (curly quotes “ ”).

### 4.4 List row
A whole-row `<button>` (no nested interactive elements):
- `display:flex; flex-direction:column; gap:4px; width:100%; text-align:left; border:0; border-radius:12px; padding:12px; margin-bottom:2px; color:#1F1F1F; cursor:pointer`.
- Background: **selected `#E3F0DE`**, otherwise transparent; hover (not selected) `#F2F2F2`. `aria-current="true"` on the selected row.
- Line 1 — title: 14px / 600, line-height 1.4, **wraps** (never truncate with ellipsis).
- Line 2 — meta: 12px, `#5C5C5C`, then an emphasised trailing part ("extra") 12px / 600 in its own colour:

| Group | Meta (regular) | Extra (600) | Extra colour |
|---|---|---|---|
| decide | `{id} · from {submitterName} · ` | due text (§10.2) | overdue `#B42318`, due ≤ 2 days `#7A4100`, else `#5C5C5C` |
| sentback | `{id} · ` | Needs your changes | `#7A4100` |
| waiting | `{id} · ` | With {reviewerName} | `#5C5C5C` |
| done | `{id} · ` | outcome text (§10.3) | approved `#2E6B21`, sent back `#7A4100`, rejected/withdrawn `#B42318` |

- If the reviewer is not yet known (nobody opened it), waiting extra = **With Finance Approvers** (the approver group's display name).

### 4.5 Sort order
- decide: overdue first, then due soonest (`dueAt` ascending), ties by `submittedAt` ascending. Requests with no due date go last.
- sentback: `sentBackAt` descending.
- waiting: `submittedAt` descending.
- done: `decidedAt` descending; only the last 30 days; max 50 rows.

---

## 5. Detail card (right) — header and details

Card: bg `#FFFFFF`, `border 1px solid #D9D9D9`, radius 16px, column, overflow hidden. Scroll body `flex 1 1 auto; min-height 0; overflow:auto`. Inner column `max-width 820px; padding 28px 32px 32px; gap 24px`.

### 5.1 Request header
Column, `gap 10px`:
1. Row (`gap 10px`, align centre):
   - Request ID: JetBrains Mono 13px / 600, `#3D3D3D`.
   - Status pill: 12px / 700, `padding 3px 10px`, radius 999px:

| Group / outcome | Label | Text | Bg |
|---|---|---|---|
| decide | Awaiting your approval | `#2E6B21` | `#E3F0DE` |
| sentback | Sent back to you | `#7A4100` | `#FFF1DE` |
| waiting | In review | `#1D5FA8` | `#E4EEFA` |
| done · approved | Approved | `#2E6B21` | `#E3F0DE` |
| done · sent back (by me) | Sent back | `#7A4100` | `#FFF1DE` |
| done · rejected / withdrawn | Closed | `#4A4A4A` | `#ECECEC` |

2. Title `h2`: 24px / 700, line-height 1.3, letter-spacing −0.01em, margin 0, wraps.

**Nothing else goes in the header** (no avatar, no "asking you to approve" sentence, no breadcrumbs).

### 5.2 Request details (`section aria-label="Request details"`)
- Heading `h3` "Request details": 15px / 700, margin 0. Gap to grid 8px.
- Grid `<dl>`: `display:grid; grid-template-columns: repeat(3, minmax(0,1fr)); gap: 18px 24px; border:1px solid #E6E6E6; border-radius:12px; padding:18px 20px; margin:0`.
- Each fact: column, `gap 4px`, `min-width 0`; `<dt>` 12px `#5C5C5C`; `<dd>` margin 0, 14px, line-height 1.5, weight **600** (except full-width text facts: **400**), colour `#1F1F1F` unless stated. Values wrap.
- Facts, **in this exact order** (row 1 then row 2, left → right):

| # | Label | Value | Notes |
|---|---|---|---|
| 1 | Requested by | submitter display name ("You" when it's me) | |
| 2 | Submitted | date (§10.1) | |
| 3 | decide: **Due** / waiting & sentback: **Reviewer** / done: **Outcome** | due text / reviewer name (or "Finance Approvers") / outcome text | Due colour+weight: overdue `#B42318`/700, ≤ 2 days `#7A4100`/700, else `#1F1F1F`/600. Due text here uses "In 4 days" instead of "Due in 4 days"; "Overdue by 1 day" stays. |
| 4 | Request type | Org change · Property change · New node (from the request's workflow model) | |
| 5 | Hierarchy | hierarchy code (e.g. `ORGTL`) or "All hierarchies" | plain text, not mono |
| 6 | Version | `{version} Working` / `{version} Finalized` | |
| 7 | Reviewer's note | the send-back note | **sentback only**, `grid-column: 1 / -1`, weight 400 |
| 8 | Reason for change | the submitter's justification | only when present, `grid-column: 1 / -1`, weight 400 |

- No quotes around note/reason values in the grid.

---

## 6. Detail card (right) — changes, timeline, decision bar

### 6.1 Changes (`section aria-label="Changes"`)
- Heading `h3` **"Changes ({n})"**, 15px / 700. Gap 8px.
- List box: `border 1px solid #E6E6E6; border-radius 12px; overflow hidden`.
- Each change row: flex, align centre, `gap 14px`, `padding 14px 16px`, `border-top 1px solid #ECECEC` (first row: transparent).
  - Left column (`flex 1; min-width 0; gap 4px`):
    - Line 1 (14px): **kind** (600) · space · **code** (JetBrains Mono 12px, `#2E6B21`, margin `0 4px`) · **name** (400). For Property changes, name = `{node name} · {property label}`.
    - Line 2 (13px, flex, `gap 8px`, wraps): **before** (`#6B6B6B`, `text-decoration: line-through` — none when before is "New") · arrow SVG 14×14 stroke `#9E9E9E` width 2, paths `<path d="M5 12h14"/><path d="m13 6 6 6-6 6"/>`, `aria-label="changes to"` · **after** (600, `#1F1F1F`).
  - Right: link **"View in tree"** 13px / 600, `white-space:nowrap`, design-system link colours. Navigates to `/hierarchies/{hierarchy}?node={code}&version={version}&request={id}` (Hierarchies opens with the node selected and the request's pending values shown).

| Kind label | Before | After |
|---|---|---|
| Move | old parent code | new parent code |
| Rename | old name | new name |
| Add | `New` (no strike) | `Under {parentCode}` |
| Property | old value | new value |
| Status | `Active` | `Inactive` (or reverse) |

- Show all changes (no pagination) up to 50; above 50 show the first 50 and a final row "+{n} more changes · View all in tree" (13px link).

### 6.2 Integrity line (under the list, gap 8px)
- **Pass:** inline-flex, `gap 6px`, 12px, colour `#2E6B21`; check SVG 14×14 `stroke: currentColor; stroke-width 2.4`, path `m5 12 5 5 9-10`; text **"All changes pass the tree integrity checks"**.
- **Fail:** same layout, colour `#B42318`, X icon paths `M6 6l12 12M18 6 6 18` (stroke 2.4), text **"{n} change(s) fail the tree integrity checks"**; each failing change row gets a third line 12px / 600 `#B42318`: `{RULE_CODE} · {message}` (e.g. `PARENT_IS_LIMB · 99302 can't be placed under a leaf`). Approve is disabled (§6.5).

### 6.3 Timeline (`section aria-label="Timeline"`)
- Heading `h3` "Timeline", 15px / 700, margin `0 0 4px`. Section gap 6px.
- Entries, oldest first. Each entry: flex, `gap 12px`, `padding 6px 0`:
  - Avatar: 28×28 circle, bg `#F2F2F2`, text `#3D3D3D` 11px / 600, centred initials (§10.4), `aria-hidden`.
  - Text column (`gap 4px`, `padding-top 4px`):
    - 13px: **who** (600) · space · what (`#3D3D3D`) · space · `· {when}` (`#6B6B6B`).
    - Optional quoted text: 14px, line-height 1.5, `#1F1F1F`, wrapped in curly quotes “…”.
- Event "what" strings (exact):

| Event | what | Has text |
|---|---|---|
| SUBMITTED | submitted this request | no |
| RESUBMITTED | resubmitted it | no |
| REVIEW_STARTED | started reviewing it | no |
| DETAILS_ADDED | added supporting details | no |
| COMMENTED | commented | yes |
| SENT_BACK | sent it back | yes (the note) |
| REJECTED | rejected it | yes (the note) |
| APPROVED | approved it | no |
| WITHDRAWN | withdrew it | no |
| REMINDER_SENT | sent a reminder | no |

- "who" is "You" for the current user.
- **Comment input** (below entries, `padding-top 8px`, flex, `gap 8px`):
  - Hidden label "Ask a question or add a comment".
  - Input: flex 1, height 40px, `border 1.5px solid #BDBDBD`, radius 999px, `padding 0 16px`, 13px, placeholder **"Ask a question or add a comment"**.
  - Button **Send**: height 40px, `padding 0 16px`, radius 999px, `border 1.5px solid #BDBDBD`, bg `#FFFFFF`, 13px / 600.
  - Enter in the input = Send. Empty/whitespace → do nothing (no error). On success: append entry "You commented · just now" with the text, clear input, keep the same request selected, **no toast**. Comments are available in every group, including Done.
  - A comment notifies the other party (submitter ↔ reviewer) — server-side notification only, no UI here.

### 6.4 Decision bar (footer, pinned)
- Container: `flex 0 0 auto; border-top 1px solid #E6E6E6; bg #FFFFFF; padding 14px 32px;` column, `gap 12px`.
- Row: flex, align centre, `gap 10px`, wraps. Left: footer text 13px `#4A4A4A`, `flex 1 1 200px`. Right: buttons in the order listed.
- Button base: height 44px, radius 999px, `border 1.5px solid`, 14px / 600, `white-space:nowrap`. Variants:

| Variant | Padding-x | Border | Bg | Text | Hover bg |
|---|---|---|---|---|---|
| primary | 22px | `#368727` | `#368727` | `#FFFFFF` | `#2E6B21` |
| secondary | 22px | `#BDBDBD` | `#FFFFFF` | `#1F1F1F` | `#F2F2F2` |
| text | 14px | transparent | `#FFFFFF` | `#1F1F1F` | `#F2F2F2` |
| text-danger | 22px | transparent | `#FFFFFF` | `#B42318` | `#FDECEA` |
| danger | 22px | `#B42318` | `#B42318` | `#FFFFFF` | `#912018` |

- **Exact footer per state:**

| State | Footer text | Buttons (left → right) |
|---|---|---|
| decide, default | Approving commits this change to {version}. / Approving commits all {n} changes to {version}. | Reject (text-danger) · Send back (secondary) · **Approve** (primary) |
| decide, mode = back | It goes back to {submitterName} to edit and resubmit. | Cancel (text) · **Send back** (primary) |
| decide, mode = reject | Nothing will change in the hierarchy. | Cancel (text) · **Reject request** (danger) |
| sentback | Resubmitting sends it back to {reviewerName}. | Withdraw (text-danger) · Resubmit as is (secondary) · **Edit changes** (primary) |
| waiting | No action needed from you. | Withdraw (text-danger) · Send reminder (secondary) |
| done | — | **No decision bar** (the card has no footer) |

### 6.5 Note box (decide → Send back / Reject)
Clicking **Send back** or **Reject** does **not** act immediately; it switches the bar to `mode` and opens the note box *above* the button row:
- Column, `gap 8px`.
- Label (13px / 600): back → **"What should {submitterName} change?"**; reject → **"Why are you rejecting this?"**
- Textarea `rows=3`: `border 1.5px solid #BDBDBD` (error: `#B42318`), radius 12px, `padding 10px 12px`, 14px, `resize: vertical`. Placeholder: back → "e.g. Please attach the Finance approval email." ; reject → "e.g. This move is no longer needed after the reorg."
- Autofocus the textarea when it opens.
- Confirm with empty/whitespace note → border turns `#B42318` and an error line appears: **"Please add a short note so they know what to fix."** (12px / 600, `#B42318`, `aria-live="polite"`, linked via `aria-describedby`). Typing clears the error.
- Cancel → close box, discard note, return to default buttons.
- Esc inside the textarea = Cancel.
- Changing selection or tab discards the note without asking.

### 6.6 Disabled / blocked cases
- **Integrity fail:** Approve rendered with `disabled` (opacity .5, `cursor:not-allowed`); footer text becomes "Fix the integrity issues before approving." Send back and Reject stay enabled.
- **Finalized version:** no buttons; footer text "This version is finalized. Requests can't be decided."
- **Self-submitted request** can never reach the decide group (§2.1), so there is no "you can't approve your own request" UI.

### 6.7 Empty detail
When the active tab has no rows (or search hides all), the detail card shows centred (`margin:auto; padding 40px; gap 6px`):

| Tab | Title (16px / 600) | Sub (13px `#5C5C5C`) |
|---|---|---|
| To decide | You're all caught up | Nothing needs your decision right now. |
| My requests | No open requests | Requests you submit will show here. |
| Done | Nothing handled yet | Requests you decide on will show here. |

---

## 7. Behaviour

### 7.1 Selection
- On load / tab switch / search change: if the current selection is still visible in the active tab, keep it; otherwise select the **first row of the active tab in sort order** (§4.5). In My requests that's the first *Sent back* row, else the first *Waiting* row.
- URL reflects selection: `/worklist/{id}?tab={tab}`. Opening a deep link selects that request and switches to the tab containing it; if the user can't see it → toast "You don't have access to {id}" and normal default selection.
- Up/Down arrow keys on a focused row move focus; Enter selects.

### 7.2 Approve
1. Click **Approve** → optimistic UI: the request moves to Done with outcome "Approved by you · today" (§10.3), timeline gets "You approved it · just now".
2. Selection auto-advances to the **next request in To decide** (first in sort order other than the one just approved). If none remain, the empty detail state shows.
3. Toast: **"Approved {id} · committed to {version}"** with **Undo**.
4. The server call `POST /api/requests/{id}/approve` is sent **when the toast closes (6 s) or when the user starts another decision**, not before. Undo within the window cancels the call and restores the previous list and selection exactly. (This is how "Undo" works for a commit.)
5. If the server call fails: revert the UI, toast "Couldn't approve {id}. {server message}" (no Undo).

### 7.3 Send back / Reject (after a valid note)
Same pattern as Approve: optimistic move to Done ("Sent back by you · today" / "Rejected by you · today"), timeline entry with the note, auto-advance to next To decide, toast **"Sent back {id}"** / **"Rejected {id}"** with Undo; server call deferred 6 s (`POST …/send-back` / `…/reject` with `{note}`).
- After Send back, the submitter sees it under **Sent back to you** with the note as *Reviewer's note*.

### 7.4 My requests actions
- **Resubmit as is** → request moves from Sent back to Waiting (status pill "In review", reviewer kept), timeline "You resubmitted it · just now", toast **"Resubmitted {id}"** + Undo, selection stays on it. Deferred call `POST …/resubmit`.
- **Edit changes** → navigate to `/hierarchies/{hierarchy}?request={id}&mode=edit`. The request's changes load into the draft bar for editing; submitting from there resubmits the same request ID. No toast on the Worklist.
- **Withdraw** (sentback or waiting) → no confirm dialog; request moves to Done with outcome "Withdrawn by you · today", toast **"Withdrew {id}"** + Undo; selection moves to the first remaining row of My requests. Deferred call `POST …/withdraw`.
- **Send reminder** (waiting) → immediate call `POST …/remind`; toast **"Reminder sent to {reviewerName}"** (Undo is shown but does nothing other than close — implementation may hide Undo for this toast). Server allows one reminder per request per 24 h; if limited, toast "You already sent a reminder today."

### 7.5 Toast
- `role="status"`, fixed, `bottom: 84px` (clears the draft bar), horizontally centred, z-index 40.
- Pill: bg `#1F1F1F`, text `#FFFFFF` 13px, radius 999px, `padding 10px 10px 10px 18px`, inline-flex, `gap 12px`.
- Undo button: height 30px, `padding 0 12px`, radius 999px, border 0, bg `rgba(255,255,255,0.14)`, 12px / 600, white.
- Auto-dismiss after 6 s; one toast at a time (a new action flushes the previous pending call and replaces the toast).

### 7.6 Concurrency
- If a deferred decision call returns `409 ALREADY_DECIDED`: revert, then move the request to wherever the server says it now is, toast "{id} was already handled by {name}".
- Poll `GET /api/worklist/summary` every 60 s (and on window focus) to refresh tab counts and the sidebar badge; refresh the list only if counts or `etag` changed, and **never** change the current selection or close an open note because of a refresh.

### 7.7 Loading & error
- Loading list: tabs render with "–" in the count pills; list shows 4 skeleton rows (two bars: height 14px width 70%, height 10px width 45%, bg `#F2F2F2`, radius 6px, row padding 12px, gap 8px). Detail card shows skeleton blocks for title (24px × 60%), grid (3×2 bars) and one change row.
- List load error: list body shows one line "Couldn't load requests. **Retry**" (13px, Retry = link). Detail error: same text centred in the detail card.

---

## 8. Data & API (Spring Boot)

```
GET  /api/worklist/summary                         → { decide: 3, mine: 2, done: 1, sentBack: 1, etag }
GET  /api/worklist?tab=decide|mine|done            → WorklistRow[]
GET  /api/requests/{id}                            → RequestDetail
POST /api/requests/{id}/approve                    → 200 RequestDetail | 403 SELF_APPROVAL | 409 ALREADY_DECIDED | 422 INTEGRITY_FAILED | 423 VERSION_FINALIZED
POST /api/requests/{id}/send-back   {note}         → 200 | 400 NOTE_REQUIRED | 403 | 409
POST /api/requests/{id}/reject      {note}         → 200 | 400 NOTE_REQUIRED | 403 | 409
POST /api/requests/{id}/resubmit                   → 200 | 403 (not submitter) | 409
POST /api/requests/{id}/withdraw                   → 200 | 403 | 409
POST /api/requests/{id}/remind                     → 200 | 429 REMINDER_LIMIT
POST /api/requests/{id}/comments    {text}         → 201 TimelineEvent
```

```ts
type Group = 'decide' | 'sentback' | 'waiting' | 'done';
interface WorklistRow {
  id: string;              // "CR-1041"
  title: string;
  group: Group;
  submitter: { userId: string; name: string };  // name "You" never comes from the server; the client maps current user → "You"
  reviewer?: { userId: string; name: string };  // null → show approver group name
  approverGroupName: string;                    // "Finance Approvers"
  submittedAt: string; dueAt?: string; sentBackAt?: string; decidedAt?: string;  // ISO-8601
  outcome?: 'APPROVED' | 'SENT_BACK' | 'REJECTED' | 'WITHDRAWN';
  decidedBy?: { userId: string; name: string };
}
interface RequestDetail extends WorklistRow {
  requestType: 'ORG_CHANGE' | 'PROPERTY_CHANGE' | 'NEW_NODE';   // labels: Org change / Property change / New node
  hierarchy: string | null;                                       // null → "All hierarchies"
  version: { code: string; status: 'WORKING' | 'FINALIZED' };
  reason?: string; sentBackNote?: string;
  changes: { kind: 'MOVE'|'RENAME'|'ADD'|'PROPERTY'|'STATUS'; code: string; name: string;
             propertyLabel?: string; before: string | null; after: string;
             integrity?: { rule: string; message: string }[] }[];
  integrityOk: boolean;
  timeline: { type: string; actor: { userId: string; name: string }; at: string; text?: string }[];
}
```
Server rules: approver-group membership from `users.csv` roles; submitter can't decide; note required for send-back/reject; re-run integrity on approve; approve commits changes in one transaction and writes an audit record per change.

---

## 9. CSS to append to `src/styles.css` (after `nexus-ui.css`)

```css
/* ===== Worklist ===== */
.n-wl{flex:1 1 auto;min-height:0;display:flex;gap:16px;padding:16px 20px;align-items:stretch}
.n-wl-card{background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);display:flex;flex-direction:column;overflow:hidden;min-width:0}
.n-wl-list{width:400px;flex:0 0 400px}
.n-wl-detail{flex:1 1 auto}
.n-wl-list__head{padding:18px 18px 14px;display:flex;flex-direction:column;gap:12px;border-bottom:1px solid var(--n-divider)}
.n-wl-list__title{margin:0;font-size:20px;font-weight:700;letter-spacing:-.01em}
.n-wl-search{position:relative}
.n-wl-search svg{position:absolute;left:14px;top:12px;width:16px;height:16px;stroke:var(--n-text-muted);stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-wl-search input{width:100%;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 14px 0 38px;font-size:13px;background:var(--n-surface);color:var(--n-text)}
.n-wl-tabs{display:flex;gap:4px;padding:0 12px;border-bottom:1px solid var(--n-divider)}
.n-wl-tab{flex:1 1 0;display:inline-flex;align-items:center;justify-content:center;gap:6px;height:44px;padding:0 6px;background:transparent;border:0;border-bottom:3px solid transparent;margin-bottom:-1px;font-size:13px;font-weight:500;color:var(--n-text-muted);white-space:nowrap}
.n-wl-tab[aria-selected="true"]{border-bottom-color:var(--n-brand);font-weight:700;color:var(--n-text)}
.n-wl-count{font-size:11px;font-weight:700;border-radius:var(--n-r-pill);padding:1px 7px;background:var(--n-neutral-bg);color:var(--n-text-3)}
.n-wl-count--brand{background:var(--n-brand);color:#fff}
.n-wl-count--warn{background:var(--n-status-bg);color:var(--n-status)}
.n-wl-list__body{flex:1 1 auto;min-height:0;overflow:auto;padding:6px 10px 14px}
.n-wl-spacer{height:8px}
.n-wl-group{padding:14px 8px 6px;display:flex;align-items:center;gap:8px}
.n-wl-group h2{margin:0;font-size:13px;font-weight:700}
.n-wl-group--sentback h2{color:var(--n-status)}
.n-wl-group--done h2{color:var(--n-text-muted)}
.n-wl-group .n-wl-count{font-size:12px;padding:1px 8px}
.n-wl-empty{padding:4px 8px 8px;font-size:13px;color:var(--n-text-faint)}
.n-wl-nomatch{padding:24px 8px;font-size:13px;color:var(--n-text-muted)}
.n-wl-row{display:flex;flex-direction:column;gap:4px;width:100%;text-align:left;background:transparent;border:0;border-radius:var(--n-r-tile);padding:12px;margin-bottom:2px;color:var(--n-text)}
.n-wl-row:hover{background:var(--n-surface-sunk)}
.n-wl-row[aria-current="true"]{background:var(--n-brand-50)}
.n-wl-row__title{font-size:14px;font-weight:600;line-height:1.4}
.n-wl-row__meta{font-size:12px;color:var(--n-text-muted)}
.n-wl-row__extra{font-weight:600}
.n-wl-tone--danger{color:var(--n-danger)} .n-wl-tone--warn{color:var(--n-status)} .n-wl-tone--ok{color:var(--n-brand-700)} .n-wl-tone--muted{color:var(--n-text-muted)}
.n-wl-detail__scroll{flex:1 1 auto;min-height:0;overflow:auto}
.n-wl-detail__inner{max-width:820px;padding:28px 32px 32px;display:flex;flex-direction:column;gap:24px}
.n-wl-head{display:flex;flex-direction:column;gap:10px}
.n-wl-head__line{display:flex;align-items:center;gap:10px}
.n-wl-head__id{font-family:var(--n-mono);font-size:13px;font-weight:600;color:var(--n-text-2)}
.n-wl-head__title{margin:0;font-size:24px;font-weight:700;line-height:1.3;letter-spacing:-.01em}
.n-wl-status{font-size:12px;font-weight:700;border-radius:var(--n-r-pill);padding:3px 10px}
.n-wl-status--approval,.n-wl-status--approved{color:var(--n-brand-700);background:var(--n-brand-50)}
.n-wl-status--back{color:var(--n-status);background:var(--n-status-bg)}
.n-wl-status--review{color:var(--n-moved);background:var(--n-moved-bg)}
.n-wl-status--closed{color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-wl-section{display:flex;flex-direction:column;gap:8px}
.n-wl-section h3{margin:0;font-size:15px;font-weight:700}
.n-wl-facts{margin:0;display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:18px 24px;border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);padding:18px 20px}
.n-wl-fact{display:flex;flex-direction:column;gap:4px;min-width:0}
.n-wl-fact--wide{grid-column:1 / -1}
.n-wl-fact dt{font-size:12px;color:var(--n-text-muted)}
.n-wl-fact dd{margin:0;font-size:14px;font-weight:600;line-height:1.5;color:var(--n-text)}
.n-wl-fact--wide dd{font-weight:400}
.n-wl-fact dd.n-wl-due--over{color:var(--n-danger);font-weight:700}
.n-wl-fact dd.n-wl-due--soon{color:var(--n-status);font-weight:700}
.n-wl-changes{border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);overflow:hidden}
.n-wl-change{display:flex;align-items:center;gap:14px;padding:14px 16px;border-top:1px solid var(--n-divider)}
.n-wl-change:first-child{border-top-color:transparent}
.n-wl-change__body{display:flex;flex-direction:column;gap:4px;flex:1 1 auto;min-width:0}
.n-wl-change__line{font-size:14px}
.n-wl-change__kind{font-weight:600}
.n-wl-change__code{font-family:var(--n-mono);font-size:12px;color:var(--n-brand-700);margin:0 4px}
.n-wl-change__diff{display:flex;align-items:center;gap:8px;flex-wrap:wrap;font-size:13px}
.n-wl-change__before{color:var(--n-text-faint);text-decoration:line-through}
.n-wl-change__before--new{text-decoration:none}
.n-wl-change__diff svg{width:14px;height:14px;stroke:var(--n-separator);stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-wl-change__after{font-weight:600;color:var(--n-text)}
.n-wl-change__issue{font-size:12px;font-weight:600;color:var(--n-danger)}
.n-wl-change a{font-size:13px;white-space:nowrap}
.n-wl-integrity{display:inline-flex;align-items:center;gap:6px;font-size:12px;color:var(--n-brand-700)}
.n-wl-integrity--fail{color:var(--n-danger)}
.n-wl-integrity svg{width:14px;height:14px;stroke:currentColor;stroke-width:2.4;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-wl-timeline{display:flex;flex-direction:column;gap:6px}
.n-wl-timeline h3{margin:0 0 4px}
.n-wl-event{display:flex;gap:12px;padding:6px 0}
.n-wl-avatar{width:28px;height:28px;flex:0 0 28px;border-radius:50%;background:var(--n-surface-sunk);color:var(--n-text-2);display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:600}
.n-wl-event__body{display:flex;flex-direction:column;gap:4px;min-width:0;padding-top:4px}
.n-wl-event__line{font-size:13px}
.n-wl-event__who{font-weight:600}
.n-wl-event__what{color:var(--n-text-2)}
.n-wl-event__when{color:var(--n-text-faint)}
.n-wl-event__text{font-size:14px;line-height:1.5;color:var(--n-text)}
.n-wl-comment{display:flex;gap:8px;padding-top:8px}
.n-wl-comment input{flex:1 1 auto;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 16px;font-size:13px;background:var(--n-surface)}
.n-wl-comment button{height:40px;padding:0 16px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-input);background:var(--n-surface);font-size:13px;font-weight:600}
.n-wl-bar{flex:0 0 auto;border-top:1px solid var(--n-divider-2);background:var(--n-surface);padding:14px 32px;display:flex;flex-direction:column;gap:12px}
.n-wl-bar__row{display:flex;align-items:center;gap:10px;flex-wrap:wrap}
.n-wl-bar__text{font-size:13px;color:var(--n-text-3);flex:1 1 200px}
.n-wl-note{display:flex;flex-direction:column;gap:8px}
.n-wl-note label{font-size:13px;font-weight:600}
.n-wl-note textarea{border:1.5px solid var(--n-border-input);border-radius:var(--n-r-tile);padding:10px 12px;font-size:14px;resize:vertical;background:var(--n-surface);color:var(--n-text)}
.n-wl-note textarea[aria-invalid="true"]{border-color:var(--n-danger)}
.n-wl-note__err{font-size:12px;font-weight:600;color:var(--n-danger)}
.n-wl-btn{height:44px;padding:0 22px;border-radius:var(--n-r-pill);border:1.5px solid transparent;background:var(--n-surface);color:var(--n-text);font-size:14px;font-weight:600;white-space:nowrap}
.n-wl-btn--primary{background:var(--n-brand);border-color:var(--n-brand);color:#fff}
.n-wl-btn--primary:hover{background:var(--n-brand-700)}
.n-wl-btn--secondary{border-color:var(--n-border-input)}
.n-wl-btn--secondary:hover,.n-wl-btn--text:hover{background:var(--n-surface-sunk)}
.n-wl-btn--text{padding:0 14px}
.n-wl-btn--text-danger{color:var(--n-danger)}
.n-wl-btn--text-danger:hover{background:var(--n-danger-bg)}
.n-wl-btn--danger{background:var(--n-danger);border-color:var(--n-danger);color:#fff}
.n-wl-btn--danger:hover{background:#912018}
.n-wl-blank{margin:auto;display:flex;flex-direction:column;align-items:center;gap:6px;padding:40px}
.n-wl-blank strong{font-size:16px;font-weight:600}
.n-wl-blank span{font-size:13px;color:var(--n-text-muted)}
.n-wl-toast{position:fixed;bottom:84px;left:50%;transform:translateX(-50%);display:inline-flex;align-items:center;gap:12px;background:var(--n-text);color:#fff;border-radius:var(--n-r-pill);padding:10px 10px 10px 18px;font-size:13px;z-index:40}
.n-wl-toast button{height:30px;padding:0 12px;border:0;border-radius:var(--n-r-pill);background:rgba(255,255,255,.14);color:#fff;font-size:12px;font-weight:600}
@media (max-width:1240px){.n-wl-list{width:340px;flex-basis:340px}}
```

---

## 10. Copy & formatting (exact)

### 10.1 Dates
- Same year: `10 Nov`; other year: `10 Nov 2025`; today: `today`.
- Timeline: `10 Nov, 15:40` (24 h, user's timezone); within the last minute: `just now`.

### 10.2 Due text (calendar days, user's timezone)
| Condition | List / due row | Details "Due" fact | Tone |
|---|---|---|---|
| dueAt < today | Overdue by 1 day / Overdue by {n} days | same | danger |
| dueAt = today | Due today | Today | warn |
| 1–2 days | Due in 1 day / Due in 2 days | In 1 day / In 2 days | warn |
| ≥ 3 days | Due in {n} days | In {n} days | normal |
| no due date | (no extra; meta ends with `from {name}`) | — (fact shows "No due date", 400 weight) | normal |

### 10.3 Outcome text
`Approved by {name} · {date}`, `Sent back by {name} · {date}`, `Rejected by {name} · {date}`, `Withdrawn by {name} · {date}` — `{name}` is "you" (lower-case) for the current user: "Approved by you · today".

### 10.4 Initials
Use the `initials` field from `users.csv`: e2e_vadmin `VA`, e2e_steward `ES`, u_editor1 `E1`, u_approver1 `A1`, u_admin `UA`. Only if a user has no `initials` value: first letter of the first two words of the display name, upper-case.

### 10.5 Toast strings
`Approved {id} · committed to {version}` · `Sent back {id}` · `Rejected {id}` · `Resubmitted {id}` · `Withdrew {id}` · `Reminder sent to {name}` · `You already sent a reminder today.` · `{id} was already handled by {name}` · `Couldn't approve {id}. {message}`

---

## 11. HTML contract (Angular template skeleton)

```html
<div class="n-wl">
  <section class="n-wl-card n-wl-list" aria-label="Requests">
    <div class="n-wl-list__head">
      <h1 class="n-wl-list__title">Worklist</h1>
      <div class="n-wl-search">
        <label class="n-sr-only" for="wl-search">Search requests</label>
        <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>
        <input id="wl-search" type="search" placeholder="Search by title or request number" [formControl]="query">
      </div>
    </div>
    <div class="n-wl-tabs" role="tablist" aria-label="Filter requests">
      <button *ngFor="let t of tabs" class="n-wl-tab" role="tab" [attr.aria-selected]="t.key===tab" (click)="setTab(t.key)">
        {{t.label}} <span class="n-wl-count" [ngClass]="t.countClass">{{t.count}}</span>
      </button>
    </div>
    <div class="n-wl-list__body">
      <ng-container *ngFor="let g of groups">
        <div *ngIf="groups.length > 1; else spacer" class="n-wl-group" [ngClass]="'n-wl-group--'+g.key">
          <h2>{{g.label}}</h2><span class="n-wl-count">{{g.rows.length}}</span>
        </div>
        <ng-template #spacer><div class="n-wl-spacer"></div></ng-template>
        <div *ngIf="!g.rows.length && !query.value" class="n-wl-empty">{{g.emptyText}}</div>
        <button *ngFor="let r of g.rows" class="n-wl-row" [attr.aria-current]="r.id===selectedId" (click)="select(r.id)">
          <span class="n-wl-row__title">{{r.title}}</span>
          <span class="n-wl-row__meta">{{r.meta}}<span class="n-wl-row__extra" [ngClass]="r.extraTone">{{r.extra}}</span></span>
        </button>
      </ng-container>
      <div *ngIf="noMatch" class="n-wl-nomatch">No requests match “{{query.value}}”.</div>
    </div>
  </section>

  <section class="n-wl-card n-wl-detail" aria-label="Request">
    <div *ngIf="!detail" class="n-wl-blank"><strong>{{blank.title}}</strong><span>{{blank.sub}}</span></div>
    <ng-container *ngIf="detail as d">
      <div class="n-wl-detail__scroll"><div class="n-wl-detail__inner">
        <div class="n-wl-head">
          <span class="n-wl-head__line"><span class="n-wl-head__id">{{d.id}}</span><span class="n-wl-status" [ngClass]="d.statusClass">{{d.statusLabel}}</span></span>
          <h2 class="n-wl-head__title">{{d.title}}</h2>
        </div>
        <section class="n-wl-section" aria-label="Request details">
          <h3>Request details</h3>
          <dl class="n-wl-facts">
            <div *ngFor="let f of d.facts" class="n-wl-fact" [class.n-wl-fact--wide]="f.wide"><dt>{{f.label}}</dt><dd [ngClass]="f.tone">{{f.value}}</dd></div>
          </dl>
        </section>
        <section class="n-wl-section" aria-label="Changes">
          <h3>Changes ({{d.changes.length}})</h3>
          <div class="n-wl-changes">
            <div *ngFor="let c of d.changes" class="n-wl-change">
              <div class="n-wl-change__body">
                <span class="n-wl-change__line"><span class="n-wl-change__kind">{{c.kindLabel}}</span> <span class="n-wl-change__code">{{c.code}}</span>{{c.name}}</span>
                <span class="n-wl-change__diff">
                  <span class="n-wl-change__before" [class.n-wl-change__before--new]="c.isNew">{{c.before}}</span>
                  <svg viewBox="0 0 24 24" aria-label="changes to"><path d="M5 12h14"/><path d="m13 6 6 6-6 6"/></svg>
                  <span class="n-wl-change__after">{{c.after}}</span>
                </span>
                <span *ngFor="let i of c.integrity" class="n-wl-change__issue">{{i.rule}} · {{i.message}}</span>
              </div>
              <a [routerLink]="c.treeLink" [queryParams]="c.treeParams">View in tree</a>
            </div>
          </div>
          <span class="n-wl-integrity" [class.n-wl-integrity--fail]="!d.integrityOk"><!-- check or X svg -->{{d.integrityText}}</span>
        </section>
        <section class="n-wl-section n-wl-timeline" aria-label="Timeline">
          <h3>Timeline</h3>
          <div *ngFor="let e of d.timeline" class="n-wl-event">
            <span class="n-wl-avatar" aria-hidden="true">{{e.initials}}</span>
            <div class="n-wl-event__body">
              <span class="n-wl-event__line"><span class="n-wl-event__who">{{e.who}}</span> <span class="n-wl-event__what">{{e.what}}</span> <span class="n-wl-event__when">· {{e.when}}</span></span>
              <span *ngIf="e.text" class="n-wl-event__text">“{{e.text}}”</span>
            </div>
          </div>
          <form class="n-wl-comment" (ngSubmit)="comment()">
            <label class="n-sr-only" for="wl-reply">Ask a question or add a comment</label>
            <input id="wl-reply" type="text" placeholder="Ask a question or add a comment" [formControl]="reply">
            <button type="submit">Send</button>
          </form>
        </section>
      </div></div>
      <div *ngIf="bar" class="n-wl-bar">
        <div *ngIf="mode" class="n-wl-note">
          <label for="wl-note">{{bar.noteLabel}}</label>
          <textarea id="wl-note" rows="3" [placeholder]="bar.notePlaceholder" [formControl]="note" [attr.aria-invalid]="noteErr" aria-describedby="wl-note-err" cdkFocusInitial></textarea>
          <span *ngIf="noteErr" id="wl-note-err" class="n-wl-note__err" aria-live="polite">Please add a short note so they know what to fix.</span>
        </div>
        <div class="n-wl-bar__row">
          <span class="n-wl-bar__text">{{bar.text}}</span>
          <button *ngFor="let b of bar.buttons" type="button" class="n-wl-btn" [ngClass]="'n-wl-btn--'+b.variant" [disabled]="b.disabled" (click)="b.run()">{{b.label}}</button>
        </div>
      </div>
    </ng-container>
  </section>
</div>
<div *ngIf="toast" class="n-wl-toast" role="status">{{toast.text}} <button type="button" (click)="undo()">Undo</button></div>
<app-draft-bar />
```

---

## 12. Mock data (use exactly this for demos and tests; current user = e2e_vadmin "E2E Version Admin One", version 2026-11 Working, today = 17 Nov)

| ID | Group | Title | Type | Hierarchy | From | Submitted | Due / Reviewer / Outcome | Reason / Note |
|---|---|---|---|---|---|---|---|---|
| CR-1041 | decide | Update GL company for 41408 | Property change | All hierarchies | u_editor1 | 10 Nov | Overdue by 1 day | Reason: Finance asked for this before the month-end export. |
| CR-1046 | decide | Move 3 cost centres from AMFIT to AMEQT | Org change | ORGTL | E2E Steward | 14 Nov | Due in 2 days | Reason: FI trading teams move under Equity as part of the Q4 reorg (FIN-2231). |
| CR-1048 | decide | Add 2 cost centres under AGCOE | New node | ORGTL | E2E Steward | 16 Nov | Due in 4 days | — |
| CR-1044 | sentback | Rename AGCOE to “AM AI Centre of Excellence” | Org change | All hierarchies | You | 15 Nov | Reviewer u_approver1 | Note: Please confirm the spelling with Finance before resubmitting. |
| CR-1045 | waiting | Inactivate 34322 QNT Contingent | Org change | All hierarchies | You | 15 Nov | Reviewer u_approver1 | — |
| CR-1040 | done | Inactivate AMOTH AM Technology All Other | Org change | All hierarchies | You | 11 Nov | Approved by u_approver1 · 11 Nov | — |

Changes:
- CR-1041: Property `41408` FI Risk Engines · Oracle GL company: `0512` → `0444`
- CR-1046: Move `41402` FI Trading Applications `AMFIT` → `AMEQT`; Move `41408` FI Risk Engines `AMFIT` → `AMEQT`; Move `41415` FI Analytics `AMFIT` → `AMEQT`
- CR-1048: Add `99302` AI Platform Ops `New` → `Under AGCOE`; Add `99303` AI Research Tools `New` → `Under AGCOE`
- CR-1044: Rename `AGCOE`: `AM AI Center of Excellence` → `AM AI Centre of Excellence`
- CR-1045: Status `34322` QNT Contingent `Active` → `Inactive`
- CR-1040: Status `AMOTH` AM Technology All Other `Active` → `Inactive`

Timelines:
- CR-1041: E1 u_editor1 submitted this request · 10 Nov, 15:40 · UA u_admin added supporting details · 11 Nov, 09:10
- CR-1046: ES E2E Steward submitted this request · 14 Nov, 11:20
- CR-1048: ES E2E Steward submitted this request · 16 Nov, 09:05
- CR-1044: VA You submitted this request · 15 Nov, 10:00 · A1 u_approver1 sent it back · 15 Nov, 17:02 “Please confirm the spelling with Finance before resubmitting.”
- CR-1045: VA You submitted this request · 15 Nov, 12:30 · A1 u_approver1 started reviewing it · 15 Nov, 14:10
- CR-1040: VA You submitted this request · 10 Nov · A1 u_approver1 approved it · 11 Nov

Expected first render: tab **To decide** active (pill "3" green), My requests pill "2" amber, Done "1" grey; CR-1041 selected; sidebar Worklist badge 3.

---

## 13. Accessibility
- One `h1` (Worklist). Group headers `h2` (list), request title `h2` (detail), section headings `h3`.
- Tabs follow the WAI-ARIA tabs pattern (`role=tab`, `aria-selected`, arrow keys). The list body is the tabpanel (`role="tabpanel"`, `aria-labelledby` = active tab id).
- Rows are buttons with `aria-current="true"` when selected; the detail `section` has `aria-label="Request"`.
- Visually hidden labels for search and comment inputs (`.n-sr-only`).
- Note textarea: `aria-invalid`, `aria-describedby` to the error; error `aria-live="polite"`.
- Toast `role="status"`; Undo reachable by keyboard; toast stays while it has focus.
- Contrast ≥ 4.5:1 for all text (`#5C5C5C`, `#7A4100`, `#B42318`, `#2E6B21` on white all pass).
- Focus ring: design-system default on every interactive element.

---

## 14. Do NOT
- Don't add a "summary"/"asking you to approve" box or repeat any fact outside the details grid.
- Don't use coloured background banners/alerts on the detail side (only pills and the integrity line carry colour).
- Don't show Claim/Unclaim/Escalate, a ⋯ overflow menu, checkboxes, bulk actions, column tables, sort dropdowns, or filters other than the three tabs + search.
- Don't use a modal/confirm dialog for any action — Undo replaces confirmation.
- Don't truncate titles or names with ellipsis — wrap.
- Don't centre the detail content column; it is left-aligned with max-width 820px.
- Don't change the shell, sidebar order or draft bar.
- Don't invent extra statuses, colours, icons or copy.

---

## 15. Acceptance checklist
- [ ] Shell identical to other screens; Worklist active in the rail with badge = To decide count.
- [ ] List card 400px; title 20/700; pill search 40px with icon at 14px/12px.
- [ ] Tabs To decide / My requests / Done, equal width, 44px, 3px green underline on active, count pills coloured per §4.2.
- [ ] Group headers only in My requests; rows wrap, selected bg `#E3F0DE`, meta + coloured extra per §4.4.
- [ ] Sorting per §4.5; first row auto-selected on tab change.
- [ ] Detail header = mono ID + status pill + 24/700 title, nothing else.
- [ ] Request details grid: 3 columns, facts in the §5.2 order, conditional third fact, full-width note/reason rows.
- [ ] Changes list with kind · mono code · name and before (struck) → after; "View in tree" deep link.
- [ ] Integrity line pass/fail variants; Approve disabled on fail.
- [ ] Timeline entries + comment input; Enter sends; no toast for comments.
- [ ] Decision bar exactly per §6.4 table for every state; no bar in Done.
- [ ] Send back / Reject require a note with the exact error text.
- [ ] After a decision: item moves to Done, next To decide auto-selected, toast with Undo, server call deferred 6 s, Undo restores list and selection.
- [ ] Self-approval impossible (UI and API); 409 handled with toast.
- [ ] Empty, loading, error states per §6.7 and §7.7.
- [ ] Pixel check vs. the "Worklist" board at 1440px: computed styles match §4–§6 and §9.
