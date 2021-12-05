package nbtkits;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import static nbtkits.Message.*;

public class InventoryNBTSer_v1_18_R1 implements InventoryNBTSer {

	@Override
	public void setInv(Player p, File folder) throws IOException {
		File folderinv = new File(folder + "/saveinv");
		folderinv.mkdirs();
		NBTTagList Arm = toNBTTagList(p.getInventory().getArmorContents());
		NBTTagList Inv = toNBTTagList(p.getInventory().getContents());
		NBTTagCompound NBT = new NBTTagCompound();
		NBT.a("Arm", Arm);
		NBT.a("Inv", Inv);
		try {
			FileOutputStream stream = new FileOutputStream(folderinv + "/" + p.getName());
			NBTCompressedStreamTools.a(NBT, stream);
			stream.close();
			p.getInventory().clear();
			ItemStack[] in = p.getInventory().getArmorContents();
			for (int i = 0; i < in.length; i++) {
				in[i] = new ItemStack(Material.AIR, 0);
			}
			p.getInventory().setArmorContents(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getInv(Player p, File folder) {
		File folderinv = new File(folder + "/saveinv");
		folderinv.mkdirs();
		if (!new File(folderinv + "/" + p.getName()).exists()) {
			return;
		}
		NBTTagCompound NBT = fromNBTTagCompound(folderinv + "/" + p.getName());
		p.getInventory().setArmorContents(fromInventory(NBT.c("Arm", 10)).getContents());
		p.getInventory().setContents(fromInventory(NBT.c("Inv", 10)).getContents());
		new File(folderinv + "/" + p.getName()).delete();
	}

	@Override
	public void getKit(Player p, File folder, String name, boolean b) {
		if (!new File(folder + "/kits/" + name.toLowerCase() + ".kit").exists()) {
			p.sendMessage(kitsNo);
			p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
			return;
		}
		NBTTagCompound NBT = fromNBTTagCompound(folder + "/kits/" + name.toLowerCase() + ".kit");
		long time = NBT.i("time");
		NBTTagList Inv = NBT.c("kit", 10);

		if (!b) {
			if (new File(folder + "/players/" + name.toLowerCase() + "-" + p.getName()).exists()) {
				NBTTagCompound temp = fromNBTTagCompound(folder + "/players/" + name.toLowerCase() + "-" + p.getName());
				long tpl = temp.i("time");
				long calc = (System.currentTimeMillis() - tpl) / 1000;
				Time t = new Time(time - calc);
				if (calc < time) {
					p.sendMessage(kitsTmp + t.getFormat());
					p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 0);
					return;
				}
			}
		}

		ItemStack[] st = fromInventory(Inv).getContents();
		for (ItemStack s : st) {
			if (s != null) {
				HashMap<Integer, ItemStack> over = p.getInventory().addItem(s);
				for (Entry<Integer, ItemStack> entry : over.entrySet()) {
					p.getWorld().dropItemNaturally(p.getLocation(), entry.getValue());
				}
			}
		}
		p.sendMessage(kitsGive + name.toLowerCase());
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 0);
		new File(folder + "/players/").mkdirs();
		NBTTagCompound temp = new NBTTagCompound();
		temp.a("time", System.currentTimeMillis());
		try {
			FileOutputStream stream = new FileOutputStream(
					folder + "/players/" + name.toLowerCase() + "-" + p.getName());
			NBTCompressedStreamTools.a(temp, stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setKit(Player p, File folder, String name, long time) {
		new File(folder + "/kits").mkdirs();
		NBTTagCompound NBT = new NBTTagCompound();
		NBTTagList kit = toNBTTagList(p.getInventory().getContents());
		NBT.a("kit", kit);
		NBT.a("time", time);
		try {
			FileOutputStream stream = new FileOutputStream(folder + "/kits/" + name.toLowerCase() + ".kit");
			NBTCompressedStreamTools.a(NBT, stream);
			stream.close();
			p.sendMessage(kitsSave);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTime(Player p, File folder, String name, long time) {
		if (!new File(folder + "/kits/" + name.toLowerCase() + ".kit").exists()) {
			p.sendMessage(kitsNo);
			p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
			return;
		}
		NBTTagCompound NBT = fromNBTTagCompound(folder + "/kits/" + name.toLowerCase() + ".kit");
		NBT.a("time", time);
		try {
			FileOutputStream stream = new FileOutputStream(folder + "/kits/" + name.toLowerCase() + ".kit");
			NBTCompressedStreamTools.a(NBT, stream);
			stream.close();
			p.sendMessage(timeChange);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delKit(File folder, String name, Player p) {
		File k = new File(folder + "/kits/" + name.toLowerCase() + ".kit");
		if (k.exists()) {
			k.delete();
			p.sendMessage(kitsdell);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
		} else {
			p.sendMessage(kitsNo);
			p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 0);
		}
	}

	@Override
	public List<String> getLogs(File folder) {
		List<String> l = new ArrayList<String>();
		File Logs = new File(folder + "/kits");
		Logs.mkdirs();
		for (File file : Logs.listFiles()) {
			String kit = file.toString().substring(file.toString().lastIndexOf(File.separator) + 1);
			if (kit.contains(".kit")) {
				l.add(kit.replace(".kit", ""));
			}
		}
		return l;
	}

	private NBTTagList toNBTTagList(ItemStack[] inventory) {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			NBTTagCompound outputObject = new NBTTagCompound();
			boolean b = false;
			if (inventory[i] != null) {
				b = inventory[i].getType() == Material.AIR;
			}
			if (b) {
				inventory[i] = null;
			}
			CraftItemStack craft = (CraftItemStack) inventory[i];
			if (craft != null)
				CraftItemStack.asNMSCopy(craft).b(outputObject);
			itemList.add(outputObject);
		}
		return itemList;
	}

	private NBTTagCompound fromNBTTagCompound(String p) {
		try {
			return NBTCompressedStreamTools.a(new FileInputStream(p));
		} catch (IOException e) {
			e.printStackTrace();
			NBTTagCompound nul = new NBTTagCompound();
			return nul;
		}
	}

	private Inventory fromInventory(NBTTagList itemList) {
		Inventory inventory = new CraftInventoryCustom(null, itemList.size());
		for (int i = 0; i < itemList.size(); i++) {
			NBTTagCompound inputObject = (NBTTagCompound) itemList.get(i);
			if (!inputObject.f()) {
				inventory.setItem(i, CraftItemStack.asBukkitCopy(net.minecraft.world.item.ItemStack.a(inputObject)));
			}
		}
		return inventory;
	}

	@Override
	public void explode(Player p) {
		p.playSound((p).getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 2);
	}

	@Override
	public void openchest(Player p) {
		p.playSound((p).getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
	}
}