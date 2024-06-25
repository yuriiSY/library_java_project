package com.yuriisykal.library.message;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
public class EmailDTO {
    private String recipient;
    private String subject;
    private String text;
}
