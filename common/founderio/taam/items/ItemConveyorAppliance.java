package founderio.taam.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;
import founderio.taam.blocks.TileEntityConveyor;

public class ItemConveyorAppliance extends Item {

	private IIcon[] iconList;

	public ItemConveyorAppliance() {
		super();
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta) {

		if (meta < 0 || meta >= iconList.length) {
			meta = 0;
		}

		return iconList[meta];
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		int i = itemStack.getItemDamage();

		if (i < 0 || i >= Taam.ITEM_CONVEYOR_APPLIANCE_META.length) {
			i = 0;
		}

		return super.getUnlocalizedName() + "." + Taam.ITEM_CONVEYOR_APPLIANCE_META[i];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister ir) {
		iconList = new IIcon[Taam.ITEM_CONVEYOR_APPLIANCE_META.length];
		for (int i = 0; i < Taam.ITEM_CONVEYOR_APPLIANCE_META.length; i++) {
			iconList[i] = ir.registerIcon(Taam.MOD_ID + ":conveyor_appliance." + Taam.ITEM_CONVEYOR_APPLIANCE_META[i]);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for (int i = 0; i < Taam.ITEM_CONVEYOR_APPLIANCE_META.length; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		
		return false;
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
