package founderio.taam.items;

import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import founderio.taam.Taam;

public class ItemTinIngot extends Item {
		
		public ItemTinIngot(){
			super();
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		protected String getIconString(){
			return Taam.MOD_ID + ":tiningot";
		}
}
