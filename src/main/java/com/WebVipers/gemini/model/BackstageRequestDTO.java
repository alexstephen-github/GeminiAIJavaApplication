package com.WebVipers.gemini.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackstageRequestDTO {
	private String type;
	private String target;
}
