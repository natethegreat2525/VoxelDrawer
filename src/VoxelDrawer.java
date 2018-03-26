

import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.stb.STBEasyFont.*;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glGetError;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import world.BulkBlockUpdate;
import world.ChunkBuilderThread;
import world.Player;
import world.Raycast;
import world.World;
import chunks.ChunkViewport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.lwjgl.stb.STBEasyFont;
import java.awt.*;
import java.io.File;
import java.io.IOException;
public class VoxelDrawer {
	
	public static int WIDTH = 1024, HEIGHT = 768;
	
	public static int sX, sY, sZ;
	public static int offsetX = 6;
	
	public static ChunkViewport cv;
	
	public static ArrayList<VoxelData> versions = new ArrayList<VoxelData>();
	
	public static void pushNewVersion(World world) {
		int[] data = new int[sX * sY * sZ];
		for (int i = 0; i < sX; i++) {
			for (int j = 0; j < sY; j++) {
				for (int k = 0; k < sZ; k++) {
					data[i + j*sX + k*sX*sY] = world.getBlockValue(new Vector3i(i + offsetX + 1, j + 1, k + 1)) - 100;
				}
			}
		}
		VoxelData vd = new VoxelData(new Vector3i(sX, sY, sZ), data);
		versions.add(vd);
	}
	
	public static void popOldVersion(World world) {
		if (versions.size() <= 1) {
			return;
		}
		versions.remove(versions.size() - 1);
		VoxelData vd = versions.get(versions.size() - 1);
		BulkBlockUpdate bbu = new BulkBlockUpdate();
		for (int i = 0; i < sX; i++) {
			for (int j = 0; j < sY; j++) {
				for (int k = 0; k < sZ; k++) {
					bbu.setBlockValue(new Vector3i(i + offsetX + 1, j + 1, k + 1),(short) (vd.data[i + j*sX + k*sX*sY] + 100));
				}
			}
		}
		world.bulkUpdate(bbu);
	}
	
	public static void showOptions() {
		for (;;) {
			JTextField x = new JTextField("16", 10);
			JTextField y = new JTextField("16", 10);
			JTextField z = new JTextField("16", 10);
			
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(3, 2));
	
			p.add(new JLabel("Width"));
			p.add(x);
			p.add(new JLabel("Height"));
			p.add(y);
			p.add(new JLabel("Length (Depth)"));
			p.add(z);
			JOptionPane.showMessageDialog(null, p);
			
			try {
			sX = Integer.parseInt(x.getText());
			sY = Integer.parseInt(y.getText());
			sZ = Integer.parseInt(z.getText());
			} catch (Exception e) {
				continue;
			}
			break;
		}
	}
	
	public static void initBlocks(World world) {
		BulkBlockUpdate bbu = new BulkBlockUpdate();

		int cnt = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					int x = k;
					int y = 0;
					int z = j + i * 5;
					bbu.setBlockValue(x, y, z, (short) (cnt + 100));
					cnt++;
				}
			}
		}
		
		for (int i = 0; i < sX + 2; i++) {
			bbu.setBlockValue(i + offsetX, 0, 0, (short) (3));
			bbu.setBlockValue(i + offsetX, sY + 1, 0, (short) (3));
			bbu.setBlockValue(i + offsetX, 0, sZ + 1, (short) (3));
			bbu.setBlockValue(i + offsetX, sY + 1, sZ + 1, (short) (3));
		}
		for (int i = 0; i < sY + 2; i++) {
			bbu.setBlockValue(offsetX, i, 0, (short) (3));
			bbu.setBlockValue(offsetX + sX + 1, i, 0, (short) (3));
			bbu.setBlockValue(offsetX + sX + 1, i, sZ + 1, (short) (3));
			bbu.setBlockValue(offsetX, i, sZ + 1, (short) (3));
		}
		
		for (int i = 0; i < sZ + 2; i++) {
			bbu.setBlockValue(offsetX, 0, i, (short) (3));
			bbu.setBlockValue(offsetX + sX + 1, 0, i, (short) (3));
			bbu.setBlockValue(offsetX + sX + 1, sY + 1, i, (short) (3));
			bbu.setBlockValue(offsetX, sY + 1, i, (short) (3));
		}
		
		//save
		bbu.setBlockValue(offsetX,  1, -3, (short) (4));
		//open
		bbu.setBlockValue(offsetX + 5,  1, -3, (short) (5));
		
		world.bulkUpdate(bbu);
		for (int i = 0; i < 200; i ++) {
			double x = Math.random() * 200 - 100;
			double z = Math.random() * 200 - 100;
			if (x > -10 && x < 34 && z > -10 && z < 28) {
				continue;
			}
			TreeBuilder.buildTree(new Vector3f((float) x, 20, (float) z), world);
		}
	}
	
	public static void saveCurrent(World world) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Voxel", "vox");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(filter);
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			int[] data = new int[sX * sY * sZ];
			for (int i = 0; i < sX; i++) {
				for (int j = 0; j < sY; j++) {
					for (int k = 0; k < sZ; k++) {
						data[i + j*sX + k*sX*sY] = world.getBlockValue(new Vector3i(i + offsetX + 1, j + 1, k + 1)) - 100;
					}
				}
			}
			VoxelData vd = new VoxelData(new Vector3i(sX, sY, sZ), data);
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			try {
				Files.write(Paths.get(file.getPath() + ".vox"), gson.toJson(vd).getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "File was not saved!\n" + e.getStackTrace(), "File Save Error", JOptionPane.ERROR_MESSAGE, null);
			}
		}
	}
	
	public static void openNew(World world) {
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Voxel", "vox");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(filter);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			String json;
			try {
				json = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				VoxelData vd = gson.fromJson(json, VoxelData.class);
				
				sX = vd.size.x;
				sY = vd.size.y;
				sZ = vd.size.z;
				
				world.flushCache();
				cv.reset();
				
				initBlocks(world);
				BulkBlockUpdate bbu = new BulkBlockUpdate();
				for (int i = 0; i < sX; i++) {
					for (int j = 0; j < sY; j++) {
						for (int k = 0; k < sZ; k++) {
							bbu.setBlockValue(new Vector3i(i + offsetX + 1, j + 1, k + 1),(short) (vd.data[i + j*sX + k*sX*sY] + 100));
						}
					}
				}
				world.bulkUpdate(bbu);
				versions = new ArrayList<VoxelData>();
				versions.add(vd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "File could not be Opened!\n" + e.getStackTrace(), "File Open Error", JOptionPane.ERROR_MESSAGE, null);
			}
		}
		
	}

	public static void main(String[] args) throws InterruptedException {
		showOptions();
		Window win = new Window(WIDTH, HEIGHT, "Voxel Drawer");
		win.setCursorMode(GLFW.GLFW_CURSOR_DISABLED);
		N3D.init();
		ChunkEntity.loadShader();
		FadeEntity.loadShader();

		Texture tx = new Texture(VoxelDrawer.class.getClassLoader().getResourceAsStream("blocks_a.png"));
		Texture blank = new Texture(VoxelDrawer.class.getClassLoader().getResourceAsStream("blank.png"));
		
		Blocks.init();

		Camera3d c = new Camera3d((float) Math.toRadians(100), WIDTH, HEIGHT,
				.1f, 1000);
		Matrix4f ortho = Matrix4f.orthographic(0, WIDTH, 0, HEIGHT, -HEIGHT, HEIGHT);
		
		Entity box = new Entity(Shape.cube(), blank);

		World world = new World(new FlatPlane());
		cv = new ChunkViewport(new Vector3i(), new Vector3i(15, 3, 15), world, tx);		
		Simulator sim = new Simulator(world, cv, new Vector3f(0, 0, 0), box);
		
		

		PlayerColorSelect player = new PlayerColorSelect(box, new Vector3f(0, 0, 0), new Vector3f(.5f, 1.5f, .5f), c, new Vector3i(sX, sY, sZ), offsetX);
		sim.add(player);
				
		long deltaTime = System.currentTimeMillis();
		float delta = 1;
		glClearColor(126/255f, 192/255f, 238/255f, 1);
		initBlocks(world);
		pushNewVersion(world);
		
		while (!win.shouldClose()) {
			player.buildSize = new Vector3i(sX, sY, sZ);
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
			
			if (Input.isKeyHit(GLFW.GLFW_KEY_U)) {
				popOldVersion(world);
			}
			
			sim.render(c.getPosition(), c.getLookDir());

			N3D.popMatrix();
			N3D.pushMatrix();
			N3D.multMatrix(ortho);
			
			sim.render(c.getPosition(), c.getLookDir(), 1);

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