package com.lakala.ui.module.lockpattern;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.lakala.ui.R;

import java.util.List;

/**
 * [][][]
 * [][][]
 * [][][]
 * 在设置手势密码时,预览view
 * Created by Vinchaos api on 14-1-1.
 */
public class LockPreview extends LinearLayout {

    private View[][] preview;

    public LockPreview(Context context) {
        this(context, null);
    }

    public LockPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) return;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_preview, this, true);
        preview = new View[3][3];
        preview[0][0] = findViewById(R.id.preview_0);
        preview[0][1] = findViewById(R.id.preview_1);
        preview[0][2] = findViewById(R.id.preview_2);
        preview[1][0] = findViewById(R.id.preview_3);
        preview[1][1] = findViewById(R.id.preview_4);
        preview[1][2] = findViewById(R.id.preview_5);
        preview[2][0] = findViewById(R.id.preview_6);
        preview[2][1] = findViewById(R.id.preview_7);
        preview[2][2] = findViewById(R.id.preview_8);
    }

    /**
     * 更新此时连接的点
     */
    public void updatePreview(List<LockPatternView.Cell> pattern) {
        for (LockPatternView.Cell cell : pattern) {
            preview[cell.getRow()][cell.getColumn()].setBackgroundResource(R.drawable.gesture_create_grid_selected);
        }
    }

    /**
     * 清除所有点
     */
    public void clear() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                preview[i][j].setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}
