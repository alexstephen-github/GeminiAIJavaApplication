package com.WebVipers.gemini.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

@Service
public class GeminiApiService {

	@Value("${root.directory}")
	private String rootDirectory;

	public String getResponse(String prompt) throws Exception {
		Client client = null;
		try {
			client = new Client();

			Content content = Content.fromParts(Part.fromText(generatePrompt(prompt)));

			GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", content, null);
			String finalResponse = response.text();
			/*
			 * ClassPathResource resource = new ClassPathResource("readme.txt"); String
			 * finalResponse = null; try (InputStream inputStream =
			 * resource.getInputStream()) { finalResponse = new
			 * String(inputStream.readAllBytes(), StandardCharsets.UTF_8); }
			 */
			generateFiles(finalResponse);
			return finalResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null) {
				client.close();
			}

		}
	}

	public static void deleteDirectoryWithStream(Path directory) throws IOException {
		if (!Files.exists(directory)) {
			return;
		}

		try (var stream = Files.walk(directory)) {
			stream.sorted((a, b) -> b.compareTo(a)) // Reverse order to delete files before directories
					.forEach(path -> {
						try {
							Files.delete(path);
						} catch (IOException e) {
							throw new RuntimeException("Failed to delete: " + path, e);
						}
					});
		}
	}

	private String generatePrompt(String prompt) throws IOException {
		ClassPathResource resource = new ClassPathResource("Instructions.md");
		try (InputStream inputStream = resource.getInputStream()) {
			String promptContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			promptContent = promptContent.replaceAll("\\{CHAT_BOT_CONTENT\\}", prompt);
			return promptContent;
		}
	}

	private Boolean generateFiles(String response) throws IOException {
		deleteDirectoryWithStream(Path.of(rootDirectory));
		writeContent(response, "", "README.md");
		createFiles(Path.of(rootDirectory));
		return true;
	}

	public void createFiles(Path filePath) throws IOException {

		List<String> lines = Files.readAllLines(Path.of(rootDirectory + "README.md"));
		String path = "";
		String fileName = null;
		StringBuffer buffer = new StringBuffer();
		Boolean readContent = false;
		for (String line : lines) {
			if (line.startsWith("$$$$")) {
				path = line.substring(line.lastIndexOf('$') + 1);
				if (path.contains("your-project-root")) {
					path = "";
				}
				if (!path.endsWith("/")) {
					path=path+"/";
				}
			}
			if (line.startsWith("&&&&")) {
				fileName = line.substring(line.lastIndexOf('&') + 1);
			}
			if (line.startsWith("@@@@")) {
				readContent = readContent ? false : true;
				if (!readContent) {
					if (fileName != null)
						writeContent(buffer.toString(), path.trim(), fileName.trim());
					path = "";
					fileName = null;
					buffer = new StringBuffer();
				}
			} else if (readContent) {
				buffer.append(line);
				buffer.append("\n");
			}

		}

	}

	public void writeContent(String content, String filePath, String filename) throws IOException {
		String codePath = rootDirectory + filePath;
		Files.createDirectories(Path.of(codePath));
		String file = codePath + filename;
		Files.createFile(Path.of(file));
		Files.writeString(Path.of(file), content);

	}

}
