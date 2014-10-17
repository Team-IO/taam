package founderio.taam.items;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;
import founderio.taam.blocks.TileEntityConveyor;
import founderio.taam.blocks.multinet.MultinetCable;

public class ItemWrench extends Item {

	public ItemWrench() {
		super();
		this.setMaxStackSize(1);
		this.setMaxDamage(0);
		this.setTextureName(Taam.MOD_ID + ":wrench");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack,
			EntityPlayer par2EntityPlayer, List par3List, boolean par4) {

		par3List.add(EnumChatFormatting.DARK_GREEN + I18n.format("lore.taam.wrench", new Object[0]));
		if (!GuiScreen.isShiftKeyDown()) {
			par3List.add(EnumChatFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
		} else {
			String usage = I18n.format("lore.taam.wrench.usage", new Object[0]);
			//Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for(int i = 0;i < split.length; i++) {
				par3List.add(split[i]);
			}
		}
	}
		
	@Override
	public boolean onItemUse(ItemStack itemStack,
			EntityPlayer player, World world,
			int x, int y, int z,
			int side,
			float hitx, float hity, float hitz) {

		boolean didSomething = false;
		
		TileEntity te = world.getTileEntity(x, y, z);
        
        if(te instanceof TileEntityConveyor) {
        	didSomething = true;
        	TileEntityConveyor tec = (TileEntityConveyor) te;
        	tec.updateContainingBlockInfo();
//        	player.addChatMessage(new ChatComponentText(String.format((world.isRemote ? 'C' : 'S') + " Conveyor facing %s. isEnd: %b isBegin: %b", tec.getFacingDirection().toString(), tec.isEnd(), tec.isBegin())));
//        	if(tec.appliance == null) {
//        		player.addChatMessage(new ChatComponentText(String.format((world.isRemote ? 'C' : 'S') + " Appliance Type: %s Appliance is null. ", tec.applianceType)));
//        	} else {
//        		player.addChatMessage(new ChatComponentText(String.format((world.isRemote ? 'C' : 'S') + " Appliance Type: %s Appliance: %s", tec.applianceType, String.valueOf(tec.appliance))));
//        	}
        }
        if(didSomething && !world.isRemote) {
        	world.playSound(player.posX ,player.posY ,player.posZ ,"note.hat", 1, 1, false);
        }
		
        return !didSomething;
	}

}
