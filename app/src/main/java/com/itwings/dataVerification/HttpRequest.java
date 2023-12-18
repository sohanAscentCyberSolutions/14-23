package com.itwings.dataVerification;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.itwings.dataVerification.Screens.LogIn;
import com.itwings.wastemanagement.Utills.Comman;
/*import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;*/

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequest {

    String url = "https://samparkapi.com/api/ThirdParty/";  // prod
   //  String url = "https://onemapdepts.gmda.gov.in/IncomeVerification_live/";  // prod
    // String url= "http://164.100.137.245/thirdpartyapi/api/ThirdParty/"; // QA
     String urlGisServer= "https://hsacggm.in/server/rest/services/Onemap_Haryana/Village_LGD/MapServer/0/";
     String urlForOwnServer = "";

     String AppUpdateUrl = "";
     public static Boolean isStagingAPpp = false;

    JSONObject parameters ;
    JSONArray parametersArray;
    Handler handler;
    Activity ctx;


    public HttpRequest(String url, JSONObject parameters , Handler handler, Activity ctx){
        this.url = this.url.concat(url);
        this.parameters = parameters;
        this.handler = handler;
        this.ctx = ctx;
    }
    public HttpRequest(String url , Handler handler, Activity ctx){
        this.urlGisServer = this.urlGisServer.concat(url);
        this.parameters = parameters;
        this.handler = handler;
        this.ctx = ctx;
    }
    public HttpRequest(String url , JSONObject parameters , Handler handler, Activity ctx , Boolean isOwnData){
        this.urlForOwnServer = new Comman(ctx).getAccessUrlGMDA();
        this.urlForOwnServer = this.urlForOwnServer.concat(url);
        this.parameters = parameters;
        this.handler = handler;
        this.ctx = ctx;

    }
   /* public HttpRequest(String url, JSONArray parameters , Handler handler, Activity ctx){
        this.url = this.url.concat(url);
        this.parametersArray = parameters;
        this.handler = handler;
        this.ctx = ctx;
    }*/

    public   void postAPIOwnServer(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();

      /*  String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserName();*/

        Log.e("url----->" , urlForOwnServer);
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url(urlForOwnServer)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONObject jsnoobj = new JSONObject(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = ""+jsnoobj.optJSONArray("errors");
                                dialog.dismiss();
                                new Comman(ctx).showAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else if (statusCode == 400){
                    dialog.dismiss();
                    showLogOutDialog();
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });
    }

    public   void postAPIOwnServerurlEncoded(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();

      /*  String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserName();*/

        Log.e("url----->" , urlForOwnServer);
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url(urlForOwnServer)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONObject jsnoobj = new JSONObject(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = ""+jsnoobj.optJSONArray("errors");
                                dialog.dismiss();
                                new Comman(ctx).showAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else if (statusCode == 400){
                    dialog.dismiss();
                    showLogOutDialog();
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });
    }

    public   void postAPI(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();
       // client.protocols().add(Protocol.HTTP_2);
       // client.protocols().add(Protocol.HTTP_1_0);

      /*  String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserName();*/

        Log.e("url----->" , url);
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
               /* Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("message", ""+ e.getLocalizedMessage()+""+ e+""+ e.getLocalizedMessage());
                msgObj.setData(b);
                handler.sendMessage(msgObj);*/
                e.printStackTrace();
                new Comman(ctx).showToast(""+e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
              /*  Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("message", response);
                msgObj.setData(b);
                handler.sendMessage(msgObj);
                dialog.dismiss();*/
               if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONObject jsnoobj = new JSONObject(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = ""+jsnoobj.optJSONArray("errors");
                                dialog.dismiss();
                                new Comman(ctx).showAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else if (statusCode == 400){
                    dialog.dismiss();
                    showLogOutDialog();
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });
    }
    public   void getAPI(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();

      /*  String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserName();*/

        Log.e("url----->" , url);
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONObject jsnoobj = new JSONObject(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = ""+jsnoobj.optJSONArray("errors");
                                dialog.dismiss();
                                new Comman(ctx).showAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else if (statusCode == 400){
                    dialog.dismiss();
                    showLogOutDialog();
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });
    }

    public   void postAPIAppUpdate(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();

      /*
        String id = new Comman(ctx).getUserName();*/
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url("https://onemapdepts.gmda.gov.in/developer_services/api/ReadAppInfo")
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONObject jsnoobj = new JSONObject(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = ""+jsnoobj.optJSONArray("errors");
                                dialog.dismiss();
                                new Comman(ctx).showAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else if (statusCode == 400){
                    dialog.dismiss();
                    showLogOutDialog();
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });


    }

    public   void GisAPI(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
       // RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();

      /*  String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserName();*/

        Log.e("url----->" , urlGisServer);
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url(urlGisServer)
                .build();

        Call call = client.newCall(request);


        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }
            }
        });


    }


    public   void postAPIRow(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();

      /*  String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserName();*/

        Log.e("url----->" , url);
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONObject jsnoobj = new JSONObject(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = ""+jsnoobj.optJSONArray("errors");
                                dialog.dismiss();
                                new Comman(ctx).showAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else if (statusCode == 400){
                    dialog.dismiss();
                    showLogOutDialog();
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });


    }

    public   void postAPIURLEncoded(){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();

      /*  String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserName();*/

        Log.e("url----->" , url);
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONObject jsnoobj = new JSONObject(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = ""+jsnoobj.optJSONArray("errors");
                                dialog.dismiss();
                                new Comman(ctx).showAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else if (statusCode == 400){
                    dialog.dismiss();
                    showLogOutDialog();
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });


    }
    public void postAPIURLEncodedTemp(String tempUrl){
        final Dialog dialog =   new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, parameters.toString());
        Log.e("HttpService", "Params Request was: " + parameters);
        OkHttpClient client = new OkHttpClient();

      /*  String token = new Comman(ctx).getToken();
        String id = new Comman(ctx).getUserName();*/

        url = tempUrl;

        Log.e("url----->" , url);
       /* Log.e("token----->" , token);
        Log.e("id----->" , id);
*/
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("HttpService", "onFailure() Request was: " + request);
                dialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                int statusCode = r.code();
                Log.e("HttpService", "Request code: " + statusCode);
                String response = r.body().string();
                Log.e("HttpResponse", " : " + response);
                if(statusCode == 200 || statusCode == 201){
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", response);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                    dialog.dismiss();
                    Log.e("response ", "onResponse(): " + response );
                }else if (statusCode == 401){
                    dialog.dismiss();
                    try {
                        JSONObject jsnoobj = new JSONObject(response);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                String msg = ""+jsnoobj.optJSONArray("errors");
                                dialog.dismiss();
                                new Comman(ctx).showAlert(msg);
                            }
                        };
                        handler.post(doDisplayError);
                    } catch (JSONException e) {
                        Log.e("error ==" , ""+e);
                        Runnable doDisplayError = new Runnable() {
                            public void run() {
                                dialog.dismiss();
                                new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                            }
                        };
                        handler.post(doDisplayError);
                    }
                }else if (statusCode == 400){
                    dialog.dismiss();
                    showLogOutDialog();
                }else{
                    Runnable doDisplayError = new Runnable() {
                        public void run() {
                            dialog.dismiss();
                            new Comman(ctx).showToast("Invalid Response from server Please Try Again !!");
                        }
                    };
                    handler.post(doDisplayError);
                }
            }
        });


    }

    public void showLogOutDialog(){
        Runnable doDisplayError = new Runnable() {
            public void run() {
                final Dialog d = new Dialog(ctx);
                d.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFade;
                d.setContentView(R.layout.successwithok);

                TextView Labeltitle = d.findViewById(R.id.Labeltitle);
                Labeltitle.setText("Session Expired Please Log in Again to Continue !!");

                d.findViewById(R.id.btokk).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Comman(ctx).setIsLogged(false);
                        d.dismiss();
                        ctx.startActivity(new Intent(ctx , LogIn.class));
                    }
                });

                d.show();
                d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        handler.post(doDisplayError);
    }

}
