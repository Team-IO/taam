package net.teamio.taam;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.teamio.taam.content.common.BlockOre;

public class OreGenerator implements IWorldGenerator {

	public static class GenerationInfo {
		public WorldGenMinable gen;
		public int generateAbove;
		public int generateBelow;
		public int maxDepositCount;
		public GenerationInfo(WorldGenMinable gen, int generateAbove,
				int generateBelow, int maxDepositCount) {
			this.gen = gen;
			this.generateAbove = generateAbove;
			this.generateBelow = generateBelow;
			this.maxDepositCount = maxDepositCount;
		}
	}
	
	List<GenerationInfo> gens;
	
	public OreGenerator() {
		reloadGenerationInfo();
	}
	

	@SubscribeEvent
	public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		// Reload when the config changes to apply it BEFORE restart.
		if (event.modID.equalsIgnoreCase(Taam.MOD_ID))
		{
			reloadGenerationInfo();
		}
	}
	
	public void reloadGenerationInfo() {
		gens = new ArrayList<GenerationInfo>();
		BlockHelper stone = BlockHelper.forBlock(Blocks.stone);
		if(Config.genOre[0]) {
			gens.add(new GenerationInfo(new WorldGenMinable(getOre(Taam.BLOCK_ORE_META.copper), Config.oreSize[0], stone), Config.oreAbove[0], Config.oreBelow[0], Config.oreDepositCount[0]));
		}
		if(Config.genOre[1]) {
			gens.add(new GenerationInfo(new WorldGenMinable(getOre(Taam.BLOCK_ORE_META.tin), Config.oreSize[1], stone), Config.oreAbove[1], Config.oreBelow[1], Config.oreDepositCount[1]));
		}
		if(Config.genOre[2]) {
			gens.add(new GenerationInfo(new WorldGenMinable(getOre(Taam.BLOCK_ORE_META.aluminum), Config.oreSize[2], stone), Config.oreAbove[2], Config.oreBelow[2], Config.oreDepositCount[2]));
		}
		if(Config.genOre[3]) {
			gens.add(new GenerationInfo(new WorldGenMinable(getOre(Taam.BLOCK_ORE_META.bauxite), Config.oreSize[3], stone), Config.oreAbove[3], Config.oreBelow[3], Config.oreDepositCount[3]));
		}
		if(Config.genOre[4]) {
			gens.add(new GenerationInfo(new WorldGenMinable(getOre(Taam.BLOCK_ORE_META.kaolinite), Config.oreSize[4], stone), Config.oreAbove[4], Config.oreBelow[4], Config.oreDepositCount[4]));
		}
	}
	
	private IBlockState getOre(Taam.BLOCK_ORE_META ore) {
		return TaamMain.blockOre.getDefaultState().withProperty(BlockOre.VARIANT, Taam.BLOCK_ORE_META.copper);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch (world.provider.getDimensionId()) {
		case -1:
			generateNether(world, random, chunkX * 16, chunkZ * 16);
			break;
		default:
		case 0:
			generateSurface(world, random, chunkX * 16, chunkZ * 16);
			break;
		case 1:
			generateEnd(world, random, chunkX * 16, chunkZ * 16);
			break;
		}
	}

	private void generateEnd(World world, Random random, int i, int j) {
	}

	private void generateNether(World world, Random random, int i, int j) {
	}

	private void generateSurface(World world, Random random, int i, int j) {
		for(GenerationInfo gen : gens) {
			for (int k = 0; k < gen.maxDepositCount; k++) {
				int firstBlockXCoord = i + random.nextInt(16);
				int firstBlockYCoord = gen.generateAbove + random.nextInt(gen.generateBelow - gen.generateAbove);
				int firstBlockZCoord = j + random.nextInt(16);
				gen.gen.generate(world, random, new BlockPos(firstBlockXCoord, firstBlockYCoord, firstBlockZCoord));
			}
		}
	}

}