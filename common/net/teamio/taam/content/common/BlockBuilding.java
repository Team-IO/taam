package net.teamio.taam.content.common;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.teamio.taam.Taam;

public class BlockBuilding<P extends Enum<P>> extends Block {

	private IIcon[] iconList;
	private P[] metaValues;
	protected String baseName;
	
	public BlockBuilding(String baseName, P[] metaValues) {
		super(Material.rock);
		this.setStepSound(Block.soundTypeStone);
		this.setHarvestLevel("pickaxe", 1);
		this.setResistance(3.7f);
		this.setHardness(2);
		this.baseName = baseName;
		this.metaValues = metaValues;
		this.setBlockTextureName(baseName);
	}
	
	@Override
	public int damageDropped(int meta) {
		if (meta < 0 || meta >= metaValues.length) {
			meta = 0;
		}
		return meta;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (meta < 0 || meta >= iconList.length) {
			meta = 0;
		}

		return iconList[meta];
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();

		if (i < 0 || i >= metaValues.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + metaValues[i].name();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		iconList = new IIcon[metaValues.length];
		for (int i = 0; i < metaValues.length; i++) {
			iconList[i] = ir.registerIcon(Taam.MOD_ID + ":" + baseName + "." + metaValues[i].name());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		for (int i = 0; i < metaValues.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

}