package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.ohieku.newsapp.R
import com.ohieku.newsapp.data.features.news.model.Article
import com.ohieku.newsapp.ui.compose.theme.Red100
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun Loader() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(60.dp)
                .padding(10.dp),
            color = Red100
        )
    }
}

@Composable
fun NewsRowComponent(page: Int, article: Article, total: Int) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val topColumnHeight = 240.dp
    val bottomColumnHeight = screenHeight - topColumnHeight

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background((Color.White))
    ) {
        val (topColumn, bottomColumn) = createRefs()

        Column (
            modifier = Modifier
                .constrainAs(topColumn) {
                    top.linkTo(parent.top)
                }
        ) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(topColumnHeight),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.placeholder)
            )
        }

        Column (modifier = Modifier
            .clip(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
            .background(color = Color.White)
            .height(bottomColumnHeight)
            .constrainAs(bottomColumn) {
                top.linkTo(topColumn.bottom, margin = -(15).dp)
            }
        ) {
            Spacer(modifier = Modifier.size(20.dp))

            HeaderText(article.title?: "")

            Spacer(modifier = Modifier.size(10.dp))

            ContextText(article.description?: "")

            Spacer(modifier = Modifier.weight(1f))

            FooterDetail(article.author?: "", article.source?.name?: "", page, total)
        }
    }
}

@Composable
fun HeaderText(text: String, centerText: Boolean = false) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        text = text,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        ),
        textAlign = if(centerText) TextAlign.Center else TextAlign.Start
    )
}

@Composable
fun ContextText(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        text = text,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
    )
}

@Composable
fun FooterDetail(author: String, url: String, page: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = author,
            color = Color.Red,
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(
            id = R.drawable.placeholder),
            contentDescription = null
        )
        HeaderText(text = stringResource(R.string.no_news_at_the_moment_please_check_again_later), true)
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateComponentPreview () {
    EmptyState()
}

@Preview
@Composable
fun NewsRowPreviewComponent() {
    val article = Article(
        author = "Mr X",
        title = "Hello, Dummy article",
        "A rough talk about content",
        "www.w3schools.com",
        null,
        null,
        null,
        null
    )
    NewsRowComponent(0, article, 1)
}
