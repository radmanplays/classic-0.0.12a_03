package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleEngine;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Tesselator;
import java.util.Random;

public class Tile {
	public static final Tile[] tiles = new Tile[256];
	public static final boolean[] shouldTick = new boolean[256];
	public static final Tile rock = new Tile(1, 1);
	public static final Tile grass = new GrassTile(2);
	public static final Tile dirt = new DirtTile(3, 2);
	public static final Tile bedrock;
	public static final Tile water;
	public static final Tile calmWater;
	public static final Tile lava;
	public static final Tile calmLava;
	public int textureIndex;
	public final int id;
	private float x0;
	private float y0;
	private float z0;
	private float x1;
	private float y1;
	private float z1;

	protected Tile(int var1) {
		tiles[var1] = this;
		this.id = var1;
		this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	protected final void setTicking(boolean var1) {
		shouldTick[this.id] = var1;
	}

	protected final void setShape(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.x0 = 0.0F;
		this.y0 = var2;
		this.z0 = 0.0F;
		this.x1 = 1.0F;
		this.y1 = var5;
		this.z1 = 1.0F;
	}

	protected Tile(int var1, int var2) {
		this(var1);
		this.textureIndex = var2;
	}

	public void render(Tesselator t, Level level, int layer, int x, int y, int z) {
		float c1 = 1.0F;
		float c2 = 0.8F;
		float c3 = 0.6F;
		if(this.shouldRenderFace(level, x, y - 1, z, layer)) {
			t.color(c1, c1, c1);
			this.renderFace(t, x, y, z, 0);
		}

		if(this.shouldRenderFace(level, x, y + 1, z, layer)) {
			t.color(c1, c1, c1);
			this.renderFace(t, x, y, z, 1);
		}

		if(this.shouldRenderFace(level, x, y, z - 1, layer)) {
			t.color(c2, c2, c2);
			this.renderFace(t, x, y, z, 2);
		}

		if(this.shouldRenderFace(level, x, y, z + 1, layer)) {
			t.color(c2, c2, c2);
			this.renderFace(t, x, y, z, 3);
		}

		if(this.shouldRenderFace(level, x - 1, y, z, layer)) {
			t.color(c3, c3, c3);
			this.renderFace(t, x, y, z, 4);
		}

		if(this.shouldRenderFace(level, x + 1, y, z, layer)) {
			t.color(c3, c3, c3);
			this.renderFace(t, x, y, z, 5);
		}

	}

	protected boolean shouldRenderFace(Level var1, int var2, int var3, int var4, int var5) {
		if(var2 >= 0 && var3 >= 0 && var4 >= 0 && var2 < var1.width && var3 < var1.depth && var4 < var1.height) {
			boolean var6 = true;
			if(var5 == 2) {
				return false;
			} else {
				if(var5 >= 0) {
					var6 = var1.isLit(var2, var3, var4) ^ var5 == 1;
				}

				Tile var7 = tiles[var1.getTile(var2, var3, var4)];
				return !(var7 == null ? false : var7.isSolid()) && var6;
			}
		} else {
			return false;
		}
	}

	protected int getTexture(int var1) {
		return this.textureIndex;
	}

	public void renderFace(Tesselator var1, int var2, int var3, int var4, int var5) {
		int var6 = this.getTexture(var5);
		float var7 = (float)(var6 % 16) / 16.0F;
		float var8 = var7 + 0.999F / 16.0F;
		float var16 = (float)(var6 / 16) / 16.0F;
		float var9 = var16 + 0.999F / 16.0F;
		float var10 = (float)var2 + this.x0;
		float var14 = (float)var2 + this.x1;
		float var11 = (float)var3 + this.y0;
		float var15 = (float)var3 + this.y1;
		float var12 = (float)var4 + this.z0;
		float var13 = (float)var4 + this.z1;
		if(var5 == 0) {
			var1.vertexUV(var10, var11, var13, var7, var9);
			var1.vertexUV(var10, var11, var12, var7, var16);
			var1.vertexUV(var14, var11, var12, var8, var16);
			var1.vertexUV(var14, var11, var13, var8, var9);
		}

		if(var5 == 1) {
			var1.vertexUV(var14, var15, var13, var8, var9);
			var1.vertexUV(var14, var15, var12, var8, var16);
			var1.vertexUV(var10, var15, var12, var7, var16);
			var1.vertexUV(var10, var15, var13, var7, var9);
		}

		if(var5 == 2) {
			var1.vertexUV(var10, var15, var12, var8, var16);
			var1.vertexUV(var14, var15, var12, var7, var16);
			var1.vertexUV(var14, var11, var12, var7, var9);
			var1.vertexUV(var10, var11, var12, var8, var9);
		}

		if(var5 == 3) {
			var1.vertexUV(var10, var15, var13, var7, var16);
			var1.vertexUV(var10, var11, var13, var7, var9);
			var1.vertexUV(var14, var11, var13, var8, var9);
			var1.vertexUV(var14, var15, var13, var8, var16);
		}

		if(var5 == 4) {
			var1.vertexUV(var10, var15, var13, var8, var16);
			var1.vertexUV(var10, var15, var12, var7, var16);
			var1.vertexUV(var10, var11, var12, var7, var9);
			var1.vertexUV(var10, var11, var13, var8, var9);
		}

		if(var5 == 5) {
			var1.vertexUV(var14, var11, var13, var7, var9);
			var1.vertexUV(var14, var11, var12, var8, var9);
			var1.vertexUV(var14, var15, var12, var8, var16);
			var1.vertexUV(var14, var15, var13, var7, var16);
		}

	}

	public final void renderBackFace(Tesselator var1, int var2, int var3, int var4, int var5) {
		int var6 = this.getTexture(var5);
		float var7 = (float)(var6 % 16) / 16.0F;
		float var8 = var7 + 0.999F / 16.0F;
		float var16 = (float)(var6 / 16) / 16.0F;
		float var9 = var16 + 0.999F / 16.0F;
		float var10 = (float)var2 + this.x0;
		float var14 = (float)var2 + this.x1;
		float var11 = (float)var3 + this.y0;
		float var15 = (float)var3 + this.y1;
		float var12 = (float)var4 + this.z0;
		float var13 = (float)var4 + this.z1;
		if(var5 == 0) {
			var1.vertexUV(var14, var11, var13, var8, var9);
			var1.vertexUV(var14, var11, var12, var8, var16);
			var1.vertexUV(var10, var11, var12, var7, var16);
			var1.vertexUV(var10, var11, var13, var7, var9);
		}

		if(var5 == 1) {
			var1.vertexUV(var10, var15, var13, var7, var9);
			var1.vertexUV(var10, var15, var12, var7, var16);
			var1.vertexUV(var14, var15, var12, var8, var16);
			var1.vertexUV(var14, var15, var13, var8, var9);
		}

		if(var5 == 2) {
			var1.vertexUV(var10, var11, var12, var8, var9);
			var1.vertexUV(var14, var11, var12, var7, var9);
			var1.vertexUV(var14, var15, var12, var7, var16);
			var1.vertexUV(var10, var15, var12, var8, var16);
		}

		if(var5 == 3) {
			var1.vertexUV(var14, var15, var13, var8, var16);
			var1.vertexUV(var14, var11, var13, var8, var9);
			var1.vertexUV(var10, var11, var13, var7, var9);
			var1.vertexUV(var10, var15, var13, var7, var16);
		}

		if(var5 == 4) {
			var1.vertexUV(var10, var11, var13, var8, var9);
			var1.vertexUV(var10, var11, var12, var7, var9);
			var1.vertexUV(var10, var15, var12, var7, var16);
			var1.vertexUV(var10, var15, var13, var8, var16);
		}

		if(var5 == 5) {
			var1.vertexUV(var14, var15, var13, var7, var16);
			var1.vertexUV(var14, var15, var12, var8, var16);
			var1.vertexUV(var14, var11, var12, var8, var9);
			var1.vertexUV(var14, var11, var13, var7, var9);
		}

	}

	public static void renderFaceNoTexture(Player var0, Tesselator var1, int var2, int var3, int var4, int var5) {
		float var6 = (float)var2 - 0.001F;
		float var7 = (float)var2 + 1.001F;
		float var8 = (float)var3 - 0.001F;
		float var9 = (float)var3 + 1.001F;
		float var10 = (float)var4 - 0.001F;
		float var11 = (float)var4 + 1.001F;
		if(var5 == 0 && (float)var3 > var0.y) {
			var1.vertex(var6, var8, var11);
			var1.vertex(var6, var8, var10);
			var1.vertex(var7, var8, var10);
			var1.vertex(var7, var8, var11);
		}

		if(var5 == 1 && (float)var3 < var0.y) {
			var1.vertex(var7, var9, var11);
			var1.vertex(var7, var9, var10);
			var1.vertex(var6, var9, var10);
			var1.vertex(var6, var9, var11);
		}

		if(var5 == 2 && (float)var4 > var0.z) {
			var1.vertex(var6, var9, var10);
			var1.vertex(var7, var9, var10);
			var1.vertex(var7, var8, var10);
			var1.vertex(var6, var8, var10);
		}

		if(var5 == 3 && (float)var4 < var0.z) {
			var1.vertex(var6, var9, var11);
			var1.vertex(var6, var8, var11);
			var1.vertex(var7, var8, var11);
			var1.vertex(var7, var9, var11);
		}

		if(var5 == 4 && (float)var2 > var0.x) {
			var1.vertex(var6, var9, var11);
			var1.vertex(var6, var9, var10);
			var1.vertex(var6, var8, var10);
			var1.vertex(var6, var8, var11);
		}

		if(var5 == 5 && (float)var2 < var0.x) {
			var1.vertex(var7, var8, var11);
			var1.vertex(var7, var8, var10);
			var1.vertex(var7, var9, var10);
			var1.vertex(var7, var9, var11);
		}

	}

	public static AABB getBlockBoundingBox(int var0, int var1, int var2) {
		return new AABB((float)var0, (float)var1, (float)var2, (float)(var0 + 1), (float)(var1 + 1), (float)(var2 + 1));
	}

	public AABB getBoundingBox(int var1, int var2, int var3) {
		return new AABB((float)var1, (float)var2, (float)var3, (float)(var1 + 1), (float)(var2 + 1), (float)(var3 + 1));
	}

	public boolean blocksLight() {
		return true;
	}

	public boolean isSolid() {
		return true;
	}

	public boolean mayPick() {
		return true;
	}

	public void tick(Level var1, int var2, int var3, int var4, Random var5) {
	}

	public final void destroy(Level var1, int var2, int var3, int var4, ParticleEngine var5) {
		for(int var6 = 0; var6 < 4; ++var6) {
			for(int var7 = 0; var7 < 4; ++var7) {
				for(int var8 = 0; var8 < 4; ++var8) {
					float var9 = (float)var2 + ((float)var6 + 0.5F) / (float)4;
					float var10 = (float)var3 + ((float)var7 + 0.5F) / (float)4;
					float var11 = (float)var4 + ((float)var8 + 0.5F) / (float)4;
					Particle var12 = new Particle(var1, var9, var10, var11, var9 - (float)var2 - 0.5F, var10 - (float)var3 - 0.5F, var11 - (float)var4 - 0.5F, this.textureIndex);
					var5.particles.add(var12);
				}
			}
		}

	}

	public int getLiquidType() {
		return 0;
	}

	public void neighborChanged(Level var1, int var2, int var3, int var4, int var5) {
	}

	static {
		new Tile(4, 16);
		new Tile(5, 4);
		new Bush(6);
		bedrock = new Tile(7, 17);
		water = new LiquidTile(8, 1);
		calmWater = new CalmLiquidTile(9, 1);
		lava = new LiquidTile(10, 2);
		calmLava = new CalmLiquidTile(11, 2);
	}
}
