package com.pedrohlc.winclasspriority;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WinClassPriorityExecutor implements CommandExecutor {
	
	private WinClassPriority plugin;
	
	public WinClassPriorityExecutor(WinClassPriority parent){
		plugin = parent;
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    	if(cmd.getName().equalsIgnoreCase("setprt")){
    		if(!sender.hasPermission("winclasspriority.setprt"))
    			return false;
    		if(args.length != 1){
    			sender.sendMessage(ChatColor.RED + "Wrong number of arguments!");
    			return false;
    		}
    		plugin.changePriority(sender, args[0].toLowerCase());
    		return true;
    	}
    	return false; 
    }

}
