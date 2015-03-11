package founderio.taam.items;

import founderio.taam.Taam;

public class ItemIngot extends ItemWithMetadata {

	public ItemIngot(String baseName, String[] metaValues) {
		super(baseName, metaValues);
	}

	@Override
	public boolean isValidMetadata(int meta) {
		return !Taam.isOreOnly(meta) && super.isValidMetadata(meta);
	}
}
