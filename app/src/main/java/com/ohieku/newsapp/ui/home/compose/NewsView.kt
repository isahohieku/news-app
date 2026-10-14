package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.compose.AsyncImage
import com.ohieku.newsapp.R
import com.ohieku.newsapp.ui.compose.theme.PrimaryOrange
import com.ohieku.newsapp.domain.features.news.model.Article

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
            color = PrimaryOrange
        )
    }
}

@Composable
fun NewsCardComponent(page: Int, article: Article, total: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Column {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = article.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.placeholder)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Category Tag
                Text(
                    text = "SPACE", // Mocked as we don't have category in Article model
                    color = PrimaryOrange,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Headline
                Text(
                    text = article.title ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Description (Optional if space needed, keeping it as per reference)
                Text(
                    text = article.description ?: "",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1 hour ago • ${article.source?.name ?: article.author ?: "Unknown"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Place, contentDescription = "Bookmark", tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = Color.Gray)
                    }
                }
            }
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

@Composable
fun ErrorState(message: String, onRetryTapped: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeaderText(text = message, centerText = true)
        Button(onClick = onRetryTapped) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
fun PageErrorFooter(message: String, onRetryTapped: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onRetryTapped) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateComponentPreview () {
    EmptyState()
}

@Preview(showBackground = true)
@Composable
fun ErrorStateComponentPreview() {
    ErrorState(message = "No Internet, please check and try again", onRetryTapped = {})
}

@Preview
@Composable
fun NewsCardPreviewComponent() {
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
    NewsCardComponent(0, article, 1)
}
