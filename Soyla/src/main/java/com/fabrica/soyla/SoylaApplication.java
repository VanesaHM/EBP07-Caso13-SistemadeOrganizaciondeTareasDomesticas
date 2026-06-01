package com.fabrica.soyla;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SoylaApplication {

	public static void main(String[] args) {
		configureRenderDatabaseConnection();
		SpringApplication.run(SoylaApplication.class, args);
	}

	private static void configureRenderDatabaseConnection() {
		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl == null || databaseUrl.isBlank()) {
			return;
		}

		if (!(databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("postgres://"))) {
			return;
		}

		try {
			URI uri = new URI(databaseUrl);
			String host = uri.getHost();
			int port = uri.getPort() > 0 ? uri.getPort() : 5432;
			String databaseName = uri.getPath() != null ? uri.getPath().replaceFirst("^/", "") : "";
			String query = uri.getQuery();

			if (host == null || databaseName.isBlank()) {
				return;
			}

			String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
			if (query != null && !query.isBlank()) {
				jdbcUrl += "?" + query;
			}

			System.setProperty(
				"spring.datasource.url",
				jdbcUrl
			);

			String userInfo = uri.getRawUserInfo();
			if (userInfo == null || userInfo.isBlank()) {
				return;
			}

			String[] credentials = userInfo.split(":", 2);
			if (credentials.length > 0 && !credentials[0].isBlank()) {
				System.setProperty("spring.datasource.username", decodeUrlPart(credentials[0]));
			}

			if (credentials.length > 1 && !credentials[1].isBlank()) {
				System.setProperty("spring.datasource.password", decodeUrlPart(credentials[1]));
			}
		} catch (URISyntaxException exception) {
			throw new IllegalStateException("No se pudo interpretar DATABASE_URL", exception);
		}
	}

	private static String decodeUrlPart(String value) {
		return URLDecoder.decode(value, StandardCharsets.UTF_8);
	}

}
