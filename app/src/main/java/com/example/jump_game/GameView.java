package com.example.jump_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.view.View;
import androidx.core.content.ContextCompat;


public class GameView extends View {
    //Size of screen
    private int view_height;

    //current game result
    private int result_value;

    //platforms' positions
    private int[][] floors;

    //Size of platforms
    private float w_padding;
    private float unit_w;
    private float h_t_padding;
    private float h_b_padding;
    private float unit_h;
    private float unit_space;
    private float rx;
    private float ry;

    //Penguin
    private Penguin penguin;
    private int current_position;
    private int next_position;
    //Penguin img bitmap
    private Bitmap penguin_bitmap;

    //Fish
    private Fish fish;
    private int fish_position;
    private Bitmap fish_bitmap;

    //steps of flying animation
    private int flying_step = 0;

    //for flying timer
    private int timer_interval = 25;
    private int fly_time = 100;

    //Timer
    class FlyingTimer extends CountDownTimer {
        public FlyingTimer() {
            super(fly_time, timer_interval);
        }

        @Override
        public void onTick(long l) {
            flying_step++;
            update(flying_step);
        }

        @Override
        public void onFinish() {
            flying_step = 0;
        }
    }

    //start fly animation
    public int startFly(boolean to_right) {
        if (to_right) next_position = current_position + 1;
        else next_position = current_position - 1;

        FlyingTimer fly_timer = new FlyingTimer();
        fly_timer.start();

        int result_flag = 1;
        if (Utils.isCorrectTransition(next_position, floors[1])) {
            result_value++;
            if (result_value != 0 && result_value % 6 == 4) {
                fish_position = Utils.randomFishPosition(next_position, floors[3]);
            }
            if (result_value != 0 && result_value % 6 == 0 && fish_position == next_position) result_flag = -1;
            Utils.generateNewFloor(floors);
        } else result_flag = 0;
        return result_flag;
    }

    public GameView(Context context) {
        super(context);
        this.floors = new int[][] {{1, 3, 5}, {0, 4, 6}, {1, 5}, {2, 4}, {3}, {2, 4}};
        this.current_position = 3;
        this.fish_position = -100;

        penguin_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.penguin);
        fish_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fish);

        penguin = new Penguin(penguin_bitmap);
        fish = new Fish(fish_bitmap);
    }

    public void update(int step) {
        int direction = (next_position > current_position) ? 1 : -1;
        int position = current_position;

        if (step == 4) {
            position = next_position;
        }

        int fish_animation_step = result_value % 6;
        fish.update(this.fish_position, (fish_animation_step == 0 && !(this.fish_position == position)) ? 6 : fish_animation_step);

        penguin.update(step, position, direction);
        invalidate();
    }

    @Override
    protected void  onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.view_height = h;

        this.w_padding = (float)0.08 * w;
        this.unit_w = (float)0.12 * w;
        this.h_t_padding = (float)0.24 * h;
        this.h_b_padding = (float)0.16 * h;
        this.unit_h = (float)0.04 * h;
        this.unit_space = (float)0.24 * h;
        this.rx = (float)0.01 * w;
        this.ry = (float)0.01 * h;

        float penguin_img_side = (float) (0.6 * this.unit_h + this.unit_space * 0.8);
        float penguin_top = (float) (this.view_height - h_b_padding - penguin_img_side - this.unit_h * 0.4);
        float penguin_jump_h = (float) (0.5 * this.unit_space + penguin_img_side);

        penguin.setPenguinSizes(penguin_img_side, this.w_padding, this.unit_w, penguin_top, penguin_jump_h);
        fish.setFishSize(penguin_img_side, this.w_padding, this.unit_w, h_t_padding + unit_h, unit_space + unit_h);
        penguin.update(0, 3, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.flying_step == 4) {
            current_position = next_position;
            Utils.removeLeavedFloor(floors);
        }
        //draw visible floors
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.platform_color));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < floors[2 - i].length; j++) {
                float left = w_padding + floors[2 - i][j] * unit_w;
                float right = left + unit_w;
                float top = h_t_padding + i * (unit_h + unit_space);
                float bottom = top + unit_h;
                canvas.drawRoundRect(left, top, right, bottom, rx, ry, paint);
            }
        }
        fish.draw(canvas);
        penguin.draw(canvas);
    }
}
