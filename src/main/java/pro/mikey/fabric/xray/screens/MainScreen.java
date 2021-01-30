package pro.mikey.fabric.xray.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import pro.mikey.fabric.xray.records.BlockEntry;
import pro.mikey.fabric.xray.records.BlockGroup;
import pro.mikey.fabric.xray.storage.BlockStore;
import pro.mikey.fabric.xray.storage.Stores;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainScreen extends AbstractScreen {
    private List<BlockGroup> blocks = new ArrayList<>();

    final private int guiWidth = 147;
    final private int guiHeight = 166;
    private int guiLeft = 0;
    private int guiTop = 0;

    private HashSet<String> selected = new HashSet<String>();

    public MainScreen() {
        super(LiteralText.EMPTY);

        this.blocks.clear();
        this.blocks.addAll(Stores.BLOCKS.get());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0F, 0.0F, 100.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.guiLeft = (this.width - 147) / 2;
        this.guiTop = (this.height - 166) / 2;

        this.drawTexture(matrices, this.guiLeft, this.guiTop, 1, 1, 147, 166);

        drawCenteredString(matrices, textRenderer, "XRay-Fabric", this.width / 2, this.guiTop + 14, 0xffffff);
        drawCenteredString(matrices, textRenderer, "MichaelHillcox & iCollin", this.width / 2, this.guiTop + 28, 0xffffff);

        boolean foundMouse = false;
        int y = 10;
        for (BlockGroup group : this.blocks) {
            int x = 10;
            drawCenteredString(matrices, textRenderer, group.getName(), x + 80, y, group.getColorInt());
            y += textRenderer.fontHeight + 2;

            if (!this.selected.contains(group.getName())) {
                continue;
            }

            matrices.push();
            matrices.translate(0,  0, 1);
            matrices.scale(1.f, 1.f, 1.f);
            int xShift = 0;
            for (BlockEntry entry : group.getEntries()) {
                this.itemRenderer.renderInGui(new ItemStack(entry.getBlock().asItem()), x + xShift, y);
                if (!entry.isActive()){
                    this.itemRenderer.zOffset = 51.f;
                    this.itemRenderer.renderInGui(new ItemStack(Items.BARRIER), x + xShift, y);
                    this.itemRenderer.zOffset = 1.f;
                }
                if (mouseX > x + xShift && mouseX < x + xShift + 20
                        && mouseY > y && mouseY < y + 20) {
                    this.renderTooltip(matrices, Text.of(entry.getName()), mouseX, mouseY);
                }
                if (xShift < 140) {
                    xShift += 20;
                } else {
                    xShift = 0;
                    y += 20;
                }
            }
            matrices.pop();
            y += 20;
        }

        RenderSystem.popMatrix();
    }

    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        double deltaX = double_1 - 10;
        double deltaY = double_2 - 10;

        for (BlockGroup group : this.blocks) {
            int groupHeight = textRenderer.fontHeight + 2;

            String groupName = group.getName();
            boolean groupSelected = this.selected.contains(groupName);
            if (groupSelected) {
                groupHeight += 20 + 20 * (group.getEntries().size() / 8);
            }

            if (deltaY > groupHeight) {
                deltaY -= groupHeight;
                continue;
            }

            if (!groupSelected) {
                this.selected.add(groupName);
            } else if (deltaY < textRenderer.fontHeight + 2) {
                this.selected.remove(groupName);
            } else {
                // clicked on some block
                int blockIdx = 8 * (int)((deltaY - textRenderer.fontHeight - 2) / 20) + (int)(deltaX / 20);

                if (blockIdx < group.getEntries().size()) {
                    BlockEntry entry = group.getEntries().get(blockIdx);
                    entry.setActive(!entry.isActive());
                    group.getEntries().set(blockIdx, entry);

                    BlockStore bs = Stores.BLOCKS.getInstance();
                    bs.setBlockEntries(this.blocks);
                    bs.write();
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
