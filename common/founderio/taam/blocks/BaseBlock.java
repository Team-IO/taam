package founderio.taam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import founderio.taam.conveyors.ConveyorUtil;
import founderio.taam.conveyors.api.IConveyorApplianceHost;

public abstract class BaseBlock extends Block {

	public BaseBlock(Material material) {
		super(material);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack itemStack) {
		// Update Owner
		if (entity instanceof EntityPlayer) {
			BaseTileEntity te = (BaseTileEntity) world.getTileEntity(x, y, z);
			// TODO: Change to UUID
			te.setOwner(((EntityPlayer) entity).getDisplayName());
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
		TileEntity te = world.getTileEntity(x, y, z);
		// Update stuff like conveyors if something changes
		if(te != null) {
			te.updateContainingBlockInfo();
			world.markBlockForUpdate(x, y, z);
		}
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (world.isRemote) {
			return;
		}
		TileEntity te = world.getTileEntity(x, y, z);

		if(te instanceof TileEntityConveyor) {
			((TileEntityConveyor) te).dropItems();
		}
		
		/*
		 * Drop Items
		 */
		if(te instanceof IInventory) {
			IInventory inventory = (IInventory)te;
			Vector3 location = new Vector3(x, y, z);
			for (int index = 0; index < inventory.getSizeInventory(); index++) {
				ItemStack itemstack = inventory.getStackInSlot(index);

				if (itemstack != null && itemstack.getItem() != null) {
					InventoryUtils.dropItem(itemstack, world, location);
				}
			}
		}
		
		/*
		 * Drop Appliances
		 */
		if(te instanceof IConveyorApplianceHost) {
			IConveyorApplianceHost applianceHost = (IConveyorApplianceHost)te;
			ConveyorUtil.dropAppliance(applianceHost, null, world, x, y, z);
		}

		super.breakBlock(world, x, y, z, block, meta);
	}

}
