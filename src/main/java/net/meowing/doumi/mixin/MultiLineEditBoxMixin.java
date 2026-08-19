package net.meowing.doumi.mixin;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiLineEditBox.class)
public class MultiLineEditBoxMixin {
	@Unique
	private String doumi$preeditText = null;
	@Unique
	private int doumi$preeditPos = -1;
	@Unique
	private String doumi$prevValue = null;
	@Unique
	private int doumi$prevCursor = -1;
	@Unique
	private int doumi$prevSelectCursor = -1;

	@Final
	@Shadow
	private MultilineTextField textField;

	@Inject(method = "preeditUpdated", at = @At("HEAD"))
	private void updatePreedit(PreeditEvent event, CallbackInfoReturnable<Boolean> cir) {
		if (event == null) {
			doumi$preeditText = null;
			return;
		}
		MultilineTextFieldAccessor textFieldAccessor = (MultilineTextFieldAccessor) textField;
		String value = textFieldAccessor.doumi$getValue();
		int cursor = textFieldAccessor.doumi$getCursor();
		int selectCursor = textFieldAccessor.doumi$getSelectCursor();
		int minPos = Math.min(cursor, selectCursor);
		int maxPos = Math.max(cursor, selectCursor);
		String formatted = new StringBuilder(event.fullText()).insert(event.caretPosition(), "§n").insert(0, "§n").append("§r").toString();
		doumi$preeditText = new StringBuilder(value).replace(minPos, maxPos, formatted).toString();
		doumi$preeditPos = event.caretPosition() + minPos + 2;
	}

	@Inject(method = "extractContents", at = @At("HEAD"))
	private void renderHead(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (doumi$preeditText == null) return;
		MultilineTextFieldAccessor textFieldAccessor = (MultilineTextFieldAccessor) textField;
		String value = textFieldAccessor.doumi$getValue();
		int cursor = textFieldAccessor.doumi$getCursor();
		int selectCursor = textFieldAccessor.doumi$getSelectCursor();
		doumi$prevValue = value;
		doumi$prevCursor = cursor;
		doumi$prevSelectCursor = selectCursor;
		textFieldAccessor.doumi$setValue(doumi$preeditText);
		textFieldAccessor.doumi$setCursor(doumi$preeditPos);
		textFieldAccessor.doumi$setSelectCursor(doumi$preeditPos);
		textFieldAccessor.doumi$invokeReflowDisplayLines();
	}

	@Inject(method = "extractContents", at = @At("TAIL"))
	private void renderTail(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (doumi$prevValue == null) return;
		MultilineTextFieldAccessor textFieldAccessor = (MultilineTextFieldAccessor) textField;
		textFieldAccessor.doumi$setValue(doumi$prevValue);
		textFieldAccessor.doumi$setCursor(doumi$prevCursor);
		textFieldAccessor.doumi$setSelectCursor(doumi$prevSelectCursor);
		textFieldAccessor.doumi$invokeReflowDisplayLines();
		doumi$prevValue = null;
	}
}
