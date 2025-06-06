package com.ODG.ODG_back.dto.participant;

public interface EntityMapper<D, E> {
    E toEntity(D dto);
    D toDto(E entity);
}
