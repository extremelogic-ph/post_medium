/*
 * Copyright 2023 Extreme Logic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ph.extremelogic.web.postmedium;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import ph.extremelogic.web.postmedium.dto.MediumRequest;

/**
 * The main application class for posting to Medium.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {
    // Disable database, we will not be using it for now
    DataSourceAutoConfiguration.class,
})
@Slf4j
// Use as a command line application and not a web
public class PostMediumApplication implements CommandLineRunner {
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";


    @Value("${medium.account.integration-token}")
    private String token;

    @Value("${medium.account.author-id}")
    private String authorId;

    @Value("${medium.content-format:markdown}")
    private String contentFormat;

    @Value("${medium.tags}")
    private String[] tags;

    @Value("${medium.file}")
    private String file;

    @Value("${medium.file-path}")
    private String filePath;

    @Value("${medium.publish-status}")
    private String publishStatus;

    @Value("${medium.canonical-url}")
    private String canonicalUrl;

    @Value("${medium.api-base-url}")
    private String baseUrl;

    @Autowired
    private ConfigurableApplicationContext context;

    /**
     * Reads the content of a file.
     *
     * @param path     The directory path.
     * @param file     The file name.
     * @param encoding The character encoding of the file.
     * @return The content of the file as a string.
     * @throws IOException If an I/O error occurs.
     */
    private static String readFile(String path, String file, Charset encoding)
        throws IOException {
        var directoryPath = Paths.get(path);
        var filePath = directoryPath.resolve(file);
        var encoded = Files.readAllBytes(filePath);
        return new String(encoded, encoding);
    }

    /**
     * The main entry point of the application.
     *
     * @param args The command-line arguments.
     */
    public static void main(final String[] args) {
        SpringApplication.run(PostMediumApplication.class, args);
    }

    /**
     * Sends a POST request to Medium to publish a new post.
     *
     * @throws URISyntaxException If the URI syntax is invalid.
     * @throws IOException        If an I/O error occurs.
     */
    private void post() throws URISyntaxException, IOException {
        var restTemplate = new RestTemplate();
        var uri = new URI(baseUrl);
        var content = readFile(filePath, file, StandardCharsets.UTF_8);

        var headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer " + token);
        headers.add(CONTENT_TYPE, "application/json");

        var mediumRequest = new MediumRequest();
        mediumRequest.setCanonicalUrl(canonicalUrl);
        mediumRequest.setContent(content);
        mediumRequest.setContentFormat(contentFormat);
        mediumRequest.setTags(Arrays.asList(tags));
        mediumRequest.setPublishStatus(publishStatus);

        var title = "";
        var fileContents = content.split(System.lineSeparator());
        title = fileContents[0].replace("# ", "");

        log.info("Title : {}", title);
        mediumRequest.setTitle(title);

        var request = new HttpEntity<>(mediumRequest, headers);
        var result = restTemplate.postForEntity(uri, request, String.class);

        log.info("Medium post response: {}", result);
    }

    /**
     * Checks if the provided parameters are valid.
     *
     * @return {@code true} if the parameters are valid, {@code false} otherwise.
     */
    private boolean validParams() {
        if (StringUtils.isBlank(token)) {
            log.error("Configuration error. No medium token provided");
            return false;
        }
        if (StringUtils.isBlank(authorId)) {
            log.error("Configuration error. No medium author id provided");
            return false;
        }
        if (StringUtils.isBlank(contentFormat)) {
            return false;
        }
        log.info("Content format: {}", contentFormat);
        log.info("Tags: {}", tags);
        return true;

    }

    @Override
    public void run(String... args) {
        if (validParams()) {
            try {
                post();
            } catch (IOException | URISyntaxException e) {
                log.error("Unable to post {} {}", contentFormat,
                    e.getLocalizedMessage());
            }
        }
        System.exit(SpringApplication.exit(context));
    }
}

