package wangxin.example.com.piechartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangxin on 17-12-12.
 */

public class PieChart extends View  {
    private static final String TAG = "PieChart";

    //pi
    private static final double PI = 3.1415926;
    //圆心
    private Point centre;
    //添加块回调
    private OnAddedPiece mOnAddedPiece;
    //画扇形外切矩形
    private RectF mRectF;
    //选中扇形外切矩形
    private RectF selectedRectF;
    //画笔
    private Paint mPaint;
    //画文字
    private Paint mPaintText;
    //块的集合
    private List<Piece> mList;
    //块weight的总值
    private int totalWeight;
    //扇形半径
    private int radius = 0;
    //是否有piece被选中
    private Boolean isSelectPiece = false;
    //选中的index
    private int selectedPiece = -1;
    //选中piece扩展尺寸计数
    private int addCount = 1;
    //格式化百分数文字
    private DecimalFormat decimalFormat = new DecimalFormat("00.00");
    //是否显示所有文字
    private Boolean isShowAllText = false;
    //选中动画handler+runnable
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    invalidate();
                    break;

            }
            return false;
        }
    });
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(isShowAllText){
                return;
            }
            if(addCount >=20){
                addCount = 1;
                selectedRectF = new RectF(mRectF);
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
            else {
                selectedRectF.set(selectedRectF.left - 1, selectedRectF.top - 1, selectedRectF.right + 1, selectedRectF.bottom + 1);
                addCount++;
                Log.d(TAG, "run: "+ addCount);
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
                mHandler.postDelayed(this, 1000 / 60);
            }

        }
    };
    public PieChart(Context context) {
        this(context,null);
    }
    public PieChart(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mList = new ArrayList<>();
        mList.add(new Piece(2,"交通"));
        mList.add(new Piece(3,"饮食"));
        mList.add(new Piece(5,"通讯"));

        getTotalWeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.d(TAG, "onTouchEvent: "+event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: x, y "+x+" "+y);
                Log.d(TAG, "onTouchEvent: isincircle "+isIncircle(x,y));
                if(isIncircle(x,y)){
                    double angle = getAngle(x,y);
                    if(!isSelectPiece){
                        selectedPiece = angleInWhichPiece(angle);
                        mHandler.postDelayed(mRunnable,0);
                        isSelectPiece = true;
                    }
                    else{
                        selectedPiece = angleInWhichPiece(angle);
                        mHandler.postDelayed(mRunnable,0);
                    }
                }
                else{
                    selectedPiece = -1;
                    invalidate();
                    isSelectPiece = false;
                }

                break;

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "onMeasure: ws wm"+widthSize+" "+widthMode);
        Log.d(TAG, "onMeasure: hs hm"+heightSize+" "+heightMode);
        if(widthMode ==  MeasureSpec.AT_MOST){
            widthSize = 600;
        }
        if(heightMode == MeasureSpec.AT_MOST){
            heightSize = 600;
        }
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");

        if(radius == 0){
            //取长宽小的那边作为半径
            radius = (getWidth()<getHeight())?getWidth()/2:getHeight()/2;
            radius *=0.90;
            centre = new Point(getWidth()/2,getHeight()/2);
        }
        if(mRectF == null){
            //外切矩形初始化
            mRectF = new RectF(getWidth()/2-radius,getHeight()/2-radius,getWidth()/2+radius,getHeight()/2+radius);
            selectedRectF = new RectF(mRectF);
        }
        if(mPaintText == null){
            mPaintText = new Paint();
            mPaintText.setAntiAlias(true);
            mPaintText.setTextSize(radius/10);
            mPaintText.setColor(Color.WHITE);
        }
        drawArc(canvas);
        if(isShowAllText){
            for(int i=0;i<mList.size();i++){
                showTextOnPiece(mList.get(i),canvas);
            }
            isSelectPiece = false;
            selectedPiece = -1;
        }
        else {
            if(selectedPiece != -1){
                showTextOnPiece(mList.get(selectedPiece),canvas);
            }
        }

    }
    //画扇形
    protected void drawArc(Canvas canvas){
        if (mList == null)
            throw new IllegalArgumentException("didn`t input data");
        float start = 0;
        float sweep;
        for(int i=0;i<mList.size();i++){
            Log.d(TAG, "drawArc: i"+i);
            Log.d(TAG, "drawArc: tow w"+totalWeight+" "+mList.get(i).getWeight());

            mPaint.setColor(mList.get(i).getColor());
            sweep = (float) (360 * mList.get(i).getWeight()) / totalWeight;
            Log.d(TAG, "drawArc: start"+start);
            Log.d(TAG, "drawArc: sweep"+sweep);
            if(selectedPiece == i){
                canvas.drawArc(selectedRectF,start,sweep-1,true,mPaint);
            }
            else
                canvas.drawArc(mRectF,start,sweep-1,true,mPaint);
            mList.get(i).setStart(start);
            mList.get(i).setSweep(sweep);
            start += sweep;
        }

    }
    //获取总权重
    protected void getTotalWeight(){
        totalWeight = 0;
        for(int i=0;i<mList.size();i++){
            totalWeight += mList.get(i).getWeight();
        }
    }
    //获取点击处角度,顺时针为正
    protected double getAngle(float x,float y){
        double sin = (getHeight()/2 - y)/Math.pow(Math.pow(getHeight()/2-y,2)+Math.pow(getWidth()/2-x,2),0.5);
        //防止误差导致asin失败
        if(sin>1){
            sin = 1;
        }
        else if(sin < -1){
            sin = -1;
        }
        double cos = (x - getWidth()/2)/Math.pow(Math.pow(getHeight()/2-y,2)+Math.pow(getWidth()/2-x,2),0.5);
        double arcsin = Math.asin(sin);
        double angle = 180 * arcsin / PI;
        if(sin > 0){
            if(cos < 0){
                angle = 180 - angle;
            }
        }
        else{
            if(cos < 0){
                angle = 180 - angle;
            }
            else
            {
                angle = 360 + angle;
            }
        }
        return 360 - angle;
    }
    //判断点击的角度在哪一块上
    protected int angleInWhichPiece(double angle){
        for(int i=0;i<mList.size();i++){
            if(angle>mList.get(i).getStart()&&angle<mList.get(i).getSweep()+mList.get(i).getStart()){
                return i;
            }
        }
        return -1;
    }
    //在块上画文字
    protected void showTextOnPiece(Piece piece,Canvas canvas){
        if(piece == null){
            return;
        }
        double angle = (piece.getSweep()/2+piece.getStart())*PI/180;
        Log.d(TAG, "showTextOnPiece: angle"+angle);
        float y = (float) (Math.sin(angle)*radius*0.6);
        float x = (float) (Math.cos(angle)*radius*0.6);
        Log.d(TAG, "showTextOnPiece: x y"+x+" "+y);
        Log.d(TAG, "showTextOnPiece: sin agnle"+Math.sin(angle));
        Log.d(TAG, "showTextOnPiece: cos angle"+Math.cos(angle));
        String num = decimalFormat.format(100*(double)piece.getWeight()/totalWeight);
        String str = num+"%";
        float textWidth = mPaint.measureText(str);
        canvas.drawText(piece.getName(),getWidth()/2+x-textWidth*2,getHeight()/2+y-radius/10,mPaintText);
        canvas.drawText(str,getWidth()/2+x-textWidth*2,getHeight()/2+y,mPaintText);
    }
    //判断点是否在饼状图圆内
    protected Boolean isIncircle(float x,float y){
        if(Math.pow(x-centre.x,2)+Math.pow(y-centre.y,2)>Math.pow(radius,2))
            return false;
        else
            return true;
    }
    //添加块
    public void addPiece(Piece piece){
        mList.add(piece);
        totalWeight += piece.getWeight();
        invalidate();
        mOnAddedPiece.addedPie();
    }

    public void setShowAllText(Boolean b){
        isShowAllText = b;
        invalidate();
    }
    public interface OnAddedPiece{
        void addedPie();
    }

    public void setOnAddedPiece(OnAddedPiece onAddedPiece){
        this.mOnAddedPiece = onAddedPiece;
    }
}
