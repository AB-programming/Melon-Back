package com.melon.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("token", "eyJraWQiOiIyNjQ0YmJiOC1kZDZhLTQyZmItYWRlZi1iNWE5ODUxNzBmMDgiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxMTEiLCJhdWQiOiJtZWxvbiIsIm5iZiI6MTc0ODAwNTkyNSwic2NvcGUiOlsicHJvZmlsZSJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjkwMDAiLCJleHAiOjE3NDgyNjUxMjUsImlhdCI6MTc0ODAwNTkyNSwidXNlciI6eyJpZCI6IjEiLCJ1c2VybmFtZSI6IjExMSIsIm5pY2tuYW1lIjoiYWFhIiwiYXZhdGFyVXJsIjpudWxsLCJzaWduYXR1cmUiOiIxMjMxNDEyNDEzMjEzIn0sImp0aSI6IjE0NWEzMGQ5LTQwMGMtNGE1OS04NjM5LTc5MTg0MzE5Nzc2NCJ9.pULOPmp3FQmJLGrALcE-MryvjAA5PHrkDYZwo2rDh4bsFtn6cRI0DxvybJzWmg9a1r0O9ghWhayqgCZh4tjoME_6AWXAJ4DvCCBPWx7eu7exQJMF_XONOsyVs8vCAzoKy9m3N6KH6B912LZABHSCm9OoOzucyyVQD8T0Uj7pPPZerIVkIccwXnpk7nOXt8_aDsX80_WnwIF4wxFNQ6GQzjAwLt0qG5jiQFPm5jvLc4ty-mEJs_bIXLAhaH4fYyl-ngq0_Gfyl-HMhI6FfRW2uWN2Y7e4ISZ-76ypxIn6KbVtvgP4Y6z98ql_U1_05Awk29n2_i_MunJ9GjSwEwWR_g");
        HttpEntity<LinkedMultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:9000/oauth2/introspect", entity, String.class);
        System.out.println(response);
    }
}
