package com.example.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PemKeyCertOptions;

public class Server extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		HttpServer server = vertx
				.createHttpServer(new HttpServerOptions().setUseAlpn(true).setSsl(true).setPemKeyCertOptions(
						new PemKeyCertOptions().setKeyPath("server-key.pem").setCertPath("server-cert.pem")))
				.websocketHandler(ws -> ws.handler(ws::writeBinaryMessage));

		server.requestHandler(req -> {
			String path = req.path();
			HttpServerResponse resp = req.response();

			switch (path) {
			case "/":
				resp.sendFile("index.html");
				break;
			case "/push":
				resp.push(HttpMethod.GET, "/img1.png", ar -> {
					if (ar.succeeded()) {
						System.out.println("sending push");
						HttpServerResponse pushedResp = ar.result();
						pushedResp.sendFile("img1.png");
					}
				});
				resp.push(HttpMethod.GET, "/img2.png", ar -> {
					if (ar.succeeded()) {
						System.out.println("sending push");
						HttpServerResponse pushedResp = ar.result();
						pushedResp.sendFile("img2.png");
					}
				});
				resp.sendFile("index.html");
				break;
			case "/ws":
				resp.sendFile("ws.html");
				break;
			case "/img1.png":
				resp.sendFile("img1.png");
				break;
			case "/img2.png":
				resp.sendFile("img2.png");
				break;
			default:
				System.out.println("Not found " + path);
				resp.setStatusCode(404).end();
			}
		});

		server.listen(8443, "localhost", ar -> {
			if (ar.succeeded()) {
				System.out.println("Server started at https://localhost:8443/");
			} else {
				ar.cause().printStackTrace();
			}
		});
	}
}
