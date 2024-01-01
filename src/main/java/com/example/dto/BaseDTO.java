package com.example.dto;

import com.example.models.MainModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseDTO <U extends MainModel> {
    private long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public BaseDTO(U entity) {convertToDTO(entity);}
    public abstract BaseDTO<U> convertToDTO(U entity);
}
