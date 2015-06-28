package net.teamio.taam.content.multinet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.teamio.taam.content.multinet.cables.RedstoneBlockAdapter;
import net.teamio.taam.multinet.MultinetUtil;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.JItemMultiPart;
import codechicken.multipart.TMultiPart;

public class ItemMultinetMultitronix extends JItemMultiPart {

	public static final List<String> multitronix;
	
	static {
		
		multitronix = new ArrayList<String>();
		multitronix.add("redstone_block_attachment");
		//TODO: More multitronix types
	}
	
	public ItemMultinetMultitronix() {
		super();
	}

	@Override
	public TMultiPart newPart(ItemStack itemStack, EntityPlayer player, World world,
			BlockCoord blockCoords, int face, Vector3 hit) {
		
		int dmg = itemStack.getItemDamage();
		
		if(dmg < 0 || dmg > multitronix.size()) {
			return null;
		} else {
			
			if(MultinetUtil.canCableStay(world, blockCoords.x, blockCoords.y, blockCoords.z, ForgeDirection.getOrientation(face).getOpposite())) {
				MultinetMultipart part;
				
				//TODO: Factories (for flexibility, plugins, etc.)
				switch(dmg) {
				case 0:
					part = new RedstoneBlockAdapter();
					break;
				default:
					return null;
				}
				
				part.init(blockCoords, face, hit);
				
				return part;
			} else {
				return null;
			}
			
		}
	}
	
}
