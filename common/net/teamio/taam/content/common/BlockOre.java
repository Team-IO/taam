package net.teamio.taam.content.common;

import com.google.common.base.Predicate;
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
import net.teamio.taam.Taam.BLOCK_ORE_META;

import java.util.List;

public class BlockOre extends Block {

	public static final PropertyEnum<Taam.BLOCK_ORE_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_ORE_META.class, new Predicate<Taam.BLOCK_ORE_META>() {
		@Override
		public boolean apply(BLOCK_ORE_META input) {
			return input.ordinal() < 16;
		}
	});

	public BlockOre() {
		super(Material.ROCK);
		setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", 1);
		setResistance(3.14159265359f);
		setHardness(2);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_ORE_META meta = state.getValue(VARIANT);
		return Math.min(meta.ordinal(), 15);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		Taam.BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		if(meta < 0 || meta > values.length) {
			return getDefaultState();
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
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
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		Taam.BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		for (int i = 0; i < values.length; i++) {
			if(values[i].ore) {
				list.add(new ItemStack(item, 1, i));
			}
		}
	}

}