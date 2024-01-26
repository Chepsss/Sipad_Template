package it.almaviva.difesa.template.shared.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import it.almaviva.difesa.shared.common.utils.StatusSharedEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class PrincipalUtilMethod {

    private PrincipalUtilMethod() {
    }

    public static String getPrivilegesCodeFromAuthsOrElseThrow(List<String> auths) {
        return auths.stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No Privileges found"));
    }

    public static String getRoleCodeFromRolesOrElseThrow(List<String> roles) {
        return roles.stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No Roles found"));
    }

    public static Long getUserEmployeeIdFromClaims(HttpServletRequest request) {
        var authorId = Long.valueOf(Objects.requireNonNull(PrincipalUtilMethod.getClaimValueByHeaderAndClaimKey(request, StatusSharedEnum.ARQ_AUTH_HEADER.getNameMessage(), "andipId")));

        if (ObjectUtils.isEmpty(authorId)) {
            log.error("The given token does not have any claims concerning an andipId");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The given token does not have any claims concerning an andipId");
        }

        return authorId;
    }

    public static String getClaimValueByHeaderAndClaimKey(HttpServletRequest request, String header, String key) {
        String arqTokenRequest = request.getHeader(header);

        if (!ObjectUtils.isEmpty(arqTokenRequest)) {
            return getClaimsAsText(arqTokenRequest, key);
        }

        return null;
    }

    public static String getClaimValueFromTokenByKey(String token, String key) {
        return getClaimsAsText(token, key);
    }

    private static String getClaimsAsText(String token, String key) {
        String[] chunks = token.split("\\.");
        var decoder = Base64.getDecoder();
        var payload = new String(decoder.decode(chunks[1]));
        var objectMapper = new ObjectMapper();
        try {
            var jsonNode = objectMapper.readTree(payload);
            return jsonNode.get(key).asText();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static List<String> getClaimsAsTextList(String token, String key) {
        String[] chunks = token.split("\\.");
        var decoder = Base64.getDecoder();
        var payload = new String(decoder.decode(chunks[1]));
        var objectMapper = new ObjectMapper();
        try {
            var arrayNode = objectMapper.readTree(payload).get(key);
            ObjectReader reader = objectMapper.readerFor(new TypeReference<List<String>>() {
            });
            return reader.readValue(arrayNode);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static void streamResponseFile(HttpServletResponse response, byte[] data, String fileName, String contentType) throws IOException {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "inline; filename=" + fileName + ";");
        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }

    public static String getPrivilegesCodeOrElseThrow(HttpServletRequest request) {
        var auths = getClaimsAsTextList(request.getHeader(StatusSharedEnum.ARQ_AUTH_HEADER.getNameMessage()), StatusSharedEnum.AUTH.getNameMessage());
        return getPrivilegesCodeFromAuthsOrElseThrow(auths);
    }

    public static String getRoleCodeOrElseThrow(HttpServletRequest request) {
        var roles = getClaimsAsTextList(request.getHeader(StatusSharedEnum.ARQ_AUTH_HEADER.getNameMessage()), StatusSharedEnum.AUTH.getNameMessage());
        return getPrivilegesCodeFromAuthsOrElseThrow(roles);
    }
}
