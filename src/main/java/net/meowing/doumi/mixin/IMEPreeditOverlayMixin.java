package net.meowing.doumi.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.IMEPreeditOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IMEPreeditOverlay.class)
public class IMEPreeditOverlayMixin {
	@ModifyExpressionValue(
		method = "extractRenderState",
		at = @At(
			value = "CONSTANT",
			args = "intValue=9",
			ordinal = 0
		)
	)
	private int cancelShiftDown(int original) {
		return 0;
	}

	@ModifyExpressionValue(
		method = "extractRenderState",
		at = @At(
			value = "CONSTANT",
			args = "intValue=9",
			ordinal = 1
		)
	)
	private int cancelShiftUp(int original) {
		return 0;
	}

	@Inject(
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/blaze3d/platform/TextInputManager;setTextInputArea(IIII)V",
			shift = At.Shift.AFTER
		),
		cancellable = true
	)
	private void renderCancelDraw(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		ci.cancel();
	}
}
