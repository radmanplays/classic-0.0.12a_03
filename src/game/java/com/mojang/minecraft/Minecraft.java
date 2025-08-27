package com.mojang.minecraft;

import com.mojang.minecraft.character.Vec3;
import com.mojang.minecraft.character.Zombie;
import com.mojang.minecraft.gui.Font;
import com.mojang.minecraft.level.Chunk;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.LevelRenderer;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleEngine;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Frustum;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import com.mojang.util.GLAllocation;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EagUtils;

import com.mojang.minecraft.level.DirtyChunkSorter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;

public final class Minecraft implements Runnable {
	private boolean fullscreen = false;
	public int width;
	public int height;
	private FloatBuffer fogColor0 = GLAllocation.createFloatBuffer(4);
	private FloatBuffer fogColor1 = GLAllocation.createFloatBuffer(4);
	private Timer timer = new Timer(20.0F);
	private Level level;
	private LevelRenderer levelRenderer;
	private Player thePlayer;
	private int paintTexture = 1;
	private ParticleEngine particleEngine;
	private ArrayList entities = new ArrayList();
	private int yMouseAxis = 1;
	private Textures textureManager;
	private Font font;
	private int editMode = 0;
	volatile boolean running = false;
	private String fpsString = "";
	private boolean mouseGrabbed = false;
	private IntBuffer viewportBuffer = GLAllocation.createIntBuffer(16);
	private IntBuffer selectBuffer = GLAllocation.createIntBuffer(2000);
	private HitResult hitResult = null;
	private FloatBuffer lb = GLAllocation.createFloatBuffer(16);
	
	public Minecraft(int var2, int var3, boolean var4) {
		this.width = width;
		this.height = height;
		this.fullscreen = false;
		this.textureManager = new Textures();
	}
	
	
	private static void reportGLError(String string) {
		int errorCode = GL11.glGetError();
		if(errorCode != 0) {
			String errorString = GLU.gluErrorString(errorCode);
			System.out.println("########## GL ERROR ##########");
			System.out.println("@ " + string);
			System.out.println(errorCode + ": " + errorString);
			throw new RuntimeException(errorCode + ": " + errorString);

		}

	}

	public final void destroy() {
		try {
			this.level.save();
		} catch (Exception var2) {
			var2.printStackTrace();
		}
		EagRuntime.destroy();
	}

	public final void run() {
		this.running = true;
		try {
			Minecraft var4 = this;
			float var8 = 0.5F;
			float var9 = 0.8F;
			this.fogColor0.put(new float[]{var8, var9, 1.0F, 1.0F});
			this.fogColor0.flip();
			this.fogColor1.put(new float[]{(float)14 / 255.0F, (float)11 / 255.0F, (float)10 / 255.0F, 1.0F});
			this.fogColor1.flip();
			if(this.fullscreen) {
				Display.toggleFullscreen();
				this.width = Display.getWidth();
				this.height = Display.getHeight();
			} else {
				this.width = Display.getWidth();
				this.height = Display.getHeight();
			}
			
			Display.setTitle("Minecraft 0.0.12a_03");

			Display.create();
			Keyboard.create();
			Mouse.create();
			reportGLError("Pre startup");
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glClearColor(var8, var9, 1.0F, 0.0F);
			GL11.glClearDepth(1.0D);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
			GL11.glCullFace(GL11.GL_BACK);
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			reportGLError("Startup");
			this.font = new Font("/default.gif", this.textureManager);
			IntBuffer var1 = GLAllocation.createIntBuffer(256);
			var1.clear().limit(256);
			GL11.glViewport(0, 0, this.width, this.height);
			this.level = new Level(this, 256, 256, 64);
			this.levelRenderer = new LevelRenderer(this.level, this.textureManager);
			this.thePlayer = new Player(this.level);
			this.particleEngine = new ParticleEngine(this.level, this.textureManager);
			int var2 = 0;

			while(true) {
				if(var2 >= 10) {
					var4.grabMouse();
					reportGLError("Post startup");
					break;
				}

				Zombie var3 = new Zombie(var4.level, var4.textureManager, 128.0F, 0.0F, 128.0F);
				var3.resetPos();
				var4.entities.add(var3);
				++var2;
			}
		} catch (Exception var9) {
			var9.printStackTrace();
			System.out.println("Failed to start Minecraft");
			destroy();
		}

		long var23 = System.currentTimeMillis();
		int var24 = 0;

		try {
			while(this.running) {
					if(Display.isCloseRequested()) {
						this.running = false;
					}

					Timer var25 = this.timer;
					long var7 = System.nanoTime();
					long var27 = var7 - var25.lastTime;
					var25.lastTime = var7;
					if(var27 < 0L) {
						var27 = 0L;
					}

					if(var27 > 1000000000L) {
						var27 = 1000000000L;
					}

					var25.passedTime += (float)var27 * var25.timeScale * var25.ticksPerSecond / 1.0E9F;
					var25.ticks = (int)var25.passedTime;
					if(var25.ticks > 100) {
						var25.ticks = 100;
					}

					var25.passedTime -= (float)var25.ticks;
					var25.a = var25.passedTime;

					for(int var26 = 0; var26 < this.timer.ticks; ++var26) {
						this.tick();
					}

					reportGLError("Pre render");
					this.render(this.timer.a);
					reportGLError("Post render");
					++var24;

					while(System.currentTimeMillis() >= var23 + 1000L) {
						this.fpsString = var24 + " fps, " + Chunk.updates + " chunk updates";
						Chunk.updates = 0;
						var23 += 1000L;
						var24 = 0;
					}
				}
			return;
		} catch (Exception var10) {
			var10.printStackTrace();
		} finally {
			this.destroy();
		}

	}

	public void grabMouse() {
		if(!this.mouseGrabbed) {
			this.mouseGrabbed = true;
			Mouse.setGrabbed(true);

		}
	}
	
	public void releaseMouse() {
		if(this.mouseGrabbed) {
			this.mouseGrabbed = false;
			Mouse.setGrabbed(false);

		}
	}
	
	private int saveCountdown = 600;

	private void levelSave() {
	    if (level == null) return;

	    saveCountdown--;
	    if (saveCountdown <= 0) {
	        level.save();
	        saveCountdown = 600;
	    }
	}
	
	private void tick() {
		int var4;
		int var12;
		int var13;
		int var14;
		while(Mouse.next()) {
			if(!this.mouseGrabbed && Mouse.getEventButtonState()) {
				this.grabMouse();
			} else {
				if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
					if(this.editMode == 0) {
						if(this.hitResult != null) {
							Tile var2 = Tile.tiles[this.level.getTile(this.hitResult.x, this.hitResult.y, this.hitResult.z)];
							boolean var3 = this.level.setTile(this.hitResult.x, this.hitResult.y, this.hitResult.z, 0);
							if(var2 != null && var3) {
								var2.destroy(this.level, this.hitResult.x, this.hitResult.y, this.hitResult.z, this.particleEngine);
							}
						}
					} else if(this.hitResult != null) {
						label197: {
							var12 = this.hitResult.x;
							var13 = this.hitResult.y;
							var4 = this.hitResult.z;
							if(this.hitResult.f == 0) {
								--var13;
							}

							if(this.hitResult.f == 1) {
								++var13;
							}

							if(this.hitResult.f == 2) {
								--var4;
							}

							if(this.hitResult.f == 3) {
								++var4;
							}

							if(this.hitResult.f == 4) {
								--var12;
							}

							if(this.hitResult.f == 5) {
								++var12;
							}

							AABB var5 = Tile.tiles[this.paintTexture].getBoundingBox(var12, var13, var4);
							if(var5 != null) {
								AABB var7 = var5;
								Minecraft var6 = this;
								boolean var10000;
								if(this.thePlayer.boundingBox.intersects(var5)) {
									var10000 = false;
								} else {
									var14 = 0;

									while(true) {
										if(var14 >= var6.entities.size()) {
											var10000 = true;
											break;
										}

										if(((Entity)var6.entities.get(var14)).boundingBox.intersects(var7)) {
											var10000 = false;
											break;
										}

										++var14;
									}
								}

								if(!var10000) {
									break label197;
								}
							}

							this.level.setTile(var12, var13, var4, this.paintTexture);
						}
					}
				}

				if(Mouse.getEventButton() == 1 && Mouse.getEventButtonState()) {
					this.editMode = (this.editMode + 1) % 2;
				}
			}
		}

		while(true) {
			do {
				if(!Keyboard.next()) {
					Level var9 = this.level;
					var9.unprocessed += var9.width * var9.height * var9.depth;
					var12 = var9.unprocessed / 200;
					var9.unprocessed -= var12 * 200;

					for(var13 = 0; var13 < var12; ++var13) {
						var9.randValue = var9.randValue * 1664525 + 1013904223;
						var4 = var9.randValue >> 16 & var9.width - 1;
						var9.randValue = var9.randValue * 1664525 + 1013904223;
						var14 = var9.randValue >> 16 & var9.depth - 1;
						var9.randValue = var9.randValue * 1664525 + 1013904223;
						int var16 = var9.randValue >> 16 & var9.height - 1;
						byte var17 = var9.blocks[(var14 * var9.height + var16) * var9.width + var4];
						if(Tile.shouldTick[var17]) {
							Tile.tiles[var17].tick(var9, var4, var14, var16, var9.random);
						}
					}

					ParticleEngine var10 = this.particleEngine;

					for(var12 = 0; var12 < var10.particles.size(); ++var12) {
						Particle var15 = (Particle)var10.particles.get(var12);
						var15.tick();
						if(var15.removed) {
							var10.particles.remove(var12--);
						}
					}

					for(int var11 = 0; var11 < this.entities.size(); ++var11) {
						((Entity)this.entities.get(var11)).tick();
						if(((Entity)this.entities.get(var11)).removed) {
							this.entities.remove(var11--);
						}
					}

					this.thePlayer.tick();
		            levelSave();
					return;
				}
			} while(!Keyboard.getEventKeyState());

			if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE && (!this.fullscreen)) {
				this.releaseMouse();
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
				this.level.save();
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_1) {
				this.paintTexture = 1;
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_2) {
				this.paintTexture = 3;
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_3) {
				this.paintTexture = 4;
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_4) {
				this.paintTexture = 5;
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_6) {
				this.paintTexture = 6;
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_Y) {
				this.yMouseAxis = -this.yMouseAxis;
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_G) {
				this.entities.add(new Zombie(this.level, this.textureManager, this.thePlayer.x, this.thePlayer.y, this.thePlayer.z));
			}
			if(Keyboard.getEventKey() == Keyboard.KEY_N) {
				this.level.generateLevel();
				this.thePlayer.resetPos();
				boolean var1 = false;

				while(0 < this.entities.size()) {
					this.entities.remove(0);
				}
			}

			if(Keyboard.getEventKey() == Keyboard.KEY_F) {
				LevelRenderer var8 = this.levelRenderer;
				var8.drawDistance = (var8.drawDistance + 1) % 4;
			}
		}
	}

	private void focusPlayerCamera(float var1) {
		GL11.glTranslatef(0.0F, 0.0F, -0.3F);
		GL11.glRotatef(this.thePlayer.pitch, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(this.thePlayer.yaw, 0.0F, 1.0F, 0.0F);
		float var2 = this.thePlayer.xo + (this.thePlayer.x - this.thePlayer.xo) * var1;
		float var3 = this.thePlayer.yo + (this.thePlayer.y - this.thePlayer.yo) * var1;
		float var4 = this.thePlayer.zo + (this.thePlayer.z - this.thePlayer.zo) * var1;
		GL11.glTranslatef(-var2, -var3, -var4);
	}

	private void render(float var1) {
		if(!Display.isActive()) {
			this.releaseMouse();
		}
		if (Display.wasResized()) {
			this.width = Display.getWidth();
			this.height = Display.getHeight();
		}
		GL11.glViewport(0, 0, this.width, this.height);
		float var5;
		if(this.mouseGrabbed) {
			float var2 = 0.0F;
			float var3 = 0.0F;
			var2 = (float)Mouse.getDX();
			var3 = (float)Mouse.getDY();
			var5 = var3 * (float)this.yMouseAxis;
			Player var19 = this.thePlayer;
			var19.yaw = (float)((double)var19.yaw + (double)var2 * 0.15D);
			var19.pitch = (float)((double)var19.pitch - (double)var5 * 0.15D);
			if(var19.pitch < -90.0F) {
				var19.pitch = -90.0F;
			}

			if(var19.pitch > 90.0F) {
				var19.pitch = 90.0F;
			}
		}

		this.reportGLError("Set viewport");
		float pitch = this.thePlayer.pitch;
		float yaw = this.thePlayer.yaw;

		double px = this.thePlayer.x;
		double py = this.thePlayer.y;
		double pz = this.thePlayer.z;

		Vec3 cameraPos = new Vec3((float)px, (float)py, (float)pz);

		float cosYaw = (float)Math.cos(-Math.toRadians(yaw) - Math.PI);
		float sinYaw = (float)Math.sin(-Math.toRadians(yaw) - Math.PI);
		float cosPitch = (float)Math.cos(-Math.toRadians(pitch));
		float sinPitch = (float)Math.sin(-Math.toRadians(pitch));

		float dirX = sinYaw * cosPitch;
		float dirY = sinPitch;
		float dirZ = cosYaw * cosPitch;
		float reachDistance = 3.0F;
		if (pitch > 60.0F) {
		    reachDistance += 1.0F;
		}
		if (pitch >= 55.0F && pitch <= 60.0F) {
		    reachDistance += 2.0F;
		}
		Vec3 reachVec = new Vec3(
		    cameraPos.x + dirX * reachDistance,
		    cameraPos.y + dirY * reachDistance,
		    cameraPos.z + dirZ * reachDistance
		);

		this.hitResult = this.level.clip(cameraPos, reachVec);
		this.reportGLError("Picked");
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 1000.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		this.focusPlayerCamera(var1);
		reportGLError("Set up camera");
		GL11.glEnable(GL11.GL_CULL_FACE);
		Frustum var18 = Frustum.calculateFrustum();
		Frustum var21 = var18;
		LevelRenderer var22 = this.levelRenderer;

	    for (int i = 0; i < levelRenderer.chunks.length; ++i) {
	        levelRenderer.chunks[i].visible = var18.isVisible(levelRenderer.chunks[i].aabb);
	    }

		Player var4 = this.thePlayer;
		var22 = this.levelRenderer;
		LevelRenderer var35 = var22;
		ArrayList var37 = null;

		for(int i = 0; i < var35.chunks.length; ++i) {
			Chunk chunk = var35.chunks[i];
			if(chunk.isDirty()) {
				if(var37 == null) {
					var37 = new ArrayList();
				}

				var37.add(chunk);
			}
		}

		ArrayList var32 = var37;
		if(var37 != null) {
			Collections.sort(var37, new DirtyChunkSorter(var4));

			for(int i = 0; i < 8 && i < var32.size(); ++i) {
				((Chunk)var32.get(i)).rebuild();
			}
		}

		reportGLError("Update chunks");
		this.setupFog(0);
		GL11.glEnable(GL11.GL_FOG);
		this.levelRenderer.render(this.thePlayer, 0);
		reportGLError("Rendered level");

		Entity var24;
		int var25;
		for(var25 = 0; var25 < this.entities.size(); ++var25) {
			var24 = (Entity)this.entities.get(var25);
			if(var24.isLit() && var18.isVisible(var24.boundingBox)) {
				((Entity)this.entities.get(var25)).render(var1);
			}
		}

		reportGLError("Rendered entities");
		this.particleEngine.render(this.thePlayer, var1, 0);
		reportGLError("Rendered particles");
		this.setupFog(1);
		this.levelRenderer.render(this.thePlayer, 1);

		for(var25 = 0; var25 < this.entities.size(); ++var25) {
			var24 = (Entity)this.entities.get(var25);
			if(!var24.isLit() && var18.isVisible(var24.boundingBox)) {
				((Entity)this.entities.get(var25)).render(var1);
			}
		}

		this.particleEngine.render(this.thePlayer, var1, 1);
		var22 = this.levelRenderer;
	    var22.compileSurroundingGround();
		this.setupFog(0);
		var22 = this.levelRenderer;
	    var22.compileSurroundingWater();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDepthMask(false);
		this.levelRenderer.render(this.thePlayer, 2);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_FOG);
		reportGLError("Rendered rest");
		if(this.hitResult != null) {
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			this.levelRenderer.renderHit(this.thePlayer, this.hitResult, this.editMode, this.paintTexture);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}

		reportGLError("Rendered hit");
		int var27 = this.width * 240 / this.height;
		int var26 = this.height * 240 / this.height;
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)var27, (double)var26, 0.0D, 100.0D, 300.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -200.0F);
		reportGLError("GUI: Init");
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(var27 - 16), 16.0F, 0.0F);
		Tesselator var33 = Tesselator.tesselator;
		GL11.glScalef(16.0F, 16.0F, 16.0F);
		GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-1.5F, 0.5F, -0.5F);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int var30 = this.textureManager.loadTexture("/terrain.png", 9728);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, var30);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		var33.begin();
		Tile.tiles[this.paintTexture].render(var33, this.level, 0, -2, 0, 0);
		var33.end();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		reportGLError("GUI: Draw selected");
		this.font.drawShadow("0.0.12a_03", 2, 2, 16777215);
		this.font.drawShadow(this.fpsString, 2, 12, 16777215);
		reportGLError("GUI: Draw text");
		int var34 = var27 / 2;
		int var9 = var26 / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		var33.begin();
		var33.vertex((float)(var34 + 1), (float)(var9 - 4), 0.0F);
		var33.vertex((float)var34, (float)(var9 - 4), 0.0F);
		var33.vertex((float)var34, (float)(var9 + 5), 0.0F);
		var33.vertex((float)(var34 + 1), (float)(var9 + 5), 0.0F);
		var33.vertex((float)(var34 + 5), (float)var9, 0.0F);
		var33.vertex((float)(var34 - 4), (float)var9, 0.0F);
		var33.vertex((float)(var34 - 4), (float)(var9 + 1), 0.0F);
		var33.vertex((float)(var34 + 5), (float)(var9 + 1), 0.0F);
		var33.end();
		reportGLError("GUI: Draw crosshair");
		reportGLError("Rendered gui");
		Display.update();
	}
	
	private void setupFog(int var1) {
		Tile var2 = Tile.tiles[this.level.getTile((int)this.thePlayer.x, (int)this.thePlayer.y, (int)this.thePlayer.z)];
		if(var2 != null && var2.getLiquidType() == 1) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
			GL11.glFog(GL11.GL_FOG_COLOR, this.getBuffer(0.02F, 0.02F, 0.2F, 1.0F));
//			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, this.getBuffer(0.3F, 0.3F, 0.5F, 1.0F));
		} else if(var2 != null && var2.getLiquidType() == 2) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.2F);
			GL11.glFog(GL11.GL_FOG_COLOR, this.getBuffer(0.5F, 0.3F, 0.0F, 1.0F));
//			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, this.getBuffer(0.4F, 0.3F, 0.3F, 1.0F));
		} else if(var1 == 0) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.001F);
			GL11.glFog(GL11.GL_FOG_COLOR, this.fogColor0);
//			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, this.getBuffer(1.0F, 1.0F, 1.0F, 1.0F));
		} else if(var1 == 1) {
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
			GL11.glFogf(GL11.GL_FOG_DENSITY, 0.05F);
			GL11.glFog(GL11.GL_FOG_COLOR, this.fogColor1);
			float var3 = 0.6F;
//			GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, this.getBuffer(var3, var3, var3, 1.0F));
		}

//		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
//		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	private FloatBuffer getBuffer(float a, float b, float c, float d) {
		this.lb.clear();
		this.lb.put(a).put(b).put(c).put(d);
		this.lb.flip();
		return this.lb;
	}

	public final void showLoadingScreen(String var1, String var2) {
		int var3 = this.width * 240 / this.height;
		int var4 = this.height * 240 / this.height;
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		Tesselator var5 = Tesselator.tesselator;
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		int var6 = this.textureManager.loadTexture("/dirt.png", 9728);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, var6);
		var5.begin();
		var5.color(8421504);
		float var8 = 32.0F;
		var5.vertexUV(0.0F, (float)var4, 0.0F, 0.0F, (float)var4 / var8);
		var5.vertexUV((float)var3, (float)var4, 0.0F, (float)var3 / var8, (float)var4 / var8);
		var5.vertexUV((float)var3, 0.0F, 0.0F, (float)var3 / var8, 0.0F);
		var5.vertexUV(0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		var5.end();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.font.drawShadow(var1, (var3 - this.font.getWidth(var1)) / 2, var4 / 2 - 4 - 8, 16777215);
		this.font.drawShadow(var2, (var3 - this.font.getWidth(var2)) / 2, var4 / 2 - 4 + 4, 16777215);
		Display.update();

		try {
			EagUtils.sleep(200L);
		} catch (Exception var7) {
			var7.printStackTrace();
		}
	}
}
