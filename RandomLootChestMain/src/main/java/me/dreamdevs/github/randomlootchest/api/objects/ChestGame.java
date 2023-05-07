package me.dreamdevs.github.randomlootchest.api.objects;

import lombok.Getter;
import lombok.Setter;
import me.dreamdevs.github.randomlootchest.RandomLootChestMain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ChestGame {

    private String id;
    private String title;
    private long time;
    private int maxItems;
    private int maxItemsInTheSameType;
    private RandomMoney money;
    private File chestFile;
    private String particleType;
    private int particleAmount;
    private boolean particleUse;
    private List<RandomItem> items = new ArrayList<>();
    public ChestGame(String id, boolean createFile) {
        this.id = id;
        this.chestFile = new File(RandomLootChestMain.getInstance().getDataFolder(), "chests/"+id+".yml");
        if(!chestFile.exists() && createFile) {
            try {
                chestFile.createNewFile();
            } catch (Exception e) {

            }
        }
    }

}