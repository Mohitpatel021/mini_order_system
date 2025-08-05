package com.qurilo.order_service.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import com.qurilo.order_service.dto.OrderResponse;
import com.qurilo.order_service.dto.UserResponse;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	private final Executor emailExecutor = Executors.newFixedThreadPool(2);

	public void sendOrderConfirmationEmail(UserResponse user, OrderResponse order) {
		CompletableFuture.runAsync(() -> {
			try {
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
				
				helper.setTo(user.getEmail());
				helper.setSubject("Order Confirmation - Order #" + order.getId());
				helper.setText(createOrderConfirmationEmailContent(user, order), true);
				
				mailSender.send(message);
				logger.info("Order confirmation email sent successfully to user: {} for order: {}", 
						user.getEmail(), order.getId());
				
			} catch (Exception e) {
				logger.error("Failed to send order confirmation email to user: {} for order: {}. Error: {}", 
						user.getEmail(), order.getId(), e.getMessage(), e);
			}
		}, emailExecutor);
	}

	private String createOrderConfirmationEmailContent(UserResponse user, OrderResponse order) {
		try {
			ClassPathResource resource = new ClassPathResource("templates/order-confirmation-email.html");
			String htmlTemplate = StreamUtils.copyToString(resource.getInputStream(), java.nio.charset.StandardCharsets.UTF_8);
			
			String htmlContent = htmlTemplate
					.replace("{{userName}}", user.getFirstName() + " " + user.getLastName())
					.replace("{{orderId}}", String.valueOf(order.getId()))
					.replace("{{productName}}", order.getProductName())
					.replace("{{quantity}}", String.valueOf(order.getQuantity()))
					.replace("{{unitPrice}}", String.valueOf(order.getUnitPrice()))
					.replace("{{status}}", order.getStatus())
					.replace("{{orderDate}}", order.getCreatedAt())
					.replace("{{totalAmount}}", String.valueOf(order.getTotalPrice()));
			
			return htmlContent;
		} catch (IOException e) {
			logger.error("Failed to read email template: {}", e.getMessage());
			return createFallbackEmailContent(user, order);
		}
	}
	
	private String createFallbackEmailContent(UserResponse user, OrderResponse order) {
		StringBuilder content = new StringBuilder();
		content.append("Dear ").append(user.getFirstName()).append(" ").append(user.getLastName()).append(",\n\n");
		content.append("Thank you for your order! Your order has been confirmed and is being processed.\n\n");
		content.append("Order Details:\n");
		content.append("Order ID: #").append(order.getId()).append("\n");
		content.append("Product: ").append(order.getProductName()).append("\n");
		content.append("Quantity: ").append(order.getQuantity()).append("\n");
		content.append("Unit Price: ₹").append(order.getUnitPrice()).append("\n");
		content.append("Total Amount: ₹").append(order.getTotalPrice()).append("\n");
		content.append("Order Status: ").append(order.getStatus()).append("\n");
		content.append("Order Date: ").append(order.getCreatedAt()).append("\n\n");
		content.append("We will notify you once your order is shipped.\n\n");
		content.append("Best regards,\n");
		content.append("Qurilo Team");
		
		return content.toString();
	}
} 