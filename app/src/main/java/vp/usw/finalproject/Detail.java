package vp.usw.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        /* 인텐트 불러오기 */
        Intent intent = getIntent();
        Schedule sc = (Schedule) intent.getSerializableExtra("schedule");
        int month = intent.getIntExtra("month", 1);
        int day = intent.getIntExtra("day", 1);

        setTitle("2020년 " + month + "월 " + day + "일 상세일정");

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvStartTime = findViewById(R.id.tv_start_time);
        TextView tvEndTime = findViewById(R.id.tv_end_time);
        TextView tvLocation = findViewById(R.id.tv_location);
        TextView tvMemo = findViewById(R.id.tv_memo);

        /* 상세일정 있으면 출력 */
        if (sc != null) {
            tvTitle.setText(sc.getTitle());
            tvStartTime.setText(sc.getStartTime());
            tvEndTime.setText(sc.getEndTime());
            tvLocation.setText(sc.getLocation());
            tvMemo.setText(sc.getMemo());
        }

        /* 이전으로 버튼 설정 */
        Button button = findViewById(R.id.btn_exit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Detail.this.finish();
            }
        });
    }
}
