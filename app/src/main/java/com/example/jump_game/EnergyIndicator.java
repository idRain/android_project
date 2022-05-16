package com.example.jump_game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import androidx.core.content.ContextCompat;

class EnergyIndicator extends View {
    private float w;
    private float h;
    private float current_time;
    private float time_unit;

    public EnergyIndicator(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(ContextCompat.getColor(getContext(), R.color.energy_exist_color));
        canvas.drawRect(0, 0, current_time, h, paint);

        paint.setColor(ContextCompat.getColor(getContext(), R.color.energy_leave_color));
        canvas.drawRect(current_time, 0, w, h, paint);
    }

    @Override
    protected void  onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        this.current_time = w;
        this.time_unit = (float) (w * 1.0 / 300);
    }

    public void update(int abstract_time) {
        this.current_time = abstract_time * this.time_unit;
        invalidate();
    }

}
