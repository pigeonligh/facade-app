package com.pigeonligh.facade.data.types;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ResponseView {
    public String defaultMode;
    public String description;
    public Content content = new Content();
    public List<DataRefItem> children = new ArrayList<>();

    public ResponseView() {
    }

    public ResponseView(ResponseView src) {
        this.defaultMode = src.defaultMode;
        this.description = src.description;
        this.content = new Content(src.content);
        this.children = new ArrayList<>();
        for (int i = 0; i < src.children.size(); i++) {
            this.children.add(new DataRefItem(src.children.get(i)));
        }
    }

    public interface Service {
        @GET("view")
        Call<ResponseView> get(@Query("path") String path);
    }

    public static class Content {
        public String title;
        public String content;

        public Content() {
        }

        public Content(Content src) {
            this.title = src.title;
            this.content = src.content;
        }
    }
}
