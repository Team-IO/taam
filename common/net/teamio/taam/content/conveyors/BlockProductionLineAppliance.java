package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.util.TaamUtil;

public class BlockProductionLineAppliance extends BlockProductionLine {

	public static final PropertyEnum<Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META> VARIANT = PropertyEnum.create("variant", Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.class);
	
	public BlockProductionLineAppliance() {
		super();
	}
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, VARIANT);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META variant = (Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META)state.getValue(VARIANT);
		int meta = variant.ordinal();
		
		return meta;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {

		Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META[] values = Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.values();
		return getDefaultState().withProperty(VARIANT, values[meta]);
	}
	
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
			// Sprayer
			return new ApplianceSprayer();
		}
		return null;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		this.minX = 0;
		this.maxX = 1;
		this.minZ = 0;
		this.maxZ = 1;
		this.minY = 0;
		this.maxY = 1;
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
		} else {
			return true;
		}
	}
	
}
