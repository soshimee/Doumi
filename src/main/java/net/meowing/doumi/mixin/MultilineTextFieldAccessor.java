package net.meowing.doumi.mixin;

import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultilineTextField.class)
public interface MultilineTextFieldAccessor {
	@Accessor("value")
	String doumi$getValue();
	@Accessor("value")
	void doumi$setValue(String value);

	@Accessor("cursor")
	int doumi$getCursor();
	@Accessor("cursor")
	void doumi$setCursor(int cursor);

	@Accessor("selectCursor")
	int doumi$getSelectCursor();
	@Accessor("selectCursor")
	void doumi$setSelectCursor(int selectCursor);

	@Invoker("reflowDisplayLines")
	void doumi$invokeReflowDisplayLines();
}
