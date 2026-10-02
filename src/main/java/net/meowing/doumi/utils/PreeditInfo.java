package net.meowing.doumi.utils;

import net.minecraft.client.input.PreeditEvent;

public record PreeditInfo(String text, int pos, int start, int end) {
	public static PreeditInfo fromPreeditEvent(PreeditEvent event) {
		int offset = 0;
		for (int i = 0; i < event.focusedBlock(); ++i) offset += event.blocks().get(i).length();
		return new PreeditInfo(event.fullText(), event.caretPosition(), offset, offset + event.blocks().get(event.focusedBlock()).length());
	}

	public PreeditInfo applyInlineStyle() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < start; ++i) {
			sb.append(text.charAt(i));
		}
		for (int i = start; i < end; ++i) {
			sb.append("§n");
			sb.append(text.charAt(i));
		}
		sb.append("§r");
		for (int i = end; i < text.length(); ++i) {
			sb.append(text.charAt(i));
		}
		return new PreeditInfo(sb.toString(), (pos - start) * 3 + start, start, (end - start) * 3 + start + 2);
	}

	public PreeditInfo applyToText(String existingText, int pos1, int pos2) {
		int minPos = Math.min(pos1, pos2);
		int maxPos = Math.max(pos1, pos2);
		return new PreeditInfo(new StringBuilder(existingText).replace(minPos, maxPos, text).toString(), pos + minPos, start + minPos, end + minPos);
	}
}
