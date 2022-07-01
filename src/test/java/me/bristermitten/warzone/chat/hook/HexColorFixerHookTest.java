package me.bristermitten.warzone.chat.hook;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HexColorFixerHookTest {

    @Test
    void format() {
        final String format = new HexColorFixerHook()
                .format("hello §x§f§f§f§f§f§f world", null);
        System.out.println(format);
    }
}
