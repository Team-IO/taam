package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBuilding<P extends Enum<P>> extends Block {

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
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();

		if (i < 0 || i >= metaValues.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + metaValues[i].name();
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