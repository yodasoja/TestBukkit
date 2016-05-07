/*
Project: Mutant Generation Testing
Authors: Zachary Terlizzese and Michael Arnold
Date: 4/30/2016
*/


//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.CoalType;
import org.bukkit.Color;
import org.bukkit.CropState;
import org.bukkit.Difficulty;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.GrassSpecies;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.TreeSpecies;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.material.Colorable;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.Vector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

//@RunWith(Parameterized.class)
public class TestAll {
	
	//Skipped:
	//Bukkit Mirror
	//Dye Color
	//Location
	//Test Server
	
    //Art
    
    @Test(expected = IllegalArgumentException.class)
    public void getByNullName() {
        Art.getByName(null);
    }

    @Test
    public void getByIdArt() {
        for (Art art : Art.values()) {
            assertThat(Art.getById(art.getId()), is(art));
        }
    }

    @Test
    public void getByNameArt() {
        for (Art art : Art.values()) {
            assertThat(Art.getByName(art.toString()), is(art));
        }
    }

    @Test
    public void dimensionSanityCheck() {
        for (Art art : Art.values()) {
            assertThat(art.getBlockHeight(), is(greaterThan(0)));
            assertThat(art.getBlockWidth(), is(greaterThan(0)));
        }
    }

    @Test
    public void getByNameWithMixedCase() {
        Art subject = Art.values()[0];
        String name = subject.toString().replace('E', 'e');

        assertThat(Art.getByName(name), is(subject));
    }
    
    
    //Bukkit Mirror
    /*
    @Parameters(name="{index}: {1}")
    public static List<Object[]> dataBukkitMirror() {
        return Lists.transform(Arrays.asList(Server.class.getDeclaredMethods()), new Function<Method, Object[]>() {
            @Override
            public Object[] apply(Method input) {
                return new Object[] {
                    input,
                    input.toGenericString().substring("public abstract ".length()).replace("(", "{").replace(")", "}")
                    };
            }
        });
    }

    @Parameter(0)
    public Method server;

    @Parameter(1)
    public String name;

    private Method bukkit;

    @Before
    public void makeBukkit() throws Throwable {
        bukkit = Bukkit.class.getDeclaredMethod(server.getName(), server.getParameterTypes());
    }

    @Test
    public void isStatic() throws Throwable {
        assertThat(Modifier.isStatic(bukkit.getModifiers()), is(true));
    }

    @Test
    public void isDeprecated() throws Throwable {
        assertThat(bukkit.isAnnotationPresent(Deprecated.class), is(server.isAnnotationPresent(Deprecated.class)));
    }

    @Test
    public void returnType() throws Throwable {
        assertThat(bukkit.getReturnType(), is((Object) server.getReturnType()));
        assertThat(bukkit.getGenericReturnType(), is(server.getGenericReturnType()));
    }

    @Test
    public void parameterTypes() throws Throwable {
        assertThat(bukkit.getGenericParameterTypes(), is(server.getGenericParameterTypes()));
    }

    @Test
    public void declaredException() throws Throwable {
        assertThat(bukkit.getGenericExceptionTypes(), is(server.getGenericExceptionTypes()));
    }
    */
    
    //Chat Color
    
    @Test
    public void getByChar() {
        for (ChatColor color : ChatColor.values()) {
            assertThat(ChatColor.getByChar(color.getChar()), is(color));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByStringWithNull() {
        ChatColor.getByChar((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByStringWithEmpty() {
        ChatColor.getByChar("");
    }

    @Test
    public void getByNull() {
        assertThat(ChatColor.stripColor(null), is(nullValue()));
    }

    @Test
    public void getByString() {
        for (ChatColor color : ChatColor.values()) {
            assertThat(ChatColor.getByChar(String.valueOf(color.getChar())), is(color));
        }
    }

    @Test
    public void stripColorOnNullString() {
        assertThat(ChatColor.stripColor(null), is(nullValue()));
    }

    @Test
    public void stripColor() {
        StringBuilder subject = new StringBuilder();
        StringBuilder expected = new StringBuilder();

        final String filler = "test";
        for (ChatColor color : ChatColor.values()) {
            subject.append(color).append(filler);
            expected.append(filler);
        }

        assertThat(ChatColor.stripColor(subject.toString()), is(expected.toString()));
    }

    @Test
    public void toStringWorks() {
        for (ChatColor color : ChatColor.values()) {
            assertThat(String.format("%c%c", ChatColor.COLOR_CHAR, color.getChar()), is(color.toString()));
        }
    }
    
    @Test
    public void translateAlternateColorCodes() {
        String s = "&0&1&2&3&4&5&6&7&8&9&A&a&B&b&C&c&D&d&E&e&F&f&K&k & more";
        String t = ChatColor.translateAlternateColorCodes('&', s);
        String u = ChatColor.BLACK.toString() + ChatColor.DARK_BLUE + ChatColor.DARK_GREEN + ChatColor.DARK_AQUA + ChatColor.DARK_RED + ChatColor.DARK_PURPLE + ChatColor.GOLD + ChatColor.GRAY + ChatColor.DARK_GRAY + ChatColor.BLUE + ChatColor.GREEN + ChatColor.GREEN + ChatColor.AQUA + ChatColor.AQUA + ChatColor.RED + ChatColor.RED + ChatColor.LIGHT_PURPLE + ChatColor.LIGHT_PURPLE + ChatColor.YELLOW + ChatColor.YELLOW + ChatColor.WHITE + ChatColor.WHITE + ChatColor.MAGIC + ChatColor.MAGIC + " & more";
        assertThat(t, is(u));
    }

    @Test
    public void getChatColors() {
        String s = String.format("%c%ctest%c%ctest%c", ChatColor.COLOR_CHAR, ChatColor.RED.getChar(), ChatColor.COLOR_CHAR, ChatColor.ITALIC.getChar(), ChatColor.COLOR_CHAR);
        String expected = ChatColor.RED.toString() + ChatColor.ITALIC;
        assertThat(ChatColor.getLastColors(s), is(expected));

        s = String.format("%c%ctest%c%ctest", ChatColor.COLOR_CHAR, ChatColor.RED.getChar(), ChatColor.COLOR_CHAR, ChatColor.BLUE.getChar());
        assertThat(ChatColor.getLastColors(s), is(ChatColor.BLUE.toString()));
    }
    
    
    //Chat Paginator
    
    @Test
    public void testWordWrap1() {
        String rawString = ChatColor.RED + "123456789 123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);
        
        assertThat(lines.length, is(2));
        assertThat(lines[0], is(ChatColor.RED + "123456789 123456789"));
        assertThat(lines[1], is(ChatColor.RED.toString() + "123456789"));
    }

    @Test
    public void testWordWrap2() {
        String rawString = "123456789 123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 22);

        assertThat(lines.length, is(2));
        assertThat(lines[0], is(ChatColor.WHITE.toString() + "123456789 123456789"));
        assertThat(lines[1], is(ChatColor.WHITE.toString() + "123456789"));
    }

    @Test
    public void testWordWrap3() {
        String rawString = ChatColor.RED + "123456789 " + ChatColor.RED + ChatColor.RED + "123456789 " + ChatColor.RED + "123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 16);

        assertThat(lines.length, is(3));
        assertThat(lines[0], is(ChatColor.RED + "123456789"));
        assertThat(lines[1], is(ChatColor.RED.toString() + ChatColor.RED + "123456789"));
        assertThat(lines[2], is(ChatColor.RED + "123456789"));
    }

    @Test
    public void testWordWrap4() {
        String rawString = "123456789 123456789 123456789 12345";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);

        assertThat(lines.length, is(2));
        assertThat(lines[0], is(ChatColor.WHITE.toString() + "123456789 123456789"));
        assertThat(lines[1], is(ChatColor.WHITE.toString() + "123456789 12345"));
    }

    @Test
    public void testWordWrap5() {
        String rawString = "123456789\n123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);

        assertThat(lines.length, is(2));
        assertThat(lines[0], is(ChatColor.WHITE.toString() + "123456789"));
        assertThat(lines[1], is(ChatColor.WHITE.toString() + "123456789 123456789"));
    }

    @Test
    public void testWordWrap6() {
        String rawString = "12345678   23456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);

        assertThat(lines.length, is(2));
        assertThat(lines[0], is(ChatColor.WHITE.toString() + "12345678   23456789"));
        assertThat(lines[1], is(ChatColor.WHITE.toString() + "123456789"));
    }

    @Test
    public void testWordWrap7() {
        String rawString = "12345678   23456789   123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);

        assertThat(lines.length, is(2));
        assertThat(lines[0], is(ChatColor.WHITE.toString() + "12345678   23456789"));
        assertThat(lines[1], is(ChatColor.WHITE.toString() + "123456789"));
    }

    @Test
    public void testWordWrap8() {
        String rawString = "123456789 123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 6);

        assertThat(lines.length, is(6));
        assertThat(lines[0], is(ChatColor.WHITE.toString() + "123456"));
        assertThat(lines[1], is(ChatColor.WHITE.toString() + "789"));
        assertThat(lines[2], is(ChatColor.WHITE.toString() + "123456"));
        assertThat(lines[3], is(ChatColor.WHITE.toString() + "789"));
        assertThat(lines[4], is(ChatColor.WHITE.toString() + "123456"));
        assertThat(lines[5], is(ChatColor.WHITE.toString() + "789"));
    }

    @Test
    public void testWordWrap9() {
        String rawString = "1234 123456789 123456789 123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 6);

        assertThat(lines.length, is(7));
        assertThat(lines[0], is(ChatColor.WHITE.toString() + "1234"));
        assertThat(lines[1], is(ChatColor.WHITE.toString() + "123456"));
        assertThat(lines[2], is(ChatColor.WHITE.toString() + "789"));
        assertThat(lines[3], is(ChatColor.WHITE.toString() + "123456"));
        assertThat(lines[4], is(ChatColor.WHITE.toString() + "789"));
        assertThat(lines[5], is(ChatColor.WHITE.toString() + "123456"));
        assertThat(lines[6], is(ChatColor.WHITE.toString() + "789"));
    }

    @Test
    public void testWordWrap10() {
        String rawString = "123456789\n123456789";
        String[] lines = ChatPaginator.wordWrap(rawString, 19);

        assertThat(lines.length, is(2));
        assertThat(lines[0], is(ChatColor.WHITE.toString() + "123456789"));
        assertThat(lines[1], is(ChatColor.WHITE.toString() + "123456789"));
    }

    @Test
    public void testWordWrap11() {
        String rawString = ChatColor.RED + "a a a " + ChatColor.BLUE + "a a";
        String[] lines = ChatPaginator.wordWrap(rawString, 9);

        assertThat(lines.length, is(1));
        assertThat(lines[0], is(ChatColor.RED + "a a a " + ChatColor.BLUE + "a a"));
    }
    
    @Test
    public void testPaginate1() {
        String rawString = "1234 123456789 123456789 123456789";
        ChatPaginator.ChatPage page = ChatPaginator.paginate(rawString, 1, 6, 2);
        
        assertThat(page.getPageNumber(), is(1));
        assertThat(page.getTotalPages(), is(4));
        assertThat(page.getLines().length, is(2));
        assertThat(page.getLines()[0], is(ChatColor.WHITE.toString() + "1234"));
        assertThat(page.getLines()[1], is(ChatColor.WHITE.toString() + "123456"));
    }

    @Test
    public void testPaginate2() {
        String rawString = "1234 123456789 123456789 123456789";
        ChatPaginator.ChatPage page = ChatPaginator.paginate(rawString, 2, 6, 2);

        assertThat(page.getPageNumber(), is(2));
        assertThat(page.getTotalPages(), is(4));
        assertThat(page.getLines().length, is(2));
        assertThat(page.getLines()[0], is(ChatColor.WHITE.toString() + "789"));
        assertThat(page.getLines()[1], is(ChatColor.WHITE.toString() + "123456"));
    }

    @Test
    public void testPaginate3() {
        String rawString = "1234 123456789 123456789 123456789";
        ChatPaginator.ChatPage page = ChatPaginator.paginate(rawString, 4, 6, 2);

        assertThat(page.getPageNumber(), is(4));
        assertThat(page.getTotalPages(), is(4));
        assertThat(page.getLines().length, is(1));
        assertThat(page.getLines()[0], is(ChatColor.WHITE.toString() + "789"));
    }
    
    
    //Coal Type
    
    @Test
    public void getByDataCoalType() {
        for (CoalType coalType : CoalType.values()) {
            assertThat(CoalType.getByData(coalType.getData()), is(coalType));
        }
    }
    
    
    //Color
    
    static class TestColor {
        static int id = 0;
        final String name;
        final int rgb;
        final int bgr;
        final int r;
        final int g;
        final int b;

        TestColor(int rgb, int bgr, int r, int g, int b) {
            this.rgb = rgb;
            this.bgr = bgr;
            this.r = r;
            this.g = g;
            this.b = b;
            this.name = id + ":" + Integer.toHexString(rgb).toUpperCase() + "_" + Integer.toHexString(bgr).toUpperCase() + "-r" + Integer.toHexString(r).toUpperCase() + "-g" + Integer.toHexString(g).toUpperCase() + "-b" + Integer.toHexString(b).toUpperCase();
        }
    }

    static TestColor[] examples = new TestColor[] {
        /*            0xRRGGBB, 0xBBGGRR, 0xRR, 0xGG, 0xBB */
        new TestColor(0xFFFFFF, 0xFFFFFF, 0xFF, 0xFF, 0xFF),
        new TestColor(0xFFFFAA, 0xAAFFFF, 0xFF, 0xFF, 0xAA),
        new TestColor(0xFF00FF, 0xFF00FF, 0xFF, 0x00, 0xFF),
        new TestColor(0x67FF22, 0x22FF67, 0x67, 0xFF, 0x22),
        new TestColor(0x000000, 0x000000, 0x00, 0x00, 0x00)
    };

    @Test
    public void testSerialization() throws Throwable {
        for (TestColor testColor : examples) {
            Color base = Color.fromRGB(testColor.rgb);

            YamlConfiguration toSerialize = new YamlConfiguration();
            toSerialize.set("color", base);
            String serialized = toSerialize.saveToString();

            YamlConfiguration deserialized = new YamlConfiguration();
            deserialized.loadFromString(serialized);

            assertThat(testColor.name + " on " + serialized, base, is(deserialized.getColor("color")));
        }
    }

    // Equality tests
    @Test
    public void testEqualities() {
        for (TestColor testColor : examples) {
            Color fromRGB = Color.fromRGB(testColor.rgb);
            Color fromBGR = Color.fromBGR(testColor.bgr);
            Color fromRGBs = Color.fromRGB(testColor.r, testColor.g, testColor.b);
            Color fromBGRs = Color.fromBGR(testColor.b, testColor.g, testColor.r);

            assertThat(testColor.name, fromRGB, is(fromRGBs));
            assertThat(testColor.name, fromRGB, is(fromBGR));
            assertThat(testColor.name, fromRGB, is(fromBGRs));
            assertThat(testColor.name, fromRGBs, is(fromBGR));
            assertThat(testColor.name, fromRGBs, is(fromBGRs));
            assertThat(testColor.name, fromBGR, is(fromBGRs));
        }
    }

    @Test
    public void testInequalities() {
        for (int i = 1; i < examples.length; i++) {
            TestColor testFrom = examples[i];
            Color from = Color.fromRGB(testFrom.rgb);
            for (int j = i - 1; j >= 0; j--) {
                TestColor testTo = examples[j];
                Color to = Color.fromRGB(testTo.rgb);
                String name = testFrom.name + " to " + testTo.name;
                assertThat(name, from, is(not(to)));

                Color transform = from.setRed(testTo.r).setBlue(testTo.b).setGreen(testTo.g);
                assertThat(name, transform, is(not(sameInstance(from))));
                assertThat(name, transform, is(to));
            }
        }
    }

    // RGB tests
    @Test
    public void testRGB() {
        for (TestColor testColor : examples) {
            assertThat(testColor.name, Color.fromRGB(testColor.rgb).asRGB(), is(testColor.rgb));
            assertThat(testColor.name, Color.fromBGR(testColor.bgr).asRGB(), is(testColor.rgb));
            assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).asRGB(), is(testColor.rgb));
            assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).asRGB(), is(testColor.rgb));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidRGB1() {
        Color.fromRGB(0x01000000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidRGB2() {
        Color.fromRGB(Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidRGB3() {
        Color.fromRGB(Integer.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidRGB4() {
        Color.fromRGB(-1);
    }

    // BGR tests
    @Test
    public void testBGR() {
        for (TestColor testColor : examples) {
            assertThat(testColor.name, Color.fromRGB(testColor.rgb).asBGR(), is(testColor.bgr));
            assertThat(testColor.name, Color.fromBGR(testColor.bgr).asBGR(), is(testColor.bgr));
            assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).asBGR(), is(testColor.bgr));
            assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).asBGR(), is(testColor.bgr));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidBGR1() {
        Color.fromBGR(0x01000000);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidBGR2() {
        Color.fromBGR(Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidBGR3() {
        Color.fromBGR(Integer.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidBGR4() {
        Color.fromBGR(-1);
    }

    // Red tests
    @Test
    public void testRed() {
        for (TestColor testColor : examples) {
            assertThat(testColor.name, Color.fromRGB(testColor.rgb).getRed(), is(testColor.r));
            assertThat(testColor.name, Color.fromBGR(testColor.bgr).getRed(), is(testColor.r));
            assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).getRed(), is(testColor.r));
            assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).getRed(), is(testColor.r));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR01() {
        Color.fromRGB(-1, 0x00, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR02() {
        Color.fromRGB(Integer.MAX_VALUE, 0x00, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR03() {
        Color.fromRGB(Integer.MIN_VALUE, 0x00, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR04() {
        Color.fromRGB(0x100, 0x00, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR05() {
        Color.fromBGR(0x00, 0x00, -1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR06() {
        Color.fromBGR(0x00, 0x00, Integer.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR07() {
        Color.fromBGR(0x00, 0x00, Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR08() {
        Color.fromBGR(0x00, 0x00, 0x100);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR09() {
        Color.WHITE.setRed(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR10() {
        Color.WHITE.setRed(Integer.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR11() {
        Color.WHITE.setRed(Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidR12() {
        Color.WHITE.setRed(0x100);
    }

    // Blue tests
    @Test
    public void testBlue() {
        for (TestColor testColor : examples) {
            assertThat(testColor.name, Color.fromRGB(testColor.rgb).getBlue(), is(testColor.b));
            assertThat(testColor.name, Color.fromBGR(testColor.bgr).getBlue(), is(testColor.b));
            assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).getBlue(), is(testColor.b));
            assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).getBlue(), is(testColor.b));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB01() {
        Color.fromRGB(0x00, 0x00, -1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB02() {
        Color.fromRGB(0x00, 0x00, Integer.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB03() {
        Color.fromRGB(0x00, 0x00, Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB04() {
        Color.fromRGB(0x00, 0x00, 0x100);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB05() {
        Color.fromBGR(-1, 0x00, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB06() {
        Color.fromBGR(Integer.MAX_VALUE, 0x00, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB07() {
        Color.fromBGR(Integer.MIN_VALUE, 0x00, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB08() {
        Color.fromBGR(0x100, 0x00, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB09() {
        Color.WHITE.setBlue(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB10() {
        Color.WHITE.setBlue(Integer.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB11() {
        Color.WHITE.setBlue(Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidB12() {
        Color.WHITE.setBlue(0x100);
    }

    // Green tests
    @Test
    public void testGreen() {
        for (TestColor testColor : examples) {
            assertThat(testColor.name, Color.fromRGB(testColor.rgb).getGreen(), is(testColor.g));
            assertThat(testColor.name, Color.fromBGR(testColor.bgr).getGreen(), is(testColor.g));
            assertThat(testColor.name, Color.fromRGB(testColor.r, testColor.g, testColor.b).getGreen(), is(testColor.g));
            assertThat(testColor.name, Color.fromBGR(testColor.b, testColor.g, testColor.r).getGreen(), is(testColor.g));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG01() {
        Color.fromRGB(0x00, -1, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG02() {
        Color.fromRGB(0x00, Integer.MAX_VALUE, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG03() {
        Color.fromRGB(0x00, Integer.MIN_VALUE, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG04() {
        Color.fromRGB(0x00, 0x100, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG05() {
        Color.fromBGR(0x00, -1, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG06() {
        Color.fromBGR(0x00, Integer.MAX_VALUE, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG07() {
        Color.fromBGR(0x00, Integer.MIN_VALUE, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG08() {
        Color.fromBGR(0x00, 0x100, 0x00);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG09() {
        Color.WHITE.setGreen(-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG10() {
        Color.WHITE.setGreen(Integer.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG11() {
        Color.WHITE.setGreen(Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidG12() {
        Color.WHITE.setGreen(0x100);
    }
    
    
    //CropState
    
    @Test
    public void getByDataCropState() {
        for (CropState cropState : CropState.values()) {
            assertThat(CropState.getByData(cropState.getData()), is(cropState));
        }
    }
    
    
    //Diffculty
	
    @Test
    public void getByValue() {
        for (Difficulty difficulty : Difficulty.values()) {
            assertThat(Difficulty.getByValue(difficulty.getValue()), is(difficulty));
        }
    }
    
    
    //Dye Color
    /*
    @Parameters(name= "{index}: {0}")
    public static List<Object[]> dataDyeColor() {
        List<Object[]> list = new ArrayList<Object[]>();
        for (DyeColor dye : DyeColor.values()) {
            list.add(new Object[] {dye});
        }
        return list;
    }

    @Parameter public DyeColor dye;

    @Test
    @SuppressWarnings("deprecation")
    public void getByData() {
        byte data = dye.getData();

        DyeColor byData = DyeColor.getByData(data);
        assertThat(byData, is(dye));
    }

    @Test
    public void getByWoolData() {
        byte data = dye.getWoolData();

        DyeColor byData = DyeColor.getByWoolData(data);
        assertThat(byData, is(dye));
    }

    @Test
    public void getByDyeData() {
        byte data = dye.getDyeData();

        DyeColor byData = DyeColor.getByDyeData(data);
        assertThat(byData, is(dye));
    }

    @Test
    public void getDyeDyeColor() {
        testColorable(new Dye(Material.INK_SACK, dye.getDyeData()));
    }

    @Test
    public void getWoolDyeColor() {
        testColorable(new Wool(Material.WOOL, dye.getWoolData()));
    }

    private void testColorable(final Colorable colorable) {
        assertThat(colorable.getColor(), is(this.dye));
    }
    */
    
    //Effect Test
    
    @Test
    public void getByIdEffectTest() {
        for (Effect effect : Effect.values()) {
            assertThat(Effect.getById(effect.getId()), is(effect));
        }
    }
    
    
    //Entity Effect
    
    @Test
    public void getByDataEntityEffect() {
        for (EntityEffect entityEffect : EntityEffect.values()) {
            assertThat(EntityEffect.getByData(entityEffect.getData()), is(entityEffect));
        }
    }
    
    
    //Game Mode
    
    @Test
    public void getByValueGameMode() {
        for (GameMode gameMode : GameMode.values()) {
            assertThat(GameMode.getByValue(gameMode.getValue()), is(gameMode));
        }
    }
    
    
    //Grass Species
    
    @Test
    public void getByDataGrassSpecies() {
        for (GrassSpecies grassSpecies : GrassSpecies.values()) {
            assertThat(GrassSpecies.getByData(grassSpecies.getData()), is(grassSpecies));
        }
    }
    
    
    //Instrument
    
    @Test
    public void getByTypeInstrument() {
        for (Instrument instrument : Instrument.values()) {
            assertThat(Instrument.getByType(instrument.getType()), is(instrument));
        }
    }
    
    
    //Location
    /*
    private static final double I_hat_prime = 1.0 / 1000000;
    /**
     * <pre>
     * a² + b² = c², a = b
     * => 2∙(a²) = 2∙(b²) = c², c = 1
     * => 2∙(a²) = 1
     * => a² = 1/2
     * => a = √(1/2) ∎
     * </pre>
     *//*
    private static final double HALF_UNIT = Math.sqrt(1 / 2f);
    /**
     * <pre>
     * a² + b² = c², c = √(1/2)
     * => a² + b² = √(1/2)², a = b
     * => 2∙(a²) = 2∙(b²) = 1/2
     * => a² = 1/4
     * => a = √(1/4) ∎
     * </pre>
     *//*
    private static final double HALF_HALF_UNIT = Math.sqrt(1 / 4f);

    @Parameters(name= "{index}: {0}")
    public static List<Object[]> data() {
        Random RANDOM = new Random(1l); // Test is deterministic
        int r = 0;
        return ImmutableList.<Object[]>of(
            new Object[] { "X",
                1, 0, 0,
                270, 0
            },
            new Object[] { "-X",
                -1, 0, 0,
                90, 0
            },
            new Object[] { "Z",
                0, 0, 1,
                0, 0
            },
            new Object[] { "-Z",
                0, 0, -1,
                180, 0
            },
            new Object[] { "Y",
                0, 1, 0,
                0, -90 // Zero is here as a "default" value
            },
            new Object[] { "-Y",
                0, -1, 0,
                0, 90 // Zero is here as a "default" value
            },
            new Object[] { "X Z",
                HALF_UNIT, 0, HALF_UNIT,
                (270 + 360) / 2, 0
            },
            new Object[] { "X -Z",
                HALF_UNIT, 0, -HALF_UNIT,
                (270 + 180) / 2, 0
            },
            new Object[] { "-X -Z",
                -HALF_UNIT, 0, -HALF_UNIT,
                (90 + 180) / 2, 0
            },
            new Object[] { "-X Z",
                -HALF_UNIT, 0, HALF_UNIT,
                (90 + 0) / 2, 0
            },
            new Object[] { "X Y Z",
                HALF_HALF_UNIT, HALF_UNIT, HALF_HALF_UNIT,
                (270 + 360) / 2, -45
            },
            new Object[] { "-X -Y -Z",
                -HALF_HALF_UNIT, -HALF_UNIT, -HALF_HALF_UNIT,
                (90 + 180) / 2, 45
            },
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++),
            getRandom(RANDOM, r++)
        );
    }

    private static Object[] getRandom(Random random, int index) {
        final double YAW_FACTOR = 360;
        final double YAW_OFFSET = 0;
        final double PITCH_FACTOR = 180;
        final double PITCH_OFFSET = -90;
        final double CARTESIAN_FACTOR = 256;
        final double CARTESIAN_OFFSET = -128;

        Vector vector;
        Location location;
        if (random.nextBoolean()) {
            float pitch = (float) (random.nextDouble() * PITCH_FACTOR + PITCH_OFFSET);
            float yaw = (float) (random.nextDouble() * YAW_FACTOR + YAW_OFFSET);

            location = getEmptyLocation();
            location.setPitch(pitch);
            location.setYaw(yaw);

            vector = location.getDirection();
        } else {
            double x = random.nextDouble() * CARTESIAN_FACTOR + CARTESIAN_OFFSET;
            double y = random.nextDouble() * CARTESIAN_FACTOR + CARTESIAN_OFFSET;
            double z = random.nextDouble() * CARTESIAN_FACTOR + CARTESIAN_OFFSET;

            location = getEmptyLocation();
            vector = new Vector(x, y, z).normalize();

            location.setDirection(vector);
        }

        return new Object[] { "R" + index,
            vector.getX(), vector.getY(), vector.getZ(),
            location.getYaw(), location.getPitch()
        };
    }

    @Parameter(0)
    public String nane;
    @Parameter(1)
    public double x;
    @Parameter(2)
    public double y;
    @Parameter(3)
    public double z;
    @Parameter(4)
    public float yaw;
    @Parameter(5)
    public float pitch;

    @Test
    public void testExpectedPitchYaw() {
        Location location = getEmptyLocation().setDirection(getVector());

        assertThat((double) location.getYaw(), is(closeTo(yaw, I_hat_prime)));
        assertThat((double) location.getPitch(), is(closeTo(pitch, I_hat_prime)));
    }

    @Test
    public void testExpectedXYZ() {
        Vector vector = getLocation().getDirection();

        assertThat(vector.getX(), is(closeTo(x, I_hat_prime)));
        assertThat(vector.getY(), is(closeTo(y, I_hat_prime)));
        assertThat(vector.getZ(), is(closeTo(z, I_hat_prime)));
    }

    private Vector getVector() {
        return new Vector(x, y, z);
    }

    private static Location getEmptyLocation() {
        return new Location(null, 0, 0, 0);
    }

    private Location getLocation() {
        Location location = getEmptyLocation();
        location.setYaw(yaw);
        location.setPitch(pitch);
        return location;
    }
    
    
    //Material
    
    @Test
    public void getByNameMaterial() {
        for (Material material : Material.values()) {
            assertThat(Material.getMaterial(material.toString()), is(material));
        }
    }

    @Test
    public void getById() throws Throwable {
        for (Material material : Material.values()) {
            if (material.getClass().getField(material.name()).getAnnotation(Deprecated.class) != null) {
                continue;
            }
            assertThat(Material.getMaterial(material.getId()), is(material));
        }
    }

    @Test
    public void isBlock() {
        for (Material material : Material.values()) {
            if (material.getId() > 255) continue;

            assertTrue(String.format("[%d] %s", material.getId(), material.toString()), material.isBlock());
        }
    }

    @Test
    public void getByOutOfRangeId() {
        assertThat(Material.getMaterial(Integer.MAX_VALUE), is(nullValue()));
        assertThat(Material.getMaterial(Integer.MIN_VALUE), is(nullValue()));
    }

    @Test
    public void getByNameNull() {
        assertThat(Material.getMaterial(null), is(nullValue()));
    }

    @Test
    public void getData() {
        for (Material material : Material.values()) {
            Class<? extends MaterialData> clazz = material.getData();

            assertThat(material.getNewData((byte) 0), is(instanceOf(clazz)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void matchMaterialByNull() {
        Material.matchMaterial(null);
    }

    @Test
    public void matchMaterialById() throws Throwable {
        for (Material material : Material.values()) {
            if (material.getClass().getField(material.name()).getAnnotation(Deprecated.class) != null) {
                continue;
            }
            assertThat(Material.matchMaterial(String.valueOf(material.getId())), is(material));
        }
    }

    @Test
    public void matchMaterialByName() {
        for (Material material : Material.values()) {
            assertThat(Material.matchMaterial(material.toString()), is(material));
        }
    }

    @Test
    public void matchMaterialByLowerCaseAndSpaces() {
        for (Material material : Material.values()) {
            String name = material.toString().replaceAll("_", " ").toLowerCase();
            assertThat(Material.matchMaterial(name), is(material));
        }
    }
    */
    
    //Note
    
    @Test
    public void getToneByData() {
        for (Note.Tone tone : Note.Tone.values()) {
            assertThat(Note.Tone.getById(tone.getId()), is(tone));
        }
    }

    @Test
    public void verifySharpedData() {
        for (Note.Tone tone : Note.Tone.values()) {
            if (!tone.isSharpable()) return;

            assertFalse(tone.isSharped(tone.getId(false)));
            assertTrue(tone.isSharped(tone.getId(true)));
        }
    }

    @Test
    public void verifyUnknownToneData() {
        Collection<Byte> tones = Lists.newArrayList();
        for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
            tones.add((byte) i);
        }

        for (Note.Tone tone : Note.Tone.values()) {
            if (tone.isSharpable()) tones.remove(tone.getId(true));
            tones.remove(tone.getId());
        }

        for (Byte data : tones) {
            assertThat(Note.Tone.getById(data), is(nullValue()));

            for (Note.Tone tone : Note.Tone.values()) {
                try {
                    tone.isSharped(data);

                    fail(data + " should throw IllegalArgumentException");
                } catch (IllegalArgumentException e) {
                    assertNotNull(e);
                }
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNoteBelowMin() {
        new Note((byte) -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNoteAboveMax() {
        new Note((byte) 25);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNoteOctaveBelowMax() {
        new Note((byte) -1, Note.Tone.A, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNoteOctaveAboveMax() {
        new Note((byte) 3, Note.Tone.A, true);
    }

    @Test
    public void createNoteOctaveNonSharpable() {
        Note note = new Note((byte) 0, Note.Tone.B, true);
        assertFalse(note.isSharped());
        assertThat(note.getTone(), is(Note.Tone.C));
    }

    @Test
    public void createNoteFlat() {
        Note note = Note.flat(0, Note.Tone.D);
        assertTrue(note.isSharped());
        assertThat(note.getTone(), is(Note.Tone.C));
    }

    @Test
    public void createNoteFlatNonFlattenable() {
        Note note = Note.flat(0, Note.Tone.C);
        assertFalse(note.isSharped());
        assertThat(note.getTone(), is(Note.Tone.B));
    }

    @Test
    public void testFlatWrapping() {
        Note note = Note.flat(1, Note.Tone.G);
        assertTrue(note.isSharped());
        assertThat(note.getTone(), is(Note.Tone.F));
    }

    @Test
    public void testFlatWrapping2() {
        Note note = new Note(1, Note.Tone.G, false).flattened();
        assertTrue(note.isSharped());
        assertThat(note.getTone(), is(Note.Tone.F));
    }

    @Test
    public void testSharpWrapping() {
        Note note = new Note(1, Note.Tone.F, false).sharped();
        assertTrue(note.isSharped());
        assertThat(note.getTone(), is(Note.Tone.F));
        assertEquals(note.getOctave(), 2);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSharpWrapping2() {
        new Note(2, Note.Tone.F, true).sharped();
    }

    @Test
    public void testHighest() {
        Note note = new Note(2, Note.Tone.F, true);
        assertEquals(note.getId(), (byte)24);
    }

    @Test
    public void testLowest() {
        Note note = new Note(0, Note.Tone.F, true);
        assertEquals(note.getId(), (byte)0);
    }

    @Test
    public void doo() {
        for (int i = 1; i <= 24; i++) {
            Note note = new Note((byte) i);
            int octave = note.getOctave();
            Note.Tone tone = note.getTone();
            boolean sharped = note.isSharped();

            Note newNote = new Note(octave, tone, sharped);
            assertThat(newNote, is(note));
            assertThat(newNote.getId(), is(note.getId()));
        }
    }
    
    
    //Test Server
    //Omitted
    
    
    //Tree Species
    
    @Test
    public void getByDataTreeSpecies() {
        for (TreeSpecies treeSpecies : TreeSpecies.values()) {
            assertThat(TreeSpecies.getByData(treeSpecies.getData()), is(treeSpecies));
        }
    }
    
    
    //World Type
    
    @Test
    public void getByName() {
        for (WorldType worldType : WorldType.values()) {
            assertThat(WorldType.getByName(worldType.getName()), is(worldType));
        }
    }
}
