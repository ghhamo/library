package job.hamo.library.service;

import job.hamo.library.dto.LocationDTO;
import org.springframework.stereotype.Component;

@Component
public class LocationService {

    public LocationDTO stringToLocation(String[] location) {
        boolean isFound;
        if (location.length == 0) {
            return new LocationDTO(" ", " ", " ");
        }
        if (location.length == 1) {
            return new LocationDTO(location[0], " ", " ");
        }
        if (location.length == 2) {
            isFound = location[1].contains("n/a");
            if (location[0].length() == 0) {
                location[0] = " ";
            }
            if (location[1].length() == 0) {
                location[1] = " ";
            }
            if (isFound) {
                return new LocationDTO(location[0], null, " ");
            } else {
                return new LocationDTO(location[0], location[1], " ");
            }
        }
        isFound = location[1].contains("n/a");
        if (location[0].length() == 0) {
            location[0] = " ";
        }
        if (isFound) {
            return new LocationDTO(location[0], null, location[2]);
        } else {
            return new LocationDTO(location[0], location[1], location[2]);
        }
    }
}
