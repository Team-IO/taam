package founderio.taam.multinet;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.vec.BlockCoord;

public interface IMultinetAttachment {
	boolean canAttach(ForgeDirection face, ForgeDirection dir, int layer,
			String type);

	BlockCoord getCoordinates();
	
	int getLayer();
	
	ForgeDirection getFace();
	
	String getCableType();
	
	World getDimension();
	
	void setNetwork(Multinet network);
	
	Multinet getNetwork();
	
	boolean isAvailable();
	
	/**
	 * Get Attachments that are different from regular block side attachments, like inter-layer connections or wireless connections.
	 * @return
	 */
	List<IMultinetAttachment> getIrregularAttachments();
}
