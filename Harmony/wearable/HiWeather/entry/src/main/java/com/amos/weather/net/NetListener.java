package com.amos.weather.net;

import org.devio.hi.json.HiJson;

public interface NetListener {
    void onSuccess(HiJson hiJson);

    void onFail(String error);
}
