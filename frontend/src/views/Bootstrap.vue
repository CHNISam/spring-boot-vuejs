<template>
  <div class="chat-container">
    <header class="chat-header">
      <div class="header-left">
        <h2 class="title">AI Chat</h2>
        <select v-model="selectedConfig" class="model-select">
          <option v-for="c in configs" :key="c.name" :value="c">{{ c.name }}</option>
        </select>
        <select v-model="model" class="model-select">
          <option v-for="m in models" :key="m.value" :value="m.value">{{ m.label }}</option>
        </select>
        <input v-model="newModel" placeholder="新增模型" class="input-add" @keyup.enter="addModel" />
        <button class="btn-add" @click="addModel">添加</button>
      </div>
    </header>

    <div class="chat-body" ref="bodyRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-message', msg.sender]">
        <div v-if="msg.sender==='ai'" class="avatar">🤖</div>
        <div class="bubble">
          <p class="text">{{ msg.text }}</p>
          <img v-if="msg.image" :src="msg.image" class="image-preview" alt="sent image" />
        </div>
      </div>
      <div v-if="loading" class="typing-indicator">AI 正在输入…</div>
    </div>

    <footer class="chat-footer">
      <button class="btn-attach" @click="triggerFileInput" :disabled="loading">
        <span class="attach-icon">📎</span>
        <span class="attach-text">添加图片</span>
      </button>
      <input type="file" accept="image/*" ref="fileInputRef" class="hidden" @change="onFileChange" />
      <input v-model="text" @keyup.enter="send" :disabled="loading" placeholder="Type a message..." class="input-text" />
      <button class="btn-send" @click="send" :disabled="loading || (!text && !imageFile)">
        {{ loading ? '发送中…' : '发送' }}
      </button>
    </footer>

    <div v-if="preview" class="preview-container">
      <div class="preview-box">
        <img :src="preview" class="preview-thumb" alt="preview image" />
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
const models = ref(JSON.parse(localStorage.getItem('chat_models') || 'null') || defaultModels);
const model = ref(models.value[0].value);
const newModel = ref('');
watch(models, (val) => localStorage.setItem('chat_models', JSON.stringify(val)), { deep: true });

const messages = reactive([]);
const loading = ref(false);
const text = ref('');
const imageFile = ref(null);
const preview = ref(null);
const fileInputRef = ref(null);
const bodyRef = ref(null);

watch(() => messages.length, async () => {
  await nextTick();
  if (bodyRef.value) bodyRef.value.scrollTop = bodyRef.value.scrollHeight;
});

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

// 关键：构造不同平台的payload
function buildPayload(cfg, message) {
  switch (cfg.formatter) {
    case 'bigmodel':
      return {
        model: model.value,
        messages: [{ role: 'user', content: message }],
        temperature: 0.95,
        top_p: 0.7
      };
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

// ** 关键：发请求 **
async function send() {
  if (loading.value || (!text.value.trim() && !imageFile.value)) return;

  messages.push({
    sender: 'user',
    text: text.value,
    image: preview.value
  });
  loading.value = true;

  try {
    const cfg = selectedConfig.value;
    const payload = buildPayload(cfg, text.value);

    // 智谱、OpenAI、Claude各自用自己的头
    const headers = { 'Content-Type': 'application/json' };
    if (cfg.formatter === 'bigmodel') {
      headers['Authorization'] = `Bearer ${cfg.apiKey}`;
    } else if (cfg.formatter === 'openai') {
      headers['Authorization'] = `Bearer ${cfg.apiKey}`;
    } else if (cfg.formatter === 'claude') {
      headers['x-api-key'] = cfg.apiKey;
    }

    const res = await fetch(cfg.url.trim(), {
      method: 'POST',
      headers,
      body: JSON.stringify(payload)
    });

    if (res.status === 401) throw new Error('401 Unauthorized，请检查你的API Key或接口权限');
    if (!res.ok) throw new Error(`接口异常(${res.status})`);

    const data = await res.json();
    console.log('原始返回（调试用）：', data);

    // 智谱/GLM风格
    const reply =
      data.choices?.[0]?.message?.content ||
      data.choices?.[0]?.content ||
      data.reply ||
      data.text ||
      '（无回复）';

    messages.push({ sender: 'ai', text: reply, image: null });
  } catch (err) {
    console.error(err);
    messages.push({ sender: 'ai', text: `❌ 发送失败：${err.message}` });
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
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #30363d;
}
.header-left,
.header-right {
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
.input-config {
  padding: 6px 12px;
  background: var(--bg-dark);
  border: 1px solid #30363d;
  border-radius: 8px;
  color: var(--fg-primary);
  font-size: 0.9rem;
}
.input-add {
  padding: 6px 10px;
  background: var(--bg-dark);
  border: 1px dashed #30363d;
  border-radius: 8px;
  color: var(--fg-primary);
  width: 100px;
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

/* 图片预览 */
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

/* Attach 按钮 */
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

/* Send 按钮 */
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

/* Image Preview Box */
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

/* 隐藏元素 */
.hidden {
  display: none;
}
</style>
