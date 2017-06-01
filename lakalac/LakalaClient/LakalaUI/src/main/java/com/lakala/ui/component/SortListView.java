package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.ui.R;
import com.lakala.ui.component.IndexLayout.OnTouchingLetterChangedListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Blues on 13-10-12.    <br>
 * 
 *搜索列表类，用于拼音排序列表数据显示，支持拼音全拼，拼音首字母，汉字搜索定位数据 .   <br><br>
 *
 * 使用本类的界面在退出该界面时需要进行释放资源需要在onDestroy方法中调用{@link #recyleAction()}方法
 * 
 */
public class SortListView extends RelativeLayout {

    private ListView listView;
    private EditText searchEditText;
    private TextView nullTextView;
    private ImageView searchImgBtn;
    private SortListAdapter adapter;
    private TextView overlay;//悬浮索引提示文字
    private IndexLayout letterIndexLayout;//索引布局
    private HashMap<String, Integer> alphaIndexer;//存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;//存放存在的汉语拼音首字母
    private OverlayThread overlayThread;
    private WindowManager windowManager;
    private ArrayList<SortListData> dataLists =new ArrayList<SortListData>();//所有城市列表
    private ArrayList<SortListData> searchAllDataLists=new ArrayList<SortListData>();//搜索城市列表
    private ArrayList<SortListData> searchDataLists=new ArrayList<SortListData>();//搜索城市列表
    private Context mContext;
    private Handler handler;
    private ListItemClick listItemClick ;//城市列表点击事件监听器
    private boolean isSearchStatus = false;//是否为搜索模式

    /**列表索引条背景*/
    private int indexBackground = 0;
    /**列表索引文字大小*/
    private float searchIndexTextSize = 0;
    /**列表索引文字颜色*/
    private int searchIndexTextColor = 0;
    /**列表item文字大小*/
    private float searchListItemTextSize = 0;
    /**列表item文字颜色*/
    private int searchListItemTextColor = 0;
    
    private String selectedString = "";
    
    /**
     * 设置列表点击事件监听器
     * @param listItemClick
     */
    public void setListItemClick(ListItemClick listItemClick){
        this.listItemClick = listItemClick;
    }

    public SortListView(Context context,ArrayList<SortListData> datalists) {
        super(context);
        mContext = context;
        dataLists = datalists;
        loadLayout(this);
        init(null);
    }

    public SortListView(Context context, ArrayList<SortListData> datalists,AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dataLists = datalists;
        loadLayout(this);
        init(attrs);
    }
    
    public SortListView(Context context,AttributeSet attrs) {
      super(context, attrs);
      mContext = context;
      loadLayout(this);
      init(attrs);
    }

    /**
     * 获取索引Index大小写
     * @return
     */
    public boolean getIndexCase(){
        return  letterIndexLayout.getIndexUppercase();
    }

    /**
     * 配置列表数据源
     * @param listData  数据源
     */
    public  void setDataAdapter(ArrayList<SortListData> listData){
        setDataAdapter(listData, false);
    }

    /**
     * 配置列表数据源
     * @param listData  数据源
     */
    public  void setDataAdapter(ArrayList<SortListData> listData,boolean isHaveLoc,boolean isHaveHis,boolean isHaveHot){
        letterIndexLayout.removeLocaAndHot(isHaveLoc,isHaveHis,isHaveHot);
        setDataAdapter(listData, false);
    }

    /**
     * 配置列表数据源
     * @param listDatas 数据源
     * @param isSearch  是否为搜索模式（true 则不显示列表索引item标题   false 显示）
     */
    public  void setDataAdapter(ArrayList<SortListData> listDatas,boolean isSearch){
      isSearchStatus = isSearch;
      if (listView==null) return;
      if (!isSearch) {
        dataLists = listDatas;
        if (searchAllDataLists.size() ==0) {
          for (int i = 0; i < dataLists.size(); i++) {
            if (dataLists.get(i).type.equals("1")) {
              searchAllDataLists.add(dataLists.get(i));
            }
          }
        }
      }
      if (adapter == null) {
        adapter = new SortListAdapter(listDatas);
        listView.setAdapter(adapter);
      }else {
        adapter.notifyDataSetChanged();
      }
    }

    /**
     * 更新列表数据
     */
    public  void notityDataChange(){
        if (adapter!=null) adapter.notifyDataSetChanged();
    }

    /**
     * 初始化view属性值
     * @param attrs 属性
     */
    protected  void init(AttributeSet attrs){
      if(attrs ==null) return;
      TypedArray array = getContext().obtainStyledAttributes(attrs,R.styleable.SortListView);
      
      indexBackground           = array.getColor(R.styleable.SortListView_indexTextBackground, 0);
      searchIndexTextColor      = array.getColor(R.styleable.SortListView_indexTxtColor, 0);
      searchIndexTextSize       = array.getDimension(R.styleable.SortListView_indexTxtSize, 0);
      searchListItemTextSize    = array.getDimension(R.styleable.SortListView_listItemTextSize, 0);
      searchListItemTextColor   = array.getColor(R.styleable.SortListView_listItemTextColor, 0);
      if (indexBackground ==0) {
        indexBackground = array.getResourceId(R.styleable.SortListView_indexTextBackground, 0);
      }
      String hintText = array.getString(R.styleable.SortListView_searchHintText);
      if (hintText !=null) {
        setSearchHintText(hintText);
      }
      
      float searchTextSize = array.getDimension(R.styleable.SortListView_searchTextSize, 0.0f);
      if (searchTextSize!=0.0f) {
        setSearchTextSize(searchTextSize);
      }
      float overlayTextSize           = array.getDimension(R.styleable.SortListView_overlayTextSize, 0);
      if (overlayTextSize!=0.0f) {
        setOverlayTextSize(overlayTextSize);
      }
      int overlayTextColor          = array.getColor(R.styleable.SortListView_overlayTextColor, 0);
      if (overlayTextColor!=0) {
        setOverlayTextColor(overlayTextColor);
      }
      
      array.recycle();
    }
    
    /**
     *设置列表索引标题背景
     * @param indexBackground
     */
    public void setIndexBackground(int indexBackground) {
    	letterIndexLayout.setBackgroundColor(indexBackground);
    }
    
    /**
     *设置列表索引标题背景
     * @param indexBackground
     */
    public void setIndexTextBackground(int indexBackground) {
      this.indexBackground = indexBackground;
      notityDataChange();
    }

    /**
     * 设置列表索引标题文字大小
     * @param searchIndexTextSize
     */
    public void setSearchIndexTextSize(float searchIndexTextSize) {
      this.searchIndexTextSize = searchIndexTextSize;
      notityDataChange();
    }

    /**
     * 设置列表索引标题文字颜色
     * @param searchIndexTextColor
     */
    public void setSearchIndexTextColor(int searchIndexTextColor) {
      this.searchIndexTextColor = searchIndexTextColor;
      notityDataChange();
      
    }

    /**
     * 设置列表item文字大小
     * @param searchListItemTextSize
     */
    public void setSearchListItemTextSize(float searchListItemTextSize) {
      this.searchListItemTextSize = searchListItemTextSize;
      notityDataChange();
    }

    /**
     * 设置列表item文字颜色
     * @param searchListItemTextColor
     */
    public void setSearchListItemTextColor(int searchListItemTextColor) {
      this.searchListItemTextColor = searchListItemTextColor;
      notityDataChange();
    }
    
    /**
     * 设置搜索框hint文字
     * @param resid  文字资源id
     */
    public void setSearchHintText(int resid) {
      searchEditText.setHint(resid);
    }
    
    /**
     * 设置搜索框hint文字
     * @param text  文字字符
     */
    public void setSearchHintText(CharSequence text) {
      searchEditText.setHint(text);
    }
    
    /**
     * 设置搜索框输入文字大小
     * @param size
     */
    public void setSearchTextSize(float size) {
      searchEditText.setTextSize(size);
    }
    
    /**
     * 设置索引悬浮提示文字颜色
     * @param size
     */
    public void setOverlayTextSize(float size) {
      overlay.setTextSize(size);
    }

    public void setListSelectionItem(String itemName) {
      selectedString = itemName;
      notityDataChange();
    }
    
    /**
     * 设置索引悬浮提示文字颜色
     * @param color
     */
    public void setOverlayTextColor(int color) {
      overlay.setTextColor(color);
    }

    /**
     * 初始化布局
     * @param container
     */
    protected void loadLayout(ViewGroup container){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.ui_sort_list, container);
        nullTextView = (TextView) findViewById(R.id.null_text);
        searchImgBtn = (ImageView) findViewById(R.id.id_search);
        listView =(ListView) findViewById(R.id.list_view);
        letterIndexLayout = (IndexLayout) findViewById(R.id.index_layout);
        letterIndexLayout.setIndexUppercase(true);
        letterIndexLayout.setOnTouchingLetterChangedListener(new LetterListViewListener());
        searchEditText = (EditText) findViewById(R.id.search_box);
        searchEditText.addTextChangedListener(searchTextWatcher);
        searchImgBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchEditText.getText().toString());
            }
        });
        
        //存放首字母
        alphaIndexer = new HashMap<String, Integer>();
        handler = new Handler();
        overlayThread = new OverlayThread();
        //初始化 字母提示
        initOverlay();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listItemClick.onItemClick(adapter.dataInfos.get(i),i);
            }
        });

    }
    
    private TextWatcher searchTextWatcher = new TextWatcher() {
      
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        
      }
      
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
      }
      
      @Override
      public void afterTextChanged(Editable s) {
           search(s.toString());
      }
    };

    private  void search(String keyword) {
        if (keyword.length()>0) {
            adapter = null;
            letterIndexLayout.setVisibility(View.GONE);
            ArrayList<SortListData> datas = getSearchResultList(keyword);
            if (listView.getHeaderViewsCount()==0 && datas.size()==0) {
                nullTextView.setVisibility(View.VISIBLE);
            }else {
                nullTextView.setVisibility(View.GONE);
            }
            setDataAdapter(datas,true);
        }else {
            adapter = null;
            letterIndexLayout.setVisibility(View.VISIBLE);
            nullTextView.setVisibility(View.GONE);
            setDataAdapter(dataLists,false);
        }
    }

    /**
     * 获取搜索结果
     * @param keyword 搜索关键字
     * @return
     */
    private ArrayList<SortListData> getSearchResultList(String keyword){
      searchDataLists.clear();
      for (SortListData itemData : searchAllDataLists) {
        if (startsWithtIgnoreCase(itemData.sortCharacter,keyword)) {//拼音首字母
          searchDataLists.add(itemData);
        }else if (startsWithtIgnoreCase(itemData.itemNamePy,keyword)) {//拼音全拼
          searchDataLists.add(itemData);
        }else if (itemData.itemName.contains(keyword)) {//文字字符
          searchDataLists.add(itemData);
        }
      }
      
      return searchDataLists;
    }
    
    /**
     *是否包含（忽略大小写）
     * @param str   字符
     * @param subStr  被包含字符
     * @return
     */
    private boolean containsIgnoreCase(String str,String subStr) {
    	String string = str.toLowerCase();
    	String substr = str.toLowerCase();
    	if (string.contains(subStr)) {
			return true;
		}else {
			return false;
		}
	}
    
    /**
     *是否以指定字符串开始（忽略大小写）
     * @param str   字符
     * @param subStr  被包含字符
     * @return
     */
    private boolean startsWithtIgnoreCase(String str,String subStr) {
    	String string = str.toLowerCase();
    	String substr = str.toLowerCase();
    	if (string.startsWith(subStr)) {
    		return true;
    	}else {
    		return false;
    	}
    }
    
    //初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        overlay = (TextView) inflater.inflate(R.layout.l_list_item_overlay, null);
        overlay.setBackgroundColor(Color.TRANSPARENT);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    /**
     * 索引列表点击事件监听器
     *
     */
    private class LetterListViewListener implements OnTouchingLetterChangedListener{

      @Override
      public void onTouchingLetterChanged(final String s) {

          if(alphaIndexer.get(s) != null) {
              int position = alphaIndexer.get(s);
              listView.setSelection(position);
              if (sections[position].length()>1) {
                  overlay.setText(sections[position]);
                  overlay.setTextSize(30);
              }else {
                  overlay.setText(sections[position]);
                  overlay.setTextSize(70);
              }
              overlay.setVisibility(View.VISIBLE);
              handler.removeCallbacks(overlayThread);
              //延迟一秒后执行，让overlay为不可见
              handler.postDelayed(overlayThread, 1500);
          } 
      }
  }
    //设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    /**
     * 列表适配器
     *
     */
    private class SortListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<SortListData> dataInfos = new ArrayList<SortListData>();
        public SortListAdapter(ArrayList<SortListData> datas) {
            dataInfos = datas;
            this.inflater = LayoutInflater.from(mContext);
            alphaIndexer = new HashMap<String, Integer>();
            sections = new String[dataInfos.size()];
            for (int i = 0; i < dataInfos.size(); i++) {
                //当前汉语拼音首字母
                String currentStr = dataInfos.get(i).sortCharacter;
                //上一个汉语拼音首字母，如果不存在为“ ”
                String previewStr = (i - 1) >= 0 ? dataInfos.get(i-1).sortCharacter : " ";

                if (!previewStr.equals(currentStr)) {
                    String name = dataInfos.get(i).sortCharacter;
                    alphaIndexer.put(name, i);
                    sections[i] = name;
                }
            }
        }

        @Override
        public int getCount() {
            return dataInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return dataInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.l_list_item, null);
                holder = new ViewHolder();
                holder.index = (TextView) convertView.findViewById(R.id.id_index_title);
                holder.name = (TextView) convertView.findViewById(R.id.id_item);
                holder.refreshBtn = (ImageView) convertView.findViewById(R.id.id_refresh);
                holder.progressBar = (ProgressBar)convertView.findViewById(R.id.id_progressBar);
                holder.refreshBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listItemClick.onRightBtnClick();
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position==0 && dataInfos.get(0).sortCharacter.equals("定位")){
                SortListData sortListData = dataInfos.get(0);
                if ( "正在定位".equals(sortListData.itemName)){
                    holder.refreshBtn.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                }else {
                    holder.refreshBtn.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.GONE);
                }
            }else {
                holder.progressBar.setVisibility(View.GONE);
                holder.refreshBtn.setVisibility(View.GONE);
            }

            if (searchIndexTextColor!=0) {
              holder.index.setTextColor(searchIndexTextColor);
            }
            if (searchIndexTextSize!=0) {
              holder.index.setTextSize(searchIndexTextSize);
            }
            if (searchListItemTextColor!=0) {
              holder.name.setTextColor(searchListItemTextColor);
            }else {
              holder.name.setTextColor(Color.BLACK);
            }
            if (searchListItemTextSize!=0) {
              holder.name.setTextSize(searchListItemTextSize);
            }
            
            if (indexBackground!=0) {
              holder.index.setBackgroundColor(indexBackground);
            }else {
              holder.index.setBackgroundColor(mContext.getResources().getColor(R.color.l_light_gray));
            }
            
            if (selectedString!=null && !selectedString.equals("") && selectedString.equals(dataInfos.get(position).itemName)) {
              holder.name.setTextColor(Color.BLUE);
            }else {
              if (searchListItemTextColor!=0) {
                holder.name.setTextColor(searchListItemTextColor);
              }else {
                holder.name.setTextColor(Color.BLACK);
              }
            }
            
            holder.name.setText(dataInfos.get(position).itemName);

            String currentStr = dataInfos.get(position).sortCharacter;
            String previewStr = (position - 1) >= 0 ? dataInfos.get(position-1).sortCharacter : " ";

            if (!currentStr.trim().equals(previewStr.trim())) {
                  holder.index.setVisibility(View.VISIBLE);
                  holder.index.setText(currentStr);
            }else {
                holder.index.setVisibility(View.GONE);
            }

            if ( isSearchStatus || currentStr.equals("定位")) {
              holder.index.setVisibility(View.GONE);
            }
            
            return convertView;
        }

        private class ViewHolder {
            TextView index;
            TextView name;
            ImageView refreshBtn;
            ProgressBar progressBar;
        }
    }


    /**
     * 退出使用该view界面时要做的关闭资源操作
     */
    public void recyleAction(){

        windowManager.removeView(overlay);
    }

    public  interface  ListItemClick{
        void onItemClick(SortListData listData, int position);
        void onRightBtnClick();
    }
    
    public static  class SortListData implements Serializable ,Comparable<SortListData>{
    	
    	private static final long serialVersionUID = -7811713998211744318L;
    	
    	/** 类型*/
        public String type = "0";
        /** 等级*/
        public String level;
        /** 名字*/
        public String itemName;
        /** ID*/
        public String itemId;
        /** 拼音*/
        public String itemNamePy;
        /** 拼音简写*/
        public String itemShortPin;
        /** 拼音三字码*/
        public String itemSimpleCode;
        /** 分组标识*/
        public String sortCharacter;
        /** 存储时间*/
        public long saveTime;
        
    	@Override
    	public int compareTo(SortListData another) {
    		
    		return (int) (this.saveTime - another.saveTime);
    	}
    }
}
