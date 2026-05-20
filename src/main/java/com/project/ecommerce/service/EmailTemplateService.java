package com.project.ecommerce.service;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    public String buildVerificationEmail(String userName, String verificationLink){

        return String.format("""

                Dear %s,

                Welcome to Ecommerce Platform!

                Your account has been successfully registered.

                Please verify your email using the link below:

                %s

                This verification link will expire in 24 hours.

                For security reasons, do not share this link with anyone.

                If you did not create this account, please ignore this email.

                Best regards,
                Ecommerce Platform Team
                """,
                userName,
                verificationLink
        );
    }

    public String buildWelcomeEmail(String userName){
        return String.format("""

                Dear %s,

                Welcome to Ecommerce Platform!

                Your email has been verified successfully.

                Your account is now fully activated.

                Thank you for choosing Ecommerce Platform.

                Best regards,
                Ecommerce Platform Team
                """,
                userName
        );
    }

    public String buildResetEmail(String userName, String resetLink){
        return String.format("""
            
            Dear %s,
            
            We received a request to reset your password.
            
            Please click the link below to reset your password:
            
            %s
            
            This link will expire in 15 minutes.
            
            If you did not request this, please ignore this email.
            
            Regards,
            Ecommerce Platform Team
            """,
                userName,
                resetLink
        );

    }

}
