package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.teamio.taam.Taam;
import net.teamio.taam.content.IRotatable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockProductionLineAttachable extends BlockProductionLine {
	
	public BlockProductionLineAttachable() {
		super();
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}
		return super.getUnlocalizedName() + "." + values[i].name();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list) {
		Enum<?>[] values = Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.values();
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		int type = metadata & 3;
		if(type == 0) {
			// Item Bag
			return new TileEntityConveyorItemBag();
		} else if(type == 1) {
			// Trash Can
			return new TileEntityConveyorTrashCan();
		}
		return null;
	}
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world,
			int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		int rot = (meta & 12) >> 2;
		//int type = meta & 3;
		this.minY = 0f;
		this.maxY = 0.5f;
		switch(rot) {
		default:
		case 0: //NORTH
			this.minX = 0;
			this.maxX = 1;
			this.minZ = 0;
			this.maxZ = 0.35f;
			break;
		case 1: //SOUTH
			this.minX = 0;
			this.maxX = 1;
			this.minZ = 0.65f;
			this.maxZ = 1;
			break;
		case 2: //EAST
			this.minX = 0.65f;
			this.maxX = 1;
			this.minZ = 0;
			this.maxZ = 1;
			break;
		case 3: //WEST
			this.minX = 0;
			this.maxX = 0.35f;
			this.minZ = 0;
			this.maxZ = 1;
			break;
		}
	}
	
	@Override
	public int damageDropped(int meta) {
		return meta & 3;
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof IRotatable) {
			return ATileEntityAttachable.canAttach(world, x, y, z, ((IRotatable) te).getFacingDirection());
		} else {
			return true;
		}
	}
	
}
