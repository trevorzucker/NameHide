package codes.zucker.NameHide;

import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Think(), 0, 1);
    }

    @Override
    public void onDisable() {
        for (Entry<UUID, List<PlayerStand>> entry : PlayerStand.Stands.entrySet()) {
            for(int i = 0; i < entry.getValue().size(); i++) {
                entry.getValue().get(i).Remove();
            }
        }
    }
}