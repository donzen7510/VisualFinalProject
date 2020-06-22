package vp.usw.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    final int VIEW_MONTHLY = 0; // 월간 방식
    final int VIEW_WEEKLY = 1; // 주간 방식

    Calendar calendar;
    int year, mon, day; // 이번 년도, 월, 일, 보여주고 있는 월
    int selectionMon, selectionDay, selectionView; // 선택된 월, 일, 보기 방식

    LinearLayout body; // 달력 나오는 레이아웃
    Button prevBtn, nextBtn; // 이전, 이후 월(주) 넘기기 버튼

    View prevClick; // 이전에 클릭한 뷰(흰테두리.선택 해제용)
    View space; // 공백(주간 방식으로 변경할 때 왼쪽 공백)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        /* 캘린더 설정 */
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        mon = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        /* 서브 타이틀 설정(2020년 고정) */
        myToolbar.setSubtitle(year + "년");

        /* 뷰 불러오기 */
        body = (LinearLayout) findViewById(R.id.body);
        space = findViewById(R.id.space);
        prevBtn = findViewById(R.id.btn_prev);
        nextBtn = findViewById(R.id.btn_next);

        /* 현재 날짜 선택, 월간 방식 */
        selectionMon = mon;
        selectionDay = day;
        selectionView = VIEW_MONTHLY;

        /* 월(주) 넘기기 버튼 클릭 설정 */
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectionView) {
                    /* 월간 방식 */
                    case VIEW_MONTHLY:
                        if (selectionMon > 1) { // 1월 이전으로 가는 것을 방지합니다.
                            createMonthlyView(--selectionMon);
                        }
                        break;
                    /* 주간 방식 */
                    case VIEW_WEEKLY:
                        calendar.set(2020, selectionMon - 1, 1);
                        int firstWeek = calendar.get(Calendar.DAY_OF_WEEK); // 이달 1일의 요일
                        calendar.set(2020, selectionMon - 1, selectionDay);
                        int week = calendar.get(Calendar.DAY_OF_WEEK); // 현재 선택된 요일

                        if (selectionDay - 1 < 7 && week < firstWeek) // 1주 차이나고 1일과 현재가 7일 미만 차이
                            selectionDay = 1;
                        else if (selectionDay - 1 < 7) { // 1일과 같은 주
                            selectionMon--;
                            if (selectionMon < 1) {
                                selectionMon = 1;
                                break; // 1월 이전으로 가는 것을 방지합니다.
                            }
                            calendar.set(2020, selectionMon - 1, 1);
                            selectionDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        } else
                            selectionDay -= 7;

                        createWeeklyView(selectionMon, selectionDay);
                        break;
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectionView) {
                    /* 월간 방식 */
                    case VIEW_MONTHLY:
                        if (selectionMon < 12) // 12월 이후로 가는 것을 방지합니다.
                            createMonthlyView(++selectionMon);
                        break;
                    /* 주간 방식 */
                    case VIEW_WEEKLY:
                        calendar.set(2020, selectionMon - 1, 1);
                        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 이달 마지막 날짜
                        calendar.set(2020, selectionMon - 1, lastDay);
                        int lastWeek = calendar.get(Calendar.DAY_OF_WEEK); // 이달 마지막 날의 요일
                        calendar.set(2020, selectionMon - 1, selectionDay);
                        int week = calendar.get(Calendar.DAY_OF_WEEK); // 현재 날짜의 요일

                        if (lastDay - selectionDay < 7 && week <= lastWeek) { // 마지막 날짜와 현재 날짜가 같은 주
                            selectionMon++;
                            if (selectionMon > 12) {
                                selectionMon = 12;
                                break; // 12월 이후로 가는 것을 방지합니다.
                            }
                            selectionDay = 1;
                        } else if (lastDay - selectionDay < 7 && week > lastWeek) // 마지막 날짜와 1주 차이
                            selectionDay = lastDay;
                        else
                            selectionDay += 7;

                        createWeeklyView(selectionMon, selectionDay);
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* (뷰를 모두 불러오고) 월간 혹은 주간 일정표 출력 */
        switch (selectionView) {
            case VIEW_MONTHLY:
                createMonthlyView(selectionMon);
                break;
            case VIEW_WEEKLY:
                createWeeklyView(selectionMon, selectionDay);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /* 오늘 메뉴 */
            case R.id.today:
                selectionMon = mon;
                selectionDay = day;
                switch (selectionView) {
                    case VIEW_MONTHLY:
                        createMonthlyView(selectionMon);
                        break;
                    case VIEW_WEEKLY:
                        createWeeklyView(selectionMon, selectionDay);
                        break;
                }
                return true;
            /* 보기 방식 메뉴 */
            case R.id.viewing:
                /* 월간이면 주간, 주간이면 월간으로 변경 */
                selectionView = selectionView == VIEW_MONTHLY ? VIEW_WEEKLY : VIEW_MONTHLY;
                switch (selectionView) {
                    case VIEW_MONTHLY:
                        Toast.makeText(getApplicationContext(), "월별 일정", Toast.LENGTH_SHORT).show();
                        createMonthlyView(selectionMon);
                        break;
                    case VIEW_WEEKLY:
                        Toast.makeText(getApplicationContext(), "주간 일정", Toast.LENGTH_SHORT).show();
                        createWeeklyView(selectionMon, selectionDay);
                        break;
                }
                return true;
        }
        return false;
    }

    /** 월간 방식으로 출력 */
    @SuppressLint("SetTextI18n")
    private void createMonthlyView(int month) {
        space.setVisibility(View.GONE); // (주간 방식을 위한) 공백 숨기기

        setTitle(month + "월");

        calendar.set(2020, month - 2, 1);
        int prevLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 이전달 마지막 날짜
        calendar.set(2020, month - 1, 1);
        int startDay = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 이달 1일의 요일
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 이달 마지막 날짜

        LinearLayout monthlyBody = new LinearLayout(getApplicationContext()); // 월간 방식 레이아웃
        monthlyBody.setOrientation(LinearLayout.VERTICAL);
        monthlyBody.setWeightSum(6);

        LinearLayout.LayoutParams weekParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
        LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

        /* 본격적인 출력 */
        for (int w = 0; w < 6; w++) { // 6주
            LinearLayout week = new LinearLayout(getApplicationContext()); // 한 주의 레이아웃
            week.setLayoutParams(weekParams);
            week.setOrientation(LinearLayout.HORIZONTAL);
            week.setWeightSum(7);
            for (int d = 0; d < 7; d++) { // 7일
                int n = w * 7 + d - startDay + 1; // n = 며칠(1 ~ )
                int m = month;

                /* 일의 뷰 */
                MonthlyDay viewDay = new MonthlyDay(getApplicationContext());
                viewDay.setLayoutParams(dayParams);
                viewDay.setBackgroundColor(Color.BLACK);
                viewDay.setClickable(true);

                /* 일의 뷰에 있는 텍뷰 불러오기 */
                TextView tvMon = viewDay.findViewById(R.id.tv_mon);
                TextView tvDay = viewDay.findViewById(R.id.tv_day);
                TextView tvSchedule = viewDay.findViewById(R.id.tv_schedule);

                Schedule sc; // 상세일정 DTO

                if (n < 1) { // 지난달의 일
                    n += prevLastDay;
                    m--;
                    viewDay.setAlpha(0.5f);
                } else if (n > lastDay) { // 다음달의 일
                    n -= lastDay;
                    m++;
                    viewDay.setAlpha(0.5f);
                }

                tvMon.setText("" + m);
                tvDay.setText("" + n);

                calendar.set(2020, m - 1, n);

                /* 선택된 날에 하얀 테두리 설정 */
                if (m == selectionMon && n == selectionDay) {
                    viewDay.setBackground(getDrawable(R.drawable.border));
                    prevClick = viewDay;
                }

                /* 상세일정 있으면 */
                sc = getSchedule(m, n);
                if (sc != null)
                    tvSchedule.setText(sc.getTitle()); // 제목 출력

                /* 일요일은 빨간색 */
                if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    tvDay.setTextColor(Color.rgb(255, 0, 0));

                /* 클릭 설정 */
                viewDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        select(view);
                        scheduleDialog(selectionMon, selectionDay);
                    }
                });

                /* 롱 클릭 설정 */
                viewDay.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        select(view);

                        Schedule sc = getSchedule(selectionMon, selectionDay);
                        if (sc != null) {
                            Intent intent = new Intent(getApplicationContext(), Detail.class);
                            intent.putExtra("month", selectionMon);
                            intent.putExtra("day", selectionDay);
                            intent.putExtra("schedule", sc);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "일정이 없습니다", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });

                week.addView(viewDay);
            }
            monthlyBody.addView(week);
        }

        body.removeAllViews();
        body.addView(monthlyBody, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /** 주간 방식으로 출력 */
    @SuppressLint("SetTextI18n")
    private void createWeeklyView(int month, int day) {
        space.setVisibility(View.VISIBLE); // 주간 방식을 위한 공백 보이기

        setTitle(month + "월");

        calendar.set(2020, month - 2, 1);
        int prevLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 이전달 마지막 날짜
        calendar.set(2020, month - 1, day);
        int startDay = day - calendar.get(Calendar.DAY_OF_WEEK) + 1; // 이번주 며칠부터?
        int endDay = startDay + 7;
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 이달 마지막 날짜

        LinearLayout weeklyBody = (LinearLayout) View.inflate(MainActivity.this, R.layout.weekly_view, null); // 주간 방식 레이아웃
        LinearLayout weekView = weeklyBody.findViewById(R.id.week_view);

        LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

        /* 본격적인 출력 */
        for (int d = startDay; d < endDay; d++) {
            /* 일의 뷰 */
            WeeklyDay viewDay = new WeeklyDay(getApplicationContext());
            viewDay.setLayoutParams(dayParams);
            viewDay.setBackgroundColor(Color.BLACK);
            viewDay.setClickable(true);

            /* 일의 뷰에 있는 텍뷰 불러오기 */
            TextView tvMon = viewDay.findViewById(R.id.tv_mon);
            TextView tvDay = viewDay.findViewById(R.id.tv_day);
            tvDay.setTextColor(Color.WHITE);
            tvDay.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            int m = month;
            int n = d; // n = 며칠

            if (n < 1) { // 지난달의 일
                n += prevLastDay;
                m--;
                tvDay.setAlpha(0.5f);
            } else if (d > lastDay) { // 다음달의 일
                n -= lastDay;
                m++;
                tvDay.setText("" + n);
                tvDay.setAlpha(0.5f);
            }

            tvMon.setText("" + m);
            tvDay.setText("" + n);

            /* 선택된 날에 하얀 테두리 설정 */
            if (m == selectionMon && n == selectionDay) {
                viewDay.setBackground(getDrawable(R.drawable.border));
                prevClick = viewDay;
            }

            calendar.set(2020, m - 1, n);

            /* 일요일은 빨간색 */
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                tvDay.setTextColor(Color.rgb(255, 0, 0));

            /* 클릭 설정(선택만) */
            viewDay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    select(view);
                }
            });

            weekView.addView(viewDay);
        }

        body.removeAllViews();
        body.addView(weeklyBody);

        /* 시간표 출력 */
        RelativeLayout timezone = weeklyBody.findViewById(R.id.timezone);
        timezone.removeAllViews();

        /* px -> dp */
        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams toParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

        for (int i = 0; i < 24; i++) { // 0시 ~ 24시
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
            params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25 + 50 * i, getResources().getDisplayMetrics());

            /* 시간 텍뷰 만들기 */
            final TextView tvTime = new TextView(getApplicationContext());
            tvTime.setLayoutParams(params);
            tvTime.setPadding(padding, padding, padding, padding);
            tvTime.setGravity(Gravity.CENTER | Gravity.END);
            tvTime.setText("" + (i + 1));
            tvTime.setTextColor(Color.WHITE);

            timezone.addView(tvTime);

            /* 네모칸 레이아웃 만들기 */
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            lineParams.leftMargin = width;
            lineParams.topMargin = height * i;

            LinearLayout line = new LinearLayout(getApplicationContext());
            line.setLayoutParams(lineParams);

            /* 네모칸 뷰 7개(일 ~ 토) */
            for (int w = 0; w < 7; w++) {
                /* 네모칸 만들기 */
                TimezoneOne to = new TimezoneOne(getApplicationContext());
                to.setLayoutParams(toParams);
                to.setBackground(getDrawable(R.drawable.rectangle));
                to.setClickable(true);

                /* 네모칸 텍뷰 불러오기 */
                TextView tvMon = to.findViewById(R.id.tv_mon);
                TextView tvDay = to.findViewById(R.id.tv_day);
                TextView tvTimeTo = to.findViewById(R.id.tv_time);
                TextView tvSchedule = to.findViewById(R.id.tv_schedule);

                int m = selectionMon;
                int d = startDay + w;

                if (d < 1) { // 지난달이면
                    m--;
                    d = prevLastDay - w;
                } else if (d > lastDay) { // 다음달이면
                    m++;
                    d -= lastDay;
                }
                tvMon.setText("" + m);
                tvTimeTo.setText("" + i);
                tvDay.setText("" + d);

                /* 클릭 설정 */
                to.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView tvMon = view.findViewById(R.id.tv_mon);
                        TextView tvDay = view.findViewById(R.id.tv_day);
                        TextView tvTime = view.findViewById(R.id.tv_time);

                        scheduleDialog(Integer.parseInt(tvMon.getText().toString()),
                                Integer.parseInt(tvDay.getText().toString()),
                                Integer.parseInt(tvTime.getText().toString()));

                        prevClick = view;
                    }
                });

                /* 롱 클릭 설정 */
                to.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        TextView tvMon = view.findViewById(R.id.tv_mon);
                        TextView tvDay = view.findViewById(R.id.tv_day);

                        Schedule sc = getSchedule(Integer.parseInt(tvMon.getText().toString()),
                                Integer.parseInt(tvDay.getText().toString()));
                        if (sc != null) {
                            Intent intent = new Intent(getApplicationContext(), Detail.class);
                            intent.putExtra("month", Integer.parseInt(tvMon.getText().toString()));
                            intent.putExtra("day", Integer.parseInt(tvDay.getText().toString()));
                            intent.putExtra("schedule", sc);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "일정이 없습니다", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });

                /* 상세일정 있으면 */
                Schedule sc = getSchedule(m, d);
                if (sc != null)
                    if (Integer.parseInt(sc.getStartTime().split(":")[0]) == i) // 설정된 (시작) 시간에
                        tvSchedule.setText(sc.getTitle()); // 출력

                line.addView(to);
            }
            timezone.addView(line);
        }
    }

    private void select(View view) {
        TextView tvMon = view.findViewById(R.id.tv_mon);
        TextView tvDay = view.findViewById(R.id.tv_day);
        selectionMon = Integer.parseInt(tvMon.getText().toString());
        selectionDay = Integer.parseInt(tvDay.getText().toString());

        if (prevClick != null) {
            prevClick.setBackgroundColor(Color.BLACK);
        }
        view.setBackground(getDrawable(R.drawable.border));
        prevClick = view;
    }

    private void scheduleDialog(int month, int day) {
        scheduleDialog(month, day, -1);
    }

    /** 상세일정 다이얼로그 출력 */
    @SuppressLint("DefaultLocale")
    private void scheduleDialog(int month, int day, int time) {
        final String fileName = "2020_" + month + "_" + day;

        final View dialogView = View.inflate(MainActivity.this, R.layout.dialog, null);
        final EditText etTitle = dialogView.findViewById(R.id.et_title);
        final TextView tvStartTime = dialogView.findViewById(R.id.tv_start_time);
        final TextView tvEndTime = dialogView.findViewById(R.id.tv_end_time);
        final EditText etLocation = dialogView.findViewById(R.id.et_location);
        final EditText etMemo = dialogView.findViewById(R.id.et_memo);

        /* 상세일정 */
        Schedule sc = getSchedule(fileName);
        if (sc != null) {
            /* 있으면 설정 */
            etTitle.setText(sc.getTitle());
            tvStartTime.setText(sc.getStartTime());
            tvEndTime.setText(sc.getEndTime());
            etLocation.setText(sc.getLocation());
            etMemo.setText(sc.getMemo());
        } else if (time != -1) {
            /* 없으면 (주간) 클릭한 시간대로 설정 */
            tvStartTime.setText(String.format("%02d:%02d", time, 0));
            tvEndTime.setText(String.format("%02d:%02d", time + 1, 0));
        } else {
            /* (월간) 기본 시간대로 설정 */
            tvStartTime.setText(String.format("%02d:%02d", 6, 0));
            tvEndTime.setText(String.format("%02d:%02d", 7, 0));
        }

        /* 다이얼로그 만들기 */
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this, R.style.AppAlertDialog);
        dlg.setTitle("2020년 " + month + "월 " + day + "일 상세일정 입력");
        dlg.setView(dialogView);

        /* 저장 버튼 설정 */
        dlg.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                    Schedule sc = new Schedule(etTitle.getText().toString(), tvStartTime.getText().toString(), tvEndTime.getText().toString()
                            , etLocation.getText().toString(), etMemo.getText().toString());

                    /* 상세일정 DTO -> bytes */
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutput out = new ObjectOutputStream(bos);
                    out.writeObject(sc);
                    byte[] bytes = bos.toByteArray();

                    fos.write(bytes);
                    fos.close();

                    Toast.makeText(getApplicationContext(), "저장 완료", Toast.LENGTH_SHORT).show();
                    ((TextView)prevClick.findViewById(R.id.tv_schedule)).setText(sc.getTitle());
                } catch (IOException e) {

                }
            }
        });

        /* 삭제 버튼 설정 */
        dlg.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /* 파일 있으면 삭제 */
                File f = new File(getFilesDir(), fileName);
                if(f.delete()) {
                    Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                    ((TextView)prevClick.findViewById(R.id.tv_schedule)).setText("");
                }
            }
        });

        /* 시작 시간 설정 */
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = Integer.parseInt(((TextView)view.findViewById(R.id.tv_start_time)).getText().toString().split(":")[0]);
                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        tvStartTime.setText(String.format("%02d:%02d", hour, min));
                        if (hour > Integer.parseInt(tvEndTime.getText().toString().split(":")[0]))
                            tvEndTime.setText(String.format("%02d:%02d", hour, min));
                    }
                }, hour, 0, false);
                tpd.show();
            }
        });

        /* 종료 시간 설정 */
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = Integer.parseInt(((TextView)view.findViewById(R.id.tv_end_time)).getText().toString().split(":")[0]);
                TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        tvEndTime.setText(String.format("%02d:%02d", hour, min));
                        if (hour < Integer.parseInt(tvStartTime.getText().toString().split(":")[0]))
                            tvStartTime.setText(String.format("%02d:%02d", hour, min));
                    }
                }, hour, 0, false);
                tpd.show();
            }
        });

        dlg.show();
    }

    private Schedule getSchedule(int month, int day) {
        return getSchedule("2020_" + month + "_" + day);
    }

    private Schedule getSchedule(String fileName) {
        Schedule sc = null;
        try {
            FileInputStream fis = openFileInput(fileName);
            byte[] txt = new byte[1024];
            fis.read(txt);

            /* bytes -> 상세일정 DTO */
            ByteArrayInputStream bis = new ByteArrayInputStream(txt);
            ObjectInput in = new ObjectInputStream(bis);
            sc = (Schedule) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return sc;
    }
}
