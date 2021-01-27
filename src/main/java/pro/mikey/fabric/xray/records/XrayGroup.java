package pro.mikey.fabric.xray.records;

import net.minecraft.block.Block;

import java.util.HashSet;

public class XrayGroup {
    private int color;
    private HashSet<Block> blocks;

    public XrayGroup(BlockGroup bg) {
        String color = bg.getColor();
        this.color = Integer.valueOf(color.substring(1), 16);

        this.blocks = new HashSet<>();
        for (BlockEntry be : bg.getEntries()) {
            this.blocks.add(be.getBlock());
        }
    }

    public int getColor() {
        return color;
    }

    public HashSet<Block> getBlocks() {
        return blocks;
    }
}
