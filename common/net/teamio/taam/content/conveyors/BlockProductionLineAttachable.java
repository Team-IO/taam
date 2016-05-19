package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.properties.IProperty;
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
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.util.TaamUtil;

public class BlockProductionLineAttachable extends BlockProductionLine {

	public static final PropertyEnum<Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.class);

	public BlockProductionLineAttachable() {
		super();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { VARIANT },
				new IUnlistedProperty[] { OBJModel.OBJProperty.INSTANCE });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META variant = state.getValue(VARIANT);
		return variant.ordinal();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();
		if (meta < 0 || meta > values.length) {
			meta = 0;
		}
		return getDefaultState().withProperty(VARIANT, values[meta]);
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
		Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META variant = (Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META) state
				.getValue(VARIANT);
		switch (variant) {
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
		//TODO: Optimize
		float minY, maxY, minX,maxX, minZ, maxZ;
		minY = 0f;
		maxY = 0.5f;
		TileEntity te = source.getTileEntity(pos);
		if(state.getBlock() != this || !(te instanceof IRotatable)) {
			minX = 0;
			maxX = 1;
			minZ = 0;
			maxZ = 1;
		} else {
			EnumFacing facing = ((IRotatable) te).getFacingDirection();
			switch (facing) {
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
		}
		return new AxisAlignedBB(minX,minY,minZ, maxX, maxY,maxZ);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IRotatable) {
			return TaamUtil.canAttach(world, pos, ((IRotatable) te).getFacingDirection());
		} else {
			return true;
		}
	}

}
