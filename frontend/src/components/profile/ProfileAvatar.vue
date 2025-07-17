<template>
  <figure
    :class="[
      'rounded-full ring-4 ring-[#181818] overflow-hidden select-none bg-gray-200 dark:bg-gray-800 flex items-center justify-center shadow-xl',
      $attrs.class,
    ]"
    :style="{ width: sizePx, height: sizePx }"
  >
    <img
      v-if="!broken"
      :src="src || fallback"
      :alt="alt"
      class="object-cover w-full h-full"
      loading="lazy"
      @error="broken = true"
    />
    <svg
      v-else
      class="w-1/2 h-1/2 text-gray-400"
      fill="none"
      viewBox="0 0 48 48"
      stroke="currentColor"
    >
      <circle cx="24" cy="24" r="20" stroke-width="2" fill="white" />
      <path
        d="M14 34c2-6 10-6 12 0m-8-8a2 2 0 104 0 2 2 0 00-4 0zm8 0a2 2 0 104 0 2 2 0 00-4 0z"
        stroke-width="2"
      />
    </svg>
  </figure>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

const props = defineProps<{
  src?: string
  alt?: string
  size?: number // px
}>()

const broken = ref(false)
const fallback =
  'https://avatars.githubusercontent.com/u/9919?s=280&v=4' /* Github logo兜底 */

const sizePx = computed(() => (props.size ?? 128) + 'px')
</script>
