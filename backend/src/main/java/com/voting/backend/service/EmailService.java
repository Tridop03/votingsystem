package com.voting.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.backend-url}")
    private String backendUrl;

    @Async
    public void sendVerificationEmail(String toEmail, String fullName, String token) {
        String verificationLink = backendUrl + "/api/auth/verify-email?token=" + token;
        String subject = "Verify Your Email - Online Voting System";
        String body = buildVerificationEmailBody(fullName, verificationLink);
        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String fullName, String token) {
        String resetLink = baseUrl + "/reset-password?token=" + token;
        String subject = "Password Reset Request - Online Voting System";
        String body = buildPasswordResetEmailBody(fullName, resetLink);
        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    public void sendVoteConfirmationEmail(String toEmail, String fullName,
                                          String electionTitle, String categoryName,
                                          String candidateName) {
        String subject = "Vote Confirmation - " + electionTitle;
        String body = buildVoteConfirmationEmailBody(fullName, electionTitle, categoryName, candidateName);
        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    public void sendAccountApprovedEmail(String toEmail, String fullName) {
        String subject = "Account Approved - Online Voting System";
        String body = buildAccountApprovedEmailBody(fullName);
        sendHtmlEmail(toEmail, subject, body);
    }

    @Async
    public void sendAccountDeactivatedEmail(String toEmail, String fullName) {
        String subject = "Account Deactivated - Online Voting System";
        String body = buildAccountDeactivatedEmailBody(fullName);
        sendHtmlEmail(toEmail, subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String buildVerificationEmailBody(String fullName, String verificationLink) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background-color: #1a237e; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">Online Voting System</h1>
                </div>
                <div style="background-color: #f5f5f5; padding: 30px; border-radius: 0 0 8px 8px;">
                    <h2>Hello, %s!</h2>
                    <p>Thank you for registering. Please verify your email address to complete your registration.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #1a237e; color: white; padding: 14px 28px;
                           text-decoration: none; border-radius: 4px; font-size: 16px;">
                            Verify Email Address
                        </a>
                    </div>
                    <p>This link will expire in 24 hours.</p>
                    <p>If you did not create an account, please ignore this email.</p>
                    <hr style="border: 1px solid #ddd; margin: 20px 0;">
                    <p style="color: #666; font-size: 12px;">
                        If the button doesn't work, copy and paste this link:<br>
                        <a href="%s">%s</a>
                    </p>
                </div>
            </body>
            </html>
            """.formatted(fullName, verificationLink, verificationLink, verificationLink);
    }

    private String buildPasswordResetEmailBody(String fullName, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background-color: #1a237e; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">Online Voting System</h1>
                </div>
                <div style="background-color: #f5f5f5; padding: 30px; border-radius: 0 0 8px 8px;">
                    <h2>Hello, %s!</h2>
                    <p>We received a request to reset your password. Click the button below to set a new password.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #d32f2f; color: white; padding: 14px 28px;
                           text-decoration: none; border-radius: 4px; font-size: 16px;">
                            Reset Password
                        </a>
                    </div>
                    <p>This link will expire in 1 hour.</p>
                    <p>If you did not request a password reset, please ignore this email and your password will remain unchanged.</p>
                    <hr style="border: 1px solid #ddd; margin: 20px 0;">
                    <p style="color: #666; font-size: 12px;">
                        If the button doesn't work, copy and paste this link:<br>
                        <a href="%s">%s</a>
                    </p>
                </div>
            </body>
            </html>
            """.formatted(fullName, resetLink, resetLink, resetLink);
    }

    private String buildVoteConfirmationEmailBody(String fullName, String electionTitle,
                                                  String categoryName, String candidateName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background-color: #2e7d32; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">Vote Confirmed ✓</h1>
                </div>
                <div style="background-color: #f5f5f5; padding: 30px; border-radius: 0 0 8px 8px;">
                    <h2>Hello, %s!</h2>
                    <p>Your vote has been successfully recorded. Here are the details:</p>
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;">
                        <tr>
                            <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold; width: 40%%;">Election</td>
                            <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold;">Category</td>
                            <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding: 10px; border: 1px solid #ddd; font-weight: bold;">Your Vote</td>
                            <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                        </tr>
                    </table>
                    <p>Thank you for exercising your democratic right!</p>
                </div>
            </body>
            </html>
            """.formatted(fullName, electionTitle, categoryName, candidateName);
    }

    private String buildAccountApprovedEmailBody(String fullName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background-color: #2e7d32; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">Account Approved!</h1>
                </div>
                <div style="background-color: #f5f5f5; padding: 30px; border-radius: 0 0 8px 8px;">
                    <h2>Hello, %s!</h2>
                    <p>Your voter account has been approved by the administrator.</p>
                    <p>You can now log in and participate in active elections.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s/login" style="background-color: #2e7d32; color: white; padding: 14px 28px;
                           text-decoration: none; border-radius: 4px; font-size: 16px;">
                            Login Now
                        </a>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, baseUrl);
    }

    private String buildAccountDeactivatedEmailBody(String fullName) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background-color: #b71c1c; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">Account Deactivated</h1>
                </div>
                <div style="background-color: #f5f5f5; padding: 30px; border-radius: 0 0 8px 8px;">
                    <h2>Hello, %s!</h2>
                    <p>Your voter account has been deactivated by the administrator.</p>
                    <p>If you believe this is an error, please contact the system administrator.</p>
                </div>
            </body>
            </html>
            """.formatted(fullName);
    }
}