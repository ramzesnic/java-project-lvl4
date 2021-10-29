package hexlet.code;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.path;

import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

public final class App {
    private static final String DEFAULT_PORT = "5000";
    private static final String PORT_ENV = "PORT";
    private static final String APP_ENV = "APP_ENV";
    private static final String DEV_MODE = "development";
    private static final String TEMPLATE_PREFIX = "/templates/";

    private static int getPort() {
        final String port = System.getenv().getOrDefault(PORT_ENV, DEFAULT_PORT);
        return Integer.valueOf(port);
    }

    private static boolean isDevelopment() {
        return System.getenv()
                .getOrDefault(APP_ENV, DEV_MODE)
                .equals(DEV_MODE);
    }

    private static TemplateEngine getTemplateEngine() {
        final TemplateEngine templateEngine = new TemplateEngine();
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix(TEMPLATE_PREFIX);
        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", RootController::main);
        app.routes(() -> {
            path("urls", () -> {
                get(UrlController::getUrls);
                post(UrlController::create);
                path("{id}", () -> {
                    get(UrlController::getUrl);
                    path("checks", () -> {
                        post(UrlController::checkUrl);
                    });
                });
            });
        });
    }

    public static Javalin getApp() {
        final Javalin app = Javalin.create(config -> {
            if (isDevelopment()) {
                config.enableDevLogging();
            }
            config.enableWebjars();
            JavalinThymeleaf.configure(getTemplateEngine());
        });
        addRoutes(app);

        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });

        return app;
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
}
