package net.teamio.taam.content.common;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.teamio.taam.Taam;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWrench extends Item {

	public ItemWrench() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
		setTextureName(Taam.MOD_ID + ":wrench");
		setFull3D();
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
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if(player.isSneaking() && entity instanceof EntityLivingBase) {
			EntityLivingBase entLiving = (EntityLivingBase)entity;
			entLiving.rotationYawHead = (entLiving.rotationYawHead + 180) % 360f;
		} else {
			entity.rotationYaw = (entity.rotationYaw + 180) % 360f;
		}
		entity.attackEntityFrom(new EntityDamageSource("taam.reconfigured", player), 3f);
		return true;
	}
	
//	@Override
//	public boolean hitEntity(ItemStack stack, EntityLivingBase source,
//			EntityLivingBase target) {
//	Not used, as we skip the default attack code by implementing the function onLeftClickEntity
//		return true;
//	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		// Required for disassembling, as we need shift click on the target block
		return true;
	};
	
//	@Override
//	public boolean onItemUse(ItemStack itemStack,
//			EntityPlayer player, World world,
//			int x, int y, int z,
//			int side,
//			float hitx, float hity, float hitz) {
//	Not used, all handling is done by the >blocks< and / or central Utils.
//	}

}
