package org.derleta.nebula.shared.adapter.in.rest.dto;

import java.util.Map;

public interface ResponseWithCookieHeaders extends Response {

    void setCookiesHeaders(Map<String, String> headers);

    Map<String, String> getCookiesHeaders();

}

