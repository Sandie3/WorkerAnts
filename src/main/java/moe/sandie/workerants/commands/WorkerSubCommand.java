package moe.sandie.workerants.commands;

import moe.sandie.workerants.util.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class WorkerSubCommand {

    public abstract String getName();
    public abstract String getNNameI18N();
    public abstract String getDescription();
    public abstract String getPermission();
    public abstract String getSyntax();
    public abstract int getMaxArguments();
    public abstract void execute(CommandSender commandSender, String[] args);
    public List<String> tabComplete(CommandSender commandSender, String[] args) {
        return Collections.emptyList();
    }

    public static boolean assertNonPlayer(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.YELLOW + Lang.get("consoleError"));
            return true;
        }
        return false;
    }

}
