package com.mojang.minecraft.character;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.renderer.Textures;
import org.lwjgl.opengl.GL11;

public final class Zombie extends Entity {
	private float rot;
	private float timeOffset;
	private float speed;
	private float rotA;
	private static ZombieModel zombieModel = new ZombieModel();
	private Textures textureManager;

	public Zombie(Level var1, Textures var2, float var3, float var4, float var5) {
		super(var1);
		this.textureManager = var2;
		this.rotA = (float)(Math.random() + 1.0D) * 0.01F;
		this.setPos(var3, var4, var5);
		this.timeOffset = (float)Math.random() * 1239813.0F;
		this.rot = (float)(Math.random() * Math.PI * 2.0D);
		this.speed = 1.0F;
	}

	public final void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		float var1 = 0.0F;
		float var2 = 0.0F;
		if(this.y < -100.0F) {
			super.removed = true;
		}

		this.rot += this.rotA;
		this.rotA = (float)((double)this.rotA * 0.99D);
		this.rotA = (float)((double)this.rotA + (Math.random() - Math.random()) * Math.random() * Math.random() * (double)0.08F);
		var1 = (float)Math.sin((double)this.rot);
		var2 = (float)Math.cos((double)this.rot);
		if(this.onGround && Math.random() < 0.08D) {
			this.yd = 0.5F;
		}

		this.moveRelative(var1, var2, this.onGround ? 0.1F : 0.02F);
		this.yd = (float)((double)this.yd - 0.08D);
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.91F;
		this.yd *= 0.98F;
		this.zd *= 0.91F;
		if(this.onGround) {
			this.xd *= 0.7F;
			this.zd *= 0.7F;
		}

	}

	public final void render(float var1) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureManager.loadTexture("/char.png", GL11.GL_NEAREST));
		GL11.glPushMatrix();
		double var2 = (double)System.nanoTime() / 1.0E9D * 10.0D * (double)this.speed + (double)this.timeOffset;
		float var4 = 0.058333334F;
		float var5 = (float)(-Math.abs(Math.sin(var2 * 0.6662D)) * 5.0D - 23.0D);
		GL11.glTranslatef(this.xo + (this.x - this.xo) * var1, this.yo + (this.y - this.yo) * var1, this.zo + (this.z - this.zo) * var1);
		GL11.glScalef(1.0F, -1.0F, 1.0F);
		GL11.glScalef(var4, var4, var4);
		GL11.glTranslatef(0.0F, var5, 0.0F);
		var1 = 57.29578F;
		GL11.glRotatef(this.rot * var1 + 180.0F, 0.0F, 1.0F, 0.0F);
		var1 = (float)var2;
		ZombieModel var6 = zombieModel;
		var6.head.yRot = (float)Math.sin((double)var1 * 0.83D);
		var6.head.xRot = (float)Math.sin((double)var1) * 0.8F;
		var6.arm0.xRot = (float)Math.sin((double)var1 * 0.6662D + Math.PI) * 2.0F;
		var6.arm0.zRot = (float)(Math.sin((double)var1 * 0.2312D) + 1.0D);
		var6.arm1.xRot = (float)Math.sin((double)var1 * 0.6662D) * 2.0F;
		var6.arm1.zRot = (float)(Math.sin((double)var1 * 0.2812D) - 1.0D);
		var6.leg0.xRot = (float)Math.sin((double)var1 * 0.6662D) * 1.4F;
		var6.leg1.xRot = (float)Math.sin((double)var1 * 0.6662D + Math.PI) * 1.4F;
		var6.head.render();
		var6.body.render();
		var6.arm0.render();
		var6.arm1.render();
		var6.leg0.render();
		var6.leg1.render();
		GL11.glPopMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
}
