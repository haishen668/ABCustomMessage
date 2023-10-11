package me.albert.abcustommessage.objects;

import me.albert.abcustommessage.ABCustomMessage;
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
        String path = PlaceholderAPI.setPlaceholders(player, this.source);
        if (path.startsWith("url:")) {
            srcImg = downloadImage(path.substring(4));
        } else {
            File imageFolder = new File(ABCustomMessage.getInstance().getDataFolder(), "images");
            File imageFile = new File(imageFolder, path);
            srcImg = ImageIO.read(imageFile);
        }
        if (Math.min(this.width, this.height) > 0)
            srcImg = resizeImage(srcImg, this.width, this.height);
        Graphics2D g2d = srcImg.createGraphics();
        g2d.drawImage(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), null);
        for (SubImage subImage : this.subImages) {
            String url = subImage.url;
            url = PlaceholderAPI.setPlaceholders(player, url);
            BufferedImage image = downloadImage(url);
            int w = subImage.width;
            int h = subImage.height;
            int x = subImage.x;
            int z = subImage.z;
            if (Math.min(w, h) > 0)
                image = resizeImage(image, w, h);
            g2d.drawImage(image, x, z, (ImageObserver) null);
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
            int x = customText.x;
            int z = customText.z;
            Font font = customText.font;
            Color color = customText.color;
            g2d.setFont(font);
            g2d.setColor(color);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawString(text, x, z);
        }
        return srcImg;
    }
}


/* Location:              D:\服务端\[20001]幻梦斗罗\plugins\ABCustomMessage.jar!\me\albert\abcustommessage\objects\CustomImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */