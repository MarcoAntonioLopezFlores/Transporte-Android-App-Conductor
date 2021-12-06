package com.app.expresstaxiconductor.utils.api;

import java.io.IOException;
import java.net.SocketException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {

    private int intentos;

    public RetryInterceptor(int intentos){this.intentos = intentos;}

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        boolean responseOK = false;
        int tryCount = 0;

        while (!responseOK && tryCount < this.intentos) {
            try{
                System.out.println("Intentando hacer la request -> " + tryCount);
                response = chain.proceed(request);
                responseOK = response.isSuccessful() | (response.code() >= 400 && response.code() <= 499);
            } catch (Exception e){
                System.out.println("Request sin éxito -> " + tryCount);
            }finally {
                tryCount ++;
            }
        }

        if (response == null) {
            throw new SocketException("Error no específicado de red");
        }

        return response;
    }
}
