package founderio.taam.items;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Config;
import founderio.taam.Taam;
import founderio.taam.blocks.TileEntityConveyor;
import founderio.taam.blocks.multinet.MultinetCable;
import founderio.taam.multinet.MultinetUtil;

public class ItemDebugTool extends Item {

	public ItemDebugTool() {
		super();
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setTextureName(Taam.MOD_ID + ":coffee");
	}
	
	private MultinetCable cableA;
	private MultinetCable cableB;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		if (!GuiScreen.isShiftKeyDown()) {
			par3List.add("This is our Debug Tool");
			par3List.add("Hold Shift");
		} else {

			par3List.add("This item can't be obtained ");
			par3List.add("in Survival.");
			par3List.add("It prints debug information");
			par3List.add("into the chat or console.");
		}

	}
		
	@Override
	public boolean onItemUse(ItemStack itemStack,
			EntityPlayer player, World world,
			int x, int y, int z,
			int side,
			float hitx, float hity, float hitz) {

//		if(world.isRemote) {
//			return true;
//		}

		if(!Config.debug)
		{
			world.playSound(player.posX ,player.posY ,player.posZ ,"random.drink", 1, 1, false);
//			return true;
		}
					
		ForgeDirection dir = ForgeDirection.getOrientation(side);
        ForgeDirection dirOpp = dir.getOpposite();

        Vector3 localHit = new Vector3(hitx, hity, hitz);
        
        int layer = MultinetUtil.getHitLayer(dirOpp, localHit);
		
        boolean didSomething = false;
        
        MultinetCable cable = MultinetUtil.getCable(world, new BlockCoord(x, y, z), layer, dirOpp, null);
        
        if(cable != null) {
        	didSomething = true;
            if(cableA == null) {
            	player.addChatMessage(new ChatComponentText("Selected cable A"));
            	cableA = cable;
            	System.out.println(cableA);
            } else if(cableA == cable) {
            	player.addChatMessage(new ChatComponentText("You already selected this cable as cable A"));
            	//cableA = null;
            } else {
            	cableB = cable;
            	player.addChatMessage(new ChatComponentText("Selected cable B"));
	        	System.out.println(MultinetUtil.findConnection(cableA, cableB));
	        	cableA = null;
	        	cableB = null;
            }
        }
        
        TileEntity te = world.getTileEntity(x, y, z);
        
        if(te instanceof TileEntityConveyor) {
        	didSomething = true;
        	TileEntityConveyor tec = (TileEntityConveyor) te;
        	tec.updateContainingBlockInfo();
        	player.addChatMessage(new ChatComponentText(String.format("Conveyor facing %s. isEnd: %b isBegin: %b", tec.getFacingDirection().toString(), tec.isEnd(), tec.isBegin())));
        }
        if(didSomething && !world.isRemote) {
        	world.playSound(player.posX ,player.posY ,player.posZ ,"random.drink", 1, 1, false);
        }
		
        return !didSomething;
	}

}
