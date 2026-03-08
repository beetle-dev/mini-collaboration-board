package com.minicollaborationboard.global.common.service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@RequiredArgsConstructor
public class AWSEmailSender implements EmailSender {

    private final SesClient ses;

    @Override
    public void sendHtmlMessage(String from, String to, String subject, String htmlBody) {

        SendEmailRequest request = SendEmailRequest.builder()
                .source(from)
                .destination(Destination.builder()
                        .toAddresses(to)
                        .build())
                .message(Message.builder()
                        .subject(Content.builder()
                                .data(subject)
                                .charset("UTF-8")
                                .build())
                        .body(Body.builder()
                                .html(Content.builder()
                                        .data(htmlBody)
                                        .charset("UTF-8")
                                        .build())
                                .build())
                        .build())
                .build();

        ses.sendEmail(request);
    }
}
