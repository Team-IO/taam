package founderio.taam.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;
import founderio.taam.blocks.multinet.MultinetCable;
import founderio.taam.multinet.MultinetUtil;

public class ItemDebugTool extends Item {

	public ItemDebugTool() {
		super();
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
	}
	
	private MultinetCable cableA;
	private MultinetCable cableB;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
	{
	par3List.add(Taam.LORE_DEBUG_NO_SHIFT);
	par3List.add(Taam.LORE_HOLD_SHIFT);
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack,
			EntityPlayer player, World world,
			int x, int y, int z,
			int side,
			float hitx, float hity, float hitz) {
		
		if(world.isRemote) {
			return true;
		}
		
		ForgeDirection dir = ForgeDirection.getOrientation(side);
        ForgeDirection dirOpp = dir.getOpposite();

        Vector3 localHit = new Vector3(hitx, hity, hitz);
        
        int layer = MultinetUtil.getHitLayer(dirOpp, localHit);
		
        MultinetCable cable = MultinetUtil.getCable(world, new BlockCoord(x, y, z), layer, dirOpp, null);
        
        if(cableA == null) {
        	cableA = cable;
        	System.out.println(cableA);
        } else if(cableA == cable) {
        	//cableA = null;
        } else {
        	cableB = cable;
        	System.out.println(cableB);
        	if(cableB != null) {
	        	System.out.println(MultinetUtil.findConnection(cableA, cableB));
	        	cableA = null;
	        	cableB = null;
        	}
        }
        
        return true;
	}

}
