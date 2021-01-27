package pro.mikey.fabric.xray;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import pro.mikey.fabric.xray.cache.RenderBlock;
import pro.mikey.fabric.xray.records.BlockEntry;
import pro.mikey.fabric.xray.records.BlockGroup;
import pro.mikey.fabric.xray.records.XrayGroup;
import pro.mikey.fabric.xray.storage.BlockStore;
import pro.mikey.fabric.xray.storage.Stores;

import java.util.*;

public class ScanTask implements Runnable {
    public ScanTask() {}

    @Override
    public void run() {
        XRay.renderQueue.clear();
        XRay.renderQueue.addAll(this.collectBlocks());
    }

    /**
     * This is an "exact" copy from the forge version of the mod but with the optimisations
     * that the rewrite (Fabric) version has brought like chunk location based cache, etc.
     *
     * This is only run if the cache is invalidated.
     * @implNote Using the {@link BlockPos#iterate(BlockPos, BlockPos)} may be a better system for the scanning.
     */
    private List<RenderBlock> collectBlocks() {
        BlockStore blockStore = BlockStore.getInstance();
        List<XrayGroup> blocks = blockStore.getXrayGroups();

        // If we're not looking for blocks, don't run.
        if ( blocks.isEmpty() ) {
            if( !XRay.renderQueue.isEmpty() )
                XRay.renderQueue.clear();
            return new ArrayList<>();
        }

        MinecraftClient instance = MinecraftClient.getInstance();

        final World world = instance.world;
        final PlayerEntity player = instance.player;

        // Just stop if we can't get the player or world.
        if( world == null || player == null )
            return new ArrayList<>();

        final List<RenderBlock> renderQueue = new ArrayList<>();

        int cX = player.chunkX;
        int cZ = player.chunkZ;

        int range = Stores.SETTINGS.get().getRange();
        for(int i = cX - range; i <= cX + range; i ++) {
            int chunkStartX = i << 4;
            for (int j = cZ - range; j <= cZ + range; j++) {
                int chunkStartZ = j << 4;

                int height = Arrays.stream(world.getChunk(i, j).getSectionArray())
                        .filter(Objects::nonNull)
                        .mapToInt(ChunkSection::getYOffset)
                        .max().orElse(0);

                for (int k = chunkStartX; k < chunkStartX + 16; k++) {
                    for (int l = chunkStartZ; l < chunkStartZ + 16; l++) {
                        for (int m = 0; m < height + (1 << 4); m++) {
                            BlockPos pos = new BlockPos(k, m, l);
                            BlockState state = world.getBlockState(pos);
                            if (state.isAir()) {
                                continue;
                            }
                            Block testBlock = state.getBlock();
                            for (XrayGroup group : blocks) {
                                if (group.getBlocks().contains(testBlock)) {
                                    renderQueue.add(new RenderBlock(pos, group.getColor()));
                                }
                            }
                        }
                    }
                }
            }
        }

        return renderQueue;
    }
}
