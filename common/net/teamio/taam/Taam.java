package net.teamio.taam;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.teamio.taam.content.common.BlockOre;
import net.teamio.taam.content.piping.MachineFluidDrier;
import net.teamio.taam.content.piping.MachineMixer;
import net.teamio.taam.content.piping.MachinePipe;
import net.teamio.taam.content.piping.MachinePump;
import net.teamio.taam.content.piping.MachineTank;
import net.teamio.taam.conveyors.IConveyorApplianceMetaInfo;
import net.teamio.taam.conveyors.IConveyorSlots;
import net.teamio.taam.gui.advanced.IAdvancedMachineGUI;
import net.teamio.taam.integration.mcmultipart.MachineMultipart;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.machines.IMachineMetaInfo;
import net.teamio.taam.machines.IMachineWrapper;
import net.teamio.taam.machines.MachineBlock;
import net.teamio.taam.machines.MachineItemBlock;
import net.teamio.taam.machines.MachineTileEntity;
import net.teamio.taam.piping.IPipe;
import net.teamio.taam.rendering.TankRenderInfo;
import net.teamio.taam.util.TaamUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the main constants class for Taam.
 * It also includes references to the capabilities in use and holds additional metadata in enums.
 */
public final class Taam {

	/*
	 * Capabilities
	 *
	 * REMEMBER TO REGISTER THEM in TaamMain, end of preInit!
	 * Else there will be conflicts (null value!)
	 */

	@CapabilityInject(IPipe.class)
	public static Capability<IPipe> CAPABILITY_PIPE;
	@CapabilityInject(TankRenderInfo[].class)
	public static Capability<TankRenderInfo[]> CAPABILITY_RENDER_TANK;
	@CapabilityInject(IConveyorSlots.class)
	public static Capability<IConveyorSlots> CAPABILITY_CONVEYOR;
	@CapabilityInject(IAdvancedMachineGUI.class)
	public static Capability<IAdvancedMachineGUI> CAPABILITY_ADVANCED_GUI;

	public static final String MOD_ID = "taam";
	public static final String MOD_NAME = "Taam";
	public static final String MOD_VERSION = "@VERSION@";
	public static final String MOD_AUTHOR1 = "founderio";
	public static final String MOD_AUTHOR2 = "Xanderio";
	public static final String MOD_DESCRIPTION = "Taam: Tech and Accessories Mod\nTaam is open source, puplished under the MIT license. Feel free to poke around the source code! Any support is appreciated, so check out our Patreon page and our website and report any issues on GitHub!";
	public static final String MOD_CREDITS = "Taam is a mod by Team I/O";
	public static final String MOD_URL = "https://team-io.net/taam.php";
	public static final String MOD_UPDATE_URL = "https://team-io.net/taam-updates.php";
	public static final String MOD_LOGO_PATH = "/assets/taam/logo_80dpi_blue.png";

	/**
	 * Network channel name
	 */
	public static final String CHANNEL_NAME = "TAAM";
	/**
	 * Class name for the GUI factory (used for the config GUI)
	 * Factory class: {@link net.teamio.taam.gui.GuiFactory}
	 * Config GUI class: {@link net.teamio.taam.gui.ModGuiConfig}
	 */
	public static final String GUI_FACTORY_CLASS = "net.teamio.taam.gui.GuiFactory";

	/*
	 * Integration
	 */

	public static final String INTEGRATION_JEI_CAT_GRINDER = "taam.integration.jei.grinder";
	public static final String INTEGRATION_JEI_CAT_CRUSHER = "taam.integration.jei.crusher";
	public static final String INTEGRATION_JEI_CAT_FLUIDDRIER = "taam.integration.jei.fluiddrier";
	public static final String INTEGRATION_JEI_CAT_MIXER = "taam.integration.jei.mixer";
	public static final String INTEGRATION_JEI_CAT_SPRAYER = "taam.integration.jei.sprayer";

	public static final String INTEGRATION_JEI_CATNAME_GRINDER = "taam.integration.jei.categories.grinder";
	public static final String INTEGRATION_JEI_CATNAME_CRUSHER = "taam.integration.jei.categories.crusher";
	public static final String INTEGRATION_JEI_CATNAME_FLUIDDRIER = "taam.integration.jei.categories.fluiddrier";
	public static final String INTEGRATION_JEI_CATNAME_MIXER = "taam.integration.jei.categories.mixer";
	public static final String INTEGRATION_JEI_CATNAME_SPRAYER = "taam.integration.jei.categories.sprayer";

	public static final String INTEGRATION_JEI_LORE_INTERNAL_CAPACITY = "taam.integration.jei.lore.internalcapacity";
	public static final String INTEGRATION_JEI_LORE_INTERNAL_CAPACITY_IN = "taam.integration.jei.lore.internalcapacity_in";
	public static final String INTEGRATION_JEI_LORE_INTERNAL_CAPACITY_OUT = "taam.integration.jei.lore.internalcapacity_out";

	/*
	 * Sounds
	 */

	public static final ResourceLocation SOUND_SIP_AH = new ResourceLocation("taam", "sip_ah");

	/*
	 * Blocks
	 */

	public static final String BLOCK_LAMP = "lamp";
	public static final String BLOCK_LAMP_INVERTED = "lampInverted";
	public static final String BLOCK_SENSOR = "sensor";
	public static final String BLOCK_MACHINES = "machines";
	public static final String BLOCK_PRODUCTIONLINE = "productionline";
	public static final String BLOCK_PRODUCTIONLINE_ATTACHABLE = "productionline_attachable";
	public static final String BLOCK_PRODUCTIONLINE_APPLIANCE = "productionline_appliance";
	public static final String BLOCK_SENSOR_MOTION = "sensor.motion";
	public static final String BLOCK_SENSOR_MINECT = "sensor.minect";
	public static final String BLOCK_ORE = "ore";
	public static final String BLOCK_CONCRETE = "concrete";
	public static final String BLOCK_SUPPORT_BEAM = "support_beam";

	/**
	 * Wrapper for multipart-based machines, if multipart is not found or disabled via config.
	 * <p>
	 * See {@link MACHINE_META} for the single meta values (not actually block metadata.)
	 */
	public static final String BLOCK_MACHINE_WRAPPER = "machine";

	/*
	 * Items
	 */

	public static final String ITEM_DEBUG_TOOL = "debugger";
	public static final String ITEM_WRENCH = "wrench";
	public static final String ITEM_MATERIAL = "material";
	public static final String ITEM_PART = "part";
	public static final String ITEM_TOOL = "tool";
	public static final String ITEM_INGOT = "ingot";
	public static final String ITEM_DUST = "dust";

	/*
	 * TileEntities
	 */

	public static final String TILEENTITY_SENSOR = "sensor";
	public static final String TILEENTITY_CHUTE = "chute";
	public static final String TILEENTITY_CREATIVECACHE = "creativecache";

	public static final String TILEENTITY_CONVEYOR = "conveyor";
	public static final String TILEENTITY_CONVEYOR_HOPPER = "conveyor_hopper";
	public static final String TILEENTITY_CONVEYOR_PROCESSOR = "conveyor_processor";
	public static final String TILEENTITY_CONVEYOR_ITEMBAG = "itembag";
	public static final String TILEENTITY_CONVEYOR_TRASHCAN = "trashcan";
	public static final String TILEENTITY_CONVEYOR_SIEVE = "sieve";
	public static final String TILEENTITY_CONVEYOR_ELEVATOR = "elevator";

	public static final String TILEENTITY_CREATIVEWELL = "creativewell";

	public static final String TILEENTITY_APPLIANCE_SPRAYER = "appliance.sprayer";
	public static final String TILEENTITY_APPLIANCE_ALIGNER = "appliance.aligner";

	public static final String TILEENTITY_MACHINE_WRAPPER = "machine_wrapper";

	/*
	 * Fluids
	 */

	public static final String FLUID_DYE = "dye_";

	/*
	 * Metadata
	 */

	public enum BLOCK_ORE_META implements IStringSerializable {
		/*0*/copper(true, true, "Copper", 14, 7, 0, 59),
		/*1*/tin(true, true, "Tin", 13, 7, 0, 59),
		/*2*/aluminum(true, true, "Aluminum", 2, 3, 0, 59),
		/*3*/bauxite(false, true, "Bauxite", 35, 10, 0, 128),  //No Ingot
		/*4*/kaolinite(false, true, "Kaolinite", 35, 5, 0, 100), //No Ingot
		// Reserved for future use as blocks
		/*5*/reserved1(false, false),
		/*6*/reserved2(false, false),
		/*7*/reserved3(false, false),
		/*8*/reserved4(false, false),
		/*9*/reserved5(false, false),
		/*10*/reserved6(false, false),
		/*11*/reserved7(false, false),
		/*12*/reserved8(false, false),
		/*13*/reserved9(false, false),
		/*14*/reserved10(false, false),
		/*15*/reserved11(false, false),

		//Vanilla requires only the "custom" stuff
		/*16*/gold(false, true),
		/*17*/iron(false, true),
		/*18*/coal(false, true),

		// Non-Ore stuff
		/*19*/stone(false, true),
		;

		public final boolean ore, ingot, dust;

		public final int gen_default_size, gen_default_count, gen_default_above, gen_default_below;

		public final String config_name;

		BLOCK_ORE_META(boolean ingot, boolean dust) {
			ore = false;
			this.ingot = ingot;
			this.dust = dust;
			gen_default_size = 0;
			gen_default_count = 0;
			gen_default_above = 0;
			gen_default_below = 0;
			config_name = name();
		}

		BLOCK_ORE_META(boolean ingot, boolean dust, String config_name, int default_size, int default_count, int default_above, int default_below) {
			ore = true;
			this.ingot = ingot;
			this.dust = dust;
			gen_default_size = default_size;
			gen_default_count = default_count;
			gen_default_above = default_above;
			gen_default_below = default_below;
			this.config_name = config_name;
		}

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}

		@Nonnull
		@Override
		public String getName() {
			return name();
		}

		public static IBlockState getOre(Taam.BLOCK_ORE_META ore) {
			return TaamMain.blockOre.getDefaultState().withProperty(BlockOre.VARIANT, ore);
		}
	}

	public enum BLOCK_CONCRETE_META implements IStringSerializable {
		rough,
		rough_chiseled,
		fine,
		fine_chiseled,
		coated,
		coated_chiseled,
		black,
		black_chiseled,
		warn1,
		warn2;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}

		@Nonnull
		@Override
		public String getName() {
			return name();
		}
	}

	/**
	 * Non-Multipart machines that are not part of the conveyor-system.
	 *
	 * @author Oliver Kahrmann
	 */
	public enum BLOCK_MACHINES_META implements IStringSerializable {
		chute,
		creativecache,
		creativewell;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}

		@Nonnull
		@Override
		public String getName() {
			return name();
		}
	}

	/**
	 * Productionline-Machines, i.e. part of the conveyor system.
	 *
	 * @author Oliver Kahrmann
	 */
	public enum BLOCK_PRODUCTIONLINE_META implements IStringSerializable {
		conveyor1,
		conveyor2,
		conveyor3,
		hopper,
		hopper_hs,
		sieve,
		shredder,
		grinder,
		crusher,
		chute,
		elevator;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}

		@Nonnull
		@Override
		public String getName() {
			return name();
		}
	}

	public enum BLOCK_PRODUCTIONLINE_ATTACHABLE_META implements IStringSerializable {
		itembag,
		trashcan;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}

		@Nonnull
		@Override
		public String getName() {
			return name();
		}
	}

	public enum BLOCK_PRODUCTIONLINE_APPLIANCE_META implements IConveyorApplianceMetaInfo {
		sprayer,
		aligner;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}

		@Nonnull
		@Override
		public String getName() {
			return name();
		}

		@Override
		public int metaData() {
			return ordinal();
		}

		@Nonnull
		@Override
		public String unlocalizedName() {
			return name();
		}

		@Override
		public boolean isDirectionSupported(EnumFacing dir) {
			return dir.getAxis() != EnumFacing.Axis.Y;
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			// So far, no additional info
		}
	}

	public enum ITEM_TOOL_META {
		saw;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}
	}

	public enum ITEM_MATERIAL_META {
		plastic_sheet,
		rubber_bar,
		graphite,
		iron_nugget,
		aluminum_nugget,
		copper_nugget,
		tin_nugget,
		silicon_wafer,
		wooden_board,
		aluminum_plate,
		resin,
		cement,
		cementRough,
		pigment_black,
		pigment_red,
		pigment_green,
		pigment_brown,
		pigment_blue,
		pigment_purple,
		pigment_cyan,
		pigment_lightGray,
		pigment_gray,
		pigment_pink,
		pigment_lime,
		pigment_yellow,
		pigment_lightBlue,
		pigment_magenta,
		pigment_orange,
		pigment_white;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}
	}

	public enum ITEM_PART_META {
		photocell,
		motor,
		support_frame_wood,
		support_frame,
		circuit_basic,
		circuit_advanced,
		logistics_chip,
		rubber_band,
		pump,
		tank,
		nozzle,
		magnetic_coil,
		iron_frame,
		metal_bearing,
		copper_wire,
		sieve,
		redirector,
		wooden_band;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}
	}

	public enum FLUID_DYE_META {
		black,
		red,
		green,
		brown,
		blue,
		purple,
		cyan,
		lightGray,
		gray,
		pink,
		lime,
		yellow,
		lightBlue,
		magenta,
		orange,
		white;

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}
	}

	public enum FLUID_MATERIAL_META {
		concreteFine("concreteFine", 2000, 8000),
		concreteRough("concreteRough", 2000, 10000),
		coating("coating", 900, 4000);

		public final String registryName;
		public final int viscosity;
		public final int density;

		FLUID_MATERIAL_META(String registryName, int viscosity, int density) {
			this.registryName = registryName;
			this.viscosity = viscosity;
			this.density = density;
		}

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}
	}

	/**
	 * Meta info for all multipart-based machines. Used for items, the wrapper block & multipart registry.
	 * <p>
	 * {@link MachineBlock} + {@link MachineTileEntity} -> Wrapper block & TE if multipart is not available.
	 * <p>
	 * {@link MachineItemBlock} -> Item for the wrapper block, mutually exclusive with FIXME not anymore MachineItemMultipart.
	 * Only one of them is used, depending on availability of multipart.
	 * <p>
	 * {@link MachineMultipart} -> Multipart wrapper for all these machines.
	 *
	 * @author Oliver Kahrmann
	 */
	public enum MACHINE_META implements IMachineMetaInfo {

		pipe(MachinePipe.class, "pipe", null),
		tank(MachineTank.class, "tank", null),
		pump(MachinePump.class, "pump", null),
		mixer(MachineMixer.class, "mixer", null),
		fluid_drier(MachineFluidDrier.class, "fluid_drier", null);

		private Class<? extends IMachine> machineClass;
		private String unlocalizedName;
		private String[] info;


		/**
		 * @param machineClass    Implementation of the machine logic
		 * @param unlocalizedName The unlocalized name for registration and translation
		 * @param info            Information added to the item tooltip
		 */
		MACHINE_META(Class<? extends IMachine> machineClass, String unlocalizedName, String[] info) {
			this.machineClass = machineClass;
			this.unlocalizedName = unlocalizedName;
			this.info = info;
		}

		/*
		 * IMachineMetaInfo implementation
		 */

		@Override
		public IMachine createMachine(IMachineWrapper wrapper) {
			try {
				IMachine machine = machineClass.newInstance();
				machine.setWrapper(wrapper);
				return machine;
			} catch (InstantiationException | IllegalAccessException e) {
				Log.error("Could not create machine instance. Returning null. THIS IS AN ERROR, please report!", e);
			}
			return null;
		}

		@Override
		public int metaData() {
			return ordinal();
		}

		@Override
		public String unlocalizedName() {
			return unlocalizedName;
		}

		@Override
		public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
			if (info != null) {
				Collections.addAll(tooltip, info);
			}
		}

		/*
		 * IStringSerializable implementation
		 */

		@Nonnull
		@Override
		public String getName() {
			return unlocalizedName();
		}

		/*
		 * Static stuff
		 */

		private static final Map<String, MACHINE_META> nameToInstanceMap = new HashMap<>();

		static {
			for (MACHINE_META value : values()) {
				nameToInstanceMap.put(value.unlocalizedName(), value);
			}
		}

		public static IMachineMetaInfo fromId(String id) {
			return nameToInstanceMap.get(id);
		}

		public static String[] valuesAsString() {
			return TaamUtil.enumValuesAsString(values());
		}
	}

	private Taam() {
		//Util Class
	}
}
