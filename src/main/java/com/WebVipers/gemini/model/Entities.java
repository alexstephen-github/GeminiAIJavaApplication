package com.WebVipers.gemini.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entities {
	
	private String apiVersion;
	private String kind;
	private Spec spec;
	private MetaData metadata;
}
