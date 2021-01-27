package pro.mikey.fabric.xray.storage;

import com.google.gson.reflect.TypeToken;
import pro.mikey.fabric.xray.records.BlockEntry;
import pro.mikey.fabric.xray.records.BlockGroup;
import pro.mikey.fabric.xray.records.XrayGroup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BlockStore extends Store<List<BlockGroup>> {
    private List<BlockGroup> blockEntries = new ArrayList<>();
    private List<XrayGroup> xrayGroups = new ArrayList<>();

    private static BlockStore instance;

    public static BlockStore getInstance() {
        if (instance == null) {
            instance = new BlockStore();
        }

        return instance;
    }

    private BlockStore() {
        super("blocks");

        List<BlockGroup> entries = this.read();
        if (entries == null || entries.isEmpty()) {
            entries = new ArrayList<>();
            List<BlockEntry> blocks = new ArrayList<>();
            blocks.add(new BlockEntry("", "chest", true));
            entries.add(new BlockGroup("default", blocks, "#FF0000"));
        }

        this.blockEntries = entries;
        this.xrayGroups = new ArrayList<>();

        for (BlockGroup entry : entries) {
            this.xrayGroups.add(new XrayGroup(entry));
        }
    }

    public List<XrayGroup> getXrayGroups() {
        return xrayGroups;
    }

    @Override
    public List<BlockGroup> get() {
        return this.blockEntries;
    }

    @Override
    Type getType() {
        return new TypeToken<List<BlockGroup>>() {}.getType();
    }
}
