# cgv19-aicb – AI CodeBlock

A cgV19 module that provides an `AiCodeBlock` which calls an LLM (via **LangChain4j**) in its `toCode()` method and returns the generated response as code.

## Quick Start

### In a Groovy/Java Template

**Without post-processing (simplest form):**

```groovy
import de.spraener.nxtgen.aicb.AiCodeBlock

def generateLogic(MClass mc) {
    String prompt = """
        Write Java business logic for the following class:
        ${mc}
        Return only method bodies, no surrounding class declaration.
    """.stripIndent()

    sb.append(AiCodeBlock.resolve(prompt))
    return sb.toString()
}
```

**With post-processing (Groovy trailing closure):**

```groovy
def generateLogic(MClass mc) {
    String prompt = "Write Java code for: ${mc}"

    sb.append(AiCodeBlock.resolve(prompt) { text ->
        // Remove Markdown code fences and trim
        text.replaceAll('```(?:java)?', '').trim() + '\n'
    })
    return sb.toString()
}
```

**With post-processing (Java style):**

```java
Function<String, String> postProcessor = s -> s.trim() + "\n";
sb.append(AiCodeBlock.resolve(prompt, postProcessor));
```

## Configuration

### 1. Set the API Key

The API key is provided via the environment variable `CGV19_LLM_API_KEY`:

```bash
export CGV19_LLM_API_KEY="sk-..."
```

Or via a `.env` file (e.g. with `java-dotenv`).

### 2. ai-config.json (optional)

The configuration can be placed in three locations. Values from lower locations **override** values from higher ones:

| Priority | Location | Description |
|----------|----------|-------------|
| 1 (base) | `${HOME}/.cgv19/ai-config.json` | Global user config – applies to all projects |
| 2 (override) | Classpath `/ai-config.json` | Project/cartridge config – overrides global settings |
| 3 (final) | `./.cgv19/ai-config.json` | Local directory config – highest priority, overrides everything |

**Example `ai-config.json`:**

```json
{
  "type": "langchain4j",
  "url": "https://api.openai.com/v1/chat/completions",
  "model": "gpt-4o-mini",
  "postProcessorClass": null
}
```

| Field | Description |
|-------|-------------|
| `type` | LLM type (`langchain4j`). Extensible for future providers. |
| `url` | Endpoint URL of the LLM service. |
| `model` | Model name (e.g. `gpt-4o-mini`, `gpt-4`). Default: `gpt-4o-mini`. |
| `postProcessorClass` | Optional: fully-qualified class name of a `PostProcessor` implementation. |

**Without any config file**, the **mock client** is used automatically, which returns `"// mock AI output"`. This makes the module work out-of-the-box for testing and development without needing an LLM API key.

## Cache

The cache stores LLM responses persistently under `$HOME/.cgv19/llm-cache`.

- **Key**: `SHA-256(prompt + configHash)` – the same prompt with the same configuration always produces the same cache key.
- **Value**: The post-processed LLM response.
- **Clear the cache**: `rm -rf ~/.cgv19/llm-cache`

## Post-Processor

A `PostProcessor` is a functional interface (`Function<String, String>`) for post-processing the LLM output.

### Inline (in templates)

```groovy
Function<String, String> pp = s -> {
    // Remove Markdown code blocks
    return s.replaceAll("```java", "").replaceAll("```", "")
}
AiCodeBlock.resolve(prompt, pp)
```

### As a standalone class

```java
package my.cartridge;

import de.spraener.nxtgen.aicb.PostProcessor;

public class MarkdownStripper implements PostProcessor {
    @Override
    public String apply(String s) {
        return s.replaceAll("```(?:java)?", "").trim();
    }
}
```

And in `ai-config.json`:

```json
{
  "postProcessorClass": "my.cartridge.MarkdownStripper"
}
```

## Conversation API

For multi-step interactions, you can bind a running conversation to a `ModelElement`. The full message history is sent on each call, enabling **KV-caching** at the LLM provider (the provider re-uses cached attention for the common prefix).

```groovy
import de.spraener.nxtgen.aicb.AiCodeBlock

// First prompt – creates a conversation attached to `myModel`
sb.append(AiCodeBlock.resolve(myModel,
                              "Generate a POJO for this class"))

// Refine – the same conversation is automatically reused
sb.append(AiCodeBlock.resolve(myModel,
                              "Add a toString() method"))

// Optional: reset the conversation (e.g., start a fresh generation pass)
AiCodeBlock.clearConversation(myModel)
```

The conversation is stored in the model element's internal object bag (`MAbstractModelElement.getObject/putObject`) under the key `"__aiConversation"`. No explicit conversation management is needed in templates.

## Architecture

| Class | Role |
|-------|------|
| `AiCodeBlock` | Public API – extends `CodeBlockImpl`, static factory method `resolve()` |
| `AiConversation` | Holds the running conversation bound to a ModelElement |
| `ClientFactory` | Singleton factory for `LlmClient` (mock or LangChain4j) |
| `Config` | Loads `ai-config.json`, computes config hash |
| `Cache` | Persistent file-based cache under `.cgv19/llm-cache` |
| `LlmClient` | Interface for LLM calls (single-shot and conversation-based) |
| `LangChain4jClient` | Implementation via LangChain4j 1.9.1 |
| `MockLlmClient` | Fallback client (default when no config is present) |
| `PostProcessor` | Functional interface for post-processing |

## Error Handling

- **No API key**: Falls back to `MockLlmClient`.
- **LLM call fails**: Returns a comment: `// [AI generation failed: <error>]`.
- **PostProcessor loading fails**: Throws an exception with a clear error message.

## Build & Tests

```bash
cd core && ./gradlew :cgv19-aicb:test
```

All tests pass and cover >80% of the code.
