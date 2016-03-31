package net.teamio.taam;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.teamio.taam.content.ItemWithMetadata;
import net.teamio.taam.content.ItemWithMetadata.ItemDelegate;
import net.teamio.taam.content.common.BlockBuilding;
import net.teamio.taam.content.common.BlockMachines;
import net.teamio.taam.content.common.BlockOre;
import net.teamio.taam.content.common.BlockSensor;
import net.teamio.taam.content.common.BlockSlidingDoor;
import net.teamio.taam.content.common.BlockSupportBeam;
import net.teamio.taam.content.common.FluidDye;
import net.teamio.taam.content.common.ItemDebugTool;
import net.teamio.taam.content.common.ItemTool;
import net.teamio.taam.content.common.ItemWrench;
import net.teamio.taam.content.common.TileEntityChute;
import net.teamio.taam.content.common.TileEntityCreativeCache;
import net.teamio.taam.content.common.TileEntitySensor;
import net.teamio.taam.content.conveyors.BlockProductionLine;
import net.teamio.taam.content.conveyors.BlockProductionLineAppliance;
import net.teamio.taam.content.conveyors.BlockProductionLineAttachable;
import net.teamio.taam.content.conveyors.ItemAppliance;
import net.teamio.taam.content.conveyors.ItemAttachable;
import net.teamio.taam.content.conveyors.ItemProductionLine;
import net.teamio.taam.content.conveyors.TileEntityConveyor;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.content.piping.BlockPipe;
import net.teamio.taam.content.piping.BlockPipeMachines;
import net.teamio.taam.content.piping.TileEntityCreativeWell;
import net.teamio.taam.content.piping.TileEntityMixer;
import net.teamio.taam.content.piping.TileEntityPipe;
import net.teamio.taam.content.piping.TileEntityPump;
import net.teamio.taam.content.piping.TileEntityTank;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.gui.GuiHandler;


@Mod(modid = Taam.MOD_ID, name = Taam.MOD_NAME, version = Taam.MOD_VERSION, guiFactory = Taam.GUI_FACTORY_CLASS)
public class TaamMain {
	@Instance(Taam.MOD_ID)
	public static TaamMain instance;

	@SidedProxy(clientSide = "net.teamio.taam.TaamClientProxy", serverSide = "net.teamio.taam.TaamCommonProxy")
	public static TaamCommonProxy proxy;

	public static SimpleNetworkWrapper network;

	public static ItemDebugTool itemDebugTool;
	public static ItemWrench itemWrench;
	public static ItemTool itemSaw;
	public static ItemWithMetadata<Taam.ITEM_MATERIAL_META> itemMaterial;
	public static ItemWithMetadata<Taam.ITEM_PART_META> itemPart;
	public static ItemWithMetadata<Taam.BLOCK_ORE_META> itemIngot;
	public static ItemWithMetadata<Taam.BLOCK_ORE_META> itemDust;

	public static CreativeTabs creativeTab;

	public static BlockSensor blockSensor;
	public static BlockMachines blockMachines;
	public static BlockProductionLine blockProductionLine;
	public static BlockProductionLineAttachable blockProductionLineAttachable;
	public static BlockProductionLineAppliance blockProductionLineAppliance;
	public static BlockSlidingDoor blockSlidingDoor;
	public static BlockOre blockOre;
	public static BlockBuilding blockConcrete;
	public static BlockSupportBeam blockSupportBeam;

	public static BlockPipeMachines blockPipeMachines;
	public static BlockPipe blockPipe;

	public static FluidDye[] fluidsDye;
	public static BlockFluidClassic[] blocksFluidDye;

	public static DamageSource ds_processed = new DamageSource("taam.processed").setDamageBypassesArmor();
	public static DamageSource ds_shredded = new DamageSource("taam.shredded").setDamageBypassesArmor();
	public static DamageSource ds_ground = new DamageSource("taam.ground").setDamageBypassesArmor();
	public static DamageSource ds_crushed = new DamageSource("taam.crushed").setDamageBypassesArmor();
	public static DamageSource ds_reconfigured = new DamageSource("taam.reconfigured").setDamageIsAbsolute();

	private void registerBlock(Block block, Class<? extends ItemBlock> itemClass, String name) {
		block.setUnlocalizedName(name);
		block.setCreativeTab(creativeTab);
		GameRegistry.registerBlock(block, itemClass, name);
	}

	private void registerItem(Item item, String name) {
		item.setUnlocalizedName(name);
		item.setCreativeTab(creativeTab);
		GameRegistry.registerItem(item, name);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ModMetadata meta = event.getModMetadata();
		meta.authorList.add(Taam.MOD_AUTHOR1);
		meta.authorList.add(Taam.MOD_AUTHOR2);
		meta.description = Taam.MOD_DESCRIPTION;
		meta.logoFile = Taam.MOD_LOGO_PATH;
		meta.autogenerated = false;

		MinecraftForge.EVENT_BUS.register(new TaamCraftingHandler());
		MinecraftForge.EVENT_BUS.register(new Config());
		MinecraftForge.EVENT_BUS.register(proxy);

		Config.init(event.getSuggestedConfigurationFile());
		creativeTab = new CreativeTabs(Taam.MOD_ID) {

			@Override
			@SideOnly(Side.CLIENT)
			public ItemStack getIconItemStack() {
				return new ItemStack(blockProductionLine, 1, 1);
			}

			@Override
			@SideOnly(Side.CLIENT)
			public Item getTabIconItem() {
				return null;
			}
		};

		registerBlock(blockSensor = new BlockSensor(), ItemBlock.class, Taam.BLOCK_SENSOR);

		registerBlock(blockMachines = new BlockMachines(), null, Taam.BLOCK_MACHINES);
		registerItem(new ItemMultiTexture(blockMachines, blockMachines, Taam.BLOCK_MACHINES_META.valuesAsString()), Taam.BLOCK_MACHINES);

		registerBlock(blockProductionLine = new BlockProductionLine(), null, Taam.BLOCK_PRODUCTIONLINE);
		registerItem(new ItemProductionLine(blockProductionLine, blockProductionLine, Taam.BLOCK_PRODUCTIONLINE_META.valuesAsString()), Taam.BLOCK_PRODUCTIONLINE);

		registerBlock(blockProductionLineAttachable = new BlockProductionLineAttachable(), null, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE);
		registerItem(new ItemAttachable(blockProductionLineAttachable, blockProductionLineAttachable, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.valuesAsString()), Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE);

		registerBlock(blockProductionLineAppliance = new BlockProductionLineAppliance(), null, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE);
		registerItem(new ItemAppliance(blockProductionLineAppliance, blockProductionLineAppliance, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.valuesAsString()), Taam.BLOCK_PRODUCTIONLINE_APPLIANCE);

		registerBlock(blockOre = new BlockOre(), null, Taam.BLOCK_ORE);
		registerItem(new ItemMultiTexture(blockOre, blockOre, Taam.BLOCK_ORE_META.valuesAsString()), Taam.BLOCK_ORE);

		registerBlock(blockConcrete = new BlockBuilding(), null, Taam.BLOCK_CONCRETE);
		registerItem(new ItemMultiTexture(blockConcrete, blockConcrete, Taam.BLOCK_CONCRETE_META.valuesAsString()), Taam.BLOCK_CONCRETE);

		registerBlock(blockSupportBeam = new BlockSupportBeam(), ItemBlock.class, Taam.BLOCK_SUPPORT_BEAM);

		registerItem(itemDebugTool = new ItemDebugTool(), Taam.ITEM_DEBUG_TOOL);
		registerItem(itemWrench = new ItemWrench(), Taam.ITEM_WRENCH);
		registerItem(itemSaw = new ItemTool(Taam.ITEM_TOOL_META.saw), Taam.ITEM_TOOL + "." + Taam.ITEM_TOOL_META.saw.name());

		registerItem(itemMaterial = new ItemWithMetadata<Taam.ITEM_MATERIAL_META>("material", Taam.ITEM_MATERIAL_META.values(), null), Taam.ITEM_MATERIAL);
		registerItem(itemPart = new ItemWithMetadata<Taam.ITEM_PART_META>("part", Taam.ITEM_PART_META.values(), null), Taam.ITEM_PART);
		registerItem(itemIngot = new ItemWithMetadata<Taam.BLOCK_ORE_META>("ingot", Taam.BLOCK_ORE_META.values(),
				new ItemDelegate<Taam.BLOCK_ORE_META>() {
					@Override
					public boolean isValidMetadata(Taam.BLOCK_ORE_META meta) {
						return meta.ingot;
					}

					@Override
					public void addInformation(ItemStack stack, EntityPlayer player, List<String> lines,
							boolean detailedInfoSetting) {
					}
				}), Taam.ITEM_INGOT);

		registerItem(itemDust = new ItemWithMetadata<Taam.BLOCK_ORE_META>("dust", Taam.BLOCK_ORE_META.values(),
				new ItemDelegate<Taam.BLOCK_ORE_META>() {
					@Override
					public boolean isValidMetadata(Taam.BLOCK_ORE_META meta) {
						return meta.dust;
					}

					@Override
					public void addInformation(ItemStack stack, EntityPlayer player, List<String> lines,
							boolean detailedInfoSetting) {
					}
				}), Taam.ITEM_DUST);

		registerBlock(blockPipeMachines = new BlockPipeMachines(), null, Taam.BLOCK_PIPEMACHINES);
		registerItem(new ItemMultiTexture(blockPipeMachines, blockPipeMachines, Taam.BLOCK_PIPEMACHINES_META.valuesAsString()), Taam.BLOCK_PIPEMACHINES);

		registerBlock(blockPipe = new BlockPipe(), ItemBlock.class, Taam.BLOCK_PIPE);

		GameRegistry.registerTileEntity(TileEntitySensor.class, Taam.TILEENTITY_SENSOR);
		GameRegistry.registerTileEntity(TileEntityChute.class, Taam.TILEENTITY_CHUTE);
		GameRegistry.registerTileEntity(TileEntityCreativeCache.class, Taam.TILEENTITY_CREATIVECACHE);

		GameRegistry.registerTileEntity(TileEntityConveyor.class, Taam.TILEENTITY_CONVEYOR);
		GameRegistry.registerTileEntity(TileEntityConveyorHopper.class, Taam.TILEENTITY_CONVEYOR_HOPPER);
		GameRegistry.registerTileEntity(TileEntityConveyorProcessor.class, Taam.TILEENTITY_CONVEYOR_PROCESSOR);
		GameRegistry.registerTileEntity(TileEntityConveyorItemBag.class, Taam.TILEENTITY_CONVEYOR_ITEMBAG);
		GameRegistry.registerTileEntity(TileEntityConveyorTrashCan.class, Taam.TILEENTITY_CONVEYOR_TRASHCAN);
		GameRegistry.registerTileEntity(TileEntityConveyorSieve.class, Taam.TILEENTITY_CONVEYOR_SIEVE);

		GameRegistry.registerTileEntity(ApplianceSprayer.class, Taam.TILEENTITY_APPLIANCE_SPRAYER);

		GameRegistry.registerTileEntity(TileEntityPipe.class, Taam.TILEENTITY_PIPE);
		GameRegistry.registerTileEntity(TileEntityTank.class, Taam.TILEENTITY_TANK);
		GameRegistry.registerTileEntity(TileEntityCreativeWell.class, Taam.TILEENTITY_CREATIVEWELL);
		GameRegistry.registerTileEntity(TileEntityPump.class, Taam.TILEENTITY_PUMP);
		GameRegistry.registerTileEntity(TileEntityMixer.class, Taam.TILEENTITY_MIXER);

		OreGenerator worldgen = new OreGenerator();
		MinecraftForge.EVENT_BUS.register(worldgen);

		GameRegistry.registerWorldGenerator(worldgen, 2);
		
		FluidRegistry.enableUniversalBucket();

		boolean registerFluidBlocks = false;

		Enum<?>[] fluidsDyeValues = Taam.FLUID_DYE_META.values();
		fluidsDye = new FluidDye[fluidsDyeValues.length];
		blocksFluidDye = new BlockFluidClassic[fluidsDyeValues.length];
		for (int i = 0; i < fluidsDyeValues.length; i++) {
			fluidsDye[i] = new FluidDye(Taam.FLUID_DYE + fluidsDyeValues[i].name());
			FluidRegistry.registerFluid(fluidsDye[i]);
			FluidRegistry.addBucketForFluid(fluidsDye[i]);

			if (registerFluidBlocks) {
				BlockFluidClassic fluidBlock = new BlockFluidClassic(fluidsDye[i], Material.water);
				String blockName = "fluid.dye." + fluidsDyeValues[i].name();
				GameRegistry.registerBlock(fluidBlock, blockName);
				fluidBlock.setUnlocalizedName(blockName);
				fluidBlock.setCreativeTab(creativeTab);
				blocksFluidDye[i] = fluidBlock;
			}

		}

		network = NetworkRegistry.INSTANCE.newSimpleChannel(Taam.CHANNEL_NAME);
		proxy.registerPackets(network);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		proxy.registerRenderStuff();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		oreRegistration();
		TaamRecipes.addRecipes();
		// TaamRecipes.addSmeltingRecipes();
		TaamRecipes.addOreRecipes();

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	}

	public static void oreRegistration() {
		OreDictionary.registerOre("oreCopper", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.copper.ordinal()));
		OreDictionary.registerOre("oreTin", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.tin.ordinal()));
		OreDictionary.registerOre("oreAluminum", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.aluminum.ordinal()));
		OreDictionary.registerOre("oreBauxite", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.bauxite.ordinal()));
		OreDictionary.registerOre("oreKaolinte", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.kaolinite.ordinal()));

		OreDictionary.registerOre("ingotCopper", new ItemStack(itemIngot, 1, Taam.BLOCK_ORE_META.copper.ordinal()));
		OreDictionary.registerOre("ingotTin", new ItemStack(itemIngot, 1, Taam.BLOCK_ORE_META.tin.ordinal()));
		OreDictionary.registerOre("ingotAluminum", new ItemStack(itemIngot, 1, Taam.BLOCK_ORE_META.aluminum.ordinal()));

		OreDictionary.registerOre("dustIron", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.iron.ordinal()));
		OreDictionary.registerOre("dustCopper", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.copper.ordinal()));
		OreDictionary.registerOre("dustTin", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.tin.ordinal()));
		OreDictionary.registerOre("dustAluminum", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.aluminum.ordinal()));
		OreDictionary.registerOre("dustBauxite", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.bauxite.ordinal()));
		OreDictionary.registerOre("dustKaolinite", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.kaolinite.ordinal()));
		OreDictionary.registerOre("dustGold", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.gold.ordinal()));
		OreDictionary.registerOre("dustCoal", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.coal.ordinal()));

		OreDictionary.registerOre("nuggetIron", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.iron_nugget.ordinal()));

		OreDictionary.registerOre("materialPlastic", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.plastic_sheet.ordinal()));
		OreDictionary.registerOre("materialRubber", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()));
		OreDictionary.registerOre("itemRubber", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()));
		OreDictionary.registerOre("materialGraphite", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.graphite.ordinal()));
		OreDictionary.registerOre("materialSiliconWafer", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.silicon_wafer.ordinal()));
		OreDictionary.registerOre("materialResin", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()));

		OreDictionary.registerOre("partPhotocell", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.photocell.ordinal()));
		OreDictionary.registerOre("partMotor", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()));
		OreDictionary.registerOre("partBasicCircuit", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()));
		OreDictionary.registerOre("partAdvancedCircuit", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()));
	}
}
