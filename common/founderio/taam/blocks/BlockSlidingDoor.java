package founderio.taam.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockSlidingDoor extends BaseBlock {

	public BlockSlidingDoor(int par1) {
		super(par1, Material.iron);
		this.setHardness(3.5f);
		this.setStepSound(soundMetalFootstep);
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 1);
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
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntitySlidingDoor();
	}

	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z) {
		return false;
	}

}