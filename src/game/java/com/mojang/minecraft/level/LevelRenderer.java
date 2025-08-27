package com.mojang.minecraft.level;

import com.mojang.minecraft.HitResult;
import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import java.util.Arrays;
import org.lwjgl.opengl.GL11;

public final class LevelRenderer {
	public Level level;
	public Chunk[] chunks;
	private Chunk[] sortedChunks;
	private int chunkX;
	private int chunkY;
	private int chunkZ;
	private Textures textureManager;
	public int surroundLists;
	public int drawDistance = 0;
	private float X = 0.0F;
	private float Y = 0.0F;
	private float Z = 0.0F;

	public LevelRenderer(Level var1, Textures var2) {
		this.level = var1;
		this.textureManager = var2;
		var1.levelListeners.add(this);
		this.chunkX = (var1.width + 16 - 1) / 16;
		this.chunkY = (var1.depth + 16 - 1) / 16;
		this.chunkZ = (var1.height + 16 - 1) / 16;
		this.chunks = new Chunk[this.chunkX * this.chunkY * this.chunkZ];
		this.sortedChunks = new Chunk[this.chunkX * this.chunkY * this.chunkZ];

		for(int var11 = 0; var11 < this.chunkX; ++var11) {
			for(int var3 = 0; var3 < this.chunkY; ++var3) {
				for(int var4 = 0; var4 < this.chunkZ; ++var4) {
					int var5 = var11 << 4;
					int var6 = var3 << 4;
					int var7 = var4 << 4;
					int var8 = var11 + 1 << 4;
					int var9 = var3 + 1 << 4;
					int var10 = var4 + 1 << 4;
					if(var8 > var1.width) {
						var8 = var1.width;
					}

					if(var9 > var1.depth) {
						var9 = var1.depth;
					}

					if(var10 > var1.height) {
						var10 = var1.height;
					}

					this.chunks[(var11 + var3 * this.chunkX) * this.chunkZ + var4] = new Chunk(var1, var5, var6, var7, var8, var9, var10);
					this.sortedChunks[(var11 + var3 * this.chunkX) * this.chunkZ + var4] = this.chunks[(var11 + var3 * this.chunkX) * this.chunkZ + var4];
				}
			}
		}
	}

	public final void render(Player var1, int var2) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureManager.loadTexture("/terrain.png", GL11.GL_NEAREST));
		float var3 = var1.x - this.X;
		float var4 = var1.y - this.Y;
		float var5 = var1.z - this.Z;
		if(var3 * var3 + var4 * var4 + var5 * var5 > 64.0F) {
			this.X = var1.x;
			this.Y = var1.y;
			this.Z = var1.z;
			Arrays.sort(this.sortedChunks, new DistanceSorter(var1));
		}

		for(int var6 = 0; var6 < this.sortedChunks.length; ++var6) {
			if(this.sortedChunks[var6].visible) {
				var4 = (float)(256 / (1 << this.drawDistance));
				if(this.drawDistance == 0 || this.sortedChunks[var6].distanceToSqr(var1) < var4 * var4) {
					this.sortedChunks[var6].render(var2);
				}
			}
		}

		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public void compileSurroundingGround() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureManager.loadTexture("/rock.png", GL11.GL_NEAREST));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Tesselator var1 = Tesselator.tesselator;
		float var2 = 32.0F - 2.0F;
		var1.begin();

		int var3;
		for(var3 = -640; var3 < this.level.width + 640; var3 += 128) {
			for(int var4 = -640; var4 < this.level.height + 640; var4 += 128) {
				float var5 = var2;
				if(var3 >= 0 && var4 >= 0 && var3 < this.level.width && var4 < this.level.height) {
					var5 = 0.0F;
				}

				var1.vertexUV((float)var3, var5, (float)(var4 + 128), 0.0F, (float)128);
				var1.vertexUV((float)(var3 + 128), var5, (float)(var4 + 128), (float)128, (float)128);
				var1.vertexUV((float)(var3 + 128), var5, (float)var4, (float)128, 0.0F);
				var1.vertexUV((float)var3, var5, (float)var4, 0.0F, 0.0F);
			}
		}

		var1.end();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureManager.loadTexture("/rock.png", GL11.GL_NEAREST));
		GL11.glColor3f(0.8F, 0.8F, 0.8F);
		var1.begin();

		for(var3 = 0; var3 < this.level.width; var3 += 128) {
			var1.vertexUV((float)var3, 0.0F, 0.0F, 0.0F, 0.0F);
			var1.vertexUV((float)(var3 + 128), 0.0F, 0.0F, (float)128, 0.0F);
			var1.vertexUV((float)(var3 + 128), var2, 0.0F, (float)128, var2);
			var1.vertexUV((float)var3, var2, 0.0F, 0.0F, var2);
			var1.vertexUV((float)var3, var2, (float)this.level.height, 0.0F, var2);
			var1.vertexUV((float)(var3 + 128), var2, (float)this.level.height, (float)128, var2);
			var1.vertexUV((float)(var3 + 128), 0.0F, (float)this.level.height, (float)128, 0.0F);
			var1.vertexUV((float)var3, 0.0F, (float)this.level.height, 0.0F, 0.0F);
		}

		GL11.glColor3f(0.6F, 0.6F, 0.6F);

		for(var3 = 0; var3 < this.level.height; var3 += 128) {
			var1.vertexUV(0.0F, var2, (float)var3, 0.0F, 0.0F);
			var1.vertexUV(0.0F, var2, (float)(var3 + 128), (float)128, 0.0F);
			var1.vertexUV(0.0F, 0.0F, (float)(var3 + 128), (float)128, var2);
			var1.vertexUV(0.0F, 0.0F, (float)var3, 0.0F, var2);
			var1.vertexUV((float)this.level.width, 0.0F, (float)var3, 0.0F, var2);
			var1.vertexUV((float)this.level.width, 0.0F, (float)(var3 + 128), (float)128, var2);
			var1.vertexUV((float)this.level.width, var2, (float)(var3 + 128), (float)128, 0.0F);
			var1.vertexUV((float)this.level.width, var2, (float)var3, 0.0F, 0.0F);
		}

		var1.end();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public void compileSurroundingWater() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1.0F, 1.0F, 1.0F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureManager.loadTexture("/water.png", GL11.GL_NEAREST));
		float var1 = 32.0F;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tesselator var2 = Tesselator.tesselator;
		var2.begin();

		for(int var3 = -640; var3 < this.level.width + 640; var3 += 128) {
			for(int var4 = -640; var4 < this.level.height + 640; var4 += 128) {
				float var5 = var1 - 0.1F;
				if(var3 < 0 || var4 < 0 || var3 >= this.level.width || var4 >= this.level.height) {
					var2.vertexUV((float)var3, var5, (float)(var4 + 128), 0.0F, (float)128);
					var2.vertexUV((float)(var3 + 128), var5, (float)(var4 + 128), (float)128, (float)128);
					var2.vertexUV((float)(var3 + 128), var5, (float)var4, (float)128, 0.0F);
					var2.vertexUV((float)var3, var5, (float)var4, 0.0F, 0.0F);
					var2.vertexUV((float)var3, var5, (float)var4, 0.0F, 0.0F);
					var2.vertexUV((float)(var3 + 128), var5, (float)var4, (float)128, 0.0F);
					var2.vertexUV((float)(var3 + 128), var5, (float)(var4 + 128), (float)128, (float)128);
					var2.vertexUV((float)var3, var5, (float)(var4 + 128), 0.0F, (float)128);
				}
			}
		}

		var2.end();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	public void renderHit(Player var1, HitResult h, int mode, int tileType) {
		Tesselator t = Tesselator.tesselator;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, ((float)Math.sin((double)System.currentTimeMillis() / 100.0D) * 0.2F + 0.4F) * 0.5F);
		if(mode == 0) {
			t.begin();

			for(int br = 0; br < 6; ++br) {
				Tile.renderFaceNoTexture(var1, t, h.x, h.y, h.z, br);
			}

			t.end();
		} else {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float var10 = (float)Math.sin((double)System.currentTimeMillis() / 100.0D) * 0.2F + 0.8F;
			GL11.glColor4f(var10, var10, var10, (float)Math.sin((double)System.currentTimeMillis() / 200.0D) * 0.2F + 0.5F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			int id = this.textureManager.loadTexture("/terrain.png", 9728);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			int x = h.x;
			int y = h.y;
			int z = h.z;
			if(h.f == 0) {
				--y;
			}

			if(h.f == 1) {
				++y;
			}

			if(h.f == 2) {
				--z;
			}

			if(h.f == 3) {
				++z;
			}

			if(h.f == 4) {
				--x;
			}

			if(h.f == 5) {
				++x;
			}

			t.begin();
			t.noColor();
			Tile.tiles[tileType].render(t, this.level, 0, x, y, z);
			Tile.tiles[tileType].render(t, this.level, 1, x, y, z);
			t.end();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
	public final void setDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
		var1 /= 16;
		var4 /= 16;
		var2 /= 16;
		var5 /= 16;
		var3 /= 16;
		var6 /= 16;
		if(var1 < 0) {
			var1 = 0;
		}

		if(var2 < 0) {
			var2 = 0;
		}

		if(var3 < 0) {
			var3 = 0;
		}

		if(var4 >= this.chunkX) {
			var4 = this.chunkX - 1;
		}

		if(var5 >= this.chunkY) {
			var5 = this.chunkY - 1;
		}

		if(var6 >= this.chunkZ) {
			var6 = this.chunkZ - 1;
		}

		for(var1 = var1; var1 <= var4; ++var1) {
			for(int var7 = var2; var7 <= var5; ++var7) {
				for(int var8 = var3; var8 <= var6; ++var8) {
					this.chunks[(var1 + var7 * this.chunkX) * this.chunkZ + var8].setDirty();
				}
			}
		}

	}

	public final void resetChunks() {
		for(int var1 = 0; var1 < this.chunks.length; ++var1) {
			this.chunks[var1].reset();
		}

	}
}
