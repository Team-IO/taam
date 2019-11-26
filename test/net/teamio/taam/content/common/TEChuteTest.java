package net.teamio.taam.content.common;

import com.builtbroken.mc.testing.junit.AbstractTest;
import com.builtbroken.mc.testing.junit.VoltzTestRunner;
import com.builtbroken.mc.testing.junit.server.FakeDedicatedServer;
import com.builtbroken.mc.testing.junit.world.FakeWorldServer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.teamio.taam.Config;
import net.teamio.taam.Taam;
import net.teamio.taam.TaamMain;
import net.teamio.taam.TestUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

import java.io.File;


/**
 * Created by oliver on 2019-11-11.
 */
@RunWith(VoltzTestRunner.class)
public class TEChuteTest extends AbstractTest {
	static FakeWorldServer world;
	static FakeDedicatedServer server;

	@Override
	public void setUpForEntireClass() {
		//Setup game
		Bootstrap.register();

		Config.init(null);

		// This will spew "Potentially Dangerous alternative prefix" messages.
		// They are fine, we do not have a mod container in this fake scenario.
		TaamMain.registerBlocksAndItems();
		TestUtil.registerCapabilities();

		//Create server for world for player
		server = new FakeDedicatedServer(new File(FakeWorldServer.baseFolder, "TEChuteTest"));
		server.init();

		world = FakeWorldServer.newWorld(server, "TEChuteTest");
		world.init(); //Must run after world creation in order to create a player without crashing
	}

	@Override
	public void tearDownForEntireClass() {
		server.dispose();
	}

	@Override
	public void tearDownForTest(String name) {
		//Clear chunk to make sure each test is valid
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					pos.setPos(x, y, z);
					world.setBlockToAir(pos);
				}
			}
		}

		// Delete all items & other entities from the world
		for (Entity ent : world.loadedEntityList) {
			world.removeEntity(ent);
		}

		// Remove destroyed entities
		world.updateEntities();
	}

	@Test
	public void testChuteTE() {
		//Set
		world.setBlockState(BlockPos.ORIGIN, TaamMain.blockMachines.getDefaultState()
				.withProperty(BlockMachines.VARIANT, Taam.BLOCK_MACHINES_META.chute));

		//Check block
		final IBlockState state = world.getBlockState(BlockPos.ORIGIN);
		final Block block = state.getBlock();
		Assertions.assertEquals(block, TaamMain.blockMachines, "Should be a " + Taam.MOD_ID + ":" + Taam.BLOCK_MACHINES);

		//check tile entity
		final TileEntity tile = world.getTileEntity(BlockPos.ORIGIN);
		Assertions.assertSame(tile.getClass(), TileEntityChute.class, "World.getTileEntity() should have returned a TileEntityChute. Actually got " + tile);

	}

	@Test
	public void testChestOutput() {
		/*
		arrange
		 */
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		// 0, 0, 0: Chest
		world.setBlockState(pos, Blocks.CHEST.getDefaultState());
		final TileEntityChest chest = (TileEntityChest) world.getTileEntity(pos);
		final IItemHandler itemHandlerChest = chest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		// 0, 1, 0: Chute
		pos.setY(1);
		world.setBlockState(pos, TaamMain.blockMachines.getDefaultState()
				.withProperty(BlockMachines.VARIANT, Taam.BLOCK_MACHINES_META.chute));
		final TileEntityChute chute = (TileEntityChute) world.getTileEntity(pos);
		final IItemHandler itemHandlerChute = chute.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		// Preconditions for the test
		Assertions.assertNotNull(itemHandlerChute, "Chute should have an item handler capability");
		Assertions.assertNotNull(itemHandlerChest, "Chest should have an item handler capability");
		// Make sure the target slot in the chest is empty, otherwise we test nonsense
		Assertions.assertSame(ItemStack.EMPTY, itemHandlerChest.getStackInSlot(0), "Chest slot 0 should be empty");

		/*
		act
		 */

		// Insert some items through chute
		final ItemStack toInsert = new ItemStack(Items.STICK, 5);
		final ItemStack remaining = itemHandlerChute.insertItem(0, toInsert, false);

		/*
		assert
		 */

		// Check that the chest contains the correct stack
		final ItemStack expected = new ItemStack(Items.STICK, 5);
		final ItemStack inChest = itemHandlerChest.getStackInSlot(0);
		Assertions.assertTrue(ItemStack.areItemStacksEqual(expected, inChest));

		// Check that everything was inserted
		Assertions.assertSame(ItemStack.EMPTY, remaining);
	}

	@Test
	public void testDropItems() {
		/*
		arrange
		 */
		// 0, 1, 0: Chute
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 1, 0);
		world.setBlockState(pos, TaamMain.blockMachines.getDefaultState()
				.withProperty(BlockMachines.VARIANT, Taam.BLOCK_MACHINES_META.chute));
		final TileEntityChute chute = (TileEntityChute) world.getTileEntity(pos);
		final IItemHandler itemHandlerChute = chute.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		// Preconditions for the test
		Assertions.assertNotNull(itemHandlerChute, "Chute should have an item handler capability");
		Assertions.assertTrue(world.loadedEntityList.isEmpty(), "Testing nonsense, world should not have entities present");

		/*
		act
		 */

		// Insert some items through chute
		final ItemStack toInsert = new ItemStack(Items.STICK, 5);
		final ItemStack remaining = itemHandlerChute.insertItem(0, toInsert, false);

		/*
		assert
		 */

		// Get dropped item
		Assertions.assertEquals(1, world.loadedEntityList.size(), "Expected exactly one entity in the world");
		final Entity entityInWorld = world.loadedEntityList.get(0);
		Assertions.assertSame(EntityItem.class, entityInWorld.getClass());

		// Check that the dropped item has the correct stack
		final ItemStack inWorld = ((EntityItem) entityInWorld).getItem();
		final ItemStack expected = new ItemStack(Items.STICK, 5);
		Assertions.assertTrue(ItemStack.areItemStacksEqual(expected, inWorld));

		// Check that everything was inserted
		Assertions.assertSame(ItemStack.EMPTY, remaining);
	}

	@Test
	public void testDropItemsBlocked() {
		/*
		arrange
		 */
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		// 0, 0, 0: Solid Block
		world.setBlockState(pos, Blocks.PLANKS.getDefaultState());

		// 0, 1, 0: Chute
		pos.setY(1);
		world.setBlockState(pos, TaamMain.blockMachines.getDefaultState()
				.withProperty(BlockMachines.VARIANT, Taam.BLOCK_MACHINES_META.chute));
		final TileEntityChute chute = (TileEntityChute) world.getTileEntity(pos);
		final IItemHandler itemHandlerChute = chute.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		// Preconditions for the test
		Assertions.assertNotNull(itemHandlerChute, "Chute should have an item handler capability");
		Assertions.assertTrue(world.loadedEntityList.isEmpty(), "Testing nonsense, world should not have entities present");

		/*
		act
		 */

		// Insert some items through chute
		final ItemStack toInsert = new ItemStack(Items.STICK, 5);
		final ItemStack remaining = itemHandlerChute.insertItem(0, toInsert, false);

		/*
		assert
		 */

		// Check that no item dropped
		Assertions.assertTrue(world.loadedEntityList.isEmpty(), "Expected no entities in world");

		// Check that the remaining items are unchanged
		final ItemStack expected = new ItemStack(Items.STICK, 5);
		Assertions.assertTrue(ItemStack.areItemStacksEqual(expected, remaining));
	}

	@Test
	public void testItemEmpty() {
		/*
		arrange
		 */
		// 0, 1, 0: Chute
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 1, 0);
		world.setBlockState(pos, TaamMain.blockMachines.getDefaultState()
				.withProperty(BlockMachines.VARIANT, Taam.BLOCK_MACHINES_META.chute));
		final TileEntityChute chute = (TileEntityChute) world.getTileEntity(pos);
		final IItemHandler itemHandlerChute = chute.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		// Preconditions for the test
		Assertions.assertNotNull(itemHandlerChute, "Chute should have an item handler capability");
		Assertions.assertTrue(world.loadedEntityList.isEmpty(), "Testing nonsense, world should not have entities present");

		/*
		act
		 */

		// Insert ItemStack.EMPTY
		final ItemStack remaining = itemHandlerChute.insertItem(0, ItemStack.EMPTY, false);

		/*
		assert
		 */

		// Check that no item dropped
		Assertions.assertTrue(world.loadedEntityList.isEmpty(), "Expected no entities in world");

		// Check that the remaining items are unchanged
		Assertions.assertSame(ItemStack.EMPTY, remaining);
	}

	@Test
	public void testItemNull() {
		/*
		arrange
		 */
		// 0, 1, 0: Chute
		final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 1, 0);
		world.setBlockState(pos, TaamMain.blockMachines.getDefaultState()
				.withProperty(BlockMachines.VARIANT, Taam.BLOCK_MACHINES_META.chute));
		final TileEntityChute chute = (TileEntityChute) world.getTileEntity(pos);
		final IItemHandler itemHandlerChute = chute.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);

		// Preconditions for the test
		Assertions.assertNotNull(itemHandlerChute, "Chute should have an item handler capability");
		Assertions.assertTrue(world.loadedEntityList.isEmpty(), "Testing nonsense, world should not have entities present");

		/*
		act
		 */

		// Insert ItemStack.EMPTY
		final ItemStack remaining = itemHandlerChute.insertItem(0, null, false);

		/*
		assert
		 */

		// Check that no item dropped
		Assertions.assertTrue(world.loadedEntityList.isEmpty(), "Expected no entities in world");

		// Check that the remaining items are unchanged
		Assertions.assertSame(ItemStack.EMPTY, remaining);
	}
}