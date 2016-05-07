package net.teamio.taam.machines;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.block.BlockMultipart;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Log;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.MACHINE_META;
import net.teamio.taam.content.BaseBlock;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.content.MaterialMachinesTransparent;

public class MachineBlock extends BaseBlock implements ITileEntityProvider {

	private final IMachineMetaInfo[] values;

	public static final PropertyEnum<Taam.MACHINE_META> VARIANT = PropertyEnum.create("variant", Taam.MACHINE_META.class);
	public static final PropertyEnum<EnumFacing> DIRECTION = PropertyEnum.create("direction", EnumFacing.class);
	
	public MachineBlock(IMachineMetaInfo[] values) {
		super(MaterialMachinesTransparent.INSTANCE);
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("Specified meta values were null or empty");
		}
		this.values = values;
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		// No meta for this block
		return 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		// No meta for this block
		return getDefaultState().withProperty(VARIANT, (Taam.MACHINE_META)getInfo(meta));
	}
	
	@Override
	protected BlockState createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { DIRECTION, VARIANT }, new IUnlistedProperty[]{BlockMultipart.properties[0], OBJModel.OBJProperty.instance});
	};
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	};
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		MachineTileEntity te = (MachineTileEntity) worldIn.getTileEntity(pos);
		Taam.MACHINE_META vari = (Taam.MACHINE_META)te.meta;
		if(vari == null) {
			Log.warn("Replacing NULL variant with 'pipe' to avoid crashes. {}", pos);
			vari = MACHINE_META.pipe;
		}
		IBlockState newState;
		if(te.machine instanceof IRotatable) {
			newState = state.withProperty(DIRECTION, ((IRotatable)te.machine).getFacingDirection()).withProperty(VARIANT, vari);
		} else {
			newState = state.withProperty(DIRECTION, EnumFacing.DOWN).withProperty(VARIANT, vari);
		}
		return te.machine.getExtendedState(newState, worldIn, pos);
	}

	public IMachineMetaInfo getInfo(int meta) {
		int ordinal = MathHelper.clamp_int(meta, 0, values.length);
		return values[ordinal];
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new MachineTileEntity();
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}
	
	private final ThreadLocal<List<AxisAlignedBB>> tempList = new ThreadLocal<List<AxisAlignedBB>>() {
		@Override
		protected List<AxisAlignedBB> initialValue() {
			return new ArrayList<AxisAlignedBB>(6);
		}
	};
	private AxisAlignedBB closestBB; 
	
	@Override
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		MachineTileEntity tileEntity = (MachineTileEntity)worldIn.getTileEntity(pos);
		
		List<AxisAlignedBB> tempList = this.tempList.get();
		tempList.clear();
		
		tileEntity.machine.addCollisionBoxes(mask.offset(-pos.getX(), -pos.getY(), -pos.getZ()), tempList, collidingEntity);
		for(int i = 0; i < tempList.size(); i++) {
			list.add(tempList.get(i).offset(pos.getX(), pos.getY(), pos.getZ()));
		}
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		start = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		end = end.subtract(pos.getX(), pos.getY(), pos.getZ());

		List<AxisAlignedBB> tempList = this.tempList.get();
		tempList.clear();
		
		MachineTileEntity tileEntity = (MachineTileEntity)world.getTileEntity(pos);
		tileEntity.machine.addSelectionBoxes(tempList);
		
		MovingObjectPosition closestHit = null;
		double dist = Double.MAX_VALUE;
		closestBB = null;

		for (AxisAlignedBB box : tempList) {

			MovingObjectPosition newHit = box.calculateIntercept(start, end);
			if (newHit != null) {
				double newDist = newHit.hitVec.distanceTo(start);
				if (newDist < dist) {
					closestHit = newHit;
					dist = newDist;
					closestBB = box;
				}
			}
		}
		if (closestHit == null) {
			return null;
		} else {
			return new MovingObjectPosition(closestHit.hitVec, closestHit.sideHit, pos);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
		// Get player position + look vector
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		Vec3 eyes = player.getPositionEyes(0);
		Vec3 look = player.getLook(0);
		float reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3 dest = eyes.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);

		// in that method, we update the closestBB
		collisionRayTrace(world, pos, eyes, dest);

		// Return the box that is hovered, or the default if nothing could be
		// determined (edge cases)
		if (closestBB == null) {
			return new AxisAlignedBB(pos, pos.add(1, 1, 1));
		} else {
			return closestBB.offset(pos.getX(), pos.getY(), pos.getZ());
		}
	}

}
