package nbtkits;

import java.io.File;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import static nbtkits.Permissions.*;

public class Death implements Listener {

	private File folder;
	private InventoryNBTSer inbt;

	public Death(File folder, InventoryNBTSer inbt) {
		this.folder = folder;
		this.inbt = inbt;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) throws IOException {
		Player p = e.getEntity();
		if (p.hasPermission(deathinv)) {
			e.setKeepInventory(true);
		}
		if (p.hasPermission(deathexp)) {
			e.setKeepLevel(true);
			e.setDroppedExp(0);
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) throws IOException {
		Player p = e.getPlayer();
		if (!p.hasPlayedBefore()) {
			if (e.getPlayer().hasPermission(starter)) {
				this.inbt.getKit(p, this.folder, "starter", true);
			}
		}
	}
}
