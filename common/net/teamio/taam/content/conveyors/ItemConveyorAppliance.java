package net.teamio.taam.content.conveyors;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.ItemWithMetadata;

public class ItemConveyorAppliance extends ItemWithMetadata<Taam.ITEM_CONVEYOR_APPLIANCE_META> {

	public ItemConveyorAppliance() {
		super("conveyor_appliance", Taam.ITEM_CONVEYOR_APPLIANCE_META.values(), null);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean detailInfo) {

		list.add(ChatFormatting.DARK_GREEN + I18n.format("lore.taam.conveyor_appliance", new Object[0]));
		if (!GuiScreen.isShiftKeyDown()) {
			list.add(ChatFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
		} else {
			String usage = I18n.format("lore.taam.conveyor_appliance.usage", new Object[0]);
			//Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for(int i = 0;i < split.length; i++) {
				list.add(split[i]);
			}
		}
	}
}
