package com.ywl01.jlinfo.observers;


/**
 * Created by ywl01 on 2017/12/10.
 * <config>
 * <vec_map_url>http://120.92.200.168:8399/arcgis/rest/services/jlmap2/MapServer</vec_map_url>
 * <arcgis_license>runtimelite,1000,rud7393493088,none,FA0RJAY3FLJ5EBZNR078</arcgis_license>
 * <base_url>http://120.92.200.168/</base_url>
 * <db_url>http://120.92.200.168/jlInfo/operateDB.php</db_url>
 * <server_image_folder_url>http://120.92.200.168/jlmap_image/</server_image_folder_url>
 * </config>
 */


public class AppConfigObserver<String,Integer> extends BaseObserver {

//    @Override
//    protected Integer convert(String data) {
////        CommVar appConfig = CommVar;
////        try {
////            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
////            parser.setInput(new StringReader(data));
////            int eventType = parser.getEventType();
////            while (eventType != XmlPullParser.END_DOCUMENT) {
////                switch (eventType) {
////                    case XmlPullParser.START_DOCUMENT:
////                        break;
////                    case XmlPullParser.START_TAG:
////                        String name = parser.getName();
////                        if ("vec_map_url".equals(name)) {
////                            appConfig.vecMapUrl = parser.nextText();
////                        } else if ("arcgis_license".equals(name)) {
////                            appConfig.arcgis_license = parser.nextText();
////                        } else if ("base_url".equals(name)) {
////                            appConfig.baseUrl = parser.nextText();
////                        } else if ("server_image_folder_url".equals(name)) {
////                            appConfig.serverImageRootUrl = parser.nextText();
////                        } else if ("count_map_level".equals(name)) {
////                            appConfig.mapLevel = Integer.parseInt(parser.nextText());
////                        }else if ("sql_url".equals(name)) {
////                            appConfig.sqlUrl = parser.nextText();
////                        }
////                        break;
////                }
////                eventType = parser.next();
////            }
////            return appConfig;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        return null;
//    }

    @Override
    protected Object convert(Object data) {
        return null;
    }
}
