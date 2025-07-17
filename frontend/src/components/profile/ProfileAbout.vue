<script setup lang="ts">
const props = defineProps<{ bio?: string; isSelf: boolean }>()
const emit = defineEmits<{ (e: 'edit'): void }>()
</script>
