package hexlet.code.controllers;

import io.javalin.http.Handler;

public final class RootController {
    private static Handler main = ctx -> {
        ctx.render("index.html");
    };

    public static Handler getMain() {
        return main;
    }
}
