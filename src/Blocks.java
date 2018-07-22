import com.nshirley.engine3d.math.Vector4f;

import blockdraw.AirBlock;
import blockdraw.BlockContainer;
import blockdraw.LowPolyBlock;
import blockdraw.NullBlock;
import blockdraw.UniformBlock;
import blockdraw.WaterBlock;


public class Blocks {
	public static void init() {
		//Set first block type to be air
		BlockContainer.blockTypes[0] = new AirBlock();
		BlockContainer.blockTypes[1] = new UniformBlock(9, 4, 4, new Vector4f(0, .6f, .1f, 1));
		BlockContainer.blockTypes[2] = new UniformBlock(15, 4, 4, new Vector4f(1, 1, 1, 1));
		BlockContainer.blockTypes[3] = new WaterBlock(15, 4, 4, new Vector4f(.7f, .8f, 1, .5f));
		//save
		BlockContainer.blockTypes[4] = new UniformBlock(14, 4, 4, new Vector4f(1, 1, 1, 1));
		//open
		BlockContainer.blockTypes[5] = new UniformBlock(13, 4, 4, new Vector4f(1, 1, 1, 1));
		//grass
		BlockContainer.blockTypes[6] = new UniformBlock(15, 4, 4, new Vector4f(0, .15f, .01f, 1));

		BlockContainer.blockTypes[7] = new WaterBlock(15, 4, 4, new Vector4f(.05f, .3f, 1, .7f));

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 8; k++) {
					BlockContainer.blockTypes[100 + (i * 64 + j * 8 + k)] = new UniformBlock(15, 4, 4, new Vector4f(i/7.0f, j/7.0f, k/7.0f, 1));
				}
			}
		}

		
		BlockContainer.blockTypes[BlockContainer.NUM_BLOCK_TYPES - 1] = new NullBlock();
	}
}
