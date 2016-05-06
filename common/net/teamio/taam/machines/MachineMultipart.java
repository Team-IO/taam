package net.teamio.taam.machines;

import java.util.List;

import mcmultipart.MCMultiPartMod;
import mcmultipart.block.BlockMultipart;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IOccludingPart;
import mcmultipart.multipart.Multipart;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.IRenderable;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.util.WrenchUtil;
import net.teamio.taam.util.inv.InventoryUtils;

public class MachineMultipart extends Multipart implements IOccludingPart, ITickable {
	public IMachine machine;
	private IMachineMetaInfo meta;

	public static final PropertyEnum<Taam.MACHINE_META> VARIANT = PropertyEnum.create("variant", Taam.MACHINE_META.class);
	public static final PropertyEnum<EnumFacing> DIRECTION = PropertyEnum.create("direction", EnumFacing.class);

	public MachineMultipart() {
	}
	
	public MachineMultipart(IMachineMetaInfo meta) {
		this.meta = meta;
		this.machine = meta.createMachine();
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
		machine.blockUpdate(getWorld(), getPos());
		if(machine.renderUpdate(getWorld(), getPos())) {
			markRenderUpdate();
			sendUpdatePacket(true);
		}
	}
	
	@Override
	public void onNeighborBlockChange(Block block) {
		machine.blockUpdate(getWorld(), getPos());
		if(machine.renderUpdate(getWorld(), getPos())) {
			markRenderUpdate();
			sendUpdatePacket(true);
		}
	}
	
	@Override
	public void onNeighborTileChange(EnumFacing facing) {
		machine.blockUpdate(getWorld(), getPos());
		if(machine.renderUpdate(getWorld(), getPos())) {
			markRenderUpdate();
			sendUpdatePacket(true);
		}
	}
	
	@Override
	public boolean onActivated(EntityPlayer player, ItemStack stack, PartMOP hit) {
		
		boolean playerHasWrench = WrenchUtil.playerHasWrench(player);

		if (!playerHasWrench) {
			return false;
		}

		boolean playerIsSneaking = player.isSneaking();
		Log.debug("Wrenching block. Player is sneaking: {}", playerIsSneaking);

		if (playerIsSneaking) {
			ItemStack dropStack = this.getPickBlock(player, hit);
			InventoryUtils.tryDropToInventory(player, dropStack, getPos());
			this.getContainer().removePart(this);
			return true;
		} else {
			rotatePart(hit.sideHit);
			return true;
		}
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
			OBJModel.OBJState retState = new OBJModel.OBJState(visibleParts, true, new TRSRTransformation(EnumFacing.SOUTH));

			IExtendedBlockState extendedState = (IExtendedBlockState)state;
			newState = extendedState.withProperty(OBJModel.OBJProperty.instance, retState);
		}
		
		if(machine instanceof IRotatable) {
			newState = newState.withProperty(DIRECTION, ((IRotatable)machine).getFacingDirection()).withProperty(VARIANT, (Taam.MACHINE_META)meta);
		} else {
			newState = newState.withProperty(VARIANT, (Taam.MACHINE_META)meta);
		}
		return machine.getExtendedState(newState, world, pos);
	}
	
	@Override
	public boolean canRenderInLayer(EnumWorldBlockLayer layer) {
		return layer == EnumWorldBlockLayer.CUTOUT;
	}
	
	@Override
	public BlockState createBlockState() {
		if(machine instanceof IRotatable) {
			return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[] { DIRECTION, VARIANT }, new IUnlistedProperty[]{BlockMultipart.properties[0], OBJModel.OBJProperty.instance});
		} else {
			return new ExtendedBlockState(MCMultiPartMod.multipart, new IProperty[] { VARIANT }, new IUnlistedProperty[]{BlockMultipart.properties[0], OBJModel.OBJProperty.instance});
		}
	}
	
	@Override
	public String getModelPath() {
		return machine.getModelPath();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		String machineID = tag.getString("machine");
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if(meta != null) {
			this.meta = meta;
			machine = meta.createMachine();
		}
		machine.readPropertiesFromNBT(tag);
	}
	
	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		String machineID = buf.readStringFromBuffer(30);
		IMachineMetaInfo meta = Taam.MACHINE_META.fromId(machineID);
		if(meta != null) {
			this.meta = meta;
			machine = meta.createMachine();
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

}
