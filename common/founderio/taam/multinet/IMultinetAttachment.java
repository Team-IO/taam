package founderio.taam.multinet;

import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.vec.BlockCoord;

public interface IMultinetAttachment {
	boolean canAttach(ForgeDirection face, ForgeDirection dir, int layer,
			String type);

	BlockCoord getCoordinates();
	
	World getDimension();
}
