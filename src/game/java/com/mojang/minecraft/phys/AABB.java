package com.mojang.minecraft.phys;

public final class AABB {
	private float epsilon = 0.0F;
	public float x0;
	public float y0;
	public float z0;
	public float x1;
	public float y1;
	public float z1;

	public AABB(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.x0 = var1;
		this.y0 = var2;
		this.z0 = var3;
		this.x1 = var4;
		this.y1 = var5;
		this.z1 = var6;
	}

	public final boolean intersects(AABB var1) {
		return var1.x1 > this.x0 && var1.x0 < this.x1 ? (var1.y1 > this.y0 && var1.y0 < this.y1 ? var1.z1 > this.z0 && var1.z0 < this.z1 : false) : false;
	}

	public final void move(float var1, float var2, float var3) {
		this.x0 += var1;
		this.y0 += var2;
		this.z0 += var3;
		this.x1 += var1;
		this.y1 += var2;
		this.z1 += var3;
	}
}
