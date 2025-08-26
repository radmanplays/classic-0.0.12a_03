package com.mojang.minecraft.renderer;

import com.mojang.minecraft.phys.AABB;
import com.mojang.util.GLAllocation;

import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;

import org.lwjgl.opengl.GL11;

public final class Frustum {
	private float[][] m_Frustum = new float[6][4];
	private static Frustum frustum = new Frustum();
	private FloatBuffer projectionBuffer = GLAllocation.createFloatBuffer(16);
	private FloatBuffer modelBuffer = GLAllocation.createFloatBuffer(16);
	private FloatBuffer clipBuffer = GLAllocation.createFloatBuffer(16);
	private float[] projection = new float[16];
	private float[] model = new float[16];
	private float[] clip = new float[16];

	public static Frustum calculateFrustum() {
		Frustum var0 = frustum;
		var0.projectionBuffer.clear();
		var0.modelBuffer.clear();
		var0.clipBuffer.clear();
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, var0.projectionBuffer);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, var0.modelBuffer);
		var0.projectionBuffer.flip().limit(16);
		var0.projectionBuffer.get(var0.projection);
		var0.modelBuffer.flip().limit(16);
		var0.modelBuffer.get(var0.model);
		var0.clip[0] = var0.model[0] * var0.projection[0] + var0.model[1] * var0.projection[4] + var0.model[2] * var0.projection[8] + var0.model[3] * var0.projection[12];
		var0.clip[1] = var0.model[0] * var0.projection[1] + var0.model[1] * var0.projection[5] + var0.model[2] * var0.projection[9] + var0.model[3] * var0.projection[13];
		var0.clip[2] = var0.model[0] * var0.projection[2] + var0.model[1] * var0.projection[6] + var0.model[2] * var0.projection[10] + var0.model[3] * var0.projection[14];
		var0.clip[3] = var0.model[0] * var0.projection[3] + var0.model[1] * var0.projection[7] + var0.model[2] * var0.projection[11] + var0.model[3] * var0.projection[15];
		var0.clip[4] = var0.model[4] * var0.projection[0] + var0.model[5] * var0.projection[4] + var0.model[6] * var0.projection[8] + var0.model[7] * var0.projection[12];
		var0.clip[5] = var0.model[4] * var0.projection[1] + var0.model[5] * var0.projection[5] + var0.model[6] * var0.projection[9] + var0.model[7] * var0.projection[13];
		var0.clip[6] = var0.model[4] * var0.projection[2] + var0.model[5] * var0.projection[6] + var0.model[6] * var0.projection[10] + var0.model[7] * var0.projection[14];
		var0.clip[7] = var0.model[4] * var0.projection[3] + var0.model[5] * var0.projection[7] + var0.model[6] * var0.projection[11] + var0.model[7] * var0.projection[15];
		var0.clip[8] = var0.model[8] * var0.projection[0] + var0.model[9] * var0.projection[4] + var0.model[10] * var0.projection[8] + var0.model[11] * var0.projection[12];
		var0.clip[9] = var0.model[8] * var0.projection[1] + var0.model[9] * var0.projection[5] + var0.model[10] * var0.projection[9] + var0.model[11] * var0.projection[13];
		var0.clip[10] = var0.model[8] * var0.projection[2] + var0.model[9] * var0.projection[6] + var0.model[10] * var0.projection[10] + var0.model[11] * var0.projection[14];
		var0.clip[11] = var0.model[8] * var0.projection[3] + var0.model[9] * var0.projection[7] + var0.model[10] * var0.projection[11] + var0.model[11] * var0.projection[15];
		var0.clip[12] = var0.model[12] * var0.projection[0] + var0.model[13] * var0.projection[4] + var0.model[14] * var0.projection[8] + var0.model[15] * var0.projection[12];
		var0.clip[13] = var0.model[12] * var0.projection[1] + var0.model[13] * var0.projection[5] + var0.model[14] * var0.projection[9] + var0.model[15] * var0.projection[13];
		var0.clip[14] = var0.model[12] * var0.projection[2] + var0.model[13] * var0.projection[6] + var0.model[14] * var0.projection[10] + var0.model[15] * var0.projection[14];
		var0.clip[15] = var0.model[12] * var0.projection[3] + var0.model[13] * var0.projection[7] + var0.model[14] * var0.projection[11] + var0.model[15] * var0.projection[15];
		var0.m_Frustum[0][0] = var0.clip[3] - var0.clip[0];
		var0.m_Frustum[0][1] = var0.clip[7] - var0.clip[4];
		var0.m_Frustum[0][2] = var0.clip[11] - var0.clip[8];
		var0.m_Frustum[0][3] = var0.clip[15] - var0.clip[12];
		normalizePlane(var0.m_Frustum, 0);
		var0.m_Frustum[1][0] = var0.clip[3] + var0.clip[0];
		var0.m_Frustum[1][1] = var0.clip[7] + var0.clip[4];
		var0.m_Frustum[1][2] = var0.clip[11] + var0.clip[8];
		var0.m_Frustum[1][3] = var0.clip[15] + var0.clip[12];
		normalizePlane(var0.m_Frustum, 1);
		var0.m_Frustum[2][0] = var0.clip[3] + var0.clip[1];
		var0.m_Frustum[2][1] = var0.clip[7] + var0.clip[5];
		var0.m_Frustum[2][2] = var0.clip[11] + var0.clip[9];
		var0.m_Frustum[2][3] = var0.clip[15] + var0.clip[13];
		normalizePlane(var0.m_Frustum, 2);
		var0.m_Frustum[3][0] = var0.clip[3] - var0.clip[1];
		var0.m_Frustum[3][1] = var0.clip[7] - var0.clip[5];
		var0.m_Frustum[3][2] = var0.clip[11] - var0.clip[9];
		var0.m_Frustum[3][3] = var0.clip[15] - var0.clip[13];
		normalizePlane(var0.m_Frustum, 3);
		var0.m_Frustum[4][0] = var0.clip[3] - var0.clip[2];
		var0.m_Frustum[4][1] = var0.clip[7] - var0.clip[6];
		var0.m_Frustum[4][2] = var0.clip[11] - var0.clip[10];
		var0.m_Frustum[4][3] = var0.clip[15] - var0.clip[14];
		normalizePlane(var0.m_Frustum, 4);
		var0.m_Frustum[5][0] = var0.clip[3] + var0.clip[2];
		var0.m_Frustum[5][1] = var0.clip[7] + var0.clip[6];
		var0.m_Frustum[5][2] = var0.clip[11] + var0.clip[10];
		var0.m_Frustum[5][3] = var0.clip[15] + var0.clip[14];
		normalizePlane(var0.m_Frustum, 5);
		return frustum;
	}

	private static void normalizePlane(float[][] var0, int var1) {
		float var2 = (float)Math.sqrt((double)(var0[var1][0] * var0[var1][0] + var0[var1][1] * var0[var1][1] + var0[var1][2] * var0[var1][2]));
		var0[var1][0] /= var2;
		var0[var1][1] /= var2;
		var0[var1][2] /= var2;
		var0[var1][3] /= var2;
	}

	public final boolean isVisible(AABB var1) {
		float var6 = var1.z1;
		float var5 = var1.y1;
		float var4 = var1.x1;
		float var3 = var1.z0;
		float var2 = var1.y0;
		float var9 = var1.x0;
		Frustum var8 = this;

		for(int var7 = 0; var7 < 6; ++var7) {
			if(var8.m_Frustum[var7][0] * var9 + var8.m_Frustum[var7][1] * var2 + var8.m_Frustum[var7][2] * var3 + var8.m_Frustum[var7][3] <= 0.0F && var8.m_Frustum[var7][0] * var4 + var8.m_Frustum[var7][1] * var2 + var8.m_Frustum[var7][2] * var3 + var8.m_Frustum[var7][3] <= 0.0F && var8.m_Frustum[var7][0] * var9 + var8.m_Frustum[var7][1] * var5 + var8.m_Frustum[var7][2] * var3 + var8.m_Frustum[var7][3] <= 0.0F && var8.m_Frustum[var7][0] * var4 + var8.m_Frustum[var7][1] * var5 + var8.m_Frustum[var7][2] * var3 + var8.m_Frustum[var7][3] <= 0.0F && var8.m_Frustum[var7][0] * var9 + var8.m_Frustum[var7][1] * var2 + var8.m_Frustum[var7][2] * var6 + var8.m_Frustum[var7][3] <= 0.0F && var8.m_Frustum[var7][0] * var4 + var8.m_Frustum[var7][1] * var2 + var8.m_Frustum[var7][2] * var6 + var8.m_Frustum[var7][3] <= 0.0F && var8.m_Frustum[var7][0] * var9 + var8.m_Frustum[var7][1] * var5 + var8.m_Frustum[var7][2] * var6 + var8.m_Frustum[var7][3] <= 0.0F && var8.m_Frustum[var7][0] * var4 + var8.m_Frustum[var7][1] * var5 + var8.m_Frustum[var7][2] * var6 + var8.m_Frustum[var7][3] <= 0.0F) {
				return false;
			}
		}

		return true;
	}
}
