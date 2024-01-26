package it.almaviva.difesa.template.service.impl;


import it.almaviva.difesa.shared.data.dto.VwSg155StgiurFastMiCiDTO;
import it.almaviva.difesa.template.service.MSSipadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MSSipadServiceImpl implements MSSipadService {

    @Value("${ms.sipad.base.url}")
    private String msSipadBaseUrl;

    @Value("${ms.sipad.find-user}")
    private String findById;

    private final WebClient sipadClient;


    /***
     * Method to find user by employee id
     * @param id of employee
     * @return
     * @throws URISyntaxException
     */
    @Override
    public VwSg155StgiurFastMiCiDTO findUserById(Long id) throws URISyntaxException {
        Map<String, String> param = new HashMap<>();
        param.put("employeeId", String.valueOf(id));
        var uri = createUri(msSipadBaseUrl, findById, param);
        return sipadClient.get().uri(uri).retrieve()
                .bodyToMono(VwSg155StgiurFastMiCiDTO.class).block();
    }


    /***
     * Method to create the uri for webclient, it takes base url, path and query params.
     * It constructs the uri in base of the presence of query params. It they
     * @param baseUrl
     * @param path
     * @param queryParams
     * @return
     * @throws URISyntaxException
     */
    public URI createUri (String baseUrl, String path, Map<String, String> queryParams) throws URISyntaxException {
        String protocol = "";
        if (queryParams==null) {
            protocol = "http://";
            return new URI(protocol + baseUrl + path);
        }
        else {
            var mapParams = queryParams.entrySet().stream().collect(Collectors.groupingBy(Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
            return UriComponentsBuilder.fromPath(protocol + baseUrl + path).queryParams(new LinkedMultiValueMap<>(mapParams)).build().encode(StandardCharsets.UTF_8).toUri();
        }
    }

    /***
     * Overloaded signature for the method to build the uri for webclient. This is for
     * uri without params
     * @param baseUrl base of the url
     * @param path path for specific service
     * @return a URI
     * @throws URISyntaxException
     */
    public URI createUri (String baseUrl, String path) throws URISyntaxException {
        return createUri (baseUrl, path, null);
    }

}
