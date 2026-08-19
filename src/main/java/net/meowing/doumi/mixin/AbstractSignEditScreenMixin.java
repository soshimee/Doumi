package net.meowing.doumi.mixin;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public class AbstractSignEditScreenMixin {
	@Unique
	private String doumi$preeditText = null;
	@Unique
	private int doumi$preeditPos = -1;
	@Unique
	private String doumi$prevMessage = null;
	@Unique
	private int doumi$prevCursorPos = -1;
	@Unique
	private int doumi$prevSelectionPos = -1;

	@Final
	@Shadow
	private String[] messages;
	@Shadow
	private int line;
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
		String formatted = new StringBuilder(event.fullText()).insert(event.caretPosition(), "§n").insert(0, "§n").append("§r").toString();
		doumi$preeditText = new StringBuilder(message).replace(minPos, maxPos, formatted).toString();
		doumi$preeditPos = event.caretPosition() + minPos + 2;
	}

	@Inject(method = "extractSignText", at = @At("HEAD"))
	private void renderHead(GuiGraphicsExtractor graphics, Vector2f cursorPosOutput, CallbackInfo ci) {
		if (doumi$preeditText == null) return;
		TextFieldHelperAccessor signFieldAccessor = (TextFieldHelperAccessor) signField;
		String message = messages[line];
		int cursorPos = signFieldAccessor.doumi$getCursorPos();
		int selectionPos = signFieldAccessor.doumi$getSelectionPos();
		doumi$prevMessage = message;
		doumi$prevCursorPos = cursorPos;
		doumi$prevSelectionPos = selectionPos;
		messages[line] = doumi$preeditText;
		signFieldAccessor.doumi$setCursorPos(doumi$preeditPos);
		signFieldAccessor.doumi$setSelectionPos(doumi$preeditPos);
	}

	@Inject(method = "extractSignText", at = @At("RETURN"))
	private void renderReturn(GuiGraphicsExtractor graphics, Vector2f cursorPosOutput, CallbackInfo ci) {
		if (doumi$prevMessage == null) return;
		TextFieldHelperAccessor signFieldAccessor = (TextFieldHelperAccessor) signField;
		messages[line] = doumi$prevMessage;
		signFieldAccessor.doumi$setCursorPos(doumi$prevCursorPos);
		signFieldAccessor.doumi$setSelectionPos(doumi$prevSelectionPos);
		doumi$prevMessage = null;
	}
}
