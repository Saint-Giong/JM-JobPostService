package rmit.saintgiong.jobpost.api.internal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response DTO for created job post")
public class CreateJobPostResponseDto {
    @Schema(description = "Unique identifier of the created job post", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private String id;
}

