package pro.mikey.fabric.xray.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import pro.mikey.fabric.xray.records.BlockEntry;
import pro.mikey.fabric.xray.records.BlockGroup;
import pro.mikey.fabric.xray.storage.BlockStore;
import pro.mikey.fabric.xray.storage.Stores;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends AbstractScreen {
    private List<BlockGroup> blocks = new ArrayList<>();

    final private int guiWidth = 147;
    final private int guiHeight = 166;
    private int guiLeft = 0;
    private int guiTop = 0;

    private String selectedCategory = "";

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

        int y = this.guiTop + 20;
        for (BlockGroup group : this.blocks) {
            int x = this.width / 2 - 40;
            drawStringWithShadow(matrices, textRenderer, group.getName(), x, y, group.getColorInt());
            y += textRenderer.fontHeight + 2;

            if (group.getName() != this.selectedCategory) {
                continue;
            }

            matrices.push();
            matrices.translate(this.width / 2f - 60, y - 5, 0);
            matrices.scale(.8f, .8f, .8f);
            int xShift = 0;
            for (BlockEntry entry : group.getEntries()) {
                this.itemRenderer.renderInGui(new ItemStack(entry.getBlock().asItem()), x + xShift, y);
                if (!entry.isActive()) {
                    this.itemRenderer.renderInGui(new ItemStack(Items.BARRIER), x + xShift, y);
                }
                if (xShift < 80) {
                    xShift += 20;
                } else {
                    xShift = 0;
                    y += 20;
                }
            }
            matrices.pop();
            y += 24;
        }

        RenderSystem.popMatrix();
    }

    @Override
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (double_1 < guiLeft || double_1 > guiLeft + guiWidth
                || double_2 < guiTop || double_2 > guiTop + guiHeight) {
            return false;
        }

        double deltaX = double_1 - this.width / 2 + 40;
        double deltaY = double_2 - (guiTop + 20);

        for (BlockGroup group : this.blocks) {
            int groupHeight = textRenderer.fontHeight + 2;

            String groupName = group.getName();
            boolean groupSelected = this.selectedCategory == groupName;
            if (groupSelected) {
                groupHeight += 24 + 20 * (group.getEntries().size() / 5);
            }

            if (deltaY > groupHeight) {
                deltaY -= groupHeight;
                continue;
            }

            if (!groupSelected) {
                this.selectedCategory = groupName;
            } else if (deltaY < textRenderer.fontHeight + 2) {
                this.selectedCategory = "";
            } else {
                // clicked on some block
                int blockIdx = 5 * (int)((deltaY - textRenderer.fontHeight - 2) / 20) + (int)(deltaX / 20);

                if (blockIdx < group.getEntries().size()) {
                    BlockEntry entry = group.getEntries().get(blockIdx);
                    entry.setActive(!entry.isActive());
                    group.getEntries().set(blockIdx, entry);

                    BlockStore bs = Stores.BLOCKS.getInstance();
                    bs.setBlockEntries(this.blocks);
                    bs.write();
                }
            }

            break;
        }

        return true;
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
