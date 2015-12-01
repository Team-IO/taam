package net.teamio.taam.content.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.teamio.taam.Taam;
import net.teamio.taam.Taam.BLOCK_ORE_META;
import net.teamio.taam.content.ItemWithMetadata;

public class ItemIngot extends ItemWithMetadata {

	public ItemIngot(String baseName, Enum<?>[] metaValues) {
		super(baseName, metaValues);
	}

	@Override
	public boolean isValidMetadata(int meta) {
		return Taam.BLOCK_ORE_META.valueOf(meta).ingot && super.isValidMetadata(meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {
		BLOCK_ORE_META[] values = Taam.BLOCK_ORE_META.values();
		iconList = new IIcon[values.length];
		for (int i = 0; i < values.length; i++) {
			if(values[i].ingot) {
				iconList[i] = ir.registerIcon(Taam.MOD_ID + ":" + baseName + "." + values[i].name());
			} else {
				iconList[i] = ir.registerIcon(Taam.MOD_ID + ":" + baseName + ".impossible");
			}
		}
	}
}
