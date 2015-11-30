package net.teamio.taam.content.common;

import net.teamio.taam.Taam;
import net.teamio.taam.content.ItemWithMetadata;

public class ItemDust extends ItemWithMetadata {
	public ItemDust(String baseName, Enum<?>[] metaValues) {
		super(baseName, metaValues);
	}

	@Override
	public boolean isValidMetadata(int meta) {
		return !Taam.isOreOnly(meta) && super.isValidMetadata(meta);
	}

}
