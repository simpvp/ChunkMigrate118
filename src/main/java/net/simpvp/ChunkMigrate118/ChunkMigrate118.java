package net.simpvp.ChunkMigrate118;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunkMigrate118 extends JavaPlugin {

	public static ChunkMigrate118 instance;

	public static int offset;
	public static int total;

	public ChunkMigrate118() {
		instance = this;
	}

	public void onEnable() {
		File dir = new File("plugins/ChunkMigrate118");
		if (!dir.exists()) {
			dir.mkdir();
		}

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		SQLite.connect();

		offset = this.getConfig().getInt("offset");
		total = SQLite.total_count();

		new BukkitRunnable() {
			@Override
			public void run() {
				ChunkLoader.run_iteration();
			}
		}.runTaskLater(this, 1);

		getLogger().info("Done enabling ChunkMigrate");
	}

	public void onDisable() {
		SQLite.close();
		this.getConfig().set("offset", offset);
		this.saveConfig();
	}

}
