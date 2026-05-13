package org.derleta.nebula.account.adapter.in.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.derleta.nebula.shared.adapter.in.rest.dto.ResponseWithCookieHeaders;

import java.util.Map;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public final class JwtTokenResponse implements ResponseWithCookieHeaders {

    @JsonIgnore
    private Map<String, String> cookiesHeaders;

    @JsonProperty("username")
    private String username;
    @JsonProperty("email")
    private String email;

    @Override
    public void setCookiesHeaders(Map<String, String> headers) {
        cookiesHeaders = headers;
    }

    @Override
    public Map<String, String> getCookiesHeaders() {
        return cookiesHeaders;
    }

}
