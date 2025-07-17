<script setup lang="ts">
const props = defineProps<{ tabs: string[]; currentTab: string }>()
const emit = defineEmits<{ (e: 'change', tab: string): void }>()
</script>
