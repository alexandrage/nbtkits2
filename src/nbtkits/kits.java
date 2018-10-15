package nbtkits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import static nbtkits.Message.*;
import static nbtkits.Permissions.*;

public class kits extends JavaPlugin {
	private File folder = getDataFolder();
	private InventoryNBTSer inbt;

	@Override
	public void onEnable() {
		this.inbt = new InventoryNBTSer_v1_12_R1();
		getConfig().options().copyDefaults(true);
		getDataFolder().mkdirs();
		this.folder.mkdir();
		PluginManager pm = getServer().getPluginManager();
		Death d = new Death(this.folder, this.inbt);
		pm.registerEvents(d, this);
		getLogger().info("kits Enabled!");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("kit") && sender instanceof Player) {
			if (args.length == 3) {
				if (sender.hasPermission(admin) && args[0].matches("^[A-Za-z0-9]{1,10}$")
						&& args[1].matches("^[A-Za-z0-9]{1,10}$") && args[2].matches("^[0-9]{1,32}$")) {
					long time = Long.parseLong(args[2]);
					if (args[0].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("time")) {
						this.inbt.setKit((Player) sender, this.folder, args[1], time);
						return true;
					}
					if (args[0].equalsIgnoreCase("time")) {
						this.inbt.setTime((Player) sender, this.folder, args[1], time);
						return true;
					}
				}
			}
			if (args.length == 2) {
				if (sender.hasPermission(admin) && args[0].matches("^[A-Za-z0-9]{1,10}$")
						&& args[1].matches("^[A-Za-z0-9_]{1,10}$")) {
					if (args[0].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("time")) {
						this.inbt.setKit((Player) sender, this.folder, args[1], 86400);
						return true;
					}
					if (args[0].equalsIgnoreCase("del")) {
						this.inbt.delKit(this.folder, args[1], (Player) sender);
						return true;
					}
					Player pl = Bukkit.getPlayer(args[1]);
					if (pl == null) {
						sender.sendMessage(playerNo);
						((Player) sender).playSound(((Entity) sender).getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1,
								2);
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
					Player p = (Player) sender;
					p.playSound((p).getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
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
				Player p = (Player) sender;
				p.playSound((p).getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
				return true;
			}
		}
		if (cmd.getName().equalsIgnoreCase("kits")) {
			List<String> l = new ArrayList<String>();
			for (String s : this.inbt.getLogs(this.folder)) {
				if (sender.hasPermission(kit + s)) {
					l.add(s);
				}
			}
			sender.sendMessage(kits + l.toString().substring(1, l.toString().length() - 1).replace(",", ""));
			Player p = (Player) sender;
			p.playSound((p).getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
			return true;
		}
		return false;
	}
}