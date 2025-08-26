package com.mojang.minecraft.level;

import com.mojang.minecraft.Player;
import java.util.Comparator;

public final class DirtyChunkSorter implements Comparator {
	private Player thePlayer;

	public DirtyChunkSorter(Player var1) {
		this.thePlayer = var1;
	}

	public final int compare(Object var1, Object var2) {
		Chunk var10001 = (Chunk)var1;
		Chunk var6 = (Chunk)var2;
		Chunk var5 = var10001;
		boolean var3 = var5.visible;
		boolean var4 = var6.visible;
		return var3 && !var4 ? -1 : ((!var4 || var3) && var5.distanceToSqr(this.thePlayer) < var6.distanceToSqr(this.thePlayer) ? -1 : 1);
	}
}
