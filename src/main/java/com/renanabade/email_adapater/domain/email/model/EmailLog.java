package com.renanabade.email_adapater.domain.email.model;

import com.renanabade.email_adapater.domain.email.constants.LogStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "email-logs")
@NoArgsConstructor
@Getter
@Setter
public class EmailLog {

    @Id
    private String id;
    private String recipient;
    private String sender;
    private String subject;
    private String content;
    private LogStatus status;
    @Setter(AccessLevel.NONE)
    private LocalDateTime timestamp;

    public EmailLog(String recipient, String sender, String subject, String content, LogStatus status) {
        this.recipient = recipient;
        this.sender = sender;
        this.subject = subject;
        this.content = content;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
