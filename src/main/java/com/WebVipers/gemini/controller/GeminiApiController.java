package com.WebVipers.gemini.controller;

import java.util.HashMap;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.WebVipers.gemini.service.GeminiApiService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class GeminiApiController {

	private static final Logger LOG = Logger.getLogger(GeminiApiController.class.getName());

	@Autowired
	private GeminiApiService geminiApiService;

	@PostMapping("/processrequest")
	public ResponseEntity<HashMap<String, Object>> processRequest(@RequestParam("prompt") String prompt,@RequestParam("agent") String agent) {
		LOG.info("\n\nINSIDE CLASS == GeminiApiController, METHOD == Process Request(); ");

		try {
			
			String result = null;
			
			if (agent.equalsIgnoreCase("Scaffold")) {
			 result = geminiApiService.getScafoldResponse(prompt);
			}else {
				result = geminiApiService.getSpecResponse(prompt);
			}
			if (result != null) {
				LOG.info("\nRequest processed successfully.");
				return getResponseFormat(HttpStatus.OK, "Success", result);
			} else {
				LOG.info("\nRequest processing failed.");
				LOG.info("\nEXITING METHOD == processRequest() OF CLASS == GeminiApiController \n\n");
				return getResponseFormat(HttpStatus.INTERNAL_SERVER_ERROR, "Failure", "unable to read data");
			}
		} catch (Exception e) {
			LOG.severe("\nError in processRequest() method of GeminiApiController: " + e.getMessage());
			LOG.info("\nEXITING METHOD == processRequest() OF CLASS == GeminiApiController \n\n");
			return getResponseFormat(HttpStatus.INTERNAL_SERVER_ERROR, "Failure", e.getMessage());
		}
	}
	
	public ResponseEntity<HashMap<String, Object>> getResponseFormat(HttpStatus status, String message, Object data) {
		int responseStatus = (status.equals(HttpStatus.OK)) ? 1 : 0;

		HashMap<String, Object> map = new HashMap<>();
		map.put("responseCode", responseStatus);
		map.put("message", message);
		map.put("data", data);
		return ResponseEntity.status(status).body(map);
	}
}
