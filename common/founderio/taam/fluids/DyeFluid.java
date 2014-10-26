package founderio.taam.fluids;

import net.minecraftforge.fluids.Fluid;

public class DyeFluid extends Fluid {

	public DyeFluid(String fluidName) {
		super(fluidName);
		setViscosity(400);
		setDensity(1300);
	}

}
