package founderio.taam.blocks.multinet.cables;

import java.util.List;

import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import founderio.taam.multinet.AMultinetBlockAttachment;
import founderio.taam.multinet.IMultinetAttachment;
import founderio.taam.multinet.MultinetUtil;

public class RedstoneBlockAdapter extends AMultinetBlockAttachment {

	public RedstoneBlockAdapter(World world, int x, int y, int z,
			ForgeDirection face, ForgeDirection dir, int layer, String type) {
		super(world, x, y, z, face, dir, layer, type);
	}

	@Override
	public List<IMultinetAttachment> getIrregularAttachments() {
		return null;
	}
	
	/**
	 * 
	 * @param blockCoords
	 * @param blockface
	 * @param hit The face of the block that was hit, on which the cable should be placed on. (Cable face will be offosite of this!)
	 * @param part
	 */
	public void init(BlockCoord blockCoords, int hitface, Vector3 hit, String part) {
		
		this.type = part;
		
		ForgeDirection dir = ForgeDirection.getOrientation(hitface).getOpposite();
		this.face = dir;
		
		this.layer = MultinetUtil.getHitLayer(dir, hit);
	}

}
