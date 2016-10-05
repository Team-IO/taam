package net.teamio.taam.machines;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import mcmultipart.MCMultiPartMod;
import mcmultipart.block.BlockMultipart;
import mcmultipart.capabilities.ISlottedCapabilityProvider;
import mcmultipart.microblock.IMicroblock;
import mcmultipart.multipart.*;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.IWorldInteractable;
import net.teamio.taam.content.piping.MachinePipe;
import net.teamio.taam.rendering.obj.OBJModel;
import net.teamio.taam.util.FaceBitmap;
import net.teamio.taam.util.InventoryUtils;
import net.teamio.taam.util.WrenchUtil;

public class MachineMultipart extends Multipart implements IOccludingPart, ITickable, ISlottedPart, ISlottedCapabilityProvider {
	public IMachine machine;
	private IMachineMetaInfo meta;

	public static final PropertyEnum<Taam.MACHINE_META> VARIANT = PropertyEnum.create("variant", Taam.MACHINE_META.class);
	public static final PropertyEnum<EnumFacing> DIRECTION = PropertyEnum.create("direction", EnumFacing.class);

	public MachineMultipart() {
	}

	public MachineMultipart(IMachineMetaInfo meta) {
		this.meta = meta;
		machine = meta.createMachine();
	}

	@Override
	public boolean isToolEffective(String type, int level) {
		return "pickaxe".equals(type) && level >= 1;
	}

	@Override
	public float getHardness(PartMOP hit) {
		return 3.5f;
	}

	@Override
	public String getType() {
		return meta.unlocalizedName();
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		machine.addCollisionBoxes(mask, list, collidingEntity);
	}

	@Override
	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		machine.addSelectionBoxes(list);
	}

	@Override
	public void addOcclusionBoxes(List<AxisAlignedBB> list) {
		machine.addOcclusionBoxes(list);
	}

	@Override
	public void onPartChanged(IMultipart part) {
		doBlockUpdate();
	}

	@Override
	public void onNeighborBlockChange(Block block) {
		doBlockUpdate();
	}

	@Override
	public void onNeighborTileChange(EnumFacing facing) {
		doBlockUpdate();
	}

	@Override
	public List<ItemStack> getDrops() {
		return Arrays.asList(new ItemStack(TaamMain.itemMachine, 1, meta.metaData()));
	}

	@Override
	public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
		return new ItemStack(TaamMain.itemMachine, 1, meta.metaData());
	}

	private void doBlockUpdate() {
		byte occlusionField = 0;
		Collection<? extends IMultipart> parts = getContainer().getParts();
		for(EnumFacing side : EnumFacing.VALUES) {
			PartSlot slot = PartSlot.getFaceSlot(side);

			/*
			 * Physical occlusion
			 */
			if(!OcclusionHelper.occlusionTest(parts, this, MachinePipe.bbFaces[side.ordinal()])) {
				occlusionField = FaceBitmap.setSideBit(occlusionField, side);
				continue;
			}

			/*
			 * Check for face parts & if they are hollow (covers, panels, ..)
			 */
			ISlottedPart part = getContainer().getPartInSlot(slot);
			if (part instanceof IMicroblock.IFaceMicroblock) {
				IMicroblock.IFaceMicroblock faceMicro = (IMicroblock.IFaceMicroblock) part;
				// Only occluded if the face is NOT hollow
				if (!faceMicro.isFaceHollow()) {
					occlusionField = FaceBitmap.setSideBit(occlusionField, side);
				}
				continue;
				/*
				 * Last resort: slotted occluding parts
				 */
			} else if(OcclusionHelper.isSlotOccluded(parts, this, slot)) {
				occlusionField = FaceBitmap.setSideBit(occlusionField, side);
			}
		}

		machine.blockUpdate(getWorld(), getPos(), occlusionField);
		if(machine.renderUpdate(getWorld(), getPos())) {
			markRenderUpdate();
			sendUpdatePacket(true);
		}
	}

	@Override
	public boolean onActivated(EntityPlayer player, ItemStack heldItem, PartMOP hit) {
		boolean playerHasWrench = WrenchUtil.playerHasWrenchInHand(player);

		if (!playerHasWrench) {
			if(machine instanceof IWorldInteractable) {
				return ((IWorldInteractable) machine).onBlockActivated(getWorld(), player, false, hit.sideHit, (float)hit.hitVec.xCoord, (float)hit.hitVec.yCoord, (float)hit.hitVec.zCoord);
			}
			return false;
		}

		boolean playerIsSneaking = player.isSneaking();
		Log.debug("Wrenching multipart. Player is sneaking: {}", playerIsSneaking);

		if (playerIsSneaking) {
			ItemStack dropStack = getPickBlock(player, hit);
			InventoryUtils.tryDropToInventory(player, dropStack, getPos());
			getContainer().removePart(this);
			return true;
		}
		rotatePart(hit.sideHit);
		return true;
	}

	@Override
	public boolean rotatePart(EnumFacing axis) {
		if(machine instanceof IRotatable) {
			IRotatable rotatable = (IRotatable) machine;
			rotatable.setFacingDirection(rotatable.getNextFacingDirection());
			markDirty();
			markRenderUpdate();
			markLightingUpdate();
			return true;
		}
		return false;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state) {
		World world = getWorld();
		BlockPos pos = getPos();
		machine.renderUpdate(world, pos);

		IBlockState newState = state;
		List<String> visibleParts = null;
		if(machine instanceof IRenderable) {
			visibleParts = ((IRenderable) machine).getVisibleParts();

			if(visibleParts == null) {
				visibleParts = BaseBlock.ALL;
			}
			OBJModel.OBJState retState = new OBJModel.OBJState(visibleParts);
			retState.setIgnoreHidden(true);

			IExtendedBlockState extendedState = (IExtendedBlockState)state;
			newState = extendedState.withProperty(OBJModel.OBJProperty.instance, retState);
		}

		if(machine instanceof IRotatable) {
			newState = newState.withProperty(DIRECTION, ((IRotatable)machine).getFacingDirection()).withProperty(VARIANT, (Taam.MACHINE_META)meta);
		} else {
			newState = newState.withProperty(DIRECTION, EnumFacing.DOWN).withProperty(VARIANT, (Taam.MACHINE_META)meta);
		}
		return machine.getExtendedState(newState, world, pos);
	}

	@Override
	public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
		return layer == EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public BlockState createBlockState() {
		return new ExtendedBlockState(MCMultiPartMod.multipart,
				new IProperty[] { DIRECTION, VARIANT },
				new IUnlistedProperty[] { BlockMultipart.properties[0], OBJModel.OBJProperty.instance }
				);
	}

	@Override
	public String getModelPath() {
		return machine.getModelPath();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		String machineID = tag.getString("machine");
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if(meta != null && meta != this.meta) {
			this.meta = meta;
			machine = meta.createMachine();
			markRenderUpdate();
		}
		machine.readPropertiesFromNBT(tag);
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		String machineID = buf.readStringFromBuffer(30);
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if(meta != null && meta != this.meta) {
			this.meta = meta;
			machine = meta.createMachine();
			markRenderUpdate();
		}
		machine.readUpdatePacket(buf);
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		buf.writeString(meta.unlocalizedName());
		machine.writeUpdatePacket(buf);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("machine", meta.unlocalizedName());
		machine.writePropertiesToNBT(tag);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return machine.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		return machine.getCapability(capability, facing);
	}


	/*
	 * ITickable implementation
	 */

	@Override
	public void update() {
		machine.update(getWorld(), getPos());
	}

	/*
	 * ISlottedCapabilityProvider
	 */

	@Override
	public boolean hasCapability(Capability<?> capability, PartSlot slot, EnumFacing facing) {
		return machine.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, PartSlot slot, EnumFacing facing) {
		return machine.getCapability(capability, facing);
	}

	/*
	 * ISlottedPart implementation
	 */

	private static final EnumSet<PartSlot> slotSet = EnumSet.of(PartSlot.CENTER);

	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return slotSet;
	}

}
