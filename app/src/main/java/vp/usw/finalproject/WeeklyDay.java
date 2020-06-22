package vp.usw.finalproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class WeeklyDay extends LinearLayout {

    public WeeklyDay(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public WeeklyDay(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.weekly_day_view, this, true);
    }
}
