package net.teamio.taam.recipes;

import net.minecraft.item.ItemStack;
import net.teamio.taam.util.TaamUtil;

/**
 * Base class for processing recipes providing matching for input items.
 *
 * @author Oliver Kahrmann
 *
 */
public abstract class BaseProcessingRecipe implements IProcessingRecipe {

	protected ItemStack input;
	protected String inputOreDict;

	@Override
	public boolean inputMatches(ItemStack itemStack) {
		return TaamUtil.isInputMatching(input, inputOreDict, itemStack);
	}

	@Override
	public ItemStack getInput() {
		return input;
	}

	@Override
	public String getInputOreDict() {
		return inputOreDict;
	}

}
