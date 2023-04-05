package moe.sandie.workerants.commands;

import moe.sandie.workerants.ItemManager;
import moe.sandie.workerants.WorkerAnts;
import moe.sandie.workerants.util.Lang;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class CommandManager implements TabExecutor {
    private final WorkerAnts plugin;

    public CommandManager(final WorkerAnts plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (plugin.isLoading()){
            sender.sendMessage(ChatColor.RED + Lang.get("errorLoading"));
            return true;
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.isOp()){
                player.sendMessage(ChatColor.RED + Lang.get((Player) sender, "noPermission"));
                return true;
            }
        }
        else {
            sender.sendMessage("Only players can use this command");
            return true;
        }
        if (command.getName().equalsIgnoreCase("worker")){
            ((Player) sender).getPlayer().getInventory().addItem(ItemManager.npcSpawnEgg);
        }
        if (command.getName().equalsIgnoreCase("resetnpcs")){
            CitizensAPI.getNPCRegistry().deregisterAll();
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {

        return null;
    }
}
