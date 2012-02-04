package com.pedrohlc.winclasspriority;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class WinClassPriority extends JavaPlugin {
	
	private boolean onWindows = false;
	private int process = 0;
	private WinClassPriorityExecutor executor = null;
	private Kernel32 kernel32 = null;
	private Logger log = Logger.getLogger("Minecraft");
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;

	@Override
	public void onDisable() {
		if(!onWindows) return;
		if(kernel32.SetPriorityClass(process, Kernel32.NORMAL_PRIORITY_CLASS))
			log.info("Process is back to normal priority!");
	}
	
	public void changePriority(CommandSender sender, String priority){
		String msg = "Process priority changed to ";
		boolean r = false;
		
		/*log.info("kernel32.SetPriorityClass("+
				new Integer(process).toString()+","+
				new Integer(Kernel32.HIGH_PRIORITY_CLASS).toString()+");"
				);*/
		if ((priority.compareTo("high") == 0)|(priority.compareTo("2") == 0)){
			msg += "HIGH";
			r=kernel32.SetPriorityClass(process, Kernel32.HIGH_PRIORITY_CLASS);
		}else if ((priority.compareTo("above") == 0)|(priority.compareTo("1") == 0)){
			msg += "ABOVE NORMAL";
			r=kernel32.SetPriorityClass(process, Kernel32.ABOVE_NORMAL_PRIORITY_CLASS);
		}else if ((priority.compareTo("normal") == 0)|(priority.compareTo("0") == 0)){
			msg += "NORMAL";
			r=kernel32.SetPriorityClass(process, Kernel32.NORMAL_PRIORITY_CLASS);
		}else if ((priority.compareTo("below") == 0)|(priority.compareTo("-1") == 0)){
			msg += "BELOW NORMAL";
			r=kernel32.SetPriorityClass(process, Kernel32.BELOW_NORMAL_PRIORITY_CLASS);
		}else if ((priority.compareTo("idle") == 0)|(priority.compareTo("-2") == 0)){
			msg += "IDLE";
			r=kernel32.SetPriorityClass(process, Kernel32.IDLE_PRIORITY_CLASS);
		}else{
			if ((sender != null) & (sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "Unknow priority...");
				log.info(((Player)sender).getName().toString() + " tried to change the process priority...");
			}
			else
				log.info("Unknow priority...");
			return;
		}
		
		if(!r){
			Integer lasterror = new Integer(kernel32.GetLastError());
			msg = ("Error on SetPriorityClass function, code " + lasterror.toString() + ".");
			if ((sender != null) & (sender instanceof Player))
				sender.sendMessage(ChatColor.RED + msg);
			log.info(msg);
			return;
		}
		
		if ((sender != null) & (sender instanceof Player)){
			msg += (" by " + ((Player)sender).getName().toString() + "!");
			sender.sendMessage(ChatColor.GREEN + msg);
		}else
			msg += "!";
		log.info(msg);
	}
	
	@Override
	public void onEnable() {
		onWindows = (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
		if(!onWindows) return;
		
		executor = new WinClassPriorityExecutor(this);
		getCommand("setprt").setExecutor(executor);
		
		kernel32 = Kernel32.INSTANCE;
		process = kernel32.GetCurrentProcess();
		
		customConfigFile = new File(getDataFolder(), "config.yml");
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
		InputStream defConfigStream = getResource("config.yml");
		if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
		try {
			customConfig.save(customConfigFile);
		} catch (IOException e) {}
		
		changePriority(null, customConfig.getString("defaultpriority").toLowerCase());
	}

}
