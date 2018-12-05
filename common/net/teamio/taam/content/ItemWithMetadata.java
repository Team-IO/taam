package net.teamio.taam.content;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Generic item class for items with metadata variants, defined in an enumeration.
 *
 * Passes metadata validation and information texts to a delegate.
 *
 * @author Oliver Kahrmann
 *
 * @param <P> The enumeration type.
 */
public class ItemWithMetadata<P extends Enum<P>> extends Item {

	public abstract static class ItemDelegate<T extends Enum<T>> {
		public abstract boolean isValidMetadata(T meta);

		public abstract void addInformation(ItemStack stack, EntityPlayer player, List<String> lines, boolean detailedInfoSetting);
	}

	private final P[] metaValues;
	protected final String baseName;
	private final ItemDelegate<P> delegate;

	public ItemWithMetadata(String baseName, P[] metaValues, ItemDelegate<P> delegate) {
		super();
		this.baseName = baseName;
		this.metaValues = metaValues;
		this.delegate = delegate;
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> lines, boolean detailedInfoSetting) {
		if(delegate != null) {
			delegate.addInformation(stack, player, lines, detailedInfoSetting);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();

		if (i < 0 || i >= metaValues.length) {
			i = 0;
		}

		return this.getUnlocalizedName() + "." + metaValues[i].name();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		for (int i = 0; i < metaValues.length; i++) {
			if(!isValidMetadata(i)) {
				continue;
			}
			list.add(new ItemStack(item, 1, i));
		}
	}

	public boolean isValidMetadata(int meta) {
		if(meta >= 0 && meta < metaValues.length) {
			if(delegate == null) {
				return true;
			}
			return delegate.isValidMetadata(metaValues[meta]);
		}
		return false;
	}

}