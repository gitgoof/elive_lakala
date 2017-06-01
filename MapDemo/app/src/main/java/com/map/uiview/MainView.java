package com.map.uiview;

import com.baidu.mapapi.map.Marker;
import com.map.bean.MyMarker;

/**
 * Created by xiaogu on 2017/3/13.
 */
public interface MainView {
    void setMarkerInfo(MyMarker markerInfo);

    void setMarkerChecked(Marker marker);//设置marker点击后的样式

    void setMarkerUnChecked();//把选中的还原
}
