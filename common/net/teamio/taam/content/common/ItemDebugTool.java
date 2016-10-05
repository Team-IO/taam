package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.conveyors.IConveyorApplianceHost;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.util.FluidUtils;

/**
 * Debug Tool, currently used for debugging conveyors.
 *
 * @author founderio
 *
 */
public class ItemDebugTool extends Item {

	public ItemDebugTool() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
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
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(!Config.debug_output)
		{
			worldIn.playSoundEffect(playerIn.posX, playerIn.posY + 1f, playerIn.posZ, "taam:sip_ah", 1f, 1f);
			// return EnumActionResult.SUCCESS;
		}
		char remoteState = worldIn.isRemote ? 'C' : 'S';

		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();

		String text = String.format(remoteState + " RS: %b Side: %s Weak: %d Strong: %d",
				block.canProvidePower(), facing.toString(), block.getWeakPower(worldIn, pos, state, facing), block.getStrongPower(worldIn, pos, state, facing));

		playerIn.addChatMessage(new ChatComponentText(text));

		EnumFacing oppSide = facing.getOpposite();

		text = String.format(remoteState + " RS: %b Opposite Side: %s Weak: %d Strong: %d",
				block.canProvidePower(), oppSide.toString(), block.getWeakPower(worldIn, pos, state, oppSide), block.getStrongPower(worldIn, pos, state, oppSide));


		text = String.format(remoteState + " Indirectly Powered: %d",
				worldIn.isBlockIndirectlyGettingPowered(pos));

		playerIn.addChatMessage(new ChatComponentText(text));

		boolean didSomething = false;

		TileEntity te = worldIn.getTileEntity(pos);


		if(te instanceof TileEntityConveyor) {
			didSomething = true;
			TileEntityConveyor tec = (TileEntityConveyor) te;
			tec.updateContainingBlockInfo();

			text = String.format(remoteState + " Conveyor facing %s. isEnd: %b isBegin: %b",
					tec.getFacingDirection().toString(), tec.isEnd, tec.isBegin);

			playerIn.addChatMessage(new ChatComponentText(text));

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

			playerIn.addChatMessage(new ChatComponentText(text));
		}

		IFluidHandler fh = FluidUtils.getFluidHandler(te, facing);

		if (fh != null) {

			FluidTankInfo[] ti = fh.getTankInfo(facing);
			String content = "";
			if (ti.length > 0) {
				int capacity = ti[0].capacity;
				FluidStack contents = ti[0].fluid;
				if (contents == null) {
					content = "Nothing 0/" + capacity;
				} else {
					content = contents.getLocalizedName() + " " + contents.amount + "/" + capacity;
				}
			}

			text = String.format(remoteState + " Content: %s", content);

			playerIn.addChatMessage(new ChatComponentText(text));
			
			didSomething = true;
		}
		if (didSomething) {
			return true;
		}
		return false;
	}

}
