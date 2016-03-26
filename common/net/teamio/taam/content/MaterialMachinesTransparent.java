package net.teamio.taam.content;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialTransparent;

public class MaterialMachinesTransparent extends MaterialTransparent {
	public static final MaterialMachinesTransparent INSTANCE = new MaterialMachinesTransparent(MapColor.ironColor);
	
	public MaterialMachinesTransparent(MapColor color) {
		super(color);
		this.setRequiresTool();
	}

    @Override
    public boolean isSolid()
    {
        return false;
    }

    @Override
    public boolean getCanBlockGrass()
    {
        return false;
    }

    @Override
    public boolean blocksMovement()
    {
        return false;
    }
    
    @Override
    public boolean isOpaque() {
    	return false;
    }
}
