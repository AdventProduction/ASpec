package kz.ifihtich.aspec;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectateCommand implements TabExecutor {

    private final ConfigManager config = new ConfigManager();

    private final SpectateManager spectateManager;

    public SpectateCommand(SpectateManager spectateManager) {
        this.spectateManager = spectateManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!commandSender.hasPermission("aspec.use")){
            commandSender.sendMessage(config.getString("messages.noPerm"));
            return true;
        }
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage(config.getString("messages.onlyPlayer"));
            return true;
        }

        if (strings.length < 1) return true;

        Player spectator = (Player) commandSender;
        String sub = strings[0].toLowerCase();

        if (sub.equalsIgnoreCase("reload")){
            if (!commandSender.hasPermission("aspec.reload")){
                commandSender.sendMessage(config.getString("messages.noPerm"));
                return true;
            }
            ASpec.getInstance().reloadConfig();
            commandSender.sendMessage(config.getString("messages.reload"));
            return true;
        }

        switch (sub) {
            case "start": {

                if (strings.length < 2) {
                    spectator.sendMessage(config.getString("messages.emptyPlayer"));
                    return true;
                }
                Player target = Bukkit.getPlayer(strings[1]);

                if (target == null || !target.isOnline()){
                    commandSender.sendMessage(config.getString("messages.noPlayer"));
                    return true;
                }

                if (spectator == target) {
                    spectator.sendMessage(config.getString("messages.forYourself"));
                    return true;
                }

                List<String> blacklist = ASpec.getInstance().getConfig().getStringList("settings.blacklist");

                if (blacklist.stream().anyMatch(name -> name.equalsIgnoreCase(target.getName()))){
                    spectator.sendMessage(config.getString("messages.banPlayer"));
                    return true;
                }

                spectateManager.specStart(spectator, target);
                spectator.sendMessage(config.getString("messages.specStart").replace("{target}", target.getName()));
                break;
            }

            case "stop": {
                if (!spectateManager.inSpec(spectator)){
                    commandSender.sendMessage(config.getString("messages.noSpec"));
                    return true;
                }

                spectateManager.specStop(spectator);
                break;
            }
            default: {
                for (String line : ASpec.getInstance().getConfig().getStringList("messages.usage")){
                    spectator.sendMessage(Utils.color(line));
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> completions = new ArrayList<>();

        if (strings.length == 1) {
            if (commandSender.hasPermission("aspec.use")) {
                completions.add("start");
                completions.add("stop");
            }
            if (commandSender.hasPermission("aspec.reload")) {
                completions.add("reload");
            }
            return completions;
        }

        if (strings.length == 2 && strings[0].equalsIgnoreCase("start") && commandSender.hasPermission("aspec.use")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                completions.add(player.getName());
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
