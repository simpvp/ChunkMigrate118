package net.simpvp.ChunkMigrate118;

import java.util.Objects;

import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkLoc {
	int x;
	int z;

	ChunkLoc(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public Chunk chunk(World world) {
		return world.getChunkAt(this.x, this.z);
	}

	public boolean included() {
		return SQLite.is_included(this);
	}

	public ChunkLoc west() {
		return new ChunkLoc(this.x - 1, this.z);
	}

	public ChunkLoc south() {
		return new ChunkLoc(this.x, this.z + 1);
	}

	public ChunkLoc east() {
		return new ChunkLoc(this.x + 1, this.z);
	}

	public ChunkLoc north() {
		return new ChunkLoc(this.x, this.z - 1);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ChunkLoc other = (ChunkLoc) o;
		return this.x == other.x && this.z == other.z;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.x, this.z);
	}
}
