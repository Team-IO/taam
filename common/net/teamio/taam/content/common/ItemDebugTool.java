package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ) {
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

		IBlockState state = world.getBlockState(pos);
		Block clickedOn = state.getBlock();

    	String text = String.format(remoteState + " RS: %b Side: %s Weak: %d Strong: %d",
    			clickedOn.canProvidePower(), side.toString(), clickedOn.isProvidingWeakPower(world, pos, state, side), clickedOn.isProvidingStrongPower(world, pos, state, side));

    	player.addChatMessage(new ChatComponentText(text));
    	
    	EnumFacing oppSide = side.getOpposite();
    	
    	text = String.format(remoteState + " RS: %b Opposite Side: %s Weak: %d Strong: %d",
    			clickedOn.canProvidePower(), oppSide.toString(), clickedOn.isProvidingWeakPower(world, pos, state, oppSide), clickedOn.isProvidingStrongPower(world, pos, state, oppSide));
    	

    	text = String.format(remoteState + " Indirectly Powered: %d",
    	    	world.isBlockIndirectlyGettingPowered(pos));
    	
    	player.addChatMessage(new ChatComponentText(text));
		
		//EnumFacing dir = EnumFacing.getOrientation(side);
        //EnumFacing dirOpp = dir.getOpposite();

        //Vector3 localHit = new Vector3(hitx, hity, hitz);
        
		
        boolean didSomething = false;
        
        TileEntity te = world.getTileEntity(pos);

    	
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
