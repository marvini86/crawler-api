package com.gft.api.crawler.service;


import com.gft.api.crawler.domain.dto.CrawlDataDto;
import com.gft.api.crawler.domain.dto.CrawlPayload;
import com.gft.api.crawler.domain.dto.CrawlResponse;
import com.gft.api.crawler.domain.entity.CrawlStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for managing the crawler business rule
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CrawlerService {
    private static List<CrawlDataDto> list = new ArrayList<>();

    private final int NUM_OF_THREADS = 50;

    /**
     * Gets crawl data by id
     *
     * @param id
     * @return Optional<CrawlDataDto>
     */
    public Optional<CrawlDataDto> getCrawlDataById(String id) {
        return list.stream().filter(x -> x.getId().equals(id)).findFirst();
    }

    /**
     * init the crawl processor
     *
     * @param payload
     * @return CrawlResponse
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @SneakyThrows
    public CrawlResponse crawl(CrawlPayload payload) throws ExecutionException, InterruptedException {
        var id = getId(); // generate id

        log.info("Generated id: {}", id);

        var executor = Executors.newFixedThreadPool(NUM_OF_THREADS);

        /**
         * run async crawl page
         */
        CompletableFuture.supplyAsync(() -> crawlPage(payload.getUrl(), payload.getKeyword(), id), executor);

        return new CrawlResponse(id);
    }

    /**
     * Process the crawl task
     *
     * @param keyword
     * @param id
     * @return CrawlDataDto
     */
    private CrawlDataDto crawlPage(String baseUrl, String keyword, String id) {
        Queue<String> urlsToVisit = new LinkedList<>();
        List<String> visitedUrls = new ArrayList<>();

        var crawlData = CrawlDataDto.builder()
                .id(id)
                .url(baseUrl)
                .keyword(keyword)
                .status(CrawlStatus.ACTIVE.toString())
                .matchedUrls(new HashSet<>())
                .build();

        this.saveObjectState(crawlData);

        /**
         * set the first element with the base Url
         */
        urlsToVisit.add(baseUrl);

        /**
         * iterate over url to visit
         */
        while (!urlsToVisit.isEmpty()) {

            var urlToCrawl = urlsToVisit.remove();

            /**
             * gets the page content
             */
            var content = crawlPageContent(urlToCrawl);

            /**
             * if content is null for some reason skips this page
             */
            if (content == null) {
                log.info("No content found");
                continue;
            }

            /**
             * adds the crawled url to visited list control
             */
            visitedUrls.add(urlToCrawl);


            /**
             * if the keyword exists in page content, crawl data will be updated
             */
            if (content.toLowerCase().indexOf(keyword.toLowerCase()) > 0) {
                crawlData.getMatchedUrls().add(urlToCrawl);
            }

            /**
             *  match pattern to extract the values from href links
             */
            Pattern urlPattern = Pattern.compile("href=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = urlPattern.matcher(content);

            /**
             * process the matches
             */
            while (matcher.find()) {
                String match = matcher.group(1);
                String url = buildUrl(baseUrl, match);

                /**
                 * if it`s a valid url and not present in control lists, will be added to queue
                 */
                if (isValidURL(url) && !visitedUrls.contains(url) && !urlsToVisit.contains(url)) {
                    urlsToVisit.add(url);
                    log.info("Found the following url to crawl: {} ", url);
                }
            }
        }

        crawlData.setStatus(CrawlStatus.DONE.toString());

        return crawlData;

    }

    /**
     * builds an url from match
     *
     * @param match
     * @return String
     */
    private String buildUrl(String baseUrl, String match) {
        String url = "";

        if ((match.indexOf(".html") > 0 || match.indexOf(".htm") > 0)
                && (match.indexOf(baseUrl) == -1 && match.indexOf("http") == -1)) {
            url = baseUrl.concat("/").concat(match.replaceAll("^/+", "").replace("../", ""));
        } else if (match.indexOf(baseUrl) > 0) {
            log.info("Url same path {} ", match);
            url = match;
        }

        return url;
    }

    /**
     * crawl a page and get its content
     *
     * @param urlToCrawl
     * @return
     */
    private String crawlPageContent(String urlToCrawl) {
        log.info("Crawling the url: {}", urlToCrawl);

        try {
            // get URL content
            var url = new URL(urlToCrawl);
            var conn = url.openConnection();

            // init BufferedReader
            var br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuilder htmlPage = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                htmlPage.append(inputLine);
            }
            br.close();

            return htmlPage.toString();
        } catch (MalformedURLException e) {
            log.error(e.toString());
        } catch (IOException e) {
            log.error(e.toString());
        }

        return null;
    }

    /**
     * check if it`s a valid URL
     *
     * @param url
     * @return
     */
    private boolean isValidURL(String url) {
        boolean result = false;
        try {
            new URL(url).toURI();
            result = true;
        } catch (Exception e) {
        }

        return result;
    }

    /**
     * generates new id
     *
     * @return
     */
    private String getId() {
        int strSize = 8;
        SecureRandom random = new SecureRandom();
        StringBuffer buff = new StringBuffer(strSize);
        String CHARACTER_SET = "0123456789abcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < strSize; i++) {
            int offset = random.nextInt(CHARACTER_SET.length());
            buff.append(CHARACTER_SET.substring(offset, offset + 1));
        }
        return buff.toString();
    }

    /**
     * saves object state
     *
     * @param crawlData
     */
    private void saveObjectState(CrawlDataDto crawlData) {
        list.stream().filter(x -> x.getId().equals(crawlData.getId())).findFirst().ifPresent(list::remove);
        list.add(crawlData);
    }


}
