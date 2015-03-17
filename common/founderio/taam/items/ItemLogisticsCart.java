package founderio.taam.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import codechicken.lib.inventory.InventoryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;
import founderio.taam.entities.EntityLogisticsCart;

public class ItemLogisticsCart extends ItemWithMetadata {

	public ItemLogisticsCart() {
		super("logistics_cart", Taam.ITEM_LOGISTICS_CART_META);
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

//		par3List.add(EnumChatFormatting.DARK_GREEN + I18n.format("lore.taam.conveyor_appliance", new Object[0]));
//		if (!GuiScreen.isShiftKeyDown()) {
//			par3List.add(EnumChatFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
//		} else {
//			String usage = I18n.format("lore.taam.conveyor_appliance.usage", new Object[0]);
//			//Split at literal \n in the translated text. a lot of escaping here.
//			String[] split = usage.split("\\\\n");
//			for(int i = 0;i < split.length; i++) {
//				par3List.add(split[i]);
//			}
//		}
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		int i = stack.getItemDamage();

		if (i < 0 || i >= Taam.ITEM_LOGISTICS_CART_META.length) {
			i = 0;
		}
		
		EntityLogisticsCart cart = new EntityLogisticsCart(world);
		cart.setPosition(x, y + 1, z);
		
		if(!world.isRemote) {
			world.spawnEntityInWorld(cart);
		}
		
		if(!player.capabilities.isCreativeMode) {
			InventoryUtils.consumeItem(player.inventory, player.inventory.currentItem);
		}
		
		return true;
	}
}
