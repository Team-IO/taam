package net.teamio.taam.content;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.teamio.taam.Taam;

public class ItemWithMetadata extends Item {

	private IIcon[] iconList;
	private Enum<?>[] metaValues;
	private String baseName;
	
	public ItemWithMetadata(String baseName, Enum<?>[] metaValues) {
		super();
		this.baseName = baseName;
		this.metaValues = metaValues;
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
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
		return meta >= 0 && meta < metaValues.length;
	}

}