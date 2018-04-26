package com.pointters.utils.wheelpicker.ppw;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pointters.R;
import com.pointters.utils.wheelpicker.listener.OnCityWheelComfirmListener;
import com.pointters.utils.wheelpicker.listener.OnWheelChangedListener;
import com.pointters.utils.wheelpicker.weight.WheelView;
import com.pointters.utils.wheelpicker.weight.adapters.ArrayWheelAdapter;

/**
 * Created by Administrator on 2018/1/3.
 */

public class CityWheelPickerPopupWindow extends BasePopupWindow implements View.OnClickListener ,OnWheelChangedListener {
    private View mView;
    private Activity activity;
    private TextView btn_cancel,btn_confirm;
    private WheelView mTime, mUnit ;
    private OnCityWheelComfirmListener listener = null;

    public CityWheelPickerPopupWindow(Activity activity){
        this.activity = activity;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.ppw_wheel_picker_view, null,false);
        btn_cancel = mView.findViewById(R.id.btn_cancel);
        btn_confirm = mView.findViewById(R.id.btn_confirm);
        mTime = mView.findViewById(R.id.id_time);
        mUnit = mView.findViewById(R.id.id_unit);

        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        mTime.addChangingListener(this);
        mUnit.addChangingListener(this);

        this.setContentView(mView);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.Animation);
        this.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setOutsideTouchable(true);

        this.setOnDismissListener(new OnDismissListener(){
            @Override
            public void onDismiss() {
                close();
            }
        });
    }

    public void show(){
        showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        backgroundAlpha(0.6f);
        initProvinceData(activity);
        mUnit.setViewAdapter(new ArrayWheelAdapter<String>(activity, mUnits));
        mUnit.setVisibleItems(7);
        mTime.setVisibleItems(7);
        updateTimes();
    }

    public void close(){
        releaseProvinceData();
        backgroundAlpha(1f);
        dismiss();
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0~1.0
        activity.getWindow().setAttributes(lp);
    }

    public void setListener(OnCityWheelComfirmListener wheelPickerComfirmListener){
        this.listener = wheelPickerComfirmListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            dismiss();
        } else if (i == R.id.btn_confirm) {
            if (listener != null) {
                listener.onSelected(mCurrentTime, mCurrentUnit);
            }
            dismiss();
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mUnit) {
            updateTimes();
        } else if (wheel == mTime) {
            mCurrentTime = mDeliveryDataMap.get(mCurrentUnit)[newValue];
        }
    }

    private void updateTimes() {
        int pCurrent = mUnit.getCurrentItem();
        mCurrentUnit = mUnits[pCurrent];
        String[] times = mDeliveryDataMap.get(mCurrentUnit);
        if (times == null) {
            times = new String[] { "" };
        }
        mTime.setViewAdapter(new ArrayWheelAdapter<String>(activity, times));
        mTime.setCurrentItem(0);
    }
}
