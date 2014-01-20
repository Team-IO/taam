package founderio.taam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BaseBlock extends Block {

	public BaseBlock(int par1, Material par2Material) {
		super(par1, par2Material);
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4,
			EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		// Update Owner
		if (par5EntityLivingBase instanceof EntityPlayer) {
			BaseTileEntity te = ((BaseTileEntity) par1World
					.getBlockTileEntity(par2, par3, par4));
			te.setOwner(((EntityPlayer) par5EntityLivingBase).username);
		}
	}
	
}
