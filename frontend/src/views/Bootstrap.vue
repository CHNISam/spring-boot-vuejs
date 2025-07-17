<template>
  <div class="chat-container">
    <!-- Header -->
    <header class="chat-header">
      <div class="header-left">
        <h2 class="title">AI Chat</h2>
        <select v-model="model" class="model-select">
          <option v-for="m in models" :key="m.value" :value="m.value">{{ m.label }}</option>
        </select>
        <input v-model="newModel" placeholder="新增模型" class="input-add" @keyup.enter="addModel" />
        <button class="btn-add" @click="addModel">添加</button>
      </div>
      <div class="header-right">
        <input v-model="apiUrl" placeholder="API URL" class="input-config" />
        <input v-model="apiKey" placeholder="API Key" class="input-config" />
      </div>
    </header>

    <!-- Body -->
    <div class="chat-body" ref="bodyRef">
      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        :class="['chat-message', msg.sender]"
      >
        <div v-if="msg.sender==='ai'" class="avatar">🤖</div>
        <div class="bubble">
          <p class="text">{{ msg.text }}</p>
          <img
            v-if="msg.image"
            :src="msg.image"
            class="image-preview"
            alt="sent image"
          />
        </div>
      </div>
      <div v-if="loading" class="typing-indicator">AI 正在输入…</div>
    </div>

    <!-- Footer -->
    <footer class="chat-footer">
      <button
        class="btn-attach"
        @click="triggerFileInput"
        :disabled="loading"
      >
        <span class="attach-icon">📎</span>
        <span class="attach-text">添加图片</span>
      </button>
      <input
        type="file"
        accept="image/*"
        ref="fileInput"
        class="hidden"
        @change="onFileChange"
      />

      <input
        type="text"
        v-model="text"
        @keyup.enter="send"
        :disabled="loading"
        placeholder="Type a message..."
        class="input-text"
      />

      <button
        class="btn-send"
        @click="send"
        :disabled="loading || (!text.trim() && !imageFile)"
      >
        {{ loading ? '发送中…' : '发送' }}
      </button>
    </footer>

    <!-- Image Preview -->
    <div v-if="preview" class="preview-container">
      <div class="preview-box">
        <img :src="preview" class="preview-thumb" alt="preview image" />
        <button class="btn-remove" @click="removeImage">✕</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { nextTick, onMounted, reactive, ref, watch } from 'vue'

// —— 可填写并自动存储的配置 —— 
const apiUrl = ref(localStorage.getItem('chat_apiUrl') || 'https://open.bigmodel.cn/api/paas/v4/chat/completions')
const apiKey = ref(localStorage.getItem('chat_apiKey') || '76915445ad0955e6442a0aa6d24ad251.27G8TUC8AM9euXxQ')
// 保存时机
watch(apiUrl, v => localStorage.setItem('chat_apiUrl', v))
watch(apiKey, v => localStorage.setItem('chat_apiKey', v))
// —— 结束 ——

// 默认模型列表 + 本地持久化
const defaultModels = [
  { value: 'glm-4-flash', label: 'GLM-4-Flash' },
  { value: 'gpt-4', label: 'GPT-4' },
  { value: 'chatglm', label: 'ChatGLM-6B' }
]
const models = ref([])
const model = ref('')
const newModel = ref('')

onMounted(() => {
  const stored = JSON.parse(localStorage.getItem('chat_models') || 'null')
  models.value = stored || defaultModels
  model.value = models.value[0].value
})

// 新增模型并存储
function addModel() {
  if (!newModel.value.trim()) return
  models.value.push({ value: newModel.value, label: newModel.value })
  localStorage.setItem('chat_models', JSON.stringify(models.value))
  model.value = newModel.value
  newModel.value = ''
}

// 消息和交互
const messages = reactive([])
const loading = ref(false)
const text = ref('')
const imageFile = ref(null)
const preview = ref(null)
const fileInput = ref(null)
const bodyRef = ref(null)

watch(() => messages.length, async () => {
  await nextTick()
  const el = bodyRef.value
  if (el) el.scrollTop = el.scrollHeight
})

function triggerFileInput() {
  fileInput.value.click()
}

function onFileChange(e) {
  const f = e.target.files[0]
  if (f && f.type.startsWith('image/')) {
    imageFile.value = f
    preview.value = URL.createObjectURL(f)
  }
}

function removeImage() {
  if (preview.value) URL.revokeObjectURL(preview.value)
  imageFile.value = null
  preview.value = null
}

async function send() {
  if (loading.value || (!text.value.trim() && !imageFile.value)) return

  messages.push({ sender: 'user', text: text.value.trim(), image: imageFile.value ? URL.createObjectURL(imageFile.value) : null })

  loading.value = true
  try {
    const fd = new FormData()
    fd.append('model', model.value)
    fd.append('message', text.value.trim())
    fd.append('apiKey', apiKey.value)
    if (imageFile.value) fd.append('image', imageFile.value)

    const res = await fetch(apiUrl.value, { method: 'POST', body: fd })
    const { reply } = await res.json()

    messages.push({ sender: 'ai', text: reply || '（AI 未返回内容）', image: null })
  } catch {
    messages.push({ sender: 'ai', text: '❌ 发送失败，请检查网络或配置', image: null })
  } finally {
    loading.value = false
    text.value = ''
    removeImage()
  }
}
</script>

<style scoped>
.chat-container {
  --bg-dark: #0d1117;
  --bg-light: #161b22;
  --fg-primary: #c9d1d9;
  --fg-secondary: #8b949e;
  --accent: #238636;
  --accent-hover: #2ea043;
  font-family: 'Segoe UI', Roboto, sans-serif;
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg-dark);
  color: var(--fg-primary);
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
.header-left { display: flex; align-items: center; gap: 12px; }
.header-right { display: flex; align-items: center; gap: 12px; }

.title { font-size: 1.3rem; font-weight: 600; margin: 0; }

.model-select, .input-config {
  padding: 6px 12px;
  background: var(--bg-dark);
  border: 1px solid #30363d;
  border-radius: 8px;
  color: var(--fg-primary);
  font-size: 0.9rem;
}
.input-add {
  width: 100px;
  padding: 6px 10px;
  background: var(--bg-dark);
  border: 1px dashed #30363d;
  border-radius: 8px;
  color: var(--fg-primary);
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
.chat-message {
  display: flex;
  margin-bottom: 14px;
}
.chat-message.ai { justify-content: flex-start; }
.chat-message.user { justify-content: flex-end; }
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
  position: relative;
  background: var(--bg-light);
}
.chat-message.user .bubble {
  background: var(--accent);
  color: #fff;
}
.text { margin: 0; white-space: pre-wrap; }

/* 精修图片显示 */
.image-preview {
  display: block;
  max-width: 100%;
  max-height: 200px;
  margin: 8px auto 0;
  border-radius: 10px;
  border: 1px solid #30363d;
}
.typing-indicator { font-style: italic; color: var(--fg-secondary); }

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
.input-text:focus { outline: none; border-color: var(--accent); }

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
.attach-icon { font-size: 1.1rem; }
.attach-text { font-size: 0.9rem; }
.btn-attach:hover { transform: translateY(-1px); }

/* 发送按钮 */
.btn-send {
  padding: 8px 18px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
}
.btn-send:disabled { background: #444; cursor: not-allowed; }

/* Preview */
.preview-container {
  position: absolute;
  bottom: 90px;
  left: 30px;
}
.preview-thumb {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 10px;
}
.btn-remove {
  position: absolute;
  top: -6px; right: -6px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 22px; height: 22px;
  cursor: pointer;
}
.hidden { display: none; }
</style>
