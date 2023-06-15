package ru.panic.template.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.panic.template.dto.ReplenishmentHashRequestDto;
import ru.panic.template.service.impl.ReplenishmentServiceImpl;

@Component
@RabbitListener(queues = "replenishment-queue")
public class ReplenishmentRabbitListener {
    public ReplenishmentRabbitListener(ReplenishmentServiceImpl replenishmentService) {
        this.replenishmentService = replenishmentService;
    }
    private final ReplenishmentServiceImpl replenishmentService;
    @RabbitHandler
    private void getReplenishment(String request){
        ObjectMapper objectMapper = new ObjectMapper();
        ReplenishmentHashRequestDto replenishmentHashRequestDto = null;

        try {
            replenishmentHashRequestDto = objectMapper.readValue(request, ReplenishmentHashRequestDto.class);
        } catch (Exception e){
            e.printStackTrace();
        }

        replenishmentService.handleReplenishment(replenishmentHashRequestDto);
    }
}
