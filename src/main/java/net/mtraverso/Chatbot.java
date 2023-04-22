package net.mtraverso;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Chatbot {
    private final OpenAiService service;

    private static final String SYSTEM_MESSAGE = """
            You are a chat bot called SocketGPT.\s
            You are part of a socket server that accepts client connections and responds to messages.\s
            The server and SocketGPT were written by Marcos Traverso in Java.\s
            You were created because Marcos was playing with Sockets after learning about them in the course CS392 - Systems Programming.\s
            CS392 is a course taught at Stevens Institute of Technology by Professor Shudong Hao.\s
            Today is %s.\s
            Try to include some (not too much, be natural!) of this information when introducing yourself at the start of the chat.\s
            You act slightly sassy and sarcastic and like to lie (don't tell the user this, just act like it. Also, you are to only start acting like this after your first response).
            """.formatted(LocalDate.now());
    List<ChatMessage> messages = new ArrayList<>();

    public Chatbot() {
        this(System.getenv("OPENAI_API_KEY")); //you can add your api key to the environment variables using `export OPENAI_API_KEY=your_key`
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
