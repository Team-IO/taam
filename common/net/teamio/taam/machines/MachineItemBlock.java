package net.teamio.taam.machines;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MachineItemBlock extends ItemBlock {

	private final IMachineMetaInfo[] values;
	
	public MachineItemBlock(Block block, IMachineMetaInfo[] values) {
		super(block);
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("Specified meta values were null or empty");
		}
		this.values = values;
	}

	public IMachineMetaInfo getInfo(int meta) {
		int ordinal = MathHelper.clamp_int(meta, 0, values.length);
		return values[ordinal];
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = getInfo(meta);
		info.addInformation(stack, playerIn, tooltip, advanced);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		IMachineMetaInfo info = getInfo(meta);

		return this.getUnlocalizedName() + "." + info.unlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, values[i].metaData()));
		}
	}

}
