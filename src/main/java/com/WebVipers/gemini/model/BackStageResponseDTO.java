package com.WebVipers.gemini.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackStageResponseDTO {
	private LocationDTO location;
	private Boolean exists;
	private List<Entities> entities;
}
