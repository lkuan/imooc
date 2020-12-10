package com.amos.weather.net;

import com.amos.weather.util.HiExecutor;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.net.NetHandle;
import ohos.net.NetManager;
import org.devio.hi.json.HiJson;
import org.devio.hi.json.JSONException;
import org.devio.hi.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.*;
import java.util.Map;

public class HiNet implements IHiNet {

    private static final String TAG = "HiNet";
    private NetManager netManager;
    private HiLogLabel hiLogLabel = new HiLogLabel(HiLog.LOG_APP, HiLog.LOG_APP, TAG);

    public HiNet() {
        netManager = NetManager.getInstance(null);
    }

    @Override
    public void get(String url, Map<String, String> params, NetListener netListener) {
        String finalUrl = HiNetUtil.buildParams(url, params);
        HiLog.debug(hiLogLabel, "finalUrl : " + finalUrl);
        HiExecutor.runBG(new Runnable() {
            @Override
            public void run() {
                doGet(finalUrl, netListener);
            }
        });
    }

    private void doGet(String finalUrl, NetListener netListener) {
        System.out.println("netManager == null ? " + (netManager == null));
        NetHandle netHandle = netManager.getDefaultNet();
        System.out.println("netHandle == null ? " + (netHandle == null));
        HttpURLConnection conn = null;
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(finalUrl);
            URLConnection urlConnection = netHandle.openConnection(url, Proxy.NO_PROXY);
            if (urlConnection instanceof HttpURLConnection) {
                conn = (HttpURLConnection) urlConnection;
                conn.setRequestMethod("GET");
                conn.connect();
                HiLog.debug(hiLogLabel, "connect...");
                if (HttpURLConnection.HTTP_OK == conn.getResponseCode()) {
                    HiLog.debug(hiLogLabel, "HTTP_OK");
                    in = conn.getInputStream();
                    baos = new ByteArrayOutputStream();
                    int len;
                    byte[] bytes = new byte[1024];
                    while ((len = in.read(bytes)) != -1) {
                        baos.write(bytes, 0, len);
                    }
                    String result = baos.toString();
                    HiExecutor.runUI(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                HiJson hiJson = new HiJson(new JSONObject(result));
                                HiLog.debug(hiLogLabel, "解析成功");
                                netListener.onSuccess(hiJson);
                            } catch (JSONException e) {
                                netListener.onFail("数据解析错误 msg : " + e.toString());
                            }
                        }
                    });
                } else {
                    HiLog.debug(hiLogLabel, "请求失败 code : " + conn.getResponseCode());
                    netListener.onFail("请求失败 code : " + conn.getResponseCode());
                }
            }
        } catch (Exception e) {
            HiLog.debug(hiLogLabel, "请求失败 msg : " + e.toString());
            netListener.onFail("请求失败 msg : " + e.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            HiNetUtil.close(in);
            HiNetUtil.close(baos);
        }
    }
}
