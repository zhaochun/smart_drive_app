package com.example.hyg.amap2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;
import com.example.hyg.amap2.SearchAddressAdapter;

/**
 * Created by hyg on 2016/12/19.
 */

public class LocationActivity extends Activity implements LocationSource,AMapLocationListener, PoiSearch.OnPoiSearchListener, GeocodeSearch.OnGeocodeSearchListener {
    private MapView mapView=null;
    private AMap aMap;
    private UiSettings uiSettings;
    OnLocationChangedListener mListener;
    AMapLocationClientOption mLocationOption;
    AMapLocationClient mLocationClient;
    Integer ZoomLevel =14;
    boolean FirstLocation = true;
    LatLonPoint start;                 //start相当于自身位置
    private EditText searchWord;
    private int currentPage = 0;    // 当前页面，从0开始计数
    PoiSearch.Query query;
    String addressString;
    private String type = "";
    ProgressDialog dialog;
    private int searchType = 0;
    private Context context;
    private List<Tip> poiItemList = new ArrayList<>();
    private SearchAddressAdapter adapter;
    private GeocodeSearch geocodeSearch;
//    String search;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = this;
        //获取地图控件引用
        mapView = (MapView) findViewById(R.id.map1);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mapView.onCreate(savedInstanceState);
        if (aMap ==null){
            aMap = mapView.getMap();
        }
        control_init();
        set_init();
        doSearchQuery(true);

    }

    private void doSearchQuery(boolean scope) {
        //dialog.show();
        aMap.clear();
        currentPage = 0;
        String city = "";
        PoiSearch.Query query = new PoiSearch.Query(addressString,"","021");
        query.setPageSize(10);
        query.setPageNum(currentPage);    //设置查询页码
        PoiSearch poiSearch = new PoiSearch(this,query);
        if (start!=null){
            if (scope){
                PoiSearch.SearchBound Bond = new PoiSearch.SearchBound(start,5000,true);
                poiSearch.setBound(Bond);
            }
        }
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    private void control_init() {
        searchWord = (EditText)findViewById(R.id.et1);
        searchWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Location2Activity.class);
                startActivityForResult(intent,2);
            }
        });
    }

    private void set_init() {
        uiSettings = aMap.getUiSettings();
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);          //设置地图模式
        aMap.setTrafficEnabled(true);                      //设置交通情况
        uiSettings.setCompassEnabled(true);               //设置指南针显示
        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);  //设置高德地图的图标
        uiSettings.setScaleControlsEnabled(true);           //设置比例尺显示
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setScrollGesturesEnabled(true);
        uiSettings.setZoomGesturesEnabled(true);
        aMap.setLocationSource(this);// 设置定位监听
        uiSettings.setMyLocationButtonEnabled(true); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);

        GeocodeQuery query1 = new GeocodeQuery(addressString,"010");
        geocodeSearch.getFromLocationNameAsyn(query1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==2 &&resultCode ==2){
            addressString = data.getStringExtra("address");
            aMap.clear();
            doSearchQuery(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        if (null != mLocationClient){
            mLocationClient.onDestroy();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener =onLocationChangedListener;
        if (mLocationClient == null){
            //初始化定位
            mLocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mLocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setGpsFirst(true);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();

        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient!=null){
            mLocationClient.stopLocation();;
            mLocationClient.onDestroy();
        }
        mLocationClient=null;
    }


    public void onLocationChanged(AMapLocation location) {

        //第一次进入时，对定位进行设置，将其设置为固定大小的比例尺
        if (FirstLocation){
            FirstLocation =false;                           //设置这个标识，为了使其只在第一次进入时，设置为固定大小显示
            if (mListener!=null && location!=null){
                if (location !=null &&location.getErrorCode() ==0){
                    mListener.onLocationChanged(location);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(ZoomLevel));
                    Log.e("AMapError", "Success" );
                }else {
                    String errText = "定位失败"+location.getErrorCode()+":"+location.getErrorInfo();
                    Log.e("AMapError", errText );
                }
            }
            //这边是什么意思？？
            if(start == null || start.getLatitude() != location.getLatitude()){
                start = new LatLonPoint(location.getLatitude(), location.getLongitude());       //这里应该是获得当前的位置
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(location.getLatitude(), location.getLongitude())));     //这个应该是当你改变位置时，地图定位你当前的位置
            }
        }

    }

    List<Marker> markers = new ArrayList<>();

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if (rCode == 1000) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
//                    List<SuggestionCity> suggestionCities = poiResult
//                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    poiItems.clear();
                    markers.clear();
                    aMap.clear();// 清理之前的图标
//                  PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
//                  poiOverlay.removeFromMap();
//                  poiOverlay.addToMap();
//                  poiOverlay.zoomToSpan();
                    for (PoiItem item:poiResult.getPois()){
                          if (item.getTitle().contains(addressString)){
                             poiItems.add(item);
                             break;
                            }
                        }
                        if (poiItems != null && poiItems.size() > 0) {
//                        aMap.clear();// 清理之前的图标
                            PoiOverlay poiOverlay = new PoiOverlay(aMap,poiItems);
                            //aMap, poiItems
                            poiOverlay.zoomToSpan();

                            for(int i = 0;i<poiItems.size();i++){
                                initMarkView();
                                textViewNumber.setText((i + 1) + "");
                                MarkerOptions m = new MarkerOptions()
                                        .title(poiItems.get(i).getTitle())
                                        .position(new LatLng(poiItems.get(i).getLatLonPoint().getLatitude(),
                                                poiItems.get(i).getLatLonPoint().getLongitude()))
                                        .icon(BitmapDescriptorFactory
                                                .fromView(markerView))
                                        .draggable(true);
                                Marker marker = aMap.addMarker(m);
                                markers.add(marker);
                            }

                            //添加自己的位置
                            if (start != null) {
                                if (poiItems.size() != 1) {
                                    set_init();
                                } else {
                                    Toast.makeText(this,"不是初始情况",Toast.LENGTH_SHORT).show();
//                                    workLine(poiItems.get(0).getLatLonPoint().getLatitude(), poiItems.get(0).getLatLonPoint().getLongitude(), DRIVElINE);
                                }

                            }
                        }

                }
            } else {
                Log.e("Error", "No Result " );
            }
        } else {
            Log.e("Error", "rCode is not 1000"+rCode );
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    View markerView;
    TextView textViewNumber;
    ImageView imageViewBg;
    public void initMarkView(){
        markerView = LayoutInflater.from(context).inflate(R.layout.item_map_position,null);
        textViewNumber = (TextView)markerView.findViewById(R.id.textView_number);
        imageViewBg = (ImageView)markerView.findViewById(R.id.imageView13);
        imageViewBg.setImageResource(R.drawable.icon_place);
    }



    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
            Log.e("Error", "onRegeocodeSearched: -------------------->"+regeocodeResult.getRegeocodeAddress().getDistrict());

            LatLonPoint latLng = regeocodeResult.getRegeocodeQuery().getPoint();
            Marker marker = aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latLng.getLatitude(), latLng.getLongitude()))
                    .title(regeocodeResult.getRegeocodeAddress().getFormatAddress())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .draggable(true));
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker.getPosition()));
            marker.showInfoWindow();// 设置默认显示一个infowinfow
        }
        dialog.dismiss();
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
        if (rCode == 1000) {

            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null
                    && geocodeResult.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);

                aMap.clear();
                String name = geocodeResult.getGeocodeQuery().getLocationName();
                LatLonPoint latLng = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
                Marker marker = aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.getLatitude(),latLng.getLongitude()))
                        .title(name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .draggable(true));
                marker.showInfoWindow();

            }
        }
    }
}
