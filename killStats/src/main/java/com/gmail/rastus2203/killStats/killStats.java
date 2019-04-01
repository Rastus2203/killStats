package com.gmail.rastus2203.killStats;



import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class killStats extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		//initConfig();
		getLogger().info("onEnable has been invoked!");
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("onDisable has been invoked!");
	}
	/*
	public void initConfig() {
		getConfig().options().copyDefaults(true);
		String path = "users.test";
		getConfig().addDefault(path, 7);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}*/
	
	 @EventHandler
	 public void onKill(PlayerDeathEvent e)
	 {
		 String killedPlayerName = e.getEntity().getName();
		 String killedPlayerUUID = e.getEntity().getUniqueId().toString();
		 String killedByName;
		 String killedByUUID;
		 try {
			 killedByName = e.getEntity().getKiller().getName();
			 killedByUUID = e.getEntity().getKiller().getUniqueId().toString();
			 
			 getConfig().set("users." + killedByUUID + ".name", killedByName);
			 
			 int totalKills = getConfig().getInt("users." + killedByUUID + ".totalKills");
			 getConfig().set("users." + killedByUUID + ".totalKills", totalKills + 1);
			 
			 int playerKills = getConfig().getInt("users." + killedByUUID + ".playerKills." + killedPlayerUUID + ".kills");
			 getConfig().set("users." + killedByUUID + ".playerKills." + killedPlayerUUID + ".kills", playerKills + 1);
			 getConfig().set("users." + killedByUUID + ".playerKills." + killedPlayerUUID + ".name", killedPlayerName);
			 saveConfig();
		 }
		 catch (Exception a) {
			 killedByName = null;
			 killedByUUID = null;
		 }
		 
		 finally {
			 
		 }
		 
	 }
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("killstats")) { 
			String name;
			String uuid;
			try {
				name = args[0];
				uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
			}
			catch (Exception a) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("Please enter a player name");
					return true;
				}
				else {
					uuid = ((Player) sender).getUniqueId().toString();
					name = sender.getName();
				}
			}	finally {}
			
			try {
				getConfig().getConfigurationSection("users." + uuid + ".playerKills").getKeys(false);
			}
			
			catch (Exception a) {
				sender.sendMessage(name + " has no kills.");
				return true;
			}	finally {}
			
			
			int totalKills = getConfig().getInt("users." + uuid + ".totalKills");
			getLogger().info("Listing Kills");

			ArrayList<String> killedUUIDs = new ArrayList<String>();
			ArrayList<Integer> killedNumber = new ArrayList<Integer>();

			for (String i : getConfig().getConfigurationSection("users." + uuid + ".playerKills").getKeys(false)) {
				killedUUIDs.add(i);
				killedNumber.add(getConfig().getInt("users." + uuid + ".playerKills." + i + ".kills"));

			}
			sender.sendMessage(name + " has killed " + totalKills + " players.");
			for (Integer i = 0; i<killedUUIDs.size(); i++) {
				String killedName = getConfig().getString("users." + uuid + ".playerKills." + killedUUIDs.get(i) + ".name");
				sender.sendMessage(killedName + ": " + killedNumber.get(i).toString());
			}
	
			
			return true;
		} 
	       
		return false; 
	}
}
