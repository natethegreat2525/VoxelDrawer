import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;

import world.BulkBlockUpdate;
import world.Raycast;
import world.World;

public class TreeBuilder {

	public static void buildTree(Vector3f pos, World world) {
		Raycast rc = world.raycast(pos, new Vector3f(0, -1, 0), 40);
		if (rc == null || world.getBlockValue(rc.blockPosition) != 1) {
			return;
		}
		
		float base = rc.blockPosition.y;
		float thickness = (float) Math.random() * .5f + 1.5f;
		float height = (float) Math.random() * 5 + 7;
		if (Math.random() > .95) {
			height *= 2;
		}
		int numLeaves = 3;
		
		BulkBlockUpdate bbu = new BulkBlockUpdate();
		sphere(new Vector3f(pos.x, base, pos.z), new Vector3f(thickness + 1, thickness + 1, thickness + 1), (short) 116
				, bbu);
		cylinder(new Vector3f(pos.x, base + height / 2, pos.z), new Vector3f(thickness, height / 2, thickness), (short) 116, bbu);
		//colors
		short[] leafcols = {6, 120, 136, 140};
		short col = (short) (leafcols[(int) (Math.random() * leafcols.length)]);
		for (int i = 0; i < numLeaves; i++) {
			float ox = (float) Math.random() * 10 - 5;
			float oy = (float) Math.random() * 5 - 2;
			float oz = (float) Math.random() * 10 - 5;
			float tx = (float) Math.random() * 4 + 4;
			float tz = tx;
			float ty = (float) Math.random() * 1 + 2;
			sphere(new Vector3f(pos.x + ox, base + height + oy, pos.z + oz), new Vector3f(tx, ty, tz), col, bbu);
		}
		
		world.bulkUpdate(bbu);
	}
	
	public static void sphere(Vector3f center, Vector3f rad, short value, BulkBlockUpdate bbu) {
		Vector3i start = new Vector3i((int) (center.x - rad.x - 1), (int) (center.y - rad.y - 1), (int) (center.z - rad.z - 1));
		Vector3i end = new Vector3i((int) (center.x + rad.x + 1), (int) (center.y + rad.y + 1), (int) (center.z + rad.z + 1));
		for (int i = start.x; i <= end.x; i++) {
			for (int j = start.y; j <= end.y; j++) {
				for (int k = start.z; k <= end.z; k++) {
					double dx = (i - center.x) / rad.x;
					double dy = (j - center.y) / rad.y;
					double dz = (k - center.z) / rad.z;
					double dist = (dx * dx + dy * dy + dz * dz);
					if (dist < 1) {
						bbu.setBlockValue(i, j, k, value);
					}
				}
			}
		}
	}
	public static void cylinder(Vector3f center, Vector3f rad, short value, BulkBlockUpdate bbu) {
		Vector3i start = new Vector3i((int) (center.x - rad.x - 1), (int) (center.y - rad.y - 1), (int) (center.z - rad.z - 1));
		Vector3i end = new Vector3i((int) (center.x + rad.x + 1), (int) (center.y + rad.y + 1), (int) (center.z + rad.z + 1));
		for (int i = start.x; i <= end.x; i++) {
			for (int j = start.y; j <= end.y; j++) {
				for (int k = start.z; k <= end.z; k++) {
					double dx = (i - center.x) / rad.x;
					double dz = (k - center.z) / rad.z;
					double dist = (dx * dx + dz * dz);
					if (dist < 1) {
						bbu.setBlockValue(i, j, k, value);
					}
				}
			}
		}
	}
}
