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
		
		BlockContainer.blockTypes[BlockContainer.NUM_BLOCK_TYPES - 1] = new NullBlock();
	}
}
