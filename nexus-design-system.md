# Nexus · Master Data Central — UI Design System (exact CSS)

> **Purpose:** one source of truth for every screen of *Nexus – Master Data Central* (our MDC replacement). Give this file to any LLM or developer building the Angular UI. **Copy §3 (`nexus-ui.css`) verbatim into the app's global styles and build every screen only with these classes and tokens.** That is how the running app and the mock-ups stay pixel-identical.
>
> Hard rules:
> 1. **Do not invent colours, sizes, radii, shadows or fonts.** Every value is a token in §2. If something is missing, add a token here first.
> 2. **No component-library defaults.** Do not use Angular Material / PrimeNG / Bootstrap themes; if a library is used for behaviour (e.g. CDK virtual scroll, overlay), it must be unstyled.
> 3. **No per-component visual CSS overrides.** Component `.scss` files may contain layout only (`display`, `flex`, `grid`, `gap`, widths). Colours, type and shapes come from the global classes.
> 4. Use real `<button>`, `<a href>`, `<input>` + `<label>`. Never click handlers on `div`/`span`.

Screens using this system: Shell (all pages), Hierarchies (Option D), Queries, Compare, As-Was, and future Worklist / Sandboxes / Versions / Audit / Settings.

---

## 1. Principles

| Principle | Meaning in UI |
|---|---|
| Shape language | **Round = you can click it** (pills, chips, buttons, cards). **Square = limb** (can hold children). **Circle = leaf** (cost centre). Thin vertical guide lines = depth. Orange triangle = changed in this version. |
| Colour never alone | Every status/change carries a text label and/or a symbol. |
| Calm | White cards on a soft grey page, one brand green, one warning orange; no gradients, no shadows except overlays, no decorative left-border stripes, no emoji. |
| Codes are monospace | Node codes, paths, version codes, user IDs use the mono font. |
| Touch targets | Every interactive control ≥ 36 px tall; primary controls 40–44 px. |
| Contrast | Body and label text ≥ 4.5:1. The lightest text grey allowed is `--n-text-faint` `#6B6B6B`. |

---

## 2. Design tokens

### 2.1 Colour
| Token | Value | Use |
|---|---|---|
| `--n-brand` | `#368727` | Header bar, primary button, active tab underline, selected-node icon, toggle on, focus ring |
| `--n-brand-700` | `#2E6B21` | Node codes, links, crumb links, level chip text, active nav icon |
| `--n-brand-800` | `#1F4D16` | Active nav text, "Active" status text, link hover |
| `--n-brand-50` | `#E3F0DE` | Selected tree row, active nav item, Active status bg, avatar bg |
| `--n-brand-40` | `#EEF5EB` | Level chip bg, icon tile bg, selected column chip bg |
| `--n-brand-30` | `#F2F8EF` | Selected list row, move-picker header, "plain words" box |
| `--n-brand-20` | `#F4FAF1` | Current row in "position" card |
| `--n-brand-border` | `#B9D9AE` | Border of current row in "position" card |
| `--n-page` | `#F5F6F4` | Page background |
| `--n-surface` | `#FFFFFF` | Cards, header sub-bars, sidebar |
| `--n-surface-alt` | `#FAFAF8` | Card header strips, table header row |
| `--n-surface-sunk` | `#F2F2F2` | Segmented-control track |
| `--n-chip` / `--n-chip-border` | `#F2F4EF` / `#DCE3D7` | "Recent" chips |
| `--n-text` | `#1F1F1F` | Primary text, dark buttons, toast bg |
| `--n-text-2` | `#3D3D3D` | Icons, limb markers, paths |
| `--n-text-3` | `#4A4A4A` | Descriptions, inactive tab text, secondary values |
| `--n-text-muted` | `#5C5C5C` | Labels, meta, codes in tree |
| `--n-text-faint` | `#6B6B6B` | Child counts, leaf marker, group labels |
| `--n-separator` | `#9E9E9E` | Breadcrumb "/" |
| `--n-border` | `#D9D9D9` | Card borders, bar bottom borders |
| `--n-border-input` | `#BDBDBD` | Inputs, selects, secondary buttons, toggle off |
| `--n-border-soft` | `#DCDCDC` | Inactive filter/column chips |
| `--n-divider` | `#ECECEC` | Dividers inside cards |
| `--n-divider-2` | `#E6E6E6` | Strip borders, legend top border |
| `--n-divider-3` | `#EEEEEE` | List item separators |
| `--n-sidebar-border` | `#E3E3E3` | Sidebar right border |
| `--n-guide` | `#D4D4D4` | Tree guide lines |
| `--n-neutral-bg` | `#ECECEC` | Inactive status bg, blocked-reason chip |
| `--n-warn` | `#C77700` | Changed triangle, impact panel border |
| `--n-warn-text` | `#7A4100` | "CHANGED" label text |
| `--n-warn-bg` | `#FFF8EE` | Impact panel, stale-results bar |
| `--n-success-on-dark` | `#A8CF9C` | Check icon in toast |
| `--n-danger` / `--n-danger-bg` / `--n-danger-border` | `#B42318` / `#FDECEA` / `#E3B9B4` | Remove/Delete button text, delete confirm, validation errors |
| `--n-draft-row` | `#FFFCF6` | Property row with a pending draft value |
| Change types | Added `#2E6B21`/`#E3F0DE` · Removed `#3D3D3D`/`#E6E6E6` · Moved `#1D5FA8`/`#E4EEFA` · Renamed `#6B3FA0`/`#EFE7F8` · Status `#7A4100`/`#FFF1DE` | Compare / As-Was / Audit (fg / tint) |

### 2.2 Typography
- **UI:** `Inter` 400/500/600/700 · fallback `system-ui, -apple-system, "Segoe UI", sans-serif`.
- **Mono:** `JetBrains Mono` 400/500/600 · fallback `ui-monospace, Consolas, monospace`.
- Load once in `index.html`:
  `<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">`
  (If CDN fonts are blocked internally, self-host the same files; never substitute another family.)

| Token | Size / weight / line-height | Use |
|---|---|---|
| `--n-fs-display` | 26px / 700 / 1.2, letter-spacing −0.01em | Node name in details card |
| `--n-fs-h1` | 24px / 700 / 1.25, −0.01em | Page title |
| `--n-fs-h2` | 16px / 700 / 1.3 | Card titles |
| `--n-fs-h3` | 15px / 700 | Tab titles, section titles |
| `--n-fs-body` | 14px / 400–600 / 1.5 | Body, tree names, buttons |
| `--n-fs-small` | 13px | Secondary text, chips, links |
| `--n-fs-caption` | 12px | Labels, meta |
| `--n-fs-micro` | 11px / 700, letter-spacing 0.06em, UPPERCASE | Group labels, fact labels, badges |

### 2.3 Spacing, radius, elevation, motion
| Token | Value |
|---|---|
| Spacing scale `--n-s-*` | 2, 4, 6, 8, 10, 12, 14, 16, 20, 24, 28, 32 px |
| `--n-r-card` | 16px |
| `--n-r-panel` | 14px |
| `--n-r-tile` | 12px |
| `--n-r-row` | 10px (lists, nav items) |
| `--n-r-tree` | 8px (tree rows) |
| `--n-r-pill` | 999px |
| `--n-shadow-overlay` | `0 12px 32px rgba(0,0,0,0.14)` — dropdowns/popovers only |
| `--n-shadow-raised` | `0 1px 2px rgba(0,0,0,0.14)` — active segment in a raised segmented control |
| Motion | 120ms ease for background/colour; chevron rotate 150ms; no other animation |
| Layout widths | Sidebar 216px; content max 1600px; designed at 1440px |

---

## 3. `nexus-ui.css` — copy verbatim into `src/styles.css`

```css
/* ===== Nexus UI · tokens ===== */
:root{
  --n-brand:#368727; --n-brand-700:#2E6B21; --n-brand-800:#1F4D16;
  --n-brand-50:#E3F0DE; --n-brand-40:#EEF5EB; --n-brand-30:#F2F8EF; --n-brand-20:#F4FAF1; --n-brand-border:#B9D9AE;
  --n-page:#F5F6F4; --n-surface:#FFFFFF; --n-surface-alt:#FAFAF8; --n-surface-sunk:#F2F2F2;
  --n-chip:#F2F4EF; --n-chip-border:#DCE3D7;
  --n-text:#1F1F1F; --n-text-2:#3D3D3D; --n-text-3:#4A4A4A; --n-text-muted:#5C5C5C; --n-text-faint:#6B6B6B; --n-separator:#9E9E9E;
  --n-border:#D9D9D9; --n-border-input:#BDBDBD; --n-border-soft:#DCDCDC;
  --n-divider:#ECECEC; --n-divider-2:#E6E6E6; --n-divider-3:#EEEEEE; --n-sidebar-border:#E3E3E3;
  --n-guide:#D4D4D4; --n-neutral-bg:#ECECEC;
  --n-warn:#C77700; --n-warn-text:#7A4100; --n-warn-bg:#FFF8EE; --n-success-on-dark:#A8CF9C;
  --n-added:#2E6B21; --n-added-bg:#E3F0DE; --n-removed:#3D3D3D; --n-removed-bg:#E6E6E6;
  --n-moved:#1D5FA8; --n-moved-bg:#E4EEFA; --n-renamed:#6B3FA0; --n-renamed-bg:#EFE7F8;
  --n-status:#7A4100; --n-status-bg:#FFF1DE;
  --n-danger:#B42318; --n-danger-bg:#FDECEA; --n-danger-border:#E3B9B4; --n-draft-row:#FFFCF6;
  --n-font:Inter,system-ui,-apple-system,"Segoe UI",sans-serif;
  --n-mono:"JetBrains Mono",ui-monospace,Consolas,monospace;
  --n-r-card:16px; --n-r-panel:14px; --n-r-tile:12px; --n-r-row:10px; --n-r-tree:8px; --n-r-pill:999px;
  --n-shadow-overlay:0 12px 32px rgba(0,0,0,.14); --n-shadow-raised:0 1px 2px rgba(0,0,0,.14);
  --n-sidebar-w:216px;
}

/* ===== base ===== */
*,*::before,*::after{box-sizing:border-box}
html,body{margin:0;padding:0}
body{background:var(--n-page);color:var(--n-text);font-family:var(--n-font);font-size:14px;line-height:1.5;-webkit-font-smoothing:antialiased}
button,input,select,textarea{font:inherit;color:inherit}
button{cursor:pointer}
button:disabled{cursor:not-allowed;opacity:.5}
a{color:var(--n-brand-700);font-weight:600;text-decoration:none}
a:hover{color:var(--n-brand-800);text-decoration:underline;text-underline-offset:3px}
h1,h2,h3,p{margin:0}
:focus-visible{outline:2px solid var(--n-brand);outline-offset:2px}
.n-mono{font-family:var(--n-mono)}
.n-sr-only{position:absolute;width:1px;height:1px;overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap}
.n-spacer{flex:1 1 auto}

/* ===== shell ===== */
.n-app{min-height:100vh;display:flex;flex-direction:column;background:var(--n-page)}
.n-header{background:var(--n-brand);color:#fff;display:flex;flex-wrap:wrap;align-items:center;gap:14px;padding:0 24px;min-height:60px}
.n-header__logo{height:28px;width:auto}
.n-header__logo--placeholder{border:1.5px dashed rgba(255,255,255,.75);border-radius:6px;padding:6px 10px;font-size:11px;font-weight:700;letter-spacing:.05em}
.n-header__name{font-size:18px;font-weight:700}
.n-header__sub{font-size:15px;opacity:.9}
.n-env-badge{font-size:11px;font-weight:700;letter-spacing:.06em;background:rgba(0,0,0,.2);border-radius:var(--n-r-pill);padding:5px 10px;text-transform:uppercase}
.n-user-chip{display:flex;align-items:center;gap:10px;background:#fff;color:var(--n-text);border:0;border-radius:var(--n-r-pill);padding:0 12px 0 6px;height:40px}
.n-user-chip__avatar{width:28px;height:28px;border-radius:50%;background:var(--n-brand-50);color:var(--n-brand-800);display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:700}
.n-user-chip__text{display:flex;flex-direction:column;text-align:left;line-height:1.15}
.n-user-chip__name{font-size:13px;font-weight:600}
.n-user-chip__id{font-size:11px;color:var(--n-text-muted);font-family:var(--n-mono)}
.n-user-chip__action{font-size:12px;font-weight:600;color:var(--n-brand-700);margin-left:4px}

.n-context{background:var(--n-surface);border-bottom:1px solid var(--n-border);display:flex;flex-wrap:wrap;align-items:center;gap:10px 14px;padding:10px 24px}
.n-context__group{display:flex;align-items:center;gap:8px}
.n-context__label{font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-context__note{display:inline-flex;align-items:center;gap:6px;font-size:12px;color:var(--n-text-2)}

.n-body{display:flex;flex:1 1 auto;align-items:stretch}
.n-sidebar{width:var(--n-sidebar-w);flex:0 0 var(--n-sidebar-w);background:var(--n-surface);border-right:1px solid var(--n-sidebar-border);padding:16px 12px;display:flex;flex-direction:column;gap:2px}
.n-sidebar__group{font-size:11px;font-weight:700;letter-spacing:.06em;color:var(--n-text-faint);padding:14px 12px 6px;text-transform:uppercase}
.n-nav-item{display:flex;align-items:center;gap:10px;min-height:40px;padding:0 12px;border-radius:var(--n-r-row);color:var(--n-text);font-size:14px;font-weight:500;text-decoration:none;transition:background .12s ease}
.n-nav-item:hover{background:var(--n-surface-sunk);color:var(--n-text);text-decoration:none}
.n-nav-item svg{width:18px;height:18px;flex:0 0 18px;stroke:var(--n-text-muted);stroke-width:1.8;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-nav-item[aria-current="page"]{background:var(--n-brand-50);color:var(--n-brand-800);font-weight:700}
.n-nav-item[aria-current="page"] svg{stroke:var(--n-brand-700)}
.n-nav-item__label{flex:1 1 auto}
.n-nav-item__badge{font-size:11px;font-weight:700;color:var(--n-text-2);background:var(--n-divider-3);border-radius:var(--n-r-pill);padding:2px 8px}
.n-main{flex:1 1 auto;min-width:0;display:flex;flex-direction:column}
.n-page{padding:24px 28px 32px;display:flex;flex-direction:column;gap:20px;max-width:1360px}
.n-page__title{font-size:24px;font-weight:700;letter-spacing:-.01em;line-height:1.25}
.n-page__intro{font-size:14px;color:var(--n-text-muted)}

/* ===== hierarchy tab bar ===== */
.n-tabbar{background:var(--n-surface);border-bottom:1px solid var(--n-border);display:flex;flex-wrap:wrap;align-items:center;gap:16px;padding:0 24px}
.n-tabs{display:flex;gap:4px;flex-wrap:wrap}
.n-tab{background:transparent;border:0;border-bottom:3px solid transparent;padding:14px 18px 11px;text-align:left;min-height:44px}
.n-tab__title{display:block;font-size:15px;font-weight:500;color:var(--n-text-3)}
.n-tab__sub{display:block;font-size:12px;color:var(--n-text-muted);margin-top:2px}
.n-tab[aria-pressed="true"]{border-bottom-color:var(--n-brand)}
.n-tab[aria-pressed="true"] .n-tab__title{font-weight:700;color:var(--n-text)}
.n-lock-note{display:flex;align-items:center;gap:8px;font-size:13px;color:var(--n-text-2);padding:10px 0}
.n-lock-note .n-mono{font-weight:600}

/* ===== cards ===== */
.n-card{background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);overflow:hidden}
.n-card--pad{padding:20px;display:flex;flex-direction:column;gap:12px}
.n-card__strip{display:flex;flex-wrap:wrap;align-items:center;gap:12px;padding:12px 20px;background:var(--n-surface-alt);border-bottom:1px solid var(--n-divider-2)}
.n-card__head{display:flex;align-items:center;gap:8px;padding:16px 20px;border-bottom:1px solid var(--n-divider)}
.n-card__title{font-size:16px;font-weight:700;line-height:1.3}
.n-card__sub{font-size:13px;color:var(--n-text-muted);line-height:1.5}
.n-card__foot{display:flex;flex-wrap:wrap;gap:20px;padding:12px 20px;border-top:1px solid var(--n-divider-2);font-size:12px;color:var(--n-text-muted)}

/* ===== buttons ===== */
.n-btn{display:inline-flex;align-items:center;justify-content:center;gap:8px;height:44px;padding:0 18px;border-radius:var(--n-r-pill);font-size:14px;font-weight:600;border:1.5px solid transparent;background:transparent;color:var(--n-text);white-space:nowrap;transition:background .12s ease}
.n-btn svg{width:16px;height:16px;flex:0 0 16px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-btn--sm{height:40px;padding:0 16px;font-size:13px}
.n-btn--xs{height:32px;padding:0 14px;font-size:12px}
.n-btn--primary{background:var(--n-brand);color:#fff}
.n-btn--primary:hover{background:var(--n-brand-700)}
.n-btn--secondary{background:var(--n-surface);border-color:var(--n-border-input);color:var(--n-text)}
.n-btn--secondary:hover{background:var(--n-surface-sunk)}
.n-btn--outline-brand{background:var(--n-surface);border-color:var(--n-brand);color:var(--n-brand-800)}
.n-btn--outline-brand[aria-pressed="true"]{background:var(--n-brand);color:#fff}
.n-btn--outline-dark{background:var(--n-surface);border-color:var(--n-text);color:var(--n-text)}
.n-btn--dark{background:var(--n-text);color:#fff}
.n-btn--text{height:40px;padding:0 8px;border:0;color:var(--n-brand-700)}
.n-btn--on-dark{height:32px;padding:0 14px;border:0;background:rgba(255,255,255,.14);color:#fff;font-size:12px}
.n-btn--on-brand{height:40px;background:transparent;border-color:#fff;color:#fff}
.n-icon-btn{width:40px;height:40px;border-radius:50%;border:1.5px solid var(--n-border-input);background:var(--n-surface);display:inline-flex;align-items:center;justify-content:center;color:var(--n-text);padding:0}
.n-icon-btn--ghost{border-color:transparent;background:transparent;color:var(--n-text-muted)}
.n-icon-btn--sm{width:36px;height:36px}
.n-icon-btn svg{width:16px;height:16px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round;stroke-linejoin:round}

/* ===== form controls ===== */
.n-field{display:flex;flex-direction:column;gap:6px}
.n-label{font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-input{height:40px;border:1.5px solid var(--n-border-input);border-radius:10px;padding:0 12px;font-size:13px;background:var(--n-surface);color:var(--n-text)}
.n-input--pill{border-radius:var(--n-r-pill);padding:0 14px}
.n-input--sm{height:36px}
.n-input--edit{height:48px;border:2px solid var(--n-brand);border-radius:var(--n-r-tile);padding:0 14px;font-size:18px;font-weight:600}
.n-select{position:relative;display:inline-block}
.n-select select{appearance:none;height:40px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 36px 0 14px;font-size:14px;font-weight:600;background:var(--n-surface);color:var(--n-text)}
.n-select--sm select{height:36px;padding:0 34px 0 14px;font-size:13px}
.n-select--box select{border-radius:10px;font-weight:400;font-size:13px;padding:0 30px 0 12px}
.n-select::after{content:"";position:absolute;right:15px;top:50%;width:7px;height:7px;border-right:2px solid var(--n-text);border-bottom:2px solid var(--n-text);transform:translateY(-70%) rotate(45deg);pointer-events:none}

.n-search{position:relative}
.n-search__icon{position:absolute;left:18px;top:16px;width:20px;height:20px;stroke:var(--n-text-2);stroke-width:2;fill:none;stroke-linecap:round}
.n-search__input{width:100%;height:52px;border:1.5px solid var(--n-border-input);border-radius:var(--n-r-pill);padding:0 20px 0 50px;font-size:16px;background:var(--n-surface)}
.n-popover{position:absolute;top:60px;left:0;right:0;background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);box-shadow:var(--n-shadow-overlay);padding:8px;z-index:20}
.n-popover__label{display:block;padding:6px 10px;font-size:11px;font-weight:700;letter-spacing:.06em;color:var(--n-text-muted);text-transform:uppercase}
.n-result{display:flex;align-items:center;gap:12px;width:100%;text-align:left;background:transparent;border:0;border-radius:var(--n-r-row);padding:10px;min-height:52px}
.n-result:hover,.n-result:focus-visible{background:var(--n-brand-30)}
.n-result__name{font-size:14px;font-weight:600}
.n-result__code{font-family:var(--n-mono);font-size:12px;color:var(--n-text-muted);font-weight:400}
.n-result__path{display:block;font-family:var(--n-mono);font-size:11px;color:var(--n-text-muted)}

/* toggle switch: <button class="n-toggle" aria-pressed="true|false"><span class="n-toggle__track"><span class="n-toggle__knob"></span></span>Label</button> */
.n-toggle{display:inline-flex;align-items:center;gap:8px;background:transparent;border:0;padding:0 4px;height:40px;font-size:13px;color:var(--n-text)}
.n-toggle__track{position:relative;width:32px;height:18px;border-radius:var(--n-r-pill);background:var(--n-border-input);transition:background .12s ease}
.n-toggle__knob{position:absolute;top:2px;left:2px;width:14px;height:14px;border-radius:50%;background:#fff;transition:left .12s ease}
.n-toggle[aria-pressed="true"] .n-toggle__track{background:var(--n-brand)}
.n-toggle[aria-pressed="true"] .n-toggle__knob{left:16px}

/* segmented control: raised (default) and dark variants */
.n-seg{display:inline-flex;gap:2px;background:var(--n-surface-sunk);border-radius:var(--n-r-pill);padding:3px}
.n-seg__btn{display:inline-flex;align-items:center;gap:6px;background:transparent;border:0;border-radius:var(--n-r-pill);padding:0 12px;height:34px;font-size:13px;font-weight:600;color:var(--n-text)}
.n-seg__btn[aria-pressed="true"]{background:var(--n-surface);box-shadow:var(--n-shadow-raised)}
.n-seg--sm .n-seg__btn{height:30px;font-size:12px}
.n-seg--dark .n-seg__btn{height:30px;padding:0 14px;font-size:12px}
.n-seg--dark .n-seg__btn[aria-pressed="true"]{background:var(--n-text);color:#fff;box-shadow:none}

/* ===== chips, pills, badges ===== */
.n-chip{display:inline-flex;align-items:center;gap:8px;height:36px;padding:0 14px;border-radius:var(--n-r-pill);border:1px solid var(--n-chip-border);background:var(--n-chip);font-size:13px;color:var(--n-text)}
.n-chip .n-mono{font-size:12px;color:var(--n-brand-700)}
.n-chip svg{width:13px;height:13px;stroke:var(--n-text-muted);stroke-width:2;fill:none}
.n-chip-toggle{display:inline-flex;align-items:center;gap:6px;height:34px;padding:0 12px;border-radius:var(--n-r-pill);border:1.5px solid var(--n-border-soft);background:var(--n-surface);font-size:13px;color:var(--n-text-2)}
.n-chip-toggle[aria-pressed="true"]{border-color:var(--n-brand);background:var(--n-brand-40);color:var(--n-brand-800)}
.n-status{display:inline-block;font-size:12px;font-weight:600;border-radius:var(--n-r-pill);padding:4px 12px}
.n-status--active{color:var(--n-brand-800);background:var(--n-brand-50)}
.n-status--inactive{color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-status--working{color:var(--n-brand-800);background:var(--n-brand-50)}
.n-status--finalized{color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-level-chip{font-size:12px;font-weight:600;color:var(--n-brand-700);background:var(--n-brand-40);border-radius:var(--n-r-pill);padding:3px 10px}
.n-reason-chip{font-size:11px;font-weight:600;color:var(--n-text-3);background:var(--n-neutral-bg);border-radius:var(--n-r-pill);padding:3px 10px}
.n-flag{display:inline-flex;align-items:center;gap:6px;font-size:11px;font-weight:700;letter-spacing:.04em;text-transform:uppercase}
.n-flag--changed{color:var(--n-warn-text)}
.n-flag--changed::before{content:"";width:0;height:0;border-left:5px solid transparent;border-right:5px solid transparent;border-bottom:9px solid var(--n-warn)}
.n-flag--inactive{color:var(--n-text-muted)}

/* node-type markers (shape language) */
.n-mark-limb{display:inline-block;width:10px;height:10px;border:1.5px solid var(--n-text-2);flex:0 0 auto}
.n-mark-leaf{display:inline-block;width:8px;height:8px;border-radius:50%;border:1.5px solid var(--n-text-faint);flex:0 0 auto}
.n-mark--brand{border-color:var(--n-brand)}

/* ===== breadcrumb ===== */
.n-crumbs{display:flex;flex-wrap:wrap;align-items:center;flex:1 1 auto;min-width:0}
.n-crumb{background:transparent;border:0;padding:6px 4px;font-family:var(--n-mono);font-size:13px;font-weight:500;color:var(--n-brand-700);text-decoration:underline;text-underline-offset:4px}
.n-crumb[aria-current="location"]{color:var(--n-text);font-weight:700;text-decoration:none}
.n-crumb-sep{color:var(--n-separator);padding:0 6px;font-size:13px}

/* ===== tree ===== */
.n-tree{padding:10px 16px 16px;max-height:640px;overflow:auto}
.n-tree-row{display:flex;align-items:stretch;min-height:44px;border-radius:var(--n-r-tree)}
.n-tree-row[aria-selected="true"]{background:var(--n-brand-50)}
.n-tree-row__guide{width:24px;flex:0 0 24px;display:flex;justify-content:center}
.n-tree-row__guide::before{content:"";width:1px;background:var(--n-guide)}
.n-tree-row__toggle{width:32px;flex:0 0 32px;border:0;background:transparent;display:flex;align-items:center;justify-content:center;padding:0}
.n-tree-row__box{width:14px;height:14px;border:1.5px solid var(--n-text-2);background:#fff;display:flex;align-items:center;justify-content:center;font-family:var(--n-mono);font-size:12px;line-height:1;color:var(--n-text)}
.n-tree-row__leafslot{width:32px;flex:0 0 32px;display:flex;align-items:center;justify-content:center}
.n-tree-row__body{flex:1 1 auto;min-width:0;display:flex;align-items:center;gap:14px;background:transparent;border:0;padding:0 10px;text-align:left;color:var(--n-text)}
.n-tree-row__code{font-family:var(--n-mono);font-size:13px;min-width:56px;color:var(--n-text-muted)}
.n-tree-row__name{font-size:14px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.n-tree-row__count{font-size:12px;color:var(--n-text-faint);margin-left:auto}
.n-tree-row[aria-selected="true"] .n-tree-row__code{color:var(--n-brand-700)}
.n-tree-row[aria-selected="true"] .n-tree-row__name{font-weight:700}
.n-tree-row[aria-selected="true"] .n-mark-leaf{border-color:var(--n-brand)}
.n-tree-row--inactive .n-tree-row__body{color:var(--n-text-muted)}
.n-legend{display:inline-flex;align-items:center;gap:6px}

/* ===== details card ===== */
.n-identity{padding:20px 20px 16px;display:flex;flex-direction:column;gap:12px}
.n-identity__row{display:flex;align-items:center;gap:10px}
.n-icon-tile{width:40px;height:40px;flex:0 0 40px;border-radius:var(--n-r-tile);background:var(--n-brand-40);display:flex;align-items:center;justify-content:center}
.n-icon-tile .n-mark-limb{width:14px;height:14px;border:2px solid var(--n-brand)}
.n-icon-tile .n-mark-leaf{width:12px;height:12px;border:2px solid var(--n-brand)}
.n-identity__code{font-family:var(--n-mono);font-size:13px;font-weight:600;color:var(--n-brand-700)}
.n-identity__type{font-size:12px;color:var(--n-text-muted)}
.n-identity__name{font-size:26px;font-weight:700;line-height:1.2;letter-spacing:-.01em}
.n-identity__desc{font-size:14px;color:var(--n-text-3);line-height:1.5}
.n-facts{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));border-top:1px solid var(--n-divider);border-bottom:1px solid var(--n-divider)}
.n-fact{padding:12px 20px;display:flex;flex-direction:column;gap:3px}
.n-fact:nth-child(odd){border-right:1px solid var(--n-divider)}
.n-fact:nth-child(-n+2){border-bottom:1px solid var(--n-divider)}
.n-fact__label{font-size:11px;font-weight:600;letter-spacing:.06em;color:var(--n-text-muted);text-transform:uppercase}
.n-fact__value{font-size:14px;font-weight:600}
.n-fact__link{background:transparent;border:0;padding:0;text-align:left;font-size:14px;font-weight:600;color:var(--n-brand-700);text-decoration:underline;text-underline-offset:3px}
.n-actions{padding:16px 20px 20px;display:flex;flex-direction:column;gap:12px}
.n-actions__row{display:flex;flex-wrap:wrap;gap:8px}

.n-panel{border-radius:var(--n-r-panel);overflow:hidden}
.n-panel--brand{border:1.5px solid var(--n-brand)}
.n-panel--warn{border:1.5px solid var(--n-warn);background:var(--n-warn-bg);padding:14px;display:flex;flex-direction:column;gap:10px}
.n-panel__head{padding:12px 14px;background:var(--n-brand-30);display:flex;align-items:center;gap:8px}
.n-panel__title{font-size:13px;font-weight:700;flex:1 1 auto}
.n-panel--warn .n-panel__title{font-size:14px}
.n-panel__list{max-height:260px;overflow:auto;padding:6px}
.n-panel__foot{padding:10px 14px;border-top:1px solid #E6EFE2;font-size:12px;color:var(--n-text-3)}
.n-pick{display:flex;align-items:center;gap:10px;width:100%;background:transparent;border:0;border-radius:var(--n-r-row);padding:0 10px;min-height:44px;text-align:left}
.n-pick:hover{background:var(--n-brand-30)}
.n-pick__code{font-family:var(--n-mono);font-size:12px;color:var(--n-brand-700);min-width:52px}
.n-pick__name{font-size:14px;flex:1 1 auto}
.n-pick__meta{font-size:12px;color:var(--n-text-muted)}
.n-pick--blocked{color:var(--n-text-faint)}
.n-pick--blocked .n-pick__code{color:var(--n-text-faint)}
.n-pick--blocked .n-mark-limb{border-color:var(--n-border-input)}

/* position-in-every-hierarchy rows */
.n-position{display:flex;flex-direction:column;gap:6px;width:100%;text-align:left;background:var(--n-surface);border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);padding:12px 14px;color:var(--n-text)}
.n-position[aria-current="true"]{background:var(--n-brand-20);border-color:var(--n-brand-border)}
.n-position__top{display:flex;align-items:center;gap:8px;width:100%}
.n-position__code{font-size:13px;font-weight:700}
.n-position__role{font-size:12px;color:var(--n-text-muted)}
.n-position__path{font-family:var(--n-mono);font-size:12px;color:var(--n-text-2);line-height:1.6;word-break:break-word}

/* history list */
.n-history{display:flex;flex-direction:column;gap:4px;padding:10px 0;border-top:1px solid var(--n-divider-3)}
.n-history__text{font-size:14px;font-weight:600}
.n-history__meta{font-size:12px;color:var(--n-text-muted)}

/* toast */
.n-toast{display:flex;align-items:center;gap:10px;background:var(--n-text);color:#fff;border-radius:var(--n-r-pill);padding:8px 8px 8px 16px;font-size:13px}
.n-toast svg{width:16px;height:16px;stroke:var(--n-success-on-dark);stroke-width:2.6;fill:none;stroke-linecap:round;stroke-linejoin:round}
.n-toast__msg{flex:1 1 auto}

/* banners */
.n-banner{display:flex;align-items:center;gap:10px;padding:10px 20px;font-size:13px}
.n-banner--warn{background:var(--n-warn-bg);border-bottom:1px solid #F3E2C6;color:#5A3500}
.n-banner--info{background:var(--n-brand-30);border-radius:var(--n-r-tile);padding:12px 14px;color:var(--n-brand-800)}

/* tables */
.n-table-wrap{overflow-x:auto}
.n-table{width:100%;border-collapse:collapse;font-size:13px}
.n-table th{text-align:left;padding:10px 12px;font-size:12px;font-weight:600;color:var(--n-text-muted);background:var(--n-surface-alt);border-bottom:1px solid var(--n-divider);white-space:nowrap}
.n-table td{padding:10px 12px;border-bottom:1px solid #F2F2F2;vertical-align:middle}
.n-table th:first-child,.n-table td:first-child{padding-left:20px}
.n-table th:last-child,.n-table td:last-child{padding-right:20px}
.n-table td.n-hit{background:var(--n-brand-40);font-weight:600}

/* empty states */
.n-empty{padding:40px 20px;text-align:center;display:flex;flex-direction:column;align-items:center;gap:6px}
.n-empty__title{font-size:15px;font-weight:600}
.n-empty__text{font-size:13px;color:var(--n-text-muted)}

/* collapsible sidebar (hamburger in header) */
.n-burger{width:40px;height:40px;margin-left:-8px;border-radius:10px;border:0;background:transparent;color:#fff;display:flex;align-items:center;justify-content:center;padding:0}
.n-burger[aria-expanded="true"]{background:rgba(0,0,0,.18)}
.n-burger svg{width:20px;height:20px;stroke:currentColor;stroke-width:2;fill:none;stroke-linecap:round}
.n-sidebar--rail{width:64px;flex-basis:64px;padding:16px 10px}
.n-sidebar--rail .n-nav-item{justify-content:center;padding:0;position:relative}
.n-sidebar--rail .n-nav-item__label,.n-sidebar--rail .n-nav-item__badge{display:none}
.n-sidebar--rail .n-sidebar__group{height:1px;padding:0;margin:10px 8px;background:var(--n-divider-2);font-size:0}
.n-nav-item__dot{display:none}
.n-sidebar--rail .n-nav-item__dot{display:flex;position:absolute;top:6px;right:8px;min-width:16px;height:16px;border-radius:var(--n-r-pill);background:var(--n-text-2);color:#fff;font-size:10px;font-weight:700;align-items:center;justify-content:center;padding:0 4px}
/* tree names wrap instead of truncating */
.n-tree-row__name--wrap{white-space:normal;overflow:visible;text-overflow:clip;overflow-wrap:anywhere;line-height:1.35;flex:1 1 auto;min-width:0}
.n-tree--compact .n-tree-row__body{padding:8px}
.n-tree--compact .n-tree-row__guide{width:16px;flex-basis:16px}
.n-tree--compact .n-tree-row__code{flex:0 0 48px}
.n-pane--tree{width:520px;flex:0 0 520px}
.n-sidebar:not(.n-sidebar--rail) ~ .n-main .n-pane--tree{width:440px;flex-basis:440px}

/* ===== master–detail workspace (Hierarchies) ===== */
.n-app--fixed{height:100vh;min-height:900px;overflow:hidden}
.n-app--fixed .n-body,.n-app--fixed .n-main{min-height:0}
.n-workspace{flex:1 1 auto;min-height:0;display:flex;gap:16px;padding:16px 20px;align-items:stretch}
.n-pane{background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);display:flex;flex-direction:column;overflow:hidden;min-width:0}
.n-pane--detail{flex:1 1 auto}
.n-pane__top{padding:14px 14px 10px;display:flex;flex-direction:column;gap:10px;border-bottom:1px solid var(--n-divider)}
.n-pane__scroll{flex:1 1 auto;min-height:0;overflow:auto}
.n-pane__foot{display:flex;flex-wrap:wrap;align-items:center;gap:12px;padding:8px 14px;border-top:1px solid var(--n-divider-2);font-size:12px;color:var(--n-text-muted)}
.n-search--compact .n-search__icon{left:15px;top:13px;width:18px;height:18px}
.n-search--compact .n-search__input{height:44px;font-size:14px;padding:0 16px 0 42px}
.n-search--compact .n-popover{top:52px}
.n-chip--sm{height:30px;padding:0 10px;gap:6px;font-size:12px}
.n-chip--sm .n-mono{font-size:11px}
/* compact tree rows inside the tree pane */
.n-tree--compact{padding:8px 10px 12px;max-height:none}
.n-tree--compact .n-tree-row{min-height:40px}
.n-tree--compact .n-tree-row__toggle,.n-tree--compact .n-tree-row__leafslot{width:28px;flex-basis:28px}
.n-tree--compact .n-tree-row__box{width:13px;height:13px;font-size:11px}
.n-tree--compact .n-tree-row__body{gap:10px}
.n-tree--compact .n-tree-row__code{font-size:12px;min-width:48px}
.n-tree--compact .n-tree-row__name{font-size:13px}
.n-tree--compact .n-tree-row__count{font-size:11px}
.n-flag--draft{color:var(--n-warn-text);font-size:10px;letter-spacing:.05em}
.n-flag--draft::before{content:"";width:0;height:0;border-left:4px solid transparent;border-right:4px solid transparent;border-bottom:8px solid var(--n-warn)}

/* detail header */
.n-detail-head{padding:18px 24px 16px;display:flex;flex-direction:column;gap:12px;border-bottom:1px solid var(--n-divider)}
.n-detail-head .n-crumb{font-size:12px;padding:4px 3px;text-underline-offset:3px}
.n-detail-head .n-crumb-sep{font-size:12px;padding:0 4px}
.n-detail-title{display:flex;align-items:flex-start;gap:14px;flex-wrap:wrap}
.n-detail-title .n-icon-tile{width:44px;height:44px;flex-basis:44px}
.n-detail-title__name{font-size:24px;font-weight:700;line-height:1.2;letter-spacing:-.01em}
.n-detail-title__meta{display:flex;flex-wrap:wrap;align-items:center;gap:8px;font-size:13px;color:var(--n-text-muted)}
.n-detail-title__meta .n-mono{font-weight:600;color:var(--n-brand-700)}
.n-pending{display:flex;align-items:center;gap:10px;background:var(--n-warn-bg);border:1px solid #F3E2C6;border-radius:var(--n-r-tile);padding:10px 14px;font-size:13px;color:#5A3500}
.n-btn--danger{background:var(--n-surface);border-color:var(--n-danger-border);color:var(--n-danger)}
.n-btn--danger[aria-pressed="true"]{background:var(--n-danger-bg);border-color:var(--n-danger)}
.n-btn--danger-solid{background:var(--n-danger);color:#fff}
.n-btn--secondary[aria-pressed="true"]{background:var(--n-brand);border-color:var(--n-brand);color:#fff}
/* 4-up fact strip */
.n-facts--strip{grid-template-columns:repeat(4,minmax(0,1fr));border-top:0}
.n-facts--strip .n-fact{padding:12px 24px;border-bottom:0;border-right:1px solid var(--n-divider)}
.n-facts--strip .n-fact:last-child{border-right:0}
/* detail tabs */
.n-dtabs{display:flex;gap:4px;padding:0 20px;border-bottom:1px solid var(--n-divider);flex:0 0 auto}
.n-dtab{background:transparent;border:0;border-bottom:2px solid transparent;padding:0 12px;height:46px;font-size:14px;font-weight:500;color:var(--n-text-3);display:inline-flex;align-items:center;gap:6px}
.n-dtab[aria-selected="true"]{border-bottom-color:var(--n-brand);font-weight:700;color:var(--n-text)}
.n-dtab__count{font-size:11px;font-weight:700;color:var(--n-text-2);background:var(--n-divider-3);border-radius:var(--n-r-pill);padding:1px 7px}
.n-tabpanel{flex:1 1 auto;min-height:0;overflow:auto;padding:16px 24px 24px}
/* property table */
.n-props{border:1px solid var(--n-divider-2);border-radius:var(--n-r-tile);overflow:hidden}
.n-props__row{display:grid;grid-template-columns:minmax(0,1.2fr) minmax(0,1fr) minmax(0,1fr) 88px;align-items:center;min-height:52px;border-top:1px solid #F2F2F2}
.n-props__row--head{min-height:0;border-top:0;background:var(--n-surface-alt);border-bottom:1px solid var(--n-divider);font-size:12px;font-weight:600;color:var(--n-text-muted)}
.n-props__row--head>span{padding:10px 12px}
.n-props__row--head>span:first-child{padding-left:16px}
.n-props__row--draft{background:var(--n-draft-row)}
.n-props__cat{padding:10px 16px 6px;font-size:11px;font-weight:700;letter-spacing:.06em;color:var(--n-text-faint);border-top:1px solid #F0F0F0;text-transform:uppercase}
.n-props__name{padding:8px 16px;display:flex;flex-direction:column;gap:2px;min-width:0}
.n-props__label{font-size:14px;font-weight:500}
.n-props__key{font-family:var(--n-mono);font-size:11px;color:var(--n-text-faint)}
.n-props__value{padding:8px 12px;font-family:var(--n-mono);font-size:13px;font-weight:600}
.n-props__value--empty{font-family:var(--n-font);font-weight:400;color:var(--n-text-faint)}
.n-props__action{padding:8px 12px;text-align:right}
.n-source{display:inline-flex;align-items:center;font-size:12px;font-weight:600;border-radius:var(--n-r-pill);padding:3px 10px;white-space:nowrap}
.n-source--set{color:var(--n-brand-800);background:var(--n-brand-50)}
.n-source--inherited{color:var(--n-moved);background:var(--n-moved-bg)}
.n-source--default{color:var(--n-text-3);background:var(--n-neutral-bg)}
.n-source--derived{color:var(--n-renamed);background:var(--n-renamed-bg)}
.n-source--none{color:var(--n-text-muted);background:transparent;padding-left:0}
.n-source--draft{color:var(--n-warn-text);background:var(--n-status-bg)}
/* radio option cards (remove choices) */
.n-option{display:flex;align-items:flex-start;gap:12px;width:100%;text-align:left;background:var(--n-surface);border:1.5px solid var(--n-divider-2);border-radius:var(--n-r-tile);padding:12px 14px;color:var(--n-text)}
.n-option[aria-checked="true"]{border-color:var(--n-text)}
.n-option__radio{width:18px;height:18px;flex:0 0 18px;border-radius:50%;border:2px solid var(--n-border-input);display:flex;align-items:center;justify-content:center;margin-top:1px}
.n-option[aria-checked="true"] .n-option__radio{border-color:var(--n-text)}
.n-option[aria-checked="true"] .n-option__radio::after{content:"";width:8px;height:8px;border-radius:50%;background:var(--n-text)}
.n-option__label{display:flex;align-items:center;gap:8px;font-size:14px;font-weight:600}
.n-option__text{font-size:13px;color:var(--n-text-3);line-height:1.45}
.n-tag-rec{font-size:11px;font-weight:700;color:var(--n-brand-800);background:var(--n-brand-50);border-radius:var(--n-r-pill);padding:2px 8px}
.n-error{font-size:12px;font-weight:600;color:var(--n-danger)}
.n-input--error{border-color:var(--n-danger)}
/* governance draft bar (bottom of n-main) */
.n-draftbar{position:relative;flex:0 0 auto;background:var(--n-surface);border-top:1px solid var(--n-border);display:flex;flex-wrap:wrap;align-items:center;gap:10px 14px;padding:10px 24px}
.n-draftbar__count{min-width:28px;height:28px;border-radius:var(--n-r-pill);background:var(--n-brand);color:#fff;display:flex;align-items:center;justify-content:center;font-size:13px;font-weight:700;padding:0 8px}
.n-draftbar__count--zero{background:var(--n-neutral-bg);color:var(--n-text-3)}
.n-draftbar__text{display:flex;flex-direction:column;gap:1px;flex:1 1 360px;min-width:0}
.n-draftbar__title{font-size:14px;font-weight:700}
.n-draftbar__sub{font-size:12px;color:var(--n-text-muted)}
.n-draftbar__toast{display:inline-flex;align-items:center;gap:8px;background:var(--n-text);color:#fff;border-radius:var(--n-r-pill);padding:6px 14px;font-size:12px}
.n-draftbar__list{position:absolute;bottom:calc(100% + 8px);left:24px;width:520px;max-width:calc(100% - 48px);background:var(--n-surface);border:1px solid var(--n-border);border-radius:var(--n-r-card);box-shadow:var(--n-shadow-overlay);padding:8px;z-index:30}
.n-type-dot{width:26px;height:26px;flex:0 0 26px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:10px;font-weight:700}

/* responsive */
@media (max-width:1100px){
  .n-workspace{flex-direction:column;overflow:auto}
  .n-pane--tree{width:auto;flex:0 0 420px}
  .n-pane--detail{flex:0 0 auto}
  .n-facts--strip{grid-template-columns:repeat(2,minmax(0,1fr))}
}
@media (max-width:900px){
  .n-sidebar{display:none}
  .n-page{padding:16px}
  .n-tabbar,.n-context{padding-left:16px;padding-right:16px}
}
```

---

## 4. Component catalogue (HTML contract)

Use these exact structures; Angular components only add bindings.

### 4.1 App shell
```html
<div class="n-app">
  <header class="n-header">
    <img class="n-header__logo" src="assets/logo.svg" alt="Company logo"><!-- approved asset only -->
    <span class="n-header__name">Nexus</span><span class="n-header__sub">Master Data Central</span>
    <span class="n-spacer"></span>
    <span class="n-env-badge">Development</span>
    <button class="n-user-chip" type="button">
      <span class="n-user-chip__avatar" aria-hidden="true">VA</span>
      <span class="n-user-chip__text"><span class="n-user-chip__name">E2E Version Admin One</span><span class="n-user-chip__id">e2e_vadmin</span></span>
      <span class="n-user-chip__action">Switch</span>
    </button>
  </header>
  <div class="n-context">
    <div class="n-context__group"><label class="n-context__label" for="ctx-version">Version</label>
      <span class="n-select n-select--sm"><select id="ctx-version"><option>2026-11 · Working</option><option>2026-10 · Finalized</option></select></span></div>
    <div class="n-context__group"><label class="n-context__label" for="ctx-asof">As of</label>
      <input id="ctx-asof" class="n-input n-input--pill n-input--sm" type="date"></div>
    <div class="n-context__group"><span class="n-context__label">Work in</span>
      <div class="n-seg n-seg--dark" role="group" aria-label="Workspace">
        <button class="n-seg__btn" aria-pressed="true">Live data</button><button class="n-seg__btn" aria-pressed="false">Sandbox</button></div></div>
    <span class="n-spacer"></span>
    <span class="n-context__note"><!-- lock svg 13px -->Finalized version · read-only</span><!-- only when Finalized -->
    <a href="/versions">Manage versions</a>
  </div>
  <div class="n-body">
    <nav class="n-sidebar" aria-label="Main">…§4.2…</nav>
    <main class="n-main"><router-outlet></router-outlet></main>
  </div>
</div>
```
- The **user chip** opens the "log in as" picker sourced from `users.csv`.
- The **context bar is global state** (version, as-of date, workspace) shared by every page through one Angular service.

### 4.2 Sidebar navigation (exact items, order and icons)
**Collapsible:** a hamburger `button.n-burger` is the first item in `.n-header` (`aria-controls="main-nav"`, `aria-expanded`, `aria-label` "Expand menu" / "Collapse menu"). Collapsed (default) = icon rail `nav.n-sidebar.n-sidebar--rail` (64px): icons only, centred, each link gets `aria-label` + `title` with its name, group labels become 1px dividers, counts show as a dot badge `.n-nav-item__dot`. Expanded = 216px with labels. The choice is remembered per user (localStorage `nexus.navOpen`). Below 900px the expanded menu overlays the page instead of pushing it.

Group labels: none for the first block, then `CHANGE`, then `ADMIN`.

| Item | Route | Icon path (`<svg viewBox="0 0 24 24"><path d="…"/></svg>`) |
|---|---|---|
| Home | `/` | `M3 11l9-8 9 8M5 10v10h14V10` |
| Hierarchies | `/hierarchies/:code` | `M10 3h4v5h-4zM4 16h4v5H4zM16 16h4v5h-4zM12 8v4M6 16v-4h12v4` |
| Queries | `/queries` | `M11 4a7 7 0 1 0 0 14a7 7 0 1 0 0-14zM20 20l-4-4` |
| Compare | `/compare` | `M7 7h12M15 3l4 4-4 4M17 17H5M9 13l-4 4 4 4` |
| As-Was | `/as-was` | `M12 7v5l3 2M12 3a9 9 0 1 0 0 18a9 9 0 1 0 0-18z` |
| *CHANGE* | | |
| Worklist (badge = count) | `/worklist` | `M9 6h11M9 12h11M9 18h11M4 6h1M4 12h1M4 18h1` |
| Sandboxes | `/sandboxes` | `M4 4h16v16H4zM4 12h16M12 4v16` |
| Audit | `/audit` | `M6 3h9l3 3v15H6zM9 10h6M9 14h6M9 18h4` |
| *ADMIN* | | |
| Versions | `/versions` | `M8 8h12v12H8zM4 16V4h12` |
| Settings | `/settings` | `M12 9a3 3 0 1 0 0 6a3 3 0 1 0 0-6zM12 2v3M12 19v3M2 12h3M19 12h3M5 5l2 2M17 17l2 2M5 19l2-2M17 7l2-2` |

```html
<a class="n-nav-item" routerLink="/queries" routerLinkActive #rla="routerLinkActive" [attr.aria-current]="rla.isActive ? 'page' : null">
  <svg viewBox="0 0 24 24" aria-hidden="true"><path d="…"/></svg><span class="n-nav-item__label">Queries</span>
</a>
<span class="n-sidebar__group">Change</span>
```

### 4.3 Other components → classes
| Component | Classes |
|---|---|
| Hierarchy tabs | `.n-tabbar > .n-tabs > button.n-tab[aria-pressed] > .n-tab__title + .n-tab__sub`, right side `.n-lock-note` |
| Card | `.n-card` (+ `.n-card--pad`), `.n-card__strip`, `.n-card__head`, `.n-card__title`, `.n-card__foot` |
| Buttons | `.n-btn` + `--primary` / `--secondary` / `--outline-brand` / `--outline-dark` / `--dark` / `--text` / `--on-dark`; sizes `--sm` (40px) `--xs` (32px); icon-only `.n-icon-btn` (+`--ghost`, `--sm`) with `aria-label` |
| Inputs | `.n-field > .n-label + .n-input` (`--pill`, `--sm`, `--edit`); selects wrapped in `.n-select` (`--sm`, `--box`) |
| Big search | `.n-search > svg.n-search__icon + input.n-search__input`, results `.n-popover > .n-popover__label + button.n-result` |
| Toggle | `button.n-toggle[aria-pressed] > .n-toggle__track > .n-toggle__knob` |
| Segmented | `.n-seg` (raised) / `.n-seg n-seg--dark`, `.n-seg--sm`; children `button.n-seg__btn[aria-pressed]` |
| Chips | Recent `.n-chip`; filter/column `.n-chip-toggle[aria-pressed]` |
| Status | `.n-status--active|inactive|working|finalized`; `.n-level-chip`; `.n-reason-chip`; `.n-flag--changed|inactive` |
| Node markers | `.n-mark-limb` (square), `.n-mark-leaf` (circle), add `.n-mark--brand` when selected |
| Breadcrumb | `.n-crumbs > button.n-crumb` (+`aria-current="location"` on last) `+ span.n-crumb-sep` ("/") |
| Tree | `.n-tree > .n-tree-row[aria-selected]` with `.n-tree-row__guide`×depth, `.n-tree-row__toggle > .n-tree-row__box` (`+`/`−`) or `.n-tree-row__leafslot > .n-mark-leaf`, `button.n-tree-row__body` |
| Details | `.n-identity`, `.n-icon-tile`, `.n-facts > .n-fact`, `.n-actions` |
| Panels | `.n-panel.n-panel--brand` (move picker) with `.n-pick` rows; `.n-panel--warn` (impact preview) |
| Position rows | `button.n-position[aria-current]` |
| History | `.n-history` |
| Toast | `.n-toast[role=status]` |
| Banner | `.n-banner--warn` (stale results), `.n-banner--info` (plain-words summary) |
| Table | `.n-table-wrap > table.n-table`, matched cell `td.n-hit` |
| Empty state | `.n-empty` |
| Hamburger / rail | `button.n-burger[aria-expanded]` in header; `nav.n-sidebar` + `.n-sidebar--rail` when collapsed; `.n-nav-item__dot` count in rail |
| Master–detail workspace | `.n-app.n-app--fixed` → `.n-workspace > section.n-pane.n-pane--tree` + `section.n-pane.n-pane--detail`; pane parts `.n-pane__top`, `.n-pane__scroll`, `.n-pane__foot` |
| Compact search / chips / tree | `.n-search.n-search--compact`, `.n-chip.n-chip--sm`, `.n-tree.n-tree--compact`, names use `.n-tree-row__name.n-tree-row__name--wrap` (wrap, never truncate), `.n-flag--draft` |
| Detail header | `.n-detail-head` (crumbs + `.n-detail-title` + `.n-pending` + action row), `.n-facts.n-facts--strip` |
| Detail tabs | `[role=tablist].n-dtabs > button[role=tab].n-dtab[aria-selected]` (+`.n-dtab__count`), `[role=tabpanel].n-tabpanel` |
| Property table | `.n-props` > `.n-props__row--head`, `.n-props__cat`, `.n-props__row` (+`--draft`) with `.n-props__name`, `.n-props__value` (+`--empty`), `.n-source--set|inherited|default|derived|none|draft`, `.n-props__action` |
| Danger actions | `.n-btn--danger` (outline), `.n-btn--danger-solid` (delete confirm) |
| Option cards | `[role=radiogroup] > button[role=radio].n-option[aria-checked]` with `.n-option__radio`, `.n-option__label`, `.n-option__text`, `.n-tag-rec` |
| Draft bar | `.n-draftbar` > `.n-draftbar__count` (+`--zero`), `.n-draftbar__text`, `.n-draftbar__toast`, buttons; popover `.n-draftbar__list` with `.n-type-dot` rows |
| Errors | `.n-error`, `.n-input--error` |

### 4.4 Icon rules
Inline SVG only, `viewBox="0 0 24 24"`, `fill="none"`, `stroke="currentColor"`, `stroke-width` 2 (1.8 in nav), round caps/joins. Sizes: 13, 15, 16, 18, 20 px only. Decorative icons `aria-hidden="true"`. Common paths:
| Icon | Path |
|---|---|
| Search | `M11 4a7 7 0 1 0 0 14a7 7 0 1 0 0-14zM20 20l-3.5-3.5` |
| Chevron down | `m6 9 6 6 6-6` · right `m9 6 6 6-6 6` · left `m15 6-6 6 6 6` |
| Lock | `<rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0v4"/>` |
| Pencil | `M4 20h4L19 9l-4-4L4 16v4zM14 6l4 4` |
| Arrow right | `M5 12h14M13 6l6 6-6 6` |
| Check | `m5 12 5 5 9-10` |
| Close | `M6 6l12 12M18 6 6 18` |
| Download | `M12 4v11M7 10l5 5 5-5M5 20h14` |
| Clock | `<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>` |
| Swap | `M7 7h12M15 3l4 4-4 4M17 17H5M9 13l-4 4 4 4` |

---

## 5. Copy & formatting conventions
- Sentence case for labels and buttons ("Run query", "Save name", "Hide inactive"). UPPERCASE only via `.n-fact__label`, `.n-sidebar__group`, `.n-popover__label`, `.n-flag`.
- Dates: `13 Nov 2026, 10:42`; versions: `2026-11 · Working`; paths: codes joined by ` / `.
- Counts: `2,818 nodes · 14 levels`, `1 child` / `8 children`.
- Toast after every edit ends with `Saved to <version>.`
- Ellipsis character `…` on buttons that open a confirm step ("Inactivate…", "Save query…").

## 6. Angular implementation notes
- Put §3 in `src/styles.css` (or `styles.scss` with no changes). Set `"inlineStyleLanguage": "css"`; do not enable a Material theme.
- Use `ViewEncapsulation.Emulated` (default) but **no visual rules** in component styles.
- Bind states through ARIA attributes the CSS already keys on: `[attr.aria-pressed]`, `[attr.aria-selected]`, `[attr.aria-current]`, `[attr.aria-expanded]`.
- Long lists (tree ≥ 2,800 rows) use `@angular/cdk/scrolling` virtual scroll with item size **44px**.
- Acceptance: open the mock-up and the app side by side at 1440px; colours, font sizes, paddings, radii and heights must match exactly (use DevTools computed styles to compare).
