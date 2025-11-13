package com.WebVipers.gemini.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

@Service
public class GeminiApiService {


	public String getResponse(MultipartFile file, String prompt) throws Exception {
		Client client = null;
		try {
			client = new Client();
		

		String mimeType = file.getContentType();
		byte[] fileBytes = file.getBytes();
		Content content = Content.fromParts(Part.fromText(prompt),
				Part.fromBytes(fileBytes, mimeType) // Specify the MIME type of the image
		);

		GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", content, null);
		
		return response.text();
		}catch (Exception e) {
			throw e;
		}finally {
			if (client!=null) {
				client.close();
			}
				
		}
	}

}
