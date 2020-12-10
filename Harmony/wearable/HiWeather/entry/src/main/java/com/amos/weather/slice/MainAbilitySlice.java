package com.amos.weather.slice;

import com.amos.weather.ResourceTable;
import com.amos.weather.data.CityMo;
import com.amos.weather.data.ListItemProvider;
import com.amos.weather.net.HiNet;
import com.amos.weather.net.NetListener;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Image;
import ohos.agp.components.ListContainer;
import ohos.agp.components.Text;
import org.devio.hi.json.HiJson;

import java.util.ArrayList;

public class MainAbilitySlice extends AbilitySlice implements ListItemProvider.OnItemClickListener {

    private ListContainer listContainer;
    private ListItemProvider listItemProvider;
    private ArrayList<CityMo> cityMos = new ArrayList<>();
    private HiNet hiNet;

    private Image imageWeatherIcon;
    private Text textWeather;
    private Text textTemperature;
    private Text textTips;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        hiNet = new HiNet();
        initLayout();
        initCity();
    }

    private void initCity() {
        cityMos.add(new CityMo("北京", "101010100"));
        cityMos.add(new CityMo("上海", "101020100"));
        cityMos.add(new CityMo("广州", "101280101"));
        cityMos.add(new CityMo("深圳", "101280601"));
        cityMos.add(new CityMo("重庆", "101040100"));
        cityMos.add(new CityMo("杭州", "101210101"));
        cityMos.add(new CityMo("成都", "101270101"));
        cityMos.add(new CityMo("武汉", "101200101"));
        cityMos.add(new CityMo("南京", "101190101"));

        listItemProvider.setData(cityMos);

        loadData(cityMos.get(0));
    }

    private void initLayout() {
        imageWeatherIcon = (Image) findComponentById(ResourceTable.Id_weather_icon);
        textWeather = (Text) findComponentById(ResourceTable.Id_weather);
        textTemperature = (Text) findComponentById(ResourceTable.Id_temperature);
        textTips = (Text) findComponentById(ResourceTable.Id_tips);

        listContainer = (ListContainer) findComponentById(ResourceTable.Id_list);
        listItemProvider = new ListItemProvider(this, this);
        listContainer.setItemProvider(listItemProvider);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    public void onItemClick(CityMo mo, int position) {
        System.out.println(mo.cityName);
        loadData(mo);
    }

    private void loadData(CityMo mo) {
//        String url = "http://t.weather.itboy.net/api/weather/city/" + mo.cityCode;
        String url = "http://wthrcdn.etouch.cn/weather_mini?citykey=" + mo.cityCode;
        System.out.println("url = " + url);
        hiNet.get(url, null, new NetListener() {
            @Override
            public void onSuccess(HiJson hiJson) {
                System.out.println(hiJson);
                bindData2(hiJson);
            }

            @Override
            public void onFail(String error) {
                System.out.println(error);
            }
        });
    }

    private void bindData1(HiJson hiJson) {
        String province = hiJson.get("cityInfo").value("parent");
        String temperature = hiJson.get("data").value("wendu") + "℃";
        HiJson hiDataForecast = hiJson.get("data").get("forecast").get(0);

        String weather = hiDataForecast.value("type");
        textWeather.setText(weather);
        int weatherId = ResourceTable.Media_sunshine;
        if (weather.contains("阴")) {
            weatherId = ResourceTable.Media_overcast;
        } else if (weather.contains("雨")) {
            weatherId = ResourceTable.Media_rain;
        }
        imageWeatherIcon.setImageAndDecodeBounds(weatherId);
        textTemperature.setText(temperature);

        String tips = province
                + " : "
                + hiDataForecast.value("fx")
                + hiDataForecast.value("fl")
                + " 空气湿度 "
                + hiJson.get("data").value("shidu");
        textTips.setText(tips);
    }

    private void bindData2(HiJson hiJson) {
        HiJson hiData = hiJson.get("data");
        String province = hiData.value("city");
        String temperature = hiData.value("wendu") + "℃";
        HiJson hiDataForecast = hiData.get("forecast").get(0);

        String weather = hiDataForecast.value("type");
        textWeather.setText(weather);
        int weatherId = ResourceTable.Media_sunshine;
        if (weather.contains("阴")) {
            weatherId = ResourceTable.Media_overcast;
        } else if (weather.contains("雨")) {
            weatherId = ResourceTable.Media_rain;
        }
        imageWeatherIcon.setImageAndDecodeBounds(weatherId);
        textTemperature.setText(temperature);

        String tips = province
                + " : "
                + hiDataForecast.value("fengxiang")
                + hiDataForecast.value("fengli").toString().replace("<![CDATA[", "").replace("]]>", "");
        textTips.setText(tips);
    }
}
