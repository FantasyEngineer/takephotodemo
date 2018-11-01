package com.cfg.takephotodemo.httpsdemo;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by cfg on 2017/1/10.
 */
public class StringFactory extends Converter.Factory {


    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        return new Converter<ResponseBody, String>() {
            @Override
            public String convert(ResponseBody value) throws IOException {
                return value.string();
            }
        };
    }
}

//extends Converter.Factory
//    @Override
//    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//
//        return new Converter<ResponseBody, String>() {
//            @Override
//            public String convert(ResponseBody value) throws IOException {
//                return value.string();
//            }
//        };
//    }
