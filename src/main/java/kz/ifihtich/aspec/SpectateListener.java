package kz.ifihtich.aspec;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.List;

public class SpectateListener implements Listener {

    private final SpectateManager spectateManager;

    private final ConfigManager config = new ConfigManager();

    public SpectateListener(SpectateManager spectateManager) {
        this.spectateManager = spectateManager;
    }

    @EventHandler
    public void onSpec(PlayerMoveEvent event){

        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        Player spectator = event.getPlayer();
        if (spectateManager.inSpec(spectator)){

            Player target = spectateManager.getTarget(spectator);

            if (target == null || !target.isOnline()) {
                spectateManager.specStop(spectator);
                return;
            }

            Location targetLoc = target.getLocation();

            if (spectator.getLocation().distance(targetLoc) > config.getInt("settings.distance")){
                Bukkit.getScheduler().runTask(ASpec.getInstance(), () ->
                        spectator.teleport(targetLoc)
                );
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        if (spectateManager.inSpec(player)) {
            spectateManager.specStop(player);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event){
        Player player = event.getPlayer();

        if (spectateManager.inSpec(player)){
            if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerStartSpectatingEntityEvent event) {

        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.SPECTATOR && spectateManager.inSpec(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void sendCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();

        if (spectateManager.inSpec(player)){
            String message = event.getMessage().toLowerCase();
            String command = message.split(" ")[0];

            List<String> blockedCommands = ASpec.getInstance().getConfig().getStringList("settings.blocked-commands");

            for (String cmd : blockedCommands){
                if (command.equalsIgnoreCase(cmd.toLowerCase())){
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
