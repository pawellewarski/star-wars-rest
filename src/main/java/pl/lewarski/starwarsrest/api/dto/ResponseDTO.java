package pl.lewarski.starwarsrest.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResponseDTO {
    private List<ReportDTO> reports;
}
