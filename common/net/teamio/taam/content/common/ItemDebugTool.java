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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.piping.IPipe;

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
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean detailInfo) {

		list.add(EnumChatFormatting.DARK_GREEN + I18n.format("lore.taam.debugtool", new Object[0]));
		if (GuiScreen.isShiftKeyDown()) {
			String usage = I18n.format("lore.taam.debugtool.usage", new Object[0]);
			//Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for(int i = 0;i < split.length; i++) {
				list.add(split[i]);
			}
		} else {
			list.add(EnumChatFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
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
    			clickedOn.canProvidePower(), side.toString(), clickedOn.getWeakPower(world, pos, state, side), clickedOn.getStrongPower(world, pos, state, side));

    	player.addChatMessage(new ChatComponentText(text));
    	
    	EnumFacing oppSide = side.getOpposite();
    	
    	text = String.format(remoteState + " RS: %b Opposite Side: %s Weak: %d Strong: %d",
    			clickedOn.canProvidePower(), oppSide.toString(), clickedOn.getWeakPower(world, pos, state, oppSide), clickedOn.getStrongPower(world, pos, state, oppSide));
    	

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
        	//IConveyorApplianceHost host = (IConveyorApplianceHost)te;
        }
        
        if(te instanceof IPipe) {
        	
        	IPipe pipe = (IPipe)te;
        	
        	String content = "[";
        	FluidStack[] fs = pipe.getFluids();
        	for(FluidStack fluidContent : fs) {
        		content += fluidContent.getLocalizedName() + " " + fluidContent.amount + ", ";
        	}
        	content += "]";
        	
        	text = String.format(remoteState + " %s Pipe pressure: %d suction: %d effective: %d Content: %s",
        			pipe.getClass().getName(), pipe.getPressure(), pipe.getSuction(), pipe.getPressure() - pipe.getSuction(), content);

        	player.addChatMessage(new ChatComponentText(text));
        }
        
        if(te instanceof IFluidHandler) {
        	
        	IFluidHandler fh = (IFluidHandler)te;
        	
        	FluidTankInfo[] ti = fh.getTankInfo(EnumFacing.UP);
        	String content = "";
        	if(ti.length > 0) {
        		if(ti[0].fluid == null) {
        			content = "Nothing 0/" + ti[0].capacity;
        		} else {
        			content = ti[0].fluid.getLocalizedName() + " " + ti[0].fluid.amount + "/" + ti[0].capacity;
        		}
        	}
        	
        	text = String.format(remoteState + " Content: %s",
        			content);

        	player.addChatMessage(new ChatComponentText(text));
        }
		
        return !didSomething;
	}

}
