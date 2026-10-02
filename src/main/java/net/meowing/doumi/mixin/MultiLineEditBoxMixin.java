package net.meowing.doumi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.meowing.doumi.utils.PreeditInfo;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.input.PreeditEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiLineEditBox.class)
public class MultiLineEditBoxMixin {
	@Unique
	private PreeditInfo doumi$preeditInfo = null;

	@Final
	@Shadow
	private MultilineTextField textField;

	@Inject(method = "preeditUpdated", at = @At("HEAD"))
	private void updatePreedit(PreeditEvent event, CallbackInfoReturnable<Boolean> cir) {
		if (event == null) {
			doumi$preeditInfo = null;
			return;
		}
		MultilineTextFieldAccessor textFieldAccessor = (MultilineTextFieldAccessor) textField;
		String value = textFieldAccessor.doumi$getValue();
		int cursor = textFieldAccessor.doumi$getCursor();
		int selectCursor = textFieldAccessor.doumi$getSelectCursor();
		doumi$preeditInfo = PreeditInfo.fromPreeditEvent(event).applyInlineStyle().applyToText(value, cursor, selectCursor);
	}

	@WrapMethod(method = "extractContents")
	private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (doumi$preeditInfo == null) {
			original.call(graphics, mouseX, mouseY, a);
			return;
		}
		MultilineTextFieldAccessor textFieldAccessor = (MultilineTextFieldAccessor) textField;
		String prevValue = textFieldAccessor.doumi$getValue();
		int prevCursor = textFieldAccessor.doumi$getCursor();
		int prevSelectCursor = textFieldAccessor.doumi$getSelectCursor();
		textFieldAccessor.doumi$setValue(doumi$preeditInfo.text());
		textFieldAccessor.doumi$setCursor(doumi$preeditInfo.pos());
		textFieldAccessor.doumi$setSelectCursor(doumi$preeditInfo.pos());
		textFieldAccessor.doumi$invokeReflowDisplayLines();
		try {
			original.call(graphics, mouseX, mouseY, a);
		} finally {
			textFieldAccessor.doumi$setValue(prevValue);
			textFieldAccessor.doumi$setCursor(prevCursor);
			textFieldAccessor.doumi$setSelectCursor(prevSelectCursor);
			textFieldAccessor.doumi$invokeReflowDisplayLines();
		}
	}
}
