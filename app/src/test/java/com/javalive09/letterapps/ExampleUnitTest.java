package com.javalive09.letterapps;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void pinyin() {
        String s = PinyinUtils.getSpells("你");
        assertEquals(s, "n");
    }

    @Test
    public void isHan() {
        String s = PinyinUtils.getSpells("W");
        assertEquals(s, "no");
    }

    @Test
    public void isNum() {
        String s = PinyinUtils.getSpells("5");
        assertEquals(s, "no");
    }
}