package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 有序广播 action（自己定义）
    private static final String ACTION_COUNT_DUCKS = "com.example.myapplication.ACTION_COUNT_DUCKS";

    private ImageView ivHorn;
    private TextView tvBubble, tvNum1, tvNum2, tvNum3;

    // 3 个接收者（动态注册）
    private final ReceiverA receiverA = new ReceiverA(); // 第一位：标 1
    private final ReceiverB receiverB = new ReceiverB(); // 第三位：标 2（故意放最后）
    private final ReceiverC receiverC = new ReceiverC(); // 第二位：标 3

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvBubble = findViewById(R.id.tvBubble);
        tvNum1 = findViewById(R.id.tvNum1);
        tvNum2 = findViewById(R.id.tvNum2);
        tvNum3 = findViewById(R.id.tvNum3);
        ivHorn = findViewById(R.id.ivHorn);

        // 动态注册 3 个广播接收者（重点：priority 决定有序广播接收顺序）
        IntentFilter filterA = new IntentFilter(ACTION_COUNT_DUCKS);
        filterA.setPriority(300); // 最大：最先收到
        registerReceiver(receiverA, filterA);

        IntentFilter filterC = new IntentFilter(ACTION_COUNT_DUCKS);
        filterC.setPriority(200); // 第二个收到
        registerReceiver(receiverC, filterC);

        IntentFilter filterB = new IntentFilter(ACTION_COUNT_DUCKS);
        filterB.setPriority(100); // 最后收到
        registerReceiver(receiverB, filterB);

        // 点击喇叭：发送有序广播
        ivHorn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetUI();

                tvBubble.setVisibility(View.VISIBLE);

                Intent intent = new Intent(ACTION_COUNT_DUCKS);
                intent.putExtra("step", 0); // 用 extras 在接收者之间“传递进度”
                sendOrderedBroadcast(intent, null);
            }
        });
    }

    private void resetUI() {
        tvBubble.setVisibility(View.GONE);

        tvNum1.setVisibility(View.GONE);
        tvNum2.setVisibility(View.GONE);
        tvNum3.setVisibility(View.GONE);

        tvNum1.setText("");
        tvNum2.setText("");
        tvNum3.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 动态注册必须反注册
        unregisterReceiver(receiverA);
        unregisterReceiver(receiverB);
        unregisterReceiver(receiverC);
    }

    /**
     * 接收者 A：最先收到 -> 给第 1 只鸭子标 1
     */
    public class ReceiverA extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra("step", 0);

            // 显示 1
            tvNum1.setText("1");
            tvNum1.setVisibility(View.VISIBLE);

            // 把“进度”传给下一个接收者（通过广播 intent 的 extras）
            intent.putExtra("step", step + 1);
        }
    }

    /**
     * 接收者 C：第二个收到 -> 给第 3 只鸭子标 3（所以画面出现 1,3,2）
     */
    public class ReceiverC extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra("step", 0);

            // 显示 3
            tvNum3.setText("3");
            tvNum3.setVisibility(View.VISIBLE);

            intent.putExtra("step", step + 1);
        }
    }

    /**
     * 接收者 B：最后收到 -> 给第 2 只鸭子标 2
     */
    public class ReceiverB extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int step = intent.getIntExtra("step", 0);

            // 显示 2
            tvNum2.setText("2");
            tvNum2.setVisibility(View.VISIBLE);

            intent.putExtra("step", step + 1);
        }
    }
}
