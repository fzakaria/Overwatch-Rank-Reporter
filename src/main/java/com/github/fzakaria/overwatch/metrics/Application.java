package com.github.fzakaria.overwatch.metrics;

import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.inject.Inject;

/**
 * Entry point into the application
 */
public final class Application {

    private final ServiceManager serviceManager;

    @Inject
    private Application(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public static void main(String [] args) {
        Injector injector = Guice.createInjector(new ApplicationModule());
        Application application = injector.getInstance(Application.class);
        application.run();
    }

    private void run() {
        serviceManager.startAsync().awaitHealthy();
    }

}
