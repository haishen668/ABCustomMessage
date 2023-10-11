package me.albert.abcustommessage.objects;

import java.util.List;

public class CustomMessage {
    public final String trigger;

    public final List<String> responses;

    public final List<String> unbind_messages;

    public final List<Long> groups;

    public final String id;

    public CustomMessage(String trigger, List<String> responses, List<String> unbind_messages, List<Long> groups, String id) {
        this.trigger = trigger;
        this.responses = responses;
        this.unbind_messages = unbind_messages;
        this.groups = groups;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public List<Long> getGroups() {
        return this.groups;
    }

    public List<String> getResponses() {
        return this.responses;
    }

    public List<String> getUnbind_messages() {
        return this.unbind_messages;
    }

    public String getTrigger() {
        return this.trigger;
    }
}


/* Location:              D:\服务端\[20001]幻梦斗罗\plugins\ABCustomMessage.jar!\me\albert\abcustommessage\objects\CustomMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */