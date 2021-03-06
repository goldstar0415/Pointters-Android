package com.pointters.rest;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pointters.utils.ConstantUtils;

import java.net.ContentHandler;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Anil Jha on 21-Dec-16.
 */
public class ApiClient {


    private static Retrofit retrofit = null;

    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();


    public static Retrofit getClient(boolean isSearch) {
        String baseURL = null;
        retrofit = null;

        if (isSearch) {
            baseURL = ConstantUtils.BASE_URL;
        } else {
            baseURL = ConstantUtils.BASE_URL;
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .client(okClient())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient okClient() {
        return new OkHttpClient.Builder()
             /*   .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        request = request.newBuilder()
                                .addHeader("koa:sess", "eyJpZCI6IjVhMDI5OTg4YjM3NDA0NTY4Y2I2ZjFmNSIsIl9leHBpcmUiOjE1NDIzNDY5ODIxNjQsIl9tYXhBZ2UiOjMxNTM2MDAwMDAwfQ==")
                                .addHeader("koa:sess.sig", "xqkmNPqWqwiYJtBIoJ812KqswY0")
                                .removeHeader("User-Agent")
                                .addHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:38.0) Gecko/20100101 Firefox/38.0")
                                .build();
                        Response response = chain.proceed(request);
                        return response;

                    }
                })*/
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

}
