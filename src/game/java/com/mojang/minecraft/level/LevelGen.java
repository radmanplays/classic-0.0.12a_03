package com.mojang.minecraft.level;

import java.util.Random;

public final class LevelGen {
	int width;
	int height;
	int depth;
	Random random = new Random();

	public LevelGen(int var1, int var2, int var3) {
		this.width = var1;
		this.height = var2;
		this.depth = var3;
	}
}
