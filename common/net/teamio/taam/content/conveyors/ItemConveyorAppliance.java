package net.teamio.taam.content.conveyors;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.teamio.taam.Taam;
import net.teamio.taam.content.ItemWithMetadata;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ItemConveyorAppliance extends ItemWithMetadata<Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META> {

	public ItemConveyorAppliance() {
		super("conveyor_appliance", Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.values(), null);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

		tooltip.add(TextFormatting.DARK_GREEN + I18n.format("lore.taam.conveyor_appliance"));
		if (!GuiScreen.isShiftKeyDown()) {
			tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("lore.taam.shift"));
		} else {
			String usage = I18n.format("lore.taam.conveyor_appliance.usage");
			//Split at literal \n in the translated text. a lot of escaping here.
			Collections.addAll(tooltip, usage.split("\\\\n"));
		}
	}
}
