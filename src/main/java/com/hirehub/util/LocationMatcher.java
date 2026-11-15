
package com.hirehub.util;

import java.util.Locale;
import java.util.Set;

public final class LocationMatcher {

    private LocationMatcher() {
    }

    public static int calculateScore(
            Set<String> preferredLocations,
            boolean remotePreference,
            String jobLocation) {

        if (jobLocation == null || jobLocation.isBlank()) {
            return 0;
        }

        String normalizedJobLocation = normalize(jobLocation);

        if (normalizedJobLocation.equals("remote")
                && remotePreference) {
            return 25;
        }

        if (preferredLocations != null) {

            boolean matches = preferredLocations.stream()
                    .filter(location ->
                            location != null && !location.isBlank())
                    .map(LocationMatcher::normalize)
                    .anyMatch(normalizedJobLocation::equals);

            if (matches) {
                return 25;
            }
        }

        return 10;
    }

    private static String normalize(String location) {
        return location.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }
}
