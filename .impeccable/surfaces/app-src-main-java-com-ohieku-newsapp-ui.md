---
version: 1
slug: "app-src-main-java-com-ohieku-newsapp-ui"
primary_target: "app/src/main/java/com/ohieku/newsapp/ui"
related_targets: []
---


# Surface brief: Central News app UI (Home, Search, Saved)

Mode: Operate (native Android, Material 3). Chosen direction: Broadcast Bulletin. Build path: code-led. Chosen sketch: `.impeccable/mocks/decision/model-pick.html`.

## Direction contract

THESIS: The front page is the top-of-the-hour bulletin: one lead story told like a broadcast lower-third, then a dense running order with times. It refuses the equal-weight image-card feed.

OWN-WORLD:
- Broadcast chrome: a near-black #0D0F14 top bar carrying the red "CN" box wordmark and the category tabs, with a 3dp red indicator.
- Icon red #E20101 as the single fill colour (source tag, time bug accent, indicator, primary button); #FF6B5E for red text in dark mode.
- Light grey #F2F3F5 ground with white running-order sheets divided by 1px hairlines, no cards.
- Archivo throughout: ExtraBold headlines, SemiBold rows, uppercase tracked labels.

STORY: A commuter sees the biggest story and plainly how old the bulletin is ("Yesterday's top stories · as of 3:44 AM"), scans 6 or more headlines per screen by clock time and source, opens one in a tap (offline, a message instead of the browser's error), saves with confirmation and Undo, and can always reach Saved offline.

FIRST VIEWPORT:
- Dark top bar (56dp) with the wordmark and search, then pinned scrollable category tabs (48dp).
- A day header on the ground naming the bulletin and its fetch time, above the lead.
- Lead photo (16:9) with a clock-time bug top-right, and a lower-third strap overlapping its foot: a red source tag over a white headline panel (headlineSmall, at most 3 lines). A lead with no photo, or whose photo fails to load, puts the headline first with the tag and time beneath it.
- Running-order rows: a red tabular clock-time column of one measured width (small AM/PM), the headline (3 lines max), an uppercase source label, and a trailing bookmark; a new day header wherever the day changes.
- Bottom bar: Home and Saved.
- Translation: the sketch's NEXT ticker is dropped (an overlay marquee on a working screen); the running order carries "next".

FORM: Broadcast news graphics, position 1 on the grounded list (the pick), seed 25838fd1.

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance
