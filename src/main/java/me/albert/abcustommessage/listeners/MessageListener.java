package me.albert.abcustommessage.listeners;

import me.albert.abcustommessage.ABCustomMessage;
import me.albert.abcustommessage.objects.CustomMessage;
import me.albert.abcustommessage.utils.MessageUtil;
import me.albert.amazingbot.bot.Bot;
import me.albert.amazingbot.events.message.GroupMessageEvent;
import me.albert.amazingbot.events.notice.group.GroupMemberDecreaseEvent;
import me.albert.amazingbot.events.notice.group.GroupMemberIncreaseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener implements Listener {
    @EventHandler
    public void onGroupMemberIncrease(GroupMemberIncreaseEvent event) {
        for (CustomMessage customMessage : ABCustomMessage.customMessageList) {
            if ((customMessage.groups.isEmpty() || customMessage.groups.contains(Long.valueOf(event.getGroupID()))) &&
                    customMessage.trigger.equalsIgnoreCase("[join]")) {
                UUID uuid = Bot.getApi().getPlayer(Long.valueOf(event.getUserID()));
                if (customMessage.unbind_messages.isEmpty() || uuid != null) {
                    MessageUtil.sendMessage(customMessage.responses, Long.valueOf(event.getGroupID()), Long.valueOf(event.getUserID()), "", "");
                    continue;
                }
                MessageUtil.sendMessage(customMessage.unbind_messages, Long.valueOf(event.getGroupID()), Long.valueOf(event.getUserID()), "", "");
            }
        }
    }

    @EventHandler
    public void onGroupMemberDecrease(GroupMemberDecreaseEvent event) {
        for (CustomMessage customMessage : ABCustomMessage.customMessageList) {
            if ((customMessage.groups.isEmpty() || customMessage.groups.contains(Long.valueOf(event.getGroupID()))) &&
                    customMessage.trigger.equalsIgnoreCase("[leave]")) {
                UUID uuid = Bot.getApi().getPlayer(Long.valueOf(event.getUserID()));
                if (customMessage.unbind_messages.isEmpty() || uuid != null) {
                    MessageUtil.sendMessage(customMessage.responses, Long.valueOf(event.getGroupID()), Long.valueOf(event.getUserID()), "", "");
                    continue;
                }
                MessageUtil.sendMessage(customMessage.unbind_messages, Long.valueOf(event.getGroupID()), Long.valueOf(event.getUserID()), "", "");
            }
        }
    }

    @EventHandler
    public void onMsg(GroupMessageEvent event) {
        if (ABCustomMessage.LOG)
            ABCustomMessage.getInstance().getLogger().info("§a群[" + event.getGroupID() + "]: §b" + event.getMsg());
        for (CustomMessage customMessage : ABCustomMessage.customMessageList) {
            if (customMessage.groups.isEmpty() || customMessage.groups.contains(event.getGroupID())) {
                boolean regex = false;
                if (customMessage.trigger.startsWith("[regex]")) {
                    String pattern = customMessage.trigger.replace("[regex]", "").trim();
                    Matcher matcher = Pattern.compile(pattern).matcher(event.getTextMessage());
                    if (matcher.find())
                        regex = true;
                }
                if (event.getTextMessage().equalsIgnoreCase(customMessage.trigger) || regex || (customMessage.trigger
                        .contains("{extra}") && event.getTextMessage().startsWith(customMessage.trigger.replace("{extra}", "")))) {
                    UUID uuid = Bot.getApi().getPlayer(event.getUserID());
                    String extra = !regex ? event.getTextMessage().substring(customMessage.trigger.replace("{extra}", "").length() - 1) : "";
                    if (customMessage.unbind_messages.isEmpty() || uuid != null) {
                        MessageUtil.sendMessage(customMessage.responses, event.getGroupID(), event.getUserID(), event.getSender().getName(), extra);
                        continue;
                    }
                    MessageUtil.sendMessage(customMessage.unbind_messages, event.getGroupID(), event.getUserID(), event.getSender().getName(), extra);
                }
            }
        }
    }
}


/* Location:              D:\服务端\[20001]幻梦斗罗\plugins\ABCustomMessage.jar!\me\albert\abcustommessage\listeners\MessageListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */