package pro.mikey.fabric.xray.cache;

import net.minecraft.util.math.BlockPos;

public class RenderBlock {
    BlockPos blockPos;
    int color;

    public RenderBlock(BlockPos pos, int col) {
        this.blockPos = pos;
        this.color = col;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public int getColor() {
        return color;
    }
}
