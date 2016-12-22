package com.example.hyg.amap2;
/**
 * Created by hyg on 2016/12/16.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import static android.content.ContentValues.TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.graphics.BitmapFactory;
import android.util.Pair;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by hyg on 2016/12/9.
 */

public class TestActivity extends Activity implements LocationSource, AMapLocationListener,RouteSearch.OnRouteSearchListener,
        PoiSearch.OnPoiSearchListener,
        Inputtips.InputtipsListener{

    private MapView mapView;
    private AMap amap;
    private UiSettings uiSettings;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private EditText et1;
    //    LatLonPoint start;
    boolean hasLocation ;
    Context context1;
    Context context2;
    Integer ZoomLevel = 14;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置对应的布局文件
        setContentView(R.layout.activity_location);
        mapView = (MapView) findViewById(R.id.map1);
        mapView.onCreate(savedInstanceState);
        et1 = (EditText)findViewById(R.id.et1) ;
        context1 = this;
        init();
        baseSetting();
        //setMarker();

        //searchFor();

    }
    PoiSearch.Query query;
    private void searchFor() {
        String st = et1.toString();
        PoiSearch.Query query = new PoiSearch.Query(st,"","021");
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，
        //POI搜索类型共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();

        InputtipsQuery inputQuery = new InputtipsQuery(st,"021");
        inputQuery.setCityLimit(true);
        Inputtips inputtips= new Inputtips(TestActivity.this,inputQuery);
        inputtips.setInputtipsListener(this);

        inputtips.requestInputtipsAsyn();

    }


    private void init() {
        hasLocation =true;
        if (amap ==null){
            amap = mapView.getMap();

        }
        uiSettings = amap.getUiSettings();
        Log.e(TAG, "uiSettings: "+uiSettings );

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    private void baseSetting() {
        context2= this;        //为什么this都不一样呢？？
        amap.setMapType(AMap.MAP_TYPE_NORMAL);          //设置地图模式
        amap.setTrafficEnabled(true);                      //设置交通情况
        uiSettings.setCompassEnabled(true);               //设置指南针显示
        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);  //设置高德地图的图标
        uiSettings.setScaleControlsEnabled(true);           //设置比例尺显示
        amap.setLocationSource(this);// 设置定位监听
        uiSettings.setMyLocationButtonEnabled(true); // 是否显示默认的定位按钮
        amap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
        //设置routeSearch对象和其数据回调监听
        RouteSearch routeSearch = new RouteSearch(context1);
        routeSearch.setRouteSearchListener(this);   //这边这个listener是需要添加接口OnRouteSearchListener ，然后才能通过this来获得

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            //进入后的情况
            mListener.onLocationChanged(aMapLocation);
            if (aMapLocation!=null &&aMapLocation.getErrorCode()==0){
                // amap.moveCamera(CameraUpdateFactory.zoomTo(ZoomLevel));
            }else{
                //amap.moveCamera(CameraUpdateFactory.zoomTo(15));
            }
        }else {
            String errText = "定位失败，"+aMapLocation.getErrorCode()+":"+aMapLocation.getErrorInfo();
            Log.e("AmapErr",errText);

        }
    }


    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient ==null){
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听

            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();
        }

    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient!=null){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
    List<Marker> markers = new ArrayList<>();
    List<PoiItem> poiItems = new ArrayList<>();
    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        PoiResult poiResult;
        if (rCode ==1000){
            if (result !=null &&result.getQuery()!=null){
                if (result.getQuery().equals(query)){
                    //取得搜索到的poiitems有多少页
                    List<SuggestionCity> suggestionCities = result.getSearchSuggestionCitys();
                    if (poiItems!=null &&poiItems.size()>0){
                        amap.clear();
                        PoiOverlay poiOverlay = new PoiOverlay(amap,poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    }else if (suggestionCities!=null &&suggestionCities.size()>0){
                        //showSuggestCity(suggestionCities);   这里是个新的方法，专门用来显示提示的城市信息的
                    }else {
                        Log.e("SuggestionCity", "onPoiSearched: "+suggestionCities );
                    }

                }
            }
        }else {
            Log.e("SearchForErr", "AnyErr " );
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {
        if (rCode==1000){
            if (poiItem !=null){
                PoiItem mPoi;
                mPoi =poiItem;

            }
        }
    }

    @Override
    public void onGetInputtips(List<Tip> list, int rCode) {
        if (rCode ==1000){
            List<HashMap<String,String>> listString = new ArrayList<HashMap<String, String>>();
            for (int i=0;i<list.size();i++){
                HashMap<String,String > map = new HashMap<String,String>();
                map.put("name",list.get(i).getName());
                map.put("address",list.get(i).getDistrict());
                listString.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(),listString,R.layout.item_layout,
                    new String[] {"name","address"},new int[]{R.id.et1,R.id.et2});

        }

    }
}

