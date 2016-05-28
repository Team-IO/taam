package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.util.TaamUtil;

public class ItemAppliance extends ItemMultiTexture {

	public ItemAppliance(Block block, String[] names) {
		super(block, block, names);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, IBlockState newState) {
		EnumFacing dir = side.getOpposite();
		if (TaamUtil.canAttachAppliance(world, pos, dir)) {
			boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
			if (success) {
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof IRotatable) {
					((IRotatable) te).setFacingDirection(dir);
				}
			}
			return success;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean detailInfo) {

		list.add(TextFormatting.DARK_GREEN + I18n.format("lore.taam.conveyor_appliance", new Object[0]));
		if (!GuiScreen.isShiftKeyDown()) {
			list.add(TextFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
		} else {
			String usage = I18n.format("lore.taam.conveyor_appliance.usage", new Object[0]);
			// Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for (int i = 0; i < split.length; i++) {
				list.add(split[i]);
			}
		}
	}

}
