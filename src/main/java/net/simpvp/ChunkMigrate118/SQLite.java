package net.simpvp.ChunkMigrate118;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class SQLite {

	private static Connection conn = null;

	private static HashMap<ChunkLoc, Boolean> cache = new HashMap<>();

	public static void connect() {

		String database = "jdbc:sqlite:plugins/ChunkMigrate118/chunkmigrate118.sqlite";

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(database);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void close() {
		try {
			conn.close();
		} catch (Exception e) {
			ChunkMigrate118.instance.getLogger().severe(e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean is_included(ChunkLoc chunk) {
		Boolean c = cache.get(chunk);
		if (c != null) {
			return c;
		}

		return load_region(chunk);
	}

	private static boolean load_region(ChunkLoc chunk) {
		int regionx = chunk.x >> 5;
		int regionz = chunk.z >> 5;

		boolean ret = false;

		int min_x = regionx << 5;
		int min_z = regionz << 5;

		int max_x = min_x + 32;
		int max_z = min_z + 32;

		for (int x = min_x; x < max_x; x++) {
			for (int z = min_z; z < max_z; z++) {
				cache.put(new ChunkLoc(x, z), false);
			}
		}

		try {
			PreparedStatement st = conn.prepareStatement("SELECT x, z FROM chunks WHERE regionx = ? AND regionz = ?");
			st.setInt(1, regionx);
			st.setInt(2, regionz);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int x = rs.getInt("x");
				int z = rs.getInt("z");

				if (x == chunk.x && z == chunk.z) {
					ret = true;
				}

				ChunkLoc c = new ChunkLoc(x, z);
				cache.put(c, true);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


		return ret;
	}

	public static ArrayList<ChunkLoc> load_next() {
		ArrayList<ChunkLoc> ret = new ArrayList<>();

		try {
			PreparedStatement st = conn.prepareStatement("SELECT id, x, z FROM chunks ORDER BY id LIMIT 500 OFFSET ?");
			st.setInt(1, ChunkMigrate118.offset);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				ChunkMigrate118.offset = rs.getInt("id");
				int x = rs.getInt("x");
				int z = rs.getInt("z");

				ChunkLoc c = new ChunkLoc(x, z);
				ret.add(c);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return ret;
	}

	public static Integer total_count() {
		Integer ret = null;

		try {
			PreparedStatement st = conn.prepareStatement("SELECT count(*) FROM chunks");
			ResultSet rs = st.executeQuery();
			rs.next();
			ret = rs.getInt(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return ret;
	}
}
