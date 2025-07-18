<template>
  <div class="chat-container">
    <!-- Header -->
    <header class="chat-header">
      <div class="header-left">
        <h2 class="title">AI Chat</h2>
        <select v-model="selectedConfig" class="model-select">
          <option v-for="c in configs" :key="c.name" :value="c">{{ c.name }}</option>
        </select>
        <select v-model="model" class="model-select">
          <option v-for="m in models" :key="m.value" :value="m.value">{{ m.label }}</option>
        </select>
        <input
          v-model="newModel"
          placeholder="Add new model"
          class="input-add"
          @keyup.enter="addModel"
        />
        <button class="btn-add" @click="addModel">Add</button>
      </div>
    </header>

    <!-- Chat Body -->
    <div class="chat-body" ref="bodyRef">
      <!-- Display messages -->
      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        :class="['chat-message', msg.sender]"
      >
        <!-- User Avatar -->
        <div v-if="msg.sender === 'user'" class="avatar">👤</div>
        <!-- AI Avatar -->
        <div v-if="msg.sender === 'ai'" class="avatar">🤖</div>

        <!-- Message Bubble -->
        <div class="bubble">
          <p class="text" v-html="renderMessage(msg.text)"></p>
          <img
            v-if="msg.image"
            :src="msg.image"
            class="image-preview"
            alt="Sent image preview"
          />
        </div>
      </div>

      <!-- Typing Indicator -->
      <div v-if="loading" class="typing-indicator">AI is typing…</div>
    </div>

    <!-- Footer -->
    <footer class="chat-footer">
      <!-- Attach Button -->
      <button
        class="btn-attach"
        @click="triggerFileInput"
        :disabled="loading"
      >
        <span class="attach-icon">📎</span>
        <span class="attach-text">Attach Image</span>
      </button>
      <input
        type="file"
        accept="image/*"
        ref="fileInputRef"
        class="hidden"
        @change="onFileChange"
      />

      <!-- Message Input -->
      <input
        v-model="text"
        @keyup.enter="send"
        :disabled="loading"
        placeholder="Type your message..."
        class="input-text"
      />

      <!-- Send Button -->
      <button
        class="btn-send"
        @click="send"
        :disabled="loading || (!text && !imageFile)"
      >
        {{ loading ? 'Sending…' : 'Send' }}
      </button>
    </footer>

    <!-- Image Preview -->
    <div v-if="preview" class="preview-container">
      <div class="preview-box">
        <img
          :src="preview"
          class="preview-thumb"
          alt="Image preview"
        />
        <button class="btn-remove" @click="removeImage">✕</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import configsData from '@/config/chat.config.json';
import { nextTick, reactive, ref, watch } from 'vue';

const configs = ref(configsData);
const selectedConfig = ref(configsData[0]);
const defaultModels = [
  { value: 'glm-4-flash', label: 'GLM-4-Flash' },
  { value: 'gpt-4', label: 'GPT-4' },
  { value: 'chatglm', label: 'ChatGLM-6B' }
];
const models = ref(
  JSON.parse(localStorage.getItem('chat_models') || 'null') ||
  defaultModels
);
const model = ref(models.value[0].value);
const newModel = ref('');
watch(
  models,
  (val) => localStorage.setItem('chat_models', JSON.stringify(val)),
  { deep: true }
);

const messages = reactive([]);
const loading = ref(false);
const text = ref('');
const imageFile = ref(null);
const preview = ref(null);
const fileInputRef = ref(null);
const bodyRef = ref(null);

// Scroll to bottom when new message is added
watch(
  () => messages.length,
  async () => {
    await nextTick();
    if (bodyRef.value) {
      bodyRef.value.scrollTop = bodyRef.value.scrollHeight;
    }
  }
);

function triggerFileInput() {
  fileInputRef.value?.click();
}

function onFileChange(e) {
  const f = e.target.files?.[0];
  if (f?.type.startsWith('image/')) {
    imageFile.value = f;
    preview.value = URL.createObjectURL(f);
  }
}

function removeImage() {
  if (preview.value) URL.revokeObjectURL(preview.value);
  imageFile.value = null;
  preview.value = null;
}

function addModel() {
  const v = newModel.value.trim();
  if (!v) return;
  models.value.push({ value: v, label: v });
  model.value = v;
  newModel.value = '';
}

function buildPayload(cfg, message) {
  switch (cfg.formatter) {
    case 'bigmodel':
    case 'openai':
      return {
        model: model.value,
        messages: [{ role: 'user', content: message }],
        temperature: 0.95,
        top_p: 0.7
      };
    case 'claude':
      return {
        prompt: message,
        model: model.value
      };
    default:
      throw new Error(`Unknown formatter: ${cfg.formatter}`);
  }
}

// 渲染不同类型的消息内容
function renderMessage(message) {
  // 处理Markdown格式内容
  if (message.startsWith('```') && message.endsWith('```')) {
    const code = message.slice(3, -3).trim(); // 去掉 ``` 
    return `<pre class="code-block">${code}</pre>`;
  }

  // 检测是否包含HTML代码，并且展示为HTML
  if (/<.*?>/.test(message)) {
    return message; // 直接返回HTML内容
  }

  return message; // 普通文本
}

function simulateTypingEffect(message, callback) {
  let index = 0;
  const interval = setInterval(() => {
    if (index < message.length) {
      callback(message.slice(0, index + 1)); // 每次更新当前文本
      index++;
    } else {
      clearInterval(interval); // 完成时停止
    }
  }, 100); // 每个字符的间隔时间
}

// 发送消息
async function send() {
  if (loading.value || (!text.value.trim() && !imageFile.value)) return;

  // 发送用户消息
  messages.push({
    sender: 'user',
    text: text.value,
    image: preview.value
  });
  loading.value = true;

  try {
    const cfg = selectedConfig.value;
    const payload = buildPayload(cfg, text.value);

    const headers = { 'Content-Type': 'application/json' };
    if (cfg.formatter === 'bigmodel' || cfg.formatter === 'openai') {
      headers['Authorization'] = `Bearer ${cfg.apiKey}`;
    } else if (cfg.formatter === 'claude') {
      headers['x-api-key'] = cfg.apiKey;
    }

    const res = await fetch(cfg.url.trim(), {
      method: 'POST',
      headers,
      body: JSON.stringify(payload)
    });

    if (res.status === 401)
      throw new Error('401 Unauthorized: Check your API key or permissions');
    if (!res.ok) throw new Error(`API error (${res.status})`);

    const data = await res.json();
    console.log('Raw response:', data);

    const reply =
      data.choices?.[0]?.message?.content ||
      data.choices?.[0]?.content ||
      data.reply ||
      data.text ||
      '(no reply)';

    // 每次用户输入时创建新消息
    messages.push({ sender: 'ai', text: '', image: null });  // Push a new empty AI message

    // 使用模拟打字效果逐字显示 AI 回复
    simulateTypingEffect(reply, (partialReply) => {
      // 更新AI的最新消息
      const aiMessage = messages[messages.length - 1];
      aiMessage.text = partialReply;
    });
  } catch (err) {
    console.error(err);
    messages.push({ sender: 'ai', text: `❌ Send failed: ${err.message}` });
  } finally {
    loading.value = false;
    text.value = '';
    removeImage();
  }
}
</script>


<style scoped>
.chat-container {
  --bg-dark:     #0d1117;
  --bg-light:    #161b22;
  --fg-primary:  #c9d1d9;
  --fg-secondary:#8b949e;
  --accent:      #238636;
  --accent-hover:#2ea043;

  font-family: 'Segoe UI', Roboto, sans-serif;
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg-dark);
  color: var(--fg-primary);
  position: relative;
}

/* Header */
.chat-header {
  padding: 12px 20px;
  background: var(--bg-light);
  display: flex;
  align-items: center;
  border-bottom: 1px solid #30363d;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.title {
  font-size: 1.3rem;
  margin: 0;
  font-weight: 600;
}
.model-select,
.input-add {
  padding: 6px 12px;
  background: var(--bg-dark);
  border: 1px solid #30363d;
  border-radius: 8px;
  color: var(--fg-primary);
  font-size: 0.9rem;
}
.btn-add {
  padding: 6px 10px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

/* Body */
.chat-body {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}
.chat-body::-webkit-scrollbar {
  width: 6px;
}
.chat-body::-webkit-scrollbar-thumb {
  background: #30363d;
  border-radius: 3px;
}
.chat-message {
  display: flex;
  margin-bottom: 14px;
}
.chat-message.ai {
  justify-content: flex-start;
}
.chat-message.user {
  justify-content: flex-end;
}
.avatar {
  width: 32px;
  height: 32px;
  margin-right: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-light);
  border-radius: 50%;
}
.bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 20px;
  background: var(--bg-light);
  word-wrap: break-word;
}
.chat-message.user .bubble {
  background: var(--accent);
  color: #fff;
}
.text {
  margin: 0;
  white-space: pre-wrap;
}

/* Image preview */
.image-preview {
  display: block;
  max-width: 100%;
  max-height: 200px;
  margin: 8px auto 0;
  border-radius: 10px;
  border: 1px solid #30363d;
}
.typing-indicator {
  font-style: italic;
  color: var(--fg-secondary);
}

/* Footer */
.chat-footer {
  padding: 12px 20px;
  background: var(--bg-light);
  display: flex;
  align-items: center;
  gap: 10px;
  border-top: 1px solid #30363d;
}
.input-text {
  flex: 1;
  padding: 8px 14px;
  background: var(--bg-dark);
  border: 1px solid #30363d;
  border-radius: 8px;
  color: var(--fg-primary);
}
.input-text:focus {
  outline: none;
  border-color: var(--accent);
}

/* Attach button */
.btn-attach {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  background: linear-gradient(135deg, #238636, #2ea043);
  color: #fff;
  border: none;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(35, 134, 54, 0.5);
  cursor: pointer;
  transition: transform 0.1s, box-shadow 0.2s;
}
.attach-icon {
  font-size: 1.1rem;
}
.attach-text {
  font-size: 0.9rem;
}
.btn-attach:hover {
  transform: translateY(-1px);
}

/* Send button */
.btn-send {
  padding: 8px 18px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}
.btn-send:disabled {
  background: #444;
  cursor: not-allowed;
}

/* Preview box */
.preview-container {
  position: absolute;
  bottom: 90px;
  left: 30px;
}
.preview-box {
  position: relative;
}
.preview-thumb {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 10px;
  border: 2px solid #30363d;
}
.btn-remove {
  position: absolute;
  top: -6px;
  right: -6px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 22px;
  height: 22px;
  cursor: pointer;
}

/* Utility */
.hidden {
  display: none;
}
</style>
