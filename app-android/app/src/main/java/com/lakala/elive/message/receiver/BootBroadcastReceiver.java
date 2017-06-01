package com.lakala.elive.message.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lakala.elive.message.service.MessageService;

/**
 * @author hongzhiliang
 * @version $Rev$
 * @time 2016/11/22 11:43
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDate $Date$
 * @updateDes ${TODO}
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        Intent service = new Intent(context, MessageService.class);
        context.startService(service);
        Log.v("TAG", "开机自动服务自动启动.....");
    }


}
