package net.teamio.taam.content;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
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
		 * @param worldIn The current world
		 * @param tooltip The tooltip that is built
		 * @param flagIn  Additional flags, as defined in {@link Item#addInformation}
		 */
		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (delegate != null) {
			delegate.addInformation(stack, worldIn, tooltip, flagIn);
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		int i = stack.getItemDamage();

		if (i < 0 || i >= metaValues.length) {
			i = 0;
		}

		return this.getTranslationKey() + "." + metaValues[i].name();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		for (int i = 0; i < metaValues.length; i++) {
			if (!isValidMetadata(i)) {
				continue;
			}
			items.add(new ItemStack(this, 1, i));
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
