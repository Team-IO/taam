package net.teamio.taam.content.common;

import java.util.List;

import codechicken.lib.render.TextureUtils.IIconRegister;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.BLOCK_ORE_META;

public class BlockOre extends Block {

	private IIcon[] iconList;
	
	public BlockOre() {
		super(Material.rock);
		this.setStepSound(Block.soundTypeStone);
		this.setHarvestLevel("pickaxe", 1);
		this.setResistance(3.14159265359f);
		this.setHardness(2);
		this.setBlockTextureName(Taam.MOD_ID + ":ore");
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		if (meta < 0 || meta >= Taam.BLOCK_ORE_META.values().length) {
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
		Enum<?>[] values = Taam.BLOCK_ORE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + values[i].name();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ir) {
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		iconList = new IIcon[values.length];
		for (int i = 0; i < values.length; i++) {
			if(values[i].ore) {
				iconList[i] = ir.registerIcon(Taam.MOD_ID + ":ore." + values[i].name());
			} else {
				iconList[i] = ir.registerIcon(Taam.MOD_ID + ":ore.impossible");
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		for (int i = 0; i < values.length; i++) {
			if(values[i].ore) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

}