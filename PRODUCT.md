# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users
People doing a quick daily catch-up on the news, in short bursts: a commute, a coffee, a gap between tasks. They want to see the day's top headlines fast, scan without effort, and open the few stories worth reading.

## Product Purpose
Central News shows today's top headlines from many publishers in one Android app. It also lets readers browse by category, search, and save stories to read later. Success means a reader can see what matters right now within a few seconds of opening the app, and leave with the stories they care about opened or saved.

## Positioning
"Central" means *what matters right now*. The app is a front page, not an endless feed: it leads with the biggest story and makes importance and recency obvious, instead of giving every headline the same weight.

## Operating Context
- Opened in short, often one-handed sessions; scanning speed matters more than depth.
- Reading happens on the publisher's own site, opened in a Chrome Custom Tab. The app owns the scanning, choosing and saving, not the article body.
- Connectivity varies (commutes). Saved stories are stored on the device; the feed itself needs a network connection.

## Capabilities and Constraints
- Content comes from NewsAPI on the **free developer plan**:
  - Top headlines only reliably return results for the US. The app tries the device's country first and falls back to `us`.
  - At most 100 results per query (HTTP 426 beyond that).
  - About 100 requests per day.
  - Search uses the `/everything` endpoint, sorted newest first and filtered to the device language when supported.
  - Headlines arrive with a " - Source" suffix.
  - Many articles have no image (`urlToImage` is null).
- Categories: Top, Business, Entertainment, Health, Science, Sports, Technology.
- Features: home feed with categories, pull to refresh, search, and bookmarks (Room, stored on the device), with Home and Saved tabs.
- Stack: Kotlin, Jetpack Compose, Material 3 1.2.1, Hilt, Retrofit, Coil. minSdk 24, targetSdk 34.

## Brand Commitments
- Name: **Central News**, used for both the launcher label and the in-app wordmark.
- Brand colour must match the app icon: icon red **#E20101** (a white newspaper glyph on a red field, `ic_launcher-playstore.png`). The earlier orange #FF5A00 does not match the icon.

## Evidence on Hand
- Real, live headline content from NewsAPI. There are no testimonials, user counts or press, and future work must not invent any.
- App icon assets in `app/src/main/res/mipmap-*` and `ic_launcher-playstore.png`.

## Product Principles
1. **Lead with what matters.** One clear top story, then fast-to-scan headlines. Hierarchy over uniformity.
2. **Scannable in seconds.** Density and legibility come before decoration; imagery has to earn its space.
3. **Always show the source and the time.** Readers judge a headline by who published it and how fresh it is.
4. **Never lose the reader's things.** Saved stories are theirs: confirm saves, allow undo, keep them available offline.

## Accessibility & Inclusion
WCAG AA is required: text contrast of at least 4.5:1 (3:1 for large text), layouts that hold at large system font sizes, 48dp touch targets, and full TalkBack support.
