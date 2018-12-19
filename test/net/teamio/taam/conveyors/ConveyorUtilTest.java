package net.teamio.taam.conveyors;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.common.ItemWrench;
import net.teamio.taam.content.conveyors.BlockProductionLine;
import net.teamio.taam.content.conveyors.BlockProductionLineAppliance;
import net.teamio.taam.content.conveyors.ItemAppliance;
import net.teamio.taam.content.conveyors.ItemProductionLine;
import net.teamio.taam.util.TaamUtil;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2018-06-17.
 */
@RunWith(VoltzTestRunner.class)
public class ConveyorUtilTest extends AbstractTest {

	@Override
	public void setUpForEntireClass() {
		TaamMain.itemWrench = new ItemWrench();
		ForgeRegistries.ITEMS.register(TaamMain.itemWrench.setRegistryName(Taam.MOD_ID, Taam.ITEM_WRENCH));

		TaamMain.blockProductionLine = new BlockProductionLine();
		ForgeRegistries.BLOCKS.register(TaamMain.blockProductionLine.setRegistryName(Taam.MOD_ID, Taam.BLOCK_PRODUCTIONLINE));
		ForgeRegistries.ITEMS.register(new ItemProductionLine(TaamMain.blockProductionLine, TaamUtil.enumValuesAsString(Taam.BLOCK_PRODUCTIONLINE_META.values())).setRegistryName(Taam.MOD_ID, Taam.BLOCK_PRODUCTIONLINE));

		TaamMain.blockProductionLineAppliance = new BlockProductionLineAppliance();
		ForgeRegistries.BLOCKS.register(TaamMain.blockProductionLineAppliance.setRegistryName(Taam.MOD_ID, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE));
		ForgeRegistries.ITEMS.register(new ItemAppliance(TaamMain.blockProductionLineAppliance, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.values()).setRegistryName(Taam.MOD_ID, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE));

		Config.init(null);
		Config.debug_output_as_info = true;
		Config.debug_output = true;
		Config.pl_conveyor_rightclick_blacklist.clear();
		Config.pl_conveyor_rightclick_blacklist.add("taam:wrench");
		Config.pl_conveyor_rightclick_blacklist.add("taam:productionline@2");
		Config.pl_conveyor_rightclick_blacklist.add("taam:productionline_appliance");
		Config.pl_conveyor_rightclick_blacklist.add("minecraft:potion");
		// TODO: at runtime it seems that getHasSubtypes is true for potion items, adding @0 before the NBT...
		// Not during unit testing, though
		Config.pl_conveyor_rightclick_blacklist.add("minecraft:splash_potion#{Potion:\"minecraft:invisibility\"}");
	}


	public void testIsBlacklistedForConveyor() {
		// Different items
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.itemWrench)));
		assertFalse(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(Items.STICK)));

		// No specific metadata specified
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLineAppliance)));
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLineAppliance, 1, 0)));
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLineAppliance, 1, 1)));
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLineAppliance, 1, 2)));
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLineAppliance, 1, 3)));

		// Only specific metadata in blacklist
		assertFalse(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLine)));
		assertFalse(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLine, 1, 0)));
		assertFalse(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLine, 1, 1)));
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLine, 1, 2)));
		assertFalse(ConveyorUtil.isBlacklistedForConveyor(new ItemStack(TaamMain.blockProductionLine, 1, 3)));
	}

	public void testIsBlacklistedForConveyor_NBT() {
		// NBT Tag Compound
		// NBT not in blacklist, NBT is ignored
		ItemStack stack = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(stack, PotionTypes.INVISIBILITY);
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(stack));

		stack = new ItemStack(Items.POTIONITEM);
		PotionUtils.addPotionToItemStack(stack, PotionTypes.LEAPING);
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(stack));

		// NBT in blacklist, NBT is respected
		stack = new ItemStack(Items.SPLASH_POTION);
		PotionUtils.addPotionToItemStack(stack, PotionTypes.INVISIBILITY);
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(stack));

		stack = new ItemStack(Items.SPLASH_POTION);
		PotionUtils.addPotionToItemStack(stack, PotionTypes.LEAPING);
		assertFalse(ConveyorUtil.isBlacklistedForConveyor(stack));
	}
}
