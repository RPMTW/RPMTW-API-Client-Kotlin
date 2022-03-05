package com.rpmtw.rpmtw_api_client.utilities

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class UtilitiesTest {
    @Test
    fun markdownToMinecraftFormatting() {
        val formatted =
            Utilities.markdownToMinecraftFormatting("**bold**, *italic*, _alternative italic_, ~~strikethrough~~, __underscore__, \\~\\~test backslash\\~\\~")
        assertEquals(
            formatted,
            "§l§obold§r§o§r, §oitalic§r, §oalternative italic§r, §m~strikethrough§r~, §n§ounderscore§r§o§r, ~~test backslash~~"
        )
    }
}