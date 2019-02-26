package com.haoxueche.mz200alib.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.haoxueche.mz200alib.util.ContextHolder;
import com.haoxueche.winterlog.L;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author 李昱辰 987424501@qq.com
 * @date 2019/2/26
 */
public class LocationHelper implements LocationListener{
    public static final String TAG = "LocationHelper";

    private LocationManager mLocationManager;
    private boolean requested = false;

    private List<String> providers;


    private LocationHelper() {

    }

    /**
     * 每隔一段时间重新发起定位，主要是用来让设备gps模块正常工作，减少漂移
     */
    public void locateAtIntervals() {
        Observable.interval(0, 60, TimeUnit.MINUTES).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                requestLocation(1000, 0);
            }

            @Override
            public void onError(Throwable e) {
                L.e(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @SuppressLint("CheckResult")
    public void requestLocation(final int time, final int distance) {
        stopLocListener();
        mLocationManager = (LocationManager) ContextHolder.getInstance().getSystemService(Context.LOCATION_SERVICE);

        Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                L.i( "requestLocation==" + integer);
                providers = mLocationManager.getAllProviders();
                for (String provider : providers) {
                    L.i( "provider==" + provider);
                    mLocationManager.requestLocationUpdates(provider, time, distance, LocationHelper.this);
                }
            }
        });
        requested = true;
    }

    public void stopLocListener() {
        if (requested) {
            mLocationManager.removeUpdates(this);
            requested = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public static LocationHelper getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        private static final LocationHelper instance = new LocationHelper();
    }

}
