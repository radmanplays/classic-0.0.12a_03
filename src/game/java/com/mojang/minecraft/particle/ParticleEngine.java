package com.mojang.minecraft.particle;

import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public final class ParticleEngine {
	public List particles = new ArrayList();
	private Textures textureManager;

	public ParticleEngine(Level var1, Textures var2) {
		this.textureManager = var2;
	}

	public final void render(Player var1, float var2, int var3) {
		if(this.particles.size() != 0) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			int var4 = this.textureManager.loadTexture("/terrain.png", 9728);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, var4);
			float var26 = -((float)Math.cos((double)var1.yaw * Math.PI / 180.0D));
			float var5 = -((float)Math.sin((double)var1.yaw * Math.PI / 180.0D));
			float var6 = -var5 * (float)Math.sin((double)var1.pitch * Math.PI / 180.0D);
			float var7 = var26 * (float)Math.sin((double)var1.pitch * Math.PI / 180.0D);
			float var25 = (float)Math.cos((double)var1.pitch * Math.PI / 180.0D);
			Tesselator var8 = Tesselator.tesselator;
			GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
			var8.begin();

			for(int var9 = 0; var9 < this.particles.size(); ++var9) {
				Particle var10 = (Particle)this.particles.get(var9);
				if(var10.isLit() ^ var3 == 1) {
					float var18 = ((float)(var10.tex % 16) + var10.u / 4.0F) / 16.0F;
					float var19 = var18 + 0.999F / 64.0F;
					float var20 = ((float)(var10.tex / 16) + var10.v / 4.0F) / 16.0F;
					float var21 = var20 + 0.999F / 64.0F;
					float var22 = 0.1F * var10.size;
					float var23 = var10.xo + (var10.x - var10.xo) * var2;
					float var24 = var10.yo + (var10.y - var10.yo) * var2;
					float var27 = var10.zo + (var10.z - var10.zo) * var2;
					var8.vertexUV(var23 - var26 * var22 - var6 * var22, var24 - var25 * var22, var27 - var5 * var22 - var7 * var22, var18, var21);
					var8.vertexUV(var23 - var26 * var22 + var6 * var22, var24 + var25 * var22, var27 - var5 * var22 + var7 * var22, var18, var20);
					var8.vertexUV(var23 + var26 * var22 + var6 * var22, var24 + var25 * var22, var27 + var5 * var22 + var7 * var22, var19, var20);
					var8.vertexUV(var23 + var26 * var22 - var6 * var22, var24 - var25 * var22, var27 + var5 * var22 - var7 * var22, var19, var21);
				}
			}

			var8.end();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
}
