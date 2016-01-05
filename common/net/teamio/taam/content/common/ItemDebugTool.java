package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.conveyors.IConveyorAppliance;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;

/**
 * Debug Tool, currently used for debugging conveyors.
 * 
 * @author founderio
 *
 */
public class ItemDebugTool extends Item {

	public ItemDebugTool() {
		super();
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		par3List.add(EnumChatFormatting.DARK_GREEN + I18n.format("lore.taam.debugtool", new Object[0]));
		if (GuiScreen.isShiftKeyDown()) {
			String usage = I18n.format("lore.taam.debugtool.usage", new Object[0]);
			//Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for(int i = 0;i < split.length; i++) {
				par3List.add(split[i]);
			}
		} else {
			par3List.add(EnumChatFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack,
			EntityPlayer player, World world,
			int x, int y, int z,
			int side,
			float hitx, float hity, float hitz) {

		if(!Config.debug)
		{
			//TODO: Clarify!
			//if(!world.isRemote) {
			world.playSoundEffect(player.posX, player.posY + 1, player.posZ,
					"taam:sip_ah", 1, 1);
			//}
			return true;
		}
		char remoteState = world.isRemote ? 'C' : 'S';

		Block clickedOn = world.getBlock(x, y, z);
		

    	String text = String.format(remoteState + " RS: %b Side: %s Weak: %d Strong: %d",
    			clickedOn.canProvidePower(), ForgeDirection.getOrientation(side).toString(), clickedOn.isProvidingWeakPower(world, x, y, z, side), clickedOn.isProvidingStrongPower(world, x, y, z, side));

    	player.addChatMessage(new ChatComponentText(text));
    	
    	int oppSide = ForgeDirection.getOrientation(side).getOpposite().ordinal();
    	
    	text = String.format(remoteState + " RS: %b Opposite Side: %s Weak: %d Strong: %d",
    			clickedOn.canProvidePower(), ForgeDirection.getOrientation(oppSide).toString(), clickedOn.isProvidingWeakPower(world, x, y, z, oppSide), clickedOn.isProvidingStrongPower(world, x, y, z, oppSide));
    	

    	text = String.format(remoteState + " Indirectly Powered: %b with %d",
    	    	world.isBlockIndirectlyGettingPowered(x, y, z), world.getBlockPowerInput(x, y, z));
    	
    	player.addChatMessage(new ChatComponentText(text));
		
		//ForgeDirection dir = ForgeDirection.getOrientation(side);
        //ForgeDirection dirOpp = dir.getOpposite();

        //Vector3 localHit = new Vector3(hitx, hity, hitz);
        
		
        boolean didSomething = false;
        
        TileEntity te = world.getTileEntity(x, y, z);

    	
        if(te instanceof TileEntityConveyor) {
        	didSomething = true;
        	TileEntityConveyor tec = (TileEntityConveyor) te;
        	tec.updateContainingBlockInfo();
        	
        	text = String.format(remoteState + " Conveyor facing %s. isEnd: %b isBegin: %b",
        			tec.getFacingDirection().toString(), tec.isEnd, tec.isBegin);

        	player.addChatMessage(new ChatComponentText(text));

        }
        
        if(te instanceof IConveyorApplianceHost) {
        	IConveyorApplianceHost host = (IConveyorApplianceHost)te;
        	IConveyorAppliance appliance = host.getAppliance();
        	String applianceType = host.getApplianceType();

        	if(appliance == null) {
        		text = String.format(remoteState + " Appliance Type: %s Appliance is null. ", applianceType);
        	} else {
        		text = String.format(remoteState + " Appliance Type: %s Appliance: %s", applianceType, String.valueOf(appliance));
        	}
        	player.addChatMessage(new ChatComponentText(text));
        }
		
        return !didSomething;
	}

}
