package com.apps.FourInRow.lab.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.apps.FourInRow.lab.R;
import com.apps.FourInRow.lab.game_control.Direction;

/**
 * Custom кнопка, хранящая в себе id своих соседей (других таких кнопок)
 */
public class NeighborImageButton extends ImageButton
{
    private int mLeftViewId;    //id соседней кнопки слева
    private int mRightViewId;   //id соседней кнопки справа
    private int mTopViewId;     //id соседней кнопки сверху
    private int mBottomViewId;  //id соседней кнопки снизу
    private int mLtViewId;      //id соседней кнопки слева-сверху
    private int mRtViewId;      //id соседней кнопки справа-сверху
    private int mLbViewId;      //id соседней кнопки слева-снизу
    private int mRbViewId;      //id соседней кнопки справа-снизу

    /**
     * Переопределение одного из родительских конструкторов
     *
     * @see ImageButton
     */
    public NeighborImageButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        //Берем ids соседей, заданные в xml разметке слоя
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.NeighborImageButton, 0, 0);
        try
        {
            mTopViewId = typedArray.
                    getResourceId(R.styleable.NeighborImageButton_top_view_id, 0);
            mBottomViewId = typedArray.
                    getResourceId(R.styleable.NeighborImageButton_bottom_view_id, 0);
            mLeftViewId = typedArray.
                    getResourceId(R.styleable.NeighborImageButton_left_view_id, 0);
            mRightViewId = typedArray.
                    getResourceId(R.styleable.NeighborImageButton_right_view_id, 0);
            mLtViewId = typedArray.
                    getResourceId(R.styleable.NeighborImageButton_lt_view_id, 0);
            mRtViewId = typedArray.
                    getResourceId(R.styleable.NeighborImageButton_rt_view_id, 0);
            mLbViewId = typedArray.
                    getResourceId(R.styleable.NeighborImageButton_lb_view_id, 0);
            mRbViewId = typedArray.
                    getResourceId(R.styleable.NeighborImageButton_rb_view_id, 0);
        } catch (RuntimeException e)
        {
            e.printStackTrace();
        } finally
        {
            /**
             * @see TypedArray#recycle()
             */
            typedArray.recycle();
        }
    }

    /**
     * Получить id соседней кнопки по направлению
     *
     * @param direction - направление
     * @return - id соседней кнопки, или 0, если не найдена.
     */
    public int getNeighborViewIdByDirection(Direction direction)
    {
        switch (direction)
        {
            case LEFT:
                return mLeftViewId;
            case RIGHT:
                return mRightViewId;
            case TOP:
                return mTopViewId;
            case BOTTOM:
                return mBottomViewId;
            case LT:
                return mLtViewId;
            case RT:
                return mRtViewId;
            case LB:
                return mLbViewId;
            case RB:
                return mRbViewId;
            default:
                return 0;
        }
    }
}