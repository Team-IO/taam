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
 * <p>
 * Passes metadata validation and information texts to a delegate.
 *
 * @param <P> The enumeration type.
 * @author Oliver Kahrmann
 */
public class ItemWithMetadata<P extends Enum<P>> extends Item {

	private final P[] metaValues;
	protected final String baseName;
	private final ItemDelegate<P> delegate;

	public abstract static class ItemDelegate<T extends Enum<T>> {
		/**
		 * Callback to exclude metadata values from the sub-items.
		 * The default implementation does not exclude anything (returns true).
		 *
		 * @param meta The metadata as given in the constructor of the item class.
		 * @return true, if the given metadata value should be included.
		 */
		public boolean isValidMetadata(T meta) {
			return true;
		}

		/**
		 * Callback to add information to the item tooltip.
		 * The default implementation does not add any information.
		 *
		 * @param stack   The stack being investigated
		 * @param player  The player holding the item
		 * @param tooltip The tooltip that is built
		 * @param detailedInfoSetting Is detail info enabled?
		 */
		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean detailedInfoSetting) {
			// Default implementation does not add information
		}
	}

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
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean detailedInfoSetting) {
		if(delegate != null) {
			delegate.addInformation(stack, player, tooltip, detailedInfoSetting);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int i = stack.getItemDamage();

		if (i < 0 || i >= metaValues.length) {
			i = 0;
		}

		return this.getUnlocalizedName() + "." + metaValues[i].name();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> items) {
		for (int i = 0; i < metaValues.length; i++) {
			if (!isValidMetadata(i)) {
				continue;
			}
			items.add(new ItemStack(item, 1, i));
		}
	}

	public boolean isValidMetadata(int meta) {
		if (meta >= 0 && meta < metaValues.length) {
			if (delegate == null) {
				return true;
			}
			return delegate.isValidMetadata(metaValues[meta]);
		}
		return false;
	}

}
