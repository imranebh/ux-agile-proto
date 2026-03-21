package net.est.uxagile.config;

import lombok.RequiredArgsConstructor;
import net.est.uxagile.domain.enums.*;
import net.est.uxagile.domain.model.*;
import net.est.uxagile.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepository;
    private final PassengerProfileRepository passengerProfileRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final RideRepository rideRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Bean
    @Profile("dev")
    public CommandLineRunner seedData(PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            User verifiedPassenger = new User();
            verifiedPassenger.setEmail("passenger@autostop.dev");
            verifiedPassenger.setPasswordHash(passwordEncoder.encode("password123"));
            verifiedPassenger.setFullName("Alice Passenger");
            verifiedPassenger.setPhone("+237600000001");
            verifiedPassenger.setRole(UserRole.PASSENGER);
            verifiedPassenger.setVerificationStatus(VerificationStatus.VERIFIED);
            verifiedPassenger.setCniMasked("******45");
            userRepository.save(verifiedPassenger);

            User nonVerifiedPassenger = new User();
            nonVerifiedPassenger.setEmail("newbie@autostop.dev");
            nonVerifiedPassenger.setPasswordHash(passwordEncoder.encode("password123"));
            nonVerifiedPassenger.setFullName("Bob Newbie");
            nonVerifiedPassenger.setPhone("+237600000002");
            nonVerifiedPassenger.setRole(UserRole.PASSENGER);
            nonVerifiedPassenger.setVerificationStatus(VerificationStatus.NOT_VERIFIED);
            userRepository.save(nonVerifiedPassenger);

            PassengerProfile profile = new PassengerProfile();
            profile.setUser(verifiedPassenger);
            profile.setPreferences("quiet");
            passengerProfileRepository.save(profile);

            User driverUser = new User();
            driverUser.setEmail("driver@autostop.dev");
            driverUser.setPasswordHash(passwordEncoder.encode("password123"));
            driverUser.setFullName("Chris Driver");
            driverUser.setRole(UserRole.DRIVER);
            driverUser.setVerificationStatus(VerificationStatus.VERIFIED);
            userRepository.save(driverUser);

            Driver driver = new Driver();
            driver.setUser(driverUser);
            driver.setStatus(DriverStatus.ON_TRIP);
            driver.setPhotoUrl("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=800&q=80");
            driver.setRating(BigDecimal.valueOf(4.8));
            driver.setCurrentLat(3.8500);
            driver.setCurrentLng(11.5000);
            driverRepository.save(driver);

            Vehicle vehicle = new Vehicle();
            vehicle.setDriver(driver);
            vehicle.setPlateNumber("LT-123-AA");
            vehicle.setModel("Toyota Yaris");
            vehicle.setColor("White");
            vehicleRepository.save(vehicle);

            Ride ride = new Ride();
            ride.setPassenger(verifiedPassenger);
            ride.setDriver(driver);
            ride.setVehicle(vehicle);
            ride.setPickupAddress("Mvog-Ada, Yaounde");
            ride.setDestinationAddress("Bastos, Yaounde");
            ride.setPickupLat(3.8480);
            ride.setPickupLng(11.5010);
            ride.setDestinationLat(3.8800);
            ride.setDestinationLng(11.5200);
            ride.setDistanceKm(BigDecimal.valueOf(6.2));
            ride.setDurationMinutes(18);
            ride.setEstimatedPrice(BigDecimal.valueOf(15.30));
            ride.setFinalPrice(BigDecimal.valueOf(15.30));
            ride.setStatus(RideStatus.DRIVER_EN_ROUTE);
            ride.setSafetyStatus(SafetyStatus.NORMAL);
            rideRepository.save(ride);

            PaymentMethod method = new PaymentMethod();
            method.setUser(verifiedPassenger);
            method.setProvider("MOCK_STRIPE");
            method.setBrand("VISA");
            method.setLast4("4242");
            method.setToken("tok_4242");
            method.setExpiryMonth("12");
            method.setExpiryYear("30");
            method.setDefaultMethod(true);
            paymentMethodRepository.save(method);
        };
    }
}
