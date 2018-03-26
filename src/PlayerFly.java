import org.lwjgl.glfw.GLFW;

import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.window.Input;
import com.nshirley.engine3d.window.MouseButton;

import engine.PlayerEntity;
import engine.Simulator;
import world.Raycast;

public class PlayerFly extends PlayerEntity {

	public float sety = 5;
	public PlayerFly(Entity box, Vector3f pos, Vector3f size, Camera3d cam) {
		super(box, pos, size, cam);
	}
	
	@Override
	public void update(Simulator s, float delta) {
		
		if (Input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
			sety -= .1f * delta;
		}
		if (Input.isKeyDown(GLFW.GLFW_KEY_SPACE)) {
			sety += .1f * delta;
		}
		
		super.player.setVelocityY(0);
		super.player.setPositionY(sety);
		super.update(s, delta);

	}

}
