

import simplex.Simplex;
import world.ChunkBuilder;
import world.ChunkData;
import chunks.Chunk;

import com.nshirley.engine3d.math.Vector3i;

public class FlatPlane implements ChunkBuilder {
	
	private Simplex s = new Simplex((int) (Math.random() * Integer.MAX_VALUE));

	@Override
	public ChunkData buildChunk(Vector3i pos) {
		ChunkData chunk = new ChunkData(pos);
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					double x = (pos.x * Chunk.SIZE + i);
					double y = (pos.y * Chunk.SIZE + j);
					double z = (pos.z * Chunk.SIZE + k);
					double dx = x - 14;
					double dz = z;
					double dist = Math.sqrt(dx * dx + dz * dz)/5;
					dist = Math.min(dist, 100/5.0);
					double sn = s.noise(x / 64.0, y / 64.0, z / 64.0);
					double n = sn - y / dist;
					if (x >= 0 && x <= 24 && z >= -3 && z <= 20) {
						n = (y > 0) ? -1 : 1;
					}
					chunk.setValue(i, j, k, (short) (n < 0 ? 0 : 1));
				}
			}
		}
		return chunk;
	}

}
