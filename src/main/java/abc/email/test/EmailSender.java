package abc.email.test;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

@Service
public class EmailSender {
    private final JavaMailSender javaMailSender;

    private final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    private final String TO = "983cd62c-9c08-44fc-8ac9-7f3dc120270b@example.com";
    private final String FROM = "90c04173-f358-4c74-9dee-b1a3438a9c0f@example.com";

    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    void sendSimple(String to, String subject, String content) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(new InternetAddress(FROM, "No Reply"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false);

            javaMailSender.send(message);

        } catch (UnsupportedEncodingException | MessagingException e) {
            logger.error(e.getMessage());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void test() {
        try {
            sendSimple(TO, "Before async", "Before async");
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }

        CompletableFuture.runAsync(() -> {
            try {
                sendSimple(TO, "CompletableFuture", "CompletableFuture");
            } catch (Throwable e) {
                logger.error(e.getMessage());
            }
        });

        try {
            sendSimple(TO, "After async", "After async");
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
    }
}
