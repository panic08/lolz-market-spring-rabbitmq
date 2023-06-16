package ru.panic.template.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.service.impl.WithdrawalServiceImpl;

@Component
@RabbitListener(queues = "withdrawal-queue")
public class WithdrawalRabbitListener {
    public WithdrawalRabbitListener(WithdrawalServiceImpl withdrawalService) {
        this.withdrawalService = withdrawalService;
    }
    private final WithdrawalServiceImpl withdrawalService;
    @RabbitHandler
    private void getWithdrawal(String request){
        ObjectMapper objectMapper = new ObjectMapper();
        WithdrawalRequestDto withdrawalRequestDto = null;

        try {
            withdrawalRequestDto = objectMapper.readValue(request, WithdrawalRequestDto.class);
        } catch (Exception e){
            e.printStackTrace();
        }

        withdrawalService.handleWithdrawal(withdrawalRequestDto);
    }
}
