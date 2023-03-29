package com.pigeonligh.facade.common;

import androidx.annotation.Nullable;

import com.haohaohu.cachemanage.CacheUtil;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CachedImageGetter {
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private final OkHttpClient client = new OkHttpClient();

    public CachedImageGetter() {

    }

    private static String contentType(@Nullable String contentType) {
        if (contentType == null) {
            return null;
        }

        final int index = contentType.indexOf(';');
        if (index > -1) {
            return contentType.substring(0, index);
        }

        return contentType;
    }

    private ImageCache fetch(String url) {
        final Request request = new Request.Builder()
                .url(url)
                .tag(url)
                .build();

        final Response response;
        try {
            response = client.newCall(request).execute();
        } catch (Throwable t) {
            throw new IllegalStateException("Exception obtaining network resource: " + url, t);
        }

        if (response == null) {
            throw new IllegalStateException("Could not obtain network response: " + url);
        }

        final ResponseBody body = response.body();

        ImageCache ret = new ImageCache();
        ret.contentType = contentType(response.header(HEADER_CONTENT_TYPE));
        try {
            ret.data = body.bytes();
        } catch (IOException e) {
            throw new IllegalStateException("Could not get response body: " + url);
        }
        return ret;
    }

    public ImageCache get(String url) {
        String key = Utils.hashImageURL(url);
        String cached = CacheUtil.get(key);

        ImageCache cache;
        if (cached == null || cached.isEmpty()) {
            cache = fetch(url);
            cached = Utils.gson().toJson(cache);
            CacheUtil.put(key, cached);
        } else {
            cache = Utils.gson().fromJson(cached, ImageCache.class);
        }

        return cache;
    }

    public class ImageCache {
        public String contentType;
        public byte[] data;
    }
}
