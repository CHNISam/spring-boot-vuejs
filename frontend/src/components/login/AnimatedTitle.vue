<!-- src/components/login/AnimatedTitle.vue -->
<template>
  <transition name="fade-scale">
    <h3 v-if="visible" class="animated-title dark-title" v-bind="$attrs">
      {{ displayedText }}<span class="typing-cursor"></span>
    </h3>
  </transition>
</template>

<script lang="ts">
import { defineComponent, ref, watch } from 'vue'

export default defineComponent({
  name: 'AnimatedTitle',
  props: {
    text: { type: String, required: true },
    speedFirst: { type: Number, default: 120 },
    speedMid: { type: Number, default: 60 },
    speedLast: { type: Number, default: 40 },
    fadeDuration: { type: Number, default: 150 },
    pauseDuration: { type: Number, default: 150 }
  },
  setup(props) {
    const displayedText = ref('')
    const visible = ref(false)
    let timerId: number

    function startTyping(text: string) {
      displayedText.value = ''
      visible.value = true
      let i = 0
      function typeChar() {
        if (i < text.length) {
          displayedText.value += text[i++]
          const delay =
            i === 1 ? props.speedFirst
            : i === text.length ? props.speedLast
            : props.speedMid
          timerId = window.setTimeout(typeChar, delay)
        }
      }
      typeChar()
    }

    function fadeOutThenType(text: string) {
      visible.value = false
      clearTimeout(timerId)
      setTimeout(() => startTyping(text), props.fadeDuration + props.pauseDuration)
    }

    watch(() => props.text, newText => fadeOutThenType(newText), { immediate: true })
    return { displayedText, visible }
  }
})
</script>

<style scoped>
.animated-title {
  display: inline-block;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  color: var(--text-primary, #333);
}
.fade-scale-enter-active,
.fade-scale-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-scale-enter-from,
.fade-scale-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
.typing-cursor {
  display: inline-block;
  width: 1px;
  height: 1em;
  background-color: currentColor;
  margin-left: 4px;
  animation: blink 1s steps(1) infinite;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50%      { opacity: 0; }
}

/* 暗色主题覆写 */
.dark-title {
  color: #fff !important;
}
</style>
