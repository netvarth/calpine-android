package com.jaldeeinc.jaldee.singlerowcalendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jaldeeinc.jaldee.R;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private static final String TAG = "TimelineAdapter";
    private static final String[] YEAR = DateFormatSymbols.getInstance().getEras();
    private static final String[] WEEK_DAYS = DateFormatSymbols.getInstance().getShortWeekdays();
    private static final String[] MONTH_NAME = DateFormatSymbols.getInstance().getShortMonths();

    private Calendar calendar = Calendar.getInstance();
    private TimelineView timelineView;
    private Date[] deactivatedDates;

    private OnDateSelectedListener listener;

    private View selectedView;
    private int selectedPosition;

    public TimelineAdapter(TimelineView timelineView, int selectedPosition) {
        this.timelineView = timelineView;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_item, parent, false);
        return new TimelineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        resetCalendar();
        calendar.add(Calendar.DAY_OF_YEAR, position);

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        final boolean isDisabled = holder.bind(month, day, dayOfWeek, year, position);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedView != null) {
                    selectedView.setBackground(timelineView.getResources().getDrawable(R.drawable.calendar_item_background));
                    TextView previouse_tv_date_calendar_item = selectedView.findViewById(R.id.tv_date_calendar_item);
                    TextView previouse_tv_day_calendar_item = selectedView.findViewById(R.id.tv_day_calendar_item);
                    previouse_tv_date_calendar_item.setTextColor(Color.parseColor("#252525"));
                    previouse_tv_day_calendar_item.setTextColor(Color.parseColor("#252525"));
                }
                if (!isDisabled) {
                    v.setBackground(timelineView.getResources().getDrawable(R.drawable.selected_calendar_item_background));
                    TextView selected_tv_date_calendar_item = v.findViewById(R.id.tv_date_calendar_item);
                    TextView selected_tv_day_calendar_item = v.findViewById(R.id.tv_day_calendar_item);
                    selected_tv_date_calendar_item.setTextColor(Color.parseColor("#ffffff"));
                    selected_tv_day_calendar_item.setTextColor(Color.parseColor("#ffffff"));

                    selectedPosition = position;
                    selectedView = v;

                    if (listener != null) listener.onDateSelected(year, month, day, dayOfWeek);
                } else {
                    if (listener != null)
                        listener.onDisabledDateSelected(year, month, day, dayOfWeek, isDisabled);
                }
            }
        });
    }

    private void resetCalendar() {
        calendar.set(timelineView.getYear(), timelineView.getMonth(), timelineView.getDate(),
                1, 0, 0);
    }

    /**
     * Set the position of selected date
     *
     * @param selectedPosition active date Position
     */
    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public void disableDates(Date[] dates) {
        this.deactivatedDates = dates;
        notifyDataSetChanged();
    }

    public void setDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date_calendar_item;
        TextView tv_day_calendar_item;
        /* private TextView monthView;
         private  TextView dateView;
         private  TextView dayView;
         private  TextView yearView;*/
        private View rootView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // using this method we can bind data to calendar view
            // good practice is if all views in layout have same IDs in all item views
            tv_date_calendar_item = itemView.findViewById(R.id.tv_date_calendar_item);
            tv_day_calendar_item = itemView.findViewById(R.id.tv_day_calendar_item);
            rootView = itemView.findViewById(R.id.cl_calendar_item);
           /* monthView = itemView.findViewById(R.id.monthView);
            dateView = itemView.findViewById(R.id.dateView);
            dayView = itemView.findViewById(R.id.dayView);
            yearView = itemView.findViewById(R  .id.yearView);
            rootView = itemView.findViewById(R.id.rootView);*/
        }

        boolean bind(int month, int day, int dayOfWeek, int year, int position) {


            tv_date_calendar_item.setText(String.valueOf(day));
            tv_day_calendar_item.setText(WEEK_DAYS[dayOfWeek].toUpperCase(Locale.US));
           /* monthView.setTextColor(timelineView.getMonthTextColor());
            dateView.setTextColor(timelineView.getDateTextColor());
            dayView.setTextColor(timelineView.getDayTextColor());
            yearView.setTextColor(timelineView.getDayTextColor());

            yearView.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            dayView.setText(WEEK_DAYS[dayOfWeek].toUpperCase(Locale.US));
            monthView.setText(MONTH_NAME[month].toUpperCase(Locale.US));
            dateView.setText(String.valueOf(day));*/

            if (selectedPosition == position) {
                rootView.setBackground(timelineView.getResources().getDrawable(R.drawable.selected_calendar_item_background));
                tv_date_calendar_item.setTextColor(Color.parseColor("#ffffff"));
                tv_day_calendar_item.setTextColor(Color.parseColor("#ffffff"));
                selectedView = rootView;
            } else {
                rootView.setBackground(timelineView.getResources().getDrawable(R.drawable.calendar_item_background));
                tv_date_calendar_item.setTextColor(Color.parseColor("#252525"));
                tv_day_calendar_item.setTextColor(Color.parseColor("#252525"));
            }

            for (Date date : deactivatedDates) {
                Calendar tempCalendar = Calendar.getInstance();
                tempCalendar.setTime(date);
                if (tempCalendar.get(Calendar.DAY_OF_MONTH) == day &&
                        tempCalendar.get(Calendar.MONTH) == month &&
                        tempCalendar.get(Calendar.YEAR) == year) {
                    tv_date_calendar_item.setTextColor(timelineView.getDisabledDateColor());
                    tv_day_calendar_item.setTextColor(timelineView.getDisabledDateColor());

                    rootView.setBackground(null);
                    return true;
                }
            }

            return false;
        }
    }

}