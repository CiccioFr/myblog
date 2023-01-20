package it.cgmconsulting.myblog.payload.request;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * Request da FrontEnd per la segnalazione di un Post.
 * Contiene:  commentId - reason
 */
@Getter
public class ReportingRequest {

    @Min(1)
    private long commentId;

    @NotBlank
    private String reason;
}
