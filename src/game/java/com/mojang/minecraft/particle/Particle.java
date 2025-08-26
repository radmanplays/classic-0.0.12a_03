package com.mojang.minecraft.particle;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;

public final class Particle extends Entity {
	private float xxd;
	private float yyd;
	private float zzd;
	public int tex;
	float u;
	float v;
	private int age = 0;
	private int lifetime = 0;
	float size;

	public Particle(Level var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
		super(var1);
		this.tex = var8;
		this.setCollisionBoxSize(0.2F, 0.2F);
		this.heightOffset = this.collisionBoxHeight / 2.0F;
		this.setPos(var2, var3, var4);
		this.xxd = var5 + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
		this.yyd = var6 + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
		this.zzd = var7 + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
		float var9 = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
		var2 = (float)Math.sqrt((double)(this.xxd * this.xxd + this.yyd * this.yyd + this.zzd * this.zzd));
		this.xxd = this.xxd / var2 * var9 * 0.4F;
		this.yyd = this.yyd / var2 * var9 * 0.4F + 0.1F;
		this.zzd = this.zzd / var2 * var9 * 0.4F;
		this.u = (float)Math.random() * 3.0F;
		this.v = (float)Math.random() * 3.0F;
		this.size = (float)(Math.random() * 0.5D + 0.5D);
		this.lifetime = (int)(4.0D / (Math.random() * 0.9D + 0.1D));
		this.age = 0;
	}

	public final void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if(this.age++ >= this.lifetime) {
			super.removed = true;
		}

		this.yyd = (float)((double)this.yyd - 0.04D);
		this.move(this.xxd, this.yyd, this.zzd);
		this.xxd *= 0.98F;
		this.yyd *= 0.98F;
		this.zzd *= 0.98F;
		if(this.onGround) {
			this.xxd *= 0.7F;
			this.zzd *= 0.7F;
		}

	}
}
