package de.spraener.nxtgen.aicb;

import dev.langchain4j.data.message.ChatMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AiConversationTest {

    @Test
    void get_createsNewConversationOnFirstCall() {
        de.spraener.nxtgen.oom.model.MAbstractModelElement model = new de.spraener.nxtgen.oom.model.MAbstractModelElement();
        AiConversation conv = AiConversation.get(model);

        assertThat(conv).isNotNull();
        assertThat(conv.getModel()).isSameAs(model);
        assertThat(conv.getMessages()).isEmpty();
    }

    @Test
    void get_returnsSameInstanceOnSubsequentCalls() {
        de.spraener.nxtgen.oom.model.MAbstractModelElement model = new de.spraener.nxtgen.oom.model.MAbstractModelElement();
        AiConversation conv1 = AiConversation.get(model);
        AiConversation conv2 = AiConversation.get(model);

        assertThat(conv1).isSameAs(conv2);
    }

    @Test
    void addUserMessage_appendsToHistory() {
        de.spraener.nxtgen.oom.model.MAbstractModelElement model = new de.spraener.nxtgen.oom.model.MAbstractModelElement();
        AiConversation conv = AiConversation.get(model);

        conv.addUserMessage("Hello");
        conv.addUserMessage("World");

        List<ChatMessage> messages = conv.getMessages();
        assertThat(messages).hasSize(2);
    }

    @Test
    void getLastAssistant_returnsMostRecentReply() {
        de.spraener.nxtgen.oom.model.MAbstractModelElement model = new de.spraener.nxtgen.oom.model.MAbstractModelElement();
        AiConversation conv = AiConversation.get(model);

        conv.addUserMessage("Q1");
        conv.addAssistantMessage("A1");
        conv.addUserMessage("Q2");
        conv.addAssistantMessage("A2");

        assertThat(conv.getLastAssistant()).isEqualTo("A2");
    }

    @Test
    void clear_removesConversationFromModel() {
        de.spraener.nxtgen.oom.model.MAbstractModelElement model = new de.spraener.nxtgen.oom.model.MAbstractModelElement();
        AiConversation conv1 = AiConversation.get(model);
        conv1.addUserMessage("test");

        AiConversation.clear(model);

        // After clear, get() should create a fresh conversation
        AiConversation conv2 = AiConversation.get(model);
        assertThat(conv2).isNotSameAs(conv1);
        assertThat(conv2.getMessages()).isEmpty();
    }

    @Test
    void get_throwsExceptionForNonMAbstractModelElement() {
        Object invalidModel = "not a model element";

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> AiConversation.get(invalidModel)
        );
    }
}
