package com.notification.gateway.provider;

import com.notification.gateway.model.EmailMessage;

public interface EmailProvider {
    void send(EmailMessage emailMessage, String subject, String body, boolean isHtml);
}
