package com.orion.ops.module.asset.handler.host.terminal;

import com.orion.ops.framework.websocket.core.handler.TextWebSocketHandler;
import com.orion.ops.module.asset.handler.host.terminal.enums.InputTypeEnum;
import com.orion.ops.module.asset.handler.host.terminal.manager.TerminalManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 终端处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2023/12/28 14:33
 */
@Slf4j
@Component
public class TerminalMessageDispatcher extends TextWebSocketHandler {

    @Resource
    private TerminalManager terminalManager;

    @Override
    public void onMessage(WebSocketSession session, String payload) {
        try {
            // 解析类型
            InputTypeEnum type = InputTypeEnum.of(payload);
            if (type != null) {
                // 解析并处理消息
                type.getHandler().handle(session, type.parse(payload));
            }
        } catch (Exception e) {
            log.error("TerminalDispatchHandler-handleMessage-error id: {}, msg: {}", session.getId(), payload, e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("TerminalMessageDispatcher-handleTransportError id: {}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String id = session.getId();
        log.info("TerminalMessageDispatcher-afterConnectionClosed  id: {}, code: {}, reason: {}", id, status.getCode(), status.getReason());
        // 关闭会话
        terminalManager.closeAll(id);
    }

}
