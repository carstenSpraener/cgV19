package de.spraener.nxtgen.aicb;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import de.spraener.nxtgen.oom.model.MAbstractModelElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a running LLM conversation bound to a ModelElement.
 * Stored lazily in the element's object bag via getObject/putObject.
 */
public class AiConversation {

    private static final String MAP_KEY = "__aiConversation";

    private final MAbstractModelElement model;
    private final List<ChatMessage> messages = new ArrayList<>();

    AiConversation(MAbstractModelElement model) {
        this.model = model;
    }

    /**
     * Lazily retrieves or creates a conversation for the given model element.
     */
    public static AiConversation get(Object model) {
        if (!(model instanceof MAbstractModelElement me)) {
            throw new IllegalArgumentException("Model must be an MAbstractModelElement");
        }

        AiConversation existing = (AiConversation) me.getObject(MAP_KEY);
        if (existing != null) {
            return existing;
        }

        AiConversation conv = new AiConversation(me);
        me.putObject(MAP_KEY, conv);
        return conv;
    }

    /**
     * Adds a user message to the conversation.
     */
    public void addUserMessage(String text) {
        messages.add(UserMessage.from(text));
    }

    /**
     * Adds an assistant message to the conversation. Package-private – only AiCodeBlock calls this.
     */
    void addAssistantMessage(String text) {
        messages.add(AiMessage.from(text));
    }

    /**
     * Returns the full message history (read-only view).
     */
    public List<ChatMessage> getMessages() {
        return new ArrayList<>(messages);
    }

    /**
     * Returns the most recent assistant reply, or null if none exists.
     */
    public String getLastAssistant() {
        for (int i = messages.size() - 1; i >= 0; i--) {
            ChatMessage msg = messages.get(i);
            if (msg instanceof AiMessage ai) {
                return ai.text();
            }
        }
        return null;
    }

    /**
     * Returns the model element this conversation is bound to.
     */
    public MAbstractModelElement getModel() {
        return model;
    }

    /**
     * Clears the conversation from the model element's object bag.
     */
    public static void clear(Object model) {
        if (model instanceof MAbstractModelElement me) {
            me.removeObject(MAP_KEY);
        }
    }
}
