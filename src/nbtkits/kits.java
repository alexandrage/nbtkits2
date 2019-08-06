package nbtkits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import static nbtkits.Message.*;
import static nbtkits.Permissions.*;

public class kits extends JavaPlugin implements TabCompleter {
	private File folder = getDataFolder();
	private InventoryNBTSer inbt;
	private String version = Bukkit.getServer().getClass().getName().split("\\.")[3];

	@Override
	public void onEnable() {
		try {
			Class<?> NBTSer = Class.forName("nbtkits.InventoryNBTSer_" + version);
			this.inbt = (InventoryNBTSer) NBTSer.newInstance();
			getConfig().options().copyDefaults(true);
			getDataFolder().mkdirs();
			this.folder.mkdir();
			PluginManager pm = getServer().getPluginManager();
			Death d = new Death(this.folder, this.inbt);
			pm.registerEvents(d, this);
			getLogger().info("kits Enabled!");
		} catch (Exception e) {
			e.printStackTrace();
			this.setEnabled(false);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			if (cmd.getName().contains("createkit")) {
				if (args.length == 2) {
					if (sender.hasPermission(admin) && args[0].matches("^[A-Za-z0-9]{1,10}$")
							&& args[1].matches("^[0-9]{1,32}$")) {
						long time = Long.parseLong(args[1]);
						this.inbt.setKit((Player) sender, this.folder, args[0], time);
						return true;
					}
				}
				if (args.length == 1) {
					if (sender.hasPermission(admin) && args[0].matches("^[A-Za-z0-9]{1,10}$")) {
						long time = 10l;
						this.inbt.setKit((Player) sender, this.folder, args[0], time);
						return true;
					}
				}
			}
			if (cmd.getName().contains("timekit")) {
				if (args.length == 2) {
					if (sender.hasPermission(admin) && args[0].matches("^[A-Za-z0-9]{1,10}$")
							&& args[1].matches("^[0-9]{1,32}$")) {
						long time = Long.parseLong(args[1]);
						this.inbt.setTime((Player) sender, this.folder, args[0], time);
						return true;
					}
				}
			}
			if (cmd.getName().contains("delkit")) {
				if (args.length == 1) {
					if (sender.hasPermission(admin) && args[0].matches("^[A-Za-z0-9]{1,10}$")) {
						this.inbt.delKit(this.folder, args[0], (Player) sender);
						return true;
					}
				}
			}
			if (cmd.getName().contains("kit")) {
				if (args.length == 2) {
					if (sender.hasPermission(admin) && args[0].matches("^[A-Za-z0-9]{1,10}$")
							&& args[1].matches("^[A-Za-z0-9_]{1,10}$")) {
						Player pl = Bukkit.getPlayer(args[1]);
						if (pl == null) {
							sender.sendMessage(playerNo);
							this.inbt.explode((Player) sender);
							return true;
						}
						this.inbt.getKit(pl, this.folder, args[0], true);
						return true;
					}
				}
				if (args.length == 1) {
					if (sender.hasPermission(kit + args[0]) && args[0].matches("^[A-Za-z0-9]{1,10}$")) {
						this.inbt.getKit((Player) sender, this.folder, args[0], sender.hasPermission(bypass));
						return true;
					} else {
						sender.sendMessage(permNo);
						this.inbt.explode((Player) sender);
						return true;
					}
				}
				if (args.length == 0) {
					List<String> l = new ArrayList<String>();
					for (String s : this.inbt.getLogs(this.folder)) {
						if (sender.hasPermission(kit + s)) {
							l.add(s);
						}
					}
					sender.sendMessage(kits + l.toString().substring(1, l.toString().length() - 1).replace(",", ""));
					this.inbt.openchest((Player) sender);
					return true;
				}
			}
		}
		return false;
	}

	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().contains("createkit")) {
			return null;
		}
		List<String> cmds = new ArrayList<String>();
		for (String s : this.inbt.getLogs(this.folder)) {
			if (sender.hasPermission(kit + s)) {
				cmds.add(s);
			}
		}
		if (args.length > 1) {
			return null;
		}
		return cmds;
	}
}