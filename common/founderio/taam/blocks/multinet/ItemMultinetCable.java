package founderio.taam.blocks.multinet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.TMultiPart;
import founderio.taam.blocks.multinet.cables.CableRedstone;

public class ItemMultinetCable extends JItemMultiPart {

	public static final List<String> cables;
	
	static {
		
		cables = new ArrayList<String>();
		cables.add("redstone");
		//TODO: More cable types
	}
	
	public ItemMultinetCable(int id) {
		super(id);
	}

	@Override
	public TMultiPart newPart(ItemStack itemStack, EntityPlayer player, World world,
			BlockCoord blockCoords, int face, Vector3 hit) {
		
		int dmg = itemStack.getItemDamage();
		
		if(dmg < 0 || dmg > cables.size()) {
			return null;
		} else {
			
			
			if(MultinetCable.canStay(world, blockCoords.x, blockCoords.y, blockCoords.z, ForgeDirection.getOrientation(face).getOpposite())) {
				MultinetCable cable;
				
				//TODO: Factories (for flexibility, plugins, etc.)
				switch(dmg) {
				case 0:
					cable = new CableRedstone();
					break;
				default:
					return null;
				}
				
				cable.init(blockCoords, face, hit, cables.get(dmg));
				
				return cable;
			} else {
				return null;
			}
			
		}
	}
	
}
