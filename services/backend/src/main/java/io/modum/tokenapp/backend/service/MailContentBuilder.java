package io.modum.tokenapp.backend.service;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Optional;

@Service
public class MailContentBuilder {

    private final static Logger LOG = LoggerFactory.getLogger(MailContentBuilder.class);

    private TemplateEngine templateEngine;

    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void buildConfirmationEmail(Optional<MimeMessageHelper> oMessage, String confirmationEmaiLink) {
        if (oMessage.isPresent()) {
            // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
            try {
                Context context = new Context();
                context.setVariable("confirmationEmaiLink", confirmationEmaiLink);
                context.setVariable("modumLogo", "modumLogo");
                String html5Content = templateEngine.process("confirmation_email", context);

                oMessage.get().setText(html5Content, true);

                final InputStreamSource modumLogoImage =
                        new ByteArrayResource(IOUtils.toByteArray(this.getClass().getResourceAsStream("/static/images/modum_logo.png")));
                oMessage.get().addInline("modumLogo", modumLogoImage, "image/png");

            } catch (MessagingException e) {
                LOG.error("Error to add inline images to the message.");
            } catch (IOException e) {
                LOG.error("Error on finding the inline image on the resources path.");
            }
        }
    }

    public void buildGenericWarningMail(Optional<MimeMessageHelper> oMessage, String warningContent) {
        if (oMessage.isPresent()) {
            try {
                oMessage.get().setText(warningContent, true);
            } catch (MessagingException e) {
                LOG.error("Error to add inline images to the message.");
            }
        }
    }

}