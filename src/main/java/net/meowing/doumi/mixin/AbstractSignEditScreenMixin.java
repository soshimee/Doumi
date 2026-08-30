package net.meowing.doumi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.input.PreeditEvent;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public class AbstractSignEditScreenMixin {
	@Unique
	private String doumi$preeditText = null;
	@Unique
	private int doumi$preeditPos = -1;

	@Final
	@Shadow
	private String[] messages;
	@Shadow
	private int line;
	@Final
	@Shadow
	private TextFieldHelper signField;

	@Inject(method = "preeditUpdated", at = @At("HEAD"))
	private void updatePreedit(PreeditEvent event, CallbackInfoReturnable<Boolean> cir) {
		if (event == null) {
			doumi$preeditText = null;
			return;
		}
		TextFieldHelperAccessor signFieldAccessor = (TextFieldHelperAccessor) signField;
		String message = messages[line];
		int cursorPos = signFieldAccessor.doumi$getCursorPos();
		int selectionPos = signFieldAccessor.doumi$getSelectionPos();
		int minPos = Math.min(cursorPos, selectionPos);
		int maxPos = Math.max(cursorPos, selectionPos);
		StringBuilder before = new StringBuilder();
		String focused = event.blocks().get(event.focusedBlock());
		StringBuilder after = new StringBuilder();
		for (int i = 0; i < event.focusedBlock(); ++i) before.append(event.blocks().get(i));
		for (int i = event.focusedBlock() + 1; i < event.blocks().size(); ++i) after.append(event.blocks().get(i));
		int bonus = 0;
		if (event.caretPosition() > before.length()) bonus += 2;
		if (event.caretPosition() >= before.length() + focused.length()) bonus += 2;
		doumi$preeditText = new StringBuilder(message).replace(minPos, maxPos, before + "§n" + focused + "§r" + after).toString();
		doumi$preeditPos = event.caretPosition() + minPos + bonus;
	}

	@WrapMethod(method = "extractSignText")
	private void render(GuiGraphicsExtractor graphics, Vector2f cursorPosOutput, Operation<Void> original) {
		if (doumi$preeditText == null) {
			original.call(graphics, cursorPosOutput);
			return;
		}
		TextFieldHelperAccessor signFieldAccessor = (TextFieldHelperAccessor) signField;
		String prevMessage = messages[line];
		int prevCursorPos = signFieldAccessor.doumi$getCursorPos();
		int prevSelectionPos = signFieldAccessor.doumi$getSelectionPos();
		messages[line] = doumi$preeditText;
		signFieldAccessor.doumi$setCursorPos(doumi$preeditPos);
		signFieldAccessor.doumi$setSelectionPos(doumi$preeditPos);
		try {
			original.call(graphics, cursorPosOutput);
		} finally {
			messages[line] = prevMessage;
			signFieldAccessor.doumi$setCursorPos(prevCursorPos);
			signFieldAccessor.doumi$setSelectionPos(prevSelectionPos);
		}
	}
}
