package net.teamio.taam.content.common;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Config;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.PipeUtil;
import net.teamio.taam.util.FluidUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Debug Tool, currently used for debugging conveyors.
 *
 * @author founderio
 */
public class ItemDebugTool extends Item {

	public ItemDebugTool() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> tooltip, boolean detailInfo) {
		tooltip.add(TextFormatting.DARK_GREEN + I18n.format("lore.taam.debugtool"));
		if (GuiScreen.isShiftKeyDown()) {
			String usage = I18n.format("lore.taam.debugtool.usage");
			//Split at literal \n in the translated text. a lot of escaping here.
			Collections.addAll(tooltip, usage.split("\\\\n"));
		} else {
			tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("lore.taam.shift"));
		}
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!Config.debug_output) {
			worldIn.playSound(player, pos, TaamMain.soundSipAh, SoundCategory.BLOCKS, 1f, 1f);
			// return EnumActionResult.SUCCESS;
		}
		String remoteState = worldIn.isRemote ? "Client" : "Server";

		IBlockState state = worldIn.getBlockState(pos);

		String text = String.format("%s RS: %b Side: %s Weak: %d Strong: %d",
				remoteState, state.canProvidePower(), facing.toString(), state.getWeakPower(worldIn, pos, facing), state.getStrongPower(worldIn, pos, facing));

		player.sendStatusMessage(new TextComponentString(text));

		EnumFacing oppSide = facing.getOpposite();

		text = String.format("%s RS: %b Opposite Side: %s Weak: %d Strong: %d",
				remoteState, state.canProvidePower(), oppSide.toString(), state.getWeakPower(worldIn, pos, oppSide), state.getStrongPower(worldIn, pos, oppSide));

		player.sendStatusMessage(new TextComponentString(text));

		text = String.format("%s Powered: %b",
				remoteState, worldIn.isBlockPowered(pos));

		player.sendStatusMessage(new TextComponentString(text));

		boolean didSomething = false;

		TileEntity te = worldIn.getTileEntity(pos);
		if (te == null) {
			return EnumActionResult.PASS;
		}


		if (te instanceof TileEntityConveyor) {
			didSomething = true;
			TileEntityConveyor tec = (TileEntityConveyor) te;
			tec.updateContainingBlockInfo();

			text = String.format("%s Conveyor facing %s. isEnd: %b isBegin: %b",
					remoteState, tec.getFacingDirection().toString(), tec.isEnd, tec.isBegin);

			player.sendStatusMessage(new TextComponentString(text));

		}

		//if(te instanceof IConveyorApplianceHost) {
		//IConveyorApplianceHost host = (IConveyorApplianceHost)te;
		//}

		IPipe pipe = PipeUtil.getPipe(worldIn, pos, facing);

		if (pipe != null) {

			StringBuilder content = new StringBuilder(remoteState)
					.append(' ')
					.append(pipe.getClass().getName())
					.append(" Pipe pressure: ")
					.append(pipe.getPressure())
					.append(" Content: [");
			List<FluidStack> fs = pipe.getFluids();
			if (fs != null) {
				for (FluidStack fluidContent : fs) {
					content.append(fluidContent.getLocalizedName()).append(' ').append(fluidContent.amount).append(", ");
				}
			}
			content.append(']');

			player.sendStatusMessage(new TextComponentString(content.toString()));
		}

		IFluidHandler fh = FluidUtils.getFluidHandler(te, facing);

		if (fh != null) {

			IFluidTankProperties[] ti = fh.getTankProperties();
			StringBuilder content = new StringBuilder(remoteState)
					.append(" Content: [");
			if (ti.length > 0) {
				int capacity = ti[0].getCapacity();
				FluidStack contents = ti[0].getContents();
				if (contents == null) {
					content.append("Nothing 0/").append(capacity);
				} else {
					content.append(contents.getLocalizedName()).append(' ')
							.append(contents.amount).append('/').append(capacity);
				}
			} else {
				content.append("No Tanks Present");
			}
			content.append(']');

			player.sendStatusMessage(new TextComponentString(content.toString()));

			didSomething = true;
		}
		if (didSomething) {
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

}
