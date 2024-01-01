package com.example.dto;

import com.example.models.Cast;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CastDTO extends BaseDTO<Cast> {
    private String roleName;
    private Long movie_id;
    private Long person_id;

    public CastDTO(Cast cast){
        super(cast);
    }

    @Override
    public BaseDTO<Cast> convertToDTO(Cast entity) {
        setId(entity.getId());
        setMovie_id(entity.getMovie().getId());
        setPerson_id(entity.getPerson().getId());
        setRoleName(entity.getRoleName());
        setCreatedAt(entity.getCreatedAt());
        setUpdatedAt(entity.getUpdatedAt());
        return this;
    }

}
