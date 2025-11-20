package com.WebVipers.gemini.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.WebVipers.gemini.model.BackStageResponseDTO;
import com.WebVipers.gemini.model.BackstageRequestDTO;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

@Service
public class GeminiApiService {

	@Value("${root.directory.scafold.code}")
	private String rootDirectoryScafoldCode;
	
	@Value("${root.directory.spec}")
	private String rootDirectorySpec;
	

	@Value("${backstage.url.location}")
	private String backstageUrlLocation;

	@Value("${backstage.catalog.path}")
	private String backstageCatalogPath;

	@Value("${git.spec.path}")
	private String repositoryPath;

	@Value("${git.remote.url.generated.code}")
	private String remoteUrlGeneratedCode;
	
	@Value("${git.remote.url.spec}")
	private String remoteUrlSpec;
	
	
	@Autowired
	private GitService gitService;

	@Autowired
	private WebClientService webClientService;

	public String getScafoldResponse(String prompt) throws Exception {
		Client client = null;
		try {
			client = new Client();

			Content content = Content.fromParts(Part.fromText(generatePrompt(prompt, "Instructions.md")));

			GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", content, null);
			String finalResponse = response.text();

			// ClassPathResource resource = new ClassPathResource("readme.txt");
//			String finalResponse = null;
//			try (InputStream inputStream = resource.getInputStream()) {
//				finalResponse = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
//			}
			generateFiles(finalResponse);
			gitService.commitAndPush(prompt, ".",rootDirectoryScafoldCode,remoteUrlGeneratedCode);
			createServiceCatalog();
			return finalResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null) {
				client.close();
			}

		}
	}

	public String getSpecResponse(String prompt) throws Exception {
		Client client = null;
		try {
			client = new Client();
			String finalResponse = null;
			ClassPathResource resource = new ClassPathResource("Agent-template.md");
			try (InputStream inputStream = resource.getInputStream()) {
				Content content = Content.fromParts(Part.fromText(generatePrompt(prompt, "Instructions-spec.md")),
						Part.fromBytes(inputStream.readAllBytes(), "text/plain"));
				GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", content, null);
				finalResponse = response.text();
			}
			String fileName="Agent-"+prompt.replaceAll(" ", "-")+".md";
			writeContent(finalResponse, rootDirectorySpec,  fileName);
			gitService.commitAndPush(prompt, ".",rootDirectorySpec,remoteUrlSpec);
			return finalResponse;
		} catch (Exception e) {
			throw e;
		} finally {
			if (client != null) {
				client.close();
			}

		}
	}

	public void createServiceCatalog() {

		BackstageRequestDTO request = new BackstageRequestDTO("url", backstageCatalogPath);
		BackStageResponseDTO response = webClientService.callPostApi(backstageUrlLocation, request,
				BackStageResponseDTO.class);
	}

	public static void deleteDirectoryWithStream(Path directory) throws IOException {
		if (!Files.exists(directory)) {
			return;
		}

		try (var stream = Files.walk(directory)) {
			stream.sorted((a, b) -> b.compareTo(a)) // Reverse order to delete files before directories
					.forEach(path -> {
						try {
							if (!path.toString().contains(".git")
									&& !path.toString().equalsIgnoreCase(directory.toString()))
								Files.delete(path);
						} catch (IOException e) {
							throw new RuntimeException("Failed to delete: " + path, e);
						}
					});
		}
	}

	private String generatePrompt(String prompt, String instructionFile) throws IOException {
		ClassPathResource resource = new ClassPathResource(instructionFile);
		try (InputStream inputStream = resource.getInputStream()) {
			String promptContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			promptContent = promptContent.replaceAll("\\{CHAT_BOT_CONTENT\\}", prompt);
			return promptContent;
		}
	}

	private Boolean generateFiles(String response) throws IOException {
		deleteDirectoryWithStream(Path.of(rootDirectoryScafoldCode));
		writeContent(response, rootDirectoryScafoldCode, "README.md");
		createFiles(Path.of(rootDirectoryScafoldCode));
		addCatlogFile();
		return true;
	}

	private void addCatlogFile() throws IOException {
		ClassPathResource resource = new ClassPathResource("catalog-info.yaml");
		try (InputStream inputStream = resource.getInputStream()) {
			String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			writeContent(content, rootDirectoryScafoldCode, "catalog-info.yaml");
		}
	}

	private void createFiles(Path filePath) throws IOException {

		List<String> lines = Files.readAllLines(Path.of(rootDirectoryScafoldCode + "README.md"));
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
					path = path + "/";
				}
			}
			if (line.startsWith("&&&&")) {
				fileName = line.substring(line.lastIndexOf('&') + 1);
			}
			if (line.startsWith("@@@@")) {
				readContent = readContent ? false : true;
				if (!readContent) {
					if (fileName != null)
						writeContent(buffer.toString(), rootDirectoryScafoldCode+path.trim(), fileName.trim());
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

	private void writeContent(String content, String codePath, String filename) throws IOException {
		Files.createDirectories(Path.of(codePath));
		String file = codePath + filename;
		Files.createFile(Path.of(file));
		Files.writeString(Path.of(file), content);

	}

}
