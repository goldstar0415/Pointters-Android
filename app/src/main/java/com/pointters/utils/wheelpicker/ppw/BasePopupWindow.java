package com.pointters.utils.wheelpicker.ppw;

import android.app.Activity;
import android.content.res.AssetManager;
import android.widget.PopupWindow;

import com.pointters.utils.wheelpicker.model.DeliveryTimeModel;
import com.pointters.utils.wheelpicker.service.XmlParserHandler;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Administrator on 2018/1/3.
 */

public class BasePopupWindow extends PopupWindow {
    /**
     * 所有省
     */
    protected String[] mUnits;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mDeliveryDataMap = new HashMap<String, String[]>();
//    /**
//     * key - 市 values - 区
//     */
//    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();
//
//    /**
//     * key - 区 values - 邮编
//     */
//    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 当前省的名称
     */
    protected String mCurrentTime = "";
    /**
     * 当前市的名称
     */
    protected String mCurrentUnit = "days";
//    /**
//     * 当前区的名称
//     */
//    protected String mCurrentDistrictName ="";
//
//    /**
//     * 当前区的邮政编码
//     */
//    protected String mCurrentZipCode ="";

    /**
     * 解析省市区的XML数据
     */

    protected void initProvinceData(Activity activity)
    {
        List<DeliveryTimeModel> provinceList = null;
        AssetManager asset = activity.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList!= null && !provinceList.isEmpty()) {
                mCurrentUnit = provinceList.get(0).getName();
                List<String> timearray = provinceList.get(0).getTimeList();
                String[] timelist = new String[provinceList.size()];
//                mDeliveryDataMap.put(mCurrentUnit, timelist);
            }
            //*/
            mUnits = new String[provinceList.size()];
            for (int i=0; i< provinceList.size(); i++) {
                // 遍历所有省的数据
                mUnits[i] = provinceList.get(i).getName();
                List<String> timeList = provinceList.get(i).getTimeList();
                String[] timeNames = new String[timeList.size()];
                for (int j=0; j< timeList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    timeNames[j] = timeList.get(j);
                }
                mDeliveryDataMap.put(mUnits[i], timeNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 释放数据
     */
    public void releaseProvinceData(){
        mUnits = null;
        mDeliveryDataMap.clear();
        mCurrentTime = null;
        mCurrentUnit = null;
    }
}
