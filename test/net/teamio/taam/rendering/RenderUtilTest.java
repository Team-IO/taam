package net.teamio.taam.rendering;

import net.minecraft.util.EnumFacing;
import net.teamio.taam.content.IRotatable;
import org.junit.Test;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

/**
 * Created by oliver on 2018-12-03.
 */
public class RenderUtilTest {

	@Test
	public void getRotationDegrees_West270() {
		float result = RenderUtil.getRotationDegrees(EnumFacing.WEST);
		assertEquals(270, result, 0.0000000001f);
	}

	@Test
	public void getRotationDegrees_North180() {
		float result = RenderUtil.getRotationDegrees(EnumFacing.NORTH);
		assertEquals(180, result, 0.0000000001f);
	}

	@Test
	public void getRotationDegrees_East90() {
		float result = RenderUtil.getRotationDegrees(EnumFacing.EAST);
		assertEquals(90, result, 0.0000000001f);
	}

	@Test
	public void getRotationDegrees_South0() {
		float result = RenderUtil.getRotationDegrees(EnumFacing.SOUTH);
		assertEquals(0, result, 0.0000000001f);
	}

	@Test
	public void getRotationDegrees_NullFacing() {
		float result = RenderUtil.getRotationDegrees((EnumFacing) null);
		assertEquals(0, result, 0.0000000001f);
	}

	@Test
	public void getRotationDegrees_NullRotatable() {
		float result = RenderUtil.getRotationDegrees((IRotatable) null);
		assertEquals(0, result, 0.0000000001f);
	}

	@Test
	public void getRotationDegrees_NullObject() {
		float result = RenderUtil.getRotationDegrees((Object) null);
		assertEquals(0, result, 0.0000000001f);
	}

	@Test
	public void getRotationDegrees_Object() {
		float result = RenderUtil.getRotationDegrees((Object) new IRotatable() {

			@Override
			public EnumFacing getFacingDirection() {
				return EnumFacing.EAST;
			}

			@Override
			public EnumFacing getNextFacingDirection() {
				return null;
			}

			@Override
			public void setFacingDirection(EnumFacing direction) {
			}
		});
		assertEquals(90, result, 0.0000000001f);
	}

	@Test
	public void getRotationDegrees_Rotatable() {
		float result = RenderUtil.getRotationDegrees(new IRotatable() {

			@Override
			public EnumFacing getFacingDirection() {
				return EnumFacing.NORTH;
			}

			@Override
			public EnumFacing getNextFacingDirection() {
				return null;
			}

			@Override
			public void setFacingDirection(EnumFacing direction) {
			}
		});
		assertEquals(180, result, 0.0000000001f);
	}
}