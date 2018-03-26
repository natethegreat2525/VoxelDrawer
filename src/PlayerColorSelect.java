import org.lwjgl.glfw.GLFW;

import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;
import com.nshirley.engine3d.math.Vector4f;
import com.nshirley.engine3d.window.Input;
import com.nshirley.engine3d.window.MouseButton;
import com.nshirley.engine3d.window.MouseWheel;

import blockdraw.BlockContainer;
import blockdraw.UniformBlock;
import engine.PlayerEntity;
import engine.Simulator;
import world.Raycast;

public class PlayerColorSelect extends PlayerFly {

	public Entity cube;
	public int selected = 0;
	public int offsetX;
	public Vector3i buildSize;
	public boolean middleMouse;
	public Vector3i mmStart;
	
	public PlayerColorSelect(Entity box, Vector3f pos, Vector3f size, Camera3d cam, Vector3i buildSize, int offsetX) {
		super(box, pos, size, cam);
		this.cube = box;
		this.buildSize = buildSize;
		this.offsetX = offsetX;
	}
	
	public boolean checkBounds(Vector3i pos) {
		return (pos.x > offsetX && pos.x <= offsetX + buildSize.x &&
				pos.y > 0 && pos.y <= buildSize.y &&
				pos.z > 0 && pos.z <= buildSize.z);
	}
	
	@Override
	public void update(Simulator s, float delta) {
		super.update(s, delta);
		selected = (int) ((MouseWheel.Y + 64000) % 64);
		
		
		if (MouseButton.mouseDown(2)) {
			if (!middleMouse) {
				//hit
				middleMouse = true;
				Raycast rc = s.world.raycast(this.headPos.clone(), this.cam.getLookDir().clone(), 40);
				mmStart = rc.blockPosition.clone();
			}
		} else {
			if (middleMouse) {
				//release
				middleMouse = false;
				Raycast rc = s.world.raycast(this.headPos.clone(), this.cam.getLookDir().clone(), 40);
				Vector3i mmEnd = rc.blockPosition.clone();
				if (mmStart.x > mmEnd.x) {
					int tmp = mmStart.x;
					mmStart.x = mmEnd.x;
					mmEnd.x = tmp;
				}
				
				if (mmStart.y > mmEnd.y) {
					int tmp = mmStart.y;
					mmStart.y = mmEnd.y;
					mmEnd.y = tmp;
				}
				
				if (mmStart.z > mmEnd.z) {
					int tmp = mmStart.z;
					mmStart.z = mmEnd.z;
					mmEnd.z = tmp;
				}
				for (int i = mmStart.x; i < mmEnd.x+1; i++) {
					for (int j = mmStart.y; j < mmEnd.y+1; j++) {
						for (int k = mmStart.z; k < mmEnd.z+1; k++) {
							Vector3i pos = new Vector3i(i, j, k);
							if (checkBounds(pos)) {
								s.world.setBlockValue(pos, (short) (100 + selected)); 
							}
						}
					}
				}
				VoxelDrawer.pushNewVersion(s.world);
			}
		}
		
		if (MouseButton.mousePress(0)) {
			Raycast rc = s.world.raycast(this.headPos.clone(), this.cam.getLookDir().clone(), 40);
			if (rc != null) {
				short v = s.world.getBlockValue(rc.blockPosition);
				if (v == 4) {
					//save;
					VoxelDrawer.saveCurrent(s.world);
				}
				if (v == 5) {
					//open
					VoxelDrawer.openNew(s.world);
				}
				rc.blockPosition.x += rc.normal.x;
				rc.blockPosition.y += rc.normal.y;
				rc.blockPosition.z += rc.normal.z;
				if (checkBounds(rc.blockPosition)) {
					s.world.setBlockValue(rc.blockPosition, (short) (100 + selected));
					VoxelDrawer.pushNewVersion(s.world);
				}
			}
		}
		if (MouseButton.mousePress(1)) {
			Raycast rc = s.world.raycast(this.headPos.clone(), this.cam.getLookDir().clone(), 40);
			if (rc != null) {
				if (checkBounds(rc.blockPosition)) {
					s.world.setBlockValue(rc.blockPosition, (short) 0);
					VoxelDrawer.pushNewVersion(s.world);
				}
			}
		}
		if (Input.isKeyHit(GLFW.GLFW_KEY_E)) {
			Raycast rc = s.world.raycast(this.headPos.clone(), this.cam.getLookDir().clone(), 40);
			if (rc != null) {
				MouseWheel.Y = s.world.getBlockValue(rc.blockPosition) - 100; 
			}
		}
	}
	
	public void render(int pass) {
		super.render(pass);
		if (pass == 1) {
			cube.setColor(((UniformBlock) BlockContainer.getBlockType((short) (selected + 100))).getColor());
			cube.setModelMatrix(
					Matrix4f.identity()
						.multiply(Matrix4f.translate(new Vector3f(50, 50, 0)))
						.multiply(Matrix4f.scale(new Vector3f(10f, 10f, 10f)))
						.multiply(Matrix4f.rotateX(45))
						.multiply(Matrix4f.rotateY((System.currentTimeMillis() % 3600)/10f))

					);
			cube.render();
			cube.setColor(new Vector4f(1, 1, 1, 1));
		}
	}

}
