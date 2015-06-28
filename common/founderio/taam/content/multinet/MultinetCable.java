package founderio.taam.content.multinet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.render.uv.IconTransformation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;
import founderio.taam.TaamMain;
import founderio.taam.multinet.IMultinetAttachment;
import founderio.taam.multinet.MultinetUtil;
import founderio.taam.util.WorldCoord;

public abstract class MultinetCable extends MultinetMultipart {


	public MultinetCable(String cableType) {
		super(cableType);
	}

	private static CCModel ccm = CCModel.newModel(7, 48);
	
	@Override
	public String toString() {
		return "Multinet Cable [" + cableType + "] + network: " + network;
	}
	
	@Override
	public String getType() {
		return Taam.MULTIPART_MULTINET_CABLE + "." + cableType;
	}
	
	@Override
	public boolean canAttach(WorldCoord coords, ForgeDirection face, ForgeDirection dir, int layer, String type) {
		//TODO: Actually check for occlusion!
		return face == this.face && layer == this.layer;
	}
	
	@Override
	public List<IMultinetAttachment> getIrregularAttachments() {
		return null;
	}

	@Override
	protected void saveProperties(NBTTagCompound tag) {
		
	}

	@Override
	protected void loadProperties(NBTTagCompound tag) {
		
	}

	@Override
	protected void saveProperties(MCDataOutput packet) {
		
	}

	@Override
	protected void loadProperties(MCDataInput packet) {
		
	}
	
	@Override
	public Iterable<ItemStack> getDrops() {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(TaamMain.itemMultinetCable, 1, ItemMultinetCable.cables.indexOf(cableType)));
		return drops;
	}
	
	@Override
	public Iterable<IndexedCuboid6> getSubParts() {
		List<Cuboid6> boxes = (List<Cuboid6>) getCollisionBoxes();
		List<IndexedCuboid6> retBoxes = new ArrayList<IndexedCuboid6>();
		for(Cuboid6 box : boxes) {
			retBoxes.add(new IndexedCuboid6(null, box));
		}
		return retBoxes;
	}
	
	@Override
	public boolean canStay(World world, int x, int y, int z, ForgeDirection side) {
		return MultinetUtil.canCableStay(world, x, y, z, side);
	}

	@Override
	public boolean occlusionTest(TMultiPart npart) {
		
		if(npart instanceof MultinetCable) {
			return ((MultinetCable) npart).layer != layer || ((MultinetCable) npart).face != face;
		} else {
			Iterable<Cuboid6> otherBoxes = npart.getCollisionBoxes();
			
			Cuboid6 collisionCube = new Cuboid6(
					face.offsetX - face.offsetX * MultinetUtil.cableWidth, face.offsetY - face.offsetY * MultinetUtil.cableWidth, face.offsetZ - face.offsetZ * MultinetUtil.cableWidth,
					face.offsetX, face.offsetY, face.offsetZ);
			
			for(Cuboid6 box : otherBoxes) {
				if(Cuboid6.intersects(box, collisionCube)) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderStatic(Vector3 pos, int pass) {
		MultinetCable.render(world(), pos, pass, face, layer, false);
		return true;
	}
	
	public static void render(World world, Vector3 pos, int pass, ForgeDirection face, int layer, boolean preview) {

		float layerOffset = (float)layer/MultinetUtil.layerCount;
		float ox1 = 0;
		float oy1 = 0;
		float oz1 = 0;
		float ox2 = 0;
		float oy2 = 0;
		float oz2 = 0;
		
		IconTransformation ictrans = new IconTransformation(Blocks.redstone_block.getBlockTextureFromSide(0));
		
		TextureUtils.bindAtlas(0);
		CCRenderState.useNormals = false;
		CCRenderState.pullLightmap();
        CCRenderState.setBrightness(world, (int)Math.round(pos.x), (int)Math.round(pos.y), (int)Math.round(pos.z));
//		CCRenderState.changeTexture(Taam.MOD_ID + ":tech_block");
		if(preview) {
			CCRenderState.alphaOverride = 80;
		}
		
		switch(face) {
		case DOWN:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = MultinetUtil.cableWidth;
			oy2 = MultinetUtil.cableWidth;
			oz2 = 1;
			ccm.generateBox(0, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = MultinetUtil.cableWidth;
			oz2 = MultinetUtil.cableWidth;

			ccm.generateBox(24, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			break;
		case UP:
			ox1 = layerOffset;
			oy1 = 1 - MultinetUtil.cableWidth;
			oz1 = 0;
			ox2 = MultinetUtil.cableWidth;
			oy2 = MultinetUtil.cableWidth;
			oz2 = 1;
			ccm.generateBox(0, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			ox1 = 0;
			oy1 = 1f - MultinetUtil.cableWidth;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = MultinetUtil.cableWidth;
			oz2 = MultinetUtil.cableWidth;

			ccm.generateBox(24, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			break;
		case NORTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = MultinetUtil.cableWidth;
			oy2 = 1;
			oz2 = MultinetUtil.cableWidth;
			ccm.generateBox(0, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = MultinetUtil.cableWidth;
			oz2 = MultinetUtil.cableWidth;

			ccm.generateBox(24, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			break;
		case SOUTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 1 - MultinetUtil.cableWidth;
			ox2 = MultinetUtil.cableWidth;
			oy2 = 1;
			oz2 = MultinetUtil.cableWidth;
			ccm.generateBox(0, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 1 - MultinetUtil.cableWidth;
			ox2 = 1;
			oy2 = MultinetUtil.cableWidth;
			oz2 = MultinetUtil.cableWidth;

			ccm.generateBox(24, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			break;
		case WEST:
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = MultinetUtil.cableWidth;
			oy2 = 1;
			oz2 = MultinetUtil.cableWidth;
			ccm.generateBox(0, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = MultinetUtil.cableWidth;
			oy2 = MultinetUtil.cableWidth;
			oz2 = 1;

			ccm.generateBox(24, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			break;
		case EAST:
			ox1 = 1 - MultinetUtil.cableWidth;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = MultinetUtil.cableWidth;
			oy2 = 1;
			oz2 = MultinetUtil.cableWidth;
			ccm.generateBox(0, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			ox1 = 1 - MultinetUtil.cableWidth;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = MultinetUtil.cableWidth;
			oy2 = MultinetUtil.cableWidth;
			oz2 = 1;

			ccm.generateBox(24, ox1, oy1, oz1,
					ox2, oy2, oz2, 0, 0, 16, 16, 1);
			break;
		default:
			break;
		}
//		ccm.computeLightCoords();
		ccm.render(new Translation(pos), ictrans);
		TextureUtils.bindAtlas(0);
	}
	
	@Override
	public Iterable<Cuboid6> getCollisionBoxes() {
		List<Cuboid6> boxes = new ArrayList<Cuboid6>();

		float layerOffset = (float)layer/MultinetUtil.layerCount;
		float ox1 = 0;
		float oy1 = 0;
		float oz1 = 0;
		float ox2 = 0;
		float oy2 = 0;
		float oz2 = 0;
		
		
		switch(face) {
		case DOWN:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = layerOffset + MultinetUtil.cableWidth;
			oy2 = MultinetUtil.cableWidth;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
								  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = MultinetUtil.cableWidth;
			oz2 = layerOffset + MultinetUtil.cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case UP:
			ox1 = layerOffset;
			oy1 = 1 - MultinetUtil.cableWidth;
			oz1 = 0;
			ox2 = layerOffset + MultinetUtil.cableWidth;
			oy2 = 1;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = 1f - MultinetUtil.cableWidth;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + MultinetUtil.cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case NORTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = layerOffset + MultinetUtil.cableWidth;
			oy2 = 1;
			oz2 = MultinetUtil.cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + MultinetUtil.cableWidth;
			oz2 = MultinetUtil.cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case SOUTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 1 - MultinetUtil.cableWidth;
			ox2 = layerOffset + MultinetUtil.cableWidth;
			oy2 = 1;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 1 - MultinetUtil.cableWidth;
			ox2 = 1;
			oy2 = layerOffset + MultinetUtil.cableWidth;
			oz2 = 1;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case WEST:
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = MultinetUtil.cableWidth;
			oy2 = 1;
			oz2 = layerOffset + MultinetUtil.cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = MultinetUtil.cableWidth;
			oy2 = layerOffset + MultinetUtil.cableWidth;
			oz2 = 1;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case EAST:
			ox1 = 1 - MultinetUtil.cableWidth;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + MultinetUtil.cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 1 - MultinetUtil.cableWidth;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + MultinetUtil.cableWidth;
			oz2 = 1;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		default:
			break;
		}
		return boxes;
	}
}
