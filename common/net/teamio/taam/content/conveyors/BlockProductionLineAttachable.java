package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.util.TaamUtil;

public class BlockProductionLineAttachable extends BlockProductionLine {

	public static final PropertyEnum<Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.class);
	public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
	
	public BlockProductionLineAttachable() {
		super();
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, FACING);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META variant = (Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META)state.getValue(VARIANT);
		int meta = variant.ordinal();
		
		int rot;
		EnumFacing facing = (EnumFacing)state.getValue(FACING);
		switch(facing) {
		default:
		case NORTH:
			rot = 0;
			break;
		case SOUTH:
			rot = 1;
			break;
		case EAST:
			rot = 2;
			break;
		case WEST:
			rot = 3;
			break;
		}
		meta |= rot << 2;
		return meta;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {

		int type = meta & 3;
		int rot = (meta & 12) >> 2;
		EnumFacing facing;
		// In 1.7 this did not use the "regular" order, so we don't use it here as well.
		switch(rot) {
		default:
		case 0:
			facing = EnumFacing.NORTH;
			break;
		case 1:
			facing = EnumFacing.SOUTH;
			break;
		case 2:
			facing = EnumFacing.EAST;
			break;
		case 3:
			facing = EnumFacing.WEST;
			break;
		}
		
		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();
		if(type < 0 || type > values.length) {
			return getDefaultState().withProperty(FACING, facing);
		}
		//Mouse.setGrabbed(false);
		return getDefaultState().withProperty(VARIANT, values[type]).withProperty(FACING, facing);
	}
	
	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}
		return super.getUnlocalizedName() + "." + values[i].name();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META variant = (Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META)state.getValue(VARIANT);
		switch(variant) {
		case itembag:
			// Item Bag
			return new TileEntityConveyorItemBag();
		case trashcan:
			// Trash Can
			return new TileEntityConveyorTrashCan();
		}
		return null;
	}
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		float minY, maxY, minX,maxX, minZ, maxZ;
		minY = 0f;
		maxY = 0.5f;
		if(state.getBlock() != this) {
			minX = 0;
			maxX = 0;
			minZ = 0;
			maxZ = 0;
		}
		EnumFacing facing = (EnumFacing)state.getValue(FACING);
		switch(facing) {
		default:
		case NORTH:
			minX = 0;
			maxX = 1;
			minZ = 0;
			maxZ = 0.35f;
			break;
		case SOUTH:
			minX = 0;
			maxX = 1;
			minZ = 0.65f;
			maxZ = 1;
			break;
		case EAST:
			minX = 0.65f;
			maxX = 1;
			minZ = 0;
			maxZ = 1;
			break;
		case WEST:
			minX = 0;
			maxX = 0.35f;
			minZ = 0;
			maxZ = 1;
			break;
		}
		return new AxisAlignedBB(minX,minY,minZ, maxX, maxY,maxZ);
	}
		
	@Override
	public int damageDropped(IBlockState state) {
		return ((Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META)state.getValue(VARIANT)).ordinal();
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof IRotatable) {
			return TaamUtil.canAttach(world, pos, ((IRotatable) te).getFacingDirection());
		} else {
			return true;
		}
	}
	
}
