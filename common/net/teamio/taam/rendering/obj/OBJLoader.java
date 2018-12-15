package net.teamio.taam.rendering.obj;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Loader for OBJ models.
 * To enable your mod call instance.addDomain(modid).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 *
 * Replacement for the original OBJLoader. Hacky workaround.
 *
 * @author shadekiller666
 * https://github.com/shadekiller666/MinecraftForge/tree/1.9_OBJLoader/src/main/java/net/minecraftforge/client/model/obj
 *
 */
public enum OBJLoader implements ICustomModelLoader {
	INSTANCE;

	private IResourceManager manager;
	private final Set<String> enabledDomains = new HashSet<String>();
	private final Map<ResourceLocation, OBJModel> cache = new HashMap<ResourceLocation, OBJModel>();

	public void addDomain(String domain) {
		this.enabledDomains.add(domain.toLowerCase());
		FMLLog.log(Level.INFO, "OBJLoader: Domain %s has been added.", domain.toLowerCase());
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.manager = resourceManager;
		this.cache.clear();
	}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return this.enabledDomains.contains(modelLocation.getNamespace())
				&& modelLocation.getPath().endsWith(".obj");
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		ResourceLocation file = new ResourceLocation(modelLocation.getNamespace(),
				modelLocation.getPath());
		if (!this.cache.containsKey(file)) {
			IResource resource = null;
			try {
				resource = manager.getResource(file);
			} catch (FileNotFoundException e) {
				if (modelLocation.getPath().startsWith("models/block/"))
					resource = manager.getResource(new ResourceLocation(file.getNamespace(),
							"models/item/" + file.getPath().substring("models/block/".length())));
				else if (modelLocation.getPath().startsWith("models/item/"))
					resource = manager.getResource(new ResourceLocation(file.getNamespace(),
							"models/block/" + file.getPath().substring("models/item/".length())));
				else
					throw e;
			}
			OBJModel.Parser parser = new OBJModel.Parser(resource, manager);
			OBJModel model = null;
			try {
				model = parser.parse();
			} finally {
				cache.put(modelLocation, model);
			}
		}
		OBJModel model = cache.get(file);
		if (model == null)
			throw new ModelLoaderRegistry.LoaderException("Error loading model previously: " + file);
		return model;
	}
}
