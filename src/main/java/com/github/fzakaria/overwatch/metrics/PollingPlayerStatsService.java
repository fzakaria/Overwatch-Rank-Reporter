package com.github.fzakaria.overwatch.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.github.sunghyuk.overwatch.OverwatchClient;
import com.github.sunghyuk.overwatch.model.Player;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class PollingPlayerStatsService extends AbstractScheduledService {

    private final static Graphite GRAPHITE = new Graphite(new InetSocketAddress("54.89.101.217", 2003));

    private static final Logger LOG = LoggerFactory.getLogger(PollingPlayerStatsService.class);

    private final String playerName;
    private final OverwatchClient overwatchClient;
    private final LongGauge rankGauge = new LongGauge(0);

    public PollingPlayerStatsService(String playerName, OverwatchClient overwatchClient,
                                     MetricRegistry metrics) {
        this.playerName = playerName;
        this.overwatchClient = overwatchClient;
        metrics.register(String.format("%s.rank", playerName), rankGauge);
    }

    protected void runOneIteration() throws Exception {
        try {
            final Player player = overwatchClient.findPlayer(playerName)
                    .orElseThrow(() -> new IllegalStateException(String.format("%s player does not exist", playerName)));
            rankGauge.setValue(player.getRank().getRating());
        } catch (Throwable t) {
            LOG.error("Exception encountered trying to get player stats.", t);
        }
    }

    @Override
    protected String serviceName() {
        return playerName;
    }

    protected Scheduler scheduler() {
        return Scheduler.newFixedRateSchedule(0, 1, TimeUnit.MINUTES);
    }
}
