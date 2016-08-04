package com.epicodus.myrestaurants;

import android.os.Build;
import android.widget.ListView;

import com.epicodus.myrestaurants.find_restaurants.FindRestaurantsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)

public class RestaurantListActivityTest {
    private FindRestaurantsActivity activity;
    private ListView mRestaurantListView;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(FindRestaurantsActivity.class);
        mRestaurantListView = (ListView) activity.findViewById(R.id.listView);
    }

    @Test
    public void restaurantListViewPopulates() {
        assertNotNull(mRestaurantListView.getAdapter());
        assertEquals(mRestaurantListView.getAdapter().getCount(), 16);
    }

}
