package com.tmb.oneapp.productsexpservice.model.flexiloan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CheckSystemOffResponse {
    private Boolean isSystemOff;
    private String systemOnTime;
    private String systemOffTime;
}