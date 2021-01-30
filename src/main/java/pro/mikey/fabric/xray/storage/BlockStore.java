package pro.mikey.fabric.xray.storage;

import com.google.gson.reflect.TypeToken;
import net.minecraft.block.Blocks;
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
            entries.add(new BlockGroup("containers", "#ff0000", new String[]{"chest", "trapped_chest", "dropper", "dispenser"}));
            entries.add(new BlockGroup("portals", "#a832a0", new String[]{"end_gateway", "end_portal", "nether_portal", "ender_chest"}));
            entries.add(new BlockGroup("spawner", "#ffffff", new String[]{"spawner"}));
            entries.add(new BlockGroup("beehives", "#d4be17", new String[]{"bee_nest"}));
            entries.add(new BlockGroup("shulkers", "#1bbf20", new String[]{"shulker_box", "red_shulker_box", "orange_shulker_box", "yellow_shulker_box", "lime_shulker_box", "green_shulker_box", "light_blue_shulker_box", "blue_shulker_box", "magenta_shulker_box", "purple_shulker_box", "pink_shulker_box", "gray_shulker_box", "light_gray_shulker_box", "brown_shulker_box", "black_shulker_box", "cyan_shulker_box", "white_shulker_box" }));
            entries.add(new BlockGroup("diamond", "#34c9eb", new String[]{"diamond_ore", "diamond_block"}));
            entries.add(new BlockGroup("nether_ore", "#ffffff", new String[]{"ancient_debris", "quartz_ore"}));
            entries.add(new BlockGroup("iron_ore", "#bf8f0a", new String[]{"iron_ore"}));
            entries.add(new BlockGroup("lapis_ore", "#1f0cc9", new String[]{"lapis_ore"}));
        }

        setBlockEntries(entries);
    }

    public void setBlockEntries(List<BlockGroup> entries) {
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
