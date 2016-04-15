package net.teamio.taam.content.piping.multiparts;

import java.util.List;

import mcmultipart.multipart.MultipartRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.teamio.taam.TaamMain;
import net.teamio.taam.content.piping.ItemPipeMachines;
import net.teamio.taam.content.piping.MachinePipe;
import net.teamio.taam.machines.IMachine;
import net.teamio.taam.machines.IMachineMetaInfo;
import net.teamio.taam.machines.MachineItemMultipart;
import net.teamio.taam.machines.MachineMultipart;

public class MultitpartHandlerPiping {
	public static MachineItemMultipart itemPartPipe;
	
	public static void registerMultiparts() {
		
		IMachineMetaInfo inf = new IMachineMetaInfo() {
			
			@Override
			public String unlocalizedName() {
				return "pipe";
			}
			
			@Override
			public int metaData() {
				return 0;
			}
			
			@Override
			public IMachine createMachine() {
				return new MachinePipe();
			}
			
			@Override
			public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
			}
		};
		
		itemPartPipe = new MachineItemMultipart(new IMachineMetaInfo[] { inf });
		itemPartPipe.setUnlocalizedName("multipart" + inf.unlocalizedName());
		itemPartPipe.setCreativeTab(TaamMain.creativeTab);
		
		MultipartRegistry.registerPart(MachineMultipart.class, "multipartPipe");
		
		GameRegistry.registerItem(itemPartPipe, "multipartPipe");
	}
}
