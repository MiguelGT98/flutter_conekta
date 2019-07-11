package com.example.flutter_conekta;

import android.content.Context;
import android.util.Base64;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Alfredo Quintero Tlacuilo on 11/07/19.
 */
public class Connection {
    private String endPoint;

    public interface ConnectionRequest {
        public void onRequestReady(JSONObject data);
    }

    private ConnectionRequest listener;

    public Connection() {
        this.listener = null;
    }

    // Assign the listener implementing events interface that will receive the events
    public void onRequestListener(ConnectionRequest listener) {
        this.listener = listener;
    }

    public void request(Context context, final Map<String, String> cardDetaiks, String endPoint) {

        this.endPoint = endPoint;
        String url = Conekta.getBaseUri() + endPoint;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestReady(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle this error
                    }
                }
        ) {
            @Override
            public byte[] getBody() {
                String body = "{\"card\":{"
                        +"\"number\":"
                        + cardDetaiks.get("number") + ","
                        +"\"name\":"
                        + cardDetaiks.get("name") + ","
                        +"\"cvc\":"
                        + cardDetaiks.get("cvc") + ","
                        +"\"exp_month\":"
                        + cardDetaiks.get("exp_month") + ","
                        +"\"exp_year\":"
                        + cardDetaiks.get("exp_year") + ","
                        +"\"device_fingerprint\":"
                        + cardDetaiks.get("device_fingerprint")
                        + "}}";
                return body.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();

                headers.put("Accept", "application/vnd.conekta-v" + Conekta.getApiVersion() + "+json");
                headers.put("Accept-Language", Conekta.getLanguage());
                headers.put("Conekta-Client-User-Agent", "{\"agent\": \"Conekta Android SDK\"}");
                String encoding = Base64.encodeToString( Conekta.getPublicKey().getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + encoding);

                return headers;
            }
        };
        RequestQueueSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


}
