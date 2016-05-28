package net.teamio.taam.recipes;

import net.minecraft.item.ItemStack;

public class ChancedOutput {
	public ItemStack output;
	public float chance;

	public ChancedOutput(ItemStack output, float chance) {
		super();
		this.output = output;
		this.chance = chance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(chance);
		result = prime * result
				+ (output == null ? 0 : output.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChancedOutput other = (ChancedOutput) obj;
		if (Float.floatToIntBits(chance) != Float
				.floatToIntBits(other.chance)) {
			return false;
		}
		if (output == null) {
			if (other.output != null) {
				return false;
			}
		} else if (!output.equals(other.output)) {
			return false;
		}
		return true;
	}
}