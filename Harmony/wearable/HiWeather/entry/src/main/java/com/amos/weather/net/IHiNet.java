package com.amos.weather.net;

import java.util.Map;

public interface IHiNet {
    void get(String url, Map<String, String> params, NetListener netListener);
}
