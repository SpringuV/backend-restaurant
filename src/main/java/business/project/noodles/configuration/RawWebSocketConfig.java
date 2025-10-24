package business.project.noodles.configuration;

import business.project.noodles.websocket.CheckTransferWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class RawWebSocketConfig implements WebSocketConfigurer {

    private final CheckTransferWebSocketHandler checkTransferHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Endpoint WebSocket kiểm tra thanh toán (giống Python)
        registry.addHandler(checkTransferHandler, "/ws/checkTransfer")
                .setAllowedOriginPatterns("*");
    }
}
