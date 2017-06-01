package com.lakala.ui.common;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lakala.library.util.LogUtil;

/**
 * Created by Blues on 14-1-17.
 */
public class ListUtil {

    /**
     * 在ScrollView中嵌套ListView
     * ListView的每个Item必须是LinearLayout，不能是其他的，因为其他的Layout(如RelativeLayout)没有重写onMeasure()，所以会在onMeasure()时抛出异常。
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        setHeightBaseOnDisplayChildren(listView, listAdapter.getCount());
    }

    public static void setListViewHeightInScrollView(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 根据需要显示的item个数，控制ListView的高度
     * @param listView
     * @param displayChildrenCount 需要显示的item个数，最后一个显示半个，其余未显示内容通过滑动显示
     */
    public static void setHeightBaseOnDisplayChildren(ListView listView,int displayChildrenCount){

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int itemCount = listAdapter.getCount();
        if (itemCount <= displayChildrenCount) return;

        View mView = listAdapter.getView(0,null,listView);
        mView.measure(0,0);

        int itemHeight = mView.getMeasuredHeight();
        int dividerHeight = listView.getDividerHeight();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (displayChildrenCount-1)*itemHeight + itemHeight/2 +displayChildrenCount*dividerHeight);

        listView.setLayoutParams(layoutParams);

    }
    /**
     * 根据需要显示的item个数，控制ListView的高度
     * @param listView
     * @param displayChildrenCount 需要显示的item个数
     */
    public static void setHeightBaseOnAllDisplayChildren(ListView listView,
                                                         int displayChildrenCount){

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int itemCount = listAdapter.getCount();
        if (itemCount <= displayChildrenCount) return;

        View mView = listAdapter.getView(0,null,listView);
        mView.measure(0,0);

        int itemHeight = mView.getMeasuredHeight();
        int dividerHeight = listView.getDividerHeight();
        LogUtil.i("test","itemHeight:"+itemHeight);
        LogUtil.i("test","displayCount:"+displayChildrenCount);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                displayChildrenCount*itemHeight +displayChildrenCount*dividerHeight);

        listView.setLayoutParams(layoutParams);

    }
}
