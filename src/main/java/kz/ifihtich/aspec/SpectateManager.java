package kz.ifihtich.aspec;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class SpectateManager {

    private final HashMap<Player, Player> session = new HashMap<>();
    private final HashMap<Player, Location> loc = new HashMap<>();

    public void specStart(Player spectator, Player target){

        if (session.containsKey(spectator)) {
            spectator.teleport(target.getLocation());
            session.put(spectator, target);
            return;
        }
        for (String cmd : ASpec.getInstance().getConfig().getStringList("settings.atTheStart")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", spectator.getName()));
        }
        loc.put(spectator, spectator.getLocation());
        spectator.setGameMode(GameMode.SPECTATOR);
        spectator.teleport(target.getLocation());
        session.put(spectator, target);
    }

    public void specStop(Player spectator){
        if (!session.containsKey(spectator)) return;

        spectator.setGameMode(GameMode.SURVIVAL);
        if (loc.containsKey(spectator)){
            spectator.teleport(loc.get(spectator));
        }
        for (String cmd : ASpec.getInstance().getConfig().getStringList("settings.uponCompletion")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", spectator.getName()));
        }
        loc.remove(spectator);
        session.remove(spectator);
    }

    public boolean inSpec(Player spectator){
        return session.containsKey(spectator);
    }

    public Player getTarget(Player spectator){
        return session.get(spectator);
    }

    public Set<Player> getSpectators(){
        return session.keySet();
    }
}
