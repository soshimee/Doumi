package net.meowing.doumi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.meowing.doumi.utils.PreeditInfo;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
	@Unique
	private PreeditInfo doumi$preeditInfo = null;

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
			doumi$preeditInfo = null;
			return;
		}
		doumi$preeditInfo = PreeditInfo.fromPreeditEvent(event).applyToText(value, cursorPos, highlightPos);
	}

	@WrapMethod(method = "extractWidgetRenderState")
	private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (doumi$preeditInfo == null) {
			original.call(graphics, mouseX, mouseY, a);
			return;
		}
		String prevValue = value;
		int prevCursorPos = cursorPos;
		int prevHighlightPos = highlightPos;
		value = doumi$preeditInfo.text();
		cursorPos = doumi$preeditInfo.pos();
		highlightPos = doumi$preeditInfo.pos();
		scrollTo(doumi$preeditInfo.pos());
		try {
			original.call(graphics, mouseX, mouseY, a);
		} finally {
			value = prevValue;
			cursorPos = prevCursorPos;
			highlightPos = prevHighlightPos;
		}
	}

	@WrapMethod(method = "updateTextPosition")
	private void updateTextPosition(Operation<Void> original) {
		if (doumi$preeditInfo == null) {
			original.call();
			return;
		}
		String prevValue = value;
		value = doumi$preeditInfo.text();
		try {
			original.call();
		} finally {
			value = prevValue;
		}
	}

	@WrapMethod(method = "applyFormat")
	private FormattedCharSequence renderStyle(String text, int offset, Operation<FormattedCharSequence> original) {
		FormattedCharSequence baseSequence = original.call(text, offset);
		if (doumi$preeditInfo == null) return baseSequence;
		int textLength = text.length();
		int segmentEnd = offset + textLength;
		if (segmentEnd <= doumi$preeditInfo.start() || offset >= doumi$preeditInfo.end()) return baseSequence;
		Style styleModifier = Style.EMPTY.withUnderlined(true);
		int[] index = new int[] {0};
		return (sink) -> baseSequence.accept((charIndex, currentStyle, codePoint) -> {
			int globalIndex = offset + index[0]++;
			Style finalStyle = (globalIndex >= doumi$preeditInfo.start() && globalIndex < doumi$preeditInfo.end())
				? currentStyle.applyTo(styleModifier)
				: currentStyle;
			return sink.accept(charIndex, finalStyle, codePoint);
		});
	}
}
