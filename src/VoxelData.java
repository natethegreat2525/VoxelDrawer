import com.nshirley.engine3d.math.Vector3i;

public class VoxelData {

	public Vector3i size;
	public int[] data;
	
	public VoxelData(Vector3i size, int[] data) {
		this.size = size;
		this.data = data;
	}
}
