package org.zoo.swan.core.disruptor.handler;

import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.enums.EventTypeEnum;
import org.zoo.swan.core.concurrent.ConsistentHashSelector;
import org.zoo.swan.core.coordinator.SwanCoordinatorService;
import org.zoo.swan.core.disruptor.AbstractDisruptorConsumerExecutor;
import org.zoo.swan.core.disruptor.DisruptorConsumerFactory;
import org.zoo.swan.core.disruptor.event.SwanTransactionEvent;

/**
 * this is disruptor consumer.
 *
 * @author dzc
 */
public class SwanConsumerLogDataHandler extends AbstractDisruptorConsumerExecutor<SwanTransactionEvent> implements DisruptorConsumerFactory {

    private ConsistentHashSelector executor;

    private final SwanCoordinatorService coordinatorService;

    public SwanConsumerLogDataHandler(final ConsistentHashSelector executor, final SwanCoordinatorService coordinatorService) {
        this.executor = executor;
        this.coordinatorService = coordinatorService;
    }

    @Override
    public String fixName() {
        return "CatConsumerDataHandler";
    }

    @Override
    public AbstractDisruptorConsumerExecutor create() {
        return this;
    }

    @Override
    public void executor(final SwanTransactionEvent event) {
        String transId = event.getCatTransaction().getTransId();
        executor.select(transId).execute(() -> {
            EventTypeEnum eventTypeEnum = EventTypeEnum.buildByCode(event.getType());
            switch (eventTypeEnum) {
                case SAVE:
                    coordinatorService.save(event.getCatTransaction());
                    break;
                case DELETE:
                    coordinatorService.remove(event.getCatTransaction().getTransId());
                    break;
                case UPDATE_STATUS:
                    final SwanTransaction catTransaction = event.getCatTransaction();
                    break;
                case UPDATE_PARTICIPANT:
                    coordinatorService.updateParticipant(event.getCatTransaction());
                    break;
                default:
                    break;
            }
            event.clear();
        });
    }
}
