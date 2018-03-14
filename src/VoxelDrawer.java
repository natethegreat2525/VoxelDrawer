

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFW;

import world.ChunkBuilderThread;
import world.Player;
import world.Raycast;
import world.World;
import chunks.ChunkViewport;

import com.nshirley.engine3d.N3D;
import com.nshirley.engine3d.entities.Camera3d;
import com.nshirley.engine3d.entities.Entity;
import com.nshirley.engine3d.entities.shapes.Shape;
import com.nshirley.engine3d.graphics.Texture;
import com.nshirley.engine3d.math.Matrix4f;
import com.nshirley.engine3d.math.Vector3f;
import com.nshirley.engine3d.math.Vector3i;
import com.nshirley.engine3d.window.Input;
import com.nshirley.engine3d.window.Mouse;
import com.nshirley.engine3d.window.Window;

import drawentity.ChunkEntity;
import drawentity.FadeEntity;
import engine.PlayerEntity;
import engine.Simulator;
import physics.PhysSim;
import physics.Rect;

public class VoxelDrawer {
	
	public static int grassAdded, grassEaten;
	public static int bunniesBorn, bunniesDied;

	public static int WIDTH = 1024, HEIGHT = 768;

	public static void main(String[] args) throws InterruptedException {
		Window win = new Window(WIDTH, HEIGHT, "Voxel Drawer");
		win.setCursorMode(GLFW.GLFW_CURSOR_DISABLED);

		N3D.init();
		ChunkEntity.loadShader();
		FadeEntity.loadShader();

		Texture tx = new Texture("res/blocks_a.png");
		Texture blank = new Texture("res/blank.png");
		
		Blocks.init();

		Camera3d c = new Camera3d((float) Math.toRadians(100), WIDTH, HEIGHT,
				.1f, 1000);
		
		Entity box = new Entity(Shape.cube(), blank);

		World world = new World(new FlatPlane());
		ChunkViewport cv = new ChunkViewport(new Vector3i(), new Vector3i(5, 3, 5), world, tx);		
		Simulator sim = new Simulator(world, cv, new Vector3f(0, 0, 0), box);
		
		Raycast playerStart = world.raycast(new Vector3f(.5f, 30, .5f), new Vector3f(0, -1, 0), 30);
		playerStart.position.y += .5;

		PlayerEntity player = new PlayerEntity(box, playerStart.position, new Vector3f(.5f, 1.5f, .5f), c);
		sim.add(player);
				
		long deltaTime = System.currentTimeMillis();
		float delta = 1;
		glClearColor(.7f, 1, 1, 1);
				
		while (!win.shouldClose()) {
			long newDelta = System.currentTimeMillis();
			delta = (newDelta - deltaTime) / (1000 / 60.0f);
			long diff = newDelta - deltaTime;

			deltaTime = newDelta;
			delta = Math.min(delta, 4);
			
			if (diff < 10) {
				Thread.sleep(10 - diff);
			}
			
			
			win.clear();
			win.pollEvents();
			
			sim.update(delta);
			
			N3D.pushMatrix();
			N3D.multMatrix(c.getTotalMatrix());
			
			if (Input.isKeyDown(GLFW.GLFW_KEY_Q)) {
				glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
			} else {
				glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
			}
			
			sim.render(c.getPosition(), c.getLookDir());

			N3D.popMatrix();
						
			int i = glGetError();
			if (i != GL_NO_ERROR) {
				System.out.println(i);
			}
			
			win.flip();
			if (Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE))
				break;
		}
		sim.finish();
	}
}