package net.teamio.taam.content.common;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.BLOCK_MACHINES_META;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.piping.TileEntityCreativeWell;
import net.teamio.taam.rendering.obj.OBJModel;

import java.util.List;

public class BlockMachines extends BaseBlock {

	public static final PropertyEnum<Taam.BLOCK_MACHINES_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_MACHINES_META.class);

	public BlockMachines() {
		super(Material.WOOD);
		setSoundType(SoundType.METAL);
		setHardness(6);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { VARIANT },
				new IUnlistedProperty[] { OBJModel.OBJProperty.instance });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_MACHINES_META meta = state.getValue(VARIANT);
		return meta.ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		Taam.BLOCK_MACHINES_META[] values = Taam.BLOCK_MACHINES_META.values();
		if (meta < 0 || meta > values.length) {
			return getDefaultState();
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState blockState) {
		Taam.BLOCK_MACHINES_META variant = blockState.getValue(VARIANT);
		switch (variant) {
		case chute:
			return new TileEntityChute(false);
		case creativecache:
			return new TileEntityCreativeCache();
		case creativewell:
			return new TileEntityCreativeWell();
		default:
			return null;
		}
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_MACHINES_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + values[i].name();
	}


	@Override
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> items) {
		BLOCK_MACHINES_META[] values = Taam.BLOCK_MACHINES_META.values();
		for (int i = 0; i < values.length; i++) {
			items.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		Taam.BLOCK_MACHINES_META variant = state.getValue(VARIANT);
		if (variant == BLOCK_MACHINES_META.creativecache) {
			// Do not drop anything for the creative cache (fake items...)
			return;
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		Taam.BLOCK_MACHINES_META variant = base_state.getValue(VARIANT);
		if (variant == BLOCK_MACHINES_META.chute) {
			return side == EnumFacing.DOWN || side == EnumFacing.UP;
		}
		return true;
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}

}
