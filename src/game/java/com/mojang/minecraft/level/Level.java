package com.mojang.minecraft.level;

import com.mojang.minecraft.HitResult;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.character.Vec3;
import com.mojang.minecraft.level.tile.LiquidTile;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.phys.AABB;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.lwjgl.opengl.GL11;

public final class Level {
	public final int width;
	public final int height;
	public final int depth;
	public byte[] blocks;
	private int[] lightDepths;
	ArrayList levelListeners = new ArrayList();
	public Random random = new Random();
	public int randValue = this.random.nextInt();
	private Minecraft minecraft;
	public int unprocessed = 0;
	private int[] coords = new int[1048576];

	public Level(Minecraft var1, int var2, int var3, int var4) {
		this.minecraft = var1;
		var3 = var1.width * 240 / var1.height;
		var2 = var1.height * 240 / var1.height;
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, (double)var3, (double)var2, 0.0D, 100.0D, 300.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -200.0F);
		var1.showLoadingScreen("Loading level", "Reading..");
		this.width = 256;
		this.height = 256;
		this.depth = 64;
		this.blocks = new byte[256 << 8 << 6];
		this.lightDepths = new int[256 << 8];
		boolean var5 = this.load();
		if(!var5) {
			this.generateLevel();
		}

	}

	public final void generateLevel() {
		this.minecraft.showLoadingScreen("Generating level", "Raising..");
		LevelGen var1 = new LevelGen(this.width, this.height, this.depth);
		LevelGen var2 = var1;
		int var3 = var1.width;
		int var4 = var1.height;
		int var5 = var1.depth;
		int[] var6 = (new NoiseMap(var1.random, 1, true)).read(var3, var4);
		int[] var7 = (new NoiseMap(var1.random, 0, true)).read(var3, var4);
		int[] var8 = (new NoiseMap(var1.random, 2, false)).read(var3, var4);
		int[] var9 = (new NoiseMap(var1.random, 4, false)).read(var3, var4);
		int[] var10 = (new NoiseMap(var1.random, 1, true)).read(var3, var4);
		byte[] var11 = new byte[var1.width * var1.height * var1.depth];
		int var12 = var5 / 2;

		int var17;
		int var19;
		int var20;
		int var21;
		for(int var13 = 0; var13 < var3; ++var13) {
			for(int var14 = 0; var14 < var5; ++var14) {
				for(int var15 = 0; var15 < var4; ++var15) {
					int var16 = var6[var13 + var15 * var2.width];
					var17 = var7[var13 + var15 * var2.width];
					int var18 = var8[var13 + var15 * var2.width];
					var19 = var9[var13 + var15 * var2.width];
					if(var18 < 128) {
						var17 = var16;
					}

					var20 = var16;
					if(var17 > var16) {
						var20 = var17;
					}

					var20 = (var20 - 128) / 8;
					var20 = var20 + var12 - 1;
					var21 = ((var10[var13 + var15 * var2.width] - 128) / 6 + var12 + var20) / 2;
					if(var19 < 92) {
						var20 = var20 / 2 << 1;
					} else if(var19 < 160) {
						var20 = var20 / 4 << 2;
					}

					if(var20 < var12 - 2) {
						var20 = (var20 - var12) / 2 + var12;
					}

					if(var21 > var20 - 2) {
						var21 = var20 - 2;
					}

					int var22 = (var14 * var2.height + var15) * var2.width + var13;
					int var23 = 0;
					if(var14 == var20 && var14 >= var5 / 2) {
						var23 = Tile.grass.id;
					}

					if(var14 < var20) {
						var23 = Tile.dirt.id;
					}

					if(var14 <= var21) {
						var23 = Tile.rock.id;
					}

					var11[var22] = (byte)var23;
				}
			}
		}

		this.blocks = var11;
		this.minecraft.showLoadingScreen("Generating level", "Carving..");
		byte[] var27 = this.blocks;
		var2 = var1;
		var4 = var1.width;
		var5 = var1.height;
		int var29 = var1.depth;
		int var30 = var4 * var5 * var29 / 256 / 64;

		int var25;
		for(int var31 = 0; var31 < var30; ++var31) {
			float var33 = var2.random.nextFloat() * (float)var4;
			float var35 = var2.random.nextFloat() * (float)var29;
			float var37 = var2.random.nextFloat() * (float)var5;
			var12 = (int)(var2.random.nextFloat() + var2.random.nextFloat() * 150.0F);
			float var39 = (float)((double)var2.random.nextFloat() * Math.PI * 2.0D);
			float var40 = 0.0F;
			float var41 = (float)((double)var2.random.nextFloat() * Math.PI * 2.0D);
			float var42 = 0.0F;

			for(var17 = 0; var17 < var12; ++var17) {
				var33 = (float)((double)var33 + Math.sin((double)var39) * Math.cos((double)var41));
				var37 = (float)((double)var37 + Math.cos((double)var39) * Math.cos((double)var41));
				var35 = (float)((double)var35 + Math.sin((double)var41));
				var39 += var40 * 0.2F;
				var40 *= 0.9F;
				var40 += var2.random.nextFloat() - var2.random.nextFloat();
				var41 += var42 * 0.5F;
				var41 *= 0.5F;
				var42 *= 0.9F;
				var42 += var2.random.nextFloat() - var2.random.nextFloat();
				float var43 = (float)(Math.sin((double)var17 * Math.PI / (double)var12) * 2.5D + 1.0D);

				for(var19 = (int)(var33 - var43); var19 <= (int)(var33 + var43); ++var19) {
					for(var20 = (int)(var35 - var43); var20 <= (int)(var35 + var43); ++var20) {
						for(var21 = (int)(var37 - var43); var21 <= (int)(var37 + var43); ++var21) {
							float var44 = (float)var19 - var33;
							float var45 = (float)var20 - var35;
							float var24 = (float)var21 - var37;
							var24 = var44 * var44 + var45 * var45 * 2.0F + var24 * var24;
							if(var24 < var43 * var43 && var19 >= 1 && var20 >= 1 && var21 >= 1 && var19 < var2.width - 1 && var20 < var2.depth - 1 && var21 < var2.height - 1) {
								var25 = (var20 * var2.height + var21) * var2.width + var19;
								if(var27[var25] == Tile.rock.id) {
									var27[var25] = 0;
								}
							}
						}
					}
				}
			}
		}

		this.blocks = var27;
		this.minecraft.showLoadingScreen("Generating level", "Watering..");
		long var26 = System.nanoTime();
		long var28 = 0L;
		var25 = Tile.calmWater.id;

		for(var30 = 0; var30 < this.width; ++var30) {
			var28 += this.floodFill(var30, this.depth / 2 - 1, 0, 0, var25);
			var28 += this.floodFill(var30, this.depth / 2 - 1, this.height - 1, 0, var25);
		}

		for(var30 = 0; var30 < this.height; ++var30) {
			var28 += this.floodFill(0, this.depth / 2 - 1, var30, 0, var25);
			var28 += this.floodFill(this.width - 1, this.depth / 2 - 1, var30, 0, var25);
		}

		long var32 = System.nanoTime();
		this.minecraft.showLoadingScreen("Generating level", "Melting..");
		var25 = 0;

		for(var29 = 0; var29 < 400; ++var29) {
			int var34 = this.random.nextInt(this.width);
			int var36 = this.random.nextInt(this.depth / 2);
			int var38 = this.random.nextInt(this.height);
			if(this.getTile(var34, var36, var38) == 0) {
				++var25;
				var28 += this.floodFill(var34, var36, var38, 0, Tile.calmLava.id);
			}
		}

		System.out.println("LavaCount: " + var25);
		System.out.println("Flood filled " + var28 + " tiles in " + (double)(var32 - var26) / 1000000.0D + " ms");
		this.calculateLightDepths(0, 0, this.width, this.height);

		for(var29 = 0; var29 < this.levelListeners.size(); ++var29) {
			((LevelRenderer)this.levelListeners.get(var29)).resetChunks();
		}

	}

	private boolean load() {
		try {
			VFile2 file = new VFile2("level.dat");
			if (!file.exists()) {
				return false;
			}
			DataInputStream var1 = new DataInputStream(new GZIPInputStream(file.getInputStream()));
			var1.readFully(this.blocks);
			var1.close();
			this.calculateLightDepths(0, 0, this.width, this.height);

			for(int var3 = 0; var3 < this.levelListeners.size(); ++var3) {
				((LevelRenderer)this.levelListeners.get(var3)).resetChunks();
			}

			return true;
		} catch (Exception var2) {
			var2.printStackTrace();
			return false;
		}
	}

	public final void save() {
		try {
			VFile2 file = new VFile2("level.dat");
			DataOutputStream var1 = new DataOutputStream(new GZIPOutputStream(file.getOutputStream()));
			var1.write(this.blocks);
			var1.close();
		} catch (Exception var2) {
			var2.printStackTrace();
		}
	}

	private void calculateLightDepths(int var1, int var2, int var3, int var4) {
		for(int var5 = var1; var5 < var1 + var3; ++var5) {
			for(int var6 = var2; var6 < var2 + var4; ++var6) {
				int var7 = this.lightDepths[var5 + var6 * this.width];

				int var8;
				for(var8 = this.depth - 1; var8 > 0; --var8) {
					Tile var14 = Tile.tiles[this.getTile(var5, var8, var6)];
					if(var14 == null ? false : var14.blocksLight()) {
						break;
					}
				}

				this.lightDepths[var5 + var6 * this.width] = var8 + 1;
				if(var7 != var8) {
					int var9 = var7 < var8 ? var7 : var8;
					var7 = var7 > var8 ? var7 : var8;

					for(var8 = 0; var8 < this.levelListeners.size(); ++var8) {
						LevelRenderer var10 = (LevelRenderer)this.levelListeners.get(var8);
						var10.setDirty(var5 - 1, var9 - 1, var6 - 1, var5 + 1, var7 + 1, var6 + 1);
					}
				}
			}
		}

	}

	public final boolean setTile(int var1, int var2, int var3, int var4) {
		if(var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < this.width && var2 < this.depth && var3 < this.height) {
			if(var4 == this.blocks[(var2 * this.height + var3) * this.width + var1]) {
				return false;
			} else {
				this.blocks[(var2 * this.height + var3) * this.width + var1] = (byte)var4;
				this.updateNeighborAt(var1 - 1, var2, var3, var4);
				this.updateNeighborAt(var1 + 1, var2, var3, var4);
				this.updateNeighborAt(var1, var2 - 1, var3, var4);
				this.updateNeighborAt(var1, var2 + 1, var3, var4);
				this.updateNeighborAt(var1, var2, var3 - 1, var4);
				this.updateNeighborAt(var1, var2, var3 + 1, var4);
				this.calculateLightDepths(var1, var3, 1, 1);

				for(var4 = 0; var4 < this.levelListeners.size(); ++var4) {
					LevelRenderer var5 = (LevelRenderer)this.levelListeners.get(var4);
					var5.setDirty(var1 - 1, var2 - 1, var3 - 1, var1 + 1, var2 + 1, var3 + 1);
				}

				return true;
			}
		} else {
			return false;
		}
	}

	public final boolean setTileNoUpdate(int var1, int var2, int var3, int var4) {
		if(var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < this.width && var2 < this.depth && var3 < this.height) {
			if(var4 == this.blocks[(var2 * this.height + var3) * this.width + var1]) {
				return false;
			} else {
				this.blocks[(var2 * this.height + var3) * this.width + var1] = (byte)var4;
				return true;
			}
		} else {
			return false;
		}
	}

	private void updateNeighborAt(int var1, int var2, int var3, int var4) {
		if(var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < this.width && var2 < this.depth && var3 < this.height) {
			Tile var5 = Tile.tiles[this.blocks[(var2 * this.height + var3) * this.width + var1]];
			if(var5 != null) {
				var5.neighborChanged(this, var1, var2, var3, var4);
			}

		}
	}

	public final boolean isLit(int var1, int var2, int var3) {
		return var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < this.width && var2 < this.depth && var3 < this.height ? var2 >= this.lightDepths[var1 + var3 * this.width] : true;
	}

	public final int getTile(int var1, int var2, int var3) {
		return var1 >= 0 && var2 >= 0 && var3 >= 0 && var1 < this.width && var2 < this.depth && var3 < this.height ? this.blocks[(var2 * this.height + var3) * this.width + var1] : 0;
	}

	public final boolean containsLiquid(AABB var1, int var2) {
		int var3 = (int)Math.floor((double)var1.x0);
		int var4 = (int)Math.floor((double)(var1.x1 + 1.0F));
		int var5 = (int)Math.floor((double)var1.y0);
		int var6 = (int)Math.floor((double)(var1.y1 + 1.0F));
		int var7 = (int)Math.floor((double)var1.z0);
		int var11 = (int)Math.floor((double)(var1.z1 + 1.0F));
		if(var3 < 0) {
			var3 = 0;
		}

		if(var5 < 0) {
			var5 = 0;
		}

		if(var7 < 0) {
			var7 = 0;
		}

		if(var4 > this.width) {
			var4 = this.width;
		}

		if(var6 > this.depth) {
			var6 = this.depth;
		}

		if(var11 > this.height) {
			var11 = this.height;
		}

		for(var3 = var3; var3 < var4; ++var3) {
			for(int var8 = var5; var8 < var6; ++var8) {
				for(int var9 = var7; var9 < var11; ++var9) {
					Tile var10 = Tile.tiles[this.getTile(var3, var8, var9)];
					if(var10 != null && var10.getLiquidType() == var2) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private long floodFill(int var1, int var2, int var3, int var4, int var5) {
		byte var18 = (byte)var5;
		ArrayList var19 = new ArrayList();
		byte var6 = 0;
		int var7 = this.height - 1;
		int var8 = this.width - 1;
		int var20 = var6 + 1;
		this.coords[0] = ((var2 << 8) + var3 << 8) + var1;
		long var11 = 0L;
		var1 = this.width * this.height;

		while(var20 > 0) {
			--var20;
			var2 = this.coords[var20];
			if(var20 == 0 && var19.size() > 0) {
				System.out.println("IT HAPPENED!");
				this.coords = (int[])var19.remove(var19.size() - 1);
				var20 = this.coords.length;
			}

			var3 = var2 >> 8 & var7;
			int var9 = var2 >> 16;
			int var10 = var2 & var8;

			int var13;
			for(var13 = var10; var10 > 0 && this.blocks[var2 - 1] == 0; --var2) {
				--var10;
			}

			while(var13 < this.width && this.blocks[var2 + var13 - var10] == 0) {
				++var13;
			}

			int var14 = var2 >> 8 & var7;
			int var15 = var2 >> 16;
			if(var14 != var3 || var15 != var9) {
				System.out.println("hoooly fuck");
			}

			boolean var21 = false;
			boolean var22 = false;
			boolean var16 = false;
			var11 += (long)(var13 - var10);

			for(var10 = var10; var10 < var13; ++var10) {
				this.blocks[var2] = var18;
				boolean var17;
				if(var3 > 0) {
					var17 = this.blocks[var2 - this.width] == 0;
					if(var17 && !var21) {
						if(var20 == this.coords.length) {
							var19.add(this.coords);
							this.coords = new int[1048576];
							var20 = 0;
						}

						this.coords[var20++] = var2 - this.width;
					}

					var21 = var17;
				}

				if(var3 < this.height - 1) {
					var17 = this.blocks[var2 + this.width] == 0;
					if(var17 && !var22) {
						if(var20 == this.coords.length) {
							var19.add(this.coords);
							this.coords = new int[1048576];
							var20 = 0;
						}

						this.coords[var20++] = var2 + this.width;
					}

					var22 = var17;
				}

				if(var9 > 0) {
					var17 = this.blocks[var2 - var1] == 0;
					if(var17 && !var16) {
						if(var20 == this.coords.length) {
							var19.add(this.coords);
							this.coords = new int[1048576];
							var20 = 0;
						}

						this.coords[var20++] = var2 - var1;
					}

					var16 = var17;
				}

				++var2;
			}
		}

		return var11;
	}
	
	public HitResult clip(Vec3 var1, Vec3 var2) {
		if(!Float.isNaN(var1.x) && !Float.isNaN(var1.y) && !Float.isNaN(var1.z)) {
			if(!Float.isNaN(var2.x) && !Float.isNaN(var2.y) && !Float.isNaN(var2.z)) {
				int var3 = (int)Math.floor((double)var2.x);
				int var4 = (int)Math.floor((double)var2.y);
				int var5 = (int)Math.floor((double)var2.z);
				int var6 = (int)Math.floor((double)var1.x);
				int var7 = (int)Math.floor((double)var1.y);
				int var8 = (int)Math.floor((double)var1.z);

				int var50 = 20;
				
				while(!Float.isNaN(var1.x) && !Float.isNaN(var1.y) && !Float.isNaN(var1.z)&& var50-- > 0) {
					if(var6 == var3 && var7 == var4 && var8 == var5) {
						return null;
					}

					float var9 = 999.0F;
					float var10 = 999.0F;
					float var11 = 999.0F;
					if(var3 > var6) {
						var9 = (float)var6 + 1.0F;
					}

					if(var3 < var6) {
						var9 = (float)var6;
					}

					if(var4 > var7) {
						var10 = (float)var7 + 1.0F;
					}

					if(var4 < var7) {
						var10 = (float)var7;
					}

					if(var5 > var8) {
						var11 = (float)var8 + 1.0F;
					}

					if(var5 < var8) {
						var11 = (float)var8;
					}

					float var12 = 999.0F;
					float var13 = 999.0F;
					float var14 = 999.0F;
					float var15 = var2.x - var1.x;
					float var16 = var2.y - var1.y;
					float var17 = var2.z - var1.z;
					if(var9 != 999.0F) {
						var12 = (var9 - var1.x) / var15;
					}

					if(var10 != 999.0F) {
						var13 = (var10 - var1.y) / var16;
					}

					if(var11 != 999.0F) {
						var14 = (var11 - var1.z) / var17;
					}

					boolean var18 = false;
					byte var20;
					if(var12 < var13 && var12 < var14) {
						if(var3 > var6) {
							var20 = 4;
						} else {
							var20 = 5;
						}

						var1.x = var9;
						var1.y += var16 * var12;
						var1.z += var17 * var12;
					} else if(var13 < var14) {
						if(var4 > var7) {
							var20 = 0;
						} else {
							var20 = 1;
						}

						var1.x += var15 * var13;
						var1.y = var10;
						var1.z += var17 * var13;
					} else {
						if(var5 > var8) {
							var20 = 2;
						} else {
							var20 = 3;
						}

						var1.x += var15 * var14;
						var1.y += var16 * var14;
						var1.z = var11;
					}

					var6 = (int)Math.floor((double)var1.x);
					if(var20 == 5) {
						--var6;
					}

					var7 = (int)Math.floor((double)var1.y);
					if(var20 == 1) {
						--var7;
					}

					var8 = (int)Math.floor((double)var1.z);
					if(var20 == 3) {
						--var8;
					}

					int var19 = this.getTile(var6, var7, var8);
					if(var19 > 0 && Tile.tiles[var19].getLiquidType() == 0) {
						return new HitResult(0, var6, var7, var8, var20);
					}
				}

				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
