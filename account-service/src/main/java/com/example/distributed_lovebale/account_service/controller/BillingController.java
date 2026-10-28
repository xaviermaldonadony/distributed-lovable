package com.example.distributed_lovebale.account_service.controller;

import com.example.distributed_lovebale.account_service.dto.subscription.CheckoutRequest;
import com.example.distributed_lovebale.account_service.dto.subscription.CheckoutResponse;
import com.example.distributed_lovebale.account_service.dto.subscription.PortalResponse;
import com.example.distributed_lovebale.account_service.dto.subscription.SubscriptionResponse;
import com.example.distributed_lovebale.account_service.service.PaymentProcessor;
import com.example.distributed_lovebale.account_service.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BillingController {

    private final SubscriptionService subscriptionService;
    private final PaymentProcessor paymentProcessor;

    @Value("${stripe.webhook.secret}")
    String webSocketScret;


    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(
            @RequestBody CheckoutRequest request
    ) {
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(request));
    }

     @PostMapping("/api/payments/portal")
     public ResponseEntity<PortalResponse> openCustomerPortal(){
         Long userId = 1L;
         return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
     }

    @PostMapping("/webhooks/payment")
    public ResponseEntity<String> handlePaymentWebHooks(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signHeader){

        try{
            Event event = Webhook.constructEvent(payload, signHeader, webSocketScret);
            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
            StripeObject stripeObject = null;

            if (deserializer.getObject().isPresent()) { // happy case
                stripeObject = deserializer.getObject().get();
            } else {
                // Fallback: Deserialize from raw JSON
                try {
                    stripeObject = deserializer.deserializeUnsafe();
                    if (stripeObject == null) {
                        log.warn("Failed to deserialize webhook object for event: {}", event.getType());
                        return ResponseEntity.ok().build();
                    }
                } catch (Exception e) {
                    log.error("Unsafe deserialization failed for event {}: {}", event.getType(), e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deserialization failed");
                }
            }
            // Now extract metadata only if it's a Checkout Session
            Map<String, String> metadata = new HashMap<>();
            if (stripeObject instanceof Session session) {
                metadata = session.getMetadata();
            }

            // Pass to your processor
            paymentProcessor.handleWebhookEvent(event.getType(), stripeObject, metadata);

            return ResponseEntity.ok().build();
        }catch(SignatureVerificationException e){
            throw new RuntimeException(e);
        }
    }
}

//stripe listen --forward-to localhost:8080/webhook/payment \ --events checkout.session.completed, customer.subscription.crated, customer.subscription.updated, customer.subscription.deleted, inovoice.paid, invoice.payment_failed
