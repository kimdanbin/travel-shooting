package com.example.travelshooting.reservation.service;

import com.example.travelshooting.company.entity.Company;
import com.example.travelshooting.enums.UserRole;
import com.example.travelshooting.notification.service.ReservationMailService;
import com.example.travelshooting.part.entity.Part;
import com.example.travelshooting.part.service.PartService;
import com.example.travelshooting.product.entity.Product;
import com.example.travelshooting.product.service.ProductService;
import com.example.travelshooting.reservation.repository.ReservationRepository;
import com.example.travelshooting.user.entity.User;
import com.example.travelshooting.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock lock;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private PartService partService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMailService reservationMailService;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    @DisplayName("예약 동시성 제어")
    public void testCreateReservation() throws InterruptedException {
        // given
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        Integer headCount = 1;

        Product mockProduct = new Product("상품A", "설명", 10000, "주소", LocalDate.now(), LocalDate.now().plusDays(2), new Company());
        ReflectionTestUtils.setField(mockProduct, "id", 1L);
        when(productService.findProductById(mockProduct.getId())).thenReturn(mockProduct);

        User mockUser = new User("user1@naver.com", "유저1", "1234", UserRole.USER, "image");
        when(userService.findAuthenticatedUser()).thenReturn(mockUser);

        Part mockPart = new Part(LocalTime.now().plusHours(1), LocalTime.now().plusHours(2), 2, mockProduct);
        ReflectionTestUtils.setField(mockPart, "id", 1L);
        when(partService.findPartById(mockPart.getId())).thenReturn(mockPart);

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true, true, false);

        when(reservationRepository.existsReservationByUserIdAndReservationDate(mockUser.getId(), reservationDate)).thenReturn(false);
        when(reservationRepository.findTotalHeadCountByPartIdAndReservationDate(mockPart.getId(), reservationDate)).thenReturn(0);
        doNothing().when(reservationMailService).sendMail(any(), any(), any(), any(), any());

        Runnable task = () -> {
            try {
                reservationService.createReservation(mockProduct.getId(), mockPart.getId(), reservationDate, headCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // when
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        Thread thread3 = new Thread(task);

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        verify(lock, atLeast(2)).tryLock(anyLong(), anyLong(), any());
    }
}