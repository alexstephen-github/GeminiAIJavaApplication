package com.WebVipers.gemini.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaData {
	private Map<String, String> annotations;
	private String name;
	private String namespace;

}
