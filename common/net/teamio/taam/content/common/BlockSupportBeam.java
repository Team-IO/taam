package net.teamio.taam.content.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamClientProxy;
import net.teamio.taam.content.MaterialMachinesTransparent;

public class BlockSupportBeam extends Block {

	protected String baseName;
	
	public BlockSupportBeam() {
		super(MaterialMachinesTransparent.INSTANCE);
		this.setStepSound(Block.soundTypeMetal);
		this.setHarvestLevel("pickaxe", 1);
		this.setResistance(3.7f);
		this.setHardness(2);
		this.setBlockTextureName(Taam.MOD_ID + ":support_beam");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return TaamClientProxy.blockRendererId;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return true;
	}

}