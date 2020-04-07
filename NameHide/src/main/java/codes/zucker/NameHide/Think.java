package codes.zucker.NameHide;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_15_R1.DataWatcher;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer;

public class Think implements Runnable {


    static ScoreboardManager manager = Bukkit.getScoreboardManager();
    static Scoreboard board = manager.getNewScoreboard();
    Team t = board.registerNewTeam("defaultTeam");

    @Override
    public void run() {
        t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            String name = p.getDisplayName();

            t.addEntry(p.getDisplayName());
            p.setScoreboard(board);
    
            List<Player> visiblePlayers = new ArrayList<Player>();

            List<Entity> entList = p.getNearbyEntities(50, 50, 50);
            for (Entity ent : entList) {
                if (!(ent instanceof Player)) continue;
                Player pl = (Player) ent;
                if (pl == p) continue;
                boolean bottom = Utils.PlayerCanSee(p, pl.getLocation().add(0, 0.5f, 0));
                boolean top = Utils.PlayerCanSee(p, pl.getLocation().add(0, 1.8f, 0));
                if (pl.isSneaking())
                    top = Utils.PlayerCanSee(p, pl.getLocation().add(0, 1.2f, 0));
                if (bottom && top)
                    visiblePlayers.add(pl);
            }

            List<UUID> allPlayerSees = new ArrayList<UUID>();
            if (PlayerStand.Stands.get(p.getUniqueId()) != null)
                for(PlayerStand stand : PlayerStand.Stands.get(p.getUniqueId()))
                    allPlayerSees.add(stand.target.getUniqueId());

            for(Player pl : visiblePlayers) {
                allPlayerSees.remove(pl.getUniqueId());
                EntityArmorStand id = PlayerStand.GetStandEntityForPlayer(p, pl);
                Vector pos = pl.getLocation().toVector();
                if (id == null) {
                    EntityArmorStand stand = new EntityArmorStand(((CraftWorld)p.getWorld()).getHandle().getMinecraftWorld(), pos.getX(), pos.getY() + 1.85f, pos.getZ());
                    stand.setInvisible(true);
                    stand.setCustomName(ChatSerializer.a("{\"text\": \"" + pl.getDisplayName() + "\"}"));
                    stand.setCustomNameVisible(true);
                    stand.setMarker(true);
    
                    PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(stand);
                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    
                    DataWatcher watcher = stand.getDataWatcher();
                    PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(stand.getId(), watcher, false);
                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(data);
    
                    new PlayerStand(p, pl, stand);
                    
                } else {
                    id.setLocation(pos.getX(), pos.getY() + 1.85f, pos.getZ(), 0, 0);
                    PacketPlayOutEntityTeleport posPacket = new PacketPlayOutEntityTeleport(id);
                    ((CraftPlayer)p).getHandle().playerConnection.sendPacket(posPacket);
                }
            }

            for(UUID uuid : allPlayerSees) {
                PlayerStand stand = PlayerStand.GetStandForPlayer(p, Bukkit.getPlayer(uuid));
                if (stand != null)
                    stand.Remove();
            }
        }
    }
}