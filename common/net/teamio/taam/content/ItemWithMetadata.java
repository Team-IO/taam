package net.teamio.taam.content;

import java.util.List;

import codechicken.lib.render.TextureUtils.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;

public class ItemWithMetadata<P extends Enum<P>> extends Item {

	public abstract static class ItemDelegate<T extends Enum<T>> {
		public abstract boolean isValidMetadata(T meta);

		public abstract void addInformation(ItemStack stack, EntityPlayer player, List<String> lines, boolean detailedInfoSetting);
	}

	protected IIcon[] iconList;
	private P[] metaValues;
	protected String baseName;
	private ItemDelegate<P> delegate;
	
	public ItemWithMetadata(String baseName, P[] metaValues, ItemDelegate<P> delegate) {
		super();
		this.baseName = baseName;
		this.metaValues = metaValues;
		this.delegate = delegate;
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean detailedInfoSetting) {
		if(delegate != null) {
			delegate.addInformation(stack, player, lines, detailedInfoSetting);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {
	
		if (meta < 0 || meta >= iconList.length) {
			meta = 0;
		}
	
		return iconList[meta];
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
	public void registerIcons(IIconRegister ir) {
		iconList = new IIcon[metaValues.length];
		for (int i = 0; i < metaValues.length; i++) {
			iconList[i] = ir.registerIcon(Taam.MOD_ID + ":" + baseName + "." + metaValues[i].name());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (int i = 0; i < metaValues.length; i++) {
			if(!isValidMetadata(i)) {
				continue;
			}
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	public boolean isValidMetadata(int meta) {
		if(meta >= 0 && meta < metaValues.length) {
			if(delegate != null) {
				return delegate.isValidMetadata(metaValues[meta]);
			}
		}
		return false;
	}

}