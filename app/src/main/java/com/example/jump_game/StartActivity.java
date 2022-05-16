package com.example.jump_game;

import android.view.*;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private GameView gameView;
    private Timer timer;
    private EnergyIndicator energy_indicator;
    private GameTimerTask timer_task;

    private int available_time;
    private int current_time;
    private int result;

    //pause or game process state
    private RelativeLayout pause_state;
    private GridLayout game_state;
    private TextView result_place;
    private Button back;

    /*------Timer task-----Start------*/
    public class GameTimerTask extends TimerTask {
        public GameTimerTask() {
        }

        @Override
        public void run() {
            current_time--;
            if (current_time <= 0) {
                loseGame(result);
                return;
            } else if (current_time >= available_time) current_time = available_time;
            energy_indicator.update(current_time);
            return;
        }
    }
    /*------Timer task-----End------*/

    private void loseGame(int result) {
        timer.purge();
        timer.cancel();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("result_value", result);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void backHandler() {
        if (pause_state.getVisibility() == View.VISIBLE) {
            return;
        }
        timer.purge();
        timer.cancel();
        pause_state.setVisibility(View.VISIBLE);
        game_state.setVisibility(View.INVISIBLE);
        this.back.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        this.result = 0;

        gameView = new GameView(this);
        energy_indicator = new EnergyIndicator(this);
        timer_task = new GameTimerTask();

        RelativeLayout game_wrapper = findViewById(R.id.game_wrapper);
        RelativeLayout indicator_wrapper = findViewById(R.id.indicator_wrapper);
        game_wrapper.addView(gameView);
        indicator_wrapper.addView(energy_indicator);

        this.pause_state = findViewById(R.id.pause_state);
        this.game_state = findViewById(R.id.game_state);
        this.result_place = findViewById(R.id.current_result);
        this.back = findViewById(R.id.back_from_start);

        //change default back press handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backHandler();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.available_time = 300;
        this.current_time = available_time;

        timer = new Timer(true);
        timer.schedule(timer_task, 0, 100);
    }

    @Override
    public void onClick(View view) {
        int current_id = view.getId();
        if (R.id.back_from_start == current_id) {
            backHandler();
        } else if (current_id == R.id.continue_game) {
            pause_state.setVisibility(View.INVISIBLE);
            game_state.setVisibility(View.VISIBLE);
            this.back.setVisibility(View.VISIBLE);
            timer = new Timer(true);
            timer_task = new GameTimerTask();
            timer.schedule(timer_task, 0, 100);
        } else if (current_id == R.id.back_from_pause) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("result_value", -1);
            startActivity(intent);
        } else {
            boolean is_right = (current_id == R.id.to_right) ? true : false;
            int result = gameView.startFly(is_right);
            if (result == 0) loseGame(this.result);
            else {
                this.result += 1;
                this.result_place.setText(String.valueOf(this.result));
                if (result < 0) this.current_time += 8;
            }
        }
    }
}