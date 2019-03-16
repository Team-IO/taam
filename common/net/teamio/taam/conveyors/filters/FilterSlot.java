package net.teamio.taam.conveyors.filters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.teamio.taam.util.InventoryUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class FilterSlot extends GuiButton {

	public final ItemFilterCustomizable filter;
	private final GuiScreen screen;
	public final int index;
	public Consumer<ItemStack> onChanged;

	public static final int size = 16;

	public FilterSlot(ItemFilterCustomizable filter, GuiScreen screen, int index, int x, int y) {
		super(0, x, y, size, size, null);
		this.filter = filter;
		this.screen = screen;
		this.index = index;
	}

	public void drawTooltip(Minecraft mc, int mouseX, int mouseY) {
		if (!visible || !hovered) {
			return;
		}

		ItemStack itemStack = filter.getEntries()[index];
		boolean emptyHanded = InventoryUtils.isEmpty(mc.player.inventory.getItemStack());

		if (InventoryUtils.isEmpty(itemStack)) {
			if (!emptyHanded) {
				GuiUtils.drawHoveringText(Collections.singletonList("Click to set filter"), mouseX, mouseY, screen.width, screen.height, 300, mc.fontRendererObj);
			}
		} else {
			List<String> tooltip = getItemToolTip(mc, itemStack);
			if (emptyHanded) {
				tooltip.add(0, "Click to remove filter");
			} else {
				tooltip.add(0, "Click to replace filter");
			}
			tooltip.add(1, "");

			FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
			if (font == null) font = mc.fontRendererObj;
			GuiUtils.drawHoveringText(itemStack, tooltip, mouseX, mouseY, screen.width, screen.height, 300, font);
		}

		GlStateManager.disableLighting();
		GlStateManager.enableDepth();
	}

	@Override
	public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY) {
		if (!visible) return;

		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableRescaleNormal();

		this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width
				&& mouseY < yPosition + height;

		if (hovered) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			GlStateManager.colorMask(true, true, true, false);
			drawGradientRect(xPosition, yPosition, xPosition + 16, yPosition + 16, 0x80FFFFFF, 0x80FFFFFF);
			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
		}

		ItemStack itemStack = filter.getEntries()[index];

		if (!InventoryUtils.isEmpty(itemStack)) {
			RenderItem itemRender = mc.getRenderItem();

			GlStateManager.enableDepth();
			itemRender.zLevel = 100;
			itemRender.renderItemAndEffectIntoGUI(mc.player, itemStack, xPosition, yPosition);
			itemRender.renderItemOverlayIntoGUI(mc.fontRendererObj, itemStack, xPosition, yPosition, null);
		}

		GlStateManager.disableLighting();
	}

	public List<String> getItemToolTip(Minecraft mc, ItemStack itemStack) {
		List<String> list = itemStack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, itemStack.getRarity().rarityColor + list.get(i));
			} else {
				list.set(i, TextFormatting.GRAY + list.get(i));
			}
		}

		return list;
	}

	public void clicked(Minecraft mc) {
		ItemStack itemStack = mc.player.inventory.getItemStack();

		ItemStack filterEntry;
		if (InventoryUtils.isEmpty(itemStack)) {
			filterEntry = null;
		} else {
			filterEntry = InventoryUtils.copyStack(itemStack, 1);
		}
		filter.getEntries()[index] = filterEntry;

		if (onChanged != null) {
			onChanged.accept(filterEntry);
		}
	}
}
