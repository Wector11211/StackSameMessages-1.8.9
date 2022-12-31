package net.labymod.addons.Wector11211.StackSameMessages;

import net.labymod.addons.Wector11211.StackSameMessages.Settings.TextElement;
import net.labymod.addons.Wector11211.StackSameMessages.Settings.UncoloredStringElement;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.MessageModifyChatEvent;
import net.labymod.ingamechat.IngameChatManager;
import net.labymod.ingamechat.renderer.ChatRenderer;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class StackSameMessagesAddon extends LabyModAddon {
    private boolean addonEnabled;
    private String lastMessage = "";
    private int repeatAmount;
    private String repeatCounter;
    private int counterCount = 0;
    private String annoyingMessage = "§cAnnoying repeating message ";
    public String styleToString(String style){
        return " "+style.replace("@", String.valueOf(++repeatAmount)).replace("&", "§");
    }

    @Override
    public void onEnable() {

        getApi().getEventManager().register(new MessageModifyChatEvent() {
            @Override
            public Object onModifyChatMessage(Object o) {
                IChatComponent currentMessage = (IChatComponent) o;
                if(addonEnabled && lastMessage.equals(currentMessage.getFormattedText())) {
                    try {
                        ChatRenderer chat = IngameChatManager.INSTANCE.getMain();
                        chat.getChatLines().remove(0);
                        List newBackendComponents = chat.getBackendComponents();
                        newBackendComponents.remove(0);
                        Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
                        System.out.println(repeatAmount);
                        return currentMessage.appendText(styleToString(repeatCounter));
                    } catch (IndexOutOfBoundsException ignored){}
                }
                repeatAmount = 1;
                lastMessage = currentMessage.getFormattedText();
                return currentMessage;
            }
        });
    }

    @Override
    public void loadConfig() {
        this.addonEnabled = getConfig().has( "enabled" ) ? getConfig().get("enabled").getAsBoolean() : true;
        this.repeatCounter = getConfig().has( "counter_style" ) ? getConfig().get("counter_style").getAsString() : "&r&7[x@]";
    }

    @Override
    protected void fillSettings(List<SettingsElement> options) {

        BooleanElement addonEnabledElement = new BooleanElement(
                "Enabled",
                this,
                new ControlElement.IconData(Material.LEVER),
                "enabled", this.addonEnabled);

        TextElement previewTextElement = new TextElement("", "center");

        UncoloredStringElement counterFormattingElement = new UncoloredStringElement("Counter style", new ControlElement.IconData(Material.BOOK_AND_QUILL), this.repeatCounter, new Consumer<String>() {
            @Override
            public void accept(String accepted) {
                repeatCounter = accepted;
                previewTextElement.setText(annoyingMessage + (counterCount>1 ? repeatCounter.replace("&", "§")
                        .replace("@", String.valueOf(counterCount)) : " "));
                previewTextElement.setText("§cAnnoying repeating message "+repeatCounter.replace("&", "§").replace("@", "2"));
                getConfig().addProperty("counter_style", repeatCounter);
                saveConfig();
            }
        });
        counterFormattingElement.updateField();
        counterFormattingElement.bindDescription("Character \"@\" represents counter number");

        options.add( addonEnabledElement );
        options.add( counterFormattingElement );
        options.add( previewTextElement );
    }
}
