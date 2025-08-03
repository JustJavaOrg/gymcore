package org.justjava.gymcore.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MailModel(
    @NotBlank(message = "Email subject cannot be blank")
    @Size(max = 998, message = "Email subject cannot exceed 998 characters")
    String subject,

    @NotBlank(message = "Email body cannot be blank")
    @Size(max = 50000, message = "Email body cannot exceed 50KB")
    String body
) {
}
}
