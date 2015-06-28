package founderio.taam.content.multinet.cables;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.IRedstonePart;
import founderio.taam.Taam;
import founderio.taam.content.multinet.MultinetMultipart;
import founderio.taam.logistics.WorldCoord;
import founderio.taam.multinet.IMultinetAttachment;
import founderio.taam.multinet.MultinetUtil;

public class RedstoneBlockAdapter extends MultinetMultipart implements IRedstonePart {

	

	public RedstoneBlockAdapter() {
		super("redstone");
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
	public void init(BlockCoord blockCoords, int hitface, Vector3 hit) {
		
		this.cableType = "redstone";
		
		ForgeDirection dir = ForgeDirection.getOrientation(hitface).getOpposite();
		this.face = dir;
		
		this.layer = MultinetUtil.getHitLayer(dir, hit);
	}

	@Override
	public boolean canAttach(WorldCoord coords, ForgeDirection face, ForgeDirection dir,
			int layer, String type) {
		//TODO: Actually check for occlusion!
		return face == this.face && layer == this.layer && coords.equals(getCoordinates());
	}
	
	@Override
	protected void saveProperties(NBTTagCompound tag) {
		
	}

	@Override
	protected void loadProperties(NBTTagCompound tag) {
		
	}

	@Override
	protected void saveProperties(MCDataOutput packet) {
		
	}

	@Override
	protected void loadProperties(MCDataInput packet) {
		
	}

	@Override
	public boolean canStay(World world, int x, int y, int z, ForgeDirection side) {
		return MultinetUtil.canCableStay(world, x, y, z, side);
	}

	@Override
	public String getType() {
		return Taam.MULTIPART_MULTINET_MULTITRONIX + ".redstone_block_attachment";
	}

	@Override
	public boolean canConnectRedstone(int arg0) {
		return arg0 == this.face.ordinal();
	}

	@Override
	public int strongPowerLevel(int arg0) {
		OperatorRedstone operator = (OperatorRedstone) network.operator;
		return operator.getPowerLevel();
	}

	@Override
	public int weakPowerLevel(int arg0) {
		OperatorRedstone operator = (OperatorRedstone) network.operator;
		return operator.getPowerLevel();
	}

	@Override
	public void updateAttachmentState() {
		tile().notifyPartChange(this);
	}
	
	//TODO: Rendering
	
	//TODO: Redstone input

}
