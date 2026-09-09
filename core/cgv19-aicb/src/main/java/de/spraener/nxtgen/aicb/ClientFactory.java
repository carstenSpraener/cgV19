package de.spraener.nxtgen.aicb;

import java.io.IOException;

public class ClientFactory {

    private static volatile LlmClient singleton = null;

    public static synchronized LlmClient get() {
        if (singleton != null) {
            return singleton;
        }

        Config cfg = Config.load();
        if (cfg == null) {
            singleton = new MockLlmClient();
        } else {
            try {
                singleton = new LangChain4jClient(cfg);
            } catch (IllegalStateException e) {
                singleton = new MockLlmClient();
            }
        }
        return singleton;
    }

    static synchronized void reset() {
        singleton = null;
    }
}
