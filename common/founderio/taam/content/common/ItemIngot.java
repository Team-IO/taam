package founderio.taam.content.common;

import founderio.taam.Taam;
import founderio.taam.content.ItemWithMetadata;

public class ItemIngot extends ItemWithMetadata {

	public ItemIngot(String baseName, String[] metaValues) {
		super(baseName, metaValues);
	}

	@Override
	public boolean isValidMetadata(int meta) {
		return !Taam.isOreOnly(meta) && super.isValidMetadata(meta);
	}
}
