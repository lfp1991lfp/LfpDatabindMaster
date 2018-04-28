package com.lfp.lfp_databind_master.xml;

import android.text.TextUtils;
import android.util.Xml;

import com.lfp.lfp_databind_master.LfpDatabingApp;
import com.lfp.lfp_databind_master.xml.bean.ContentBean;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lfp on 2018/3/27.
 * 工具解析类
 */

public class XmlParseTools {
    /**
     * 根据解析路径获取解析xml的解析结果.
     *
     * @param fileName 需要解析的路径
     * @param type 0表示从aeests中读取,1表示从文件夹中读取
     * @return 解析数组的结果
     */
    public static List<ContentBean> xmlParse(String fileName, int type) {
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            InputStream inputStream = path(fileName, type);
            if (inputStream == null) {
                return null;
            }
            xmlPullParser.setInput(inputStream, Charset.forName("utf-8").name());

            List<ContentBean> beans = new ArrayList<>();
            int eventType = xmlPullParser.getEventType();    //事件解析类型

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String name = xmlPullParser.getName();
                        String text = "";
                        String stype = "";
                        if (TextUtils.equals(name, XmlConstants.GROUP)) {
                            stype = "0";
                            text = xmlPullParser.getAttributeValue(null, XmlConstants.TEXT);
                        }

                        if (TextUtils.equals(name, XmlConstants.TEXT)) {
                            stype = "1";
                            text = xmlPullParser.nextText();

                        }
                        if (!TextUtils.isEmpty(text)) {
                            ContentBean bean = new ContentBean(text, stype);
                            beans.add(bean);
                        }

                        break;
                }
                eventType = xmlPullParser.next();
            }

            return beans;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InputStream path(String fileName, int type) {
        InputStream inputStream = null;
        try {
            switch (type) {
                case 1:
                    inputStream = LfpDatabingApp.getInstance().getAssets().open(fileName);
                    break;
                default:
                    inputStream = new FileInputStream(fileName);
                    break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
