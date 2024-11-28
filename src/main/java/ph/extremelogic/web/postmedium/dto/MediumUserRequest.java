package ph.extremelogic.web.postmedium.dto;

import lombok.Data;

/**
 * User request to fetch user information.
 */
@Data
public class MediumUserRequest {
    private String id;
    private String username;
    private String name;
    private String url;
    private String imageUrl;
}
