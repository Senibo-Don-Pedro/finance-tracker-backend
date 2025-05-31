package com.senibo.financetracker.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendVerificationEmail(String email, String verificationCode) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@financetracker.com");
            helper.setTo(email);
            helper.setSubject("Email Verification - Finance Tracker");

            String htmlContent = """
                    <html>
                        <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                            <h2>Verify Your Email</h2>
                            <p>Hi there,</p>
                            <p>Thanks for signing up for <strong>Finance Tracker</strong>!</p>
                            <p>Your verification code is:</p>
                            <div style="font-size: 24px; font-weight: bold; color: #2c3e50; margin: 10px 0;">%s</div>
                            <p>This code will expire in <strong>24 hours</strong>.</p>
                            <br/>
                            <p>Best regards,<br/>Finance Tracker Team</p>
                        </body>
                    </html>
                    """.formatted(verificationCode);

            helper.setText(htmlContent, true);

            emailSender.send(message);
            log.info("Verification email sent to {}", email);
        } catch (MessagingException e) {
            log.warn("Failed to send verification email to {}. Logging code instead.", email);
            log.info("VERIFICATION CODE for {}: {}", email, verificationCode);
            log.error("Email sending error: {}", e.getMessage(), e);
        }
    }
}
