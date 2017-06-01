package com.lakala.platform.common;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

public class MutexThreadManager {
	
	private static MutexThreadManager instance = new MutexThreadManager();
	
	private Map<String,Thread> threads = new HashMap<String,Thread>();
	private TimerTask timerTask = new TimerTask(){
		@Override
		public void run() {
			//定时清理 threads 已经终结的线程
			synchronized(instance.threads)
			{
				Iterator<Entry<String,Thread>> iterator= threads.entrySet().iterator();
				while (iterator.hasNext())
				{
					Entry<String,Thread> entry = iterator.next();
					Thread thread = entry.getValue();
					if (thread != null && thread.getState() == State.TERMINATED)
					{
						iterator.remove();
					}
				}
			}
		}
	};
	private Timer timer = new Timer(true);
	
	protected MutexThreadManager()
	{
		//每隔 60 秒执行一次 清理任务。
		timer.schedule(timerTask, 1000, 60000);
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		timer.cancel();
		super.finalize();
	}


	/**
	 * 运行互斥线程,如果name参数指定的线程已经存在并且还没结束则直接返回，
	 * 如果如果name参数指定的线程不存在或已经结束则启动线程。
	 * @param name     线程名称
	 * @param runnable Runnable 对象
	 * @return 如果name参数指定的线程已经存在并且还没结束则返回 false，否则返回 true。
	 */
	public static boolean runThread(String name,Runnable runnable)
	{
		if (name == null || runnable == null) 
			return false;
		
		return runThread(name,new Thread(runnable));
	}
	
	/**
	 * 运行互斥线程,如果name参数指定的线程已经存在并且还没结束则直接返回，
	 * 如果如果name参数指定的线程不存在或已经结束则启动线程。
	 * @param name     线程名称
	 * @param thread   线程对象
	 * @return 如果name参数指定的线程已经存在并且还没结束则返回 false，否则返回 true。
	 */
	public static boolean runThread(String name,Thread thread)
	{		
		if (name == null || thread == null) 
			return false;
		
		synchronized(instance.threads)
		{
			Thread t = instance.threads.get(name);
			
			if (t != null && t.getState() != State.TERMINATED)
			{
				return false;
			}
			
			instance.threads.put(name, thread);
			
			try{
				thread.start();
				return true;
			}catch(Exception e){
				instance.threads.remove(name);
				return false;
			}
		}
	}

    public static boolean getPresentThreadName(String name){

        Thread tempThread = instance.threads.get(name);

        if(tempThread != null){
            return true;
        }else {
            return false;
        }

    }

}
