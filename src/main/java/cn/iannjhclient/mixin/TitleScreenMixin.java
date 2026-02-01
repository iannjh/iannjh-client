
package cn.iannjhclient.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // 获取TextRenderer和窗口宽度
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int windowWidth = context.getScaledWindowWidth();

        // 计算文本位置（右上角）
        int x = windowWidth - textRenderer.getWidth("iannjh-client by gnuiannjh") - 5;
        int y = 5;

        // 绘制黄色文本 "iannjh-client"
        context.drawText(textRenderer, "iannjh-client", x, y, 0xFFFF00, true);

        // 绘制白色文本 " by gnuiannjh"
        int yellowTextWidth = textRenderer.getWidth("iannjh-client");
        context.drawText(textRenderer, " by gnuiannjh", x + yellowTextWidth, y, 0xFFFFFF, true);
    }
}
