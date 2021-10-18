package hexlet.code;

import io.javalin.Javalin;

public final class App {
    private static final String DEFAULT_PORT = "5000";

    private static int getPort() {
        final String port = System.getenv().getOrDefault("PORT", DEFAULT_PORT);
        return Integer.valueOf(port);
    }

    private static void addRoutes(Javalin app) {
        app.get("/", ctx -> ctx.result("Hello World"));
    }

    public static Javalin getApp() {
        final Javalin app = Javalin.create(config -> {
            config.enableDevLogging();
        });
        addRoutes(app);

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
