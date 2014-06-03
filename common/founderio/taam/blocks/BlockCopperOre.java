package founderio.taam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockCopperOre extends Block {

	public BlockCopperOre() {
		super(Material.rock);
		this.setStepSound(Block.soundTypeStone);
		this.setHarvestLevel("pickaxe", 1);
	}
}
