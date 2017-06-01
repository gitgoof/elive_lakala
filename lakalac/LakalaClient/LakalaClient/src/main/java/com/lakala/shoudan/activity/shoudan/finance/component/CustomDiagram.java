package com.lakala.shoudan.activity.shoudan.finance.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.lakala.shoudan.R;

import java.math.BigDecimal;

/**
 * Created by HJP on 2015/10/12.
 */
public class CustomDiagram extends View{
    int zeroX, zeroY;    //原点
    int w, h;    //控件的长宽
    Point point = new Point();
    public int padding = 60;    //间距
    //x,y轴总共有几个坐标单位
    private int xLabelNum = 7;
    private int yLabelNum = 6;
    private Context context;
    //数据
    String[] xData;
    float[] yData;
    //单位坐标之间的间距
    int xMargin;
    int yMargin;
    //数据中y的最小值、最大值与单位
    float minY, maxY, yUnit;
    //最大值所在的索引
    int index;
    //存放x,y的坐标值
    float[] xCoordinate = new float[xLabelNum];
    float[] yCoordinate = new float[xLabelNum];
    public CustomDiagram(Context context) {
        super(context);
        this.context = context;
    }

    public CustomDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }
    /**
     * 控件创建完成后，在显示之前都会调用这个方法，此时可获取控件的大小
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        this.h = h;
        this.w = w;
        xMargin = (w - 2 * padding) / (xLabelNum - 1);
        yMargin = (h - 2 * padding) / (yLabelNum - 1);
        zeroX = 0 + padding;
        zeroY = h - padding;
        point.set(zeroX, zeroY);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (canvas != null) {

            //x轴
            canvas.drawLine(zeroX, zeroY, w - padding, zeroY, paint);
            //y轴
            canvas.drawLine(zeroX, zeroY, zeroX, padding, paint);
            //坐标刻度
            drawText(canvas);
            //画出图案，包括曲线，原点和渐变效果
            drawPoint(canvas);
            //网格
            paint.setStrokeWidth(1f);
            paint.setColor(Color.GRAY);
            drawGrid(canvas, paint);
            //标注：第一个值、最大值和最后一个值
            drawMarker(canvas);
        }
    }

    //标注长40，宽25
    public void drawMarker(Canvas canvas){

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.custom_diagram_yellow));

        //第一个值
        RectF rectF1 = new RectF(zeroX-20,yCoordinate[0]+10,zeroX+30,yCoordinate[0]+30);
        canvas.drawRoundRect(rectF1, 4, 4, paint);

        RectF rectF2 = new RectF(xCoordinate[index]-20,yCoordinate[index]-30,xCoordinate[index]+30,yCoordinate[index]-10);
        //最大值,判断是否是第一个或者最后一个
        if (index != 0 && index != 6){
            canvas.drawRoundRect(rectF2,4,4,paint);
        }
        //最后一个值
        RectF rectF3 = new RectF(xCoordinate[6]-20,yCoordinate[6]-30,xCoordinate[6]+30,yCoordinate[6]-10);
        canvas.drawRoundRect(rectF3, 4, 4, paint);

        paint.setColor(context.getResources().getColor(R.color.white));
        paint.setStrokeWidth(25);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        //将文字画在圆形矩阵中，并居中
        float baseline1 = rectF1.top + (rectF1.bottom - rectF1.top + fontMetrics.top - fontMetrics.bottom)/2 -fontMetrics.top;
        float baseline2 = rectF2.top + (rectF2.bottom - rectF2.top + fontMetrics.top - fontMetrics.bottom)/2 -fontMetrics.top;
        float baseline3 = rectF3.top + (rectF3.bottom - rectF3.top + fontMetrics.top - fontMetrics.bottom)/2 -fontMetrics.top;

        paint.setTextAlign(Paint

                .Align.CENTER);
        BigDecimal bigDecimal1 = new BigDecimal(yData[0]);
        BigDecimal bigDecimal2 = new BigDecimal(yData[index]);
        BigDecimal bigDecimal3 = new BigDecimal(yData[6]);
        paint.setTextSize(15);
        canvas.drawText(String.valueOf(bigDecimal1.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue()), rectF1.centerX(), baseline1, paint);
        //同上判断最大值
        if (index != 0 && index != 6){
            canvas.drawText(String.valueOf(bigDecimal2.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue()), rectF2.centerX(), baseline2,paint);
        }
        canvas.drawText(String.valueOf(bigDecimal3.setScale(3,BigDecimal.ROUND_HALF_UP).floatValue()),rectF3.centerX(),baseline3,paint);
    }

    public void drawPoint(Canvas canvas) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.custom_diagram_yellow));

        for (int i = 0; i < xCoordinate.length; i++) {

            xCoordinate[i] = zeroX + i * xMargin;
            //Y坐标算法:算出y轴的坐标总数，用当前数据值减去最小值除以y轴总值求出比例，用这个
            //比例乘以坐标总数得出距离原地的坐标数，再用原点的y坐标减去这个，得到这个点的y坐标值
            int ySum = zeroY - padding;
            yCoordinate[i] = zeroY - (zeroY - padding) * ((yData[i] - minY + yUnit) / (yUnit * 5));
        }

        //画渐变效果
        Path path1 = getPath(paint);
        path1.lineTo(xCoordinate[6],zeroY);
        path1.lineTo(zeroX, zeroY);
        path1.close();
        Shader shader = new LinearGradient(zeroX,zeroY,xCoordinate[6],yCoordinate[6],new int[]{context.getResources().getColor(R.color.white),context.getResources().getColor(R.color.custom_diagram_yellow1)},new float[]{0,1.0f}, Shader.TileMode.MIRROR);
        Paint paint1 = new Paint();
        paint1.setShader(shader);
        paint1.setStyle(Paint.Style.FILL);
        canvas.drawPath(path1, paint1);

        //画曲线
        Path path = getPath(paint);
        canvas.drawPath(path, paint);

        //画起始点，最高点，最后一点的坐标
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(xCoordinate[0], yCoordinate[0], 5, paint);
        canvas.drawCircle(xCoordinate[index], yCoordinate[index], 5, paint);
        canvas.drawCircle(xCoordinate[6], yCoordinate[6], 5, paint);

    }

    public Path getPath(Paint paint){

        Path path = new Path();
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE); //空心
        paint.setStrokeWidth(2f);
        path.moveTo(xCoordinate[0], yCoordinate[0]);
        float iSlope = (yCoordinate[1]-yCoordinate[0])/(xCoordinate[1] - xCoordinate[0]);

        for (int i = 1; i < xCoordinate.length; i++) {

            float x1 = (xCoordinate[i-1] + xCoordinate[i])/2;
            float y1 = yCoordinate[i-1];

            float x2 = (xCoordinate[i-1] + xCoordinate[i])/2;
            float y2 = yCoordinate[i];
            path.cubicTo(x1,y1,x2,y2,xCoordinate[i],yCoordinate[i]);
        }
        return path;
    }
    public void drawText(Canvas canvas) {

        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.custom_diagram_text));
        //x轴上的数字
        for (int i = 0; i < xLabelNum; i++) {
            canvas.drawText(xData[i], zeroX + i * xMargin - 10, zeroY + padding / 2, paint);
        }
        //y轴上的数字
        for (int i = 0; i < yLabelNum; i++) {

            //格式化保留一位小数
            float fTemp = minY - yUnit + i * yUnit;
            BigDecimal bigDecimal = new BigDecimal(fTemp);
            String temp = String.valueOf(bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue());

            canvas.drawText(temp, zeroX - padding / 2-10, zeroY - i * yMargin, paint);
        }

    }

    private void drawGrid(Canvas canvas, Paint paint) {

        //y轴上的网格
        canvas.drawLine(zeroX, zeroY - yMargin, w - padding, zeroY - yMargin, paint);
        canvas.drawLine(zeroX, zeroY - 2 * yMargin, w - padding, zeroY - 2 * yMargin, paint);
        canvas.drawLine(zeroX, zeroY - 3 * yMargin, w - padding, zeroY - 3 * yMargin, paint);
        canvas.drawLine(zeroX, zeroY - 4 * yMargin, w - padding, zeroY - 4 * yMargin, paint);
        canvas.drawLine(zeroX, zeroY - 5 * yMargin, w - padding, zeroY - 5 * yMargin, paint);
        //x轴上的网格
        canvas.drawLine(zeroX + 1 * xMargin, zeroY, zeroX + 1 * xMargin, padding, paint);
        canvas.drawLine(zeroX + 2 * xMargin, zeroY, zeroX + 2 * xMargin, padding, paint);
        canvas.drawLine(zeroX + 3 * xMargin, zeroY, zeroX + 3 * xMargin, padding, paint);
        canvas.drawLine(zeroX + 4 * xMargin, zeroY, zeroX + 4 * xMargin, padding, paint);
        canvas.drawLine(zeroX + 5 * xMargin, zeroY, zeroX + 5 * xMargin, padding, paint);
        canvas.drawLine(zeroX + 6 * xMargin, zeroY, zeroX + 6 * xMargin, padding, paint);
    }

    public void setData(String[] xData, float[] yData) {
        this.xData = xData;
        this.yData = yData;

        calculatorY();
    }

    private void calculatorY() {

        if (xData == null || yData == null) {
            return;
        } else {
            //用冒泡排序算出用户数据的最小与最大值，乘以2为y轴的取值范围
            minY = yData[0];
            maxY = yData[0];
            for (int i = 1; i < yData.length; i++) {
                if (minY > yData[i]) {
                    minY = yData[i];
                }
                if (maxY < yData[i]) {
                    maxY = yData[i];
                    index = i;
                }
            }
            //y轴的坐标单位
            yUnit = ((maxY - minY) * 2) / 5;
            if(yUnit<0.1){
                yUnit=0.1f;
            }
        }
    }

}
