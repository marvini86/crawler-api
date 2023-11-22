package com.gft.api.crawler.controller;

import com.gft.api.crawler.domain.dto.CrawlDataDto;
import com.gft.api.crawler.domain.dto.CrawlResponse;
import com.gft.api.crawler.service.CrawlerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CrawlerControllerTest {

    @InjectMocks
    private CrawlerController controller;

    @Mock
    private CrawlerService service;

    @Mock
    private CrawlResponse crawlResponse;

    @Mock
    private Optional<CrawlDataDto> crawlDataDto;

    @Test
    public void crawl_Successfull() throws ExecutionException, InterruptedException {
        when(service.crawl(any())).thenReturn(crawlResponse);
        ResponseEntity response = controller.crawl(any());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service).crawl(any());
    }

    @Test
    public void crawl_Failed() throws ExecutionException, InterruptedException {
        when(service.crawl(any())).thenThrow(ExecutionException.class);
        ResponseEntity response = controller.crawl(any());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(service).crawl(any());
    }

    @Test
    public void getById() {
        when(service.getCrawlDataById(any())).thenReturn(crawlDataDto);
        when(crawlDataDto.isEmpty()).thenReturn(false);
        ResponseEntity response = controller.getById(any());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service).getCrawlDataById(any());
    }

    @Test
    public void getById_Empty() {
        when(service.getCrawlDataById(any())).thenReturn(crawlDataDto);
        when(crawlDataDto.isEmpty()).thenReturn(true);
        ResponseEntity response = controller.getById(any());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(service).getCrawlDataById(any());
    }


}
