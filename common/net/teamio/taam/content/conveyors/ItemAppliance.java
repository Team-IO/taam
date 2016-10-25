package net.teamio.taam.content.conveyors;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.teamio.taam.content.IRotatable;
import net.teamio.taam.conveyors.IConveyorApplianceMetaInfo;
import net.teamio.taam.machines.IMachineMetaInfo;
import net.teamio.taam.util.TaamUtil;

public class ItemAppliance extends ItemBlock {

	private final IConveyorApplianceMetaInfo[] values;

	public ItemAppliance(Block block, IConveyorApplianceMetaInfo[] values) {
		super(block);
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException("Specified meta values were null or empty");
		}
		this.values = values;
		setHasSubtypes(true);
	}

	public IConveyorApplianceMetaInfo getInfo(int meta) {
		int ordinal = MathHelper.clamp_int(meta, 0, values.length);
		return values[ordinal];
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getMetadata();
		IConveyorApplianceMetaInfo info = getInfo(meta);

		return this.getUnlocalizedName() + "." + info.unlocalizedName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List<ItemStack> list) {
		for (int i = 0; i < values.length; i++) {
			list.add(new ItemStack(item, 1, values[i].metaData()));
		}
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,
			float hitX, float hitY, float hitZ, IBlockState newState) {
		EnumFacing dir = side.getOpposite();
		int meta = stack.getMetadata();
		IConveyorApplianceMetaInfo info = getInfo(meta);

		// If the player clicked the top or bottom & that is not supported
		// use the player's facing direction
		if(!info.isDirectionSupported(dir)) {
			if(dir.getAxis() == EnumFacing.Axis.Y) {
				dir = player.getAdjustedHorizontalFacing();
			} else {
				return false;
			}
		}
		// only place block & both direction is supported & there is a suitable host
		if (info.isDirectionSupported(dir) && TaamUtil.canAttachAppliance(world, pos, dir)) {
			boolean success = super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
			if (success) {
				TileEntity te = world.getTileEntity(pos);
				if (te instanceof IRotatable) {
					((IRotatable) te).setFacingDirection(dir);
				}
			}
			return success;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean detailInfo) {

		list.add(TextFormatting.DARK_GREEN + I18n.format("lore.taam.conveyor_appliance", new Object[0]));
		if (!GuiScreen.isShiftKeyDown()) {
			list.add(TextFormatting.DARK_PURPLE + I18n.format("lore.taam.shift", new Object[0]));
		} else {
			String usage = I18n.format("lore.taam.conveyor_appliance.usage", new Object[0]);
			// Split at literal \n in the translated text. a lot of escaping here.
			String[] split = usage.split("\\\\n");
			for (int i = 0; i < split.length; i++) {
				list.add(split[i]);
			}
		}
		// Add metadata-specific values
		int meta = itemStack.getMetadata();
		IConveyorApplianceMetaInfo info = getInfo(meta);
		info.addInformation(itemStack, player, list, detailInfo);
	}

}
