package net.teamio.taam.content.common;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.util.WrenchUtil;

public class ItemWrench extends Item {

	public ItemWrench() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
		setFull3D();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean detailInfo) {

		list.add(ChatFormatting.DARK_GREEN + I18n.format("lore.taam.wrench", new Object[0]));
		if (!GuiScreen.isShiftKeyDown()) {
			list.add(ChatFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
		} else {
			String usage = I18n.format("lore.taam.wrench.usage", new Object[0]);
			//Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for(int i = 0;i < split.length; i++) {
				list.add(split[i]);
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
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player) {
		// Required for disassembling, as we need shift click on the target block
		return true;
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		// This call is for vanilla blocks (mainly stairs). Chests & Furnace etc cannot be called by this.
		// TODO: how to bypass the clicked tileentity?
		// The productionline blocks do not use this call here, they are handled in the corresponding block class itself.
		// This is done, so that wrenches from other mods can be supported eventually.
		return WrenchUtil.wrenchBlock(worldIn, pos, playerIn, facing, hitX, hitY, hitZ);
	}
}
