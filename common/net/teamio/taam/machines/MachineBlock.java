package net.teamio.taam.machines;

import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.teamio.taam.content.BaseBlock;

public class MachineBlock extends BaseBlock implements ITileEntityProvider {

	private final IMachineMetaInfo[] values;

	public MachineBlock(Material material, IMachineMetaInfo[] values) {
		super(material);
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("Specified meta values were null or empty");
		}
		this.values = values;
	}

	public IMachineMetaInfo getInfo(int meta) {
		int ordinal = MathHelper.clamp_int(meta, 0, values.length);
		return values[ordinal];
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		IMachineMetaInfo info = getInfo(meta);

		IMachine machine = info.createMachine();
		
		//TODO: distinguish between IMachine and IMachineWithSpecialRenderer later
		
		return new MachineTileEntity(machine);
	}

	@Override
	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}
	
	@Override
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		MachineTileEntity tileEntity = (MachineTileEntity)worldIn.getTileEntity(pos);
		
		tileEntity.machine.addCollisionBoxes(mask, list, collidingEntity);
	}

}
