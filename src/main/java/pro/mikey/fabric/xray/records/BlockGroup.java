package pro.mikey.fabric.xray.records;

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
