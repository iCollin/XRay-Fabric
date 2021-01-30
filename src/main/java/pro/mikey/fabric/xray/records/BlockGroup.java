package pro.mikey.fabric.xray.records;

import java.util.ArrayList;
import java.util.List;

public class BlockGroup {
    private String name;
    private List<BlockEntry> entries;
    private String color;

    public BlockGroup(String name, List<BlockEntry> entries, String color) {
        this.name = name;
        this.entries = entries;
        this.color = color;
    }

    public BlockGroup(String groupName, String groupColor, String[] blockNames) {
        this.name = groupName;
        this.color = groupColor;
        this.entries = new ArrayList<>();
        for (String blockName : blockNames) {
            this.entries.add(new BlockEntry("", blockName, true));
        }
    }

    public List<BlockEntry> getEntries() {
        return entries;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public int getColorInt() {
        return Integer.valueOf(color.substring(1), 16);
    }
}
