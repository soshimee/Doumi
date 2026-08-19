package net.meowing.doumi.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
	@Unique
	private String doumi$preeditText = null;
	@Unique
	private int doumi$preeditPos = -1;
	@Unique
	private String doumi$prevValue = null;
	@Unique
	private int doumi$prevCursorPos = -1;
	@Unique
	private int doumi$prevHighlightPos = -1;
	@Unique
	private int doumi$preeditStart = -1;
	@Unique
	private int doumi$preeditEnd = -1;

	@Shadow
	private String value;
	@Shadow
	private int cursorPos;
	@Shadow
	private int highlightPos;

	@Shadow
	protected abstract void scrollTo(int pos);

	@Inject(method = "preeditUpdated", at = @At("HEAD"))
	private void updatePreedit(PreeditEvent event, CallbackInfoReturnable<Boolean> cir) {
		if (event == null) {
			doumi$preeditText = null;
			return;
		}
		int minPos = Math.min(cursorPos, highlightPos);
		int maxPos = Math.max(cursorPos, highlightPos);
		doumi$preeditText = new StringBuilder(value).replace(minPos, maxPos, event.fullText()).toString();
		doumi$preeditPos = Math.min(cursorPos, highlightPos) + event.caretPosition();
		doumi$preeditStart = minPos;
		doumi$preeditEnd = minPos + event.fullText().length();
	}

	@Inject(method = "extractWidgetRenderState", at = @At("HEAD"))
	private void renderHead(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (doumi$preeditText == null) return;
		doumi$prevValue = value;
		doumi$prevCursorPos = cursorPos;
		doumi$prevHighlightPos = highlightPos;
		value = doumi$preeditText;
		cursorPos = doumi$preeditPos;
		highlightPos = doumi$preeditPos;
		scrollTo(doumi$preeditPos);
	}

	@Inject(method = "extractWidgetRenderState", at = @At("TAIL"))
	private void renderTail(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (doumi$prevValue == null) return;
		value = doumi$prevValue;
		cursorPos = doumi$prevCursorPos;
		highlightPos = doumi$prevHighlightPos;
		doumi$prevValue = null;
	}

	@WrapOperation(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;applyFormat(Ljava/lang/String;I)Lnet/minecraft/util/FormattedCharSequence;"))
	private FormattedCharSequence renderUnderline(EditBox instance, String text, int offset, Operation<FormattedCharSequence> original) {
		FormattedCharSequence baseSequence = original.call(instance, text, offset);
		if (doumi$preeditText == null) return baseSequence;
		int textLength = text.length();
		int segmentEnd = offset + textLength;
		if (segmentEnd <= doumi$preeditStart || offset >= doumi$preeditEnd) return baseSequence;
		Style styleModifier = Style.EMPTY.withUnderlined(true);
		return (sink) -> baseSequence.accept((charIndex, currentStyle, codePoint) -> {
			int globalIndex = offset + charIndex;
			Style finalStyle = (globalIndex >= doumi$preeditStart && globalIndex < doumi$preeditEnd)
				? currentStyle.applyTo(styleModifier)
				: currentStyle;
			return sink.accept(charIndex, finalStyle, codePoint);
		});
	}
}
