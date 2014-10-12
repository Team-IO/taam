package founderio.taam.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import founderio.taam.Taam;
import founderio.taam.blocks.TileEntityConveyor;

public class ItemConveyorAppliance extends ItemWithMetadata {

	public ItemConveyorAppliance() {
		super("conveyor_appliance", Taam.ITEM_CONVEYOR_APPLIANCE_META);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		return false;
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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		int i = stack.getItemDamage();

		if (i < 0 || i >= Taam.ITEM_CONVEYOR_APPLIANCE_META.length) {
			i = 0;
		}
		TileEntity te = world.getTileEntity(x, y, z);
		//TODO: Change to ConveyorApplianceHost once ready
		if(te instanceof TileEntityConveyor) {
			System.out.println("Item Setting up Appliance");
			((TileEntityConveyor) te).initAppliance("taam." + Taam.ITEM_CONVEYOR_APPLIANCE_META[i]);
			((TileEntityConveyor) te).updateState();
			return true;
		}
		return false;
	}
}
