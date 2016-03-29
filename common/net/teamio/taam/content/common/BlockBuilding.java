package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;

public class BlockBuilding extends Block {

	public static final PropertyEnum<Taam.BLOCK_CONCRETE_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_CONCRETE_META.class);
	
	public BlockBuilding() {
		super(Material.rock);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", 1);
		this.setResistance(3.7f);
		this.setHardness(2);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_CONCRETE_META meta = (Taam.BLOCK_CONCRETE_META)state.getValue(VARIANT);
		return meta.ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		Taam.BLOCK_CONCRETE_META[] values = Taam.BLOCK_CONCRETE_META.values();
		if(meta < 0 || meta > values.length) {
			return getDefaultState();
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_CONCRETE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + values[i].name();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		Taam.BLOCK_CONCRETE_META[] values = Taam.BLOCK_CONCRETE_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

}