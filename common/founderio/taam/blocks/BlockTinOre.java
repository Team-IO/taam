package founderio.taam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockTinOre extends Block {

	public BlockTinOre() {
		super(Material.rock);
		this.setStepSound(Block.soundTypeStone);
		this.setHarvestLevel("pickaxe", 1);
	}
}
