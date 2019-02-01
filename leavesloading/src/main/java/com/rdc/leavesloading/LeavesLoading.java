package com.rdc.leavesloading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LeavesLoading extends View {

    private final static String TAG = "LeavesLoading";
    private final static String LOADING_COMPLETED = "100%";

    private int mHeight;
    private int mWidth;
    private int mDefaultHeight;
    private int mMinWidth;
    private int mMinHeight;
    private int mMaxHeight;
    private int mYellowOvalHeight;//进度条高
    private int mProgressMargin;//进度与边界的Margin
    private int mFanRotationAngle = 0;//叶子每次旋转的角度
    private int mProgressLen;//px，进度长度，用于计算叶子飘动轨迹
    private int mFanLen;//单个扇叶长度
    private int mFanCx;//风扇坐标
    private int mFanCy;
    private int mFanBitmapWidth;//FanBitmap宽和长
    private int mFanBitmapHeight;
    private int mLeafBitmapWidth;//LeafBitmap宽和长
    private int mLeafBitmapHeight;
    private int mLeafLen;//叶片边长（叶子须是正方形）
    private Paint mProgressPaint;
    private Paint mBgPaint;
    private Paint mFanStrokePaint;
    private Paint mBitmapPaint;
    private Bitmap mLeafBitmap;
    private Bitmap mFanBitmap;
    private Paint mCompletedFanPaint;//绘制100%时的扇叶
    private Paint mCompletedTextPaint;//绘制100%时的文字
    private Context mContext;

    private boolean isLoadingCompleted = false;//是否加载完成
    private int mProgress = 0;//进度值
    private int mFanRotateSpeed = 5;//每次扇子旋转的偏移量
    private int mLeafNum = 8;//叶子数（默认为8）
    private float mCompletedTextSize;
    @ColorInt
    private int mBgColorId = Color.WHITE;
    @ColorInt
    private int mProgressColorId = getResources().getColor(R.color.orange);
    @ColorInt
    private int mFanStrokeColorId = Color.WHITE;//扇子描边颜色
    private long mLeafFloatTime = DEFAULT_LEAF_FLOAT_TIME;
    private long mLeafRotateTime = DEFAULT_LEAF_ROTATE_TIME;
    private List<Leaf> mLeafList = new ArrayList<>();//存储已有的叶子
    private LeafFactory mLeafFactory = new LeafFactory();

    private static final long DEFAULT_LEAF_FLOAT_TIME = 1500;       //叶子飘动一个周期花费的时间
    private static final long DEFAULT_LEAF_ROTATE_TIME = 2000;      //叶子旋转一个周期花费的时间

    public LeavesLoading(Context context) {
        super(context);
        initData(context);
        initPaint();
    }

    public LeavesLoading(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public LeavesLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public LeavesLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        if (attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.LeavesLoading);
            mProgress = ta.getInteger(R.styleable.LeavesLoading_progress,0);
            if (mProgress > 100){
                mProgress = 100;
            }else if (mProgress < 0){
                mProgress = 0;
            }
            if (mProgress == 100){
                isLoadingCompleted = true;
            }
            mLeafFloatTime = ta.getInteger(R.styleable.LeavesLoading_leafFloatSpeed, (int) DEFAULT_LEAF_FLOAT_TIME);
            if (mLeafFloatTime <= 0){
                mLeafFloatTime = DEFAULT_LEAF_FLOAT_TIME;
            }
            mLeafRotateTime = ta.getInteger(R.styleable.LeavesLoading_leafRotateSpeed,(int) DEFAULT_LEAF_ROTATE_TIME);
            if (mLeafRotateTime < 0){
                mLeafRotateTime = DEFAULT_LEAF_ROTATE_TIME;
            }
            mFanRotateSpeed = ta.getInteger(R.styleable.LeavesLoading_fanRotateSpeed,5);
            if (mFanRotateSpeed < 0){
                mFanRotateSpeed = 5;
            }
            Drawable leaf = ta.getDrawable(R.styleable.LeavesLoading_leafSrc);
            if (leaf != null){
                mLeafBitmap = ((BitmapDrawable)leaf).getBitmap();
            }
            Drawable fan = ta.getDrawable(R.styleable.LeavesLoading_fanSrc);
            if (fan != null){
                mFanBitmap = ((BitmapDrawable)fan).getBitmap();
            }
            mLeafNum = ta.getInteger(R.styleable.LeavesLoading_leafNum,8);
            if (mLeafNum < 0){
                mLeafNum = 0;
            }
            mBgColorId = ta.getColor(
                    R.styleable.LeavesLoading_bgColor,
                    getResources().getColor(R.color.white));
            mProgressColorId = ta.getColor(
                    R.styleable.LeavesLoading_progressColor,
                    getResources().getColor(R.color.orange));
            mFanStrokeColorId = ta.getColor(
                    R.styleable.LeavesLoading_fanStrokeColor,
                    Color.WHITE);
            ta.recycle();
        }
        initData(context);
        initPaint();
    }

    private void initData(Context context){
        mMinHeight = UiUtils.dip2px(context,40);
        mMinWidth = UiUtils.dip2px(context,200);
        mMaxHeight = UiUtils.dip2px(context,100);
        mDefaultHeight = UiUtils.dip2px(context,60);
        mContext = context;
        mLeafList.addAll(mLeafFactory.generateLeafs(mLeafNum));
    }

    private void initPaint(){
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(mProgressColorId);

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBgColorId);

        mFanStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFanStrokePaint.setColor(mFanStrokeColorId);

        if (mLeafBitmap == null){
            mLeafBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.iv_leaf_3);
        }
        mLeafBitmapHeight = mLeafBitmap.getHeight();
        mLeafBitmapWidth = mLeafBitmap.getWidth();

        if (mFanBitmap == null){
            mFanBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.iv_fan_3);
        }
        mFanBitmapWidth = mFanBitmap.getWidth();
        mFanBitmapHeight = mFanBitmap.getHeight();

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mCompletedFanPaint = new Paint();
        mCompletedFanPaint.setAntiAlias(true);
        mCompletedFanPaint.setDither(true);
        mCompletedFanPaint.setFilterBitmap(true);
        mCompletedFanPaint.setAlpha(255);

        mCompletedTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCompletedTextPaint.setStyle(Paint.Style.STROKE);
        mCompletedTextPaint.setColor(Color.WHITE);
        mCompletedTextPaint.setAlpha(0);
        mCompletedTextPaint.setFakeBoldText(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getSize(mMinWidth,-1, mMinWidth,widthMeasureSpec),
                getSize(mMinHeight,mMaxHeight, mDefaultHeight,heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        mProgressMargin = mHeight / 8;
        mYellowOvalHeight = mHeight - mProgressMargin * 2;
        mFanCx = mWidth - mHeight / 2;
        mFanCy = mHeight/2;
        mFanLen = (mHeight - mProgressMargin*2)/2;
        mLeafLen = (int) (mHeight*0.3f);
        mProgressLen = mWidth - mHeight + mYellowOvalHeight/2;
        if (mHeight <= UiUtils.dip2px(mContext,50)){
            mCompletedTextSize = UiUtils.sp2px(mContext,13);
        }else if (mHeight < UiUtils.dip2px(mContext,75)){
            mCompletedTextSize = UiUtils.sp2px(mContext,16);
        }else {
            mCompletedTextSize = UiUtils.sp2px(mContext,18);
        }
        mCompletedTextPaint.setTextSize(mCompletedTextSize);
    }

    /**
     * 根据测量模式获取相应的值
     * @param min 最小值(-1为不限)
     * @param max 最大值（-1为不限）
     * @param wrap wrap_content 时的取值
     * @param measureSpec 测量模式
     * @return 计算后得到的长度
     */
    private int getSize(int min,int max, int wrap,int measureSpec) {
        int result = min;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                //某个值
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                // wrap_content
                result = wrap;
                break;
            case MeasureSpec.EXACTLY:
                // match_parent
                result = specSize;
                break;
        }
        if (max != -1){
            result = Math.min(max,result);
        }
        if (min != -1){
            result = Math.max(min,result);
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
        mBgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(0,
                0,
                mWidth,
                mHeight,
                mHeight /2f,
                mHeight /2f,
                mBgPaint);
        //叶子
        drawLeaves(canvas);
        //外圈（为了遮住超出背景的叶子）
        drawStrokeOval(canvas);
        // 进度条
        drawProgress(canvas,mProgress);
        //扇子外圈
        canvas.drawCircle(
                mFanCx,
                mFanCy,
                mHeight /2,
                mFanStrokePaint);
        //扇子内圈
        canvas.drawCircle(
                mFanCx,
                mFanCy,
                (mHeight - mProgressMargin)/2,
                mProgressPaint);
        if (isLoadingCompleted){
            //绘制加载完成特效
            drawCompleted(canvas);
        }else {
            //绘制扇叶
            drawFan(canvas,mFanLen,mBitmapPaint);
        }
        //刷新
        postInvalidate();
    }

    /**
     * 画白色外圈
     */
    private void drawStrokeOval(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.addRoundRect(
                0,
                0,
                mWidth,
                mHeight,
                mHeight /2f,
                mHeight /2f,
                Path.Direction.CW
        );
        path.addRoundRect(
                mProgressMargin,
                mProgressMargin,
                mWidth - mProgressMargin,
                mHeight - mProgressMargin,
                mYellowOvalHeight/2,
                mYellowOvalHeight/2,
                Path.Direction.CW
                );
        canvas.clipPath(path);
        canvas.drawRoundRect(
                0,
                0,
                mWidth,
                mHeight,
                mHeight /2f,
                mHeight /2f,
                mBgPaint);
        canvas.restore();
    }

    /**
     * 画叶子
     */
    private void drawLeaves(Canvas canvas){
        long currentTime = System.currentTimeMillis();
        for (Leaf leaf : mLeafList) {
            if (currentTime > leaf.startTime && leaf.startTime != 0){
                // 获取 leaf 当前的坐标
                getLeafLocation(leaf,currentTime);
                canvas.save();
                Matrix matrix = new Matrix();
                // 缩放 自适应 View 的大小
                float scaleX = (float) mLeafLen / mLeafBitmapWidth;
                float scaleY = (float) mLeafLen / mLeafBitmapHeight;
                matrix.postScale(scaleX,scaleY);
                // 位移
                float transX = leaf.x;
                float transY = leaf.y;
                matrix.postTranslate(transX,transY);
                // 旋转
                // 计算旋转因子
                float rotateFraction = ((currentTime - leaf.startTime) % mLeafRotateTime)
                        /(float)mLeafRotateTime;
                float rotate;
                switch (leaf.rotateDir){
                    case CLOCKWISE:
                        //顺时针
                        rotate = rotateFraction*360 + leaf.rotateAngle;
                        break;
                    default:
                        //逆时针
                        rotate = -rotateFraction*360 + leaf.rotateAngle;
                        break;
                }
                // 旋转中心选择 Leaf 的中心坐标
                matrix.postRotate(rotate,transX+mLeafLen/2,transY+mLeafLen/2);
                canvas.drawBitmap(mLeafBitmap,matrix, mBitmapPaint);
                canvas.restore();
            }
        }
    }

    /**
     * 获取叶子的（x,y）位置
     * @param leaf 叶子
     * @param currentTime 当前时间
     */
    private void getLeafLocation(Leaf leaf,long currentTime){
        long intervalTime = currentTime - leaf.startTime;
        if (intervalTime <= 0){
            return;
        }else if (intervalTime > mLeafFloatTime){
            leaf.startTime = currentTime + new Random().nextInt((int)mLeafFloatTime);
        }
        float fraction = (float) intervalTime / mLeafFloatTime;
        leaf.x = (1-fraction)*mProgressLen;
        leaf.y = getLeafLocationY(leaf);

        if (leaf.x <= mYellowOvalHeight / 4){
            //叶子飘到最左边，有可能会超出边界，所以提前特殊处理
            leaf.startTime = currentTime + new Random().nextInt((int)mLeafFloatTime);
            leaf.x = mProgressLen;
            leaf.y = getLeafLocationY(leaf);
        }
    }

    /**
     * 获取叶子的Y轴坐标
     * @param leaf 叶子
     * @return 经过计算的叶子Y轴坐标
     */
    private float getLeafLocationY(Leaf leaf){
        float w = (float) (Math.PI * 2 / mProgressLen);//角频率
        float A;//计算振幅值
        switch (leaf.type){
            case LITTLE:
                A = mLeafLen/3;
                break;
            case MIDDLE:
                A = mLeafLen*2/3;
                break;
            default:
                A = mLeafLen;
                break;
        }
        // (mHeight-mLeafLen)/2 是为了让 Leaf 的Y轴起始位置居中
        return (float) (A * Math.sin(w * leaf.x + leaf.n)+(mHeight-mLeafLen)/2);
    }

    /**
     * 绘制加载完成特效
     */
    private void drawCompleted(Canvas canvas) {
        // 每次绘制风扇透明度递减10
        int alpha = mCompletedFanPaint.getAlpha() - 10;
        if (alpha <= 0){
            alpha = 0;
        }
        mCompletedFanPaint.setAlpha(alpha);
        // 文字透明度刚好与风扇相反
        mCompletedTextPaint.setAlpha(255-alpha);
        // 计算透明因子
        float fraction = alpha / 255f;
        // 叶片大小 和 文字大小 也是相反变化的
        float fanLen = fraction * mFanLen;
        float textSize = (1 - fraction) * mCompletedTextSize;
        mCompletedTextPaint.setTextSize(textSize);
        drawFan(canvas, (int) fanLen, mCompletedFanPaint);
        //测量文字占用空间
        Rect bounds = new Rect();
        mCompletedTextPaint.getTextBounds(
                LOADING_COMPLETED,
                0,
                LOADING_COMPLETED.length(),
                bounds);
        //画文字
        canvas.drawText(
                LOADING_COMPLETED,
                0,
                LOADING_COMPLETED.length(),
                mFanCx-bounds.width()/2f,
                mFanCy+bounds.height()/2f,
                mCompletedTextPaint);
    }

    /**
     * 右边转动的扇子
     */
    private void drawFan(Canvas canvas,int fanLen,Paint paint){
        canvas.save();
        Matrix matrix = new Matrix();
        //缩放到合适大小
        float scaleX = fanLen*2f / mFanBitmapWidth;
        float scaleY = fanLen*2f / mFanBitmapHeight;
        matrix.postScale(scaleX,scaleY);
        //平移
        matrix.postTranslate(mFanCx-fanLen,mFanCy-fanLen);
        //旋转
        mFanRotationAngle = (mFanRotationAngle + mFanRotateSpeed) % 360;
        matrix.postRotate(-mFanRotationAngle,mFanCx,mFanCy);
        canvas.drawBitmap(mFanBitmap,matrix,paint);
        canvas.restore();
    }

    /**
     * 绘制进度
     * @param progress 0-100
     */
    private void drawProgress(Canvas canvas,int progress){
        //圆的半径
        int r = mYellowOvalHeight / 2;
        //水平长度（已减去两个半圆）
        int len = mWidth - mHeight;
        float circleProgress = 100f * r / (r + len);//左边半圆满时所对应的进度
        float rectProgress = 100f - circleProgress;//单单中间矩形满所对应的进度

        if (progress < circleProgress){
            //半圆内进度
            canvas.drawArc(
                    mProgressMargin,
                    mHeight / 2 - r,
                    2*r+ mProgressMargin,
                    mHeight / 2 + r,
                    (2-progress/circleProgress)*90,
                    180*progress/circleProgress,
                    false,
                    mProgressPaint
                    );
        }else {
            canvas.drawArc(
                    mProgressMargin,
                    mHeight / 2 - r,
                    2*r+ mProgressMargin,
                    mHeight / 2 + r,
                    90,
                    180,
                    false,
                    mProgressPaint
            );
            canvas.drawRect(
                    mProgressMargin +r,
                    mHeight/2-r,
                    r+((progress - circleProgress)/rectProgress)*len,
                    mHeight / 2 + r,
                    mProgressPaint
            );
        }
    }

    /**
     * 叶子图片
     * @param resId 图片资源ID
     */
    public void setLeafSrc(@DrawableRes int resId){
        mLeafBitmap = ((BitmapDrawable)getResources().getDrawable(resId)).getBitmap();
        postInvalidate();
    }

    /**
     * 风扇图片
     * @param resId 图片资源ID
     */
    public void setFanSrc(@DrawableRes int resId){
        mFanBitmap = ((BitmapDrawable)getResources().getDrawable(resId)).getBitmap();
        postInvalidate();
    }

    /**
     * 设置进度（自动刷新）
     * @param progress 0-100
     */
    public void setProgress(int progress){
        if (progress < 0){
            mProgress = 0;
        }else if (progress > 100){
            mProgress = 100;
        }else {
            mProgress = progress;
        }
        if (progress == 100){
            //loading 100% 特效
            isLoadingCompleted = true;
        }else {
            isLoadingCompleted = false;
        }
        // 255 不透明
        mCompletedFanPaint.setAlpha(255);
        postInvalidate();
    }

    public int getProgress(){
        return mProgress;
    }

    /**
     * 设置进度条颜色
     */
    public void setProgressColor(@ColorInt int color){
        mProgressColorId = color;
        mProgressPaint.setColor(mProgressColorId);
        postInvalidate();
    }

    /**
     * 设置叶子数目（自动刷新）
     * @param num 大于等于0的整数
     */
    public void setLeafNum(int num){
        if (num < 0){
            num = 0;
        }
        mLeafList.clear();
        mLeafList.addAll(mLeafFactory.generateLeafs(num));
        postInvalidate();
    }

    public int getLeafNum(){
        return mLeafNum;
    }

    /**
     * 设置每片叶子一个漂浮周期时间
     * @param time 默认值为 1500
     */
    public void setLeafFloatTime(long time){
        if (time <= 0){
            return;
        }
        mLeafFloatTime = time;
        postInvalidate();
    }

    public long getLeafFloatTime(){
        return mLeafFloatTime;
    }

    /**
     * 设置每片叶子的旋转周期时间
     * @param time 默认值为 2000
     */
    public void setLeafRotateTime(long time){
        if (time <= 0){
            return;
        }
        mLeafRotateTime = time;
        postInvalidate();
    }

    public long getLeafRotateTime(){
        return mLeafRotateTime;
    }

    /**
     * 设置背景颜色
     */
    public void setBgColor(@ColorInt int color){
        mBgColorId = color;
        mBgPaint.setColor(mBgColorId);
        postInvalidate();
    }

    /**
     * 设置风扇描边颜色
     */
    public void setFanStroke(@ColorInt int color){
        mFanStrokeColorId = color;
        mFanStrokePaint.setColor(mFanStrokeColorId);
        postInvalidate();
    }

    /**
     * 设置风扇旋转速度
     * @param speed 默认值为 5
     */
    public void setFanRotateSpeed(int speed){
        if (speed < 0){
            speed = 0;
        }
        mFanRotateSpeed = speed;
        postInvalidate();
    }

    /**
     * 叶子飘动的振幅
     */
    private enum AmplitudeType {
        LITTLE, MIDDLE, BIG
    }

    /**
     * 旋转方向
     */
    private enum RotateDir {
        CLOCKWISE,//顺时针
        ANTICLOCKWISE//逆时针
    }

    private class Leaf{
        float x,y;//坐标
        AmplitudeType type;//叶子飘动振幅
        int rotateAngle;//旋转角度
        RotateDir rotateDir;//旋转方向
        long startTime;//起始时间
        int n;//初始相位
    }

    private class LeafFactory{
        private Random mRandom = new Random();
        private long mAddTime;

        Leaf generateLeaf(){
            Leaf leaf = new Leaf();
            //随机振幅
            int randomType = mRandom.nextInt(3);
            switch (randomType){
                case 0:
                    leaf.type = AmplitudeType.LITTLE;
                    break;
                case 1:
                    leaf.type = AmplitudeType.MIDDLE;
                    break;
                default:
                    leaf.type = AmplitudeType.BIG;
                    break;
            }
            //随机旋转方向
            int dir = mRandom.nextInt(2);
            switch (dir){
                case 0:
                    leaf.rotateDir = RotateDir.ANTICLOCKWISE;
                    break;
                default:
                    leaf.rotateDir = RotateDir.CLOCKWISE;
                    break;
            }
            //随机起始角度
            leaf.rotateAngle = mRandom.nextInt(360);
            leaf.n = mRandom.nextInt(20);
            mAddTime += mRandom.nextInt((int)mLeafFloatTime);
            leaf.startTime = System.currentTimeMillis() + mAddTime;
            return leaf;
        }

        List<Leaf> generateLeafs(int num){
            List<Leaf> leaves = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                leaves.add(generateLeaf());
            }
            return leaves;
        }
    }
}
