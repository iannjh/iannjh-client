package cn.iannjhclient.mixin;

import cn.iannjhclient.event.EventBus;
import cn.iannjhclient.event.events.KeyboardEvent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey",at = @At("HEAD"))
    public void key(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if(action == GLFW.GLFW_PRESS) {
            // 右Shift键总是触发事件，即使在YamlEditor中
            if(key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                EventBus.INSTANCE.post(new KeyboardEvent(key));
            }
            // 其他键只在非ChatScreen时触发
            else if(!(MinecraftClient.getInstance().currentScreen instanceof ChatScreen)) {
                EventBus.INSTANCE.post(new KeyboardEvent(key));
            }
        }
    }
}
