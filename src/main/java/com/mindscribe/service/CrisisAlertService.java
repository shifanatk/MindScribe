package com.mindscribe.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends an email when the AI returns a "Crisis" sentiment.
 * Enable with mindscribe.crisis.alert.enabled=true and configure spring.mail.*.
 */
@Service
public class CrisisAlertService {

    private static final Logger log = LoggerFactory.getLogger(CrisisAlertService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${mindscribe.crisis.alert.enabled:false}")
    private boolean enabled;

    @Value("${mindscribe.crisis.alert.to:}")
    private String alertTo;

    /**
     * Called when sentiment is "Crisis". Sends an email if mail is configured and enabled.
     */
    public void sendCrisisAlert(String entrySummary) {
        if (!enabled || alertTo == null || alertTo.isBlank()) {
            log.warn("MindScribe: Crisis sentiment detected. Alert email not sent (disabled or no recipient). Summary length: {}",
                    entrySummary != null ? entrySummary.length() : 0);
            return;
        }
        if (mailSender == null) {
            log.warn("MindScribe: Crisis sentiment detected. JavaMailSender not available (configure spring.mail.*).");
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(alertTo);
            msg.setSubject("[MindScribe] Crisis sentiment detected");
            msg.setText("A journal entry was classified as Crisis. Please check in with the user.\n\nEntry snippet (first 500 chars):\n"
                    + (entrySummary != null && entrySummary.length() > 500 ? entrySummary.substring(0, 500) + "..." : entrySummary));
            mailSender.send(msg);
            log.info("MindScribe: Crisis alert email sent to {}", alertTo);
        } catch (Exception e) {
            log.error("MindScribe: Failed to send crisis alert email", e);
        }
    }
}
