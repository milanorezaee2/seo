package com.example

import com.example.data.model.RobotsPreset
import com.example.data.model.SchemaType
import com.example.data.remote.NetworkHelper
import org.junit.Assert.*
import org.junit.Test

class SeoToolkitUnitTest {

    @Test
    fun testDomainExtraction() {
        assertEquals("google.com", NetworkHelper.extractDomain("https://www.google.com/search?q=seo"))
        assertEquals("example.org", NetworkHelper.extractDomain("http://example.org/blog"))
        assertEquals("mysite.ir", NetworkHelper.extractDomain("mysite.ir"))
    }

    @Test
    fun testUrlNormalization() {
        assertEquals("https://google.com", NetworkHelper.normalizeUrl("google.com"))
        assertEquals("https://example.com/page", NetworkHelper.normalizeUrl("https://example.com/page"))
    }

    @Test
    fun testSchemaTypes() {
        assertEquals(9, SchemaType.values().size)
        assertEquals("Article", SchemaType.ARTICLE.jsonType)
        assertEquals("LocalBusiness", SchemaType.LOCAL_BUSINESS.jsonType)
    }

    @Test
    fun testRobotsPresets() {
        assertEquals(6, RobotsPreset.values().size)
        assertTrue(RobotsPreset.WORDPRESS.description.contains("wp-admin"))
    }
}
