//package org.example.project_module4_dvc.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        // Tin nhắn từ server đến client sẽ có tiền tố này
//        config.enableSimpleBroker("/topic");
//        // Tin nhắn từ client đến server sẽ có tiền tố này
//        config.setApplicationDestinationPrefixes("/app");
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // Endpoint để client kết nối tới WebSocket server
//        registry.addEndpoint("/ws-dvc").setAllowedOriginPatterns("*").withSockJS();
//    }
//}
