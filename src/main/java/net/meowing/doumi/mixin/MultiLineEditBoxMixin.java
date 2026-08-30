package net.meowing.doumi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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
	private String doumi$preeditText = null;
	@Unique
	private int doumi$preeditPos = -1;

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
		StringBuilder before = new StringBuilder();
		String focused = event.blocks().get(event.focusedBlock());
		StringBuilder after = new StringBuilder();
		for (int i = 0; i < event.focusedBlock(); ++i) before.append(event.blocks().get(i));
		for (int i = event.focusedBlock() + 1; i < event.blocks().size(); ++i) after.append(event.blocks().get(i));
		int bonus = 0;
		if (event.caretPosition() > before.length()) bonus += 2;
		if (event.caretPosition() >= before.length() + focused.length()) bonus += 2;
		doumi$preeditText = new StringBuilder(value).replace(minPos, maxPos, before + "§n" + focused + "§r" + after).toString();
		doumi$preeditPos = event.caretPosition() + minPos + bonus;
	}

	@WrapMethod(method = "extractContents")
	private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (doumi$preeditText == null) {
			original.call(graphics, mouseX, mouseY, a);
			return;
		}
		MultilineTextFieldAccessor textFieldAccessor = (MultilineTextFieldAccessor) textField;
		String prevValue = textFieldAccessor.doumi$getValue();
		int prevCursor = textFieldAccessor.doumi$getCursor();
		int prevSelectCursor = textFieldAccessor.doumi$getSelectCursor();
		textFieldAccessor.doumi$setValue(doumi$preeditText);
		textFieldAccessor.doumi$setCursor(doumi$preeditPos);
		textFieldAccessor.doumi$setSelectCursor(doumi$preeditPos);
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
