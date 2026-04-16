package id.ac.ui.cs.a04.json.wallet.controller;

import id.ac.ui.cs.a04.json.wallet.dto.OrderAmountRequest;
import id.ac.ui.cs.a04.json.wallet.dto.RequestStatusResponse;
import id.ac.ui.cs.a04.json.wallet.dto.TopUpRequestDto;
import id.ac.ui.cs.a04.json.wallet.dto.UserIdRequest;
import id.ac.ui.cs.a04.json.wallet.dto.WalletBalanceResponse;
import id.ac.ui.cs.a04.json.wallet.dto.WithdrawRequestDto;
import id.ac.ui.cs.a04.json.wallet.model.WalletTransaction;
import id.ac.ui.cs.a04.json.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletControllerTest {

    @Test
    void getBalanceShouldAuthorizeAndDelegate() {
        WalletService service = mock(WalletService.class);
        WalletAccessGuard guard = mock(WalletAccessGuard.class);
        WalletBalanceResponse response = new WalletBalanceResponse(1L, new BigDecimal("100"), "IDR");
        when(service.getBalance(1L)).thenReturn(response);
        WalletController controller = new WalletController(service, guard);
        UserIdRequest request = new UserIdRequest(1L);

        var entity = controller.getBalance(new UsernamePasswordAuthenticationToken("1", null), request);

        verify(guard).requireUserAccess(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(true));
        assertEquals(response, entity.getBody());
    }

    @Test
    void getTransactionsShouldAuthorizeAndDelegate() {
        WalletService service = mock(WalletService.class);
        WalletAccessGuard guard = mock(WalletAccessGuard.class);
        WalletController controller = new WalletController(service, guard);
        UserIdRequest request = new UserIdRequest(1L);
        when(service.getTransactions(1L)).thenReturn(List.of(mock(WalletTransaction.class)));

        var entity = controller.getTransactions(new UsernamePasswordAuthenticationToken("1", null), request);

        verify(guard).requireUserAccess(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(true));
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void topUpShouldReturnCreatedRequestStatus() {
        WalletService service = mock(WalletService.class);
        WalletAccessGuard guard = mock(WalletAccessGuard.class);
        WalletController controller = new WalletController(service, guard);
        TopUpRequestDto request = new TopUpRequestDto(1L, new BigDecimal("100"));
        when(service.createTopUpRequest(request)).thenReturn(9L);

        var entity = controller.topUp(new UsernamePasswordAuthenticationToken("1", null), request);

        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
        assertEquals(new RequestStatusResponse(9L, true), entity.getBody());
    }

    @Test
    void topUpMarkSuccessShouldReturnServiceResult() {
        WalletService service = mock(WalletService.class);
        WalletController controller = new WalletController(service, mock(WalletAccessGuard.class));
        when(service.markTopUpSuccess(3L)).thenReturn(true);

        var entity = controller.topUpMarkSuccess(3L);

        assertEquals(new RequestStatusResponse(3L, true), entity.getBody());
    }

    @Test
    void topUpMarkFailedShouldReturnServiceResult() {
        WalletService service = mock(WalletService.class);
        WalletController controller = new WalletController(service, mock(WalletAccessGuard.class));
        when(service.markTopUpFailed(4L)).thenReturn(false);

        var entity = controller.topUpMarkFailed(4L);

        assertEquals(new RequestStatusResponse(4L, false), entity.getBody());
    }

    @Test
    void withdrawShouldReturnCreatedRequestStatus() {
        WalletService service = mock(WalletService.class);
        WalletAccessGuard guard = mock(WalletAccessGuard.class);
        WalletController controller = new WalletController(service, guard);
        WithdrawRequestDto request = new WithdrawRequestDto(1L, new BigDecimal("50"), "bank");
        when(service.createWithdrawRequest(request)).thenReturn(10L);

        var entity = controller.withdraw(new UsernamePasswordAuthenticationToken("1", null), request);

        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
        assertEquals(new RequestStatusResponse(10L, true), entity.getBody());
    }

    @Test
    void withdrawMarkSuccessShouldReturnServiceResult() {
        WalletService service = mock(WalletService.class);
        WalletController controller = new WalletController(service, mock(WalletAccessGuard.class));
        when(service.markWithdrawSuccess(5L)).thenReturn(true);

        var entity = controller.withdrawMarkSuccess(5L);

        assertEquals(new RequestStatusResponse(5L, true), entity.getBody());
    }

    @Test
    void withdrawMarkFailedShouldReturnServiceResult() {
        WalletService service = mock(WalletService.class);
        WalletController controller = new WalletController(service, mock(WalletAccessGuard.class));
        when(service.markWithdrawFailed(6L)).thenReturn(false);

        var entity = controller.withdrawMarkFailed(6L);

        assertEquals(new RequestStatusResponse(6L, false), entity.getBody());
    }

    @Test
    void deductShouldAuthorizeAndDelegate() {
        WalletService service = mock(WalletService.class);
        WalletAccessGuard guard = mock(WalletAccessGuard.class);
        WalletController controller = new WalletController(service, guard);
        OrderAmountRequest request = new OrderAmountRequest(1L, new BigDecimal("25"), 8L);
        WalletBalanceResponse response = new WalletBalanceResponse(1L, new BigDecimal("75"), "IDR");
        when(service.deduct(1L, 8L, new BigDecimal("25"))).thenReturn(response);

        var entity = controller.deduct(new UsernamePasswordAuthenticationToken("1", null), request);

        assertEquals(response, entity.getBody());
    }

    @Test
    void refundShouldAuthorizeAndDelegate() {
        WalletService service = mock(WalletService.class);
        WalletAccessGuard guard = mock(WalletAccessGuard.class);
        WalletController controller = new WalletController(service, guard);
        OrderAmountRequest request = new OrderAmountRequest(1L, new BigDecimal("25"), 8L);
        WalletBalanceResponse response = new WalletBalanceResponse(1L, new BigDecimal("100"), "IDR");
        when(service.refund(1L, 8L, new BigDecimal("25"))).thenReturn(response);

        var entity = controller.refund(new UsernamePasswordAuthenticationToken("1", null), request);

        assertEquals(response, entity.getBody());
    }
}
