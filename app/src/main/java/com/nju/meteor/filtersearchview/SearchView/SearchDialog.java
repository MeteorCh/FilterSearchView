package com.nju.meteor.filtersearchview.SearchView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nju.meteor.filtersearchview.R;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

public class SearchDialog extends Dialog {
    private ArrayList<String> mPreferList;
    private SearchAdapter mSearchAdapter;
    private final int MAX_RECORE=10;
    private IStartSearchListener startSearchListener;
    private List<SearchRecord> mAllRecord;//数据库中存储的所有记录
    private Class recordType;//对应的记录类型，以区分是哪个界面的记录
    private EditText edtToolSearch;
    public interface IStartSearchListener{
        void startSearch(String filter);
    }


    public SearchDialog(@NonNull final Context context, IStartSearchListener listener, View.OnClickListener clickListener, Class recordType) {
        super(context, R.style.MaterialSearch);
        this.startSearchListener=listener;
        this.recordType=recordType;
        Activity activity=(Activity)context;
        View view =activity.getLayoutInflater().inflate(R.layout.widget_search_view_layout, null);
        ImageView imgToolBack = (ImageView) view.findViewById(R.id.img_tool_back);
        edtToolSearch = (EditText) view.findViewById(R.id.edt_tool_search);
        ImageView imgToolMic = (ImageView) view.findViewById(R.id.img_tool_mic);
        final ListView listSearch = (ListView) view.findViewById(R.id.list_search);
        final TextView txtEmpty = (TextView) view.findViewById(R.id.txt_empty);
        View outsideView=view.findViewById(R.id.outside);
        outsideView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        View statusBg=view.findViewById(R.id.status_bar_bg);
        statusBg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,UIUtility.getStatusBarHeight(getContext())));
        UIUtility.setListViewHeightBasedOnChildren(listSearch);
        setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//solves issue with statusbar
        }
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mPreferList = getPreferList();
        mSearchAdapter = new SearchAdapter(context, mPreferList, false);
        listSearch.setVisibility(View.VISIBLE);
        listSearch.setAdapter(mSearchAdapter);
        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String country = String.valueOf(adapterView.getItemAtPosition(position));
                edtToolSearch.setText(country);
                edtToolSearch.setSelection(country.length());//将光标移至文字末尾
                listSearch.setVisibility(View.GONE);
            }
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> filterList = new ArrayList<String>();
                boolean isNodata = false;
                if (s.length() > 0) {
                    imgToolMic.setVisibility(View.VISIBLE);
                    for (int i = 0; i < mPreferList.size(); i++) {
                        if (mPreferList.get(i).toLowerCase().startsWith(s.toString().trim().toLowerCase())) {
                            filterList.add(mPreferList.get(i));
                            listSearch.setVisibility(View.VISIBLE);
                            mSearchAdapter.updateList(filterList, true);
                            isNodata = true;
                        }
                    }
                    if (!isNodata) {
                        listSearch.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("没有结果");
                    }
                } else {
                    imgToolMic.setVisibility(View.GONE);
                    listSearch.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        imgToolMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtToolSearch.setText("");
                mSearchAdapter.updateList(mPreferList, false);
                listSearch.setVisibility(View.VISIBLE);
            }
        });
        edtToolSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){//搜索按键action
                    String content = edtToolSearch.getText().toString();
                    if (content==null)
                        content="";
                    if (content.length()>0&&mPreferList.indexOf(content)<0)
                    {
                        //将数据存储到数据库中
                        Date date=new Date();
                        SearchRecord searchRecord=new SearchRecord(content,date.getTime(),SearchDialog.this.recordType.getName());
                        searchRecord.save();
                        mAllRecord.add(0,searchRecord);
                        mPreferList.add(0,content);
                        //如果数据量超过了max_size，则删除时间最早的数据（因为是降序排列的，所以直接删除倒数第一个即可)
                        if (mPreferList.size()>MAX_RECORE){
                            mPreferList.remove(mPreferList.size()-1);
                            SearchRecord lastRecord=mAllRecord.get(mAllRecord.size()-1);
                            mAllRecord.remove(mAllRecord.size()-1);
                            lastRecord.delete();
                        }
                    }
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtToolSearch.getWindowToken(), 0);
                    if (SearchDialog.this.startSearchListener!=null) {
                        dismiss();
                        SearchDialog.this.startSearchListener.startSearch(content);
                    }
                    return true;
                }
                return false;
            }
        });
        //点击筛选按钮进行处理
        ImageButton filterButton=(ImageButton)view.findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onClick(view);
            }
        });
    }

    //获取在数据库中存储的搜索记录
    private ArrayList<String> getPreferList(){
        ArrayList<String> result=new ArrayList<>();
        mAllRecord=LitePal.where("classType=?",this.recordType.getName()).order("time desc").find(SearchRecord.class);
        if (mAllRecord!=null&&mAllRecord.size()>0){
            for (SearchRecord record:mAllRecord)
                result.add(record.getValue());
        }
        return result;
    }

}
