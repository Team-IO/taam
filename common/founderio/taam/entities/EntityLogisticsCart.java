package founderio.taam.entities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import founderio.taam.multinet.logistics.InBlockRoute;
import founderio.taam.multinet.logistics.LogisticsUtil;
import founderio.taam.multinet.logistics.Route;

public class EntityLogisticsCart extends Entity {

	public EntityLogisticsCart(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity other) {
		if (other == null) {
			return null;
		}
		return other.canBePushed() ? other.getBoundingBox() : null;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return null;
	}
	
	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}
	
	@Override
	public boolean canBePushed() {
		return !isOnRail;
	}
	
	@Override
	public double getMountedYOffset() {
		// TODO Auto-generated method stub
		return super.getMountedYOffset();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float damage) {
		// TODO Auto-generated method stub
		return super.attackEntityFrom(damageSource, damage);
	}
	
	public boolean isOnRail = false;
	
	private int currentRailX;
	private int currentRailY;
	private int currentRailZ;
	
	private InBlockRoute ibr;
	private float ibrProgress;
	private Route route;
	
	private float currentSpeed = 0.1f;
	
	@Override
	public void onEntityUpdate() {
		if(worldObj.isRemote) {
			
		} else {
			this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            
            boolean isActuallyOnRail = LogisticsUtil.isMagnetRail(worldObj, x, y, z);
            if(!isOnRail && isActuallyOnRail) {
            	currentRailX = x;
            	currentRailY = y;
            	currentRailZ = z;
            	//TODO: Snap into place
            	isOnRail = true;
            }
            
            if(isOnRail) {
            	//TODO: Check where we need to go, depending on route
            	
            	if(ibr == null) {
            		//TODO: Find route
            	} else {
            		ibrProgress += currentSpeed;
            		while(ibrProgress > ibr.totalLength) {
            			//TODO: get next ibr
            			ibrProgress -= ibr.totalLength;
            		}
            		// Find the point in the ibr we are at and do a linear interpolation between the closes points on the ibr.
            		float calcOffset = ibrProgress;
            		int coordinateCount = ibr.getCoordinateCount();
            		for(int i = 1; i < coordinateCount; i++) {
            			// Are we past that point yet?
            			if(calcOffset <= ibr.lengths[i-1]) {
            				// We are not past it -> interpolate.
            				
            				// Calculate the single deltas for the next and the last point
            				float dX = Math.abs(ibr.xyzCoordinates[i*3] - ibr.xyzCoordinates[(i-1)*3]);
            				float dY = Math.abs(ibr.xyzCoordinates[i*3+1] - ibr.xyzCoordinates[(i-1)*3+1]);
            				float dZ = Math.abs(ibr.xyzCoordinates[i*3+2] - ibr.xyzCoordinates[(i-1)*3+2]);
            				// Calculate the percentage of the whole distance that we have completed.
            				float percentage = (calcOffset / ibr.lengths[i-1]);
            				// Set our postion to the interpolated value
            				posX = ibr.xyzCoordinates[(i-1)*3] + dX * percentage;
            				posY = ibr.xyzCoordinates[(i-1)*3+1] + dY * percentage;
            				posZ = ibr.xyzCoordinates[(i-1)*3+2] + dZ * percentage;
            				// And don't check the next points anymore
            				break;
            			} else {
            				// We are past it -> move to next point.
            				calcOffset -= ibr.lengths[i-1];
            			}
            		}
            	}
            } else {
            	//TODO: Grounded/Air distinction.
            	this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
            }
            
		}
	}

}
