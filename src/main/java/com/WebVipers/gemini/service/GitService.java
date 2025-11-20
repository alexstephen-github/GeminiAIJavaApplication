package com.WebVipers.gemini.service;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitService {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);

    @Value("${git.username}")
    private String username;

    @Value("${git.password}")
    private String password;

    @Value("${git.branch}")
    private String branch;

    @Value("${git.user.name")
    private String gitUserName;

    @Value("${git.user.email}")
    private String gitUserEmail;

    /**
     * Initialize a Git repository if it doesn't exist
     * @throws URISyntaxException 
     */
    public String initializeRepository(String repositoryPath, String remoteUrl) throws IOException, GitAPIException, URISyntaxException {
        File repoDir = new File(repositoryPath);
        
        if (!repoDir.exists()) {
            repoDir.mkdirs();
        }

        File gitDir = new File(repoDir, ".git");
        if (!gitDir.exists()) {
            Git.init().setDirectory(repoDir).call();
            logger.info("Initialized new Git repository at: {}", repositoryPath);
            
            // Set remote if provided
            if (remoteUrl != null && !remoteUrl.isEmpty()) {
                try (Git git = Git.open(repoDir)) {
                    git.remoteAdd()
                        .setName("origin")
                        .setUri(new org.eclipse.jgit.transport.URIish(remoteUrl))
                        .call();
                    logger.info("Added remote 'origin': {}", remoteUrl);
                }
            }
            return "Repository initialized successfully at: " + repositoryPath;
        } else {
            return "Repository already exists at: " + repositoryPath;
        }
    }

    /**
     * Get the status of the repository
     */
    public String getStatus(String repositoryPath) throws IOException, GitAPIException {
        File repoDir = new File(repositoryPath);
        
        if (!new File(repoDir, ".git").exists()) {
            return "No Git repository found at: " + repositoryPath;
        }

        try (Git git = Git.open(repoDir)) {
            Status status = git.status().call();
            
            StringBuilder sb = new StringBuilder();
            sb.append("Repository Status:\n");
            sb.append("Branch: ").append(git.getRepository().getBranch()).append("\n\n");
            
            Set<String> added = status.getAdded();
            Set<String> modified = status.getModified();
            Set<String> untracked = status.getUntracked();
            Set<String> removed = status.getRemoved();
            
            if (!added.isEmpty()) {
                sb.append("Added files:\n");
                added.forEach(f -> sb.append("  + ").append(f).append("\n"));
            }
            
            if (!modified.isEmpty()) {
                sb.append("Modified files:\n");
                modified.forEach(f -> sb.append("  M ").append(f).append("\n"));
            }
            
            if (!untracked.isEmpty()) {
                sb.append("Untracked files:\n");
                untracked.forEach(f -> sb.append("  ? ").append(f).append("\n"));
            }
            
            if (!removed.isEmpty()) {
                sb.append("Removed files:\n");
                removed.forEach(f -> sb.append("  - ").append(f).append("\n"));
            }
            
            if (added.isEmpty() && modified.isEmpty() && untracked.isEmpty() && removed.isEmpty()) {
                sb.append("Working tree clean\n");
            }
            
            return sb.toString();
        }
    }

    /**
     * Add files to staging area
     */
    public String addFiles(String pattern,String repositoryPath) throws IOException, GitAPIException {
        File repoDir = new File(repositoryPath);
        
        if (!new File(repoDir, ".git").exists()) {
            throw new IllegalStateException("No Git repository found at: " + repositoryPath);
        }

        try (Git git = Git.open(repoDir)) {
            if (pattern == null || pattern.isEmpty() || pattern.equals(".")) {
                git.add().addFilepattern(".").call();
                return "All files added to staging area";
            } else {
                git.add().addFilepattern(pattern).call();
                return "Files matching '" + pattern + "' added to staging area";
            }
        }
    }

    /**
     * Commit changes
     */
    public String commit(String message,String repositoryPath) throws IOException, GitAPIException {
        File repoDir = new File(repositoryPath);
        
        if (!new File(repoDir, ".git").exists()) {
            throw new IllegalStateException("No Git repository found at: " + repositoryPath);
        }

        try (Git git = Git.open(repoDir)) {
            // Configure Git user identity
            var config = git.getRepository().getConfig();
            config.setString("user", null, "name", gitUserName);
            config.setString("user", null, "email", gitUserEmail);
            config.save();
            
            git.commit()
                .setMessage(message)
                .setAuthor(gitUserName, gitUserEmail)
                .setCommitter(gitUserName, gitUserEmail)
                .call();
            
            logger.info("Committed changes with message: {} (by {} <{}>)", message, gitUserName, gitUserEmail);
            return "Successfully committed with message: " + message;
        }
    }

    /**
     * Push changes to remote
     */
    public String push(String repositoryPath, String remoteUrl) throws IOException, GitAPIException {
        File repoDir = new File(repositoryPath);
        
        if (!new File(repoDir, ".git").exists()) {
            throw new IllegalStateException("No Git repository found at: " + repositoryPath);
        }

        if (remoteUrl == null || remoteUrl.isEmpty()) {
            return "ERROR: No remote URL configured. Please set git.remote.url in configuration";
        }

        if (username == null || username.isEmpty()) {
            return "ERROR: No username configured. Please set git.username in configuration";
        }

        if (password == null || password.isEmpty()) {
            return "ERROR: No password/token configured. Please set git.password in configuration";
        }

        try (Git git = Git.open(repoDir)) {
            logger.info("Attempting to push to remote: {} as user: {}", remoteUrl, username);
            logger.info("Current branch: {}", git.getRepository().getBranch());
            
            PushCommand pushCommand = git.push();
            
            // Set credentials
            pushCommand.setCredentialsProvider(
                new UsernamePasswordCredentialsProvider(username, password)
            );
            
            // Set remote and branch explicitly
            pushCommand.setRemote("origin");
            pushCommand.add(branch);
            
            // Set upstream branch on first push
            pushCommand.setForce(false);
            
            // Execute push and get result
            var pushResults = pushCommand.call();
            
            // Check push results
            StringBuilder resultMsg = new StringBuilder();
            resultMsg.append("Push completed to: ").append(remoteUrl).append("\n");
            
            pushResults.forEach(result -> {
                logger.info("Push result for remote: {}", result.getRemoteUpdates());
                result.getRemoteUpdates().forEach(update -> {
                    logger.info("  Remote ref: {} - Status: {} - Message: {}", 
                        update.getRemoteName(), 
                        update.getStatus(), 
                        update.getMessage());
                    resultMsg.append("  ").append(update.getRemoteName())
                           .append(": ").append(update.getStatus()).append("\n");
                });
            });
            
            logger.info("Successfully pushed to remote: {}", remoteUrl);
            return resultMsg.toString();
        } catch (Exception e) {
            logger.error("Push failed: {}", e.getMessage(), e);
            throw new GitAPIException("Push failed: " + e.getMessage(), e) {};
        }
    }

    /**
     * Add, commit, and push in one operation
     * @throws URISyntaxException 
     */
    public String commitAndPush(String commitMessage, String filePattern,String repositoryPath, String remoteUrl) throws IOException, GitAPIException, URISyntaxException {
        StringBuilder result = new StringBuilder();
        
        // Initialize if needed
        File repoDir = new File(repositoryPath);
        if (!new File(repoDir, ".git").exists()) {
            result.append(initializeRepository(repositoryPath,remoteUrl)).append("\n");
        }
        
        // Add files
        result.append(addFiles(filePattern,repositoryPath)).append("\n");
        
        // Commit
        result.append(commit(commitMessage,repositoryPath)).append("\n");
        
        // Push
        result.append(push(repositoryPath,remoteUrl)).append("\n");
        
        
        
        return result.toString();
    }

}
