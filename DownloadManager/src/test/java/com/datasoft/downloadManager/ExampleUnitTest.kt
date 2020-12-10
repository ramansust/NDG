package com.datasoft.downloadManager

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun parseDeepLink() {
        val deepLink =
                "intent://mapinxt1.thawani.om?014754375~033b8e3c-649f-4b94-a0f2-4d67414b4d57.jpeg02182020040323470093350307500.0000409C55E641C00506Kuhl  0612Product 93350760https://mapinxt1.thawani.om/ECommerce/frmPGResponsePage.aspx0809C55E641C00968https://mapinxt1.thawani.om/ECommerce/GuestUser.aspx?REFID=C55E641C01034https://www.kuhl.store/user-wallet11049925#Intent;scheme=myapp;package=com.agstransacat.thawaniwallet;S.browser_fallback_url=https://mapinxt1.thawani.om/ECommerce/GuestUser.aspx;end"
    }
}