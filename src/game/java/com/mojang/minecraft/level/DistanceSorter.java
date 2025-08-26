package com.mojang.minecraft.level;

import com.mojang.minecraft.Player;
import java.util.Comparator;

public final class DistanceSorter implements Comparator {
	private Player thePlayer;

	public DistanceSorter(Player var1) {
		this.thePlayer = var1;
	}

	public final int compare(Object var1, Object var2) {
		Chunk var10001 = (Chunk)var1;
		Chunk var4 = (Chunk)var2;
		Chunk var3 = var10001;
		return var3.distanceToSqr(this.thePlayer) < var4.distanceToSqr(this.thePlayer) ? -1 : 1;
	}
}
