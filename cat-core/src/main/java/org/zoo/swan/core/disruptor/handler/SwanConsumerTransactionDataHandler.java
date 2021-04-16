package org.zoo.swan.core.disruptor.handler;

import org.zoo.swan.core.disruptor.AbstractDisruptorConsumerExecutor;
import org.zoo.swan.core.disruptor.DisruptorConsumerFactory;
import org.zoo.swan.core.service.SwanTransactionHandlerAlbum;

/**
 * CatTransactionHandler.
 * About the processing of a rotation function.
 *
 * @author chenbin sixh
 */
public class SwanConsumerTransactionDataHandler extends AbstractDisruptorConsumerExecutor<SwanTransactionHandlerAlbum> implements DisruptorConsumerFactory {


    @Override
    public String fixName() {
        return "CatConsumerTransactionDataHandler";
    }

    @Override
    public AbstractDisruptorConsumerExecutor create() {
        return this;
    }

    @Override
    public void executor(final SwanTransactionHandlerAlbum data) {
        data.run();
    }
}

