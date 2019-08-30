package com.nju.meteor.filtersearchview.SearchView;
/**
 * 此类是弹出的在toolbar下方的一个popwindow的基类，用作筛选条件
 * 注意：
 *      ①内容布局部分，必须是一个LinearLayout，ID为content_layout，以便自动计算内容高度，使之位于toolbar的下方
 *      ②想要遮罩效果，则需要再布局中加一个半透明的View类型的遮罩布局，其ID为outside
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


import com.nju.meteor.filtersearchview.R;

import java.util.HashMap;

public class BaseBelowToolbarDialog extends Dialog {
    //筛选按钮的监听
    public interface IFilterListener{
        void onFilter(HashMap<String, Object> searchFilter);//筛选按钮按下的回调
    }
    protected int toolBarHeight;
    protected View contentView;
    protected HashMap<String,Object> searchFilter=new HashMap<>();
    protected IFilterListener filterListener;
    protected Context mContext;
    public BaseBelowToolbarDialog(Context context, int layoutID, int toolBarHeight) {
        super(context,R.style.below_toolbar_dialog_style);
        this.mContext=context;
        this.toolBarHeight=toolBarHeight;
        Activity activity=(Activity)context;
        View view =activity.getLayoutInflater().inflate(layoutID, null);
        contentView=view;
        setContentView(view);
        //计算内容的高度
        int maskY=0;
        View contentLayout=view.findViewById(R.id.content_layout);
        if (contentLayout!=null){
            int width = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            contentLayout.measure(width, height);
            maskY=contentLayout.getMeasuredHeight(); // 获取宽度
        }
        View maskLayout=view.findViewById(R.id.mask_layout);
        if (maskLayout!=null)
            maskLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        int screenHeight=UIUtility.getScreenHight(context);
        int maskHeight=screenHeight-toolBarHeight-UIUtility.getStatusBarHeight(context)-maskY;
        maskLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,maskHeight));
        Window window=getWindow();
        window.getDecorView().setPadding(0,0,0,0);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        window.setBackgroundDrawable(new ColorDrawable());
        window.setWindowAnimations(R.style.below_toolbar_dialog_animation);
        WindowManager.LayoutParams params=window.getAttributes();
        params.width=WindowManager.LayoutParams.MATCH_PARENT;
        params.y=toolBarHeight;
        window.setAttributes(params);
        setContentView(view);
    }

    /**
     * 根据筛选条件，修改界面
     */
    protected void updateViewFromFilter(){

    }

}
