package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.BLOCK_MACHINES_META;
import net.teamio.taam.content.BaseBlock;

public class BlockMachines extends BaseBlock {

	public static final PropertyEnum VARIANT = PropertyEnum.create("variant", Taam.BLOCK_MACHINES_META.class);
	
	public BlockMachines() {
		super(Material.wood);
		this.setStepSound(soundTypeWood);
		this.setHardness(6);
		this.setHarvestLevel("pickaxe", 2);
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, VARIANT);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_MACHINES_META meta = (Taam.BLOCK_MACHINES_META)state.getValue(VARIANT);
		return meta.ordinal();
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		Taam.BLOCK_MACHINES_META[] values = Taam.BLOCK_MACHINES_META.values();
		if(meta < 0 || meta > values.length) {
			return getDefaultState();
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState blockState) {
		Taam.BLOCK_MACHINES_META variant = (Taam.BLOCK_MACHINES_META) blockState.getValue(VARIANT);
		switch(variant) {
		case chute:
			return new TileEntityChute(false);
		case creativecache:
			return new TileEntityCreativeCache();
		}
		return null;
	}
	
	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_MACHINES_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + values[i].name();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		BLOCK_MACHINES_META[] values = Taam.BLOCK_MACHINES_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() != this) {
			Log.warn("Received 'setBlockBoundsBasedOnState' with invalid block in blockstate. This might not be relevant - but does not influence anything at the moment.");
			return;
		}
		Taam.BLOCK_MACHINES_META variant = (Taam.BLOCK_MACHINES_META)state.getValue(VARIANT);
		switch(variant) {
		case chute:
			// Have chute as full model for now..
			/*this.minX = 0.10;
			this.minY = 0;
			this.minZ = 0.10;
			this.maxX = 0.9;
			this.maxY = 1;
			this.maxZ = 0.9;
			break;*/
		case creativecache:
			this.minX = 0;
			this.minY = 0;
			this.minZ = 0;
			this.maxX = 1;
			this.maxY = 1;
			this.maxZ = 1;
			break;
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		Taam.BLOCK_MACHINES_META variant = (Taam.BLOCK_MACHINES_META)state.getValue(VARIANT);
		if(variant == BLOCK_MACHINES_META.creativecache) {
			// Do not drop anything for the creative cache (fake items...)
			return;
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		IBlockState state = world.getBlockState(pos);
		Taam.BLOCK_MACHINES_META variant = (Taam.BLOCK_MACHINES_META)state.getValue(VARIANT);
		if(variant == BLOCK_MACHINES_META.chute) {
			return side == EnumFacing.DOWN || side == EnumFacing.UP;
		} else {
			return true;
		}
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}

}
