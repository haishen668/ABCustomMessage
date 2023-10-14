package me.albert.abcustommessage.utils;

import me.albert.abcustommessage.ABCustomMessage;

import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSplitUtil {
    public static ArrayList<String> TextSpit(String Text) {
        ArrayList<String> SubText = new ArrayList<>();
        //String regex = "&#[0-9A-Fa-f]{6}[^&]*";
        String regex = "(&#[0-9A-Fa-f]{6}|&[0-9A-Fa-f])[^&]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(Text);
        while (matcher.find()) {
            String match = matcher.group();
            SubText.add(match);
        }
        // 如果没有匹配项，返回整个字符串
        if (SubText.isEmpty()) {
            SubText.add(Text);
            return SubText;
        }
        return SubText;
    }

    /**
     * 解析字符串
     *
     * @param s    需要解析的字符串
     * @param mode 解析模式 string 和 color 两个模式
     * @return 解析之后返回的字符串
     */
    public static String GetParseString(String s, String mode) {
        String regex = "&#[0-9A-Fa-f]{6}|&[0-9A-Fa-f]";
        if (mode.equalsIgnoreCase("string")) {
            return s.replaceAll(regex, "");
        } else if (mode.equalsIgnoreCase("color")) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(s);
            while (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    /**
     * 将十六进制颜色码转换成Color对象
     *
     * @param colorCode 十六进制颜色码
     * @return Color 对象
     */
    public static Color ParseColor(String colorCode) {
        if (colorCode != null && colorCode.startsWith("&#")) {
            colorCode = colorCode.replace("&#", ""); // 移除可能包含的 "&#" 符号
            try {
                int intValue = Integer.parseInt(colorCode, 16);
                return new Color(intValue);
            } catch (NumberFormatException e) {
                // 处理无效的颜色码
                ABCustomMessage.getInstance().getLogger().severe("无效的颜色码");
                return null;
            }
        } else if (colorCode != null && colorCode.startsWith("&")) {
            switch (colorCode) {
                case "&0":
                    return Color.black;
                case "&1":
                    return Color.blue;
                case "&2":
                    return Color.green;
                case "&3":
                    return new Color(0, 170, 170);
                case "&4":
                    return Color.red;
                case "&5":
                    return new Color(170, 0, 170);
                case "&6":
                    return new Color(255, 170, 0);
                case "&7":
                    return new Color(170, 170, 170);
                case "&8":
                    return new Color(85, 85, 85);
                case "&9":
                    return new Color(85, 85, 255);
                case "&a":
                    return new Color(85, 255, 85);
                case "&b":
                    return new Color(85, 255, 255);
                case "&c":
                    return new Color(255, 85, 85);
                case "&d":
                    return new Color(255, 85, 255);
                case "&e":
                    return new Color(255, 255, 85);
                case "&f":
                    return new Color(255, 255, 255);
            }
        } else {
            return Color.black;
        }
        ABCustomMessage.getInstance().getLogger().severe("无效的颜色码");
        return null;
    }

    //得到该字体字符串的长度
//    public static int getStringWidth(Font font, String content) {
//        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
//        int width = 0;
//        for (int i = 0; i < content.length(); i++) {
//            width += metrics.charWidth(content.charAt(i));
//        }
//        return width;
//    }
    public static int getStringWidth(Font font, String content) {
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        return metrics.stringWidth(content);
    }

}
