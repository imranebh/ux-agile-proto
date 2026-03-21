package net.est.uxagile.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.est.uxagile.dto.AuthDtos;
import net.est.uxagile.dto.PaymentDtos;
import net.est.uxagile.dto.RideDtos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MainFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteMainRideFlow() throws Exception {
        AuthDtos.RegisterRequest register = new AuthDtos.RegisterRequest();
        register.setEmail("flowuser@autostop.dev");
        register.setPassword("password123");
        register.setFullName("Flow User");
        register.setPhone("+237600000011");

        String tokenPayload = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(tokenPayload).get("token").asText();

        RideDtos.CreateRideRequest createRideRequest = new RideDtos.CreateRideRequest();
        createRideRequest.setPickupAddress("A");
        createRideRequest.setDestinationAddress("B");
        createRideRequest.setPickupLat(3.85);
        createRideRequest.setPickupLng(11.50);
        createRideRequest.setDestinationLat(3.88);
        createRideRequest.setDestinationLng(11.53);

        String ridePayload = mockMvc.perform(post("/api/rides")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRideRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long rideId = objectMapper.readTree(ridePayload).get("id").asLong();

        PaymentDtos.AddPaymentMethodRequest method = new PaymentDtos.AddPaymentMethodRequest();
        method.setProvider("MOCK_STRIPE");
        method.setBrand("VISA");
        method.setCardNumber("4242424242424242");
        method.setExpMonth("12");
        method.setExpYear("30");
        method.setCvv("123");

        mockMvc.perform(post("/api/payments/methods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(method)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/rides/{id}/trigger-safety-check", rideId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/rides/{id}/validate-driver", rideId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/rides/{id}/start", rideId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/rides/{id}/arrive", rideId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/rides/{id}/complete", rideId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/payments/charge-ride/{id}", rideId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/invoices/{id}", rideId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateRideEstimateAndPaymentMethod() throws Exception {
        AuthDtos.RegisterRequest register = new AuthDtos.RegisterRequest();
        register.setEmail("newuser@autostop.dev");
        register.setPassword("password123");
        register.setFullName("New User");
        register.setPhone("+237600000010");

        String authPayload = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(authPayload).get("token").asText();

        RideDtos.EstimateRequest estimateRequest = new RideDtos.EstimateRequest();
        estimateRequest.setPickupLat(3.84);
        estimateRequest.setPickupLng(11.50);
        estimateRequest.setDestinationLat(3.90);
        estimateRequest.setDestinationLng(11.55);

        mockMvc.perform(post("/api/rides/estimate")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estimateRequest)))
                .andExpect(status().isOk());

        PaymentDtos.AddPaymentMethodRequest method = new PaymentDtos.AddPaymentMethodRequest();
        method.setProvider("MOCK_STRIPE");
        method.setBrand("VISA");
        method.setCardNumber("4242424242424242");
        method.setExpMonth("12");
        method.setExpYear("30");
        method.setCvv("123");

        mockMvc.perform(post("/api/payments/methods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(method)))
                .andExpect(status().isOk());
    }
}
