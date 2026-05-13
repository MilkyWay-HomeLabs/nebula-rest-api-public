package org.derleta.nebula.account.adapter.in.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.derleta.nebula.shared.adapter.in.rest.dto.ResponseWithCookieHeaders;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public final class AccessResponse implements ResponseWithCookieHeaders {

    @JsonIgnore
    private Map<String, String> cookiesHeaders;

    @JsonProperty("success")
    boolean success;

    @JsonProperty("type")
    String type;


    @Override
    public void setCookiesHeaders(Map<String, String> headers) {
        cookiesHeaders = headers;
    }

    @Override
    public Map<String, String> getCookiesHeaders() {
        return cookiesHeaders;
    }

}
