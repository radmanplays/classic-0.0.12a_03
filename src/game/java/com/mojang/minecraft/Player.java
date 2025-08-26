package com.mojang.minecraft;

import com.mojang.minecraft.level.Level;
import org.lwjgl.input.Keyboard;

public final class Player extends Entity {
	public Player(Level var1) {
		super(var1);
		this.heightOffset = 1.62F;
	}

	public final void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		float var1 = 0.0F;
		float var2 = 0.0F;
		boolean var3 = this.isInWater();
		boolean var4 = this.isInLava();
		if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
			this.resetPos();
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W)) {
			var2 = 0.0F - 1.0F;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S)) {
			++var2;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A)) {
			var1 = 0.0F - 1.0F;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
			++var1;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			if(var3) {
				this.yd += 0.06F;
			} else if(var4) {
				this.yd += 0.04F;
			} else if(this.onGround) {
				this.yd = 0.5F;
			}
		}

		if(var3) {
			this.moveRelative(var1, var2, 0.02F);
			this.move(this.xd, this.yd, this.zd);
			this.xd *= 0.7F;
			this.yd *= 0.7F;
			this.zd *= 0.7F;
			this.yd = (float)((double)this.yd - 0.02D);
		} else if(var4) {
			this.moveRelative(var1, var2, 0.02F);
			this.move(this.xd, this.yd, this.zd);
			this.xd *= 0.5F;
			this.yd *= 0.5F;
			this.zd *= 0.5F;
			this.yd = (float)((double)this.yd - 0.02D);
		} else {
			this.moveRelative(var1, var2, this.onGround ? 0.1F : 0.02F);
			this.move(this.xd, this.yd, this.zd);
			this.xd *= 0.91F;
			this.yd *= 0.98F;
			this.zd *= 0.91F;
			this.yd = (float)((double)this.yd - 0.08D);
			if(this.onGround) {
				this.xd *= 0.6F;
				this.zd *= 0.6F;
			}

		}
	}
}
