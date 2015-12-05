package net.teamio.taam.content;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WrenchUtil;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;

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
			te.setOwner((EntityPlayer) entity);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
		if(!canBlockStay(world, x, y, z)) {
			TaamUtil.breakBlockInWorld(world, x, y, z, this);
			return;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null) {
			world.markBlockForUpdate(x, y, z);
			// Update stuff like conveyors if something changes
			((BaseTileEntity)te).updateRenderingInfo();
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
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
	
	@Override
	public boolean onBlockActivated(World world, int x, int y,
			int z, EntityPlayer player, int side, float hitX, float hitY,
			float hitZ) {
				
		if(WrenchUtil.wrenchBlock(world, x, y, z, player, side, hitX, hitY, hitZ)) {
			return true;
		}
		
		if(player.isSneaking()) {
			return false;
		}
	
		if(!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			
			if(te instanceof IWorldInteractable) {
				// All world interaction (perform action, open gui, etc.) is handled within the entity
				IWorldInteractable interactable = ((IWorldInteractable) te);
				boolean playerHasWrench = WrenchUtil.playerHasWrench(player);
				boolean intercepted = interactable.onBlockActivated(world, x, y, z, player, playerHasWrench, side, hitX, hitY, hitZ);
				if(intercepted) {
					return true;
				}
			} else if(te instanceof TileEntityConveyorHopper || te instanceof TileEntityConveyorItemBag) {
				player.openGui(TaamMain.instance, 0, world, x, y, z);
			} else if(te instanceof TileEntityConveyorTrashCan) {
				((TileEntityConveyorTrashCan)te).clearOut();
			}
		}
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
			int y, int z) {
				setBlockBoundsBasedOnState(world, x, y, z);
				return super.getCollisionBoundingBoxFromPool(world, x, y, z);
			}

	/**
	 * Updates a block and all surrounding blocks (meaning, pushes a block
	 * update for this block and for all directly adjacent blocks)
	 * 
	 * Useful when working with redstone.
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void updateBlocksAround(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		world.notifyBlocksOfNeighborChange(x, y, z, block);
		world.notifyBlocksOfNeighborChange(x + 1, y, z, block);
		world.notifyBlocksOfNeighborChange(x - 1, y, z, block);
		world.notifyBlocksOfNeighborChange(x, y, z + 1, block);
		world.notifyBlocksOfNeighborChange(x, y, z - 1, block);
		world.notifyBlocksOfNeighborChange(x, y - 1, z, block);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, block);
	}

}
