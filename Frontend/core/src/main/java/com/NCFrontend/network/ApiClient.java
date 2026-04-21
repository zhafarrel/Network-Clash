package com.NCFrontend.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

public class ApiClient {

    // Interface untuk menangkap hasil balasan dari server
    public interface FetchCardsCallback {
        void onSuccess(String jsonResponse);
        void onError(Throwable t);
    }

    public static void fetchAllCards(FetchCardsCallback callback) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(Endpoints.GET_CARDS)
            .build();

        // Gdx.net.sendHttpRequest bekerja di background (tidak bikin game lag)
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                // Mendapatkan teks JSON "[]" atau "[{...}, {...}]" dari server
                String jsonResult = httpResponse.getResultAsString();

                // Kembali ke thread utama LibGDX untuk memproses UI/Logika
                Gdx.app.postRunnable(() -> callback.onSuccess(jsonResult));
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError(new Exception("Request dibatalkan")));
            }
        });
    }
}
