package net.meowing.doumi.mixin;

import net.minecraft.client.gui.font.TextFieldHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextFieldHelper.class)
public interface TextFieldHelperAccessor {
	@Accessor("cursorPos")
	int doumi$getCursorPos();
	@Accessor("cursorPos")
	void doumi$setCursorPos(int cursorPos);

	@Accessor("selectionPos")
	int doumi$getSelectionPos();
	@Accessor("selectionPos")
	void doumi$setSelectionPos(int selectionPos);
}
