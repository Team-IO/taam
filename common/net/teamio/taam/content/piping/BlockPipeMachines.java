package net.teamio.taam.content.piping;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.MaterialMachinesTransparent;

public class BlockPipeMachines extends BaseBlock {

	public static final PropertyEnum<Taam.BLOCK_PIPEMACHINES_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_PIPEMACHINES_META.class);

	public BlockPipeMachines() {
		super(MaterialMachinesTransparent.INSTANCE);
		this.setHardness(3.5f);
		this.setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, VARIANT);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_PIPEMACHINES_META meta = (Taam.BLOCK_PIPEMACHINES_META) state.getValue(VARIANT);
		return meta.ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		Taam.BLOCK_PIPEMACHINES_META[] values = Taam.BLOCK_PIPEMACHINES_META.values();
		if (meta < 0 || meta > values.length) {
			return getDefaultState();
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}

	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_PIPEMACHINES_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + values[i].name();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		Enum<?>[] values = Taam.BLOCK_PIPEMACHINES_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		Taam.BLOCK_PIPEMACHINES_META variant = (Taam.BLOCK_PIPEMACHINES_META) state.getValue(VARIANT);
		switch (variant) {
		case tank:
			return new TileEntityTank();
		case creativewell:
			return new TileEntityCreativeWell();
		case pump:
			return new TileEntityPump();
		case mixer:
			return new TileEntityMixer();
		}
		Log.error("Was not able to create a TileEntity for " + getClass().getName());
		return null;
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}

}
