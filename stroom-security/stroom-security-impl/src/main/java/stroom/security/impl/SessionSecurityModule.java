package stroom.security.impl;

import stroom.util.guice.GuiceUtil;
import stroom.util.guice.ServletBinder;

import com.google.inject.AbstractModule;
import jakarta.servlet.http.HttpSessionIdListener;
import jakarta.servlet.http.HttpSessionListener;

public class SessionSecurityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SessionListService.class).to(SessionListListener.class);

        GuiceUtil.buildMultiBinder(binder(), HttpSessionListener.class)
                .addBinding(SessionListListener.class);

        GuiceUtil.buildMultiBinder(binder(), HttpSessionIdListener.class)
                .addBinding(SessionListListener.class);

        ServletBinder.create(binder())
                .bind(SessionListServlet.class);
    }
}
