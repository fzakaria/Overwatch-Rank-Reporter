package com.github.fzakaria.overwatch.metrics;

import com.codahale.metrics.MetricRegistry;
import com.github.sunghyuk.overwatch.OverwatchClient;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Locale;

/**
 * Guice module for DI
 */
public class ApplicationModule extends AbstractModule {

    protected void configure() {
        bind(MetricRegistry.class).in(Scopes.SINGLETON);
    }

    @Provides
    public OverwatchClient overwatchClient() {
        return new OverwatchClient.Builder()
                .platform("psn")
                .locale(new Locale("en", "US"))
                .build();
    }

    @Singleton
    @Provides
    public ServiceManager serviceManager(MetricRegistry metrics, OverwatchClient client) {
        List<? extends Service> services = ImmutableList.of(
                new PollingPlayerStatsService("Setheron", client, metrics),
                new PollingPlayerStatsService("Chody1337", client, metrics),
                new PollingPlayerStatsService("gbay2", client, metrics),
                new MetricReportingService(metrics)
                );
        return new ServiceManager(services);
    }
}
