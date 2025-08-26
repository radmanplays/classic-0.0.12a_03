package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Tesselator;
import java.util.Random;

public class LiquidTile extends Tile {
	protected int liquidType;
	protected int calmTileId;
	protected int tileId;
	private int spreadSpeed = 1;

	protected LiquidTile(int var1, int var2) {
		super(var1);
		this.liquidType = var2;
		this.textureIndex = 14;
		if(var2 == 2) {
			this.textureIndex = 30;
		}

		if(var2 == 1) {
			this.spreadSpeed = 8;
		}

		if(var2 == 2) {
			this.spreadSpeed = 2;
		}

		this.tileId = var1;
		this.calmTileId = var1 + 1;
		float var3 = 0.1F;
		this.setShape(0.0F, 0.0F - var3, 0.0F, 1.0F, 1.0F - var3, 1.0F);
		this.setTicking(true);
	}

	public void tick(Level var1, int var2, int var3, int var4, Random var5) {
		this.updateWater(var1, var2, var3, var4, 0);
	}

	private boolean updateWater(Level var1, int var2, int var3, int var4, int var5) {
		boolean var6 = false;

		boolean var7;
		do {
			--var3;
			if(var1.getTile(var2, var3, var4) != 0) {
				break;
			}

			var7 = var1.setTile(var2, var3, var4, this.tileId);
			if(var7) {
				var6 = true;
			}
		} while(var7 && this.liquidType != 2);

		++var3;
		if(this.liquidType == 1 || !var6) {
			var6 |= this.checkWater(var1, var2 - 1, var3, var4, var5);
			var6 |= this.checkWater(var1, var2 + 1, var3, var4, var5);
			var6 |= this.checkWater(var1, var2, var3, var4 - 1, var5);
			var6 |= this.checkWater(var1, var2, var3, var4 + 1, var5);
		}

		if(!var6) {
			var1.setTileNoUpdate(var2, var3, var4, this.calmTileId);
		}

		return var6;
	}

	private boolean checkWater(Level var1, int var2, int var3, int var4, int var5) {
		boolean var6 = false;
		int var7 = var1.getTile(var2, var3, var4);
		if(var7 == 0) {
			boolean var8 = var1.setTile(var2, var3, var4, this.tileId);
			if(var8 && var5 < this.spreadSpeed) {
				var6 = false | this.updateWater(var1, var2, var3, var4, var5 + 1);
			}
		}

		return var6;
	}

	protected final boolean shouldRenderFace(Level var1, int var2, int var3, int var4, int var5) {
		if(var5 != 2) {
			return false;
		} else {
			var5 = var1.getTile(var2, var3, var4);
			return var5 != this.tileId && var5 != this.calmTileId ? super.shouldRenderFace(var1, var2, var3, var4, -1) : false;
		}
	}

	public final void renderFace(Tesselator var1, int var2, int var3, int var4, int var5) {
		super.renderFace(var1, var2, var3, var4, var5);
		super.renderBackFace(var1, var2, var3, var4, var5);
	}

	public final boolean mayPick() {
		return false;
	}

	public final AABB getBoundingBox(int var1, int var2, int var3) {
		return null;
	}

	public final boolean blocksLight() {
		return true;
	}

	public final boolean isSolid() {
		return false;
	}

	public final int getLiquidType() {
		return this.liquidType;
	}

	public void neighborChanged(Level var1, int var2, int var3, int var4, int var5) {
		if(this.liquidType == 1 && var5 == Tile.lava.id) {
			var1.setTileNoUpdate(var2, var3, var4, Tile.rock.id);
		}

		if(this.liquidType == 2 && var5 == Tile.water.id) {
			var1.setTileNoUpdate(var2, var3, var4, Tile.rock.id);
		}

	}
}
