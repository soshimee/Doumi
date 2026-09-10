package net.meowing.doumi.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.meowing.doumi.misc.IPreeditExtra;
import net.minecraft.client.input.PreeditEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PreeditEvent.class)
public class PreeditEventMixin implements IPreeditExtra {
	@Unique
	private int doumi$selectionStart = -1;
	@Unique
	private int doumi$selectionLength = -1;

	@Unique
	@Override
	public int doumi$getSelectionStart() {
		return doumi$selectionStart;
	}

	@Unique
	@Override
	public int doumi$getSelectionLength() {
		return doumi$selectionLength;
	}

	@Override
	public void doumi$setSelection(int selectionStart, int selectionLength) {
		doumi$selectionStart = selectionStart;
		doumi$selectionLength = selectionLength;
	}

	@WrapMethod(method = "fromSdlTextEditing")
	private static PreeditEvent addExtra(String text, int selectionStart, int selectionLength, Operation<PreeditEvent> original) {
		PreeditEvent preeditEvent = original.call(text, selectionStart, selectionLength);
		if (preeditEvent == null) return null;
		((IPreeditExtra) (Object) preeditEvent).doumi$setSelection(selectionStart, selectionLength);
		return preeditEvent;
	}
}
