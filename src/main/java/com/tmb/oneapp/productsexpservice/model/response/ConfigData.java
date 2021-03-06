package com.tmb.oneapp.productsexpservice.model.response;

import lombok.*;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.HashMap;

/**
 * ConfigData is document which will store into Mongo as record
 *
 */
@Data
@Setter
@Getter
@NoArgsConstructor
@ToString
public class ConfigData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7485173526514272196L;
	@Id
	private String id;
	private String channel;
	private HashMap<String, String> details;
	@JsonProperty("image_urls")
	private HashMap<String, String> imgUrls;
}
