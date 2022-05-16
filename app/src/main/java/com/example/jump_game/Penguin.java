package com.example.jump_game;

import android.content.res.Resources;
import android.graphics.*;

public class Penguin {
    int top;
    int left;
    int bottom;
    int right;

    float top_0;
    float side;
    float w_padding;
    float w_platform;
    float jump_h;
    float jump_w;

    Bitmap bitmap;
    Rect[] frames = new Rect[2];
    int current_frame;

    public Penguin(Bitmap bitmap) {
        this.bitmap = bitmap;

        int w = bitmap.getWidth() / 2;
        int h = bitmap.getHeight();
        for (int i = 0; i < 2; i++) {
            frames[i] = new Rect(i * w, 0, (i + 1) * w, h);
        }
    }

    public void setPenguinSizes(float side, float w_padding, float w_platform, float top, float jump_h) {
        this.side = side;
        this.top_0 = top;
        this.w_padding = w_padding;
        this.w_platform = w_platform;
        this.jump_h = jump_h;
        this.jump_w = (w_padding + side) / 2;
    }

    public void update(int animation_step, int position, int direction) {
        this.current_frame = (0 < animation_step && animation_step < 4) ? 1 : 0;

        this.left = Math.round(w_padding + (w_platform - side) / 2 + w_platform * position);
        if (0 < animation_step && animation_step < 4) {
            this.top = Math.round(this.top_0 - jump_h / 4 * animation_step);
            this.left += Math.round(direction * jump_w / 4 * animation_step);
        } else {
            this.top = Math.round(this.top_0);
        }
        this.bottom = Math.round(this.top + this.side);
        this.right = Math.round(this.left + this.side);
    }

    public void draw (Canvas canvas) {
        Rect dst_rect = new Rect(this.left, this.top, this.right, this.bottom);
        canvas.drawBitmap(bitmap, frames[current_frame], dst_rect, new Paint());
    }
}
