package net.teamio.taam.content;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialMachinesTransparent extends Material {
	public static final MaterialMachinesTransparent INSTANCE = new MaterialMachinesTransparent(MapColor.ironColor);

	public MaterialMachinesTransparent(MapColor color) {
		super(color);
		this.setRequiresTool();
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean blocksLight() {
		return false;
	}

	@Override
	public boolean blocksMovement() {
		// Required to be true, else will be broken by fluids!
		// Player suffocation: checked with Block.isVisuallyOpaque() with also
		// checks Block.isFullCube()
		return true;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}
}
