package net.teamio.taam.util;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2018-12-16.
 */
@RunWith(VoltzTestRunner.class)
public class InventoryUtilsTest extends AbstractTest {

	private Item stick;
	private Item apple;
	private Item hoe;
	private ItemStack empty;


	@Override
	public void setUpForEntireClass() {
		// Single setup in case these pesky names change again
		stick = Items.STICK;
		apple = Items.APPLE;
		hoe = Items.DIAMOND_HOE;
		empty = ItemStack.EMPTY;
	}

	public void testPreconditions() {
		// Required condition: we need a non-null item
		assertNotNull(stick);
		assertNotNull(apple);
		assertNotNull(hoe);
		// Required condition: hoes cannot stack
		assertEquals(1, hoe.getItemStackLimit(new ItemStack(hoe, 64)));
		// Required condition: ItemStack.EMPTY must not be null
		assertNotNull(empty);
		// Required condition: ItemStack.EMPTY needs to be honest about itself
		assertTrue(empty.isEmpty());
	}

	/*
	 * isEmpty
	 */

	@SuppressWarnings("ConstantConditions")
	public void testIsEmpty_TrueForNullStack() {
		boolean result = InventoryUtils.isEmpty(null);
		assertTrue(result);
	}

	public void testIsEmpty_TrueForItemStackEmtpy() {
		boolean result = InventoryUtils.isEmpty(empty);
		assertTrue(result);
	}

	public void testIsEmpty_TrueForZeroCount() {
		ItemStack test = new ItemStack(stick, 0);
		boolean result = InventoryUtils.isEmpty(test);
		assertTrue(result);
	}

	@SuppressWarnings("ConstantConditions")
	public void testIsEmpty_TrueForNullItem() {
		ItemStack test = new ItemStack((Item) null, 0);
		boolean result = InventoryUtils.isEmpty(test);
		assertTrue(result);
	}

	public void testIsEmpty_FalseForValidStack() {
		ItemStack test = new ItemStack(stick, 1);
		boolean result = InventoryUtils.isEmpty(test);
		assertFalse(result);
	}

	/*
	 * guardAgainstNull
	 */

	public void testGuardAgainstNull_NeverReturnsNull() {
		ItemStack result = InventoryUtils.guardAgainstNull(null);
		assertNotNull(result);
	}

	public void testGuardAgainstNull_CatchesEmptyStack() {
		ItemStack test = new ItemStack(stick, 0);
		ItemStack result = InventoryUtils.guardAgainstNull(test);
		assertSame(empty, result);
	}

	public void testGuardAgainstNull_ReturnsValidStack() {
		ItemStack test = new ItemStack(stick, 1);
		ItemStack result = InventoryUtils.guardAgainstNull(test);
		assertSame(test, result);
	}


	/*
	 * isItem
	 */

	public void testIsItem_FalseForEmptyStack() {
		ItemStack test = new ItemStack(stick, 0, 0);
		boolean result = InventoryUtils.isItem(test, stick, 0);
		assertFalse(result);
	}

	public void testIsItem_FalseForDifferentItem() {
		ItemStack test = new ItemStack(apple, 1, 0);
		boolean result = InventoryUtils.isItem(test, stick, 0);
		assertFalse(result);
	}

	public void testIsItem_FalseForDifferentMeta() {
		ItemStack test = new ItemStack(stick, 1, 0);
		boolean result = InventoryUtils.isItem(test, stick, 2);
		assertFalse(result);
	}

	public void testIsItem_TrueForMatching() {
		ItemStack test = new ItemStack(stick, 1, 0);
		boolean result = InventoryUtils.isItem(test, stick, 0);
		assertTrue(result);
	}

	/*
	 * canStack
	 */

	public void testCanStack_TrueBothEmpty() {
		boolean result = InventoryUtils.canStack(empty, empty);
		assertTrue(result);
	}

	public void testCanStack_FalseOneEmpty1() {
		ItemStack test1 = new ItemStack(stick, 0);
		ItemStack test2 = new ItemStack(stick, 1);
		boolean result = InventoryUtils.canStack(test1, test2);
		assertFalse(result);
	}

	public void testCanStack_FalseOneEmpty2() {
		ItemStack test1 = new ItemStack(stick, 1);
		ItemStack test2 = new ItemStack(stick, 0);
		boolean result = InventoryUtils.canStack(test1, test2);
		assertFalse(result);
	}

	public void testCanStack_FalseOneUnstackable1() {
		ItemStack test1 = new ItemStack(hoe, 1);
		ItemStack test2 = new ItemStack(stick, 1);
		boolean result = InventoryUtils.canStack(test1, test2);
		assertFalse(result);
	}

	public void testCanStack_FalseOneUnstackable2() {
		ItemStack test1 = new ItemStack(stick, 1);
		ItemStack test2 = new ItemStack(hoe, 1);
		boolean result = InventoryUtils.canStack(test1, test2);
		assertFalse(result);
	}

	public void testCanStack_FalseTwoUnstackable() {
		ItemStack test1 = new ItemStack(hoe, 1);
		ItemStack test2 = new ItemStack(hoe, 1);
		boolean result = InventoryUtils.canStack(test1, test2);
		assertFalse(result);
	}

	public void testCanStack_FalseDifferentItems() {
		ItemStack test1 = new ItemStack(stick, 1);
		ItemStack test2 = new ItemStack(apple, 1);
		boolean result = InventoryUtils.canStack(test1, test2);
		assertFalse(result);
	}

	public void testCanStack_FalseDifferentMeta() {
		ItemStack test1 = new ItemStack(apple, 1, 10);
		ItemStack test2 = new ItemStack(apple, 1, 3);
		boolean result = InventoryUtils.canStack(test1, test2);
		assertFalse(result);
	}

	public void testCanStack_FalseDifferentNBT() {
		ItemStack test1 = new ItemStack(apple, 1);
		ItemStack test2 = new ItemStack(apple, 1);
		test2.setTagCompound(new NBTTagCompound());
		boolean result = InventoryUtils.canStack(test1, test2);
		assertFalse(result);
	}

	public void testCanStack_TrueMatchingItems() {
		ItemStack test1 = new ItemStack(stick, 1);
		ItemStack test2 = new ItemStack(stick, 1);
		boolean result = InventoryUtils.canStack(test1, test2);
		assertTrue(result);
	}

	/*
	 * setCount
	 */

	public void testSetCount_EmptyStaysEmpty() {
		ItemStack result = InventoryUtils.setCount(empty, 20);
		assertSame(empty, result);
	}

	public void testSetCount_NullStaysEmpty() {
		ItemStack result = InventoryUtils.setCount(null, 20);
		assertSame(empty, result);
	}

	public void testSetCount_SetsCountInSameStack() {
		ItemStack test = new ItemStack(stick, 1);
		ItemStack result = InventoryUtils.setCount(test, 5);
		assertSame(test, result);
		assertEquals(5, result.getCount());
	}

	public void testSetCount_CountZeroCreatesEmpty() {
		ItemStack test = new ItemStack(stick, 1);
		ItemStack result = InventoryUtils.setCount(test, 0);
		assertSame(empty, result);
	}

	public void testSetCount_CountNegativeCreatesEmpty() {
		ItemStack test = new ItemStack(stick, 1);
		ItemStack result = InventoryUtils.setCount(test, -4);
		assertSame(empty, result);
	}

	/*
	 * adjustCount
	 */

	public void testAdjustCount_EmptyStaysEmpty() {
		ItemStack result = InventoryUtils.adjustCount(empty, 20);
		assertSame(empty, result);
	}

	public void testAdjustCount_NullStaysEmpty() {
		ItemStack result = InventoryUtils.adjustCount(null, 20);
		assertSame(empty, result);
	}

	public void testAdjustCount_AddsCountInSameStack() {
		ItemStack test = new ItemStack(stick, 3);
		ItemStack result = InventoryUtils.adjustCount(test, 5);
		assertSame(test, result);
		assertEquals(8, result.getCount());
	}

	public void testAdjustCount_SubtractsCountInSameStack() {
		ItemStack test = new ItemStack(stick, 7);
		ItemStack result = InventoryUtils.adjustCount(test, -5);
		assertSame(test, result);
		assertEquals(2, result.getCount());
	}

	public void testAdjustCount_ZeroDoesNotChangeCount() {
		ItemStack test = new ItemStack(stick, 7);
		ItemStack result = InventoryUtils.adjustCount(test, 0);
		assertSame(test, result);
		assertEquals(7, result.getCount());
	}

	public void testAdjustCount_ResultZeroCreatesEmpty() {
		ItemStack test = new ItemStack(stick, 1);
		ItemStack result = InventoryUtils.adjustCount(test, -1);
		assertSame(empty, result);
	}

	public void testAdjustCount_ResultBelowZeroCreatesEmpty() {
		ItemStack test = new ItemStack(stick, 6);
		ItemStack result = InventoryUtils.adjustCount(test, -8);
		assertSame(empty, result);
	}

	/*
	 * writeItemStacksToTag
	 */

	public void testWriteItemStacksToTag_WritesMultipleStacks() {
		ItemStack[] items = new ItemStack[]{
				new ItemStack(stick, 1),
				new ItemStack(stick, 2)
		};
		NBTTagList result = InventoryUtils.writeItemStacksToTag(items, 64, false);
		assertEquals(2, result.tagCount());
	}

	public void testWriteItemStacksToTag_IgnoresEmptyStacks() {
		ItemStack[] items = new ItemStack[]{
				new ItemStack(stick, 1),
				null,
				new ItemStack(stick, 2),
				empty
		};
		NBTTagList result = InventoryUtils.writeItemStacksToTag(items, 64, false);
		assertEquals(2, result.tagCount());
	}

	public void testWriteItemStacksToTag_SlotWritten() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, 1)};
		NBTTagList result = InventoryUtils.writeItemStacksToTag(items, 64, false);
		NBTTagCompound single = result.getCompoundTagAt(0);
		assertTrue(single.hasKey("Slot", Constants.NBT.TAG_SHORT));
	}

	public void testWriteItemStacksToTag_SlotNotWritten() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, 1)};
		NBTTagList result = InventoryUtils.writeItemStacksToTag(items, 64, true);
		NBTTagCompound single = result.getCompoundTagAt(0);
		assertFalse(single.hasKey("Slot"));
	}

	public void testWriteItemStacksToTag_WritesStack() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, 1)};
		NBTTagList result = InventoryUtils.writeItemStacksToTag(items, 64, false);
		NBTTagCompound single = result.getCompoundTagAt(0);
		assertTrue(single.hasKey("id", Constants.NBT.TAG_STRING));
	}

	public void testWriteItemStacksToTag_64DoesNotWriteQuantity() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, 1)};
		NBTTagList result = InventoryUtils.writeItemStacksToTag(items, 64, false);
		NBTTagCompound single = result.getCompoundTagAt(0);
		assertFalse(single.hasKey("Quantity"));
	}

	public void testWriteItemStacksToTag_MoreThanShortWritesQuantity() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, 1)};
		NBTTagList result = InventoryUtils.writeItemStacksToTag(items, Short.MAX_VALUE + 1, false);
		NBTTagCompound single = result.getCompoundTagAt(0);
		assertTrue(single.hasKey("Quantity", Constants.NBT.TAG_INT));
	}

	public void testWriteItemStacksToTag_MoreThanByteWritesQuantity() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, 1)};
		NBTTagList result = InventoryUtils.writeItemStacksToTag(items, Byte.MAX_VALUE + 1, false);
		NBTTagCompound single = result.getCompoundTagAt(0);
		assertTrue(single.hasKey("Quantity", Constants.NBT.TAG_SHORT));
	}

	/*
	 * readItemStacksFromTag
	 */
	public void testReadItemStacksFromTag_ReadsMultipleStacks() {
		ItemStack[] items = new ItemStack[]{
				new ItemStack(stick, 1),
				new ItemStack(stick, 2)
		};
		NBTTagList data = InventoryUtils.writeItemStacksToTag(items, 64, false);
		ItemStack[] result = new ItemStack[2];
		InventoryUtils.readItemStacksFromTag(result, data, false);
		assertNotNull(result[0]);
		assertNotNull(result[1]);
	}

	public void testReadItemStacksFromTag_SequentialIgnoresEmptyStacks() {
		ItemStack[] items = new ItemStack[]{
				new ItemStack(stick, 1),
				empty,
				new ItemStack(stick, 2)
		};
		NBTTagList data = InventoryUtils.writeItemStacksToTag(items, 64, false);
		ItemStack[] result = new ItemStack[3];
		InventoryUtils.readItemStacksFromTag(result, data, true);
		assertNotNull(result[0]);
		assertNotNull(result[1]);
		assertSame(empty, result[2]);
	}

	public void testReadItemStacksFromTag_NonSequentialSkipsEmptyStacks() {
		ItemStack[] items = new ItemStack[]{
				new ItemStack(stick, 1),
				empty,
				new ItemStack(stick, 2)
		};
		NBTTagList data = InventoryUtils.writeItemStacksToTag(items, 64, false);
		ItemStack[] result = new ItemStack[3];
		InventoryUtils.readItemStacksFromTag(result, data, false);
		assertNotNull(result[0]);
		assertSame(empty, result[1]);
		assertNotNull(result[2]);
	}

	public void testReadItemStacksFromTag_ReadsIntegerQuantity() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, Short.MAX_VALUE + 2)};
		NBTTagList data = InventoryUtils.writeItemStacksToTag(items, Short.MAX_VALUE + 1, false);
		ItemStack[] result = new ItemStack[1];
		InventoryUtils.readItemStacksFromTag(result, data, false);
		assertEquals(Short.MAX_VALUE + 2, result[0].getCount());
	}

	public void testReadItemStacksFromTag_ReadsShortQuantity() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, Byte.MAX_VALUE + 2)};
		NBTTagList data = InventoryUtils.writeItemStacksToTag(items, Byte.MAX_VALUE + 1, false);
		ItemStack[] result = new ItemStack[1];
		InventoryUtils.readItemStacksFromTag(result, data, false);
		assertEquals(Byte.MAX_VALUE + 2, result[0].getCount());
	}

	public void testReadItemStacksFromTag_ReadsCorrectItemAndMeta() {
		ItemStack[] items = new ItemStack[]{new ItemStack(stick, 34, 8)};
		NBTTagList data = InventoryUtils.writeItemStacksToTag(items, 64, false);
		ItemStack[] result = new ItemStack[1];
		InventoryUtils.readItemStacksFromTag(result, data, false);
		assertEquals(34, result[0].getCount());
		assertEquals(8, result[0].getMetadata());
		assertSame(stick, result[0].getItem());
	}
}
