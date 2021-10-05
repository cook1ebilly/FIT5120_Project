package com.example.fit5120_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.veken.chartview.bean.ChartBean;
import com.veken.chartview.bean.PieChartBean;
import com.veken.chartview.drawtype.DrawBgType;
import com.veken.chartview.drawtype.DrawConnectLineType;
import com.veken.chartview.drawtype.DrawLineType;
import com.veken.chartview.view.BarChartView;
import com.veken.chartview.view.LineChartView;
import com.veken.chartview.view.PieChartView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import database.MyContext;

public class Statistics extends AppCompatActivity {
    LineChartView lineChartView;
   ImageView plot;
   TextView tv;
    List<ChartBean> lineChartBeanList = new ArrayList<>();

    TextView offence, tv1, tv2;

    int index = 0;
    Button change;
    int pageIndex = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ImageView imageView = findViewById(R.id.back_2);
        change = findViewById(R.id.change);
        plot=findViewById(R.id.plot);
        offence = findViewById(R.id.offence);
        // TODO: 2021/10/4   tv1 ,tv2 根据不同sheet页 显示不同的文字
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        initData();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Statistics.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (pageIndex) {
                    case 1:
                        for (int i = 0; i < MyContext.offences.get(index).year.length; i++) {
                            ChartBean lineChartBean = new ChartBean();
                            Offence offence = MyContext.offences.get(index);
                            lineChartBean.setValue(String.valueOf(offence.data()[i]));
                            lineChartBean.setDate(String.valueOf(offence.year[i]));
                            lineChartBeanList.add(lineChartBean);
                        }
                        index++;
                        if (index == 12) {
                            pageIndex = 2;
                        }
                        lineChartBeanList.clear();
                        tv2.setText("Sexual assault had the highest in 2011 at 72.60 and was 36,200 % higher than Manslaughter, which had the lowest 2011 at 0.20%. 2011 and 2012 are positively correlated with each other.FSexual assault accounted for 92.96% of 2011.Attempted Murder in 2017 is the major contributor with 18.03%. In 2017 the criminal activities have shown an significant uptrend across all consecutive years.");

                        lineChartView.setVisibility(View.VISIBLE);
                        plot.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        lineChartView.setVisibility(View.INVISIBLE);
                        plot.setImageResource(R.mipmap.b1);
                        offence.setVisibility(View.INVISIBLE);
                        tv1.setText("Total count and Affected count by Age Group and Gender");
                        tv2.setText("We can see from the graph that Total Count was higher for Males (62) than Females (38). Also, we can see that the average total count across all age group was higher for Males (15.50) than Females (9.50). Total Count for Males and Females diverged the most when the Age Group was 35–54 years, when Males were 18 points higher than Females.");
                        plot.setVisibility(View.VISIBLE);

                        pageIndex++;
                        break;
                    case 3:
                        lineChartView.setVisibility(View.INVISIBLE);
                        plot.setImageResource(R.mipmap.b2);
                        offence.setVisibility(View.INVISIBLE);

                        tv1.setText("Total Count and Affected Count by Location");
                        tv2.setText("At 3991, Residential(c) had the highest Total Count and was 4,435.23% higher than Outbuilding/residential land, which had the lowest Total Count at 88.Total Count and total Affected Count are positively correlated with each other.Residential(c) accounted for 37.03% of Total Count.Total Count and Affected Count diverged the most when the Location was Residential(c), when Total Count were 3919 higher than Affected Count.");
                        plot.setVisibility(View.VISIBLE);
                        pageIndex++;
                        break;
                    case 4:
                        lineChartView.setVisibility(View.INVISIBLE);
                        plot.setImageResource(R.mipmap.b3);
                        offence.setVisibility(View.INVISIBLE);

                        tv1.setText("Affected Count by Age Group and Gender");
                        tv2.setText("Total Count was higher for Females (4721) than Males (721).15–19 years in Gender made up 19.29% of Count.Average Count was higher for Females (524.56) than Males (80.11).Count for Females and Males diverged the most when the Age Group was 15–19 years, when Females were 946 points higher than Males.");
                        plot.setVisibility(View.VISIBLE);
                        pageIndex++;
                        break;
                    case 5:
                        lineChartView.setVisibility(View.INVISIBLE);
                        plot.setImageResource(R.mipmap.b4);
                        offence.setVisibility(View.INVISIBLE);

                        tv1.setText("Affected count and Total Count by name of weapon");
                        tv2.setText("Weapon used has a positive correlation with the Total Count of the incidents.Weapon used accounted for 45.06% of Affected Count.Knife is most common used weapon with an affected count of 108.80 out of total 544. Syringe is the least used weapon with affected count of 0.4 points out of total 10 points.");
                        plot.setVisibility(View.VISIBLE);
                        pageIndex++;
                        break;
                    case 6:
                        lineChartView.setVisibility(View.INVISIBLE);
                        plot.setImageResource(R.mipmap.b5);
                        offence.setVisibility(View.INVISIBLE);

                        tv1.setText("Affected count by Activity");
                        tv2.setText("Apart from other types of theft, sexual assault accounts for a quarter of all criminal activity on the streets (25 percent). Attempted murders have the lowest level of activity. Kidnappings and abductions contributes 0.7% to the problem. With 9.4 percent of the total, robbery is the third most common cause of activity.");
                        plot.setVisibility(View.VISIBLE);
                        pageIndex = 0;
                        change.setText("Finish");
                        break;
                    case 0:
                        startActivity(new Intent(Statistics.this, MainActivity.class));
                        finish();
                        break;
                }
                draw();
            }
        });
        initView();
        draw();

    }

    void draw() {

        /*折线----------------------------------------------------------------------------*/
        for (int i = 0; i < MyContext.offences.get(index).year.length; i++) {
            ChartBean lineChartBean = new ChartBean();
            Offence offence = MyContext.offences.get(index);
            lineChartBean.setValue(String.valueOf(offence.data()[i]));
            lineChartBean.setDate(String.valueOf(offence.year[i]));
            lineChartBeanList.add(lineChartBean);
        }
        if (index == 12) {
            index = 0;
        }
        offence.setText(MyContext.offences.get(index).name);
        lineChartView.setData(lineChartBeanList);
        /*饼----------------------------------------------------------------------------*/


    }

    void initView() {
        {
            lineChartView = findViewById(R.id.linechart_view);
            lineChartView.setyLableText("");
//设置点击背景（可以为图片，也可以为一个颜色背景，大小根据textAndClickBgMargin设置）
            lineChartView.setDrawBgType(DrawBgType.DrawBitmap);
//设置图片资源
            lineChartView.setShowPicResource(R.mipmap.click_icon);
//连接线为虚线（也可以为实现）
            lineChartView.setDrawConnectLineType(DrawConnectLineType.DrawDottedLine);
            lineChartView.setClickable(true);
//是否需要画连接线
            lineChartView.setNeedDrawConnectYDataLine(true);
//连接线的颜色
            lineChartView.setConnectLineColor(getResources().getColor(R.color.default_color));
//是否需要背景
            lineChartView.setNeedBg(true);
            lineChartView.setDrawLineType(DrawLineType.Draw_Curve);
            lineChartView.setDefaultTextSize(24);
        }

    }

    void initData() {
        MyContext.offences = getList_offences("crmie.csv", this);
/*        MyContext.agegroups = getList_agegroup("agegroup.csv", this);
        MyContext.locationBeans = getList_location("location.csv", this);
        MyContext.weapons = getList_weapon("weapon.csv", this);
        MyContext.ageGroup2s = getList_agegroup2("age.csv", this);*/


    }

    public List<AgeGroup2> getList_agegroup2(String fileName, Context context) {
        List<AgeGroup2> csvList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName), "UTF-8"));//换成你的文件名
            reader.readLine();
            String line = "";

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                AgeGroup2 agegroup = new AgeGroup2();

                agegroup.agegroup = item[0];
                agegroup.gender = item[1];
                agegroup.totalcount = item[2];

                csvList.add(agegroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getList: agegroup2" + csvList.size());
        return csvList;
    }

    public List<Weapon> getList_weapon(String fileName, Context context) {
        List<Weapon> csvList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName), "UTF-8"));//换成你的文件名
            reader.readLine();
            String line = "";

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                Weapon weapon = new Weapon();

                weapon.weapon = item[0];
                weapon.count = item[1];

                csvList.add(weapon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getList:weapon " + csvList.size());
        return csvList;
    }

    public List<LocationBean> getList_location(String fileName, Context context) {
        List<LocationBean> csvList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName), "UTF-8"));//换成你的文件名
            reader.readLine();
            String line = "";

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                LocationBean locationBean = new LocationBean();

                locationBean.location = item[0];
                locationBean.count = item[1];

                csvList.add(locationBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getList:location " + csvList.size());
        return csvList;
    }

    public List<Agegroup> getList_agegroup(String fileName, Context context) {
        List<Agegroup> csvList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName), "UTF-8"));//换成你的文件名
            reader.readLine();
            String line = "";

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                Agegroup agegroup = new Agegroup();

                agegroup.agegroup = item[0];
                agegroup.gender = item[1];
                agegroup.totalcount = item[2];

                csvList.add(agegroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getList: agegroup" + csvList.size());
        return csvList;
    }

    public List<Offence> getList_offences(String fileName, Context context) {
        List<Offence> csvList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(fileName), "UTF-8"));//换成你的文件名
            reader.readLine();
            String line = "";

            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");
                Offence offence = new Offence();

                offence.name = item[0];
                offence.n11 = item[1];
                offence.n12 = item[2];
                offence.n13 = item[3];
                offence.n14 = item[4];
                offence.n15 = item[5];
                offence.n16 = item[6];
                offence.n17 = item[7];
                offence.n18 = item[8];
                offence.n19 = item[9];
                offence.n20 = item[10];
                csvList.add(offence);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getList:offence " + csvList.size());
        return csvList;
    }
}