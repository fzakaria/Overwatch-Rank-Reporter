package com.github.fzakaria.overwatch.metrics;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.google.common.util.concurrent.AbstractIdleService;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MetricReportingService extends AbstractIdleService {

    private final static Graphite GRAPHITE = new Graphite(new InetSocketAddress("localhost", 2003));

    private final GraphiteReporter reporter;

    @Inject
    public MetricReportingService(MetricRegistry metricRegistry) {
        this.reporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith("overwatch")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(GRAPHITE);
    }

    @Override
    protected void startUp() throws Exception {
        reporter.start(1, TimeUnit.MINUTES);
    }

    @Override
    protected void shutDown() throws Exception {
        reporter.stop();
    }
}
