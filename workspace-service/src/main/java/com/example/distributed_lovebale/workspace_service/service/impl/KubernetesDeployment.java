package com.example.distributed_lovebale.workspace_service.service.impl;

import com.example.distributed_lovebale.workspace_service.dto.deploy.DeployResponse;
import com.example.distributed_lovebale.workspace_service.service.DeploymentService;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class KubernetesDeployment implements DeploymentService {

    private final KubernetesClient client;
    private final StringRedisTemplate redisTemplate;

    private static final String NAMESPACE = "shuttle-apps";
    private static final String POOL_LABEL = "status";
    private static final String PROJECT_LABEL = "project-id";
    private static final String IDLE = "idle";
    private static final String BUSY = "busy";

    private static final String SYNCER_CONTAINER = "syncer";
    private static final String RUNNER_CONTAINER = "runner";
    private static final String REVERSE_PROXY_PORT = "8090";

    private static final String START_DEV_SERVER_COMMAND =
            "npm install && nohup npm run dev -- --host 0.0.0.0 --port 5173 > /app/dev.log 2>&1 &";

    @Override
    public DeployResponse deploy(Long projectId) {
        String domain = "project-" + projectId + ".app.domain.com";
        Pod existingPod = findActivePod(projectId);

        if (existingPod != null) {
//            log.info("Project {} is already running on pod {} - reusing it", projectId, existingPod.getMetadata().getName());
            registerRoute(domain, existingPod);
            return new DeployResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);
        }

        return claimAndStartNewPod(projectId, domain);
    }

    private DeployResponse claimAndStartNewPod(Long projectId, String domain) {
        Pod pod = claimIdlePod(projectId);
        String podName = pod.getMetadata().getName();

        try {
            registerRoute(domain, pod);
            provisionPod(podName, projectId);
            log.info("Deployment successful: http://{}:{}", domain, REVERSE_PROXY_PORT);
            return new DeployResponse("http://" + domain + ":" + REVERSE_PROXY_PORT);
        } catch (Exception e) {
            releasePod(podName, projectId, e);
            throw new RuntimeException("Failed to deploy the project with id: " + projectId, e);
        }
    }

    /**
     * Finds an idle runner pod and claims it for the given project.
     */
    private Pod claimIdlePod(Long projectId) {
        Pod pod = client.pods().inNamespace(NAMESPACE)
                .withLabel(POOL_LABEL, IDLE)
                .list().getItems().stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("No idle runner pods available - cannot deploy project {}", projectId);
                    return new IllegalStateException("No idle runners available. Please scale up the runner-pool.");
                });

        String podName = pod.getMetadata().getName();
        log.info("Claiming pod {} for project {}", podName, projectId);

        client.pods().inNamespace(NAMESPACE).withName(podName).edit(p -> {
            p.getMetadata().getLabels().put(POOL_LABEL, BUSY);
            p.getMetadata().getLabels().put(PROJECT_LABEL, projectId.toString());
            return p;
        });

        return pod;
    }

    /**
     * Runs the sync + dev-server startup sequence inside the claimed pod.
     */
    private void provisionPod(String podName, Long projectId) {
        // Syncer Commands
        log.info("Starting initial sync for project {} in pod {}", projectId, podName);
        execCommand(podName, SYNCER_CONTAINER, "sh", "-c", initialSyncCommand(projectId));

        execCommand(podName, SYNCER_CONTAINER, "sh", "-c", watchSyncCommand(projectId));

        // Runner Commands
        log.info("Starting dev server for project {}...", projectId);
        execCommand(podName, RUNNER_CONTAINER, "sh", "-c", START_DEV_SERVER_COMMAND);
    }

    /**
     * Rolls the pod back to the pool when provisioning fails.
     */
    private void releasePod(String podName, Long projectId, Exception cause) {
        log.error("Deployment failed for project {}. Releasing pod {}.", projectId, podName, cause);
        client.pods().inNamespace(NAMESPACE).withName(podName).delete();
    }

    private String initialSyncCommand(Long projectId) {
        return "mc mirror --overwrite myminio/projects/" + projectId + "/ /app/";

    }

    private String watchSyncCommand(Long projectId) {
        return "nohup mc mirror --overwrite --watch myminio/projects/"
                + projectId + "/ /app/ > /app/sync.log 2>&1 &";
    }

    private void registerRoute(String domain, Pod pod) {
        String podIp = pod.getStatus().getPodIP();
        if (podIp == null) throw new RuntimeException("Pod is running but has no IP!");

        redisTemplate.opsForValue().set("route:" + domain, podIp + ":5173", 6, TimeUnit.HOURS);
    }

    private void execCommand(String podName, String container, String... command) {
        log.info("Exec in {}:{} -> {}", podName, container, String.join(" ", command));

        CompletableFuture<String> data = new CompletableFuture<>();
        try (ExecWatch ignored = client.pods().inNamespace(NAMESPACE).withName(podName)
                .inContainer(container)
                .writingOutput(new ByteArrayOutputStream())
                .writingError(new ByteArrayOutputStream())
                .usingListener(new ExecListener() {
                    @Override
                    public void onClose(int code, String reason) {
                        data.complete("Done");
                    }
                })
                .exec(command)) {

            // Wait briefly to ensure command fired (Fabric8 exec is async)
            // For long running background jobs (nohup), we don't wait for "Done"
            if (command[command.length - 1].trim().endsWith("&")) {
                Thread.sleep(500);
            } else {
                data.get(30, TimeUnit.SECONDS); // Block for synchronous setup commands (npm install)
            }

        } catch (Exception e) {
            log.error("Exec failed", e);
            throw new RuntimeException("Pod Execution Failed", e);
        }
    }

    private Pod findActivePod(Long projectId) {
        return client.pods().inNamespace(NAMESPACE)
                .withLabel(PROJECT_LABEL, projectId.toString())
                .withLabel(POOL_LABEL, BUSY) // Only find active/busy ones
                .list().getItems().stream()
                .filter(pod -> pod.getStatus().getPhase().equals("Running"))
                .findFirst()
                .orElse(null);
    }
}
