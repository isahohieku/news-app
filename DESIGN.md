---
name: Central News
description: A broadcast bulletin for the day's top headlines; the lead story first, then a dense running order with times.
colors:
  brand-red: "#E20101"
  brand-red-on-dark: "#FF6B5E"
  on-brand-red-on-dark: "#1A0503"
  ink: "#0D0F14"
  chrome-dark: "#000000"
  on-chrome: "#FFFFFF"
  on-chrome-muted: "#AEB4BF"
  ground: "#F2F3F5"
  sheet: "#FFFFFF"
  ink-muted: "#5A606C"
  hairline: "#E1E3E8"
  image-well: "#D9DCE2"
  ground-dark: "#0B0D11"
  sheet-dark: "#171A21"
  text-on-dark: "#F1F2F4"
  text-muted-on-dark: "#A6ADB9"
  hairline-dark: "#2A2E37"
  image-well-dark: "#242832"
  raised-dark: "#2A2E37"
  raised-edge-dark: "#3D424D"
typography:
  display:
    fontFamily: "Archivo, sans-serif"
    fontSize: "36sp"
    fontWeight: 800
    lineHeight: "44sp"
    letterSpacing: "0sp"
  headline:
    fontFamily: "Archivo, sans-serif"
    fontSize: "24sp"
    fontWeight: 800
    lineHeight: "29sp"
    letterSpacing: "-0.2sp"
  title-bar:
    fontFamily: "Archivo, sans-serif"
    fontSize: "18sp"
    fontWeight: 800
    lineHeight: "24sp"
    letterSpacing: "0.4sp"
  title-row:
    fontFamily: "Archivo, sans-serif"
    fontSize: "15sp"
    fontWeight: 600
    lineHeight: "20sp"
    letterSpacing: "0sp"
  title-small:
    fontFamily: "Archivo, sans-serif"
    fontSize: "14sp"
    fontWeight: 600
    lineHeight: "18sp"
    letterSpacing: "0.1sp"
  body:
    fontFamily: "Archivo, sans-serif"
    fontSize: "16sp"
    fontWeight: 400
    lineHeight: "24sp"
    letterSpacing: "0sp"
  body-medium:
    fontFamily: "Archivo, sans-serif"
    fontSize: "14sp"
    fontWeight: 400
    lineHeight: "20sp"
    letterSpacing: "0sp"
  label:
    fontFamily: "Archivo, sans-serif"
    fontSize: "12sp"
    fontWeight: 600
    lineHeight: "16sp"
    letterSpacing: "0.6sp"
  label-tag:
    fontFamily: "Archivo, sans-serif"
    fontSize: "11sp"
    fontWeight: 800
    lineHeight: "14sp"
    letterSpacing: "0.9sp"
rounded:
  none: "0dp"
  full: "9999dp"
spacing:
  xs: "4dp"
  sm: "8dp"
  md: "12dp"
  lg: "16dp"
  xl: "24dp"
  xxl: "32dp"
components:
  broadcast-bar:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.on-chrome}"
    typography: "{typography.title-bar}"
    height: "56dp"
  category-tab:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.on-chrome-muted}"
    typography: "{typography.title-small}"
    padding: "0 12dp"
    height: "48dp"
    width: "48dp"
  category-tab-selected:
    textColor: "{colors.on-chrome}"
  wordmark-box:
    backgroundColor: "{colors.brand-red}"
    textColor: "{colors.on-chrome}"
    typography: "{typography.title-bar}"
    rounded: "{rounded.none}"
    padding: "1dp 6dp"
  source-tag:
    backgroundColor: "{colors.brand-red}"
    textColor: "{colors.on-chrome}"
    typography: "{typography.label-tag}"
    rounded: "{rounded.none}"
    padding: "5dp 10dp"
  time-bug:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.on-chrome}"
    typography: "{typography.label-tag}"
    rounded: "{rounded.none}"
    padding: "4dp 8dp"
  running-order-row:
    backgroundColor: "{colors.sheet}"
    textColor: "{colors.ink}"
    typography: "{typography.title-row}"
    rounded: "{rounded.none}"
    padding: "10dp 4dp 8dp 16dp"
  list-header:
    backgroundColor: "{colors.ground}"
    textColor: "{colors.ink-muted}"
    typography: "{typography.label}"
    padding: "8dp 4dp 0 16dp"
    height: "40dp"
  snackbar:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.on-chrome}"
    typography: "{typography.body-medium}"
    rounded: "{rounded.none}"
    padding: "0 4dp 0 16dp"
    height: "48dp"
  snackbar-dark:
    backgroundColor: "{colors.raised-dark}"
    textColor: "{colors.text-on-dark}"
  snackbar-action:
    textColor: "{colors.brand-red-on-dark}"
    typography: "{typography.title-small}"
  filter-chip:
    textColor: "{colors.ink}"
    typography: "{typography.title-small}"
    rounded: "{rounded.none}"
  filter-chip-selected:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.ground}"
  suggestion-chip:
    textColor: "{colors.ink-muted}"
    typography: "{typography.title-small}"
    rounded: "{rounded.none}"
  button-primary:
    backgroundColor: "{colors.brand-red}"
    textColor: "{colors.on-chrome}"
    typography: "{typography.title-small}"
    rounded: "{rounded.full}"
  button-secondary:
    textColor: "{colors.ink}"
    typography: "{typography.title-small}"
    rounded: "{rounded.full}"
  bottom-nav:
    backgroundColor: "{colors.sheet}"
    textColor: "{colors.ink-muted}"
  bottom-nav-selected:
    textColor: "{colors.brand-red}"
---

# Design System: Central News

## Overview

**Creative North Star: "The Top-of-the-Hour Bulletin"**

Central News borrows the graphics of a television news broadcast: a near-black broadcast bar across the top, flat red labels like a lower-third caption, and a running order of headlines keyed by clock time under day headers. Hierarchy comes from scale and weight, not from boxes. One story is told big; everything else is a dense, evenly ruled list a commuter can scan by time and source in a few seconds.

The surfaces are flat and square. White sheets sit on a cool grey ground, divided by 1dp hairlines rather than lifted as cards. Icon red (the launcher icon's red) is the only fill colour and is spent on small, high-signal pieces: the wordmark box, the source tag, the tab indicator, the time column, the primary action, and the selected states. Archivo carries every word, from ExtraBold headlines to small tracked uppercase labels.

Dark theme keeps the same structure: pure-black chrome, a near-black ground, and charcoal sheets. Red fills stay identical, while red used as text or icon tint shifts to a lighter coral so it stays readable.

**Key Characteristics:**
- Broadcast chrome: a dark bar and pinned category tabs, continued into the status bar.
- One fill colour (icon red), used sparingly and always on a clean sheet or as a solid block.
- Flat white sheets on a grey ground, ruled by hairlines; no cards and no shadows.
- Square corners on every brand element; Archivo throughout, with uppercase tracked labels.
- Dense, time-first rows keyed by clock time, grouped under day headers, with 48dp touch targets that do not add height.

## Colors

A cool, near-monochrome ink-and-paper palette with a single broadcast red.

### Primary
- **Icon Red** (brand-red): The brand colour, matched to the app icon. Used as a solid fill (wordmark box, source tag, 3dp tab indicator, primary button) in both themes, and as text or tint (time column, selected nav, bookmarked icon, text buttons, progress) in light theme on white sheets only. White on it and it on white both clear AA.
- **Coral Red** (brand-red-on-dark): Red for text and icon tint on dark surfaces, where Icon Red falls below AA: charcoal sheets in dark theme, and the snackbar action in both themes (6.86:1 on ink, 4.87:1 on the raised dark bar). Its on-colour is **Deep Ember** (on-brand-red-on-dark) for dark-theme filled buttons.

### Neutral
- **Broadcast Ink** (ink): The light-theme broadcast bar, tabs, status bar, and splash background; also the main text colour on light surfaces and the time bug behind photos (at 85% opacity).
- **Studio Black** (chrome-dark): The broadcast bar and tabs in dark theme.
- **Chrome White** (on-chrome) and **Chrome Grey** (on-chrome-muted): Titles, icons and the selected tab on the dark bar; unselected tabs and search placeholder text.
- **Cool Ground** (ground) / **Night Ground** (ground-dark): The screen background. It shows only as the gap between sheets, such as the strip under the lead story and the area around status messages.
- **Paper Sheet** (sheet) / **Charcoal Sheet** (sheet-dark): Every content surface: rows, the lead headline strap, the bottom bar, footers.
- **Graphite** (ink-muted) / **Ash** (text-muted-on-dark): Secondary text such as source lines, supporting copy, unselected icons, and the time on a lead without a photo.
- **Hairline** (hairline) / **Night Hairline** (hairline-dark): 1dp row dividers, inset 16dp from the start edge, and the rule above the bottom bar.
- **Image Well** (image-well) / **Night Well** (image-well-dark): Placeholder and error fill for photos.
- **Night Text** (text-on-dark): Primary text in dark theme, including the dark snackbar message (12.14:1 on the raised bar).
- **Raised Night** (raised-dark) and **Raised Edge** (raised-edge-dark): The dark-theme snackbar bar and its 1dp edge. The bar alone sits at only 1.43:1 against Night Ground, so the edge is what separates it.

Material slots the app never paints itself (menus, the pull-to-refresh disc, container defaults) are set explicitly so they stay neutral: the surface-container steps run Paper Sheet, Paper Sheet, Cool Ground, then two light greys (light) and Night Ground, Charcoal Sheet, then three charcoal steps (dark); primaryContainer is a pale red with a deep red on-colour; secondaryContainer is a neutral grey with ink or Night Text. Exact values are in the sidecar.

### Named Rules
**The One Fill Rule.** Icon Red is the only fill colour. No secondary or tertiary accent exists; Material's secondary and tertiary slots map back to ink and red.

**The White Sheet Rule.** Red text and red icons sit only as Icon Red on a white sheet, or as Coral Red on a dark surface (a charcoal sheet, or the snackbar bar in either theme). Icon Red on the grey ground falls below AA (4.46:1), so any action that lands on the ground uses ink: the outlined secondary button and the Clear text button on Search, for example.

**The Untinted Surface Rule.** Material's surface tint is turned off (transparent). Elevated Material surfaces stay neutral; without this, dark sheets pick up a brown cast from the red.

## Typography

**Display Font:** Archivo (bundled TTF at Regular 400, Medium 500, SemiBold 600, ExtraBold 800; OFL)
**Body Font:** Archivo
**Label Font:** Archivo, uppercase and tracked

**Character:** A single grotesque used the way broadcast graphics use one face: heavy and slightly condensed in feel for headlines, plain for body copy, small and widely spaced for labels.

### Hierarchy
- **Headline** (ExtraBold, 24/29sp, -0.2 tracking): The lead story headline, at most 3 lines (4 at large font scale).
- **Title, bar** (ExtraBold, 18/24sp, 0.4 tracking, uppercase): The wordmark and every screen title in the broadcast bar.
- **Title, row** (SemiBold, 15/20sp): Running-order headlines and status-message titles, at most 3 lines (4 at large font scale).
- **Title, small** (SemiBold, 14/18sp, 0.1 tracking): Category tabs, chip labels, and the time column, which uses tabular figures with a trailing AM/PM marker set small (10sp) as broadcast clocks do.
- **Body** (Regular, 16/24sp and 14/20sp): The search field and recent searches at 16sp; status-message supporting text, footer messages, snackbar messages and the two-line description on Saved rows at 14sp.
- **Label** (SemiBold, 12/16sp, 0.6 tracking, uppercase): Source lines under row headlines, and list headers on the ground (day headers, Search sections).
- **Label, tag** (ExtraBold, 11/14sp, 0.9 tracking, uppercase): The source tag and the time bug.
- **Display** (ExtraBold, 36/44sp) is defined in the scale but is not used on any shipped screen yet.

### Named Rules
**The Caps Are Labels Rule.** Uppercase is for the wordmark, screen titles, source names, time bugs and list headers. Headlines and body copy stay in sentence case.

**The Extra Line Rule.** When the system font scale is above 1.15, headlines get one extra line so they cut at a word, not mid-word.

## Layout

A single phone column. The content gutter is 16dp at the start edge, and rows end at 4dp so the 48dp bookmark button sits flush near the edge. Spacing steps are 4, 8, 12, 16, 24 and 32dp; vertical padding inside rows is tight (10dp top, 8dp bottom) to keep the list dense.

The frame is fixed: the broadcast bar (56dp) with optional pinned category tabs (48dp) at the top, content scrolling beneath, and a sheet-coloured bottom bar for Home and Saved. The status bar takes the chrome colour, and the system navigation bar takes the sheet colour. Articles open in a Custom Tab whose toolbar and navigation bar carry the same chrome (ink in light, Studio Black in dark). Feeds are flush, full-width lists of sheets, grouped by day: each day opens with a list header on the ground (40dp minimum, 16dp start inset), and the first one names the bulletin and when it was fetched ("Yesterday's top stories · as of 3:44 AM"). Lists not in date order (Saved, Search by relevance) drop the headers and let each row carry its own date. An 8dp strip of ground is the only separator between a lead story and the running order. Empty, error and hint states are centred on the ground with 32dp side padding, and top-aligned (56dp from the top) on Search so the keyboard does not cover them.

**The 48dp Beside Rule.** Every touch target is at least 48dp, but trailing actions sit beside the whole row, centred vertically, so they never add a line of height.

## Elevation & Depth

Flat. There are no shadows. Depth comes from tone alone: dark chrome over a grey ground, white sheets on that ground, and hairlines between rows. The bottom bar has no tonal elevation and is separated by a hairline. The one layering effect is the lead story's headline strap, which overlaps the foot of its photo (44dp, inset 24dp from the end edge), so the picture shows beside it. The snackbar floats over content without a shadow: in light it is a solid ink bar, which stands out on white by tone alone; in dark it is a raised charcoal bar outlined by a 1dp Raised Edge, because its tone alone barely separates from the ground.

**The Flat Sheet Rule.** Surfaces separate by tone and hairline, never by shadow or by tinted elevation. Where tone is too close to separate, a 1dp edge does the work, not a shadow.

## Shapes

Square. The wordmark box, source tag, time bug, tab indicator, photos, sheets, the snackbar and the chips all have 0dp corners. Brand elements carry no borders; the only outlines are functional ones (unselected chips, the outlined button, and the dark snackbar's 1dp edge). The tab indicator is a 3dp bar sized to the word, not to the whole tab cell. The one exception is the Material default left in place on buttons (fully rounded).

## Components

### Broadcast Bar
Dark, flat chrome that carries the brand. It uses the ink (or Studio Black) container with white title and icons. The title slot holds either the wordmark (a red square box with "CN", then "CENTRAL NEWS" in caps, 8dp apart) or an uppercase screen title in the same type. On Search, the title slot becomes a transparent, borderless text field with white text and cursor and a Chrome Grey placeholder.

### Category Tabs
A custom horizontal row on chrome, 48dp tall at the default font size and growing with enlarged text instead of clipping it. Each tab is sized to its word with 12dp side padding and is never narrower than 48dp, so about five fit on a phone and short labels like "Top" keep a full target. Unselected tabs use Chrome Grey, and the selected tab uses white with a 3dp red bar under the word. Selecting a tab scrolls it fully into view.

### Source Tag
The lower-third caption: the source name in uppercase tag type, white on a solid red square block (5dp by 10dp padding). It is the one place red is a fill inside the feed.

### Time Bug
The story's clock time in uppercase tag type ("1:04 AM"), white on 85% ink, set 12dp into the top-end corner of a photo. Without a photo, the same text sits in Graphite beside the source tag. TalkBack reads the day and time in full ("Yesterday, 1:04 AM").

### Lead Story
A lower-third over a 16:9 photo. A white headline strap rides up over the photo's foot, and the source tag sits on the strap's top edge. A lead without a photo, or whose photo fails to load, puts the headline first, with the tag and time below it, so a red label never sits above the headline like an eyebrow and no empty image well is left behind.

### Running-Order Row
The core list unit, on a white sheet. It has a red tabular time column, then the row headline over an uppercase Graphite source line (4dp gap), then a trailing 48dp bookmark centred beside the whole row. Rows are divided by a hairline inset 16dp from the start edge.
- **Time column:** One width for every row, measured from the widest time the list can show at the current font size (plus 8dp), so headlines align down the list. Under day headers it shows the clock ("1:02 AM", marker small); in lists without headers it shows the clock for today and the date otherwise ("Oct 13").
- **Description:** Saved rows add a two-line Graphite body-medium description between headline and source; feed and search rows do not.

### List Header
A subheader on the ground: uppercase Graphite label type, 16dp start inset, 40dp minimum height, marked as a heading, with an optional trailing action (Clear, in ink). It labels day groups in the feed and sections on Search; it never sits directly above a single headline.

### Snackbar
A square, full-width bar inset 12dp from the sides and 8dp from the bottom, 48dp minimum, with a 14sp message and a Coral Red text action (View, Undo). Light: solid ink bar, Chrome White text. Dark: Raised Night bar, Night Text, 1dp Raised Edge. It announces politely to TalkBack. It carries bookmark feedback and the offline notice when a story cannot be opened.

### Buttons
- **Primary:** A Material filled button in brand red with white text (Coral Red with Deep Ember text in dark theme).
- **Secondary:** A Material outlined button with ink text, because it sits on the ground where red text fails AA.
- **Text:** Red text buttons only on white sheets (for example, Retry in the page-error footer); on the ground they use ink (Clear on Search).
- **Shape:** The Material default (fully rounded).

### Chips
Square (0dp) Material chips used on Search, on the ground.
- **Sort (filter chips):** "Most relevant" and "Latest", 8dp apart. Unselected: ink label with a Graphite outline. Selected: solid ink fill with a ground-coloured label, no outline.
- **Category shortcuts (suggestion chips):** A flowing row of the seven categories, Graphite outline and label, 8dp apart.

### Recent Searches
Plain rows on the ground, 48dp minimum, a Graphite history icon then the query in 16sp ink, 16dp apart. No hairlines.

### Bookmark Icon Button
Outlined in Graphite when not saved, filled in brand red when saved. Every toggle shows a snackbar: "Saved", with a View action, or "Removed from Saved", with Undo. The same glyph pair marks the Saved tab in the bottom bar.

### Navigation (Bottom Bar)
A Material navigation bar on the sheet colour, with no tonal elevation and no pill indicator (transparent). The selected item uses a filled icon and red label; unselected items use outlined icons in Graphite. Home uses the house glyph, Saved the bookmark glyph. A hairline sits above the bar.

### Status Message
Empty, error and hint states: a 40dp Graphite icon, a row-title heading, one line of supporting body text, and up to two buttons (primary and secondary, 8dp apart, 20dp below).

### Progress
Red circular indicators: full-screen for a first load, 24dp with a 2dp stroke on a sheet for loading more, and red pull-to-refresh.

## Do's and Don'ts

### Do:
- **Do** use brand-red as the only fill colour, and keep red text and red icons on white sheets (Coral Red brand-red-on-dark in dark theme).
- **Do** separate content with flat sheets, the ground colour, and 1dp hairlines inset 16dp.
- **Do** keep brand elements square (0dp): tags, bugs, the wordmark box, indicators, and photos.
- **Do** set the wordmark, screen titles, source names and time bugs in uppercase tracked Archivo, and headlines in sentence case.
- **Do** show both the source and the time wherever a headline appears: clock times in tabular figures with a small AM/PM, under a day header, or the date where a list has no day headers.
- **Do** place 48dp trailing actions beside the whole row so they never add height.
- **Do** keep surfaceTint transparent on every Material scheme, and set the surface-container and container slots explicitly so Material defaults stay neutral.

### Don't:
- **Don't** put brand-red text or icons on the ground (#F2F3F5); at 4.46:1 it fails AA. Use ink.
- **Don't** add shadows, tonal elevation, or rounded cards to feed content.
- **Don't** introduce a second accent colour or a gradient.
- **Don't** place a red label above a headline on its own. The source tag sits above a headline only as a lower-third on a photo's edge; otherwise it follows the headline.
- **Don't** use Material's ScrollableTabRow for the category tabs; it forces 90dp tabs.
- **Don't** show relative ages ("26h"); headlines can be a day old, so time reads as a clock time under a day header and an "as of" fetch time.
