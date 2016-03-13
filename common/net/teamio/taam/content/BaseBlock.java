package net.teamio.taam.content;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WrenchUtil;
import net.teamio.taam.util.inv.InventoryUtils;

public abstract class BaseBlock extends Block {

	public BaseBlock(Material material) {
		super(material);
		this.fullBlock = false;
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
			ItemStack stack) {
		// Update Owner
		if (placer instanceof EntityPlayer) {
			BaseTileEntity te = (BaseTileEntity) worldIn.getTileEntity(pos);
			te.setOwner((EntityPlayer) placer);
		}
	}

	public abstract boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state);
	
	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if(!canBlockStay(worldIn, pos, state)) {
			TaamUtil.breakBlockInWorld(worldIn, pos, state);
			if(this != TaamMain.blockSensor) {
				breakBlock(worldIn, pos, state);
			}
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null) {
			// Update stuff like conveyors if something changes
			((BaseTileEntity)te).updateRenderingInfo();
			worldIn.markBlockForUpdate(pos);
		}
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (worldIn.isRemote) {
			return;
		}
		
		TileEntity te = worldIn.getTileEntity(pos);

		if(te instanceof TileEntityConveyor) {
			((TileEntityConveyor) te).dropItems();
		}

		/*
		 * Drop Items
		 */
		if(te instanceof IInventory) {
			IInventory inventory = (IInventory)te;
			for (int index = 0; index < inventory.getSizeInventory(); index++) {
				ItemStack itemstack = inventory.getStackInSlot(index);

				if (itemstack != null && itemstack.getItem() != null) {
					InventoryUtils.dropItem(itemstack, worldIn, pos);
				}
			}
		}
		
		/*
		 * Drop Appliances
		 */
		if(te instanceof IConveyorApplianceHost) {
			IConveyorApplianceHost applianceHost = (IConveyorApplianceHost)te;
			ConveyorUtil.dropAppliance(applianceHost, null, worldIn, pos);
		}

		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public boolean rotateBlock(World worldObj, BlockPos pos, EnumFacing axis) {
		return WrenchUtil.rotateBlock(worldObj.getTileEntity(pos));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumFacing side, float hitX, float hitY, float hitZ) {
				
		if(WrenchUtil.wrenchBlock(worldIn, pos, playerIn, side, hitX, hitY, hitZ)) {
			return true;
		}
		
		if(playerIn.isSneaking()) {
			return false;
		}
	
		if(!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);
			
			if(te instanceof IWorldInteractable) {
				// All world interaction (perform action, open gui, etc.) is handled within the entity
				IWorldInteractable interactable = ((IWorldInteractable) te);
				boolean playerHasWrench = WrenchUtil.playerHasWrench(playerIn);
				boolean intercepted = interactable.onBlockActivated(worldIn, playerIn, playerHasWrench, side, hitX, hitY, hitZ);
				if(intercepted) {
					return true;
				}
			} else if(te instanceof TileEntityConveyorHopper || te instanceof TileEntityConveyorItemBag) {
				playerIn.openGui(TaamMain.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			} else if(te instanceof TileEntityConveyorTrashCan) {
				((TileEntityConveyorTrashCan)te).clearOut();
			}
		}
		return true;
	}
	
	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		if(!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);
			
			if(te instanceof IWorldInteractable) {
				// All world interaction (perform action, open gui, etc.) is handled within the entity
				IWorldInteractable interactable = ((IWorldInteractable) te);
				boolean playerHasWrench = WrenchUtil.playerHasWrench(playerIn);
				/*boolean intercepted = */
				interactable.onBlockHit(worldIn, playerIn, playerHasWrench);
//				if(intercepted) {
//					return true;
//				}
			}
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		setBlockBoundsBasedOnState(worldIn, pos);
		return super.getCollisionBoundingBox(worldIn, pos, state);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube() {
		return false;
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
	public static void updateBlocksAround(World world, BlockPos pos) {
		Block blockType = world.getBlockState(pos).getBlock();
		world.notifyNeighborsOfStateChange(pos, blockType);
		world.notifyNeighborsOfStateChange(pos.west(), blockType);
		world.notifyNeighborsOfStateChange(pos.east(), blockType);
		world.notifyNeighborsOfStateChange(pos.down(), blockType);
		world.notifyNeighborsOfStateChange(pos.up(), blockType);
		world.notifyNeighborsOfStateChange(pos.north(), blockType);
        world.notifyNeighborsOfStateChange(pos.south(), blockType);
	}

}
