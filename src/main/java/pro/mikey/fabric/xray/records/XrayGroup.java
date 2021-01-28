package pro.mikey.fabric.xray.records;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashSet;

public class XrayGroup {
    private int color;
    private HashSet<Block> blocks;

    public XrayGroup(BlockGroup bg) {
        this.color = bg.getColorInt();
        this.blocks = new HashSet<>();
        for (BlockEntry be : bg.getEntries()) {
            if (be.isActive()) {
                this.blocks.add(be.getBlock());
            }
        }
    }

    public int getColor() {
        return color;
    }

    public HashSet<Block> getBlocks() {
        return blocks;
    }
}
