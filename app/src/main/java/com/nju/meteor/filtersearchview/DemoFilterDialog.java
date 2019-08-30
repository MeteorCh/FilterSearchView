package com.nju.meteor.filtersearchview;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;


import com.nju.meteor.filtersearchview.SearchView.BaseBelowToolbarDialog;
import com.nju.meteor.filtersearchview.SearchView.UIUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DemoFilterDialog extends BaseBelowToolbarDialog {
    TextView startDate;
    TextView endDate;
    Spinner objectType;
    Spinner filterType;
    Spinner isSubmitType;//是否打分
    List<String> inspectTypeList;
    List<String> objectTypes;

    public static final String INSPECT_TYPE="InspectType";
    public static final String SCORE_STATUS="IsReview";
    public static final String OBJECT_TYPE="ObjectType";
    public static final String START_TIME="StartTime";
    public static final String END_TIME="EndTime";

    public DemoFilterDialog(Context context, int toolBarHeight, HashMap<String,Object> initFilter, IFilterListener listener) {
        super(context, R.layout.demo_filter_layout, toolBarHeight);
        filterListener=listener;
        if (inspectTypeList ==null){
            inspectTypeList =new ArrayList<>();
            String[] inspectTypeArray =mContext.getResources().getStringArray(R.array.inspectType);
            for (String str:inspectTypeArray)
                inspectTypeList.add(str);
        }
        if (initFilter!=null)
            this.searchFilter=(HashMap<String, Object>) initFilter.clone();
        else
            searchFilter=new HashMap<>();
        initView();
        updateViewFromFilter();
    }

    private void initView(){
        startDate=(TextView)findViewById(R.id.mrf_start_date);
        endDate=(TextView)findViewById(R.id.mrf_end_date);
        objectType=(Spinner)findViewById(R.id.mrf_object_type);
        filterType =(Spinner)findViewById(R.id.mrf_maintain_type);
        isSubmitType =(Spinner)findViewById(R.id.mrf_submit_status);

        objectTypes=new ArrayList<>();
        //设置对象类型
        objectTypes.add("对象类型");
        for (int i=0;i<3;i++)
            objectTypes.add("类型"+i);
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(mContext,R.layout.widget_spinner_item,objectTypes);
        objectType.setAdapter(adapter);
        objectType.getBackground().setColorFilter(mContext.getResources().getColor(R.color.deepGrayColor), PorterDuff.Mode.SRC_ATOP);
        objectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                if (pos>0)
                {
                    searchFilter.put(OBJECT_TYPE,objectTypes.get(pos));
                }
                else
                {
                    searchFilter.put(OBJECT_TYPE,"");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //设置筛选类型
        ArrayAdapter<String> maintainAdapter =new ArrayAdapter<String>(mContext,R.layout.widget_spinner_item, inspectTypeList);
        filterType.setAdapter(maintainAdapter);
        filterType.getBackground().setColorFilter(mContext.getResources().getColor(R.color.deepGrayColor), PorterDuff.Mode.SRC_ATOP);
        filterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                TextView tv = (TextView) view;
                if (pos>0)
                    searchFilter.put(INSPECT_TYPE,tv.getText());
                else
                    searchFilter.put(INSPECT_TYPE,"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //设置是否打分
        String[] submitStatusArray=mContext.getResources().getStringArray(R.array.scoreStatus);
        List<String> submitList=Arrays.asList(submitStatusArray);
        ArrayAdapter<String> submitAdapter =new ArrayAdapter<String>(mContext,R.layout.widget_spinner_item,submitList);
        isSubmitType.setAdapter(submitAdapter);
        isSubmitType.getBackground().setColorFilter(mContext.getResources().getColor(R.color.deepGrayColor), PorterDuff.Mode.SRC_ATOP);
        isSubmitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                TextView tv = (TextView) view;
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                if (pos==0)
                    searchFilter.put(SCORE_STATUS,"");
                else if (pos==1)
                    searchFilter.put(SCORE_STATUS,"是");
                else
                    searchFilter.put(SCORE_STATUS,"否");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        startDate.setOnClickListener(view->onStartTimeClick());
        endDate.setOnClickListener(view -> onEndTimeClick());
        Button okButton=findViewById(R.id.ok_button);
        okButton.setOnClickListener(view -> onOkClick());
        Button clearButton=findViewById(R.id.clear_button);
        clearButton.setOnClickListener(view -> onClearClick());
    }

    void onStartTimeClick(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String endDataStr=endDate.getText().toString();
                boolean flag=false;
                if (endDataStr!=null&&endDataStr.length()>0){
                    Date date=UIUtility.string2Date(endDataStr);
                    Date newData=UIUtility.string2Date(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                    if (date.getTime()>=newData.getTime())
                        flag=true;
                    else
                        UIUtility.showToast(mContext,"开始时间大于结束时间，请重新输入");
                }else
                    flag=true;
                if (flag)
                {
                    String date=year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                    startDate.setText(date);
                    searchFilter.put(START_TIME,date);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker dp =datePickerDialog.getDatePicker();
        dp.setMaxDate(new Date().getTime());
        Date minDate=UIUtility.string2Date("1990-01-01");
        dp.setMinDate(minDate.getTime());
        datePickerDialog.show();
    }

    void onEndTimeClick(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String startDataStr=startDate.getText().toString();
                boolean flag=false;
                if (startDataStr!=null&&startDataStr.length()>0){
                    Date date=UIUtility.string2Date(startDataStr);
                    Date newData=UIUtility.string2Date(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                    if (date.getTime()<=newData.getTime())
                        flag=true;
                    else
                        UIUtility.showToast(mContext,"开始时间大于结束时间，请重新输入");
                }else
                    flag=true;
                if (flag)
                {
                    String date=year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                    endDate.setText(date);
                    date+=" 23:59:59";
                    searchFilter.put(END_TIME,date);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker dp =datePickerDialog.getDatePicker();
        dp.setMaxDate(new Date().getTime());
        Date minDate=UIUtility.string2Date("1990-01-01");
        dp.setMinDate(minDate.getTime());
        datePickerDialog.show();
    }

    void onOkClick(){
        Log.d("打印", "筛选条件"+searchFilter);
        if (filterListener!=null)
            filterListener.onFilter(searchFilter);
        dismiss();
    }

    void onClearClick(){
        searchFilter.clear();
        objectType.setSelection(0);
        filterType.setSelection(0);
        startDate.setText("");
        endDate.setText("");
        isSubmitType.setSelection(0);
        if (filterListener!=null)
            filterListener.onFilter(searchFilter);
    }

    /**
     * 根据以前的filter来恢复界面显示
     */
    @Override
    protected void updateViewFromFilter() {
        super.updateViewFromFilter();
        //设置开始时间
        String startTime=(String)searchFilter.get(START_TIME);
        if (startTime!=null&&startTime.length()>0)
            this.startDate.setText(startTime);
        else
            this.startDate.setText("");
        //设置结束时间
        String endTime=(String)searchFilter.get(END_TIME);
        if (endTime!=null&&endTime.length()>0)
            this.endDate.setText(startTime);
        else
            this.endDate.setText("");
        //设置巡查类型
        String maintainType=(String)searchFilter.get(INSPECT_TYPE);
        if (maintainType!=null&&maintainType.length()>0){
            int inspectIndex= inspectTypeList.indexOf(maintainType);
            if (inspectIndex!=-1)
                filterType.setSelection(inspectIndex);
        }
        //设置对象类型
        String type=(String) searchFilter.get(OBJECT_TYPE);
        if (type!=null&&type.length()>0)
        {
            int index=objectTypes.indexOf(type);
            if (index!=-1)
                objectType.setSelection(index+1);
            else
                objectType.setSelection(0);
        }else
            objectType.setSelection(0);
        //设置是否提交
        String isSubmit=(String)searchFilter.get(SCORE_STATUS);
        if (isSubmit!=null&&isSubmit.length()>0){
            if (isSubmit.equals("是"))
                isSubmitType.setSelection(1);
            else if (isSubmit.equals("否"))
                isSubmitType.setSelection(2);
            else
                isSubmitType.setSelection(0);
        }
    }
}
