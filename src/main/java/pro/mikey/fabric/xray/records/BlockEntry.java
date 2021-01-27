package pro.mikey.fabric.xray.records;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockEntry {
    String state;
    String name;
    boolean active;

    public BlockEntry(String state, String name, boolean active) {
        this.state = state;
        this.name = name;
        this.active = active;
    }

    public String getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public Block getBlock() {
        Block block = Registry.BLOCK.get(new Identifier(this.name));
        return block;
    }
}
