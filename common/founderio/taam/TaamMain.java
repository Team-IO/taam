package founderio.taam;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import founderio.taam.blocks.TaamBlock;
import founderio.taam.blocks.TileEntitySensor;

@Mod(modid = Taam.MOD_ID, name = Taam.MOD_NAME, version = Taam.MOD_VERSION)
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class TaamMain {
	@Instance(Taam.MOD_ID)
	public static TaamMain instance;
	
	@SidedProxy(clientSide = "founderio.taam.TaamClientProxy", serverSide = "founderio.taam.TaamCommonProxy")
	public static TaamCommonProxy proxy;
	

	public static CreativeTabs creativeTab;

	
	public static TaamBlock blockSensor;
	

	private Configuration config;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		creativeTab = new CreativeTabs(Taam.MOD_ID) {
//			@Override
//			@SideOnly(Side.CLIENT)
//			public Item getTabIconItem() {
//				return itemChaosCrystal;
//			}
		};
		
		blockSensor = new TaamBlock(config.getBlock(Taam.BLOCK_SENSOR, 3030).getInt());
		blockSensor.setUnlocalizedName(Taam.BLOCK_SENSOR);
		blockSensor.setCreativeTab(creativeTab);
		
		config.save();
		
		GameRegistry.registerBlock(blockSensor, ItemBlock.class, Taam.BLOCK_SENSOR, Taam.MOD_ID);
		
		GameRegistry.registerTileEntity(TileEntitySensor.class, Taam.TILEENTITY_SENSOR);
		
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderStuff();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
