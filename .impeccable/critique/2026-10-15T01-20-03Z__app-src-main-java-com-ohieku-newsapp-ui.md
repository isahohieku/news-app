---
target: the app UI (Home, Search, Saved)
total_score: 24
max_score: 40
na_heuristics: 
p0_count: 0
p1_count: 3
target_identity: "file:/Users/isahohieku/Documents/Projects/NewsApp/app/src/main/java/com/ohieku/newsapp/ui"
timestamp: 2026-10-15T01-20-03Z
slug: app-src-main-java-com-ohieku-newsapp-ui
---
Method: dual-agent (A: design-review sub-agent · B: detector sub-agent)

# Critique: Central News app UI (Home, Search, Saved), native Android, Operate mode

## Design Health Score
| # | Heuristic | Score | Key Issue |
|---|-----------|-------|-----------|
| 1 | Visibility of System Status | 3 | Spinner, pull-to-refresh and selected tile work. Loading is a bare spinner; saving gives no feedback beyond the icon fill. |
| 2 | Match System / Real World | 3 | Plain copy and relative times. Headlines repeat the source ("… - ESPN" under an ESPN tag); the flame icon reads as "Trending". |
| 3 | User Control and Freedom | 2 | Removing a saved article has no Undo. Re-tapping Home doesn't scroll to top. The category row scrolls away. |
| 4 | Consistency and Standards | 3 | One card on all screens. Text sizes are hardcoded rather than from a type scale; Search exists only on Home. |
| 5 | Error Prevention | 2 | The whole 300dp+ card opens the browser, so scrolls trigger accidental opens. Unsave is one tap. |
| 6 | Recognition Rather Than Recall | 3 | Tiles and tabs have labels. Search is an icon on Home only. |
| 7 | Flexibility and Efficiency | 1 | No scroll-to-top, recent searches, share, swipe actions or density option. |
| 8 | Aesthetic and Minimalist Design | 2 | About 1.2 stories per screen, 200dp grey placeholders, a redundant CATEGORIES label. |
| 9 | Error Recovery | 2 | Distinct error messages with Retry. Offline cold start is a dead end: no cache, no link to Saved. |
| 10 | Help and Documentation | 3 | Good contextual hints for the app's size. |
| **Total** | | **24/40** | **Acceptable** |

## Design Specificity Verdict
Design review: generic. This is the NewsAPI demo layout (centred title, CATEGORIES icon tiles, identical image-first cards). Orange #FF5A00 is the only brand signal and it is used for everything. There is no lead story, no ranking, no editorial type (Roboto; Type.kt customises only bodyLarge), and no sense of an edition. Reading hands off to a publisher's page in a Chrome tab.

Automated scan: the detector ran but scanned 0 files. It covers web file types only, not Kotlin or Android XML, so its "clean" result is meaningless. Supplementary code search and contrast calculations:
- #FF5A00 on white = 3.13:1 (fails AA); white on #FF5A00 = 3.13:1 (fails); #757575 on white = 4.61:1 (just passes); dark mode passes.
- 10 hardcoded text sizes, 0 uses of MaterialTheme.typography.
- 0 hardcoded user-visible strings; all IconButtons are 48dp.
- False positives: the Color.Transparent uses; Color.Red is only in the dead FooterDetail.

Overlays: none (native app, no web surface).

## Overall Impression
Solid state engineering underneath a safe template. The biggest opportunity is the feed: a headline scanner with a lead story and dense, text-first rows.

## What's Working
1. Shared ArticleFeed and NewsCardComponent across Home, Search and Saved, with live bookmark state; paging, error and refresh behave consistently.
2. Designed states, with the category row persisting through loading, error and empty; inline page retry; refresh errors shown in a snackbar.
3. A disciplined dark theme (surfaceTint transparent, orange as an accent, passes contrast); tab state is preserved.

## Priority Issues
- [P1] The brand orange fails contrast on light surfaces (source tag, white label on the selected tile, white Retry label, orange tab labels; all 3.13:1). Fix: a light-scheme text/icon orange around #C64500 (about 4.9:1), the tag at 11–12sp, dark text on the orange tile. Command: /impeccable colorize
- [P1] Feed density and hierarchy: one story per screen, 200dp placeholder on cards without a photo, unlimited headline lines, equal weight for every card. Fix: drop the image when there is no photo, a lead card plus compact thumbnail rows, cap headlines at 3 lines, strip the " - Source" suffix in the mapper. Command: /impeccable layout
- [P1] Unsave is instant with no Undo, and save has no confirmation; items jump when removed. Fix: an Undo snackbar on Saved, animated item placement, a "Saved — View" snackbar on Home and Search. Command: /impeccable harden
- [P2] The category row takes about 25% of the first screen and scrolls away; Home re-tap does nothing. Fix: FilterChips or ScrollableTabRow at 40–48dp, drop the label, pin the row (stickyHeader), re-tapping Home scrolls to top. Command: /impeccable distill
- [P2] No type system: 10 literal sizes, and the empty/error/search messages use the 24sp HeaderText; the offline error has no icon or way forward. Fix: a full Typography with an editorial headline face; state messages as icon, title, body, action; an "Open Saved" action on the offline error. Command: /impeccable typeset

## Persona Red Flags
- Sam (accessibility): the 3.13:1 tag and white-on-orange labels; the fixed 88dp tile height clips at font scale 1.5+; no TalkBack Save action.
- Casey (one-handed, distracted): Search in the top-right corner; categories scroll away; accidental card taps open the browser; offline cold start gives nothing; clearing a search drops the keyboard.
- Alex (power user): no scroll-to-top, recent searches, share or swipe-to-save; no sort control on search (off-topic first result for "climate"); about 20 flings per page of results.

## Minor Observations
Descriptions cut off with no ellipsis; the transparent top bar doesn't react to scrolling; a black system navigation bar (not edge-to-edge, and statusBarColor is deprecated); the placeholder doubles as the loading image; a blank white frame at cold start; "News App" vs "Central News"; the faint #F5F5F5 tile edge; search messages sit low behind the keyboard; dead code (FooterDetail, ContextText, DividerColor).

## Questions to Consider
- Without the wordmark, what says this isn't a NewsAPI tutorial? What does "Central" promise (a daily edition, one lead story, a briefing)?
- Should the app own the reading hand-off (a summary sheet before "Open original")?
- What if photos had to earn their space, with only the lead story and articles with real photos getting one?
