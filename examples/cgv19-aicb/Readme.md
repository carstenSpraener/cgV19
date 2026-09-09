# AiCodeBlock Example

Demonstrates AI-assisted code generation using `AiCodeBlock` in Groovy templates.

## Concepts

### Two Ways to Call the LLM

`AiCodeBlock` offers two `resolve()` methods:

1. **Single-shot** — `resolve(prompt)` or `resolve(prompt, postProcessor)`: sends one prompt to the LLM and returns the result. No context is kept between calls.

2. **Conversation** — `resolve(modelElement, prompt)` or `resolve(modelElement, prompt, postProcessor)`: binds a conversation to a specific model element. Each call appends the new prompt to the full message history and sends everything to the LLM. The model "remembers" what was generated before.

### Why Two `resolve()` Calls in the Template?

```groovy
AiCodeBlock block = AiCodeBlock
        .resolve(mc, prompt)                          // 1. Generate the class
        .resolve(mc,"Füge equals/hashCode hinzu", ...) // 2. Extend the class
```

The first call generates the complete `GreetingService` from the prompt. The second call continues the *same conversation* bound to `mc` — the LLM sees the full history (including the class it just generated) and adds `equals()` and `hashCode()`.

This is useful when you want to iteratively refine generated code without repeating context. Each `resolve(mc, ...)` call builds on the previous one.

### Caching

Results are cached using a hash of the full conversation + config hash. If you re-run generation with the same model and config, cached results are returned instantly — no LLM calls. Change the prompt or config, and a new cache key is computed.

### Post-Processing

Both methods accept an optional `Function<String, String>` post-processor. The template uses `AiCodeBlock::removeJavaFence` to strip markdown code fences from the LLM output.

## How it works

1. The `de.spraener.nxtgen.aicb.oom` model defines a `GreetingService` POJO with attributes and operations
2. The `AiCodeBlockGenerator` runs the `AiPoJoTemplate.groovy` template
3. The template uses two chained `resolve()` calls:
   - First: generates the complete Java class from a structured prompt
   - Second: adds `equals()` and `hashCode()` in the same conversation context
4. Results are cached for deterministic regeneration

## Prerequisites

- Configure LLM access via `~/.cgv19/ai-config.json` or `.cgv19/ai-config.json`:
  ```json
  {
    "type": "langchain4j",
    "url": "http://localhost:1234/v1",
    "model": "your-model-name"
  }
  ```
- Set environment variable: `CGV19_LLM_API_KEY`

## Run

```bash
./gradlew :cgv19-aicb:test
```

Generated output appears in `build/demo-app/src/main/java-gen/`.
