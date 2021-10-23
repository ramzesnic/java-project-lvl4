package hexlet.code.controllers;

import io.javalin.http.Context;

public final class RootController {
    public static void main(Context ctx) {
        ctx.render("index.html");
    }
}
