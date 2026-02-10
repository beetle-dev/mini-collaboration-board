package com.minicollaborationboard.global.common.service;

import com.minicollaborationboard.global.common.entity.Sequence;
import com.minicollaborationboard.global.common.repository.SequenceRepository;
import com.minicollaborationboard.global.exception.DuplicateResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SequenceService {

    private final SequenceRepository sequenceRepository;

    public void createSequence(String code) {

        if (sequenceRepository.existsByCode(code)) {

            throw new DuplicateResourceException("이미 시퀀스가 생성된 코드입니다.");
        }

        sequenceRepository.save(Sequence.builder().code(code).build());
    }

    public void incrementSequence(String code) {

        if (!sequenceRepository.existsByCode(code)) {

            throw new DuplicateResourceException("해당 코드의 시퀀스가 없습니다.");
        }

        sequenceRepository.increamentSequence(code);
    }

    public Long findLastInsertId() {

        return sequenceRepository.findLastInsertId();
    }
}
