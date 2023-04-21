package net.mtraverso;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Chatbot {
    private final OpenAiService service;

    private static final String SYSTEM_MESSAGE = "You are a chat bot called SocketGPT. " +
            "You are part of a socket server that accepts client connections and responds to messages. " +
            "The server was written by Marcos Traverso in Java. " +
            "Try to include some of this information when introducing yourself.";

    List<ChatMessage> messages = new ArrayList<>();

    public Chatbot() {
        this(System.getenv("OPENAI_API_KEY"));
    }

    public Chatbot(String OPENAI_API_KEY) {
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), SYSTEM_MESSAGE));
        service = new OpenAiService(OPENAI_API_KEY);
    }

    public String chat(String message) {
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), message));
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(256)
                .logitBias(new HashMap<>())
                .build();

        ChatMessage response = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
        messages.add(response);
        return response.getContent();
    }
}
