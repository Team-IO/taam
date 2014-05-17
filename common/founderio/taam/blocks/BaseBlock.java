package founderio.taam.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class BaseBlock extends Block {

	public BaseBlock(Material material) {
		super(material);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLivingBase entity, ItemStack itemStack) {
		// Update Owner
		if (entity instanceof EntityPlayer) {
			BaseTileEntity te = (BaseTileEntity) world.getTileEntity(x, y, z);
			// TODO: Change to UUID
			te.setOwner(((EntityPlayer) entity).getDisplayName());
		}
	}

}
