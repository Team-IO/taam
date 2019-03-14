package net.teamio.taam.content;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.conveyors.ConveyorUtil;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.machines.MachineTileEntity;
import net.teamio.taam.rendering.obj.OBJModel;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.TaamUtil;
import net.teamio.taam.util.WrenchUtil;

import java.util.List;

/**
 * BaseBlock is the superclass for all blocks with tile entities in Taam.
 * It is configured for easier rendering
 * <ul>
 * <li>not a full block, not opaque
 * <li>handles metadata of dropped items
 * <li>handle OBJModel block state via {@link #getExtendedState}
 * (requires unlisted state to be added to subclasses' block state)
 * </ul>
 * <p>
 * and for handling the tile entities
 * <ul>
 * <li>set the owner of a tile entity when placed
 * <li>handle breaking of unsupported blocks (canBlockStay)
 * <li>drop items in tile entity inventory when broken
 * <li>support rotation via {@link WrenchUtil#rotateBlock}
 * <li>support world interaction via {@link IWorldInteractable} on the tile entity
 * </ul>
 */
public abstract class BaseBlock extends Block {

	/**
	 * One instance for the OBJState visible parts constant "ALL"
	 */
	public static final List<String> ALL = Lists.newArrayList(OBJModel.Group.ALL);

	public BaseBlock(Material material) {
		super(material);
		fullBlock = false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer,
	                            ItemStack stack) {
		// Update Owner
		if (!(placer instanceof EntityPlayer)) {
			return;
		}
		BaseTileEntity te = (BaseTileEntity) worldIn.getTileEntity(pos);
		if (te == null) {
			Log.error("Tile entity was null at position {} in world {}, expected instance of {}", pos, worldIn.provider.getDimension(), BaseTileEntity.class.getName());
			return;
		}
		te.setOwner((EntityPlayer) placer);
	}

	public abstract boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state);

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (!canBlockStay(worldIn, pos, state)) {

			TaamUtil.breakBlockInWorld(worldIn, pos, state);
			if (this != TaamMain.blockSensor) {
				breakBlock(worldIn, pos, state);
			}
			return;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		if (te != null) {
			TaamUtil.updateBlock(worldIn, pos, true);
			((BaseTileEntity) te).blockUpdate();
		}
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);

			/*
			 * Drop Items
			 */
			if (te != null && !(te instanceof TileEntityChute)) {
				IConveyorSlots conveyorSlots = TaamUtil.getCapability(Taam.CAPABILITY_CONVEYOR, te, EnumFacing.UP);
				if (conveyorSlots != null) {
					ConveyorUtil.dropItems(worldIn, pos, conveyorSlots, false);
				}

				if (te instanceof TileEntityConveyor) {
					TileEntityConveyor conveyor = (TileEntityConveyor) te;
					int redirectorCount = 0;
					if (conveyor.isRedirectorLeft()) {
						redirectorCount++;
					}
					if (conveyor.isRedirectorRight()) {
						redirectorCount++;
					}
					if (redirectorCount > 0) {
						InventoryUtils.dropItem(new ItemStack(TaamMain.itemPart, redirectorCount, Taam.ITEM_PART_META.redirector.ordinal()), worldIn, pos, true);
					}
				}

				IItemHandler itemHandler = TaamUtil.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, te, EnumFacing.UP);
				if (itemHandler != null) {
					for (int index = 0; index < itemHandler.getSlots(); index++) {
						ItemStack itemstack = itemHandler.getStackInSlot(index);
						// dropItem checks for null/empty stacks
						InventoryUtils.dropItem(itemstack, worldIn, pos, true);
					}
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		TileEntity te = worldIn.getTileEntity(pos);
		IWorldInteractable interactable = null;
		if (te instanceof IWorldInteractable) {
			interactable = (IWorldInteractable) te;
		} else if (te instanceof MachineTileEntity) {
			MachineTileEntity mte = (MachineTileEntity) te;
			if (mte.machine instanceof IWorldInteractable) {
				interactable = (IWorldInteractable) mte.machine;
			}
		}
		if (interactable != null) {
			// All world interaction (perform action, open gui, etc.) is
			// handled within the entity
			boolean playerHasWrench = WrenchUtil.playerHoldsWrench(playerIn, hand);
			boolean intercepted = interactable.onBlockActivated(worldIn, playerIn, hand, playerHasWrench, facing, hitX, hitY, hitZ);
			if (intercepted) {
				return true;
			}
		}

		if (WrenchUtil.wrenchBlock(worldIn, pos, playerIn, hand, facing, hitX, hitY, hitZ) == EnumActionResult.SUCCESS) {
			return true;
		}

		if (playerIn.isSneaking() && hand == EnumHand.MAIN_HAND) {
			return false;
		}

		if (te instanceof TileEntityConveyorHopper || te instanceof TileEntityConveyorItemBag) {
			playerIn.openGui(TaamMain.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		if (!worldIn.isRemote) {
			TileEntity te = worldIn.getTileEntity(pos);

			IWorldInteractable interactable = null;
			if (te instanceof IWorldInteractable) {
				interactable = (IWorldInteractable) te;
			} else if (te instanceof MachineTileEntity) {
				MachineTileEntity mte = (MachineTileEntity) te;
				if (mte.machine instanceof IWorldInteractable) {
					interactable = (IWorldInteractable) mte.machine;
				}
			}
			if (interactable != null) {
				// All world interaction (perform action, open gui, etc.) is
				// handled within the entity
				boolean playerHasWrench = WrenchUtil.playerHoldsWrench(playerIn, EnumHand.MAIN_HAND);
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

		IRenderable renderable = null;

		if (te instanceof IRenderable) {
			renderable = (IRenderable) te;
		}

		if (te instanceof MachineTileEntity) {
			MachineTileEntity mte = (MachineTileEntity) te;
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

		OBJModel.OBJState retState = new OBJModel.OBJState(visibleParts);
		retState.setIgnoreHidden(true);

		IExtendedBlockState extendedState = (IExtendedBlockState) state;

		return extendedState.withProperty(OBJModel.OBJProperty.instance, retState);
	}

}
