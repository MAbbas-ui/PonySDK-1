
package com.ponysdk.core.ui.activity;

import com.ponysdk.core.ui.place.Place;

/**
 * Finds the activity to run for a given {@link Place}, used to configure an
 * {@link ActivityManager}.
 */
@FunctionalInterface
public interface ActivityMapper {

    /**
     * Returns the activity to run for the given {@link Place}, or null.
     * 
     * @param place
     *            a Place
     */
    Activity getActivity(Place place);
}