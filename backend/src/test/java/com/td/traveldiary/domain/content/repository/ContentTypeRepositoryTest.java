package com.td.traveldiary.domain.content.repository;

import com.td.traveldiary.domain.content.entity.ContentType;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ContentTypeRepositoryTest {

    @Autowired
    private ContentTypeRepository contentTypeRepository;

    @Test
    void findAll_returns_non_empty_list() {
        List<ContentType> result = contentTypeRepository.findAll();
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAll_returns_sorted_by_id_asc() {
        List<ContentType> result = contentTypeRepository.findAll();
        assertThat(result)
                .extracting(ContentType::getId)
                .isSortedAccordingTo(Long::compareTo);
    }
}