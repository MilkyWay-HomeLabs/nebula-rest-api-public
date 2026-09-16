package org.derleta.nebula;

import org.derleta.nebula.shared.security.TokenProvider;
import org.derleta.nebula.testcontainer.FileServerTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ImageUploadE2EIT {

    private static final String TEST_CSRF_TOKEN = "test-csrf-token";

    private static final Set<PosixFilePermission> DIR_PERMISSIONS = Set.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.OWNER_EXECUTE,
            PosixFilePermission.GROUP_READ,
            PosixFilePermission.GROUP_EXECUTE,
            PosixFilePermission.OTHERS_READ,
            PosixFilePermission.OTHERS_EXECUTE);

    private static final Path SHARED_ROOT = createSharedRoot();
    private static final Path AVATAR_DIR = SHARED_ROOT.resolve("nebula").resolve("avatars");

    @Container
    static final FileServerTestContainer FILE_SERVER = new FileServerTestContainer(SHARED_ROOT);

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @MockitoBean
    TokenProvider tokenProvider;

    private static Path createSharedRoot() {
        try {
            Path root = Files.createTempDirectory("fileserver-it-");
            setDirectoryPermissions(root);
            return root;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("image.avatar.path", AVATAR_DIR::toString);
    }

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(AVATAR_DIR);
        setDirectoryPermissions(SHARED_ROOT);
        setDirectoryPermissions(SHARED_ROOT.resolve("nebula"));
        setDirectoryPermissions(AVATAR_DIR);
    }

    private static void setDirectoryPermissions(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.setPosixFilePermissions(path, DIR_PERMISSIONS);
        }
    }

    @Test
    void upload_should_save_file_and_file_should_be_available_from_fileserver() throws IOException {
        when(tokenProvider.isValid("valid-token")).thenReturn(true);
        when(tokenProvider.getUserId("valid-token")).thenReturn(42L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add(HttpHeaders.COOKIE, "accessToken=valid-token; XSRF-TOKEN=" + TEST_CSRF_TOKEN);
        headers.add("X-XSRF-TOKEN", TEST_CSRF_TOKEN);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ClassPathResource("test/test-image.png"));

        ResponseEntity<String> uploadResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/image",
                new HttpEntity<>(body, headers),
                String.class);

        assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());

        Path savedFile = AVATAR_DIR.resolve("42.jpg");
        assertTrue(Files.exists(savedFile));
        assertTrue(Files.size(savedFile) > 0);

        ResponseEntity<byte[]> fileResponse = restTemplate.getForEntity(
                FILE_SERVER.getBaseUrl() + "/nebula/avatars/42.jpg",
                byte[].class);

        assertEquals(HttpStatus.OK, fileResponse.getStatusCode());
        assertNotNull(fileResponse.getBody());
        assertTrue(fileResponse.getBody().length > 0);
    }
}