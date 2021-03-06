package com.summerbrochtrup.myeats.api;

import android.widget.Toast;

import com.summerbrochtrup.myeats.Constants;
import com.summerbrochtrup.myeats.database.RestaurantDataSource;
import com.summerbrochtrup.myeats.models.Restaurant;
import com.summerbrochtrup.myeats.ui.FindRestaurantListFragment;
import com.summerbrochtrup.myeats.util.RestaurantPropertyHelper;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * Created by summerbrochtrup on 8/7/16.
 */
public class YelpService implements Callback<YelpResponse> {
    private FindRestaurantListFragment mFragment;

    public YelpService(FindRestaurantListFragment fragment) {
        mFragment = fragment;
    }

    public void getRestaurants(String location) {
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(Constants.YELP_CONSUMER_KEY, Constants.YELP_CONSUMER_SECRET);
        consumer.setTokenWithSecret(Constants.YELP_TOKEN, Constants.YELP_TOKEN_SECRET);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.YELP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        final YelpEndpoints api = retrofit.create(YelpEndpoints.class);

        Call<YelpResponse> call = api.loadRestaurants(Constants.YELP_TERM_FOOD, location);
        call.enqueue(this);
    }

    public void getRestaurantsLatLng(String lat, String lng) {
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(Constants.YELP_CONSUMER_KEY, Constants.YELP_CONSUMER_SECRET);
        consumer.setTokenWithSecret(Constants.YELP_TOKEN, Constants.YELP_TOKEN_SECRET);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.YELP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        //generates an implementation of the yelp endpoints interface
        final YelpEndpoints api = retrofit.create(YelpEndpoints.class);

        Call<YelpResponse> call = api.loadRestaurantsLatLng(Constants.YELP_TERM_FOOD, lat + "," + lng);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<YelpResponse> call, Response<YelpResponse> response) {
        if (response.isSuccessful()) {
            YelpResponse apiResponse = response.body();
            for (Restaurant restaurant : apiResponse.getBusinesses()) {
                //JSON response structure includes nested objects. Must be flattened to save to DB.
                //grab address, categories, and coordinates, and set the restaurant fields
                restaurant.setAddress(restaurant.getLocation().getDisplayAddress());
                restaurant.setCategoryList(RestaurantPropertyHelper.getCategories(restaurant.getCategories()));
                double lat = restaurant.getLocation().getCoordinate().getLatitude();
                double lng = restaurant.getLocation().getCoordinate().getLongitude();
                restaurant.setLatitude(lat);
                restaurant.setLongitude(lng);
                RestaurantDataSource dataSource = new RestaurantDataSource(mFragment.getActivity());
                //set restaurant sort number to number of restaurants in DB to ensure it will appear last if saved
                restaurant.setSortOrder(dataSource.getNumOfRestaurants());
            }
            mFragment.setRestaurants(apiResponse.getBusinesses());
        }
    }

    @Override
    public void onFailure(Call<YelpResponse> call, Throwable t) {
        Toast.makeText(mFragment.getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
