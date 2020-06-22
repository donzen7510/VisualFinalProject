package vp.usw.finalproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class MonthlyDay extends LinearLayout {

    public MonthlyDay(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public MonthlyDay(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.monthly_day_view, this, true);
    }
}
