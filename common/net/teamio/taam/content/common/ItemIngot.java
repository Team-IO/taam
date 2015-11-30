package net.teamio.taam.content.common;

import net.teamio.taam.Taam;
import net.teamio.taam.content.ItemWithMetadata;

public class ItemIngot extends ItemWithMetadata {

	public ItemIngot(String baseName, Enum<?>[] metaValues) {
		super(baseName, metaValues);
	}

	@Override
	public boolean isValidMetadata(int meta) {
		return !Taam.isOreOnly(meta) && super.isValidMetadata(meta);
	}
}
