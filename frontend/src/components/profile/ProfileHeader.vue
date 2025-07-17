<script setup lang="ts">
import { computed, ref } from 'vue'

interface UserView {
  id?: number|null
  avatar?: string
  nickname?: string
  username?: string
  firstName?: string
  lastName?: string
}

const props = defineProps<{ user: UserView; isSelf: boolean }>()
const emit = defineEmits<{ (e: 'edit'): void; (e: 'follow'): void }>()
const avatarBroken = ref(false)
const fallbackAvatar = 'https://avatars.githubusercontent.com/u/9919?s=280&v=4'

const displayName = computed(() => {
  const { nickname, username, firstName = '', lastName = '' } = props.user
  return (
    nickname?.trim() ||
    username?.trim() ||
    `${firstName} ${lastName}`.trim() ||
    '未命名用户'
  )
})
</script>
