package founderio.taam.multinet;

import java.util.List;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import founderio.taam.util.WorldCoord;

public interface IMultinetAttachment {
	boolean canAttach(WorldCoord coords, ForgeDirection face,
			ForgeDirection dir, int layer, String type);

	WorldCoord getCoordinates();

	int getLayer();

	ForgeDirection getFace();

	String getCableType();

	World getDimension();

	void setNetwork(Multinet network);

	Multinet getNetwork();

	boolean isAvailable();

	/**
	 * Get Attachments that are different from regular block side attachments,
	 * like inter-layer connections or wireless connections.
	 * 
	 * @return
	 */
	List<IMultinetAttachment> getIrregularAttachments();

	void updateAttachmentState();
}
