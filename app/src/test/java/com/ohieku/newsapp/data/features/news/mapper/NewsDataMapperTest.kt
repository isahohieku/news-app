package com.ohieku.newsapp.data.features.news.mapper

import com.ohieku.newsapp.data.features.news.mapper.NewsDataMapper.stripSourceSuffix
import org.junit.Assert.assertEquals
import org.junit.Test

class NewsDataMapperTest {

    @Test
    fun `drops a suffix that names the source`() {
        assertEquals(
            "Ohtani will be in Dodgers' Game 3 NLCS lineup, Roberts says",
            stripSourceSuffix("Ohtani will be in Dodgers' Game 3 NLCS lineup, Roberts says - ESPN", "ESPN")
        )
    }

    @Test
    fun `drops a publisher suffix that differs from the source name`() {
        assertEquals(
            "Rare fossil of T. rex cousin goes on display",
            stripSourceSuffix("Rare fossil of T. rex cousin goes on display - AP News", "Associated Press")
        )
    }

    @Test
    fun `drops a suffix after a pipe or en dash`() {
        assertEquals("Markets rally", stripSourceSuffix("Markets rally | The Wall Street Journal", null))
        assertEquals("Markets rally", stripSourceSuffix("Markets rally – Reuters", null))
    }

    @Test
    fun `keeps a trailing clause that is part of the headline`() {
        val title = "Apple unveils M5 - here's what's new"
        assertEquals(title, stripSourceSuffix(title, "The Verge"))
    }

    @Test
    fun `keeps hyphenated words and titles without a suffix`() {
        assertEquals("Self-driving cars arrive", stripSourceSuffix("Self-driving cars arrive", "Wired"))
        assertEquals("Plain headline", stripSourceSuffix("Plain headline", "CNN"))
    }

    @Test
    fun `only the last separator counts`() {
        assertEquals(
            "Live updates - day two of the summit",
            stripSourceSuffix("Live updates - day two of the summit - BBC News", "BBC News")
        )
    }
}
