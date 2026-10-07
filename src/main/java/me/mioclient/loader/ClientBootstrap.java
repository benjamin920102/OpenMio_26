package me.mioclient.loader;

import me.mioclient.api.ClientServices;

public class ClientBootstrap {
    public static ClientBootstrap helper_2 = new ClientBootstrap();

    public ClientBootstrap() {
        init();
    }

    public static ClientBootstrap getHelper_2159() {
        if (helper_2 == null) {
            helper_2 = new ClientBootstrap();
        }
        return helper_2;
    }

    public void init() {
        ClientServices.initialize();
    }
}
