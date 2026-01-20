package cn.iannjhclient.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    // 修改标识符：使用minecraft作为命名空间
    private static final Identifier CUSTOM_BACKGROUND = new Identifier("minecraft", "textures/gui/title/gnu-linux-simple-wallpaper.png");

    @Inject(
            method = "render",
            at = @At("HEAD")
    )
    private void onRenderBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // 渲染自定义背景
        context.drawTexture(
                CUSTOM_BACKGROUND,
                0, 0,
                0, 0,
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight(),
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight()
        );
        // 不要调用 ci.cancel()，这样原版的按钮等UI还会继续渲染
    }

    // 可选：完全替换背景渲染
    @Inject(
            method = "renderBackground",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onRenderBackgroundOnly(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        context.drawTexture(
                CUSTOM_BACKGROUND,
                0, 0,
                0, 0,
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight(),
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight()
        );
        ci.cancel(); // 这里可以取消，因为我们完全替换了背景渲染
    }
}