package founderio.taam.blocks.multinet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.lighting.LazyLightMatrix;
import codechicken.lib.render.CCModel;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.multipart.TMultiPart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;

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

	private static final int layerCount = 6;
	private static final float cableWidth = 2f / 16f;
	private static CCModel ccm = CCModel.newModel(7, 24);

	@Override
	public String getType() {
		return Taam.MULTIPART_MULTINET_CABLE + "." + part;
	}

	public void init(BlockCoord blockCoords, int face, Vector3 hit, String part) {
		
		this.part = part;
		
		ForgeDirection dir = ForgeDirection.getOrientation(face).getOpposite();
		this.face = dir.ordinal();
		
		this.layer = MultinetCable.getLayer(dir, hit);
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
	@SideOnly(Side.SERVER)
	public void onWorldJoin() {
		sendDescUpdate();
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
