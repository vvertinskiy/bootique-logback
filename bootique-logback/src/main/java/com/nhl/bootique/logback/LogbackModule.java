package com.nhl.bootique.logback;

import ch.qos.logback.classic.Logger;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.nhl.bootique.ConfigModule;
import com.nhl.bootique.annotation.LogLevels;
import com.nhl.bootique.config.ConfigurationFactory;
import com.nhl.bootique.shutdown.ShutdownManager;

import java.util.Map;

public class LogbackModule extends ConfigModule {

    public LogbackModule(String configPrefix) {
        super(configPrefix);
    }

    public LogbackModule() {
    }

    @Override
    protected String defaultConfigPrefix() {
        return "log";
    }

    @Override
    public void configure(Binder binder) {
        // Binding a dummy class to trigger eager init of Logback as
        // @Provides below can not be invoked eagerly..
        binder.bind(LogInitTrigger.class).asEagerSingleton();
    }

    @Singleton
    @Provides
    Logger provideRootLogger(ConfigurationFactory configFactory,
                             ShutdownManager shutdownManager,
                             @LogLevels Map<String, java.util.logging.Level> defaultLevels) {

        return configFactory
                .config(LogbackContextFactory.class, configPrefix)
                .createRootLogger(shutdownManager, defaultLevels);
    }

    static class LogInitTrigger {

        @Inject
        public LogInitTrigger(Logger rootLogger) {
            rootLogger.debug("Logback started");
        }
    }

}
