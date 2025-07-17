<template>
  <button
    role="tab"
    type="button"
    :aria-selected="active"
    :class="[
      'py-3 px-6 text-base transition border-b-2.5 outline-none',
      active
        ? 'text-[#5ea6ff] border-[#5ea6ff] font-semibold'
        : 'text-gray-400 hover:text-white border-transparent',
    ]"
  >
    <slot />
  </button>
</template>

<script setup lang="ts">
defineProps<{
  name?: string
  active: boolean
}>()
</script>
