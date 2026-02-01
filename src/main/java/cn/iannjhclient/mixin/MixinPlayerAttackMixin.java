package cn.iannjhclient.mixin;

import cn.iannjhclient.modules.KeepSprint;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerAttackMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    public void onAttack(CallbackInfo ci) {
        if (KeepSprint.INSTANCE.getEnabled()) {
            PlayerEntity player = (PlayerEntity)(Object)this;
            player.setSprinting(true);
        }
    }
}
