package com.mojang.minecraft;

import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.phys.AABB;
import java.util.ArrayList;

public class Entity {
	private Level level;
	public float xo;
	public float yo;
	public float zo;
	public float x;
	public float y;
	public float z;
	public float xd;
	public float yd;
	public float zd;
	public float yaw;
	public float pitch;
	public AABB boundingBox;
	public boolean onGround = false;
	public boolean removed = false;
	public float heightOffset = 0.0F;
	private float collisionBoxWidth = 0.6F;
	public float collisionBoxHeight = 1.8F;

	public Entity(Level var1) {
		this.level = var1;
		this.resetPos();
	}

	protected final void resetPos() {
		float var1 = (float)Math.random() * (float)this.level.width;
		float var2 = (float)(this.level.depth + 10);
		float var3 = (float)Math.random() * (float)this.level.height;
		this.setPos(var1, var2, var3);
	}

	public final void setCollisionBoxSize(float var1, float var2) {
		this.collisionBoxWidth = var1;
		this.collisionBoxHeight = var2;
	}

	public final void setPos(float var1, float var2, float var3) {
		this.x = var1;
		this.y = var2;
		this.z = var3;
		float var4 = this.collisionBoxWidth / 2.0F;
		float var5 = this.collisionBoxHeight / 2.0F;
		this.boundingBox = new AABB(var1 - var4, var2 - var5, var3 - var4, var1 + var4, var2 + var5, var3 + var4);
	}

	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
	}

	public final void move(float var1, float var2, float var3) {
		float var4 = var1;
		float var5 = var2;
		float var6 = var3;
		Level var10000 = this.level;
		AABB var9 = this.boundingBox;
		float var7 = var9.x0;
		float var8 = var9.y0;
		float var13 = var9.z0;
		float var14 = var9.x1;
		float var15 = var9.y1;
		float var16 = var9.z1;
		if(var1 < 0.0F) {
			var7 += var1;
		}

		if(var1 > 0.0F) {
			var14 += var1;
		}

		if(var2 < 0.0F) {
			var8 += var2;
		}

		if(var2 > 0.0F) {
			var15 += var2;
		}

		if(var3 < 0.0F) {
			var13 += var3;
		}

		if(var3 > 0.0F) {
			var16 += var3;
		}

		AABB var10 = new AABB(var7, var8, var13, var14, var15, var16);
		Level var21 = var10000;
		ArrayList var11 = new ArrayList();
		int var12 = (int)Math.floor((double)var10.x0);
		int var18 = (int)Math.floor((double)(var10.x1 + 1.0F));
		int var20 = (int)Math.floor((double)var10.y0);
		int var25 = (int)Math.floor((double)(var10.y1 + 1.0F));
		int var26 = (int)Math.floor((double)var10.z0);
		int var27 = (int)Math.floor((double)(var10.z1 + 1.0F));

		for(int var30 = var12; var30 < var18; ++var30) {
			for(int var22 = var20; var22 < var25; ++var22) {
				for(var12 = var26; var12 < var27; ++var12) {
					AABB var17;
					if(var30 >= 0 && var22 >= 0 && var12 >= 0 && var30 < var21.width && var22 < var21.depth && var12 < var21.height) {
						Tile var31 = Tile.tiles[var21.getTile(var30, var22, var12)];
						if(var31 != null) {
							var17 = var31.getBoundingBox(var30, var22, var12);
							if(var17 != null) {
								var11.add(var17);
							}
						}
					} else if(var30 < 0 || var22 < 0 || var12 < 0 || var30 >= var21.width || var12 >= var21.height) {
						var17 = Tile.bedrock.getBoundingBox(var30, var22, var12);
						if(var17 != null) {
							var11.add(var17);
						}
					}
				}
			}
		}

		ArrayList var19 = var11;

		float var23;
		float var24;
		AABB var28;
		float var29;
		for(var20 = 0; var20 < var19.size(); ++var20) {
			var28 = (AABB)var19.get(var20);
			var23 = var2;
			var10 = this.boundingBox;
			var9 = var28;
			if(var10.x1 > var9.x0 && var10.x0 < var9.x1) {
				if(var10.z1 > var9.z0 && var10.z0 < var9.z1) {
					if(var2 > 0.0F && var10.y1 <= var9.y0) {
						var24 = var9.y0 - var10.y1;
						if(var24 < var2) {
							var23 = var24;
						}
					}

					if(var23 < 0.0F && var10.y0 >= var9.y1) {
						var24 = var9.y1 - var10.y0;
						if(var24 > var23) {
							var23 = var24;
						}
					}

					var29 = var23;
				} else {
					var29 = var2;
				}
			} else {
				var29 = var2;
			}

			var2 = var29;
		}

		this.boundingBox.move(0.0F, var2, 0.0F);

		for(var20 = 0; var20 < var19.size(); ++var20) {
			var28 = (AABB)var19.get(var20);
			var23 = var1;
			var10 = this.boundingBox;
			var9 = var28;
			if(var10.y1 > var9.y0 && var10.y0 < var9.y1) {
				if(var10.z1 > var9.z0 && var10.z0 < var9.z1) {
					if(var1 > 0.0F && var10.x1 <= var9.x0) {
						var24 = var9.x0 - var10.x1;
						if(var24 < var1) {
							var23 = var24;
						}
					}

					if(var23 < 0.0F && var10.x0 >= var9.x1) {
						var24 = var9.x1 - var10.x0;
						if(var24 > var23) {
							var23 = var24;
						}
					}

					var29 = var23;
				} else {
					var29 = var1;
				}
			} else {
				var29 = var1;
			}

			var1 = var29;
		}

		this.boundingBox.move(var1, 0.0F, 0.0F);

		for(var20 = 0; var20 < var19.size(); ++var20) {
			var28 = (AABB)var19.get(var20);
			var23 = var3;
			var10 = this.boundingBox;
			var9 = var28;
			if(var10.x1 > var9.x0 && var10.x0 < var9.x1) {
				if(var10.y1 > var9.y0 && var10.y0 < var9.y1) {
					if(var3 > 0.0F && var10.z1 <= var9.z0) {
						var24 = var9.z0 - var10.z1;
						if(var24 < var3) {
							var23 = var24;
						}
					}

					if(var23 < 0.0F && var10.z0 >= var9.z1) {
						var24 = var9.z1 - var10.z0;
						if(var24 > var23) {
							var23 = var24;
						}
					}

					var29 = var23;
				} else {
					var29 = var3;
				}
			} else {
				var29 = var3;
			}

			var3 = var29;
		}

		this.boundingBox.move(0.0F, 0.0F, var3);
		this.onGround = var5 != var2 && var5 < 0.0F;
		if(var4 != var1) {
			this.xd = 0.0F;
		}

		if(var5 != var2) {
			this.yd = 0.0F;
		}

		if(var6 != var3) {
			this.zd = 0.0F;
		}

		this.x = (this.boundingBox.x0 + this.boundingBox.x1) / 2.0F;
		this.y = this.boundingBox.y0 + this.heightOffset;
		this.z = (this.boundingBox.z0 + this.boundingBox.z1) / 2.0F;
	}

	public final boolean isInWater() {
		return this.level.containsLiquid(this.boundingBox, 1);
	}

	public final boolean isInLava() {
		return this.level.containsLiquid(this.boundingBox, 2);
	}

	public final void moveRelative(float var1, float var2, float var3) {
		float var4 = var1 * var1 + var2 * var2;
		if(var4 >= 0.01F) {
			var4 = var3 / (float)Math.sqrt((double)var4);
			var1 *= var4;
			var2 *= var4;
			var3 = (float)Math.sin((double)this.yaw * Math.PI / 180.0D);
			var4 = (float)Math.cos((double)this.yaw * Math.PI / 180.0D);
			this.xd += var1 * var4 - var2 * var3;
			this.zd += var2 * var4 + var1 * var3;
		}
	}

	public final boolean isLit() {
		int var1 = (int)this.x;
		int var2 = (int)this.y;
		int var3 = (int)this.z;
		return this.level.isLit(var1, var2, var3);
	}

	public void render(float var1) {
	}
}
