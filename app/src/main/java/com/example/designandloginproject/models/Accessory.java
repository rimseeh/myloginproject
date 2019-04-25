package com.example.designandloginproject.models;

import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.example.designandloginproject.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Accessory {

    private static final String TAG = "Accessory";
    private String id;
    private String title;
    private String description;
    private String price;
    private String url;
    private Uri dynamicUrl;


    public Accessory(String title, String description, String price, String url,String dynamicUrl) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.dynamicUrl = Uri.parse(dynamicUrl);
        this.url = url;
    }

    public Accessory() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Uri getDynamicUrl() {
        return dynamicUrl;
    }

    public void setDynamicUrl(Uri dynamicUrl) {
        this.dynamicUrl = dynamicUrl;
    }

    @Override
    public String toString() {
        return "Accessory{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", url='" + url + '\'' +
                ", dynamicUrl=" + dynamicUrl +
                '}';
    }

    //ProductEntry.initProductEntryList(getResources())
    public static List<Accessory> initAccessoryEntryList(Resources resources) {
        InputStream inputStream = resources.openRawResource(R.raw.products);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int pointer;
            while ((pointer = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, pointer);
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error writing/reading from the JSON file.", exception);
        } finally {
            try {
                inputStream.close();
            } catch (IOException exception) {
                Log.e(TAG, "Error closing the input stream.", exception);
            }
        }
        String jsonProductsString = writer.toString();
        Gson gson = new Gson();
        Type productListType = new TypeToken<ArrayList<Accessory>>() {
        }.getType();
        return gson.fromJson(jsonProductsString, productListType);
    }
}

