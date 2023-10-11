package me.albert.abcustommessage.utils;

import me.albert.abcustommessage.ABCustomMessage;
import me.albert.abcustommessage.objects.CustomImage;
import me.albert.amazingbot.bot.Bot;
import me.albert.amazingbot.listeners.OnCommand;
import me.albert.amazingbot.utils.MsgUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

public class MessageUtil {
    public static String getMsg(List<String> message, OfflinePlayer offlinePlayer, long groupID, Long userID, String nick, String extra) {
        StringBuilder response = new StringBuilder();
        for (String s : message) {
            String name = (offlinePlayer != null) ? offlinePlayer.getName() : nick;
            s = s.replace("{qq}", userID.toString());
            s = s.replace("{nick}", name);
            s = s.replace("{extra}", extra);
            try {
                s = PlaceholderAPI.setPlaceholders(offlinePlayer, s);
            } catch (IllegalStateException e) {
                if (ABCustomMessage.getInstance().getConfig().getBoolean("debug")) {
                    e.printStackTrace();
                } else {
                    ABCustomMessage.getInstance().getLogger().warning("字符串: " + s + "中的变量无法正常解析,可能变量无法异步获取,请打开debug查看详细信息");
                }
            }
            if (s.startsWith("[image]")) {
                String imageID = s.replace("[image]", "").trim();
                try {
                    CustomImage customImage = null;
                    for (CustomImage customImage1 : ABCustomMessage.customImageList) {
                        if (customImage1.id.equalsIgnoreCase(imageID))
                            customImage = customImage1;
                    }
                    if (customImage == null) {
                        response = response.append("无法找到图片ID: ").append(imageID);
                        continue;
                    }
                    BufferedImage image = customImage.renderImage(offlinePlayer);
                    response = response.append(MsgUtil.bufferedImgToMsg(image));
                } catch (Exception e) {
                    if (ABCustomMessage.getInstance().getConfig().getBoolean("debug"))
                        e.printStackTrace();
                }
                continue;
            }
            if (s.startsWith("[command]")) {
                String cmd = s.replace("[command]", "").trim();
                CommandSender commandSender = OnCommand.getSender(groupID, true);
                Bukkit.getScheduler().runTask((Plugin) ABCustomMessage.getInstance(), () -> Bukkit.dispatchCommand(commandSender, cmd));
                continue;
            }
            response = response.append(s);
        }
        return response.toString();
    }

    public static void sendMessage(List<String> message, Long groupID, Long userID, String nick, String extra) {
        OfflinePlayer offlinePlayer = null;
        UUID uuid = Bot.getApi().getPlayer(userID);
        if (uuid != null) {
            offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.getName() == null)
                offlinePlayer = null;
        }
        String response = getMsg(message, offlinePlayer, groupID.longValue(), userID, nick, extra);
        if (!response.isEmpty())
            Bot.getApi().sendGroupMsg(groupID, response, new boolean[0]);
    }
}


/* Location:              D:\服务端\[20001]幻梦斗罗\plugins\ABCustomMessage.jar!\me\albert\abcustommessag\\utils\MessageUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */