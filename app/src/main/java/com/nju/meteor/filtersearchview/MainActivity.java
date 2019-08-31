package com.nju.meteor.filtersearchview;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nju.meteor.filtersearchview.SearchView.BaseBelowToolbarDialog;
import com.nju.meteor.filtersearchview.SearchView.SearchDialog;
import com.nju.meteor.filtersearchview.SearchView.UIUtility;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private MenuItem searchMenu;
    private HashMap<String,Object> searchFilter =new HashMap<>();
    public static final String FILTER_PATTERN="FilterPattern";
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        UIUtility.setStatusbarColor(this,getColor(R.color.color_ededed),true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchMenu=menu.findItem(R.id.action_search);
        return true;
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        toolbar=(Toolbar)findViewById(R.id.toolbar_simple);
        toolbar.setTitle("记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==R.id.action_search){
            SearchDialog dialog=new SearchDialog(this, new SearchDialog.IStartSearchListener() {
                @Override
                public void startSearch(String filter) {
                    MainActivity.this.searchFilter.put(FILTER_PATTERN,filter);
                    UIUtility.showToast(MainActivity.this,"开始搜索，筛选条件为："+ searchFilter.toString());
                    final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("正在查询，请稍后");
                    progressDialog.show();
                    //此处写根据搜索条件请求的逻辑
                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //模仿延时操作
                            for (int i=0;i<1000000000;i++){
                            }
                            //延时操作结束后，取消显示ProgressDialog
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    UIUtility.showToast(MainActivity.this,"请求结束");
                                }
                            });
                        }
                    });
                    thread.start();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DemoFilterDialog filterDialog=new DemoFilterDialog(MainActivity.this,
                            UIUtility.dip2px(MainActivity.this, 54),
                            MainActivity.this.searchFilter,
                            new BaseBelowToolbarDialog.IFilterListener() {
                                @Override
                                public void onFilter(HashMap<String, Object> searchFilter) {
                                    MainActivity.this.searchFilter =(HashMap<String, Object>) searchFilter.clone();
                                }
                            });
                    filterDialog.show();
                }
            },MainActivity.class);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
