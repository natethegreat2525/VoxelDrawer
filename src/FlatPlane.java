

import simplex.Simplex;
import world.ChunkBuilder;
import world.ChunkData;
import chunks.Chunk;

import com.nshirley.engine3d.math.Vector3i;

public class FlatPlane implements ChunkBuilder {
	
	@Override
	public ChunkData buildChunk(Vector3i pos) {
		ChunkData chunk = new ChunkData(pos);
		
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					double y = (pos.y * Chunk.SIZE + j);
					chunk.setValue(i, j, k, (short) (y > 0 ? 0 : 1));
				}
			}
		}
		return chunk;
	}

}
