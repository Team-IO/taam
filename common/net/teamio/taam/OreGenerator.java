package net.teamio.taam;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.teamio.taam.Taam.BLOCK_ORE_META;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OreGenerator implements IWorldGenerator {

	public List<GenerationInfo> gens;

	public static class GenerationInfo {
		public final WorldGenMinable gen;
		public final int generateAbove;
		public final int generateBelow;
		public final int maxDepositCount;
		// For Debug Purposes
		public final Taam.BLOCK_ORE_META ore;

		public GenerationInfo(Taam.BLOCK_ORE_META ore, WorldGenMinable gen, int generateAbove,
		                      int generateBelow, int maxDepositCount) {
			this.ore = ore;
			this.gen = gen;
			this.generateAbove = generateAbove;
			this.generateBelow = generateBelow;
			this.maxDepositCount = maxDepositCount;
		}
	}

	public OreGenerator() {
		reloadGenerationInfo();
	}


	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		// Reload when the config changes to apply it BEFORE restart.
		if (event.getModID().equalsIgnoreCase(Taam.MOD_ID)) {
			reloadGenerationInfo();
		}
	}

	public void reloadGenerationInfo() {
		Log.info("Reloading Ore Gen info");
		gens = new ArrayList<GenerationInfo>();
		Predicate<IBlockState> stone = new Predicate<IBlockState>() {

			@Override
			public boolean apply(IBlockState input) {
				return input != null && input.getBlock() == Blocks.STONE;
			}
		};
		Taam.BLOCK_ORE_META[] oreMeta = Taam.BLOCK_ORE_META.values();
		for (int i = 0; i < Config.NUM_ORES; i++) {
			if (Config.genOre[i]) {
				Log.info("Enabling {} generation", oreMeta[i].config_name);
				gens.add(new GenerationInfo(oreMeta[i],
						new WorldGenMinable(BLOCK_ORE_META.getOre(oreMeta[i]), Config.oreSize[i], stone),
						Config.oreAbove[i], Config.oreBelow[i], Config.oreDepositCount[i]));
			} else {
				Log.info("Disabling {} generation", oreMeta[i].config_name);
			}
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.getDimension()) {
			default:
			case 0:
				generateSurface(world, random, chunkX * 16, chunkZ * 16);
				break;
			case -1:
				//generateNether(world, random, chunkX * 16, chunkZ * 16);
				break;
			case 1:
				//generateEnd(world, random, chunkX * 16, chunkZ * 16);
				break;
		}
	}

	/*private void generateEnd(World world, Random random, int i, int j) {
		// Not generating anything in the end at the moment
	}

	private void generateNether(World world, Random random, int i, int j) {
		// Not generating anything in the nether at the moment
	}*/

	private void generateSurface(World world, Random random, int i, int j) {
		BlockPos.MutableBlockPos firstBlock = new BlockPos.MutableBlockPos();
		Log.debug("Generating {} surface ores at x{} z{}", gens.size(), i, j);
		for (GenerationInfo gen : gens) {
			for (int k = 0; k < gen.maxDepositCount; k++) {
				int firstBlockXCoord = i + random.nextInt(16);
				int firstBlockYCoord = gen.generateAbove + random.nextInt(gen.generateBelow - gen.generateAbove);
				int firstBlockZCoord = j + random.nextInt(16);
				firstBlock.setPos(firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
				gen.gen.generate(world, random, firstBlock);
			}
		}
	}


}
