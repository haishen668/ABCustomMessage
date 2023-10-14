package me.albert.abcustommessage.objects;

import ltd.dreamcraft.www.pokemonbag.Utils.ParsePokemon;
import me.albert.abcustommessage.ABCustomMessage;
import me.albert.abcustommessage.utils.TextSplitUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CustomImage {
    public final String id;

    public final String source;

    public final int width;

    public final int height;

    public final List<SubImage> subImages;

    public final List<CustomText> customTexts;

    public CustomImage(String id, String source, int width, int height, List<SubImage> subImages, List<CustomText> customTexts) {
        this.id = id;
        this.source = source;
        this.width = width;
        this.height = height;
        this.subImages = subImages;
        this.customTexts = customTexts;
    }

    public static BufferedImage downloadImage(String link) {
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(20000);
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0");
            connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            connection.addRequestProperty("Pragma", "no-cache");
            InputStream in = connection.getInputStream();
            BufferedImage image = ImageIO.read(in);
            in.close();
            return image;
        } catch (Exception e) {
            if (ABCustomMessage.getInstance().getConfig().getBoolean("debug"))
                e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, 16);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, 1);
        Graphics2D g = outputImage.createGraphics();
        outputImage = g.getDeviceConfiguration().createCompatibleImage(targetWidth, targetHeight, 3);
        g = outputImage.createGraphics();
        g.drawImage(resultingImage, 0, 0, (ImageObserver) null);
        return outputImage;
    }

    public BufferedImage renderImage(OfflinePlayer player) throws Exception {
        BufferedImage srcImg;
        //this.source是底图的位置
        String path = PlaceholderAPI.setPlaceholders(player, this.source);
        if (path.startsWith("[url]")) {
            //如果以[url]开头就下载这个链接地址的图片
            srcImg = downloadImage(path.substring(5));
        } else {
            File imageFolder = new File(ABCustomMessage.getInstance().getDataFolder(), "images");
            File imageFile = new File(imageFolder, path);
            srcImg = ImageIO.read(imageFile);
        }
        //如果给定了宽高就重新设置宽高的大小
        if (Math.min(this.width, this.height) > 0)
            srcImg = resizeImage(srcImg, this.width, this.height);
        Graphics2D g2d = srcImg.createGraphics();
        //渲染图片底图
        g2d.drawImage(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), null);
        for (SubImage subImage : this.subImages) {
            BufferedImage image;
            String subImgPath = subImage.path;
            subImgPath = PlaceholderAPI.setPlaceholders(player, subImgPath);
            //如果子图片的path以[url]开头，那么就读取网链，如果不是就读取本地的图片
            if (subImgPath.startsWith("[url]")) {
                image = downloadImage(subImgPath.substring(5));
            } else if (subImgPath.startsWith("[pokemon]")) {
                //在pokemonbag插件中写一个方法，返回一个image对象
                String imageFolder = String.valueOf(ABCustomMessage.getInstance().getDataFolder());
                image = ParsePokemon.pokemonToImg(imageFolder, subImgPath, player);
            } else {
                File imageFolder = new File(ABCustomMessage.getInstance().getDataFolder(), "images");
                File imageFile = new File(imageFolder, subImgPath);
                image = ImageIO.read(imageFile);
            }
            int w = subImage.width;
            int h = subImage.height;
            int x = subImage.x;
            int z = subImage.z;
            if (image != null) {
                if (Math.min(w, h) > 0)
                    image = resizeImage(image, w, h);
                g2d.drawImage(image, x, z, (ImageObserver) null);
            }
        }
        for (CustomText customText : this.customTexts) {
            String text = customText.text;
            try {
                text = PlaceholderAPI.setPlaceholders(player, text);
            } catch (IllegalStateException e) {
                if (ABCustomMessage.getInstance().getConfig().getBoolean("debug")) {
                    e.printStackTrace();
                } else {
                    ABCustomMessage.getInstance().getLogger().warning("字符串: " + text + "中的变量无法正常解析,可能变量无法异步获取,请打开debug查看详细信息");
                }
            }
            if (text.contains("[pokemon]")) {
                text = ParsePokemon.pokemonToString(text, player);
            }
            int x = customText.x;
            int z = customText.z;
            Font font = customText.font;
            //使用字符串切割工具切割
            ArrayList<String> SubString = TextSplitUtil.TextSpit(text);
            //被解析过的字符串【删除了颜色符号】
            ArrayList<String> ParseSubText = new ArrayList<>();
            if (SubString.isEmpty()) {
                //颜色提取
                Color color = TextSplitUtil.ParseColor(TextSplitUtil.GetParseString(text, "color"));
                g2d.setFont(font);
                g2d.setColor(color);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                //TextSplitUtil.GetParseString(text, "string") 文字提取
                //渲染字符串
                g2d.drawString(TextSplitUtil.GetParseString(text, "string"), x, z);
            } else {
                int TotalSubTextWidth = 0;
                //字符串拼接 由子字符串的数量决定拼接几次
                for (int i = 0; i < SubString.size(); i++) {
                    //获取字符串内容
                    String SubText = SubString.get(i);
                    Color color = TextSplitUtil.ParseColor(TextSplitUtil.GetParseString(SubText, "color"));
//                    for (int j = ParseSubText.size() - 1 ; j >= 0 ; j-- ){
//                        TotalSubTextWidth = TotalSubTextWidth + TextSplitUtil.getStringWidth(font,ParseSubText.get(j));
//                    }
                    if (i - 1 >= 0)
                        TotalSubTextWidth += TextSplitUtil.getStringWidth(font, ParseSubText.get(i - 1));
                    ParseSubText.add(TextSplitUtil.GetParseString(SubText, "string"));
//                    x += TotalSubTextWidth;
                    System.out.println("SubText=" + SubText + "i=" + i + "x=" + x);
                    g2d.setFont(font);
                    g2d.setColor(color);
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.drawString(ParseSubText.get(i), x + TotalSubTextWidth, z);
                }
            }
        }
        return srcImg;
    }
}


/* Location:              D:\服务端\[20001]幻梦斗罗\plugins\ABCustomMessage.jar!\me\albert\abcustommessage\objects\CustomImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */