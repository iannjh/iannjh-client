package cn.iannjhclient.mixin;

import cn.iannjhclient.modules.FastPlace;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient_FastPlace {
    @Shadow
    private int itemUseCooldown;

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void onDoItemUse(CallbackInfo ci) {
        if (FastPlace.INSTANCE.getEnabled()) {
            this.itemUseCooldown = 0; // 或者设置为其他较小的值
        }
    }
}
