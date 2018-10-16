package com.hanshow.ui.self.widgets;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.hanshow.R;
import com.hanshow.utils.DensityUtils;


/**
 * @author wangdeshun
 * @Description CustomerServiceView 客服icon
 * @date 2017/11/22 10:16
 * o(＞﹏＜)o
 */

public class CustomerServiceView {

    private Activity context; // 上下文
    private Button mImageView; // 可拖动按钮
    private static int mScreenWidth = -1; //屏幕的宽度
    private static int mScreenHeight = -1; //屏幕的高度
    private int relativeMoveX; // 控件相对屏幕左上角移动的位置X
    private int relativeMoveY; // 控件相对屏幕左上角移动的位置Y
    private boolean isIntercept = false; // 是否截断touch事件
    private int startDownX; // 按下时的位置控件相对屏幕左上角的位置X
    private int startDownY; // 按下时的位置控件距离屏幕左上角的位置Y
    private static int[] lastPosition; // 用于记录上一次的位置(坐标0对应x,坐标1对应y)

    /**
     * 创建客服
     *
     * @param context        上下文
     * @param mViewContainer FrameLayout
     * @param height         距离底部高度
     * @return
     */
    public static Button addFloatDragView(final Activity context, FrameLayout mViewContainer, final int height, View.OnClickListener listener) {
        CustomerServiceView floatDragView = new CustomerServiceView(context);
        Button imageView = floatDragView.getFloatDragView(listener
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        context.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Intent intent = new Intent(Intent.ACTION_DIAL);
//                                    intent.setData(Uri.parse("tel:" + context.getString(R.string.tel)));
//                                    if (intent.resolveActivity(context.getPackageManager()) != null) {
//                                        context.startActivity(intent);
//                                    }
//                                } catch (Exception e) {
//                                }
//                            }
//                        });
//                    }
//                }
                , height);
        imageView.setText("呼叫\n帮助");
        imageView.setTextColor(Color.WHITE);
        imageView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        imageView.setBackgroundResource(R.mipmap.icon_help);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(180, 161);
        layoutParams.setMargins(0, 0, 0, height);
        layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        mViewContainer.addView(imageView, layoutParams);
        return imageView;
    }

    /**
     * 初始化实例
     *
     * @param context 上下文
     */
    private CustomerServiceView(Activity context) {
        setScreenHW(context);
        this.context = context;
        lastPosition = new int[]{0, 0};
    }

    /**
     * 获取可拖动按钮的实例
     *
     * @param clickListener 点击回调
     * @param height        距离底部高度
     * @return
     */
    private Button getFloatDragView(View.OnClickListener clickListener, int height) {
        if (mImageView != null) {
            return mImageView;
        } else {
            mImageView = new Button(context);
            mImageView.setClickable(true);
            mImageView.setFocusable(true);

            setFloatDragViewParams(mImageView, height);
            mImageView.setOnClickListener(clickListener);
            setFloatDragViewTouch(mImageView);
            return mImageView;
        }
    }

    /**
     * 设置可拖动按钮的位置参数
     *
     * @param floatDragView View
     * @param height        距离底部高度
     */
    private void setFloatDragViewParams(Button floatDragView, int height) {

        // 记录最后图片在窗体的位置
        int moveX = lastPosition[0];
        int moveY = lastPosition[1];
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(180, 161);
        layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        layoutParams.setMargins(moveX, moveY, 0, height);
        floatDragView.setLayoutParams(layoutParams);

    }

    /**
     * 可拖动按钮的touch事件
     *
     * @param floatDragView ImageView
     */
    private void setFloatDragViewTouch(Button floatDragView) {
        floatDragView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isIntercept = false;
                        startDownX = relativeMoveX = (int) event.getRawX();
                        startDownY = relativeMoveY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - relativeMoveX;
                        int dy = (int) event.getRawY() - relativeMoveY;

                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;
                        if (left < 0) {
                            left = 0;
                            right = left + v.getWidth();
                        }
                        if (right > mScreenWidth) {
                            right = mScreenWidth;
                            left = right - v.getWidth();
                        }
                        if (top < 0) {
                            top = 0;
                            bottom = top + v.getHeight();
                        }
                        if (bottom > mScreenHeight) {
                            bottom = mScreenHeight;
                            top = bottom - v.getHeight();
                        }

                        v.layout(left, top, right, bottom);
                        relativeMoveX = (int) event.getRawX();
                        relativeMoveY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        int lastMoveDx = Math.abs((int) event.getRawX() - startDownX);
                        int lastMoveDy = Math.abs((int) event.getRawY() - startDownY);
                        if (5 < lastMoveDx || 5 < lastMoveDy) {  // 防止点击的时候稍微有点移动点击事件被拦截了
                            isIntercept = true;
                        } else {
                            isIntercept = false;
                        }

                        // 每次移动都要设置其layout，不然由于父布局可能嵌套listview，当父布局发生改变冲毁（如下拉刷新时）则移动的view会回到原来的位置
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(180, 161);
                        if (relativeMoveX < mScreenWidth / 2) {
                            // 每次移动都要设置其layout，不然由于父布局可能嵌套listview，当父布局发生改变冲毁（如下拉刷新时）则移动的view会回到原来的位置
                            layoutParams.gravity = Gravity.LEFT;
                            layoutParams.setMargins(0, v.getTop(), v.getRight(), lastMoveDy);
                            v.setLayoutParams(layoutParams);
                        } else {
                            layoutParams.gravity = Gravity.RIGHT;
                            layoutParams.setMargins(v.getLeft(), v.getTop(), 0, lastMoveDy);
                            v.setLayoutParams(layoutParams);
                        }
                        break;
                    default:
                        break;
                }
                return isIntercept;
            }
        });
    }

    /**
     * 计算屏幕的实际高宽
     *
     * @param context 上下文
     */
    private void setScreenHW(Activity context) {
        if (mScreenHeight < 0) {
            // 减去状态栏高度，否则挨着底部移动，导致图标变小
            Point screen = DensityUtils.getScreenSize(context);
            mScreenWidth = screen.x;
            mScreenHeight = screen.y - DensityUtils.getStatusBarHeight(context) - DensityUtils.px2dip(context,745);
        }
    }

}
