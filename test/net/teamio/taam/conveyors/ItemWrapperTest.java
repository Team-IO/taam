package net.teamio.taam.conveyors;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import net.minecraft.item.ItemStack;
import net.teamio.taam.content.common.ItemDebugTool;
import org.junit.runner.RunWith;

/**
 * Created by oliver on 2018-12-15.
 */
@RunWith(VoltzTestRunner.class)
public class ItemWrapperTest extends AbstractTest {
	public void testIsEmpty_null_true() {
		ItemWrapper cut = new ItemWrapper(null);

		assertTrue(cut.isEmpty());
	}

	public void testIsEmpty_empty_true() {
		ItemWrapper cut = new ItemWrapper(ItemStack.EMPTY);

		assertTrue(cut.isEmpty());
	}

	public void testIsEmpty_notempty_false() {
		ItemWrapper cut = new ItemWrapper(new ItemStack(new ItemDebugTool()));

		assertFalse(cut.isEmpty());
	}
}
