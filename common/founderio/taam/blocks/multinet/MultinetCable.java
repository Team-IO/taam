package founderio.taam.blocks.multinet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.lighting.LazyLightMatrix;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.IconTransformation;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;
import founderio.taam.TaamMain;
import founderio.taam.multinet.IMultinetAttachment;
import founderio.taam.multinet.Multinet;

public class MultinetCable extends TMultiPart implements IMultinetAttachment {

	/**
	 * 0-5 == Block Sides, 6 == Cable Rack
	 */
	private int face;
	/**
	 * Layer index, 0-layerCount
	 */
	private int layer = -1;
	
	private String part;
	
	public Multinet network;

	private static CCModel ccm = CCModel.newModel(7, 48);

	public boolean available = false;
	
	@Override
	public String toString() {
		return "Multinet Cable [" + part + "] + network: " + network;
	}
	
	@Override
	public String getType() {
		return Taam.MULTIPART_MULTINET_CABLE + "." + part;
	}

	public int getLayer() {
		return layer;
	}
	
	public int getFace() {
		return face;
	}
	
	public void init(BlockCoord blockCoords, int face, Vector3 hit, String part) {
		
		this.part = part;
		
		ForgeDirection dir = ForgeDirection.getOrientation(face).getOpposite();
		this.face = dir.ordinal();
		
		this.layer = Multinet.getHitLayer(dir, hit);
	}

	@Override
	public boolean occlusionTest(TMultiPart npart) {
		
		if(npart instanceof MultinetCable) {
			return ((MultinetCable) npart).layer != layer || ((MultinetCable) npart).face != face;
		} else {
			Iterable<Cuboid6> otherBoxes = npart.getCollisionBoxes();
			
			ForgeDirection fd = ForgeDirection.getOrientation(face);
			
			Cuboid6 collisionCube = new Cuboid6(
					fd.offsetX - fd.offsetX * Multinet.cableWidth, fd.offsetY - fd.offsetY * Multinet.cableWidth, fd.offsetZ - fd.offsetZ * Multinet.cableWidth,
					fd.offsetX, fd.offsetY, fd.offsetZ);
			
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
	public void renderStatic(Vector3 pos, LazyLightMatrix olm, int pass) {
		MultinetCable.render(pos, olm, pass, face, layer);
	}
	
	public static void render(Vector3 pos, LazyLightMatrix olm, int pass, int face, int layer) {
		ForgeDirection dir = ForgeDirection.getOrientation(face);

		float layerOffset = (float)layer/Multinet.layerCount;
		float ox1 = 0;
		float oy1 = 0;
		float oz1 = 0;
		float ox2 = 0;
		float oy2 = 0;
		float oz2 = 0;
		
		IconTransformation ictrans = new IconTransformation(Block.blockRedstone.getBlockTextureFromSide(0));
		
		TextureUtils.bindAtlas(0);
		
		switch(dir) {
		case DOWN:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = layerOffset + Multinet.cableWidth;
			oy2 = Multinet.cableWidth;
			oz2 = 1;
			ccm.generateBlock(0, ox1, oy1, oz1,
								 ox2, oy2, oz2);
			
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = Multinet.cableWidth;
			oz2 = layerOffset + Multinet.cableWidth;

			ccm.generateBlock(24, ox1, oy1, oz1,
					 ox2, oy2, oz2).render(new Translation(pos), ictrans);
			break;
		case UP:
			ox1 = layerOffset;
			oy1 = 1 - Multinet.cableWidth;
			oz1 = 0;
			ox2 = layerOffset + Multinet.cableWidth;
			oy2 = 1;
			oz2 = 1;
			ccm.generateBlock(0, ox1, oy1, oz1,
					 ox2, oy2, oz2);
			ox1 = 0;
			oy1 = 1f - Multinet.cableWidth;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + Multinet.cableWidth;

			ccm.generateBlock(24, ox1, oy1, oz1,
					 ox2, oy2, oz2).render(new Translation(pos), ictrans);
			break;
		case NORTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = layerOffset + Multinet.cableWidth;
			oy2 = 1;
			oz2 = Multinet.cableWidth;
			ccm.generateBlock(0, ox1, oy1, oz1,
					 ox2, oy2, oz2);
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + Multinet.cableWidth;
			oz2 = Multinet.cableWidth;

			ccm.generateBlock(24, ox1, oy1, oz1,
					 ox2, oy2, oz2).render(new Translation(pos), ictrans);
			break;
		case SOUTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 1 - Multinet.cableWidth;
			ox2 = layerOffset + Multinet.cableWidth;
			oy2 = 1;
			oz2 = 1;
			ccm.generateBlock(0, ox1, oy1, oz1,
					 ox2, oy2, oz2);
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 1 - Multinet.cableWidth;
			ox2 = 1;
			oy2 = layerOffset + Multinet.cableWidth;
			oz2 = 1;

			ccm.generateBlock(24, ox1, oy1, oz1,
					 ox2, oy2, oz2).render(new Translation(pos), ictrans);
			break;
		case WEST:
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = Multinet.cableWidth;
			oy2 = 1;
			oz2 = layerOffset + Multinet.cableWidth;
			ccm.generateBlock(0, ox1, oy1, oz1,
					 ox2, oy2, oz2);
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = Multinet.cableWidth;
			oy2 = layerOffset + Multinet.cableWidth;
			oz2 = 1;

			ccm.generateBlock(24, ox1, oy1, oz1,
					 ox2, oy2, oz2).render(new Translation(pos), ictrans);
			break;
		case EAST:
			ox1 = 1 - Multinet.cableWidth;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + Multinet.cableWidth;
			ccm.generateBlock(0, ox1, oy1, oz1,
					 ox2, oy2, oz2);
			ox1 = 1 - Multinet.cableWidth;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + Multinet.cableWidth;
			oz2 = 1;

			ccm.generateBlock(24, ox1, oy1, oz1,
					 ox2, oy2, oz2).render(new Translation(pos), ictrans);
			break;
		default:
			break;
		}
	}
	
	@Override
	public Iterable<Cuboid6> getCollisionBoxes() {
		List<Cuboid6> boxes = new ArrayList<Cuboid6>();
		ForgeDirection dir = ForgeDirection.getOrientation(face);

		float layerOffset = (float)layer/Multinet.layerCount;
		float ox1 = 0;
		float oy1 = 0;
		float oz1 = 0;
		float ox2 = 0;
		float oy2 = 0;
		float oz2 = 0;
		
		
		switch(dir) {
		case DOWN:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = layerOffset + Multinet.cableWidth;
			oy2 = Multinet.cableWidth;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
								  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = Multinet.cableWidth;
			oz2 = layerOffset + Multinet.cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case UP:
			ox1 = layerOffset;
			oy1 = 1 - Multinet.cableWidth;
			oz1 = 0;
			ox2 = layerOffset + Multinet.cableWidth;
			oy2 = 1;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = 1f - Multinet.cableWidth;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + Multinet.cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case NORTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = layerOffset + Multinet.cableWidth;
			oy2 = 1;
			oz2 = Multinet.cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + Multinet.cableWidth;
			oz2 = Multinet.cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case SOUTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 1 - Multinet.cableWidth;
			ox2 = layerOffset + Multinet.cableWidth;
			oy2 = 1;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 1 - Multinet.cableWidth;
			ox2 = 1;
			oy2 = layerOffset + Multinet.cableWidth;
			oz2 = 1;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case WEST:
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = Multinet.cableWidth;
			oy2 = 1;
			oz2 = layerOffset + Multinet.cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = Multinet.cableWidth;
			oy2 = layerOffset + Multinet.cableWidth;
			oz2 = 1;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case EAST:
			ox1 = 1 - Multinet.cableWidth;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + Multinet.cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 1 - Multinet.cableWidth;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + Multinet.cableWidth;
			oz2 = 1;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		default:
			break;
		}
		return boxes;
	}
	
	@Override
	public Iterable<ItemStack> getDrops() {
		List<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(TaamMain.itemMultinetCable, 1, ItemMultinetCable.cables.indexOf(part)));
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
	public void onWorldJoin() {
		if(world().isRemote) {
			return;
		}
		available = true;
		Multinet.addCableToNetwork(this);
		sendDescUpdate();
	}
	
	@Override
	public void onWorldSeparate() {
		if(world().isRemote) {
			return;
		}
		available = false;
		Multinet.removeFromNetwork(this);
	}
	
	@Override
	public void save(NBTTagCompound tag) {
		super.save(tag);
		
		tag.setInteger("face", face);
		tag.setInteger("layer", layer);
		tag.setString("part", part);
	}
	
	@Override
	public void load(NBTTagCompound tag) {
		super.load(tag);
		
		face = tag.getInteger("face");
		layer = tag.getInteger("layer");
		part = tag.getString("part");
	}
	
	@Override
	public void writeDesc(MCDataOutput packet) {
		packet.writeInt(face);
		packet.writeInt(layer);
	}
	
	@Override
	public void readDesc(MCDataInput packet) {
		face = packet.readInt();
		layer = packet.readInt();
	}

	@Override
	public boolean canAttach(ForgeDirection face, ForgeDirection dir,
			int layer, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BlockCoord getCoordinates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public World getDimension() {
		// TODO Auto-generated method stub
		return null;
	}
}
