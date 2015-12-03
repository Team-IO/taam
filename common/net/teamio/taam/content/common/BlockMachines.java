package net.teamio.taam.content.common;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.BLOCK_MACHINES_META;
import net.teamio.taam.content.BaseBlock;

public class BlockMachines extends BaseBlock {

	public BlockMachines() {
		super(Material.wood);
		this.setBlockTextureName(Taam.MOD_ID + ":tech_block");
		this.setStepSound(soundTypeWood);
		this.setHardness(6);
		this.setHarvestLevel("pickaxe", 2);
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		switch(metadata) {
		case 0:
			return new TileEntityChute();
		case 1:
			return new TileEntityCreativeCache();
		}
		return null;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		switch(metadata) {
		case 0:
			this.minX = 0.10;
			this.minY = 0;
			this.minZ = 0.10;
			this.maxX = 0.9;
			this.maxY = 1;
			this.maxZ = 0.9;
			break;
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
	public int damageDropped(int meta) {
		if (meta < 0 || meta >= Taam.BLOCK_MACHINES_META.values().length) {
			meta = 0;
		}
		return meta;
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

}
