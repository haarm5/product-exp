package com.tmb.oneapp.productsexpservice.model.response.fund.information;

import com.tmb.oneapp.productsexpservice.model.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationResponse {

    private Status status;

    private InformationBody data;
}
