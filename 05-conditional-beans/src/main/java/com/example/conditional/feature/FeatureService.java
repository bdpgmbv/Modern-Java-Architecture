package com.example.conditional.feature;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Feature flag example - created only if feature is enabled.
 *
 * Enable with: features.analytics=true
 */
@Service
@ConditionalOnProperty(name = "features.analytics", havingValue = "true")
public class FeatureService {

    public FeatureService() {
        System.out.println("✓ AnalyticsFeature ENABLED (features.analytics=true)");
    }

    public String getStatus() {
        return "Analytics feature is ACTIVE";
    }
}
