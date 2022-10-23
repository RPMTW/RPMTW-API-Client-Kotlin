package com.rpmtw.rpmtw_api_client.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class UtilitiesTest {
    @Test
    fun markdownToMinecraftFormatting() {
        val formatted =
            Util.markdownToMinecraftFormatting("**bold**, *italic*, _alternative italic_, ~~strikethrough~~, __underscore___, \\~\\~test backslash\\~\\~")
        assertEquals(
            formatted,
            "§lbold§r, §oitalic§r, §oalternative italic§r, §mstrikethrough§r, §n§ounderscore§r§o§n§r_, ~~test backslash~~"
        )
    }
}