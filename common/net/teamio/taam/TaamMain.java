package net.teamio.taam;

import java.util.List;

import net.teamio.blockunit.UnitTesterItem;
import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
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
import net.teamio.taam.Taam.FLUID_MATERIAL_META;
import net.teamio.taam.Taam.ITEM_PART_META;
import net.teamio.taam.content.ItemWithMetadata;
import net.teamio.taam.content.ItemWithMetadata.ItemDelegate;
import net.teamio.taam.content.common.BlockBuilding;
import net.teamio.taam.content.common.BlockLamp;
import net.teamio.taam.content.common.BlockMachines;
import net.teamio.taam.content.common.BlockOre;
import net.teamio.taam.content.common.BlockSensor;
import net.teamio.taam.content.common.BlockSlidingDoor;
import net.teamio.taam.content.common.BlockSupportBeam;
import net.teamio.taam.content.common.FluidDye;
import net.teamio.taam.content.common.FluidMaterial;
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
import net.teamio.taam.content.conveyors.TileEntityConveyorElevator;
import net.teamio.taam.content.conveyors.TileEntityConveyorHopper;
import net.teamio.taam.content.conveyors.TileEntityConveyorItemBag;
import net.teamio.taam.content.conveyors.TileEntityConveyorProcessor;
import net.teamio.taam.content.conveyors.TileEntityConveyorSieve;
import net.teamio.taam.content.conveyors.TileEntityConveyorTrashCan;
import net.teamio.taam.content.piping.TileEntityCreativeWell;
import net.teamio.taam.conveyors.ConveyorSlotsStandard;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.conveyors.appliances.ApplianceAligner;
import net.teamio.taam.conveyors.appliances.ApplianceSprayer;
import net.teamio.taam.gui.GuiHandler;
import net.teamio.taam.gui.advanced.IAdvancedMachineGUI;
import net.teamio.taam.machines.MachineBlock;
import net.teamio.taam.machines.MachineItemBlock;
import net.teamio.taam.machines.MachineItemMultipart;
import net.teamio.taam.machines.MachineTileEntity;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.piping.PipeEnd;
import net.teamio.taam.rendering.TankRenderInfo;


@Mod(modid = Taam.MOD_ID, name = Taam.MOD_NAME, version = Taam.MOD_VERSION, guiFactory = Taam.GUI_FACTORY_CLASS, updateJSON = Taam.MOD_UPDATE_URL)
public class TaamMain {

	static {
		FluidRegistry.enableUniversalBucket();
	}

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

	/**
	 * Wrapper block for multipart machines. Only used if multipart is disabled,
	 * but always loaded for worlds containing the wrapper blocks.
	 */
	public static MachineBlock blockMachine;
	/**
	 * Either {@link MachineItemBlock} or {@link MachineItemMultipart},
	 * depending on availability of multipart.
	 */
	public static Item itemMachine;

	public static UnitTesterItem unitTesterItem;

	public static CreativeTabs creativeTab;

	public static BlockLamp blockLamp;
	public static BlockLamp blockLampInverted;
	public static BlockSensor blockSensor;
	public static BlockMachines blockMachines;
	public static BlockProductionLine blockProductionLine;
	public static BlockProductionLineAttachable blockProductionLineAttachable;
	public static BlockProductionLineAppliance blockProductionLineAppliance;
	public static BlockSlidingDoor blockSlidingDoor;
	public static BlockOre blockOre;
	public static BlockBuilding blockConcrete;
	public static BlockSupportBeam blockSupportBeam;

	public static FluidDye[] fluidsDye;
	public static BlockFluidClassic[] blocksFluidDye;
	public static FluidMaterial[] fluidsMaterial;
	public static BlockFluidFinite[] blocksFluidMaterial;

	public static DamageSource ds_processed = new DamageSource("taam.processed").setDamageBypassesArmor();
	public static DamageSource ds_shredded = new DamageSource("taam.shredded").setDamageBypassesArmor();
	public static DamageSource ds_ground = new DamageSource("taam.ground").setDamageBypassesArmor();
	public static DamageSource ds_crushed = new DamageSource("taam.crushed").setDamageBypassesArmor();
	public static DamageSource ds_reconfigured = new DamageSource("taam.reconfigured").setDamageIsAbsolute();

	public static SoundEvent soundSipAh;

	private static void registerBlock(Block block, ItemBlock item, String name) {
		registerBlock(block, name);
		registerItem(item, name);
	}

	private static void registerBlock(Block block, String name) {
		block.setUnlocalizedName(Taam.MOD_ID + "." + name);
		block.setCreativeTab(creativeTab);
		block.setRegistryName(name);
		GameRegistry.register(block);
	}

	private static void registerItem(Item item, String name) {
		item.setUnlocalizedName(Taam.MOD_ID + "." + name);
		item.setCreativeTab(creativeTab);
		item.setRegistryName(name);
		GameRegistry.register(item);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		/*
		 * Metadata
		 */
		ModMetadata meta = event.getModMetadata();
		meta.authorList.add(Taam.MOD_AUTHOR1);
		meta.authorList.add(Taam.MOD_AUTHOR2);
		meta.description = Taam.MOD_DESCRIPTION;
		meta.logoFile = Taam.MOD_LOGO_PATH;
		meta.url = Taam.MOD_URL;
		meta.updateJSON = Taam.MOD_UPDATE_URL;
		meta.credits = Taam.MOD_CREDITS;
		meta.autogenerated = false;

		/*
		 * Some general stuff that needs to be registered
		 */

		MinecraftForge.EVENT_BUS.register(new TaamCraftingHandler());
		MinecraftForge.EVENT_BUS.register(new Config());
		MinecraftForge.EVENT_BUS.register(proxy);

		/*
		 * Read Config
		 */

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

		/*
		 * Check for mcmultipart
		 */
		Config.multipart_present = Loader.isModLoaded("mcmultipart");

		if (Config.multipart_present) {
			if (Config.multipart_load) {
				Log.info("Enabling multipart support");
			} else {
				Log.info("Config forced me to disabling multipart support although multipart is available.");
			}
		} else if (Config.multipart_load) {
			Log.warn("Config has multipart enabled, but it was not found. Multipart support will not be loaded.");
			Config.multipart_load = false;
		}

		/*
		 * Register Stuff
		 */

		registerBlock(
				blockLamp = new BlockLamp(false),
				new ItemBlock(blockLamp),
				Taam.BLOCK_LAMP
				);
		registerBlock(
				blockLampInverted = new BlockLamp(true),
				new ItemBlock(blockLampInverted),
				Taam.BLOCK_LAMP_INVERTED
		);
		registerBlock(
				blockSensor = new BlockSensor(),
				new ItemBlock(blockSensor),
				Taam.BLOCK_SENSOR
				);

		registerBlock(
				blockMachines = new BlockMachines(),
				new ItemMultiTexture(blockMachines, blockMachines, Taam.BLOCK_MACHINES_META.valuesAsString()),
				Taam.BLOCK_MACHINES
				);

		registerBlock(
				blockProductionLine = new BlockProductionLine(),
				new ItemProductionLine(blockProductionLine, Taam.BLOCK_PRODUCTIONLINE_META.valuesAsString()),
				Taam.BLOCK_PRODUCTIONLINE
				);

		registerBlock(
				blockProductionLineAttachable = new BlockProductionLineAttachable(),
				new ItemAttachable(blockProductionLineAttachable, Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE_META.valuesAsString()),
				Taam.BLOCK_PRODUCTIONLINE_ATTACHABLE
				);

		registerBlock(
				blockProductionLineAppliance = new BlockProductionLineAppliance(),
				new ItemAppliance(blockProductionLineAppliance, Taam.BLOCK_PRODUCTIONLINE_APPLIANCE_META.values()),
				Taam.BLOCK_PRODUCTIONLINE_APPLIANCE
				);

		registerBlock(
				blockOre = new BlockOre(),
				new ItemMultiTexture(blockOre, blockOre, Taam.BLOCK_ORE_META.valuesAsString()),
				Taam.BLOCK_ORE
				);

		registerBlock(
				blockConcrete = new BlockBuilding(),
				new ItemMultiTexture(blockConcrete, blockConcrete, Taam.BLOCK_CONCRETE_META.valuesAsString()),
				Taam.BLOCK_CONCRETE
				);

		registerBlock(
				blockSupportBeam = new BlockSupportBeam(),
				new ItemBlock(blockSupportBeam),
				Taam.BLOCK_SUPPORT_BEAM
				);

		registerItem(unitTesterItem = new UnitTesterItem(), Taam.ITEM_DEBUG_UNIT_TESTER);
		registerItem(itemDebugTool = new ItemDebugTool(), Taam.ITEM_DEBUG_TOOL);
		registerItem(itemWrench = new ItemWrench(), Taam.ITEM_WRENCH);
		registerItem(itemSaw = new ItemTool(Taam.ITEM_TOOL_META.saw), Taam.ITEM_TOOL + "." + Taam.ITEM_TOOL_META.saw.name());

		registerItem(itemMaterial = new ItemWithMetadata<Taam.ITEM_MATERIAL_META>("material", Taam.ITEM_MATERIAL_META.values(), null), Taam.ITEM_MATERIAL);
		registerItem(itemPart = new ItemWithMetadata<Taam.ITEM_PART_META>("part", Taam.ITEM_PART_META.values(),
				new ItemDelegate<Taam.ITEM_PART_META>() {
					@Override
					public void addInformation(ItemStack stack, EntityPlayer player, List<String> lines,
							boolean detailedInfoSetting) {
						if(stack.getMetadata() == Taam.ITEM_PART_META.redirector.ordinal()) {
							String usage = I18n.format("lore.taam.redirector.usage", new Object[0]);
							// Split at literal \n in the translated text. a lot of escaping here.
							String[] split = usage.split("\\\\n");
							for (int i = 0; i < split.length; i++) {
								lines.add(split[i]);
							}
						}
					}
					@Override
					public boolean isValidMetadata(ITEM_PART_META meta) {
						return true;
					}
				}), Taam.ITEM_PART);
		registerItem(itemIngot = new ItemWithMetadata<Taam.BLOCK_ORE_META>("ingot", Taam.BLOCK_ORE_META.values(),
				new ItemDelegate<Taam.BLOCK_ORE_META>() {
					@Override
					public boolean isValidMetadata(Taam.BLOCK_ORE_META meta) {
						return meta.ingot;
					}

					@Override
					public void addInformation(ItemStack stack, EntityPlayer player, List<String> lines,
							boolean detailedInfoSetting) {
						if(stack.getMetadata() == Taam.BLOCK_ORE_META.iron.ordinal() ||
								stack.getMetadata() == Taam.BLOCK_ORE_META.gold.ordinal()) {
							String usage = I18n.format("lore.taam.ingots.cheaty", new Object[0]);
							// Split at literal \n in the translated text. a lot of escaping here.
							String[] split = usage.split("\\\\n");
							for (int i = 0; i < split.length; i++) {
								lines.add(split[i]);
							}
						}
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

		/*
		 * Tile Entities
		 */

		GameRegistry.registerTileEntity(TileEntitySensor.class, Taam.TILEENTITY_SENSOR);
		GameRegistry.registerTileEntity(TileEntityChute.class, Taam.TILEENTITY_CHUTE);
		GameRegistry.registerTileEntity(TileEntityCreativeCache.class, Taam.TILEENTITY_CREATIVECACHE);

		GameRegistry.registerTileEntity(TileEntityConveyor.class, Taam.TILEENTITY_CONVEYOR);
		GameRegistry.registerTileEntity(TileEntityConveyorHopper.class, Taam.TILEENTITY_CONVEYOR_HOPPER);
		GameRegistry.registerTileEntity(TileEntityConveyorProcessor.class, Taam.TILEENTITY_CONVEYOR_PROCESSOR);
		GameRegistry.registerTileEntity(TileEntityConveyorItemBag.class, Taam.TILEENTITY_CONVEYOR_ITEMBAG);
		GameRegistry.registerTileEntity(TileEntityConveyorTrashCan.class, Taam.TILEENTITY_CONVEYOR_TRASHCAN);
		GameRegistry.registerTileEntity(TileEntityConveyorSieve.class, Taam.TILEENTITY_CONVEYOR_SIEVE);
		GameRegistry.registerTileEntity(TileEntityConveyorElevator.class, Taam.TILEENTITY_CONVEYOR_ELEVATOR);

		GameRegistry.registerTileEntity(ApplianceSprayer.class, Taam.TILEENTITY_APPLIANCE_SPRAYER);
		GameRegistry.registerTileEntity(ApplianceAligner.class, Taam.TILEENTITY_APPLIANCE_ALIGNER);

		GameRegistry.registerTileEntity(TileEntityCreativeWell.class, Taam.TILEENTITY_CREATIVEWELL);

		/*
		 * Multiparts
		 */

		GameRegistry.registerTileEntity(MachineTileEntity.class, Taam.TILEENTITY_MACHINE_WRAPPER);


		if(Config.multipart_load) {
			MultipartHandler.registerMultipartStuff();
		}

		Taam.MACHINE_META[] machine_meta_values = Taam.MACHINE_META.values();
		/*
		 * Wrapper block for machines if multipart is not available
		 */
		registerBlock(
				blockMachine = new MachineBlock(machine_meta_values),
				Taam.BLOCK_MACHINE_WRAPPER
				);

		/*
		 * Either Multipart or regular items
		 */
		if(Config.multipart_load && Config.multipart_register_items) {
			// Multipart Item
			itemMachine = MultipartHandler.createMultipartItem(machine_meta_values);
		} else {
			// Regular item, places a wrapper block
			itemMachine = new MachineItemBlock(blockMachine, machine_meta_values);
		}
		registerItem(itemMachine, Taam.BLOCK_MACHINE_WRAPPER);

		/*
		 * Worldgen
		 */

		OreGenerator worldgen = new OreGenerator();
		MinecraftForge.EVENT_BUS.register(worldgen);

		GameRegistry.registerWorldGenerator(worldgen, 2);

		/*
		 * Fluids
		 */

		boolean registerFluidBlocks = true;

		Taam.FLUID_DYE_META[] fluidsDyeValues = Taam.FLUID_DYE_META.values();
		fluidsDye = new FluidDye[fluidsDyeValues.length];
		blocksFluidDye = new BlockFluidClassic[fluidsDyeValues.length];

		for (int i = 0; i < fluidsDyeValues.length; i++) {
			fluidsDye[i] = new FluidDye(Taam.FLUID_DYE + fluidsDyeValues[i].name());
			FluidRegistry.registerFluid(fluidsDye[i]);
			FluidRegistry.addBucketForFluid(fluidsDye[i]);

			if (registerFluidBlocks) {
				BlockFluidClassic fluidBlock = new BlockFluidClassic(fluidsDye[i], Material.WATER) {
					@Override
					public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
						IBlockState neighbor = world.getBlockState(pos.offset(side));
						// Force rendering if there is a different block adjacent, not only a different material
						if (neighbor.getBlock() != this)
				        {
				            return true;
				        }
				        return super.shouldSideBeRendered(state, world, pos, side);
					}
				};
				String blockName = "fluid.dye." + fluidsDyeValues[i].name();
				registerBlock(
						fluidBlock,
						new ItemBlock(fluidBlock),
						blockName);
				blocksFluidDye[i] = fluidBlock;
			}

		}

		Taam.FLUID_MATERIAL_META[] fluidsMaterialValues = Taam.FLUID_MATERIAL_META.values();
		fluidsMaterial = new FluidMaterial[fluidsMaterialValues.length];
		blocksFluidMaterial = new BlockFluidFinite[fluidsMaterialValues.length];

		for(int i = 0; i < fluidsMaterialValues.length; i++) {
			fluidsMaterial[i] = new FluidMaterial(fluidsMaterialValues[i]);
			FluidRegistry.registerFluid(fluidsMaterial[i]);
			FluidRegistry.addBucketForFluid(fluidsMaterial[i]);

			if (registerFluidBlocks) {
				BlockFluidFinite fluidBlock = new BlockFluidFinite(fluidsMaterial[i], Material.WATER) {
					@Override
					public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
						IBlockState neighbor = world.getBlockState(pos.offset(side));
						// Force rendering if there is a different block adjacent, not only a different material
				        if (neighbor.getBlock() != this)
				        {
				            return true;
				        }
				        return super.shouldSideBeRendered(state, world, pos, side);
					}
				};
				if(fluidsMaterialValues[i] == FLUID_MATERIAL_META.coating) {
					fluidBlock.setQuantaPerBlock(2);
				} else {
					fluidBlock.setQuantaPerBlock(1);
				}
				String blockName = "fluid.material." + fluidsMaterialValues[i].name();
				registerBlock(
						fluidBlock,
						new ItemBlock(fluidBlock),
						blockName);
				blocksFluidMaterial[i] = fluidBlock;
			}
		}

		/*
		 * Capabilities
		 */

		CapabilityManager.INSTANCE.register(IPipe.class, new Capability.IStorage<IPipe>() {

			@Override
			public NBTBase writeNBT(Capability<IPipe> capability, IPipe instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<IPipe> capability, IPipe instance, EnumFacing side, NBTBase nbt) {
			}

		}, PipeEnd.class);
		CapabilityManager.INSTANCE.register(TankRenderInfo[].class, new Capability.IStorage<TankRenderInfo[]>() {

			@Override
			public NBTBase writeNBT(Capability<TankRenderInfo[]> capability, TankRenderInfo[] instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<TankRenderInfo[]> capability, TankRenderInfo[] instance, EnumFacing side, NBTBase nbt) {
			}

		}, TankRenderInfo[].class);

		CapabilityManager.INSTANCE.register(IConveyorSlots.class, new Capability.IStorage<IConveyorSlots>() {

			@Override
			public NBTBase writeNBT(Capability<IConveyorSlots> capability, IConveyorSlots instance, EnumFacing side) {
				if(instance instanceof ConveyorSlotsStandard) {
					return ((ConveyorSlotsStandard) instance).serializeNBT();
				}
				throw new NotImplementedException("Cannot save a generic IConveyorSlots instance to NBT. Only ConveyorSlotsStandard is supported.");
			}

			@Override
			public void readNBT(Capability<IConveyorSlots> capability, IConveyorSlots instance, EnumFacing side,
					NBTBase nbt) {
				if(instance instanceof ConveyorSlotsStandard) {
					((ConveyorSlotsStandard) instance).deserializeNBT((NBTTagList) nbt);
				}
				throw new NotImplementedException("Cannot read a generic IConveyorSlots instance from NBT. Only ConveyorSlotsStandard is supported.");

			}

		}, ConveyorSlotsStandard.class);

		CapabilityManager.INSTANCE.register(IAdvancedMachineGUI.class, new Capability.IStorage<IAdvancedMachineGUI>() {

			@Override
			public NBTBase writeNBT(Capability<IAdvancedMachineGUI> capability, IAdvancedMachineGUI instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<IAdvancedMachineGUI> capability, IAdvancedMachineGUI instance, EnumFacing side, NBTBase nbt) {
			}
		}, IAdvancedMachineGUI.class);

		/*
		 * Check if registry of capabilities was successful
		 */
		if(Taam.CAPABILITY_PIPE == null) {
			throw new RuntimeException("Registering a capability failed (Taam.CAPABILITY_PIPE - IPipe) - field was null after registry.");
		}
		if(Taam.CAPABILITY_RENDER_TANK == null) {
			throw new RuntimeException("Registering a capability failed (Taam.CAPABILITY_RENDER_TANK - TankRenderInfo[]) - field was null after registry.");
		}
		if(Taam.CAPABILITY_CONVEYOR == null) {
			throw new RuntimeException("Registering a capability failed (Taam.CAPABILITY_CONVEYOR - IConveyorSlots) - field was null after registry.");
		}
		if(Taam.CAPABILITY_ADVANCED_GUI == null) {
			throw new RuntimeException("Registering a capability failed (Taam.CAPABILITY_ADVANCED_GUI - IAdvancedMachineGUI) - field was null after registry.");
		}

		/*
		 * Sounds
		 */

		soundSipAh = new SoundEvent(Taam.SOUND_SIP_AH);
		soundSipAh.setRegistryName(Taam.SOUND_SIP_AH);
		GameRegistry.register(soundSipAh);

		/*
		 * Network
		 */

		network = NetworkRegistry.INSTANCE.newSimpleChannel(Taam.CHANNEL_NAME);
		proxy.registerPackets(network);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		/*
		 * Rendering & GUI
		 */

		proxy.registerRenderStuff();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		/*
		 * Recipe Stuff
		 */

		// Register things with the ore dictionary
		oreRegistration();

		// Machine Recipes
		TaamRecipesCrusher.registerRecipes();
		TaamRecipesFluidDrier.registerRecipes();
		TaamRecipesGrinder.registerRecipes();
		TaamRecipesMixer.registerRecipes();
		TaamRecipesSprayer.registerRecipes();

		// Smelting & Crafting
		TaamRecipes.registerSmeltingRecipes();
		TaamRecipes.registerCraftingRecipes();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		// Compat Recipes -> in postInit to catch as many recipes as possible
		TaamRecipeCompat.registerRecipes();
	}

	public static void oreRegistration() {
		OreDictionary.registerOre("oreCopper", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.copper.ordinal()));
		OreDictionary.registerOre("oreTin", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.tin.ordinal()));
		OreDictionary.registerOre("oreAluminum", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.aluminum.ordinal()));
		OreDictionary.registerOre("oreBauxite", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.bauxite.ordinal()));
		OreDictionary.registerOre("oreKaolinite", new ItemStack(blockOre, 1, Taam.BLOCK_ORE_META.kaolinite.ordinal()));

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
		OreDictionary.registerOre("dustStone", new ItemStack(itemDust, 1, Taam.BLOCK_ORE_META.stone.ordinal()));

		OreDictionary.registerOre("nuggetIron", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.iron_nugget.ordinal()));
		OreDictionary.registerOre("nuggetCopper", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.copper_nugget.ordinal()));
		OreDictionary.registerOre("nuggetAluminum", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.aluminum_nugget.ordinal()));
		OreDictionary.registerOre("nuggetTin", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.tin_nugget.ordinal()));

		OreDictionary.registerOre("materialPlastic", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.plastic_sheet.ordinal()));
		OreDictionary.registerOre("materialRubber", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()));
		OreDictionary.registerOre("itemRubber", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.rubber_bar.ordinal()));
		OreDictionary.registerOre("materialGraphite", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.graphite.ordinal()));
		OreDictionary.registerOre("materialSiliconWafer", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.silicon_wafer.ordinal()));
		OreDictionary.registerOre("materialResin", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.resin.ordinal()));
		OreDictionary.registerOre("materialCement", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.cement.ordinal()));
		OreDictionary.registerOre("materialRoughCement", new ItemStack(itemMaterial, 1, Taam.ITEM_MATERIAL_META.cementRough.ordinal()));

		OreDictionary.registerOre("partPhotocell", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.photocell.ordinal()));
		OreDictionary.registerOre("partMotor", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.motor.ordinal()));
		OreDictionary.registerOre("partBasicCircuit", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.circuit_basic.ordinal()));
		OreDictionary.registerOre("partAdvancedCircuit", new ItemStack(itemPart, 1, Taam.ITEM_PART_META.circuit_advanced.ordinal()));

		String[] dyes = {
				"Black",
				"Red",
				"Green",
				"Brown",
				"Blue",
				"Purple",
				"Cyan",
				"LightGray",
				"Gray",
				"Pink",
				"Lime",
				"Yellow",
				"LightBlue",
				"Magenta",
				"Orange",
				"White"
		};

		int metaBlack = Taam.ITEM_MATERIAL_META.pigment_black.ordinal();
		for(int dyeMeta = 0; dyeMeta < 16; dyeMeta++) {

			OreDictionary.registerOre("dye" + dyes[dyeMeta], new ItemStack(TaamMain.itemMaterial, 1, metaBlack + dyeMeta));
		}
	}
}
