package net.teamio.taam.conveyors;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.common.ItemWrench;
import net.teamio.taam.content.conveyors.BlockProductionLine;
import net.teamio.taam.content.conveyors.BlockProductionLineAppliance;
import net.teamio.taam.content.conveyors.ItemAttachable;
import net.teamio.taam.content.conveyors.ItemProductionLine;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2018-06-17.
 */
@RunWith(VoltzTestRunner.class)
public class ConveyorUtilTest extends AbstractTest {

	@Override
	public void setUpForEntireClass() {
		GameRegistry.register(TaamMain.itemWrench = new ItemWrench(), new ResourceLocation(Taam.MOD_ID, Taam.ITEM_WRENCH));

		GameRegistry.register(TaamMain.blockProductionLine = new BlockProductionLine(), new ResourceLocation(Taam.MOD_ID, Taam.BLOCK_PRODUCTIONLINE));
		GameRegistry.register(new ItemProductionLine(TaamMain.blockProductionLine, Taam.BLOCK_PRODUCTIONLINE_META.valuesAsString()), new ResourceLocation(Taam.MOD_ID, Taam.BLOCK_PRODUCTIONLINE));

		GameRegistry.register(TaamMain.blockProductionLineAppliance = new BlockProductionLineAppliance(), new ResourceLocation(Taam.MOD_ID, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE));
		GameRegistry.register(new ItemAttachable(TaamMain.blockProductionLineAppliance, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.valuesAsString()), new ResourceLocation(Taam.MOD_ID, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE));

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
		stack = PotionUtils.addPotionToItemStack(stack, PotionTypes.INVISIBILITY);
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(stack));

		stack = new ItemStack(Items.POTIONITEM);
		stack = PotionUtils.addPotionToItemStack(stack, PotionTypes.LEAPING);
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(stack));

		// NBT in blacklist, NBT is respected
		stack = new ItemStack(Items.SPLASH_POTION);
		stack = PotionUtils.addPotionToItemStack(stack, PotionTypes.INVISIBILITY);
		assertTrue(ConveyorUtil.isBlacklistedForConveyor(stack));

		stack = new ItemStack(Items.SPLASH_POTION);
		stack = PotionUtils.addPotionToItemStack(stack, PotionTypes.LEAPING);
		assertFalse(ConveyorUtil.isBlacklistedForConveyor(stack));
	}
}
