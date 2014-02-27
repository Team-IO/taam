package founderio.taam.blocks;

import net.minecraft.block.material.Material;
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

}