package com.td.traveldiary.domain.emotion.repository;

import com.td.traveldiary.domain.emotion.entity.EmotionType;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmotionTypeRepositoryTest {

    @Autowired
    private EmotionTypeRepository emotionTypeRepository;

    @Test
    void findAll_returns_non_empty_list() {
        List<EmotionType> result = emotionTypeRepository.findAll();
        assertThat(result).isNotEmpty();
    }

    @Test
    void findAll_returns_sorted_by_id_asc() {
        List<EmotionType> result = emotionTypeRepository.findAll();
        assertThat(result)
                .extracting(EmotionType::getId)
                .isSortedAccordingTo(Long::compareTo);
    }
}