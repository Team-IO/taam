package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.Taam;
import net.teamio.taam.content.ItemWithMetadata;
import net.teamio.taam.conveyors.api.IConveyorApplianceHost;
import net.teamio.taam.util.inv.InventoryUtils;

public class ItemConveyorAppliance extends ItemWithMetadata<Taam.ITEM_CONVEYOR_APPLIANCE_META> {

	public ItemConveyorAppliance() {
		super("conveyor_appliance", Taam.ITEM_CONVEYOR_APPLIANCE_META.values(), null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		par3List.add(EnumChatFormatting.DARK_GREEN + I18n.format("lore.taam.conveyor_appliance", new Object[0]));
		if (!GuiScreen.isShiftKeyDown()) {
			par3List.add(EnumChatFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
		} else {
			String usage = I18n.format("lore.taam.conveyor_appliance.usage", new Object[0]);
			//Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for(int i = 0;i < split.length; i++) {
				par3List.add(split[i]);
			}
		}
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ) {
		int i = stack.getItemDamage();
		Enum<?>[] values = Taam.ITEM_CONVEYOR_APPLIANCE_META.values();

		if (i < 0 || i >= values.length) {
			i = 0;
		}
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof IConveyorApplianceHost) {
			IConveyorApplianceHost host = (IConveyorApplianceHost) te;
			System.out.println("Item Setting up Appliance " + "taam." + values[i].name());
			boolean result = host.initAppliance("taam." + values[i].name());
			if(result) {
				System.out.println("Appliance set up.");
				if(!player.capabilities.isCreativeMode) {
					InventoryUtils.consumeItem(player.inventory, player.inventory.currentItem);
				}
				return true;
			} else {
				System.out.println("Appliance not set up.");
			}
		}
		return false;
	}
}
