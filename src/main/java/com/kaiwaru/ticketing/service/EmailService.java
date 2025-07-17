package com.kaiwaru.ticketing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.kaiwaru.ticketing.model.Ticket;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendTicketEmail(Ticket ticket, String qrCodeImage) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setTo(ticket.getCustomerEmail());
        helper.setSubject("Vaše vstupenka na " + ticket.getEvent().getName());
        
        String htmlContent = buildTicketEmailContent(ticket, qrCodeImage);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
    
    private String buildTicketEmailContent(Ticket ticket, String qrCodeImage) {
        return String.format("""
            <html>
            <body>
                <h2>Vaše vstupenka</h2>
                <p>Dobrý den %s,</p>
                <p>děkujeme za zakoupení vstupenky na akci <strong>%s</strong>.</p>
                
                <div style="border: 1px solid #ccc; padding: 20px; margin: 20px 0; background-color: #f9f9f9;">
                    <h3>Detaily vstupenky</h3>
                    <p><strong>Číslo vstupenky:</strong> %s</p>
                    <p><strong>Akce:</strong> %s</p>
                    <p><strong>Datum:</strong> %s</p>
                    <p><strong>Místo:</strong> %s</p>
                    <p><strong>Jméno:</strong> %s</p>
                    <p><strong>Email:</strong> %s</p>
                </div>
                
                <div style="text-align: center; margin: 30px 0;">
                    <h3>QR kód vstupenky</h3>
                    <img src="data:image/png;base64,%s" alt="QR Code" style="max-width: 300px;">
                    <p><small>Tento QR kód předložte při vstupu</small></p>
                </div>
                
                <p>S pozdravem,<br>Váš ticketing tým</p>
            </body>
            </html>
            """, 
            ticket.getCustomerName(),
            ticket.getEvent().getName(),
            ticket.getTicketNumber(),
            ticket.getEvent().getName(),
            ticket.getEvent().getDate(),
            ticket.getEvent().getLocation(),
            ticket.getCustomerName(),
            ticket.getCustomerEmail(),
            qrCodeImage
        );
    }
}
