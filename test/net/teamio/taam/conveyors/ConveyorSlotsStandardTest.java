package net.teamio.taam.conveyors;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2018-06-18.
 */
@RunWith(VoltzTestRunner.class)
public class ConveyorSlotsStandardTest extends AbstractTest {

	public void testInsertItemAt_simulate_emptySlots() {
		ConveyorSlotsStandard slots = new ConveyorSlotsStandard();

		for (int sl = 0; sl < 9; sl++) {
			int expected = 10 + sl;
			int amount = slots.insertItemAt(new ItemStack(Items.STICK, expected, 0), sl, true);

			// Simulated item transferred
			assertEquals(expected, amount);
		}

		for (int sl = 0; sl < 9; sl++) {
			// Nothing retained
			assertNull(slots.slots[sl].itemStack);
		}
	}

	public void testInsertItemAt_emptySlots() {
		ConveyorSlotsStandard slots = new ConveyorSlotsStandard();

		for (int sl = 0; sl < 9; sl++) {
			int expected = 10 + sl;
			int amount = slots.insertItemAt(new ItemStack(Items.STICK, expected, 0), sl, false);

			// Simulated item transferred
			assertEquals(expected, amount);
			// Stack retained
			assertEquals(expected, slots.slots[sl].itemStack.getCount());
			assertEquals(Items.STICK, slots.slots[sl].itemStack.getItem());
		}
	}

	public void testInsertItemAt_simulate_filledSlot() {
		ConveyorSlotsStandard slots = new ConveyorSlotsStandard();
		slots.slots[0].itemStack = new ItemStack(Items.STICK);

		int amount = slots.insertItemAt(new ItemStack(Items.APPLE), 0, true);

		// Item is different, amount unchanged
		assertEquals(0, amount);
		// Old content unchanged
		assertEquals(1, slots.slots[0].itemStack.getCount());
		assertEquals(Items.STICK, slots.slots[0].itemStack.getItem());
	}

	// TODO: move the major part of this testing to cover the actual logic in ConveyorUtil.insertItemAt
	// TODO: test for partially filled stack + additional of the same amount

	/* TODO: test other methods
	public void testRemoveItemAt() {
	}

	public void testGetSlot() {
	}

	public void testSerializeNBT() {
	}

	public void testDeserializeNBT() {
	}*/
}
