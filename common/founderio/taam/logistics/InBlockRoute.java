package founderio.taam.logistics;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * In-Block Route descriptor using Sub-Block coordinates.
 * @author oliverkahrmann
 *
 */
public class InBlockRoute {
	public ForgeDirection enterFrom;
	public ForgeDirection leaveTo;
	public float[] xyzCoordinates;
	public float[] lengths;
	public float totalLength;
	
	private InBlockRoute() {
		// Only for cloning.
	}
	
	/**
	 * Create a new InBlockRoute. This will calculate the lengths. The
	 * coordinates array will be cloned.
	 * 
	 * @param enterFrom
	 * @param leaveTo
	 * @param xyzCoordinates
	 */
	public InBlockRoute(ForgeDirection enterFrom, ForgeDirection leaveTo,
			float[] xyzCoordinates) {
		// Coordinates are in one single array, so the array has to have a length of
		// a multiple of 3 to be complete. (x, y, z coordinates for each of the points.)
		if(xyzCoordinates.length % 3 != 0) {
			throw new IllegalArgumentException("Not full x,y,z coordinate set. Length not dividable by 3");
		}
		
		this.enterFrom = enterFrom;
		this.leaveTo = leaveTo;
		this.xyzCoordinates = xyzCoordinates.clone();
		
		// Calculate the vector lengths between the single points
		// (beginning form second point, if any, calculating deltas with the respective previous point)
		// There will be one length less than there are points, obviously.
		int l = xyzCoordinates.length / 3;
		lengths = new float[l-1];
		for(int i = 1; i < l; i++) {
			float dX = Math.abs(xyzCoordinates[i*3] - xyzCoordinates[(i-1)*3]);
			float dY = Math.abs(xyzCoordinates[i*3+1] - xyzCoordinates[(i-1)*3+1]);
			float dZ = Math.abs(xyzCoordinates[i*3+2] - xyzCoordinates[(i-1)*3+2]);
			lengths[i-1] = MathHelper.sqrt_float(dX*dX + dY*dY + dZ*dZ);
			totalLength += lengths[i-1];
		}
	}
	
	public int getCoordinateCount() {
		return xyzCoordinates.length / 3;
	}
	
	/**
	 * Will rotate this InBlockRoute to the right by 90 degrees around the y
	 * axis. (North -> East -> South -> West)
	 * 
	 * @return A new InBlockRoute containing new coordinates, BUT containing THE
	 *         SAME LENGTHS. Modifying one will modify the other!
	 */
	public InBlockRoute getRotated() {
		InBlockRoute rotated = new InBlockRoute();
		rotated.enterFrom = enterFrom.getRotation(ForgeDirection.UP);
		rotated.leaveTo = leaveTo.getRotation(ForgeDirection.UP);
		rotated.lengths = lengths;
		rotated.totalLength = totalLength;
		rotated.xyzCoordinates = xyzCoordinates.clone();
		
		int l = rotated.xyzCoordinates.length / 3;
		for(int i = 0; i < l; i++) {
			/*
			 * Use the coordinates, shifted to the block origin. As we are
			 * dealing with sub-block coordinates here, everything *should* be
			 * within 0 to 1 and we rotate around the center of the block, at
			 * 0.5.)
			 */
			
			//Fetch coords;
			Vec3 newCoord = Vec3.createVectorHelper(rotated.xyzCoordinates[i*3], rotated.xyzCoordinates[i*3+1], rotated.xyzCoordinates[i*3+2]);
			// Shift to origin
			newCoord = newCoord.addVector(-0.5, -0.5, -0.5);
			// Rotate 90 degrees
			newCoord.rotateAroundY((float) Math.toRadians(90));
			// Shift back to center of the block
			newCoord = newCoord.addVector(0.5, 0.5, 0.5);
			// Store back into array
			rotated.xyzCoordinates[i*3] = (float)newCoord.xCoord;
			rotated.xyzCoordinates[i*3+1] = (float)newCoord.yCoord;
			rotated.xyzCoordinates[i*3+2] = (float)newCoord.zCoord;
		}
		return rotated;
	}
}
