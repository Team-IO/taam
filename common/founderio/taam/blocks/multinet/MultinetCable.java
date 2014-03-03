package founderio.taam.blocks.multinet;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.lighting.LazyLightMatrix;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.render.CCModel;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;
import founderio.taam.TaamMain;
import founderio.taam.multinet.Multinet;

public class MultinetCable extends TMultiPart {

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

	private static final int layerCount = 6;
	private static final float cableWidth = 2f / 16f;
	private static CCModel ccm = CCModel.newModel(7, 24);


	
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
		
		this.layer = MultinetCable.getLayer(dir, hit);
	}

	@Override
	public boolean occlusionTest(TMultiPart npart) {
		
		if(npart instanceof MultinetCable) {
			return ((MultinetCable) npart).layer != layer || ((MultinetCable) npart).face != face;
		} else {
			Iterable<Cuboid6> otherBoxes = npart.getCollisionBoxes();
			
			ForgeDirection fd = ForgeDirection.getOrientation(face);
			
			Cuboid6 collisionCube = new Cuboid6(
					fd.offsetX - fd.offsetX * cableWidth, fd.offsetY - fd.offsetY * cableWidth, fd.offsetZ - fd.offsetZ * cableWidth,
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
	
	public static int getLayer(ForgeDirection dir, Vector3 hit) {
		int layer = -1;
		switch(dir) {
		case DOWN:
		case UP:
			if(hit.x > hit.z) {
				layer = (int)(hit.x * layerCount);
			} else {
				layer = (int)(hit.z * layerCount);
			}
			break;
		case NORTH:
		case SOUTH:
			if(hit.y > hit.x) {
				layer = (int)(hit.y * layerCount);
			} else {
				layer = (int)(hit.x * layerCount);
			}
			break;
		case WEST:
		case EAST:
			if(hit.y > hit.z) {
				layer = (int)(hit.y * layerCount);
			} else {
				layer = (int)(hit.z * layerCount);
			}
			break;
		default:
			break;
		}
		return layer;
	}
	
	public static void render(Vector3 pos, LazyLightMatrix olm, int pass, int face, int layer) {
		ForgeDirection dir = ForgeDirection.getOrientation(face);

		float layerOffset = (float)layer/(float)layerCount;
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
			ox2 = layerOffset + cableWidth;
			oy2 = cableWidth;
			oz2 = 1;
			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = cableWidth;
			oz2 = layerOffset + cableWidth;

			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			break;
		case UP:
			ox1 = layerOffset;
			oy1 = 1 - cableWidth;
			oz1 = 0;
			ox2 = layerOffset + cableWidth;
			oy2 = 1;
			oz2 = 1;
			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			ox1 = 0;
			oy1 = 1f - cableWidth;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + cableWidth;

			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			break;
		case NORTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = layerOffset + cableWidth;
			oy2 = 1;
			oz2 = cableWidth;
			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + cableWidth;
			oz2 = cableWidth;

			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			break;
		case SOUTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 1 - cableWidth;
			ox2 = layerOffset + cableWidth;
			oy2 = 1;
			oz2 = 1;
			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 1 - cableWidth;
			ox2 = 1;
			oy2 = layerOffset + cableWidth;
			oz2 = 1;

			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			break;
		case WEST:
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = cableWidth;
			oy2 = 1;
			oz2 = layerOffset + cableWidth;
			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = cableWidth;
			oy2 = layerOffset + cableWidth;
			oz2 = 1;

			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			break;
		case EAST:
			ox1 = 1 - cableWidth;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + cableWidth;
			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			ox1 = 1 - cableWidth;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + cableWidth;
			oz2 = 1;

			ccm.generateBlock(0, pos.x + ox1, pos.y + oy1, pos.z + oz1,
								 pos.x + ox2, pos.y + oy2, pos.z + oz2).render();
			break;
		default:
			break;
		}
	}
	
	@Override
	public Iterable<Cuboid6> getCollisionBoxes() {
		List<Cuboid6> boxes = new ArrayList<Cuboid6>();
		ForgeDirection dir = ForgeDirection.getOrientation(face);

		float layerOffset = (float)layer/(float)layerCount;
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
			ox2 = layerOffset + cableWidth;
			oy2 = cableWidth;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
								  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = cableWidth;
			oz2 = layerOffset + cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case UP:
			ox1 = layerOffset;
			oy1 = 1 - cableWidth;
			oz1 = 0;
			ox2 = layerOffset + cableWidth;
			oy2 = 1;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = 1f - cableWidth;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case NORTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 0;
			ox2 = layerOffset + cableWidth;
			oy2 = 1;
			oz2 = cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + cableWidth;
			oz2 = cableWidth;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case SOUTH:
			ox1 = layerOffset;
			oy1 = 0;
			oz1 = 1 - cableWidth;
			ox2 = layerOffset + cableWidth;
			oy2 = 1;
			oz2 = 1;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 1 - cableWidth;
			ox2 = 1;
			oy2 = layerOffset + cableWidth;
			oz2 = 1;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case WEST:
			ox1 = 0;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = cableWidth;
			oy2 = 1;
			oz2 = layerOffset + cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 0;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = cableWidth;
			oy2 = layerOffset + cableWidth;
			oz2 = 1;

			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			break;
		case EAST:
			ox1 = 1 - cableWidth;
			oy1 = 0;
			oz1 = layerOffset;
			ox2 = 1;
			oy2 = 1;
			oz2 = layerOffset + cableWidth;
			boxes.add(new Cuboid6(ox1, oy1, oz1,
					  ox2, oy2, oz2));
			ox1 = 1 - cableWidth;
			oy1 = layerOffset;
			oz1 = 0;
			ox2 = 1;
			oy2 = layerOffset + cableWidth;
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
	
//	@Override
//	@SideOnly(Side.CLIENT)
//	public boolean drawHighlight(MovingObjectPosition hit, EntityPlayer player,
//			float frame) {
//
//        GL11.glPushMatrix();
//        GL11.glBegin(GL11.GL_LINES);
//        GL11.glTranslated(0.5, 0.5, 0.5);
//        GL11.glScaled(1.002, 1.002, 1.002);
//        GL11.glTranslated(-0.5, -0.5, -0.5);
//		
//        MultinetCable.render(new Vector3(hit.blockX,  hit.blockY, hit.blockZ), null, 1, hit.sideHit, layer);
//
//        GL11.glEnd();
//        GL11.glPopMatrix();
//        return true;
//	}
	
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
		Multinet.addNetwork(this);
		sendDescUpdate();
	}
	
	@Override
	public void onWorldSeparate() {
		if(world().isRemote) {
			return;
		}
		network.removeCable(this);
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
}
