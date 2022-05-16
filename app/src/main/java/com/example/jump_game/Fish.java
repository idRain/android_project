package com.example.jump_game;

import android.graphics.*;

public class Fish {
    float top;
    float left;
    float bottom;
    float right;
    float top_0;
    float space;
    float side;
    float w_padding;
    float w_platform;
    boolean is_visible;

    Bitmap bitmap;

    public Fish(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setFishSize(float side, float w_padding, float w_platform, float top, float space) {
        this.side = side;
        this.top_0 = top - side;
        this.w_padding = w_padding;
        this.w_platform = w_platform;
        this.space = space;
    }

    public void update(int position, int floor_num) {
        this.is_visible = (floor_num >= 4);
        if (is_visible) {
            this.top = this.top_0 + (floor_num - 4) * this.space;
            this.left = w_padding + (w_platform - side) / 2 + w_platform * position;
            this.bottom = this.top + this.side;
            this.right = this.left + this.side;
        }
    }

    public void draw(Canvas canvas) {
        if (is_visible) {
            Paint paint = new Paint();
            Rect frame = new Rect(0, 0, this.bitmap.getWidth(), this.bitmap.getHeight());
            Rect dst_rect = new Rect((int)this.left, (int)this.top, (int)this.right, (int)this.bottom);
            canvas.drawBitmap(bitmap, frame, dst_rect, new Paint());
        }
    }
}
