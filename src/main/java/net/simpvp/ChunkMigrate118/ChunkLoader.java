package net.simpvp.ChunkMigrate118;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ChunkLoader {
	private static int count = 0;

	public static void run_iteration() {
		ChunkMigrate118.instance.getLogger().info(String.format("Have completed %d chunks in this session and %d in total. %d chunks to be processed in total. %f complete.", count, ChunkMigrate118.offset, ChunkMigrate118.total, (double) ChunkMigrate118.offset / (double) ChunkMigrate118.total));
		if (count >= 50000) {
			ChunkMigrate118.instance.getLogger().info("Saving and restarting server");
			ChunkMigrate118.instance.getServer().dispatchCommand(ChunkMigrate118.instance.getServer().getConsoleSender(), "stop");
			return;
		}

		World world = ChunkMigrate118.instance.getServer().getWorld("world");
		ArrayList<ChunkLoc> next = SQLite.load_next();
		if (next.isEmpty()) {
			ChunkMigrate118.instance.getLogger().info("Done with all chunks");
			return;
		}

		for (ChunkLoc chunk : next) {
			load_chunk(world, chunk);
			count++;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				run_iteration();
			}
		}.runTaskLater(ChunkMigrate118.instance, 1);
	}

	private static void load_chunk(World world, ChunkLoc chunk) {
		Chunk c = chunk.chunk(world);

		ChunkLoc west = chunk.west();
		//ChunkMigrate118.instance.getLogger().info(String.format("Checking west wall for %d %d: %d %d", chunk.x, chunk.z, west.x, west.z));
		if (!west.included()) {
			//ChunkMigrate118.instance.getLogger().info(String.format("setting west wall for %d %d", chunk.x, chunk.z));
			int x = 0;
			for (int z = 0; z < 16; z++) {
				for (int y = -64; y < 0; y++) {
					Block b = c.getBlock(x, y, z);
					b.setType(Material.BEDROCK, false);
				}
			}
		}

		ChunkLoc south = chunk.south();
		if (!south.included()) {
			int z = 15;
			for (int x = 0; x < 16; x++) {
				for (int y = -64; y < 0; y++) {
					Block b = c.getBlock(x, y, z);
					b.setType(Material.BEDROCK, false);
				}
			}
		}

		ChunkLoc east = chunk.east();
		if (!east.included()) {
			int x = 15;
			for (int z = 0; z < 16; z++) {
				for (int y = -64; y < 0; y++) {
					Block b = c.getBlock(x, y, z);
					b.setType(Material.BEDROCK, false);
				}
			}
		}

		ChunkLoc north = chunk.north();
		if (!north.included()) {
			int z = 0;
			for (int x = 0; x < 16; x++) {
				for (int y = -64; y < 0; y++) {
					Block b = c.getBlock(x, y, z);
					b.setType(Material.BEDROCK, false);
				}
			}
		}
	}
}
