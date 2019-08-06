package nbtkits;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.entity.Player;

public interface InventoryNBTSer {
	public void setInv(Player p, File folder) throws IOException;

	public void getInv(Player p, File folder);

	public void getKit(Player p, File folder, String name, boolean b);

	public void setKit(Player p, File folder, String name, long time);

	public void setTime(Player p, File folder, String name, long time);

	public void delKit(File folder, String name, Player p);

	public List<String> getLogs(File folder);
	
	public void explode(Player p);
	
	public void openchest(Player p);
}