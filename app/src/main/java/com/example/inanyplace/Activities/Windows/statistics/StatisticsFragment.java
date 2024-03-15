package com.example.inanyplace.Activities.Windows.statistics;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.inanyplace.Model.Order;
import com.example.inanyplace.R;
import com.example.inanyplace.Utils.Utils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StatisticsFragment extends Fragment {

    private int lastWeekFirstDay;
    private int lastWeekSecondDay;
    private int lastWeekThirdDay;
    private int lastWeekFourthDay;
    private int lastWeekFifthDay;
    private int lastWeekSixthDay;
    private int lastWeekSeventhDay;
    private int firstDeliveryTimeInterval;
    private int secondDeliveryTimeInterval;
    private int thirdDeliveryTimeInterval;
    private int firstTimeRange;
    private int secondTimeRange;
    private int thirdTimeRange;
    private int firstOrderAmountInterval;
    private int secondOrderAmountInterval;
    private int thirdOrderAmountInterval;
    private int fourthOrderAmountInterval;
    private int averageDeliveryTime;
    private int averageOrderAmount;
    private int lastWeekNumberOfOrders;
    private Animation animationFromBottom;
    private Calendar calendar = Calendar.getInstance();
    private Unbinder unbinder;
    private List<Button> buttonsList;
    private List<String> last7Days;
    private AlertDialog dialog;

    @BindView(R.id.statistics_pie_chart)
    PieChart statisticsPieChart;

    @BindView(R.id.statistics_pie_chart_order_amount)
    PieChart statisticsPieChartOrderAmount;

    @BindView(R.id.statistics_horizontal_bar_chart)
    HorizontalBarChart statisticsHorizontalBarChart;

    @BindView(R.id.statistics_bar_chart)
    BarChart statisticsBarChart;

    @BindView(R.id.button_delivery_time)
    Button buttonDeliveryTime;

    @BindView(R.id.button_time_range)
    Button buttonTimeRange;

    @BindView(R.id.button_order_amount)
    Button buttonOrderAmount;

    @BindView(R.id.button_last_month)
    Button buttonLastMonth;

    @BindView(R.id.text_view_avg_delivery_time)
    TextView avgDeliveryTime;

    @BindView(R.id.text_view_avg_order_amount)
    TextView avgOrderAmount;

    @BindView(R.id.text_view_number_of_orders)
    TextView numberOfOrders;

    @BindView(R.id.text_view_most_orders)
    TextView mostOrders;

    @BindView(R.id.text_view_statistics)
    TextView textStatistics;

    @BindView(R.id.logo_statistics)
    ImageView logoStatitics;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInsanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        unbinder = ButterKnife.bind(this, root);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Please wait...").setCancelable(false);
        dialog = builder.create();

        initView();

        buttonDeliveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStatisticLogoAndText();
                resetHoverAllButtons();
                hoverButton(buttonDeliveryTime);
                deliveryTimeChart(true);
                timeRangeChart(false);
                ordersAmountChart(false);
                lastWeekChart(false);
            }
        });

        buttonTimeRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStatisticLogoAndText();
                resetHoverAllButtons();
                hoverButton(buttonTimeRange);
                deliveryTimeChart(false);
                timeRangeChart(true);
                ordersAmountChart(false);
                lastWeekChart(false);

            }
        });

        buttonOrderAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStatisticLogoAndText();
                resetHoverAllButtons();
                hoverButton(buttonOrderAmount);
                deliveryTimeChart(false);
                timeRangeChart(false);
                ordersAmountChart(true);
                lastWeekChart(false);
            }
        });

        buttonLastMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStatisticLogoAndText();
                resetHoverAllButtons();
                hoverButton(buttonLastMonth);
                deliveryTimeChart(false);
                timeRangeChart(false);
                ordersAmountChart(false);
                lastWeekChart(true);
            }
        });


        return root;
    }

    void initView() {
        dialog.show();
        new CountDownTimer(1500, 1000) {
            public void onTick(long millisUntilFinished) {
                //TODO
            }

            public void onFinish() {
                dialog.dismiss();
            }
        }.start();

        deliveryTimeStatistic();
        timeRangeStatistic();
        ordersAmountStatistic();
        lastWeekStatistic();

        buttonsList = new ArrayList<>();
        buttonsList.add(buttonDeliveryTime);
        buttonsList.add(buttonTimeRange);
        buttonsList.add(buttonOrderAmount);
        buttonsList.add(buttonLastMonth);

        last7Days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Date today = new Date();
            Calendar cal = new GregorianCalendar();
            cal.setTime(today);
            cal.add(Calendar.DAY_OF_MONTH, -i);
            last7Days.add(cal.get(Calendar.DAY_OF_MONTH)
                    + " "
                    + (Utils.getMonthOfYear(cal.get(Calendar.MONTH))));
        }
        Collections.reverse(last7Days);
    }

    void hideStatisticLogoAndText() {
        logoStatitics.setVisibility(View.GONE);
        textStatistics.setVisibility(View.GONE);
    }

    void hoverButton(Button button) {
        button.setBackgroundResource(R.drawable.button_round);
        button.setTextColor(Color.WHITE);
    }

    void resetHoverAllButtons() {
        for (Button button : buttonsList) {
            button.setBackgroundResource(R.drawable.button_frame);
            button.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_green));
        }
    }

    void ordersAmountStatistic() {
        FirebaseDatabase.getInstance()
                .getReference(Utils.ORDER_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        firstOrderAmountInterval = 0;
                        secondOrderAmountInterval = 0;
                        thirdOrderAmountInterval = 0;
                        fourthOrderAmountInterval = 0;
                        int orderAmountSum = 0;
                        int numberOfOrders = 0;
                        for (DataSnapshot orderDataSnapShot : snapshot.getChildren()) {
                            Order orderModel = orderDataSnapShot.getValue(Order.class);
                            if (orderModel.getTotalOrderPrice() >= 5 && orderModel.getTotalOrderPrice() <= 50)
                                firstOrderAmountInterval += 1;
                            else if (orderModel.getTotalOrderPrice() > 50 && orderModel.getTotalOrderPrice() <= 100)
                                secondOrderAmountInterval += 1;
                            else if (orderModel.getTotalOrderPrice() > 100 && orderModel.getTotalOrderPrice() <= 150)
                                thirdOrderAmountInterval += 1;
                            else if (orderModel.getTotalOrderPrice() > 150)
                                fourthOrderAmountInterval += 1;
                            orderAmountSum += orderModel.getTotalOrderPrice();
                            numberOfOrders += 1;
                        }
                        averageOrderAmount = orderAmountSum / numberOfOrders;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void deliveryTimeStatistic() {
        FirebaseDatabase.getInstance()
                .getReference(Utils.ORDER_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        firstDeliveryTimeInterval = 0;
                        secondDeliveryTimeInterval = 0;
                        thirdDeliveryTimeInterval = 0;
                        int deliveryTimeSum = 0;
                        int numberOfOrders = 0;
                        for (DataSnapshot orderDataSnapShot : snapshot.getChildren()) {
                            Order orderModel = orderDataSnapShot.getValue(Order.class);
                            if (orderModel.getDeliveryTime() >= 10 && orderModel.getDeliveryTime() <= 25)
                                firstDeliveryTimeInterval += 1;
                            else if (orderModel.getDeliveryTime() > 25 && orderModel.getDeliveryTime() <= 45)
                                secondDeliveryTimeInterval += 1;
                            else if (orderModel.getDeliveryTime() > 45 && orderModel.getDeliveryTime() <= 60)
                                thirdDeliveryTimeInterval += 1;
                            deliveryTimeSum += orderModel.getDeliveryTime();
                            numberOfOrders += 1;
                        }
                        averageDeliveryTime = deliveryTimeSum / numberOfOrders;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void timeRangeStatistic() {
        FirebaseDatabase.getInstance()
                .getReference(Utils.ORDER_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        firstTimeRange = 0;
                        secondTimeRange = 0;
                        thirdTimeRange = 0;
                        for (DataSnapshot orderDataSnapShot : snapshot.getChildren()) {
                            Order orderModel = orderDataSnapShot.getValue(Order.class);
                            calendar.setTimeInMillis(orderModel.getCreateDate());
                            if (calendar.get(Calendar.HOUR_OF_DAY) >= 9 && calendar.get(Calendar.HOUR_OF_DAY) < 14)
                                firstTimeRange += 1;
                            else if (calendar.get(Calendar.HOUR_OF_DAY) >= 14 && calendar.get(Calendar.HOUR_OF_DAY) < 19)
                                secondTimeRange += 1;
                            else if (calendar.get(Calendar.HOUR_OF_DAY) >= 19 && calendar.get(Calendar.HOUR_OF_DAY) <= 24)
                                thirdTimeRange += 1;
                        }

                        int maxTimeRange = Math.max(firstTimeRange, Math.max(secondTimeRange, thirdTimeRange));
                        if (maxTimeRange == firstTimeRange) {
                            mostOrders.setText(new StringBuilder().append("Most orders: 09:00 - 14:00."));
                        } else if (maxTimeRange == secondTimeRange) {
                            mostOrders.setText(new StringBuilder().append("Most orders: 14:00 - 19:00."));
                        } else if (maxTimeRange == thirdTimeRange) {
                            mostOrders.setText(new StringBuilder().append("Most orders: 19:00 - 00:00."));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void lastWeekStatistic() {
        FirebaseDatabase.getInstance()
                .getReference(Utils.ORDER_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lastWeekNumberOfOrders = 0;
                        lastWeekFirstDay = 0;
                        lastWeekSecondDay = 0;
                        lastWeekThirdDay = 0;
                        lastWeekFourthDay = 0;
                        lastWeekFifthDay = 0;
                        lastWeekSixthDay = 0;
                        lastWeekSeventhDay = 0;
                        Date today = new Date();
                        Calendar calendarTemp = new GregorianCalendar();
                        for (int i = 0; i < 7; i++) {
                            calendarTemp.setTime(today);
                            calendarTemp.add(Calendar.DAY_OF_MONTH, -i);
                            for (DataSnapshot orderDataSnapShot : snapshot.getChildren()) {
                                Order orderModel = orderDataSnapShot.getValue(Order.class);
                                calendar.setTimeInMillis(orderModel.getCreateDate());
                                if (calendarTemp.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
                                        && calendarTemp.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                                        && calendarTemp.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                                    switch (i) {
                                        case 0:
                                            lastWeekSeventhDay += 1;
                                            break;
                                        case 1:
                                            lastWeekSixthDay += 1;
                                            break;
                                        case 2:
                                            lastWeekFifthDay += 1;
                                            break;
                                        case 3:
                                            lastWeekFourthDay += 1;
                                            break;
                                        case 4:
                                            lastWeekThirdDay += 1;
                                            break;
                                        case 5:
                                            lastWeekSecondDay += 1;
                                            break;
                                        case 6:
                                            lastWeekFirstDay += 1;
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                        lastWeekNumberOfOrders = lastWeekFirstDay + lastWeekSecondDay + lastWeekThirdDay
                                + lastWeekFourthDay + lastWeekFifthDay + lastWeekSixthDay + lastWeekSeventhDay;
                        numberOfOrders.setText(new StringBuilder().append("Last week: "
                                + lastWeekNumberOfOrders + " orders."));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void timeRangeChart(boolean status) {
        if (status != false) {
            statisticsHorizontalBarChart.setVisibility(View.VISIBLE);
            mostOrders.setVisibility(View.VISIBLE);

            List<BarEntry> value = new ArrayList<>();
            value.add(new BarEntry(0, firstTimeRange));
            value.add(new BarEntry(1, secondTimeRange));
            value.add(new BarEntry(2, thirdTimeRange));

            BarDataSet barDataSet = new BarDataSet(value, "Orders");


            ArrayList<Integer> colors = new ArrayList<Integer>();
            colors.add(Color.rgb(1, 174, 188));
            colors.add(Color.rgb(252, 78, 9));
            colors.add(Color.rgb(230, 184, 0));


            barDataSet.setColors(colors);
            barDataSet.setValueTextSize(14f);
            barDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.black));
            barDataSet.notifyDataSetChanged();

            Description description = new Description();
            description.setText("");

            BarData data = new BarData(barDataSet);
            statisticsHorizontalBarChart.getXAxis().setValueFormatter(new HorizontalBarAxisXFormater());
            statisticsHorizontalBarChart.getXAxis().setGranularity(1f);
            statisticsHorizontalBarChart.getAxisLeft().setGranularity(1f);
            statisticsHorizontalBarChart.getAxisRight().setGranularity(1f);
            statisticsHorizontalBarChart.getLegend().setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            statisticsHorizontalBarChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            statisticsHorizontalBarChart.getLegend().setTextSize(18f);
            statisticsHorizontalBarChart.getLegend().setFormSize(18f);
            statisticsHorizontalBarChart.getXAxis().setTextSize(16f);
            statisticsHorizontalBarChart.getAxisRight().setTextSize(16f);
            statisticsHorizontalBarChart.getAxisLeft().setTextSize(16f);
            statisticsHorizontalBarChart.setDescription(description);
            statisticsHorizontalBarChart.animateXY(1000, 1000);
            statisticsHorizontalBarChart.setData(data);
            statisticsHorizontalBarChart.notifyDataSetChanged();
            statisticsHorizontalBarChart.invalidate();

        } else {
            statisticsHorizontalBarChart.setVisibility(View.GONE);
            mostOrders.setVisibility(View.GONE);
        }
    }

    void lastWeekChart(boolean status) {
        if (status != false) {
            statisticsBarChart.setVisibility(View.VISIBLE);
            numberOfOrders.setVisibility(View.VISIBLE);

            List<BarEntry> value = new ArrayList<>();
            value.add(new BarEntry(0, lastWeekFirstDay));
            value.add(new BarEntry(1, lastWeekSecondDay));
            value.add(new BarEntry(2, lastWeekThirdDay));
            value.add(new BarEntry(3, lastWeekFourthDay));
            value.add(new BarEntry(4, lastWeekFifthDay));
            value.add(new BarEntry(5, lastWeekSixthDay));
            value.add(new BarEntry(6, lastWeekSeventhDay));


            BarDataSet barDataSet = new BarDataSet(value, "Orders");
            barDataSet.notifyDataSetChanged();

            ArrayList<Integer> colors = new ArrayList<Integer>();
            colors.add(Color.rgb(246, 0, 0));
            colors.add(Color.rgb(255, 128, 2));
            colors.add(Color.rgb(34, 140, 34));
            colors.add(Color.rgb(255, 249, 12));
            colors.add(Color.rgb(19, 56, 190));
            colors.add(Color.rgb(112, 0, 131));
            colors.add(Color.rgb(228, 6, 147));

            barDataSet.setColors(colors);
            barDataSet.setValueTextSize(14f);

            Description description = new Description();
            description.setText("");

            BarData data = new BarData(barDataSet);
            statisticsBarChart.getXAxis().setValueFormatter(new BarAxisXFormater());
            statisticsBarChart.getXAxis().setGranularity(1f);
            statisticsBarChart.getLegend().setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            statisticsBarChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            statisticsBarChart.getLegend().setTextSize(18f);
            statisticsBarChart.getLegend().setFormSize(18f);
            statisticsBarChart.setExtraTopOffset(2f);
            statisticsBarChart.getXAxis().setTextSize(14f);
            statisticsBarChart.getAxisRight().setTextSize(14f);
            statisticsBarChart.getAxisLeft().setTextSize(14f);
            statisticsBarChart.setDescription(description);
            statisticsBarChart.animateXY(1000, 1000);
            statisticsBarChart.setData(data);
            statisticsBarChart.notifyDataSetChanged();
            statisticsBarChart.invalidate();

        } else {
            statisticsBarChart.setVisibility(View.GONE);
            numberOfOrders.setVisibility(View.GONE);
        }
    }

    void deliveryTimeChart(boolean status) {
        if (status != false) {

            statisticsPieChart.setVisibility(View.VISIBLE);
            avgDeliveryTime.setVisibility(View.VISIBLE);
            avgDeliveryTime.setText(new StringBuilder().append("Average delivery time: " + averageDeliveryTime + " minutes."));

            statisticsPieChart.setUsePercentValues(false);
            statisticsPieChart.setEntryLabelTextSize(18f);
            statisticsPieChart.setHoleRadius(35f);
            statisticsPieChart.setTransparentCircleRadius(42f);
            statisticsPieChart.animateXY(1500, 1500);

            List<PieEntry> value = new ArrayList<>();
            value.add(new PieEntry(firstDeliveryTimeInterval, "10 - 25 min"));
            value.add(new PieEntry(secondDeliveryTimeInterval, "25 - 45 min"));
            value.add(new PieEntry(thirdDeliveryTimeInterval, "45 - 60 min"));


            PieDataSet pieDataSet = new PieDataSet(value, "");
            pieDataSet.notifyDataSetChanged();
            pieDataSet.setValueTextSize(24f);
            pieDataSet.setValueTextColor(Color.WHITE);

            ArrayList<Integer> colors = new ArrayList<Integer>();
            colors.add(Color.rgb(226, 26, 55));
            colors.add(Color.rgb(248, 147, 33));
            colors.add(Color.rgb(228, 6, 147));


            pieDataSet.setColors(colors);

            PieData pieData = new PieData(pieDataSet);

            Description description = new Description();
            description.setText("");

            statisticsPieChart.setData(pieData);
            statisticsPieChart.setEntryLabelColor(Color.WHITE);
            statisticsPieChart.setDescription(description);
            statisticsPieChart.getLegend().setTextSize(18f);
            statisticsPieChart.getLegend().setFormSize(14f);
            statisticsPieChart.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
            statisticsPieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            statisticsPieChart.getLegend().setYOffset(30f);
            statisticsPieChart.notifyDataSetChanged();
            statisticsPieChart.invalidate();
        } else {
            statisticsPieChart.setVisibility(View.GONE);
            avgDeliveryTime.setVisibility(View.GONE);
        }
    }

    void ordersAmountChart(boolean status) {
        if (status != false) {

            statisticsPieChartOrderAmount.setVisibility(View.VISIBLE);
            avgOrderAmount.setVisibility(View.VISIBLE);
            avgOrderAmount.setText(new StringBuilder().append("Average order amount: " + averageOrderAmount + " Lei."));

            statisticsPieChartOrderAmount.setUsePercentValues(false);
            statisticsPieChartOrderAmount.setEntryLabelTextSize(18f);
            statisticsPieChartOrderAmount.setHoleRadius(35f);
            statisticsPieChartOrderAmount.setTransparentCircleRadius(42f);
            statisticsPieChartOrderAmount.animateXY(1500, 1500);

            List<PieEntry> value = new ArrayList<>();
            value.add(new PieEntry(firstOrderAmountInterval, "5 - 50 Lei"));
            value.add(new PieEntry(secondOrderAmountInterval, "50 - 100 Lei"));
            value.add(new PieEntry(thirdOrderAmountInterval, "100 - 150 Lei"));
            value.add(new PieEntry(fourthOrderAmountInterval, ">150 Lei"));


            PieDataSet pieDataSet = new PieDataSet(value, "");
            pieDataSet.notifyDataSetChanged();
            pieDataSet.setValueTextSize(24f);
            pieDataSet.setValueTextColor(Color.WHITE);

            ArrayList<Integer> colors = new ArrayList<Integer>();
            colors.add(Color.rgb(134, 195, 65));
            colors.add(Color.rgb(0, 173, 239));
            colors.add(Color.rgb(247, 143, 30));
            colors.add(Color.rgb(229, 24, 55));

            pieDataSet.setColors(colors);

            PieData pieData = new PieData(pieDataSet);

            Description description = new Description();
            description.setText("");

            statisticsPieChartOrderAmount.setData(pieData);
            statisticsPieChartOrderAmount.setEntryLabelColor(Color.WHITE);
            statisticsPieChartOrderAmount.setDescription(description);
            statisticsPieChartOrderAmount.getLegend().setTextSize(18f);
            statisticsPieChartOrderAmount.getLegend().setFormSize(14f);
            statisticsPieChartOrderAmount.getLegend().setOrientation(Legend.LegendOrientation.HORIZONTAL);
            statisticsPieChartOrderAmount.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            statisticsPieChartOrderAmount.getLegend().setYOffset(30f);
            statisticsPieChartOrderAmount.notifyDataSetChanged();
            statisticsPieChartOrderAmount.invalidate();
        } else {
            statisticsPieChartOrderAmount.setVisibility(View.GONE);
            avgOrderAmount.setVisibility(View.GONE);
        }
    }

    private class HorizontalBarAxisXFormater implements IAxisValueFormatter {
        private String[] values = new String[]{"09:00 - 14:00", "14:00 - 19:00", "19:00 - 00:00"};

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return values[(int) value];
        }
    }

    private class BarAxisXFormater implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return last7Days.get((int) value);
        }
    }
}
