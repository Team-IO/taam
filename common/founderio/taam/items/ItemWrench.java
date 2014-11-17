package founderio.taam.items;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
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

public class ItemWrench extends Item {

	public ItemWrench() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
		setTextureName(Taam.MOD_ID + ":wrench");
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
	public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player) {
		return true;
	};

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase source,
			EntityLivingBase target) {
		//System.out.println("Entity will be hit." + source.rotationYaw);
		if(target.isSneaking()) {
			source.rotationYawHead = source.rotationYawHead = (source.rotationYawHead + 180) % 360f;
		} else {
			source.rotationYaw = source.rotationYaw = (source.rotationYaw + 180) % 360f;
		}
		//System.out.println("Entity hit." + source.rotationYaw);
		return true;
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
			//Rotation Logic is in the block implementation.
			didSomething = true;
			TileEntityConveyor tec = (TileEntityConveyor) te;
			tec.updateContainingBlockInfo();
		}
		if (didSomething && !world.isRemote) {
			// TODO: play custom sound
			world.playSound(player.posX, player.posY, player.posZ, "note.hat", 1, 1, false);
		}

		return !didSomething;
	}

}
