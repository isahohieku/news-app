package com.ohieku.newsapp.ui.home.compose

import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.ohieku.newsapp.extensions.utils.StoryDay
import com.ohieku.newsapp.extensions.utils.storyDay
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import java.text.DateFormatSymbols
import java.util.Calendar
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.Source
import com.ohieku.newsapp.extensions.utils.parsePublishedAt
import com.ohieku.newsapp.ui.compose.theme.LocalNewsColors
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme
import java.util.Date
import java.util.Locale

private val StrapOverlap = 44.dp
private val StrapInset = 24.dp

/** How the time column reads: the feed groups by day so rows show the clock; lists without day headers show the day. */
enum class TimeStyle {
    /** Always the clock time ("2:06 PM"); the day comes from the header above. */
    Clock,
    /** Clock time for today, otherwise the date ("Oct 13"). */
    DayOrClock,
}

/** A story's time: [compact] for the time column and bug, [spoken] in full for TalkBack. */
private data class StoryTime(val compact: String, val spoken: String)

/** Headlines get one more line when the system font is enlarged, so they cut off at a word, not mid-word. */
@Composable
private fun headlineLines(base: Int): Int = if (LocalDensity.current.fontScale > 1.15f) base + 1 else base

@Composable
private fun clockPattern(): String {
    val context = LocalContext.current
    val skeleton = if (DateFormat.is24HourFormat(context)) "Hm" else "hm"
    return DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton)
}

/** The day header label: "Today", "Yesterday" or "Mon, Oct 13". */
@Composable
fun dayLabel(day: StoryDay): String = when (day) {
    StoryDay.Today -> stringResource(R.string.day_today)
    StoryDay.Yesterday -> stringResource(R.string.day_yesterday)
    is StoryDay.OnDate -> DateFormat.format(
        DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEMMMd"),
        Date(day.startOfDay)
    ).toString()
}

@Composable
fun formatClock(millis: Long): String = DateFormat.format(clockPattern(), Date(millis)).toString()

/** Sets a trailing AM/PM marker small, as broadcast clocks do, so the time column stays narrow. */
private fun clockMarkup(text: String): AnnotatedString {
    val marker = DateFormatSymbols.getInstance(Locale.getDefault()).amPmStrings
        .firstOrNull { it.isNotEmpty() && text.endsWith(it) }
        ?: return AnnotatedString(text)
    return buildAnnotatedString {
        append(text)
        addStyle(SpanStyle(fontSize = 10.sp), text.length - marker.length, text.length)
    }
}

/**
 * One width for every row's time column, measured from the widest time this style can show at the current font size,
 * so headlines line up down the running order instead of shifting with "9:45" and "10:08".
 */
@Composable
private fun rememberTimeColumnWidth(style: TimeStyle, textStyle: TextStyle): Dp {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val widestClock = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 59)
    }.timeInMillis
    val samples = buildList {
        add(clockMarkup(formatClock(widestClock)))
        if (style == TimeStyle.DayOrClock) {
            val widestDate = Calendar.getInstance().apply { set(Calendar.MONTH, Calendar.SEPTEMBER); set(Calendar.DAY_OF_MONTH, 30) }
            add(AnnotatedString(DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMd"), widestDate.time).toString()))
        }
    }
    val widest = remember(samples, textStyle, density) { samples.maxOf { measurer.measure(it, textStyle).size.width } }
    return with(density) { widest.toDp() } + 8.dp
}

@Composable
private fun rememberStoryTime(publishedAt: String?, style: TimeStyle): StoryTime? {
    val millis = remember(publishedAt) { parsePublishedAt(publishedAt) } ?: return null
    val day = storyDay(millis, System.currentTimeMillis())
    val clock = formatClock(millis)
    val compact = when {
        style == TimeStyle.Clock || day == StoryDay.Today -> clock
        else -> DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMd"), Date(millis)).toString()
    }
    return StoryTime(compact = compact, spoken = stringResource(R.string.spoken_story_time, dayLabel(day), clock))
}

private val Article.sourceLabel: String?
    get() = source?.name?.takeIf { it.isNotBlank() }

@Composable
fun Loader(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun BookmarkButton(isBookmarked: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = stringResource(if (isBookmarked) R.string.bookmark_remove else R.string.bookmark_add),
            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** The red source tag: the lower-third's label, the one place brand red is a fill inside the feed. */
@Composable
private fun SourceTag(source: String, modifier: Modifier = Modifier) {
    val colors = LocalNewsColors.current
    Text(
        text = source.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = colors.onBrandFill,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .background(colors.brandFill)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

/**
 * The top story as a broadcast lower-third: the photo with a time bug, and a headline strap that rides up over
 * the photo's foot carrying the red source tag. Without a photo the strap stands alone, tag and time in its meta line.
 */
@Composable
fun LeadStory(
    article: Article,
    onClick: (() -> Unit)?,
    isBookmarked: Boolean,
    onBookmarkClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val colors = LocalNewsColors.current
    val time = rememberStoryTime(article.publishedAt, TimeStyle.Clock)
    val openLabel = stringResource(R.string.open_article)
    // A photo that fails to load drops the lead to the no-photo strap instead of leaving an empty well
    var imageFailed by remember(article.urlToImage) { mutableStateOf(false) }
    val hasImage = !article.urlToImage.isNullOrBlank() && !imageFailed
    val bug = time?.compact?.uppercase()

    val headline: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 16.dp, top = 12.dp, bottom = if (hasImage) 8.dp else 0.dp)
        ) {
            Text(
                text = article.title.orEmpty(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = headlineLines(3),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 2.dp)
                    .semantics { heading() }
            )
            if (onBookmarkClick != null) {
                BookmarkButton(isBookmarked, onBookmarkClick)
            } else {
                Spacer(Modifier.width(16.dp))
            }
        }
    }

    // On a photo the tag rides the strap's top edge as the lower-third label. Without one, a red label above the
    // headline would be an eyebrow, so the headline leads and the tag and time follow it, as in the rows.
    val strap: @Composable () -> Unit = {
        Column {
            if (hasImage) {
                article.sourceLabel?.let { SourceTag(it) }
                headline()
            } else {
                headline()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 16.dp)
                ) {
                    article.sourceLabel?.let { SourceTag(it) }
                    if (bug != null) {
                        Text(
                            text = bug,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .semantics { contentDescription = time?.spoken.orEmpty() }
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClickLabel = openLabel) { onClick?.invoke() }
    ) {
        if (hasImage) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val photoHeight = maxWidth * 9f / 16f
                Box(modifier = Modifier.fillMaxWidth().height(photoHeight)) {
                    AsyncImage(
                        model = article.urlToImage,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = ColorPainter(colors.imageWell),
                        error = ColorPainter(colors.imageWell),
                        onError = { imageFailed = true },
                        modifier = Modifier.fillMaxSize()
                    )
                    if (bug != null) {
                        Text(
                            text = bug,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onChrome,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(colors.chrome.copy(alpha = 0.85f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .semantics { contentDescription = time?.spoken.orEmpty() }
                        )
                    }
                }
                // The strap starts inside the photo, leaving the right edge of the picture showing beside it
                Box(modifier = Modifier.padding(top = photoHeight - StrapOverlap, end = StrapInset)) {
                    strap()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 4.dp)
            ) {
                strap()
            }
        }
    }
}

/**
 * One line of the running order: time, headline over source, bookmark. The bookmark sits beside the whole row,
 * centred, so its 48dp target never adds a line of its own to the row's height.
 */
@Composable
fun RunningOrderRow(
    article: Article,
    onClick: (() -> Unit)?,
    isBookmarked: Boolean,
    onBookmarkClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    timeStyle: TimeStyle = TimeStyle.Clock,
    showDescription: Boolean = false
) {
    val colors = LocalNewsColors.current
    val time = rememberStoryTime(article.publishedAt, timeStyle)
    val timeTextStyle = MaterialTheme.typography.titleSmall.copy(fontFeatureSettings = "tnum")
    val timeColumnWidth = rememberTimeColumnWidth(timeStyle, timeTextStyle)
    val openLabel = stringResource(R.string.open_article)

    Column(modifier = modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = onClick != null, onClickLabel = openLabel) { onClick?.invoke() }
                .padding(start = 16.dp, end = 4.dp, top = 10.dp, bottom = 8.dp)
        ) {
            Text(
                text = clockMarkup(time?.compact.orEmpty()),
                style = timeTextStyle,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .width(timeColumnWidth)
                    .padding(end = 8.dp)
                    .align(Alignment.Top)
                    .padding(top = 2.dp)
                    .semantics { contentDescription = time?.spoken.orEmpty() }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = headlineLines(3),
                    overflow = TextOverflow.Ellipsis
                )
                if (showDescription && !article.description.isNullOrBlank()) {
                    Text(
                        text = article.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                article.sourceLabel?.let { source ->
                    Text(
                        text = source.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            if (onBookmarkClick != null) {
                BookmarkButton(isBookmarked, onBookmarkClick)
            }
        }
        HorizontalDivider(color = colors.hairline, modifier = Modifier.padding(start = 16.dp))
    }
}

/** A list subheader on the ground: day groups in the feed, and sections on the search screen. */
@Composable
fun ListHeader(text: String, modifier: Modifier = Modifier, action: (@Composable () -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp)
            .padding(start = 16.dp, end = 4.dp, top = 8.dp)
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 6.dp)
                .semantics { heading() }
        )
        action?.invoke()
    }
}

/**
 * Empty, error and hint states: icon, a short title, one supporting line, and up to two actions.
 * [alignTop] keeps the message clear of the keyboard on Search.
 */
@Composable
fun StatusMessage(
    icon: ImageVector,
    title: String,
    body: String?,
    modifier: Modifier = Modifier,
    alignTop: Boolean = false,
    primaryAction: Pair<String, () -> Unit>? = null,
    secondaryAction: Pair<String, () -> Unit>? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (alignTop) Arrangement.Top else Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = if (alignTop) 56.dp else 24.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp).semantics { heading() }
        )
        if (body != null) {
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
        if (primaryAction != null || secondaryAction != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 20.dp)
            ) {
                primaryAction?.let { (label, onClick) -> Button(onClick = onClick) { Text(label) } }
                // Ink, not red: red text on the grey ground falls just below AA
                secondaryAction?.let { (label, onClick) ->
                    OutlinedButton(
                        onClick = onClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                    ) { Text(label) }
                }
            }
        }
    }
}

@Composable
fun LoadMoreIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 2.dp
        )
    }
}

@Composable
fun PageErrorFooter(message: String, onRetryTapped: () -> Unit) {
    // On the white sheet, like the rows, so the red Retry clears AA
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onRetryTapped) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

private val previewArticle = Article(
    author = "Alden Gonzalez",
    title = "Ohtani will be in Dodgers' Game 3 NLCS lineup, Roberts says",
    description = "Los Angeles Dodgers manager Dave Roberts told reporters Tuesday that struggling superstar Shohei Ohtani will play.",
    url = "https://www.espn.com",
    urlToImage = null,
    publishedAt = "2026-10-13T19:06:00Z",
    content = null,
    source = Source(id = null, name = "ESPN")
)

@Preview(showBackground = true)
@Composable
fun LeadStoryPreview() {
    NewsAppTheme {
        LeadStory(previewArticle, onClick = {}, isBookmarked = false, onBookmarkClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun RunningOrderRowPreview() {
    NewsAppTheme {
        RunningOrderRow(previewArticle, onClick = {}, isBookmarked = true, onBookmarkClick = {})
    }
}
