package pro.mikey.fabric.xray;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import pro.mikey.fabric.xray.cache.RenderBlock;
import pro.mikey.fabric.xray.records.XrayGroup;
import pro.mikey.fabric.xray.storage.BlockStore;
import pro.mikey.fabric.xray.storage.Stores;

import java.util.*;

public class ScanTask implements Runnable {
    public ScanTask() {}

    @Override
    public void run() {
        XRay.renderQueue.clear();

        List<XrayGroup> blocks = BlockStore.getInstance().getXrayGroups();
        if (blocks.isEmpty()) {
            return;
        }

        MinecraftClient instance = MinecraftClient.getInstance();
        final World world = instance.world;
        final PlayerEntity player = instance.player;

        if( world == null || player == null )
            return;

        int playerChunkX = player.chunkX;
        int playerChunkZ = player.chunkZ;
        int scanChunkRange = Stores.SETTINGS.get().getRange();

        for (int chunkX = playerChunkX - scanChunkRange; chunkX <= playerChunkX + scanChunkRange; chunkX++) {
            for (int chunkZ = playerChunkZ - scanChunkRange; chunkZ <= playerChunkZ + scanChunkRange; chunkZ++) {

                int maxScanHeight = 0;

                for (ChunkSection subChunk : world.getChunk(chunkX, chunkZ).getSectionArray()) {
                    if (subChunk != null && subChunk.getYOffset() >= maxScanHeight) {
                        maxScanHeight = subChunk.getYOffset() + 16;
                    }
                }

                for (int x = chunkX * 16; x < (chunkX + 1) * 16; x++) {
                    for (int z = chunkZ * 16; z < (chunkZ + 1) * 16; z++) {
                        for (int y = 0; y < maxScanHeight; y++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockState state = world.getBlockState(pos);
                            if (state.isAir()) {
                                continue;
                            }
                            Block testBlock = state.getBlock();
                            for (XrayGroup group : blocks) {
                                if (group.getBlocks().contains(testBlock)) {
                                    XRay.renderQueue.add(new RenderBlock(pos, group.getColor()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
