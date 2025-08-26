package com.mojang.minecraft.level;

import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Tesselator;
import org.lwjgl.opengl.GL11;

public final class Chunk {
	public AABB aabb;
	private Level level;
	private int x0;
	private int y0;
	private int z0;
	private int x1;
	private int y1;
	private int z1;
	private float x;
	private float y;
	private float z;
	private boolean dirty = true;
	private int lists = -1;
	public boolean visible;
	private static Tesselator t = Tesselator.tesselator;
	public static int updates = 0;
	private static long totalTime = 0L;
	private static int totalUpdates = 0;

	public Chunk(Level var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		this.level = var1;
		this.x0 = var2;
		this.y0 = var3;
		this.z0 = var4;
		this.x1 = var5;
		this.y1 = var6;
		this.z1 = var7;
		this.x = (float)(var2 + var5) / 2.0F;
		this.y = (float)(var3 + var6) / 2.0F;
		this.z = (float)(var4 + var7) / 2.0F;
		this.aabb = new AABB((float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7);
		this.lists = GL11.glGenLists(3);
	}

	private void rebuild(int var1) {
		long var2 = System.nanoTime();
		GL11.glNewList(this.lists + var1, GL11.GL_COMPILE);
		t.begin();
		int var4 = 0;

		for(int var5 = this.x0; var5 < this.x1; ++var5) {
			for(int var6 = this.y0; var6 < this.y1; ++var6) {
				for(int var7 = this.z0; var7 < this.z1; ++var7) {
					int var8 = this.level.getTile(var5, var6, var7);
					if(var8 > 0) {
						Tile.tiles[var8].render(t, this.level, var1, var5, var6, var7);
						++var4;
					}
				}
			}
		}

		t.end();
		GL11.glEndList();
		long var9 = System.nanoTime();
		if(var4 > 0) {
			totalTime += var9 - var2;
			++totalUpdates;
		}

	}

	public final void rebuild() {
		++updates;
		this.rebuild(0);
		this.rebuild(1);
		this.rebuild(2);
		this.dirty = false;
	}

	public final void render(int var1) {
		
	    if (var1 == 1) {
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glEnable(GL11.GL_ALPHA_TEST);
	        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
	    }
	    
		GL11.glCallList(this.lists + var1);
	}

	public final void setDirty() {
		if(!this.dirty) {
			System.currentTimeMillis();
		}

		this.dirty = true;
	}

	public final boolean isDirty() {
		return this.dirty;
	}

	public final float distanceToSqr(Player var1) {
		float var2 = var1.x - this.x;
		float var3 = var1.y - this.y;
		float var4 = var1.z - this.z;
		return var2 * var2 + var3 * var3 + var4 * var4;
	}

	public final void reset() {
		this.dirty = true;

		for(int var1 = 0; var1 < 3; ++var1) {
			GL11.glNewList(this.lists + var1, GL11.GL_COMPILE);
			GL11.glEndList();
		}

	}
}
