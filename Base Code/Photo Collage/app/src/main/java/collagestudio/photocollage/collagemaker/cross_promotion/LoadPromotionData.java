package collagestudio.photocollage.collagemaker.cross_promotion;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class LoadPromotionData {

    public static ArrayList<LoadPromotionResponse> allAppsList = new ArrayList<>();
    public static ArrayList<SliderResponse> allSliderList = new ArrayList<>();
    public static boolean dataLoaded = false;

    public LoadPromotionData(Context context, String url) {

        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            Log.d("response",response.toString());

                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);

                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONArray appsArray = obj.getJSONArray("appslist");
                            JSONArray sliderArray = obj.getJSONArray("appSlideImages");

                            allAppsList = new ArrayList<>();
                            allSliderList = new ArrayList<>();
                            //now looping through fbdownloader the elements of the json array
                            for (int i = 0; i < appsArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject Object = appsArray.getJSONObject(i);

                                LoadPromotionResponse loadPromotionResponse = new LoadPromotionResponse(
                                        Object.getString("appIconStr"),
                                        Object.getString("appTitle"),
                                        Object.getString("appDescription"),
                                        Object.getString("url"),
                                        Object.getString("appCoverImage")
                                );

                                allAppsList.add(loadPromotionResponse);
                            }

                            for (int i = 0; i < sliderArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject object = sliderArray.getJSONObject(i);

                                Log.w("asdsad", object.toString());

                                SliderResponse sliderResponse = new SliderResponse(object.getString("slideImage"), object.getString("appUrl"));
                                allSliderList.add(sliderResponse);
                            }

                            dataLoaded = true;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public static LoadPromotionResponse getRandomAppData() {

        int randomIndex = (new Random()).nextInt((allAppsList.size() - 0) + 1) + 0;

        if (randomIndex < allAppsList.size()) {
            return allAppsList.get(randomIndex);
        } else {
            return null;
        }
    }

}
