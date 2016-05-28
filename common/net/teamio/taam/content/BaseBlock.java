package net.teamio.taam.content;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.machines.MachineTileEntity;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WrenchUtil;
import net.teamio.taam.util.inv.InventoryUtils;

public abstract class BaseBlock extends Block {

	/**
	 * One instance for the OBJState visible parts constant "ALL"
	 */
	public static final List<String> ALL = Lists.newArrayList(OBJModel.Group.ALL);

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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if (!canBlockStay(worldIn, pos, state)) {
			
			TaamUtil.breakBlockInWorld(worldIn, pos, state);
			if (this != TaamMain.blockSensor) {
				breakBlock(worldIn, pos, state);
			}
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if (te != null) {
			TaamUtil.updateBlock(worldIn, pos);
			((BaseTileEntity) te).blockUpdate();
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

		if (te instanceof TileEntityConveyor) {
			((TileEntityConveyor) te).dropItems();
		}

		/*
		 * Drop Items
		 */
		if (te instanceof IInventory) {
			IInventory inventory = (IInventory) te;
			for (int index = 0; index < inventory.getSizeInventory(); index++) {
				ItemStack itemstack = inventory.getStackInSlot(index);

				if (itemstack != null && itemstack.getItem() != null) {
					InventoryUtils.dropItem(itemstack, worldIn, pos);
				}
			}
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean rotateBlock(World worldObj, BlockPos pos, EnumFacing axis) {
		return WrenchUtil.rotateBlock(worldObj.getTileEntity(pos));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
				
		if(WrenchUtil.wrenchBlock(worldIn, pos, playerIn, side, hitX, hitY, hitZ) == EnumActionResult.SUCCESS) {
			return true;
		}

		if (playerIn.isSneaking()) {
			return false;
		}

		TileEntity te = worldIn.getTileEntity(pos);
		if (worldIn.isRemote) {
			return te instanceof IWorldInteractable || te instanceof TileEntityConveyorHopper
					|| te instanceof TileEntityConveyorItemBag;
		} else {

			IWorldInteractable interactable = null;
			if (te instanceof IWorldInteractable) {
				interactable = (IWorldInteractable) te;
			} else if(te instanceof MachineTileEntity) {
				MachineTileEntity mte = (MachineTileEntity)te;
				if(mte.machine instanceof IWorldInteractable) {
					interactable = (IWorldInteractable) mte.machine;
				}
			}
			if(interactable != null) {
				// All world interaction (perform action, open gui, etc.) is
				// handled within the entity
				boolean playerHasWrench = WrenchUtil.playerHasWrenchInMainhand(playerIn);
				boolean intercepted = interactable.onBlockActivated(worldIn, playerIn, playerHasWrench, side, hitX, hitY, hitZ);
				return intercepted;
			} else if (te instanceof TileEntityConveyorHopper || te instanceof TileEntityConveyorItemBag) {
				playerIn.openGui(TaamMain.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
			return false;
		}
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		if (!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);

			IWorldInteractable interactable = null;
			if (te instanceof IWorldInteractable) {
				interactable = (IWorldInteractable) te;
			} else if(te instanceof MachineTileEntity) {
				MachineTileEntity mte = (MachineTileEntity)te;
				if(mte.machine instanceof IWorldInteractable) {
					interactable = (IWorldInteractable) mte.machine;
				}
			}
			if(interactable != null) {
				// All world interaction (perform action, open gui, etc.) is
				// handled within the entity
				boolean playerHasWrench = WrenchUtil.playerHasWrenchInMainhand(playerIn);
				interactable.onBlockHit(worldIn, playerIn, playerHasWrench);
			}
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		// Required false to prevent suffocation
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {

		// Let the tile entity update anything that is required for rendering
		BaseTileEntity te = (BaseTileEntity) worldIn.getTileEntity(pos);
		te.renderUpdate();
		
		return state;
	}

	@Override
	public IExtendedBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		List<String> visibleParts = null;

		TileEntity te = world.getTileEntity(pos);

		// Decide which parts to render, delegated to the tileEntity (if
		// required)

		EnumFacing facing = EnumFacing.NORTH;

		IRotatable rotatable = null;
		IRenderable renderable = null;

		if (te instanceof IRenderable) {
			renderable = (IRenderable) te;
		}

		if (te instanceof IRotatable) {
			rotatable = (IRotatable) te;
		}

		if (te instanceof MachineTileEntity) {
			MachineTileEntity mte = (MachineTileEntity) te;
			if (mte.machine instanceof IRotatable) {
				rotatable = (IRotatable) mte.machine;
			}
			if (mte.machine instanceof IRenderable) {
				renderable = (IRenderable) mte.machine;
			}
			mte.machine.renderUpdate(world, pos);
		}

		if (renderable != null) {
			visibleParts = renderable.getVisibleParts();
		}
		if (visibleParts == null) {
			visibleParts = ALL;
		}

		if (rotatable != null) {
			facing = rotatable.getFacingDirection();
		}
		OBJModel.OBJState retState = new OBJModel.OBJState(visibleParts, true,
				new TRSRTransformation(rotateRenderDirection(facing)));

		IExtendedBlockState extendedState = (IExtendedBlockState) state;

		return extendedState.withProperty(OBJModel.OBJProperty.INSTANCE, retState);
	}

	private EnumFacing rotateRenderDirection(EnumFacing facing) {
		if (facing.getAxis() == Axis.Y) {
			return facing.getOpposite();
		} else {
			return facing.rotateY().rotateY();
		}
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
