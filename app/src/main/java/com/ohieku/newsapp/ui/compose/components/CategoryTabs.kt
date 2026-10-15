package com.ohieku.newsapp.ui.compose.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.ui.compose.theme.LocalNewsColors
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme

@get:StringRes
private val NewsCategory.labelRes: Int
    get() = when (this) {
        NewsCategory.Top -> R.string.category_top
        NewsCategory.Business -> R.string.category_business
        NewsCategory.Entertainment -> R.string.category_entertainment
        NewsCategory.Health -> R.string.category_health
        NewsCategory.Science -> R.string.category_science
        NewsCategory.Sports -> R.string.category_sports
        NewsCategory.Technology -> R.string.category_technology
    }

/**
 * Category tabs pinned in the broadcast bar. Material's ScrollableTabRow forces 90dp tabs, so these are sized to
 * their words (about five fit a phone) with the red indicator drawn under the word, not the whole cell.
 */
@Composable
fun CategoryTabs(
    selectedCategory: NewsCategory,
    onCategorySelected: (NewsCategory) -> Unit
) {
    val colors = LocalNewsColors.current
    val listState = rememberLazyListState()

    // Bring the selected tab fully into view, e.g. after picking one at the far end
    LaunchedEffect(selectedCategory) {
        val index = selectedCategory.ordinal
        val layoutInfo = listState.layoutInfo
        val item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
        val fullyVisible = item != null &&
            item.offset >= layoutInfo.viewportStartOffset &&
            item.offset + item.size <= layoutInfo.viewportEndOffset - layoutInfo.afterContentPadding
        if (!fullyVisible) listState.animateScrollToItem(index)
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(colors.chrome)
            .selectableGroup()
    ) {
        items(NewsCategory.entries) { category ->
            val selected = category == selectedCategory
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .selectable(selected = selected, role = Role.Tab, onClick = { onCategorySelected(category) })
                    .padding(horizontal = 12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight().width(IntrinsicSize.Max)
                ) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = stringResource(category.labelRes),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (selected) colors.onChrome else colors.onChromeMuted,
                        maxLines = 1
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(if (selected) colors.brandFill else colors.chrome)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CategoryTabsPreview() {
    NewsAppTheme {
        CategoryTabs(selectedCategory = NewsCategory.Top, onCategorySelected = {})
    }
}
