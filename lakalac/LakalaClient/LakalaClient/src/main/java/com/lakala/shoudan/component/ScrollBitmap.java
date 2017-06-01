package com.lakala.shoudan.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.lakala.library.util.LogUtil;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.util.ImageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

/**
 * 图片滚动加载类
 * created by gewei;
 * modify by jerry 13-8-1;
 */
public class ScrollBitmap implements Runnable,Callback {
	
	public final static int LOAD_IMAGE_COMPLETED = 0x4000;
    public final static int LOAD_IMAGE_ERROR = 0x4001;

    /** 图片加载列表  key = imageView.hashCode() 或  url.HashCode()**/
	private Map<Integer,LoadImageInfo> loadMap;
	
    /**保存加载顺序，后进先出**/
	private Stack<Integer> loadStack;
	
	/** 内存级缓存，只在卷动中使用  key = url.HashCode()*/ 
	private Map<Integer,Bitmap> memoryCacheMap;
	
	/** 保存已缓存的图片列表 key: URL 的 HashCode，value: 与key相同 **/
	private Map<Integer,Integer> fileCacheMap;

	/**缓存根路径**/
	private String cacheDir;

	private Thread loadingThread;
	
	/**是否退出加载线程**/
	private boolean exitThread = false;
	private boolean scrolling = false;
	
	/**消息处理器，用于接收图片更新消息 LOAD_IMAGE_COMPLETED  */
	private Handler handler = null;
	
	/**内存缓冲图片数量**/
	private int cacheMemoySize = 0;
	
	private boolean isBitMap=false;

    /**是否本地长期缓存图片*/
    private boolean islocalCache = false;

    /**上下文*/
    private Context mContext;
	
	/**
	 * 加载队列Key类型，1: 使用 url的 HashCode做为Key。2：使用imageView的HashCode做为 Key。
	 * 当两次调用loadImage的Key相同并且上一个相同key的图片还没有开始加载，则加载队列中只
	 * 保留最后一次调用loadImage时的加载信息。
	 */
	private int keyMode = 0;

    /**
	 * ScrollBitmap用于大型图片列表，它将载入的图片缓存到Sd卡或内部存储器上，在需要时重新载入。
	 * 在不需要 ScrollBitmap 时，需要调用 recycle 方法释放资源，否则可能会造成内存不足。
	 * @param keyMode          加载队列Key类型，0: 使用 url的 HashCode做为Key。1：使用imageView的HashCode做为 Key。
	 *                         当两次调用loadImage的Key相同并且上一个相同key的图片还没有开始加载，则加载队列中只
	 *                         保留最后一次调用loadImage时的加载信息。
	 * @param cacheMemoySize   内存缓存图片的个数，0 为不在内存缓存。
	 * @param handler          当图片加载完成后，需要接收消息的 Handler。接收的消息为 LOAD_IMAGE_COMPLETED，
	 *                         需要在该消息中调用  setImage(Msg) 方法。该参数可以为 null，ScrollBitmap 会自动
	 *                         创建一个Handler 用于处理该消息，如果这样作 ScrollBitmap 必须实例化在主线程中。
	 *                         
	 */
	public ScrollBitmap(int keyMode, int cacheMemoySize, Handler handler, Context context, boolean isBitmap)
	{
		loadMap = new HashMap<Integer,LoadImageInfo>();
		fileCacheMap = new HashMap<Integer,Integer>();
		memoryCacheMap = new Hashtable<Integer,Bitmap>();
		loadStack = new Stack<Integer>();
		isBitMap=isBitmap;
        mContext = context;
		
		if (cacheMemoySize <0 ) cacheMemoySize =0;
		this.cacheMemoySize = cacheMemoySize;
		
		if (handler == null)
			this.handler = new Handler(this);
		else
			this.handler = handler;
		
		exitThread = false;
		
		this.keyMode = keyMode;
		
		//获取外部存储器的状态，当为可读写时启用SD卡缓存。
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			//取SD卡路径
			cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + Parameters.imageCachePath;
		}
		else
		{
			//外部存储器不可用，使用内部存储器
			cacheDir = context.getFilesDir().getAbsolutePath() + Parameters.imageCachePath;
		}
		
		if (cacheDir != null)
		{
			File dir = new File(cacheDir);
			
			//如果缓存路径不存在，则创建它。
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			else if (!dir.isDirectory())
			{
				dir.delete();
				dir.mkdirs();
			}
		}
		
		loadingThread = new Thread(this);
		loadingThread.start();
	}
	
	/**
	 * 添加一个web源图片，到图像列表中。
	 * @param view
	 * @param url            图片url
	 */
	public void loadImage(ImageView view,String url)
	{
		loadImage(view,url,null);
	}


    /**
     * 添加一个web源图片，到图像列表中。
     *
     * @param view
     * @param url          图片url
     * @param islocalCache 是否本地长期缓存图片 true:是 false:否
     */
    public void loadImage(ImageView view, String url, boolean islocalCache) {
        this.islocalCache = islocalCache;
        loadImage(view, url);
    }

    /**
     * 添加一个web源图片，到图像列表中。
     *
     * @param view
     * @param url           图片url
     * @param defaultBitmap 缺省图片
     * @param islocalCache  是否本地长期缓存图片 true:是 false:否
     */
    public void loadImage(ImageView view, String url, Bitmap defaultBitmap, boolean islocalCache) {
        this.islocalCache = islocalCache;
        loadImage(view, url, defaultBitmap);
    }
	
	/**
	 * 添加一个web源图片，到图像列表中。
	 * @param view
	 * @param url            图片url
	 * @param defaultBitmap  缺省图片
	 */
	public void loadImage(ImageView view,String url,Bitmap defaultBitmap)
	{
		if (view == null)
			return;
		
		if (url == null || url.length() == 0)
		{
			//设置缺省图片
            //却图不显示
			if (defaultBitmap != null)
				//view.setImageBitmap(defaultBitmap);
//				if(isBitMap){
//					view.setImageBitmap(defaultBitmap);
//				}else {
//					view.setBackgroundDrawable(ImageUtil.BitmapToDrawable(defaultBitmap));
//				}
			return;
		}
		
		int urlHashCode = url.hashCode();
		
		//设置该view 即将要显示的图片 url 的 HashCode，因为 view 会循环利用，
		//有可能当加载线程完成时，view期望的图片已经改变，可以用此值判断 view 
		//期望的图片与加载完成的图片是否一致。
		view.setTag(urlHashCode);
		
		if (memoryCacheMap.containsKey(urlHashCode))
		{
			//当前内存缓存有该图像，尝试从中提取相应的图片，因为有时可能上下连续
			//卷动，在松开之前应保持原位置图片一直有效。
			//view.setImageBitmap(memoryCacheMap.get(urlHashCode));
			if(isBitMap){
				view.setImageBitmap(memoryCacheMap.get(urlHashCode));
			}else {
				view.setBackgroundDrawable(ImageUtil.BitmapToDrawable(memoryCacheMap.get(urlHashCode)));
			}
			return;
		}
		
		if (defaultBitmap != null)
			//view.setImageBitmap(defaultBitmap);
			if(isBitMap){
				view.setImageBitmap(defaultBitmap);
			}else {
				view.setBackgroundDrawable(ImageUtil.BitmapToDrawable(defaultBitmap));
			}
			
		int intKey = 0;
		
		if (keyMode == 0)
		{
			//使用url的HashCode做为做为加载队列的key值，如果队列中已经有此加载
			//项，则将它放在队列的顶部优先加载。
			intKey = urlHashCode;	
		}
		else
		{			
			//取view的HashCode,使用 HashCode 值做为加载队列的key值，这样
			//同一个 view的图片加载请求将被合并为一个，只保留最新的请求。
			//这是因为连续卷动的原因导致 view 循环利用，但这时有可能之前的
			//图片还没有开始加载，这时需要丢弃之前的图片信息，将加载信息设
			//置成最新的。
			intKey = view.hashCode();
		}
		
		pushLoadInfo(intKey,url,view);

		//唤醒加载线程
		synchronized (loadingThread) {
			loadingThread.notifyAll();
		}
	}
		
	/**
	 * 暂停加载线程
	 */
	public void pauseLoad()
	{
		//停用加载
		scrolling = true;
	}
	
	/**
	 * 继续加载线程。
	 */
	public void resumeLoad()
	{
		//启用加载
		scrolling = false;
		//唤醒加载线程
		synchronized (loadingThread) {
			loadingThread.notifyAll();
		}
	}

    public void setImageNull(Message msg){
        if (msg.obj instanceof LoadImageInfo)
        {
            LoadImageInfo  info = (LoadImageInfo)msg.obj;
            //加载的图片与view 期望的图片相同时设置新图片，否则忽略此图片。
            if (info.view != null) {
                info.view.setImageBitmap(null);
            }
        }


    }

	/**
	 * 在Handler 接收到 LOAD_IMAGE_COMPLETED 时调用此方法设置图片，
	 * 加载的图片与view 期望的图片相同时设置新图片，否则忽略此图片。
	 * @param msg
	 */
	public void setImage(Message msg)
	{
		if (exitThread)
			return;

		if (msg == null)
			return;
		
		if (msg.what != LOAD_IMAGE_COMPLETED)
			return;
		
		if (msg.obj instanceof LoadImageInfo)
		{
			LoadImageInfo  info = (LoadImageInfo)msg.obj;
			Integer  urlHashCode = (Integer)info.view.getTag();
			
			//加载的图片与view 期望的图片相同时设置新图片，否则忽略此图片。
			if (info.url.hashCode() == urlHashCode && info.view != null)
			{
				if (info.bitmap != null)
				{
					//info.view.setImageBitmap(info.bitmap);
					if(isBitMap){
						info.view.setImageBitmap(info.bitmap);
					}else {
						info.view.setBackgroundDrawable(ImageUtil.BitmapToDrawable(info.bitmap));
					}
					
					//将图片缓存到内存
					cacheBitmapToMemory(urlHashCode, info.bitmap);
				}
			}
			
			info.reference = null;
			info.bitmap = null;
			info.url = null;
			info.view = null;
		}
	}
	
	/**
	 * 结速加载线程，释放图片资源
	 */
	public void recycle()
	{
		exitThread = true;
		memoryCacheMap.clear();
		//唤醒加载线程
		synchronized (loadingThread) {
			loadingThread.notifyAll();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		recycle();
		super.finalize();
	}
	
	@Override
	public void run() {
		while(!exitThread)
		{
			try {
				synchronized (loadingThread) {
					loadingThread.wait();
				}
			} catch (InterruptedException e) {}
				
			//当为卷动中时，不进行加载，退出加载循环，并等待卷动停止后重新启动加载进程。
			while(!exitThread && !scrolling)
			{
				LoadImageInfo  info = popLoadInfo();
				if (info == null)
					break;
				
				Bitmap bitmap = null;
				int hashCode = info.url.hashCode();

                if (islocalCache) {
                    File file = new File(getCachePath(hashCode));
                    if (file.exists()) {
                        fileCacheMap.put(hashCode, hashCode);
                    }
                }

                if (fileCacheMap.containsKey(hashCode))
				{
					//图片已缓存过，从sd卡上读取数据
					bitmap = getBitmapFromCache(hashCode);
				}
				else
				{
					//从web 上下载图片
					bitmap = getBitmapFromWeb(info.url);
					
					//下载成功，缓存该图片到sd卡。
					if (bitmap != null)
					{
						cacheBitmapToFile(hashCode,bitmap);
					}else{

                        Message msg = new Message();
                        msg.what = LOAD_IMAGE_ERROR;
                        msg.obj  = info;
                        if (handler != null)
                            handler.sendMessage(msg);
                        return;//获取图片为空,直接返回
                    }
				}
			
				//为bitmap 创建一个弱引用，当内存不足时可以自动回收bitmap。
				//SoftReference<Bitmap> reference = new SoftReference<Bitmap>(bitmap);
				info.bitmap = bitmap;
				//info.reference = reference;
				
				Message msg = new Message();
				msg.what = LOAD_IMAGE_COMPLETED;
				msg.obj  = info;
				
				//发送消息，通知主线程可以更新图片了，更新时需要调用 SetImage(view,msg);
				if (handler != null)
					handler.sendMessage(msg);
				
				//reference = null;
				bitmap = null;
			}
		}
		
		//线程循环已退出，清空缓存的文件
		clearCacheFile();
		clearLoadInfo();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what)
		{
		case LOAD_IMAGE_COMPLETED:
			setImage(msg);
			return true;
        case LOAD_IMAGE_ERROR:
            setImageNull(msg);
            break;
		}
		return false;
	}
	
	/**
	 * 缓存图片到内存
	 * @param hashCode   图片url 的 hashCode
	 * @param bitmap
	 */
	private void cacheBitmapToMemory(int hashCode,Bitmap bitmap)
	{
		int size = memoryCacheMap.size();
		if (cacheMemoySize < 1)
			return;
		
		//当内存缓存数量满时，弹出缓存的第一个bitmap
		if (!memoryCacheMap.containsKey(hashCode) && size >= cacheMemoySize)
		{
			memoryCacheMap.remove(memoryCacheMap.keySet().iterator().next());
		}
		memoryCacheMap.put(hashCode, bitmap);
	}
	
	/**
	 * 将bitmap 缓存到Sd卡上。
	 * @param hashCode   图片 url 的HashCode
	 * @param bitmap     图片对象
	 */
	private void cacheBitmapToFile(int hashCode,Bitmap bitmap)
	{
		//sdRoot 为空表示当前的手机没有安装sd 卡或不能写入。
		if (cacheDir == null)
			return;
		
		//创建缓存文件对象 
    	File file = new File(getCachePath(hashCode));
    	
    	if (file.exists())
    	{
    		//如果文件已存在，则删除它。
    		file.delete();	
    	}
    	
    	//保存 bitmap 到文件
    	FileOutputStream outStream = null;
    	try {
			if (file.createNewFile())
			{
				outStream = new FileOutputStream(file); 
				
				//图片保存格式为 PNG，质量为 90。
				if (bitmap.compress(CompressFormat.PNG, 90, outStream))
				{
					fileCacheMap.put(hashCode, hashCode);
				}
			}
		} catch (IOException e) {
			//缓存bitmap失败
		}
    	
		if (outStream != null)
		{
			try {
				outStream.close();
			} catch (IOException e) {}
		}
	}
	
	/**
	 * 从SD卡上缓存的图片文件，创建Bitmap。缓存文件名由 HashCode 计算出来。
	 * @param hashCode
	 * @return
	 */
	private Bitmap getBitmapFromCache(int hashCode)
	{
		if (cacheDir == null)
			return null;
		
		Bitmap bitmap = BitmapFactory.decodeFile(getCachePath(hashCode));
		
		return bitmap;
	}
	
	/**
	 * 从Web 下图片数据，并构建Bitmap。
	 * @param url
	 * @return
	 */
	private Bitmap getBitmapFromWeb(String url)
	{
		if (url == null || url.length() == 0)
			return null;
		
		Bitmap bitmap = null;
		try{
    		URL bmpUrl = new URL(url);
    		URLConnection conn = bmpUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		}catch(Exception e){
            if(Parameters.debug){
				LogUtil.e(getClass().getName(), "Exception getting bitmap from web ",e);
            }
            bitmap=null;
        }
		
		return bitmap;
	}
	
	/**
	 * 根据 HashCode 计算缓存路径。
	 * @param hashCode
	 * @return
	 */
	private String getCachePath(int hashCode)
	{
        return getCachePath(hashCode,mContext);
    }
	
	/**
	 * 清空缓存文件
	 */
	private void clearCacheFile()
	{
		if (fileCacheMap == null || cacheDir == null)
			return;
		if (!islocalCache){
           Set<Entry<Integer,Integer>>  entrySet =fileCacheMap.entrySet();
           for (Entry<Integer,Integer> entry: entrySet)
           {
               File file = new File(getCachePath(entry.getValue()));
               file.delete();
           }
         }

		fileCacheMap.clear();
	}
		
	private void pushLoadInfo(Integer intKey,String url, ImageView view)
	{
		synchronized (loadMap) {
			LoadImageInfo info;
			
			if (!loadMap.containsKey(intKey))
			{
				//队列中还没有此加载信息，创建新的加载信息。
				info = new LoadImageInfo();
				loadMap.put(intKey,info);
			}
			else
			{
				//队列中已以有此HashCode 取出它，清除加载信息，重新填充。
				info = loadMap.get(intKey);
				info.url = null;
				info.view = null;
				info.reference = null;
				info.bitmap = null;
				
				//从栈中则移除它，为的是将它置顶。
				loadStack.remove(intKey);
			}
			
			info.url   = url;
			info.view  = view;
			loadStack.push(intKey);
		}
	}
	
	/**
	 * 从加载队列顶部弹出加载信息
	 */
	private LoadImageInfo popLoadInfo()
	{
		synchronized (loadMap) {
			if (!loadStack.isEmpty())
			{
				//从加载队列顶部取出需要加载的图片的信息
				Integer intKey = loadStack.pop();;
				return loadMap.remove(intKey);
			}
			else
			{
				//加载队列已空，没有需要加载的图片了，退出加载循环，等待加载队列有新内容到来。
				return null;
			}
		}
	}
	
	/**
	 * 清空加载队列
	 */
	private void clearLoadInfo()
	{
		synchronized (loadMap) {
			loadMap.clear();
			loadStack.clear();
		}
	}
	
	public class LoadImageInfo
	{
		public String url;
		@SuppressWarnings("unused")
		SoftReference<Bitmap> reference;
		public Bitmap bitmap;
		public ImageView view; //图片对应的 ImageView 
	}

    /**
     *---改造SCROLL-----4.0------
     * 增加图片本地长期缓存,并提供外部使用方法
     */

    /**
     * 本地缓存图片是否存在
     *
     * @param url     图片URL
     * @param context 应用程序上下文
     * @return
     */
    public static boolean isExistBitmapLocalCache(String url, Context context) {
        if (context == null || Util.isEmpty(url)) return false;

        File file = new File(getCachePath(url.hashCode(), context));
        return file.exists();
    }

    /**
     * 缓存Bitmap到图片缓存目录下
     *
     * @param context
     * @param url
     * @param bitmap
     * @return
     */
    public static boolean cacheBitmapToLocal(Context context, String url, Bitmap bitmap) {
        if (context == null || Util.isEmpty(url) || bitmap == null) return false;

        //创建缓存文件对象
        File file = new File(getCachePath(url.hashCode(), context));

        if (file.exists()) {
            //如果文件已存在，则删除它。
            file.delete();
        }

        //保存 bitmap 到文件
        FileOutputStream outStream = null;
        try {
            if (file.createNewFile()) {
                outStream = new FileOutputStream(file);

                //图片保存格式为 PNG，质量为 90。
                if (bitmap.compress(CompressFormat.PNG, 90, outStream)) {
                    return true;
                }
            }
        } catch (IOException e) {
            //缓存bitmap失败
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                }
            }
        }

        return false;
    }

    /**
     * 获取本地缓存图片
     *
     * @param url     图片URL
     * @param context 应用程序上下文
     * @return
     */
    public static Bitmap getBitmapFromLocalCache(String url, Context context) {
        if (context == null || Util.isEmpty(url)) return null;

        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(getCachePath(url.hashCode(), context));
        return bitmap;
    }

    /**
     * 获取本地缓存图片路径
     *
     * @param hashCode
     * @param context
     * @return
     */
    private static String getCachePath(int hashCode, Context context) {
        if (context == null) return "";

        return String.format("%s%08X.png", getLocalCacheDir(context), hashCode);
    }

    /**
     * 获取缓存图片目录
     *
     * @param context
     * @return
     */
    private static String getLocalCacheDir(Context context) {
        if (context == null) return "";

        String cacheDir = "";

        //获取外部存储器的状态，当为可读写时启用SD卡缓存。
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //取SD卡路径
            cacheDir = Environment.getExternalStorageDirectory().getPath() + Parameters.imageCachePath;
        } else {
            //外部存储器不可用，使用内部存储器
            cacheDir = context.getFilesDir().getPath() + Parameters.imageCachePath;
        }

        return cacheDir;
    }
}
