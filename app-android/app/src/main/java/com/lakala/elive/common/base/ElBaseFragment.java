/*******************************************************************************
 * * Copyright (C) 2015 www.wkzf.com
 * *
 * * Licensed under the Apache License, Version 2.0 (the "License");
 * * you may not use this file except in compliance with the License.
 * * You may obtain a copy of the License at
 * *
 * *      http://www.apache.org/licenses/LICENSE-2.0
 * *
 * * Unless required by applicable law or agreed to in writing, software
 * * distributed under the License is distributed on an "AS IS" BASIS,
 * * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * * See the License for the specific language governing permissions and
 * * limitations under the License.
 ******************************************************************************/

package com.lakala.elive.common.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by youj on 2015/4/13.
 * Fragment 基类
 */
public class ElBaseFragment extends Fragment {

  @Override public void onDestroy() {
    setEventBusEnable(false);
    super.onDestroy();
  }

  /**
   * 开关EventBus总线
   */
  protected void setEventBusEnable(boolean enable) {
    if (enable && !EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().register(this);
    }
    if (!enable && EventBus.getDefault().isRegistered(this)) {
      EventBus.getDefault().unregister(this);
    }
  }

  protected <T extends View> T findView(View rootView, int id) {
    return (T) rootView.findViewById(id);
  }



}