package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.material.Material;
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
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.BLOCK_MACHINES_META;
import net.teamio.taam.content.BaseBlock;

public class BlockMachines extends BaseBlock {

	public BlockMachines() {
		super(Material.wood);
		this.setStepSound(soundTypeWood);
		this.setHardness(6);
		this.setHarvestLevel("pickaxe", 2);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		switch(metadata) {
		case 0:
			return new TileEntityChute(false);
		case 1:
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
		int metadata = world.getBlockMetadata(pos);
		switch(metadata) {
		case 0:
			// Have chute as full model for now..
			/*this.minX = 0.10;
			this.minY = 0;
			this.minZ = 0.10;
			this.maxX = 0.9;
			this.maxY = 1;
			this.maxZ = 0.9;
			break;*/
		case 1:
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
		if(meta == 1) {
			// Do not drop anything for the creative cache (fake items...)
			return;
		}
		super.breakBlock(world, pos, state);
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		int meta = world.getBlockMetadata(pos);
		if(meta == 0) {
			return side == EnumFacing.DOWN || side == EnumFacing.UP;
		} else {
			return true;
		}
	}

}
