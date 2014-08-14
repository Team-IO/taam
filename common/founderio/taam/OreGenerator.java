package founderio.taam;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		switch(world.provider.dimensionId){
		case -1:
		    generateNether(world, random, chunkX * 16, chunkZ * 16);
		    break;
		case 0:
		    generateSurface(world, random, chunkX * 16, chunkZ * 16);
		    break;
		case 1:
		    generateEnd(world, random, chunkX * 16, chunkZ * 16);
		    break;
		}
	}

	private void generateEnd(World world, Random random, int i, int j) {}

	private void generateNether(World world, Random random, int i, int j) {}

	private void generateSurface(World world, Random random, int i, int j) {
		Random rand = new Random();
		for (int k = 0; k < 10; k++){
			int firstBlockXCoord = i + rand.nextInt(16);
        	int firstBlockYCoord = rand.nextInt(64);
        	int firstBlockZCoord = j + rand.nextInt(16);
        	
        	if (Config.genCopper == true)
        	{
        		(new WorldGenMinable(TaamMain.blockTinOre, 14)).generate(world, rand, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
        	}
        	if (Config.genTin == true)
        	{
            	(new WorldGenMinable(TaamMain.blockCopperOre, 13)).generate(world, rand, firstBlockXCoord, firstBlockYCoord, firstBlockZCoord);
        	}
		}
	
	}

}