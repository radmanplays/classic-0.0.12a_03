package com.mojang.minecraft.level;

import java.util.Random;

public final class NoiseMap {
	private Random random;
	private int levels = 0;
	private int fuzz = 16;
	private boolean flag;

	public NoiseMap(Random var1, int var2, boolean var3) {
		this.random = var1;
		this.levels = var2;
		this.flag = var3;
	}

	public final int[] read(int var1, int var2) {
		int[] var3 = new int[var1 * var2];
		int var4 = this.levels;
		int var5 = var1 >> var4;

		int var6;
		int var7;
		for(var6 = 0; var6 < var2; var6 += var5) {
			for(var7 = 0; var7 < var1; var7 += var5) {
				var3[var7 + var6 * var1] = (this.random.nextInt(256) - 128) * this.fuzz;
				if(this.flag) {
					if(var7 != 0 && var6 != 0) {
						boolean var8 = false;
						var3[var7 + var6 * var1] = (this.random.nextInt(192) - 64) * this.fuzz;
					} else {
						var3[var7 + var6 * var1] = 0;
					}
				}
			}
		}

		for(var5 = var1 >> var4; var5 > 1; var5 /= 2) {
			var6 = 256 * (var5 << var4);
			var7 = var5 / 2;

			int var9;
			int var10;
			int var11;
			int var12;
			int var13;
			int var14;
			int var17;
			for(var17 = 0; var17 < var2; var17 += var5) {
				for(var9 = 0; var9 < var1; var9 += var5) {
					var10 = var3[var9 % var1 + var17 % var2 * var1];
					var11 = var3[(var9 + var5) % var1 + var17 % var2 * var1];
					var12 = var3[var9 % var1 + (var17 + var5) % var2 * var1];
					var13 = var3[(var9 + var5) % var1 + (var17 + var5) % var2 * var1];
					var14 = (var10 + var12 + var11 + var13) / 4 + this.random.nextInt(var6 << 1) - var6;
					var3[var9 + var7 + (var17 + var7) * var1] = var14;
					if(this.flag && (var9 == 0 || var17 == 0)) {
						var3[var9 + var17 * var1] = 0;
					}
				}
			}

			for(var17 = 0; var17 < var2; var17 += var5) {
				for(var9 = 0; var9 < var1; var9 += var5) {
					var10 = var3[var9 + var17 * var1];
					var11 = var3[(var9 + var5) % var1 + var17 * var1];
					var12 = var3[var9 + (var17 + var5) % var1 * var1];
					var13 = var3[(var9 + var7 & var1 - 1) + (var17 + var7 - var5 & var2 - 1) * var1];
					var14 = var3[(var9 + var7 - var5 & var1 - 1) + (var17 + var7 & var2 - 1) * var1];
					int var15 = var3[(var9 + var7) % var1 + (var17 + var7) % var2 * var1];
					var11 = (var10 + var11 + var15 + var13) / 4 + this.random.nextInt(var6 << 1) - var6;
					var10 = (var10 + var12 + var15 + var14) / 4 + this.random.nextInt(var6 << 1) - var6;
					var3[var9 + var7 + var17 * var1] = var11;
					var3[var9 + (var17 + var7) * var1] = var10;
				}
			}
		}

		int[] var16 = new int[var1 * var2];

		for(var6 = 0; var6 < var2; ++var6) {
			for(var7 = 0; var7 < var1; ++var7) {
				var16[var7 + var6 * var1] = var3[var7 % var1 + var6 % var2 * var1] / 512 + 128;
			}
		}

		return var16;
	}
}
