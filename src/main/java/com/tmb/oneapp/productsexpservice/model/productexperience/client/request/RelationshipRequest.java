package com.tmb.oneapp.productsexpservice.model.productexperience.client.request;

import com.tmb.oneapp.productsexpservice.model.productexperience.client.CustomerClientModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RelationshipRequest extends CustomerClientModel {

}
