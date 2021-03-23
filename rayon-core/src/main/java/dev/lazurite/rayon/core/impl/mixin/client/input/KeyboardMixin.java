package dev.lazurite.rayon.core.impl.mixin.client.input;

import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.BlockRigidBody;
import dev.lazurite.rayon.core.impl.mixin.client.render.DebugRendererMixin;
import dev.lazurite.rayon.core.impl.util.debug.DebugManager;
import dev.lazurite.rayon.core.impl.util.debug.DebugLayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds an additional F3 key combination (F3 + R). When pressed
 * once, it enables rendering of {@link ElementRigidBody} objects.
 * When it's pressed again, it also enabled rendering of
 * {@link BlockRigidBody} objects in a different color.
 * @see DebugRendererMixin
 */
@Mixin(Keyboard.class)
@Environment(EnvType.CLIENT)
public abstract class KeyboardMixin {
    @Shadow protected abstract void debugWarn(String string, Object... objects);

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "processF3", at = @At("RETURN"), cancellable = true)
    private void processF3(int key, CallbackInfoReturnable<Boolean> info) {
        if (key == 'R') {
            DebugLayer layer = DebugManager.getInstance().nextLayer();

            if (DebugManager.getInstance().isEnabled()) {
                debugWarn(layer.getTranslation());
            } else {
                debugWarn("debug.rayon.off");
            }

            info.setReturnValue(true);
        } else if (key == 'Q') {
            ChatHud chatHud = this.client.inGameHud.getChatHud();
            chatHud.addMessage(new TranslatableText("debug.rayon.help"));
        }
    }
}
