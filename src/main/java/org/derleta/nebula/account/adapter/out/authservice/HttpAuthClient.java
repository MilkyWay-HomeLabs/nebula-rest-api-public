package org.derleta.nebula.account.adapter.out.authservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.derleta.nebula.account.adapter.in.rest.dto.response.AccessResponse;
import org.derleta.nebula.account.adapter.out.authservice.dto.AuthConfirmRequest;
import org.derleta.nebula.account.adapter.out.authservice.dto.AuthTokenRequest;
import org.derleta.nebula.account.adapter.out.authservice.dto.AuthTokenResponse;
import org.derleta.nebula.account.adapter.out.authservice.dto.AuthUpdatePasswordRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.derleta.nebula.account.adapter.out.authservice.dto.AccountResponse;
import org.derleta.nebula.account.adapter.out.authservice.dto.AuthServRegistrationRequest;
import org.derleta.nebula.shared.adapter.in.rest.dto.Response;
import org.derleta.nebula.shared.adapter.in.rest.dto.ResponseWithCookieHeaders;
import org.derleta.nebula.account.adapter.out.authservice.dto.UserRoles;
import org.derleta.nebula.account.domain.types.AccountResponseType;
import org.derleta.nebula.shared.domain.exception.HttpRequestException;
import org.derleta.nebula.shared.domain.exception.MissingHeaderException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HttpAuthClient is responsible for interacting with authentication API endpoints.
 * It provides methods for user account management, token generation, and handling
 * HTTP requests with JSON bodies and authorization headers.
 */
@Component
public final class HttpAuthClient {

    @Value("${header.for.auth.serv}")
    private String appHeader;

    @Value("${auth.serv.api.v1.url.account}")
    private String authApiAccountUrl;

    @Value("${auth.serv.api.v1.url.account.register}")
    private String authApiPostRegisterUrl;

    @Value("${auth.serv.api.v1.url.account.confirm}")
    private String authApiAccountConfirmUrl;

    @Value("${auth.serv.api.v1.url.account.unlock}")
    private String authApiAccountUnlockUrl;

    @Value("${auth.serv.api.v1.url.account.reset}")
    private String authApiAccountResetPasswdUrl;

    @Value("${auth.serv.api.v1.url.account.changepass}")
    private String authApiAccountChangePasswdUrl;

    @Value("${auth.serv.api.v1.url.account.token}")
    private String authApiAccountTokenUrl;

    @Value("${auth.serv.api.v1.url.access.refresh}")
    private String authApiAccessRefreshUrl;

    private static final List<String> REQUIRED_COOKIES = List.of("accessToken", "refreshToken");

    /**
     * Registers a user by validating the provided registration request and sending it
     * to the registration API.
     *
     * @param request an AuthServRegistrationRequest object containing the user details such as email and encrypted password
     * @return an AccountResponse indicating success or failure of the registration along with the appropriate response type
     */
    public AccountResponse registerUser(AuthServRegistrationRequest request) {

        if (!ValidationUtil.isValidEmail(request.getEmail()))
            return new AccountResponse(false, AccountResponseType.NEBULA_INVALID_EMAIL);
        if (!ValidationUtil.isValidEncryptedPassword(request.getEncryptedPassword()))
            return new AccountResponse(false, AccountResponseType.NEBULA_INVALID_ENCRYPTED_PASSWORD);

        String body = getBodyForRegistrationRequest(request);
        return postRequest(body, authApiPostRegisterUrl);
    }

    /**
     * Confirms a user account based on the provided confirmation request.
     *
     * @param confirmation the user confirmation request containing necessary details for account confirmation
     * @return an AccountResponse object representing the result of the account confirmation process
     */
    public AccountResponse confirmAccount(AuthConfirmRequest confirmation) {
        String body = getBodyForConfirmAccountRequest(confirmation);
        return patchRequest(body, authApiAccountConfirmUrl);
    }

    /**
     * Unlocks a user account by its unique identifier.
     *
     * @param id The unique identifier of the user account to be unlocked.
     * @return AccountResponse object containing details about the account's updated state after the unlock operation.
     */
    public AccountResponse unlockAccount(Long id) {
        String url = authApiAccountUnlockUrl + "/" + id;
        return patchRequest(url);
    }

    /**
     * Resets the password for the account associated with the given email address.
     * Sends a patch request to the authentication API endpoint for resetting passwords.
     *
     * @param email the email address of the account for which the password needs to be reset
     * @return an AccountResponse object containing the outcome of the password reset request
     */
    public AccountResponse resetPassword(String email) {
        String url = authApiAccountResetPasswdUrl + "/" + email;
        return patchRequest(url);
    }

    public AccountResponse updatePassword(String jwtToken, AuthUpdatePasswordRequest request) {
        String body = getBodyForUpdatePasswordRequest(request);
        HttpPost httpPost = new HttpPost(authApiAccountChangePasswdUrl);
        httpPost.addHeader("Authorization", "Bearer " + jwtToken);
        return sendRequestWithBody(httpPost, body, this::executeForAccountResponse);
    }

    /**
     * Refreshes the access token by sending a request to the authentication API using the provided refresh token.
     * <p>
     * This method exchanges the provided valid refresh token for a new access token. It sends a request to the authentication API
     * and returns a response containing the newly generated access token along with any relevant details.
     *
     * @param refreshToken the refresh token used to request a new access token. It must be a valid token and not null.
     * @return a JwtTokenResponse containing the generated JWT token and additional token details
     */
    public AccessResponse refreshAccess(String refreshToken) {
        return postRequestWithRefreshTokenCookie(refreshToken, authApiAccessRefreshUrl);
    }

    /**
     * Generates a JWT token by making a request to the authentication API.
     *
     * @param authRequest the authentication request containing email and other required fields
     * @return a JwtTokenResponse containing the generated JWT token and additional token details
     */
    public AuthTokenResponse generateToken(AuthTokenRequest authRequest) {
        String body = getBodyForTokenRequest(authRequest);
        return postRequestForAuthToken(body, authApiAccountTokenUrl);
    }

    /**
     * Retrieves the user roles associated with an account using the username and email.
     *
     * @param username the login name of the account
     * @param email    the email address of the account
     * @return the user roles corresponding to the given credentials
     */
    public UserRoles getAccount(String username, String email) {
        String uri = String.format("%s?username=%s&email=%s", authApiAccountUrl, username, email);
        return getRequest(uri);
    }

    /**
     * Sends an HTTP GET request to the specified URI and retrieves user roles in the response.
     *
     * @param uri the URI to which the HTTP GET request is sent
     * @return the UserRoles object obtained from the response
     * @throws RuntimeException if an IOException occurs during the request execution
     */
    private UserRoles getRequest(String uri) {
        HttpGet httpGet = new HttpGet(uri);
        try {
            return executeForUserRoles(httpGet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a POST request to the specified remote service URL with the given JSON body
     * to retrieve a JWT token response.
     *
     * @param jsonBody         the JSON string containing the request body to be sent as part of the POST request
     * @param remoteServiceURL the URL of the remote service endpoint to which the POST request will be sent
     * @return the JwtTokenResponse object containing the response from the remote service
     */
    private AuthTokenResponse postRequestForAuthToken(String jsonBody, String remoteServiceURL) {
        return sendRequestWithBody(new HttpPost(remoteServiceURL), jsonBody, this::executeForAuthTokenResponse);
    }

    /**
     * Sends a POST request to the specified authentication endpoint using the provided refresh token
     * as an HTTP cookie named {@code refreshToken}. This method is used to refresh the access token
     * by contacting the remote authentication service.
     *
     * @param refreshToken     the refresh token to be included in the request as a cookie;
     *                         must be a valid, non-expired JWT token
     * @param remoteServiceURL the full URL of the remote endpoint that handles access token refresh
     * @return an {@link AccessResponse} containing information about whether the access token
     * was successfully refreshed
     * @throws HttpRequestException if the request fails or returns an unexpected response
     */
    private AccessResponse postRequestWithRefreshTokenCookie(String refreshToken, String remoteServiceURL) {
        HttpPost request = new HttpPost(remoteServiceURL);
        request.addHeader("Cookie", "refreshToken=" + refreshToken);
        return sendRequest(request, this::executeForRefreshTokenResponse);
    }

    /**
     * Sends an HTTP POST request to the specified remote service URL with the provided JSON body and Bearer token.
     *
     * @param jsonBody         the JSON string to be sent as the request body
     * @param jwtToken         the Bearer token to be included in the HTTP Authorization header
     * @param remoteServiceURL the URL of the remote service to which the POST request will be sent
     * @return an AccountResponse object containing the response from the remote service
     */
    public AccountResponse postRequestWithBearerToken(String jsonBody, String jwtToken, String remoteServiceURL) {
        return sendRequestWithBodyAndBearerToken(
                new HttpPost(remoteServiceURL), jwtToken, jsonBody, this::executeForAccountResponse
        );
    }

    /**
     * Sends a POST request to the specified remote service URL with the provided JSON body
     * and processes the response to return an AccountResponse object.
     *
     * @param jsonBody         the JSON-formatted string representing the request body
     * @param remoteServiceURL the URL of the remote service to which the request is sent
     * @return an AccountResponse object representing the response from the remote service
     */
    public AccountResponse postRequest(String jsonBody, String remoteServiceURL) {
        return sendRequestWithBody(new HttpPost(remoteServiceURL), jsonBody, this::executeForAccountResponse);
    }

    /**
     * Sends a PATCH request to the specified remote service URL with the given JSON body
     * and processes the response into an AccountResponse.
     *
     * @param jsonBody         the JSON formatted string to be sent as the request body
     * @param remoteServiceURL the URL of the remote service to which the PATCH request will be sent
     * @return an AccountResponse object representing the processed response from the remote service
     */
    public AccountResponse patchRequest(String jsonBody, String remoteServiceURL) {
        return sendRequestWithBody(new HttpPatch(remoteServiceURL), jsonBody, this::executeForAccountResponse);
    }

    /**
     * Sends a PATCH request to the specified remote service URL and processes the response.
     *
     * @param remoteServiceURL the URL of the remote service to which the PATCH request is sent
     * @return an AccountResponse object containing the response data from the remote service
     */
    public AccountResponse patchRequest(String remoteServiceURL) {
        return sendRequest(new HttpPatch(remoteServiceURL), this::executeForAccountResponse);
    }

    /**
     * Sends an HTTP request with a JSON body and a Bearer token for authentication.
     * Adds the Bearer token as an Authorization header to the request
     * and includes the provided JSON body in the request.
     *
     * @param requestBase     the HTTP request to be executed
     * @param jwtToken        the JSON Web Token (JWT) to be added as a Bearer token in the Authorization header
     * @param jsonBody        the JSON body to be included in the request
     * @param responseHandler the response handler to process the response and return the result
     * @param <T>             the type of response expected, which extends Response
     * @return the processed response of type T
     */
    private <T extends Response> T sendRequestWithBodyAndBearerToken(HttpUriRequestBase requestBase, final String jwtToken, final String jsonBody, ResponseHandler<T> responseHandler) {
        Map<String, String> headers = createAuthHeader(jwtToken);
        headers.forEach((key, value) -> requestBase.addHeader(new BasicHeader(key, value)));
        return sendRequestWithBody(requestBase, jsonBody, responseHandler);
    }

    /**
     * Sends an HTTP request with a JSON body and processes the response using the provided response handler.
     *
     * @param requestBase     the HTTP request to be sent
     * @param jsonBody        the JSON body to be included in the request; can be null or blank
     * @param responseHandler a handler to process the HTTP response
     * @param <T>             the type of the response expected
     * @return the processed response of type T
     */
    private <T extends Response> T sendRequestWithBody(HttpUriRequestBase requestBase, final String jsonBody, ResponseHandler<T> responseHandler) {
        if (jsonBody != null && !jsonBody.isBlank()) {
            requestBase.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        }
        return sendRequest(requestBase, responseHandler);
    }

    /**
     * Sends an HTTP request using the provided {@code HttpUriRequestBase} and processes the response
     * using the given {@code ResponseHandler}. Handles any I/O exceptions that occur during the
     * request execution or response processing by throwing a custom {@code HttpRequestException}.
     *
     * @param <T>             the type of the response, which extends {@code Response}
     * @param requestBase     the HTTP request to be executed
     * @param responseHandler the handler responsible for processing the HTTP response and
     *                        returning the corresponding response object
     * @return the processed response as an object of type {@code T}
     * @throws HttpRequestException if an I/O error occurs during the execution of the request
     *                              or processing of the response
     */
    private <T extends Response> T sendRequest(final HttpUriRequestBase requestBase, ResponseHandler<T> responseHandler) {
        try {
            return responseHandler.handleResponse(requestBase);
        } catch (IOException e) {
            throw new HttpRequestException("Failed to execute account request for URL: " + requestBase.getRequestUri(), e);
        }
    }

    /**
     * Creates an authorization header map containing a single key-value pair where the key is "Authorization"
     * and the value is "Bearer " followed by the provided JWT token.
     *
     * @param jwtToken the JWT token to be included in the authorization header
     * @return a map containing the authorization header
     */
    private Map<String, String> createAuthHeader(String jwtToken) {
        return Map.of("Authorization", "Bearer " + jwtToken);
    }

    /**
     * Executes the given HTTP request and returns a deserialized {@code AuthTokenResponse} object
     * containing the authentication token and related details.
     *
     * @param requestBase the HTTP request to be executed
     * @return a {@code AuthTokenResponse} object containing the access token, username, and email details
     * @throws IOException if an I/O error occurs during the request execution or response processing
     */
    public AuthTokenResponse executeForAuthTokenResponse(HttpUriRequestBase requestBase) throws IOException {
        return executeRequest(requestBase, AuthTokenResponse.class);
    }

    /**
     * Executes the given HTTP request and returns a deserialized {@code AccessResponse} object, which contains the
     * authentication token and related details. This method processes the HTTP request and parses the response.
     *
     * @param requestBase the HTTP request to be executed
     * @return a {@code AccessResponse} object containing the refresh access status with a message
     * @throws IOException if an I/O error occurs during the request execution or response processing
     */
    public AccessResponse executeForRefreshTokenResponse(HttpUriRequestBase requestBase) throws IOException {
        return executeRequest(requestBase, AccessResponse.class);
    }

    /**
     * Executes the given HTTP request and processes its response, returning a deserialized {@code AccountResponse} object.
     * This method handles the HTTP request execution and response parsing to retrieve account-related details.
     *
     * @param requestBase the HTTP request to be executed
     * @return a {@code AccountResponse} object containing the success status and account response type
     * @throws IOException if an I/O error occurs during the request execution or response processing
     */
    public AccountResponse executeForAccountResponse(HttpUriRequestBase requestBase) throws IOException {
        return executeRequest(requestBase, AccountResponse.class);
    }

    /**
     * Executes the given HTTP request and processes its response, returning a deserialized {@code UserRoles} object.
     * This method incorporates HTTP request execution and response parsing to retrieve user role details.
     *
     * @param requestBase the HTTP request to be executed
     * @return a {@code UserRoles} object containing user account and assigned roles
     * @throws IOException if an I/O error occurs during the execution of the request or processing of the response
     */
    public UserRoles executeForUserRoles(HttpUriRequestBase requestBase) throws IOException {
        return executeRequest(requestBase, UserRoles.class);
    }

    /**
     * Executes the given HTTP request and processes its response, returning a deserialized object of the specified type.
     * This method incorporates the handling of HTTP request execution and response parsing.
     *
     * @param requestBase   the HTTP request to be executed
     * @param responseClass the class type into which the response body should be deserialized
     * @return the deserialized response body as an object of the specified class type
     * @throws IOException if an I/O error occurs during the request execution or response processing
     */
    public <T> T executeRequest(HttpUriRequestBase requestBase, Class<T> responseClass) throws IOException {
        return executeForResponse(requestBase, responseClass);
    }

    /**
     * Executes an HTTP request and parses the response into the specified response type.
     * <p>
     * Adds predefined headers to the request, sends it using a {@link CloseableHttpClient},
     * parses the JSON response body into a Java object of the given class,
     * and if applicable, extracts and attaches cookie headers to the response object.
     * </p>
     *
     * @param <T>           the expected type of the response object
     * @param requestBase   the HTTP request to be executed
     * @param responseClass the class of the expected response object
     * @return the parsed response object of type {@code T}
     * @throws IOException if the request fails or the response cannot be read
     */
    private <T> T executeForResponse(HttpUriRequestBase requestBase, Class<T> responseClass) throws IOException {
        addHeadersToRequest(requestBase);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpClientResponseHandler<T> responseHandler = response -> {
                validateHttpResponse((CloseableHttpResponse) response);
                String jsonResult = EntityUtils.toString(response.getEntity());
                T responseObject = parseJsonResponse(jsonResult, responseClass);
                if (responseObject instanceof ResponseWithCookieHeaders responseWithCookieHeaders) {
                    Map<String, String> cookies = createCookiesHeaderMap(response);
                    validateRequiredCookies(cookies);
                    responseWithCookieHeaders.setCookiesHeaders(cookies);
                }
                return responseObject;
            };
            return httpClient.execute(requestBase, responseHandler);
        }
    }

    /**
     * Generates a map of cookies from the HTTP response headers.
     * <p>
     * This method parses the 'Set-Cookie' headers from the response and generates a map of cookies
     * where the key is the cookie name and the value is the entire cookie string. Additionally, the
     * method ensures that cookies related to security (e.g., "SameSite=None", "Secure", "Partitioned")
     * are added if missing, and filters out only the cookies related to "accessToken" and "refreshToken".
     * </p>
     *
     * @param response the HTTP response to analyze, specifically its 'Set-Cookie' headers
     * @return a map where the key is the cookie name and the value is the cookie string for
     * 'accessToken' and 'refreshToken' cookies
     */
    private Map<String, String> createCookiesHeaderMap(ClassicHttpResponse response) {
        return Arrays.stream(response.getHeaders("Set-Cookie"))
                .map(Header::getValue)
                .filter(cookie -> cookie != null && !cookie.isBlank())
                .map(cookie -> {
                    if (!cookie.contains("SameSite")) {
                        cookie += "; SameSite=None";
                        if (!cookie.contains("Secure")) {
                            cookie += "; Secure";
                        }
                        if (!cookie.contains("Partitioned")) {
                            cookie += "; Partitioned";
                        }
                    }
                    return cookie;
                })
                .filter(cookie -> cookie.startsWith("accessToken") || cookie.startsWith("refreshToken"))
                .collect(Collectors.toMap(
                        cookie -> cookie.split("=")[0],
                        cookie -> cookie
                ));
    }

    /**
     * Validates the given cookies
     *
     * @param cookies map to validate if contains all required cookies
     */
    private static void validateRequiredCookies(Map<String, String> cookies) {
        for (String requiredCookie : REQUIRED_COOKIES) {
            if (!cookies.containsKey(requiredCookie)) {
                throw new MissingHeaderException("Missing required cookie: " + requiredCookie);
            }
        }
    }

    /**
     * Validates the given HTTP response by checking the status code.
     * If the status code indicates a failure (not in the range 200-299),
     * an IOException is thrown with details about the failure.
     *
     * @param response The CloseableHttpResponse object to be validated.
     * @throws IOException If the HTTP request fails with a status code outside of the 2xx range,
     *                     or if an error occurs while reading the response entity.
     */
    private void validateHttpResponse(CloseableHttpResponse response) throws IOException {
        int statusCode = response.getCode();

        if (statusCode >= 400) {
            handleHttpErrorResponse(response, statusCode);
        }

        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("HTTP request failed with unexpected status code: "
                    + statusCode + ", reason: " + response.getReasonPhrase());
        }
    }

    /**
     * Handles HTTP error responses (status codes 400 and above).
     * Extracts response details and throws an IOException with detailed information.
     *
     * @param response   The HTTP response object containing error details.
     * @param statusCode The HTTP status code indicating an error.
     * @throws IOException If the HTTP request fails with an error status code.
     */
    private void handleHttpErrorResponse(CloseableHttpResponse response, int statusCode) throws IOException {
        String responseBody = extractResponseBody(response);
        String reason = extractErrorReason(responseBody);

        throw new IOException("HTTP request failed with status code: "
                + statusCode + ", reason: " + reason);
    }

    /**
     * Extracts the body content from the HTTP response.
     *
     * @param response The HTTP response object containing the body.
     * @return The response body as a string.
     * @throws IOException If an error occurs while reading the response entity.
     */
    private String extractResponseBody(CloseableHttpResponse response) throws IOException {
        if (response.getEntity() == null) {
            return "";
        }

        try {
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new IOException("Failed to parse the response body.", e);
        }
    }

    /**
     * Extracts the error reason from a JSON-formatted response body.
     *
     * @param responseBody The JSON response body as a string.
     * @return The extracted error reason, or raw body if parsing fails.
     */
    private String extractErrorReason(String responseBody) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(responseBody);
            return jsonNode.path("type").asText();
        } catch (JsonProcessingException e) {
            System.out.println("Failed to parse error response JSON. Body was: " + responseBody);
            return responseBody;
        }
    }

    /**
     * Adds predefined headers to the given HTTP request.
     * The headers are constructed using the {@code getHeaders()} method.
     *
     * @param requestBase the HTTP request to which the headers will be added
     */
    private void addHeadersToRequest(HttpUriRequestBase requestBase) {
        getHeaders().forEach(requestBase::addHeader);
    }

    /**
     * Parses a JSON string into an object of the specified class type.
     *
     * @param jsonResult    the JSON string to be parsed
     * @param responseClass the class of the object to be created from the JSON string
     * @return an object of the specified class type, deserialized from the JSON string
     * @throws JsonProcessingException if the JSON string cannot be processed or deserialized
     */
    private <T> T parseJsonResponse(String jsonResult, Class<T> responseClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonResult, responseClass);
    }

    /**
     * Constructs and returns a list of headers required for HTTP requests.
     * The headers include a "Content-Type" header with value "application/json"
     * and an "X-Requesting-App" header with the value of the appHeader field.
     *
     * @return a list of Header objects containing the required HTTP headers
     */
    private List<Header> getHeaders() {
        return List.of(
                createHeader(org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE, "application/json"),
                createHeader("X-Requesting-App", appHeader)
        );
    }

    /**
     * Creates a header with the specified name and value.
     *
     * @param name  the name of the header
     * @param value the value of the header
     * @return a Header object containing the specified name and value
     */
    private Header createHeader(String name, String value) {
        return new BasicHeader(name, value);
    }

    /**
     * Constructs a JSON-formatted request body for user registration.
     *
     * @param request an AuthServRegistrationRequest object containing the username,
     *                email, and encrypted password of the user to be registered
     * @return a JSON string representing the request body with the user's
     * username, encrypted password, and email information
     */
    private String getBodyForRegistrationRequest(AuthServRegistrationRequest request) {
        return String.format("""
                            {
                                "username": "%s",
                                "password": "%s",
                                "email": "%s"
                            }
                        """,
                request.getUsername(),
                request.getEncryptedPassword(),
                request.getEmail());
    }

    /**
     * Constructs a JSON-formatted request body for confirming an account.
     *
     * @param confirmation the {@code AuthConfirmRequest} containing the token ID and the token string
     * @return a JSON string representing the request body
     */
    private String getBodyForConfirmAccountRequest(AuthConfirmRequest confirmation) {
        return String.format("""
                            {
                                "tokenId": %s,
                                "token": "%s"
                            }
                        """,
                confirmation.tokenId(),
                confirmation.token());
    }

    /**
     * Constructs the body for a token request using the provided {@code AuthTokenRequest}.
     *
     * @param request the {@code AuthTokenRequest} containing the email and password
     * @return the formatted JSON string representing the body of the token request
     */
    private String getBodyForTokenRequest(AuthTokenRequest request) {
        return String.format("""
                            {
                                "login": "%s",
                                "password": "%s"
                            }
                        """,
                request.email(),
                request.password());
    }

    /**
     * Creates a JSON-formatted request body for updating a user's password.
     *
     * @param request the {@code AuthUpdatePasswordRequest} containing user ID, email,
     *                actual password, and new password
     * @return a JSON string representing the request body
     */
    private String getBodyForUpdatePasswordRequest(AuthUpdatePasswordRequest request) {
        return String.format("""
                            {
                                "userId": %s,
                                "email": "%s",
                                "actualPassword": "%s",
                                "newPassword": "%s"
                            }
                        """,
                request.userId(),
                request.email(),
                request.actualPassword(),
                request.newPassword());
    }

}

