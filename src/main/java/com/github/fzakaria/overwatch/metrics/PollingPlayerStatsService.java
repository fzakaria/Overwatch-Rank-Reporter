package com.github.fzakaria.overwatch.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.github.sunghyuk.overwatch.OverwatchClient;
import com.github.sunghyuk.overwatch.model.Player;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class PollingPlayerStatsService extends AbstractScheduledService {

    private static final Logger LOG = LoggerFactory.getLogger(PollingPlayerStatsService.class);

    private final String playerName;
    private final OverwatchClient overwatchClient;
    private final MetricRegistry metrics;

    public PollingPlayerStatsService(String playerName, OverwatchClient overwatchClient,
                                     MetricRegistry metrics) {
        this.playerName = playerName;
        this.overwatchClient = overwatchClient;
        this.metrics = metrics;
    }

    protected void runOneIteration() throws Exception {
        try {
            Player player = overwatchClient.findPlayer(playerName)
                    .orElseThrow(() -> new IllegalStateException(String.format("%s player does not exist", playerName)));
            String metric = String.format("%s.rank", playerName);
            metrics.register(metric, (Gauge<Integer>) () -> player.getRank().getRating());
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
