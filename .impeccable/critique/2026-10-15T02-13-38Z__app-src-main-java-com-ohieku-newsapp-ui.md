---
target: the app UI (Home, Search, Saved)
total_score: 28
max_score: 40
na_heuristics: 
p0_count: 0
p1_count: 2
target_identity: "file:/Users/isahohieku/Documents/Projects/NewsApp/app/src/main/java/com/ohieku/newsapp/ui"
timestamp: 2026-10-15T02-13-38Z
slug: app-src-main-java-com-ohieku-newsapp-ui
closed: true
---
Method: dual-agent (A: design-review sub-agent · B: detector sub-agent)

# Critique: Central News app UI (Home, Search, Saved), second run, native Android, Operate mode

## Design Health Score
| # | Heuristic | Score | Key Issue |
|---|-----------|-------|-----------|
| 1 | Visibility of System Status | 3 | No "updated at"; every row at 26h makes stale and fresh look the same |
| 2 | Match System / Real World | 3 | "26H AGO" on the lead contradicts "right now" |
| 3 | User Control and Freedom | 3 | Undo/View present; no swipe actions or search history |
| 4 | Consistency and Standards | 3 | Strong tokens (0 hardcoded sizes or strings); stock snackbar and Custom Tab break the system |
| 5 | Error Prevention | 2 | Failed lead image leaves a ~300dp grey slab; Saved implies offline reading |
| 6 | Recognition Rather Than Recall | 3 | Search idle has no recents or suggestions |
| 7 | Flexibility and Efficiency | 2 | No history, swipe, share, or relevance toggle |
| 8 | Aesthetic and Minimalist Design | 3 | Dense and disciplined; slab and one-value time column |
| 9 | Error Recovery | 3 | Excellent offline state; no-results has no action; saved story offline dead-ends |
| 10 | Help and Documentation | 3 | One-line teaching empty states |
| **Total** | | **28/40** | **Good** |

## Design Specificity Verdict
Design review: now product-specific. The broadcast bar, red CN box, lower-third lead, time bug and red time column form one language and refuse the equal-weight card feed; about 6 headlines per screen. It breaks at: a time column flattened to 24-28h by the API delay; stock Material/Chrome at the edges (rounded snackbar, light Custom Tab toolbar); undesigned worst cases (image-failure slab, no offline reading). Brand risk: the CN box reads close to CNN.

Automated scan: the detector scanned 0 files (a control test confirmed it skips .kt/.xml), so no coverage. Mechanical checks:
- 0 hardcoded text sizes (15 theme styles in use), 0 literal strings, no red text on the grey ground.
- New: snackbar action in dark mode is inversePrimary #E20101 on #F1F2F4 = 4.42:1, failing AA (inferred from M3 defaults).
- M3 lavender defaults remain in unset slots (pull-to-refresh disc on #ECE6F0).
- The "Top" tab cell is 49.6dp, under 48dp at font scale 0.85; the tab row height is fixed at 48dp.
- Deprecations: statusBarColor/navigationBarColor (targetSdk 35); the experimental PullToRefresh API is removed in M3 1.3.
- Dead resources: 9 template colours, news_icon.xml, placeholder.png.

## Overall Impression
24 to 28/40. Hierarchy, density, contrast, feedback and type are fixed, and the app has an identity. What remains is honesty under the API's limits: a time column with one value, an image that fails to load, and an offline promise Saved can't keep.

## What's Working
1. The lower-third lead (strap over the photo, tag on its edge, time bug); the no-photo lead puts the headline first.
2. The running-order row: about 6 stories per screen; holds at font scale 1.3.
3. Save/Undo/View snackbars, and the offline state with Open Saved and AA-safe ink buttons.

## Priority Issues
- [P1] A failed lead image leaves a grey slab of about 300dp (Technology). Fix: Coil state; on error, the no-photo strap. /impeccable harden
- [P1] Saved promises offline reading, but stories are links; offline taps end on Chrome's dinosaur page. Fix: connectivity check and snackbar, a stored description on Saved, reword the two strings. /impeccable harden, /impeccable clarify
- [P2] The time column carries no information (24-28h everywhere); dates will clip in the fixed 48dp column. Fix: clock times plus Yesterday/date group headers, "Updated" line, widthIn(min). /impeccable clarify
- [P2] Search sorts newest-first (irrelevant results) and the idle screen is empty. Fix: sortBy=relevancy plus a Relevant/Latest switch, recents and category chips when idle, a no-results action. /impeccable onboard
- [P2] Seams: rounded/shadowed snackbar with a dark-mode action at 4.42:1, unthemed Custom Tab, M3 lavender slots, app_name "News App". Fix: custom square ink Snackbar, toolbar colour, set the remaining scheme slots, rename. /impeccable polish

## Persona Red Flags
- Commuter: offline cold start has no cached feed; Saved stories won't open offline; image slab; "26h" reads as stale.
- Low-vision reader with TalkBack: at 1.3 the lead plus about 1.3 rows; dates clip in the time column; fixed 48dp tab row. Good: spoken times, distinct Save/Remove labels, heading semantics.
- Power user: no history, swipe or share; no relevance control; Saved shows publish age, not when saved.

## Minor Observations
The 24dp strap-side seam on light photos; two reds in dark mode; the Saved tab glyph differs from row bookmarks; the search hint repeats the placeholder; lead headlines are cut at 3 lines; no end-of-bulletin; deprecations and dead resources; marginal "Top" tab width.

## Questions to Consider
- Should the bulletin admit day-old news ("Yesterday's top stories, as of 3:05 pm") with clock times?
- Is the red CN box distinct enough from CNN?
- What if the front page ended after one lead and ten rows?
