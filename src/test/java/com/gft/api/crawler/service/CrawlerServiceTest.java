package com.gft.api.crawler.service;

import com.gft.api.crawler.domain.dto.CrawlDataDto;
import com.gft.api.crawler.domain.dto.CrawlResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CrawlerServiceTest {

    @InjectMocks
    private CrawlerService service;

    @Mock
    private CrawlResponse crawlResponse;

    @Mock
    private Optional<CrawlDataDto> crawlDataDto;

    @Test
    public void getCrawlDataById() {

        List<CrawlDataDto> list = new ArrayList<>();
        list.add(CrawlDataDto.builder().id("1").url("http://url.com").status("done").matchedUrls(new HashSet<>()).build());
        ReflectionTestUtils.setField(service, "list", list);

        assertEquals(list.get(0), service.getCrawlDataById(any()).get());
    }

}
