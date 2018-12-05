package net.teamio.taam.content.conveyors;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.ItemWithMetadata;

import java.util.Collections;
import java.util.List;

public class ItemConveyorAppliance extends ItemWithMetadata<Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META> {

	public ItemConveyorAppliance() {
		super("conveyor_appliance", Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.values(), null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean detailInfo) {

		list.add(TextFormatting.DARK_GREEN + I18n.format("lore.taam.conveyor_appliance"));
		if (!GuiScreen.isShiftKeyDown()) {
			list.add(TextFormatting.DARK_PURPLE + I18n.format("lore.taam.shift"));
		} else {
			String usage = I18n.format("lore.taam.conveyor_appliance.usage");
			//Split at literal \n in the translated text. a lot of escaping here.
			Collections.addAll(list, usage.split("\\\\n"));
		}
	}
}
