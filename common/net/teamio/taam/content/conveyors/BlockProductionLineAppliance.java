package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.rendering.obj.OBJModel;
import net.teamio.taam.util.TaamUtil;

public class BlockProductionLineAppliance extends BlockProductionLine {

	public static final PropertyEnum<Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.class);
	public static final PropertyEnum<EnumFacing> DIRECTION = PropertyEnum.create("direction", EnumFacing.class, EnumFacing.HORIZONTALS);

	public BlockProductionLineAppliance() {
		super();
	}

	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { DIRECTION, VARIANT },
				new IUnlistedProperty[] { OBJModel.OBJProperty.instance });
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META variant = state.getValue(VARIANT);
		int meta = variant.ordinal();

		return meta;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {

		Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META[] values = Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.values();
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

		// Let the tile entity update anything that is required for rendering
		ATileEntityAppliance te = (ATileEntityAppliance) worldIn.getTileEntity(pos);
		if(te.getWorld().isRemote) {
			te.renderUpdate();
		}

		// This makes the state shows up in F3. Previously it was not actually applied on the rendering, though.
		// Rendering Transform was applied in getExtendedState
		// Since 1.9 this seems to work, though

		// Add rotation to state
		return state.withProperty(DIRECTION, ((IRotatable) te).getFacingDirection());
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}
		return super.getUnlocalizedName() + "." + values[i].name();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META variant = state.getValue(VARIANT);
		switch(variant) {
		case sprayer:
			return new ApplianceSprayer();
		case aligner:
			return new ApplianceAligner();
		default:
			Log.error("Was not able to create a TileEntity for " + getClass().getName());
			return null;
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof IRotatable) {
			return TaamUtil.canAttachAppliance(world, pos, ((IRotatable) te).getFacingDirection());
		}
		return true;
	}

}
