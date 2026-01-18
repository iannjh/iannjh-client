package cn.iannjhclient.mixin;

import cn.iannjhclient.api.ChatInputAccessor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatScreen.class)
public abstract class ChatInputMixin implements ChatInputAccessor {
    @Shadow protected TextFieldWidget chatField;
    
    @Override
    public TextFieldWidget getChatField() {
        return this.chatField;
    }
}
