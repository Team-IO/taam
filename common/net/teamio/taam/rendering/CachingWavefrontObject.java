package net.teamio.taam.rendering;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

public class CachingWavefrontObject extends WavefrontObject {

	Map<String, GroupObject> cache;

	public CachingWavefrontObject(ResourceLocation resource) throws ModelFormatException {
		super(resource);
		initCache();
	}

	public CachingWavefrontObject(String filename, InputStream stream) throws ModelFormatException {
		super(filename, stream);
		initCache();
	}

	private void initCache() {
		cache = new HashMap<String, GroupObject>();
		for (GroupObject groupObject : groupObjects) {
			cache.put(groupObject.name, groupObject);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderPart(String partName) {
		GroupObject groupObject = cache.get(partName);
		if(groupObject != null) {
			groupObject.render();
		}
	}

	@SideOnly(Side.CLIENT)
	public void tessellatePart(Tessellator tessellator, String partName) {
		GroupObject groupObject = cache.get(partName);
		if(groupObject != null) {
			groupObject.render(tessellator);
		}
	}
}
