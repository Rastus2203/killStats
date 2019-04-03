package com.gmail.rastus2203.killStats;



import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
		//Runs when the plugin starts.
		getLogger().info("onEnable has been invoked!");
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable() {
		//Runs when plugin closes
		getLogger().info("onDisable has been invoked!");
	}

	
	 @EventHandler
	 public void onKill(PlayerDeathEvent e)
	 {
		 //Runs on player death
		 
		 //Creates variables containing the name and UUID of the player who was killed.
		 String killedPlayerName = e.getEntity().getName();
		 String killedPlayerUUID = e.getEntity().getUniqueId().toString();
		 String killedByName;
		 String killedByUUID;
		 //Tries to find the name of the player who got the kill.
		 //In a try block as if the player was killed by anything other than a player, it will return null, causing an error, exiting the try block.
		 try {
			 //Gets the killers username and UUID
			 killedByName = e.getEntity().getKiller().getName();
			 killedByUUID = e.getEntity().getKiller().getUniqueId().toString();
			 
			 //Adds the killers name to their entry in the config file.
			 //Re-added each time as names can change.
			 getConfig().set("users." + killedByUUID + ".name", killedByName);
			 
			 //Gets the killers total kills and adds 1
			 int totalKills = getConfig().getInt("users." + killedByUUID + ".totalKills");
			 getConfig().set("users." + killedByUUID + ".totalKills", totalKills + 1);
			 
			 //Gets the killers kills on the killed player and adds 1
			 int playerKills = getConfig().getInt("users." + killedByUUID + ".playerKills." + killedPlayerUUID + ".kills");
			 getConfig().set("users." + killedByUUID + ".playerKills." + killedPlayerUUID + ".kills", playerKills + 1);
			 
			 //Adding the killed players name
			 getConfig().set("users." + killedByUUID + ".playerKills." + killedPlayerUUID + ".name", killedPlayerName);
			 saveConfig();
		 }	finally {}
		 
	 }
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("killstats")) { 
			//Runs on command being typed
			String name;
			String uuid;
			try {
				//If the sender specified a username, get it here. Otherwise it will error and run the catch block
				name = args[0];
				uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
			}
			catch (Exception a) {
				
				if (!(sender instanceof Player)) {
					//First checks if the console sent the command, if so, tell the console they need a playername
					sender.sendMessage("Please enter a player name");
					return true;
				}
				else {
					//If a player sent it without specifying a name, assume the player is looking for their own stats
					uuid = ((Player) sender).getUniqueId().toString();
					name = sender.getName();
				}
			}	finally {}
			
			
			Set<String> killList;
			try {
				//Saves list of all player kills
				killList = getConfig().getConfigurationSection("users." + uuid + ".playerKills").getKeys(false);
			}
			
			catch (Exception a) {
				//Player has no kills.
				sender.sendMessage(ChatColor.YELLOW + name + " has no kills.");
				return true;
			}	finally {}
			
			
			//Gets total kills
			int totalKills = getConfig().getInt("users." + uuid + ".totalKills");

			//Initialises lists of UUIDs and kills of the player with that UUID
			ArrayList<String> killedUUIDs = new ArrayList<String>();
			ArrayList<Integer> killedNumber = new ArrayList<Integer>();

			//Iterates through all the kills made by the killer
			for (String i : killList) {
				//Adds the killed players UUIDs and number of times killed to the lists
				killedUUIDs.add(i);
				killedNumber.add(getConfig().getInt("users." + uuid + ".playerKills." + i + ".kills"));
			}
			
			//Tell the player how many kills the killer has made.
			sender.sendMessage(ChatColor.YELLOW + "Listing kill stats for " + name);
			sender.sendMessage(ChatColor.YELLOW + "Total Kills: " + totalKills);
			
			//Iterates through all the players killed, sends this data to the player
			for (Integer i = 0; i<killedUUIDs.size(); i++) {
				String killedName = getConfig().getString("users." + uuid + ".playerKills." + killedUUIDs.get(i) + ".name");
				sender.sendMessage(ChatColor.YELLOW + killedName + ": " + killedNumber.get(i).toString());
			}
	
			
			return true;
		} 
	       
		return false; 
	}
}
